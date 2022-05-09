package com.dayofpi.lucys_sloths.mixin;

import com.dayofpi.lucys_sloths.common.SlothEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OcelotEntity.class)
public abstract class OcelotEntityMixin extends MobEntity {
    protected OcelotEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at=@At("TAIL"), method = "initGoals")
    private void initGoals(CallbackInfo ci) {
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, SlothEntity.class, false));
    }
}
