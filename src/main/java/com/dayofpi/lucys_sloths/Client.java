package com.dayofpi.lucys_sloths;

import com.dayofpi.lucys_sloths.client.SlothModel;
import com.dayofpi.lucys_sloths.client.SlothRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class Client implements ClientModInitializer {
    public static final EntityModelLayer SLOTH = new EntityModelLayer(new Identifier(Main.MODID, "sloth"), "main");

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(SLOTH, SlothModel::getTexturedModelData);
        EntityRendererRegistry.register(Main.SLOTH, SlothRenderer::new);
    }
}
