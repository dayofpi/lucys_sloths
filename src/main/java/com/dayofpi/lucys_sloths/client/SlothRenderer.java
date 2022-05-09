package com.dayofpi.lucys_sloths.client;

import com.dayofpi.lucys_sloths.Client;
import com.dayofpi.lucys_sloths.Main;
import com.dayofpi.lucys_sloths.common.SlothEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class SlothRenderer<T extends SlothEntity> extends MobEntityRenderer<T, SlothModel<T>> {
    public SlothRenderer(EntityRendererFactory.Context context) {
        super(context, new SlothModel<>(context.getPart(Client.SLOTH)), 0.7F);
        this.addFeature(new SlothPlantFeature<>(this, new SlothModel<>((context.getPart(Client.SLOTH)))));
    }

    @Override
    protected void scale(T entity, MatrixStack matrices, float amount) {
        if (entity.getSlothType().equals(SlothEntity.TWO_TOED))
            matrices.scale(0.8F, 0.8F, 0.8F);
        Entity vehicle = entity.getRootVehicle();
        if (vehicle instanceof PlayerEntity) {
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(45));
        }
        super.scale(entity, matrices, amount);
    }

    @Override
    public Identifier getTexture(T entity) {
        if (entity.getSlothType().equals(SlothEntity.TWO_TOED))
            return new Identifier(Main.MODID, "textures/entity/sloth/two_toed.png");
        return new Identifier(Main.MODID, "textures/entity/sloth/three_toed.png");
    }
}
