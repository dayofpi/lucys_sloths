package com.dayofpi.lucys_sloths;

import com.dayofpi.lucys_sloths.common.SlothEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final String MODID = "lucys_sloths";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final EntityType<SlothEntity> SLOTH = FabricEntityTypeBuilder.createMob().entityFactory(SlothEntity::new).spawnGroup(SpawnGroup.CREATURE).spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SlothEntity::canSpawn).defaultAttributes(SlothEntity::createSlothAttributes).dimensions(EntityDimensions.changing(1.0F, 0.55F)).build();

	public static final Item SLOTH_SPAWN_EGG = new SpawnEggItem(SLOTH, 7041069, 7360555, new FabricItemSettings().group(ItemGroup.MISC));

	@Override
	public void onInitialize() {
		LOGGER.info("Mod initialized");
		Registry.register(Registry.ENTITY_TYPE, new Identifier(Main.MODID, "sloth"), SLOTH);
		Registry.register(Registry.ITEM, new Identifier(Main.MODID, "sloth_spawn_egg"), SLOTH_SPAWN_EGG);
		Sounds.initSounds();
		BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), SpawnGroup.CREATURE, SLOTH, 50, 3, 5);
	}
}
