package com.dayofpi.lucys_sloths.common;

import com.dayofpi.lucys_sloths.Main;
import com.dayofpi.lucys_sloths.Sounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SlothEntity extends AnimalEntity {
    public static final String TWO_TOED = "two_toed";
    public static final String THREE_TOED = "three_toed";
    private static final TrackedData<String> TYPE;
    private static final TrackedData<Optional<BlockState>> PLANT_BLOCK;
    private static final TrackedData<Boolean> IS_ATTACHED;
    private static final TrackedData<BlockPos> ATTACHED_POS;

    static {
        TYPE = DataTracker.registerData(SlothEntity.class, TrackedDataHandlerRegistry.STRING);
        PLANT_BLOCK = DataTracker.registerData(SlothEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
        IS_ATTACHED = DataTracker.registerData(SlothEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ATTACHED_POS = DataTracker.registerData(SlothEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    }

    private final List<Block> naturalPlants = List.of(Blocks.GRASS, Blocks.FERN, Blocks.OAK_SAPLING, Blocks.JUNGLE_SAPLING);

    public SlothEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 0.0f);
    }

    public static DefaultAttributeContainer.Builder createSlothAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.08D);
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<SlothEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        BlockPos floor = blockPos.down();
        return isBlockValidForSpawn(floor, world) && isLightLevelValidForNaturalSpawn(world, blockPos);
    }

    private static boolean isBlockValidForSpawn(BlockPos blockPos, ServerWorldAccess world) {
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isIn(BlockTags.LOGS) || blockState.isIn(BlockTags.DIRT);
    }

    @Override
    public double getMountedHeightOffset() {
        double d = 0.5;
        if (this.getSlothType().equals(THREE_TOED)) d = 0.7;
        return super.getMountedHeightOffset() * d;
    }

    public boolean isRidingPlayer() {
        return this.getVehicle() instanceof PlayerEntity;
    }

    @Override
    public double getHeightOffset() {
        if (this.isRidingPlayer())
            return -0.5D;
        return super.getHeightOffset();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(Sounds.SLOTH_STEP, 0.2F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return Sounds.SLOTH_AMBIENT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return Sounds.SLOTH_DEATH;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return Sounds.SLOTH_HURT;
    }

    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.1D));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, OcelotEntity.class, 10.0F, 1.0D, 1.1D));
        this.goalSelector.add(4, new TemptGoal(this, 1.1D, Ingredient.fromTag(ItemTags.LEAVES), false));
        this.goalSelector.add(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.add(7, new ClimbParentGoal(this));
        this.goalSelector.add(7, new SlothWanderGoal(this, 0.9D));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.add(7, new LookAtEntityGoal(this, SlothEntity.class, 4.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TYPE, TWO_TOED);
        this.dataTracker.startTracking(PLANT_BLOCK, Optional.empty());
        this.dataTracker.startTracking(IS_ATTACHED, false);
        this.dataTracker.startTracking(ATTACHED_POS, BlockPos.ORIGIN);
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        if (world.getBlockState(pos.down()).isIn(BlockTags.LOGS) || world.getBlockState(pos.up()).isIn(BlockTags.LEAVES)) {
            return 10.0f;
        }
        if (world.getBlockState(pos.down()).isOf(Blocks.GRASS_BLOCK)) {
            return 5.0f;
        }
        return world.getBrightness(pos) - 0.5f;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("Type", this.getSlothType());
        BlockState blockState = this.getPlantBlock();
        BlockPos blockPos = this.getAttachedPos();
        if (blockState != null) {
            nbt.put("plantBlockState", NbtHelper.fromBlockState(blockState));
        }
        nbt.putBoolean("IsAttached", this.isAttached());
        nbt.putInt("AttachedX", blockPos.getX());
        nbt.putInt("AttachedY", blockPos.getY());
        nbt.putInt("AttachedZ", blockPos.getZ());
    }

    public String getSlothType() {
        return this.dataTracker.get(TYPE);
    }

    private void setSlothType(String type) {
        this.dataTracker.set(TYPE, type);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSlothType(nbt.getString("Type"));
        BlockState blockState = null;
        if (nbt.contains("plantBlockState", 10)) {
            blockState = NbtHelper.toBlockState(nbt.getCompound("plantBlockState"));
            if (blockState.isAir()) {
                blockState = null;
            }
        }
        this.setIsAttached(nbt.getBoolean("isAttached"));
        int x = nbt.getInt("AttachedX");
        int y = nbt.getInt("AttachedY");
        int z = nbt.getInt("AttachedZ");
        this.setAttachedPos(new BlockPos(x, y, z));

        this.setPlantBlock(blockState);
    }

    @Nullable
    public BlockState getPlantBlock() {
        return this.dataTracker.get(PLANT_BLOCK).orElse(null);
    }

    public void setPlantBlock(@Nullable BlockState state) {
        this.dataTracker.set(PLANT_BLOCK, Optional.ofNullable(state));
    }

    public BlockPos getAttachedPos() {
        return this.dataTracker.get(ATTACHED_POS);
    }

    public void setAttachedPos(BlockPos blockPos) {
        this.dataTracker.set(ATTACHED_POS, blockPos);
    }

    public boolean isAttached() {
        return this.dataTracker.get(IS_ATTACHED);
    }

    public void setIsAttached(boolean isAttached) {
        this.dataTracker.set(IS_ATTACHED, isAttached);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ItemTags.LEAVES);
    }

    @Override
    protected void onSwimmingStart() {
        super.onSwimmingStart();
        if (this.getPlantBlock() != null || this.isBaby() || this.world.isClient) return;
        if (this.random.nextFloat() > 0.9F) {
            this.playSound(Sounds.SLOTH_GROW_SEAGRASS, 1.0F, this.getSoundPitch());
            this.setPlantBlock(Blocks.SEAGRASS.getDefaultState());
        }
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        if (this.getPlantBlock() != null) this.dropItem(this.getPlantBlock().getBlock());
    }

    private EntityData initializeRider(ServerWorldAccess world, LocalDifficulty difficulty, MobEntity rider) {
        rider.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0f);
        rider.initialize(world, difficulty, SpawnReason.JOCKEY, null, null);
        rider.startRiding(this, true);
        return new PassiveData(0.0f);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setSlothType(random.nextBoolean() ? TWO_TOED : THREE_TOED);
        if (this.isBaby()) return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        if (random.nextInt(6) == 0) {
            this.setPlantBlock(naturalPlants.get(random.nextInt(naturalPlants.size() - 1)).getDefaultState());
        } else if (random.nextInt(6) == 0) {
            PassiveEntity passiveEntity = Main.SLOTH.create(world.toServerWorld());
            if (passiveEntity != null) {
                passiveEntity.setBreedingAge(-24000);
                entityData = this.initializeRider(world, difficulty, passiveEntity);
            }
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(Items.SHEARS)) {
            if (!this.world.isClient && this.getPlantBlock() != null) {
                this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
                this.dropItem(this.getPlantBlock().getBlock());
                this.setPlantBlock(null);
                this.emitGameEvent(GameEvent.SHEAR, player);
                stack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.CONSUME;
            }
        } else {
            ActionResult actionResult = super.interactMob(player, hand);
            if (actionResult.isAccepted() && this.isBreedingItem(stack)) {
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                if (!this.isBaby() && this.getPlantBlock() == null && random.nextBoolean()) {
                    if (!this.world.isClient) {
                        this.world.sendEntityStatus(this, (byte) 38);
                        this.playSound(SoundEvents.ITEM_BONE_MEAL_USE, 1.0F, 1.0F);
                        this.setPlantBlock(getSaplingFromStack(stack));
                    }
                }
            }
            else if (!player.shouldCancelInteraction()) {
                    this.startRiding(player);
                    this.calculateDimensions();
                return ActionResult.success(world.isClient);
            }

            return actionResult;
        }
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 38) {
            this.spawnParticlesAround();
        } else {
            super.handleStatus(status);
        }
    }

    private void spawnParticlesAround() {
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.01;
            double e = this.random.nextGaussian() * 0.01;
            double f = this.random.nextGaussian() * 0.01;
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.2, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public boolean isClimbing() {
        return super.isClimbing() || this.isAttached();
    }

    @Override
    protected void onGrowUp() {
        this.dismountVehicle();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (this.isRidingPlayer())
            return super.getDimensions(pose).scaled(0.2F, 1.2F);
        return super.getDimensions(pose);
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        if ((attacker.getPassengerList().contains(this)))
            return true;
        return super.handleAttack(attacker);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        Entity entity = this.getVehicle();
        if (entity instanceof SlothEntity && ((SlothEntity) entity).getPlantBlock() != null) {
            this.dismountVehicle();
        }
        if (entity instanceof PlayerEntity) {
            this.setYaw(entity.getYaw());
            if (entity.isSneaking()) {
                this.dismountVehicle();
                this.calculateDimensions();
            }
        }
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SpiderNavigation(this, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isAlive() || this.world.isClient) return;
        List<BlockPos> posList = BlockPos.stream(this.getBoundingBox().expand(0.5, 0, 0.5)).toList();
        for (BlockPos blockPos : posList) {
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isIn(BlockTags.LOGS) && this.getAttachedPos() != blockPos) {
                this.setAttachedPos(blockPos);
                this.setIsAttached(true);
            }
        }

        if (this.isAttached()) {
            this.setVelocity(this.getVelocity().multiply(1.1D));
        } else if (this.squaredDistanceTo(Vec3d.ofCenter(this.getAttachedPos())) > 2 || !world.getBlockState(this.getAttachedPos()).isIn(BlockTags.LOGS)) {
            this.setIsAttached(false);
        }
    }

    private BlockState getSaplingFromStack(ItemStack stack) {
        if (stack.isOf(Blocks.SPRUCE_LEAVES.asItem())) return Blocks.SPRUCE_SAPLING.getDefaultState();
        else if (stack.isOf(Blocks.BIRCH_LEAVES.asItem())) return Blocks.BIRCH_SAPLING.getDefaultState();
        else if (stack.isOf(Blocks.JUNGLE_LEAVES.asItem())) return Blocks.JUNGLE_SAPLING.getDefaultState();
        else if (stack.isOf(Blocks.ACACIA_LEAVES.asItem())) return Blocks.ACACIA_SAPLING.getDefaultState();
        else if (stack.isOf(Blocks.DARK_OAK_LEAVES.asItem())) return Blocks.DARK_OAK_SAPLING.getDefaultState();
        else if (stack.isOf(Blocks.AZALEA_LEAVES.asItem())) return Blocks.AZALEA.getDefaultState();
        else if (stack.isOf(Blocks.FLOWERING_AZALEA_LEAVES.asItem())) return Blocks.FLOWERING_AZALEA.getDefaultState();
        return Blocks.OAK_SAPLING.getDefaultState();
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(0.1F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9D));
            this.setVelocity(this.getVelocity().add(0.0D, -0.005D, 0.0D));
        } else {
            super.travel(movementInput);
        }
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        SlothEntity slothEntity = Main.SLOTH.create(world);
        if (slothEntity != null && entity instanceof SlothEntity) {
            slothEntity.setSlothType(random.nextBoolean() ? this.getSlothType() : ((SlothEntity) entity).getSlothType());
        }
        return slothEntity;
    }
}
