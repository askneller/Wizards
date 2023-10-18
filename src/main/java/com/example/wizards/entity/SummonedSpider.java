package com.example.wizards.entity;

import com.example.wizards.entity.ai.AssignedTargetGoal;
import com.example.wizards.entity.ai.ControllerHurtByTargetGoal;
import com.example.wizards.entity.ai.FollowControllerGoal;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class SummonedSpider extends Spider implements ControlledEntity {

    private static final Logger logger = LogUtils.getLogger();

    public static final String spell_name = "summon_spider";
    public static final String entity_name = "summoned_spider";

    // Stats
    private int power = 0;
    private int toughness = 0;

    private FollowControllerGoal followControllerGoal;
    private AssignedTargetGoal assignedTargetGoal;
    private ControllerHurtByTargetGoal controllerHurtByTargetGoal;
    private String controllerUuid;

    public SummonedSpider(EntityType<? extends Spider> p_33786_, Level p_33787_) {
        super(p_33786_, p_33787_);
        logger.info("Summoned Spider");
    }

    public SummonedSpider(EntityType<? extends Spider> p_33786_, Level p_33787_, int power, int toughness) {
        this(p_33786_, p_33787_);
        logger.info("Summoned Spider");
        this.power = power;
        this.toughness = toughness;
        this.updateAttributes();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new SummonedSpider.SpiderAttackGoal(this));
        this.followControllerGoal = new FollowControllerGoal(this, 1.0);
        this.goalSelector.addGoal(5, followControllerGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.assignedTargetGoal = new AssignedTargetGoal(this);
        this.targetSelector.addGoal(1, this.assignedTargetGoal);
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.controllerHurtByTargetGoal = new ControllerHurtByTargetGoal(this);
        this.targetSelector.addGoal(3, controllerHurtByTargetGoal);
        // Attack other players
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                true,
                (le -> this.getController() != null && !le.equals(this.getController()))));
        // Attack mobs controlled by other players
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                true,
                (le -> {
                    if (le instanceof ControlledEntity ce) {
                        return this.getController() != null && ce.getController() != null &&
                                !this.getController().equals(ce.getController());
                    }
                    return false;
                })));
    }

    protected void updateAttributes() {
        if (this.power > 0) {
            logger.info("updating power {}", power);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.power);
        }
        if (this.toughness > 0) {
            logger.info("updating toughness {}", toughness);
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.toughness * 10.0);
        }

        logger.info("update");
        AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);
        logger.info("\nHealth {}", attribute.getBaseValue());
        attribute = this.getAttribute(Attributes.ATTACK_DAMAGE);
        logger.info("\nATTACK_DAMAGE {}", attribute.getBaseValue());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (getControllerUuid() != null) {
            tag.putString("ControllerUuid", getControllerUuid());
        }
        tag.putInt("Power", power);
        tag.putInt("Toughness", toughness);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.controllerUuid = tag.getString("ControllerUuid");
        this.power = tag.getInt("Power");
        this.toughness = tag.getInt("Toughness");
        this.updateAttributes();
    }

    public void tick() {
        super.tick();
        SummonedCreatureUtil.trySetController(this, this.level());
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (entity instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)entity).getMobType());
            f1 += (float)EnchantmentHelper.getKnockbackBonus(this);
        }

        int i = EnchantmentHelper.getFireAspect(this);
        if (i > 0) {
            entity.setSecondsOnFire(i * 4);
        }

        logger.info("Hurting for {}, {}", f, entity);
        boolean flag = entity.hurt(this.damageSources().mobAttack(this), f);
        if (flag) {
            if (f1 > 0.0F && entity instanceof LivingEntity) {
                ((LivingEntity)entity).knockback((double)(f1 * 0.5F), (double) Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }

            if (entity instanceof Player) {
                Player player = (Player)entity;
                this.maybeDisableShield(player, this.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }

            this.doEnchantDamageEffects(this, entity);
            this.setLastHurtMob(entity);
        }

        return flag;
    }

    private void maybeDisableShield(Player p_21425_, ItemStack p_21426_, ItemStack p_21427_) {
        if (!p_21426_.isEmpty() && !p_21427_.isEmpty() && p_21426_.getItem() instanceof AxeItem && p_21427_.is(Items.SHIELD)) {
            float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
            if (this.random.nextFloat() < f) {
                p_21425_.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level().broadcastEntityEvent(p_21425_, (byte)30);
            }
        }

    }

    @Override
    public void setController(LivingEntity controller) {
        this.followControllerGoal.setController(controller);
        this.controllerHurtByTargetGoal.setController(controller);
    }

    @Override
    public LivingEntity getController() {
        return this.followControllerGoal.getController();
    }

    @Override
    public String getControllerUuid() {
        LivingEntity controller = getController();
        return controller != null ? controller.getStringUUID() : this.controllerUuid;
    }

    @Override
    public void setControllerUuid(String uuid) {
        this.controllerUuid = uuid;
    }

    @Override
    public void assignTarget(LivingEntity livingEntity) {
        this.assignedTargetGoal.assignTarget(livingEntity);
    }

    static class SpiderAttackGoal extends MeleeAttackGoal {
        public SpiderAttackGoal(Spider p_33822_) {
            super(p_33822_, 1.0D, true);
        }

        public boolean canUse() {
            return super.canUse() && !this.mob.isVehicle();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        protected double getAttackReachSqr(LivingEntity p_33825_) {
            return (double)(4.0F + p_33825_.getBbWidth());
        }
    }
}
