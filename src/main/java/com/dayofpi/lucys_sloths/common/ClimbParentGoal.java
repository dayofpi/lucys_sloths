package com.dayofpi.lucys_sloths.common;

import net.minecraft.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClimbParentGoal extends Goal {
    private final SlothEntity animal;
    @Nullable
    private SlothEntity parent;
    private int delay;

    public ClimbParentGoal(SlothEntity animal) {
        this.animal = animal;
    }

    @Override
    public boolean canStart() {
        if (this.animal.getBreedingAge() >= 0) {
            return false;
        }
        if (this.animal.getRandom().nextInt(toGoalTicks(120)) != 0) {
            return false;
        }
        if (this.animal.hasVehicle()) {
            return false;
        }
        List<SlothEntity> list = this.animal.world.getNonSpectatingEntities(SlothEntity.class, this.animal.getBoundingBox().expand(8.0, 4.0, 8.0));
        SlothEntity parent = null;
        double distance = Double.MAX_VALUE;
        for (SlothEntity animalEntity2 : list) {
            if (animalEntity2.getBreedingAge() < 0) continue;
            distance = this.animal.squaredDistanceTo(animalEntity2);
            parent = animalEntity2;
        }
        if (parent == null || parent.getPlantBlock() != null) {
            return false;
        }
        if (distance > 2.0) {
            return false;
        }
        this.parent = parent;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.getBreedingAge() >= 0) return false;
        if (this.animal.hasVehicle()) return false;
        if (this.parent != null && !this.parent.isAlive()) return false;
        double d = this.animal.squaredDistanceTo(this.parent);
        return d <= 2.0;
    }

    @Override
    public void tick() {
        if (--this.delay > 0) {
            return;
        }
        this.delay = this.getTickCount(10);
        this.animal.getNavigation().startMovingTo(this.parent, 1.0D);
        if (this.animal.squaredDistanceTo(this.parent) < 1)
            this.animal.startRiding(this.parent);
    }

    @Override
    public void start() {
        this.delay = 0;
    }

    @Override
    public void stop() {
        this.parent = null;
    }
}
