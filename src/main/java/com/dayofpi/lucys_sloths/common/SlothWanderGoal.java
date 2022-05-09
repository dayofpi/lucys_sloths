package com.dayofpi.lucys_sloths.common;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SlothWanderGoal extends WanderAroundGoal {
    protected final float probability;

    public SlothWanderGoal(PathAwareEntity pathAwareEntity, double d) {
        this(pathAwareEntity, d, 0.001f);
    }

    public SlothWanderGoal(PathAwareEntity mob, double speed, float probability) {
        super(mob, speed);
        this.probability = probability;
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        if (this.mob.isInsideWaterOrBubbleColumn()) {
            Vec3d vec3d = NoPenaltyTargeting.find(this.mob, 15, 15);
            return vec3d == null ? super.getWanderTarget() : vec3d;
        }
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            return NoPenaltyTargeting.find(this.mob, 15, 15);
        }
        return super.getWanderTarget();
    }
}
