package com.example.wizards.entity;

import com.example.wizards.entity.ai.AnimatedAttackGoal;
import com.example.wizards.entity.ai.AssignedTargetGoal;
import com.example.wizards.entity.ai.ControllerHurtByTargetGoal;
import com.example.wizards.entity.ai.FollowControllerGoal;
import com.example.wizards.magic.PowerToughnessEnchantment;
import com.example.wizards.magic.SpellEffect;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class SummonedCreature extends PathfinderMob implements ControlledEntity {


    protected static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(SummonedCreature.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> ATTACK_ANIMATION_DURATION = SynchedEntityData.defineId(SummonedCreature.class, EntityDataSerializers.INT);

    protected static Logger logger = LogUtils.getLogger();

    // Stats
    protected int power = 0;
    protected int currentPower = 0;
    protected int toughness = 0;
    protected int currentToughness = 0;

    // Effects
    protected List<SpellEffect> effects = new ArrayList<>();

    // Ai
    protected FollowControllerGoal followControllerGoal;
    protected AssignedTargetGoal assignedTargetGoal;
    protected ControllerHurtByTargetGoal controllerHurtByTargetGoal;
    protected AnimatedAttackGoal<?> animatedAttackGoal;
    protected String controllerUuid;

    // Animation
    public final AnimationState idleAnimationState = new AnimationState();
    protected int idleAnimationTimeout;
    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout;

    public SummonedCreature(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        logger.info("small construct {}", this.getClass().getSimpleName());
    }

    public SummonedCreature(EntityType<? extends PathfinderMob> entityType, Level level, int power, int toughness) {
        this(entityType, level);
        this.power = power;
        this.currentPower = power;
        this.toughness = toughness;
        this.currentToughness = toughness;
        this.updateAttributes();

        AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);
        logger.info("big construct {}", this.getClass().getSimpleName());
        logger.info("\nHealth {}", attribute.getBaseValue());
        attribute = this.getAttribute(Attributes.ATTACK_DAMAGE);
        logger.info("\nATTACK_DAMAGE {}", attribute.getBaseValue());
    }

    @Override
    protected void registerGoals() {
        // Attack goals registered in sub-classes in registerLookAndAttackGoals
        this.followControllerGoal = new FollowControllerGoal(this, 1.0);
        this.goalSelector.addGoal(5, followControllerGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.assignedTargetGoal = new AssignedTargetGoal(this);
        this.targetSelector.addGoal(1, this.assignedTargetGoal);
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.controllerHurtByTargetGoal = new ControllerHurtByTargetGoal(this);
        this.targetSelector.addGoal(5, controllerHurtByTargetGoal);
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

        registerLookAndAttackGoals();
    }

    protected void registerLookAndAttackGoals() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    protected void updateAttributes() {
        if (this.currentPower > -1) {
            logger.info("updating power {}", currentPower);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.currentPower);
        }
        if (this.currentToughness > 0) {
            logger.info("updating toughness {}", currentToughness);
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.currentToughness * 10.0);
        }

        logger.info("update");
        AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);
        logger.info("\nHealth {}", attribute.getBaseValue());
        attribute = this.getAttribute(Attributes.ATTACK_DAMAGE);
        logger.info("\nATTACK_DAMAGE {}", attribute.getBaseValue());

        this.setHealth(this.getMaxHealth());
        logger.info("Current health: {}", this.getHealth());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(ATTACK_ANIMATION_DURATION, 0);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public int getAttackAnimationDuration() {
//        logger.info("getAttackAnimationDuration {}", this.entityData.get(ATTACK_ANIMATION_DURATION));
        return this.entityData.get(ATTACK_ANIMATION_DURATION);
    }

    @Override
    public void tick() {
//        if (!this.level().isClientSide) {
//            logger.info("=========================\nEntity tick: T/C {}\n{}", tickCount, animatedAttackGoal);
//        }
        super.tick();

        SummonedCreatureUtil.trySetController(this, this.level());

        if (this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    protected void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

//        logger.info("goal {}", this.getAttackAnimationDuration());
        if (this.getAttackAnimationDuration() != 0) {
            if (this.isAttacking() && attackAnimationTimeout <= 0) {
//            logger.info("Entity: timeout done, starting animation");
                attackAnimationState.start(this.tickCount);
                attackAnimationTimeout = this.getAttackAnimationDuration(); // Length in ticks of your animation
//            logger.info("Entity: attackAnimationState {}", attackAnimationState.getAccumulatedTime());
//            logger.info("Entity: attackAnimationTimeout reset {}", attackAnimationTimeout);
            } else {
                --this.attackAnimationTimeout;
//            logger.info("Entity: countdown animation {}", this.attackAnimationTimeout);
            }

            if (!this.isAttacking()) {
//            if (attackAnimationState.isStarted()) logger.info("Stopping anim");
                attackAnimationState.stop();
            }
        } else {
//            logger.info("here !!!!");
        }
    }

    @Override
    protected void updateWalkAnimation(float partialTick) {
        float f;
        if (this.getPose() == Pose.STANDING) {
            f = Math.min(partialTick * 6f, 1f);
        } else {
            f = 0;
        }

        this.walkAnimation.update(f, 0.2f);
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
        this.currentPower = this.power;
        this.toughness = tag.getInt("Toughness");
        this.currentToughness = this.toughness;
        this.updateAttributes();
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
        if (this.followControllerGoal != null ) {
            this.followControllerGoal.setController(controller);
        }
        if (this.controllerHurtByTargetGoal != null) {
            this.controllerHurtByTargetGoal.setController(controller);
        }
    }

    @Override
    public LivingEntity getController() {
        return this.followControllerGoal != null ? this.followControllerGoal.getController() : null;
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
        if (this.assignedTargetGoal != null) {
            this.assignedTargetGoal.assignTarget(livingEntity);
        }
    }

    public int getCurrentPower() {
        return currentPower;
    }

    public int getCurrentToughness() {
        return currentToughness;
    }

    public void addEffect(SpellEffect effect) {
        this.effects.add(effect);
        effect.apply(this.level(), this);

        if (effect instanceof PowerToughnessEnchantment) {
            this.updateAttributes();
        }
    }

    public void addPowerToughness(int power, int toughness) {
        this.currentPower = this.currentPower + power;
        this.currentToughness = this.currentToughness + toughness;
    }

    public void removePowerToughness(int power, int toughness) {
        this.currentPower = this.currentPower - power;
        this.currentToughness = this.currentToughness - toughness;
    }

}
