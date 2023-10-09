package com.example.wizards.client;

//import net.minecraft.client.model.AbstractZombieModel;
import com.example.wizards.SummonedCreature;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieModel<T extends Mob> extends AbstractZombieModel<T> {

   public ZombieModel(ModelPart p_171090_) {
      super(p_171090_);
   }

   public boolean isAggressive(T p_104155_) {
      return p_104155_.isAggressive();
   }
}