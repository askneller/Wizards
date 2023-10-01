package com.example.wizards;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class SummonedPhantom extends Phantom {

    static enum AttackPhase {
        CIRCLE,
        SWOOP;
    }

    Vec3 moveTargetPoint = Vec3.ZERO;
    BlockPos anchorPoint = BlockPos.ZERO;
    SummonedPhantom.AttackPhase attackPhase = SummonedPhantom.AttackPhase.CIRCLE;


    public SummonedPhantom(EntityType<? extends Phantom> p_33101_, Level p_33102_) {
        super(p_33101_, p_33102_);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SummonedPhantom.PhantomAttackStrategyGoal());
        this.goalSelector.addGoal(2, new SummonedPhantom.PhantomSweepAttackGoal());
        this.goalSelector.addGoal(3, new SummonedPhantom.PhantomCircleAroundAnchorGoal());
//        this.targetSelector.addGoal(1, new SummonedPhantom.PhantomAttackPlayerTargetGoal());
    }

    protected boolean isSunBurnTick() {
        return false;
    }

    class PhantomAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0D);
        private int nextScanTick = reducedTickDelay(20);

        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
                return false;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<Player> list = SummonedPhantom.this.level().getNearbyPlayers(this.attackTargeting, SummonedPhantom.this, SummonedPhantom.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty()) {
                    list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for(Player player : list) {
                        if (SummonedPhantom.this.canAttack(player, TargetingConditions.DEFAULT)) {
                            SummonedPhantom.this.setTarget(player);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = SummonedPhantom.this.getTarget();
            return livingentity != null ? SummonedPhantom.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
        }
    }

    class PhantomAttackStrategyGoal extends Goal {
        private int nextSweepTick;

        public boolean canUse() {
            LivingEntity livingentity = SummonedPhantom.this.getTarget();
            return livingentity != null ? SummonedPhantom.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
        }

        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            SummonedPhantom.this.attackPhase = SummonedPhantom.AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        public void stop() {
            SummonedPhantom.this.anchorPoint = SummonedPhantom.this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, SummonedPhantom.this.anchorPoint).above(10 + SummonedPhantom.this.random.nextInt(20));
        }

        public void tick() {
            if (SummonedPhantom.this.attackPhase == SummonedPhantom.AttackPhase.CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    SummonedPhantom.this.attackPhase = SummonedPhantom.AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + SummonedPhantom.this.random.nextInt(4)) * 20);
                    SummonedPhantom.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + SummonedPhantom.this.random.nextFloat() * 0.1F);
                }
            }

        }

        private void setAnchorAboveTarget() {
            SummonedPhantom.this.anchorPoint = SummonedPhantom.this.getTarget().blockPosition().above(20 + SummonedPhantom.this.random.nextInt(20));
            if (SummonedPhantom.this.anchorPoint.getY() < SummonedPhantom.this.level().getSeaLevel()) {
                SummonedPhantom.this.anchorPoint = new BlockPos(SummonedPhantom.this.anchorPoint.getX(), SummonedPhantom.this.level().getSeaLevel() + 1, SummonedPhantom.this.anchorPoint.getZ());
            }

        }
    }

    class PhantomBodyRotationControl extends BodyRotationControl {
        public PhantomBodyRotationControl(Mob p_33216_) {
            super(p_33216_);
        }

        public void clientTick() {
            SummonedPhantom.this.yHeadRot = SummonedPhantom.this.yBodyRot;
            SummonedPhantom.this.yBodyRot = SummonedPhantom.this.getYRot();
        }
    }

    class PhantomCircleAroundAnchorGoal extends SummonedPhantom.PhantomMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        public boolean canUse() {
            return SummonedPhantom.this.getTarget() == null || SummonedPhantom.this.attackPhase == SummonedPhantom.AttackPhase.CIRCLE;
        }

        public void start() {
            this.distance = 5.0F + SummonedPhantom.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + SummonedPhantom.this.random.nextFloat() * 9.0F;
            this.clockwise = SummonedPhantom.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick() {
            if (SummonedPhantom.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + SummonedPhantom.this.random.nextFloat() * 9.0F;
            }

            if (SummonedPhantom.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (SummonedPhantom.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = SummonedPhantom.this.random.nextFloat() * 2.0F * (float)Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (SummonedPhantom.this.moveTargetPoint.y < SummonedPhantom.this.getY() && !SummonedPhantom.this.level().isEmptyBlock(SummonedPhantom.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (SummonedPhantom.this.moveTargetPoint.y > SummonedPhantom.this.getY() && !SummonedPhantom.this.level().isEmptyBlock(SummonedPhantom.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(SummonedPhantom.this.anchorPoint)) {
                SummonedPhantom.this.anchorPoint = SummonedPhantom.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float)Math.PI / 180F);
            SummonedPhantom.this.moveTargetPoint = Vec3.atLowerCornerOf(SummonedPhantom.this.anchorPoint).add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
        }
    }

    abstract class PhantomMoveTargetGoal extends Goal {
        public PhantomMoveTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return SummonedPhantom.this.moveTargetPoint.distanceToSqr(SummonedPhantom.this.getX(), SummonedPhantom.this.getY(), SummonedPhantom.this.getZ()) < 4.0D;
        }
    }

    class PhantomSweepAttackGoal extends SummonedPhantom.PhantomMoveTargetGoal {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        public boolean canUse() {
            return SummonedPhantom.this.getTarget() != null && SummonedPhantom.this.attackPhase == SummonedPhantom.AttackPhase.SWOOP;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = SummonedPhantom.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player) {
                    Player player = (Player)livingentity;
                    if (livingentity.isSpectator() || player.isCreative()) {
                        return false;
                    }
                }

                if (!this.canUse()) {
                    return false;
                } else {
                    if (SummonedPhantom.this.tickCount > this.catSearchTick) {
                        this.catSearchTick = SummonedPhantom.this.tickCount + 20;
                        List<Cat> list = SummonedPhantom.this.level().getEntitiesOfClass(Cat.class, SummonedPhantom.this.getBoundingBox().inflate(16.0D), EntitySelector.ENTITY_STILL_ALIVE);

                        for(Cat cat : list) {
                            cat.hiss();
                        }

                        this.isScaredOfCat = !list.isEmpty();
                    }

                    return !this.isScaredOfCat;
                }
            }
        }

        public void start() {
        }

        public void stop() {
            SummonedPhantom.this.setTarget((LivingEntity)null);
            SummonedPhantom.this.attackPhase = SummonedPhantom.AttackPhase.CIRCLE;
        }

        public void tick() {
            LivingEntity livingentity = SummonedPhantom.this.getTarget();
            if (livingentity != null) {
                SummonedPhantom.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
                if (SummonedPhantom.this.getBoundingBox().inflate((double)0.2F).intersects(livingentity.getBoundingBox())) {
                    SummonedPhantom.this.doHurtTarget(livingentity);
                    SummonedPhantom.this.attackPhase = SummonedPhantom.AttackPhase.CIRCLE;
                    if (!SummonedPhantom.this.isSilent()) {
                        SummonedPhantom.this.level().levelEvent(1039, SummonedPhantom.this.blockPosition(), 0);
                    }
                } else if (SummonedPhantom.this.horizontalCollision || SummonedPhantom.this.hurtTime > 0) {
                    SummonedPhantom.this.attackPhase = SummonedPhantom.AttackPhase.CIRCLE;
                }

            }
        }
    }
}
