package com.dayofpi.lucys_sloths.client;

import com.dayofpi.lucys_sloths.Main;
import com.dayofpi.lucys_sloths.common.SlothEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

@SuppressWarnings("deprecation")
public class SlothPlantFeature<T extends SlothEntity> extends FeatureRenderer<T, SlothModel<T>> {
    private final SlothModel<T> model;
    private final Identifier TEXTURE = new Identifier(Main.MODID, "textures/entity/sloth/plant_layer.png");
    public SlothPlantFeature(FeatureRendererContext<T, SlothModel<T>> context, SlothModel<T> model) {
        super(context);
        this.model = model;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        BlockState blockState = entity.getPlantBlock();

        if (!entity.isBaby() && blockState != null) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            boolean bl = minecraftClient.hasOutline(entity) && entity.isInvisible();
            if (!entity.isInvisible() || bl) {
                matrices.push();
                matrices.translate(0.0D, -1.5D, 0.0D);
                renderLayer(matrices, entity, light, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch, provider);
                matrices.pop();
                BlockRenderManager blockRenderManager = minecraftClient.getBlockRenderManager();
                int m = LivingEntityRenderer.getOverlay(entity, 0.0F);
                BakedModel bakedModel = blockRenderManager.getModel(blockState);
                matrices.push();
                matrices.scale(-0.6F, -0.6F, 0.6F);
                matrices.translate(-0.5D, 0.5D, 0.6D);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
                this.renderPlant(matrices, provider, light, bl, blockRenderManager, blockState, m, bakedModel);
                matrices.pop();
            }
        }
    }

    private void renderLayer(MatrixStack matrices, T entity, int light, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, VertexConsumerProvider provider) {
        this.getContextModel().copyStateTo(this.model);
        this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
        this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        VertexConsumer vertexConsumer = provider.getBuffer(RenderLayer.getEntityCutoutNoCull(this.TEXTURE));
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

    }

    private void renderPlant(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean renderAsModel, BlockRenderManager blockRenderManager, BlockState mushroomState, int overlay, BakedModel mushroomModel) {
        if (renderAsModel) {
            blockRenderManager.getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(RenderLayer.getOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)), mushroomState, mushroomModel, 0.0F, 0.0F, 0.0F, light, overlay);
        } else {
            blockRenderManager.renderBlockAsEntity(mushroomState, matrices, vertexConsumers, light, overlay);
        }

    }
}
