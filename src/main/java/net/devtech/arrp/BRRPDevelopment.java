package net.devtech.arrp;

import net.devtech.arrp.api.RRPCallbackConditional;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.generator.*;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.blockstate.JVariants;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.models.JElement;
import net.devtech.arrp.json.models.JFace;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.devtech.arrp.json.tags.IdentifiedTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Objects;

/**
 * This class is loaded only in development environment. So it is just for testing, not the real part of this mod.
 */
public class BRRPDevelopment implements ModInitializer {
  public static final RuntimeResourcePack PACK = RuntimeResourcePack.create(new Identifier("brrp", "test"));
  public static final BlockSoundGroup WATER_SOUND_GROUP = new BlockSoundGroup(1, 1, SoundEvents.ITEM_BUCKET_EMPTY, SoundEvents.ENTITY_PLAYER_SWIM, SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ENTITY_BOAT_PADDLE_WATER, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER);
  public static final BlockSoundGroup LAVA_SOUND_GROUP = new BlockSoundGroup(1, 1, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundEvents.BLOCK_LAVA_POP, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.BLOCK_LAVA_POP, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA);
  public static final BRRPCubeBlock WATER_BLOCK = register(new BRRPCubeBlock(FabricBlockSettings.of(new FabricMaterialBuilder(MapColor.WATER_BLUE).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().liquid().build()).nonOpaque().sounds(WATER_SOUND_GROUP), "block/cube_all", JTextures.ofAll("block/water_still")) {
    /**
     * @see TransparentBlock
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
      return stateFrom.isOf(this) || super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public @NotNull JModel getBlockModel() {
      return super.getBlockModel().element(JElement.of(0, 0, 0, 16, 16, 16).addAllFaces(direction -> new JFace("all").tintIndex(1).cullface(direction)));
    }
  }, "water_block");
  public static final BooleanProperty HARDENED = BooleanProperty.of("hardened");
  public static final Block HARDENABLE_BLOCK = Registry.register(Registry.BLOCK, new Identifier("brrp", "hardenable_block"), new Block(FabricBlockSettings.of(Material.WOOL)) {
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      super.appendProperties(builder);
      builder.add(HARDENED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
      return state.get(HARDENED) ? 3f : 0.5f;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      world.setBlockState(pos, state.with(HARDENED, !state.get(HARDENED)));
      return ActionResult.SUCCESS;
    }
  });
  public static final SlabBlock HARDENABLE_SLAB = Registry.register(Registry.BLOCK, new Identifier("brrp", "hardenable_slab"), new SlabBlock(FabricBlockSettings.of(Material.WOOL)) {
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      super.appendProperties(builder);
      builder.add(HARDENED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
      return HARDENABLE_BLOCK.calcBlockBreakingDelta(state, player, world, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      return HARDENABLE_BLOCK.onUse(state, world, pos, player, hand, hit);
    }
  });
  private static final Logger LOGGER = LoggerFactory.getLogger(BRRPDevelopment.class);
  public static final BRRPCubeBlock LAVA_BLOCK = register(BRRPCubeBlock.cubeAll(FabricBlockSettings.of(new FabricMaterialBuilder(MapColor.BRIGHT_RED).allowsMovement().lightPassesThrough().notSolid().liquid().build()).luminance(15).sounds(LAVA_SOUND_GROUP), "block/lava_still"), "lava_block");

  public static final BRRPStairsBlock LAVA_STAIRS = register(new BRRPStairsBlock(LAVA_BLOCK), "lava_stairs");
  public static final BRRPSlabBlock LAVA_SLAB = register(new BRRPSlabBlock(LAVA_BLOCK), "lava_slab");
  public static final BRRPFenceBlock LAVA_FENCE = register(new BRRPFenceBlock(LAVA_BLOCK), "lava_fence");
  public static final BRRPFenceGateBlock LAVA_FENCE_GATE = register(new BRRPFenceGateBlock(LAVA_BLOCK), "lava_fence_gate");
  public static final BRRPWallBlock LAVA_WALL = register(new BRRPWallBlock(LAVA_BLOCK), "lava_wall");

  public static final BRRPCubeBlock IRON_BLOCK = register(BRRPCubeBlock.cubeAll(FabricBlockSettings.of(Material.METAL).breakInstantly(), "block/iron_block"), "iron_block");
  public static final BRRPCubeBlock SMOOTH_STONE = register(BRRPCubeBlock.cubeBottomTop(FabricBlockSettings.copyOf(Blocks.SMOOTH_STONE), "block/smooth_stone", "block/smooth_stone_slab_side", "block/smooth_stone"), "smooth_stone");

  public static final BRRPSlabBlock CUSTOM_SLAB = register(new BRRPSlabBlock(FabricBlockSettings.of(Material.LEAVES).nonOpaque()) {
    @Override
    @Environment(EnvType.CLIENT)
    public void writeBlockModel(RuntimeResourcePack pack) {
      final Identifier id = getBlockModelId();
      final JTextures textures = new JTextures()
          .var("up", getTextureId(TextureKey.TOP))
          .var("down", getTextureId(TextureKey.BOTTOM))
          .var("east", getTextureId(TextureKey.EAST))
          .var("west", getTextureId(TextureKey.WEST))
          .var("north", getTextureId(TextureKey.NORTH))
          .var("south", getTextureId(TextureKey.SOUTH));
      pack.addModel(
          new JModel("block/block")
              .element(new JElement()
                  .from(0, 0, 0)
                  .to(16, 8, 16)
                  .addAllFaces(direction -> new JFace(direction.asString()).cullface(direction == Direction.NORTH ? null : direction).tintIndex(0))
              ).textures(textures), id);
      pack.addModel(
          new JModel("block/block")
              .element(new JElement()
                  .from(0, 8, 0)
                  .to(16, 16, 16)
                  .addAllFaces(direction -> new JFace(direction.asString()).cullface(direction == Direction.DOWN ? null : direction).tintIndex(0))
              ).textures(textures), id.brrp_append("_top"));
      pack.addModel(
          new JModel("block/block")
              .element(new JElement()
                  .from(0, 0, 0)
                  .to(16, 16, 16)
                  .addAllFaces(direction -> new JFace(direction.asString()).cullface(direction).tintIndex(0))
              ).textures(textures), id.brrp_append("_double"));
    }
  }, "custom_slab");

  static {
    blockItem(WATER_BLOCK);
    blockItem(HARDENABLE_BLOCK);
    blockItem(HARDENABLE_SLAB);
    blockItem(LAVA_BLOCK);
    blockItem(LAVA_STAIRS);
    blockItem(LAVA_SLAB);
    blockItem(LAVA_FENCE);
    blockItem(LAVA_FENCE_GATE);
    blockItem(LAVA_WALL);
    blockItem(SMOOTH_STONE);
    blockItem(CUSTOM_SLAB);
    blockItem(IRON_BLOCK);
  }

  @SuppressWarnings("UnusedReturnValue")
  private static BlockItem blockItem(Block block) {
    return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), new BlockItem(block, new FabricItemSettings().group(ItemGroup.TRANSPORTATION)));
  }

  @Override
  public void onInitialize() {
    RRPCallbackConditional.BEFORE_VANILLA.register(
        (resourceType, builder) -> builder.add(refreshPack(resourceType))
    );

    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      client();
    }
  }

  private static RuntimeResourcePack refreshPack(ResourceType resourceType) {
    LOGGER.info("Generating resources!");
    PACK.clearResources(resourceType);

    if (resourceType == ResourceType.CLIENT_RESOURCES) {
      TextureRegistry.register(CUSTOM_SLAB, new Identifier("block/oak_leaves"));
      TextureRegistry.register(CUSTOM_SLAB, TextureKey.END, new Identifier("block/spruce_leaves"));
    }

    PACK.addLang(new Identifier("brrp", "en_us"), new JLang()
        .blockRespect(HARDENABLE_BLOCK, "Hardenable Block")
        .blockRespect(HARDENABLE_SLAB, "Hardenable Slab")
        .blockRespect(LAVA_BLOCK, "Lava Block")
        .blockRespect(LAVA_STAIRS, "Lava Stairs")
        .blockRespect(LAVA_SLAB, "Lava Slab")
        .blockRespect(LAVA_FENCE, "Lava Fence")
        .blockRespect(LAVA_FENCE_GATE, "Lava Fence Gate")
        .blockRespect(LAVA_WALL, "Lava Wall")
        .blockRespect(WATER_BLOCK, "Water Block")
        .blockRespect(CUSTOM_SLAB, "Custom Slab")
        .blockRespect(IRON_BLOCK, "Iron Block")
        .blockRespect(SMOOTH_STONE, "Smooth Stone")
    );
    PACK.addLang(new Identifier("brrp", "zh_cn"), new JLang()
        .blockRespect(HARDENABLE_BLOCK, "可硬方块")
        .blockRespect(HARDENABLE_SLAB, "可硬台阶")
        .blockRespect(LAVA_BLOCK, "熔岩方块")
        .blockRespect(LAVA_STAIRS, "熔岩楼梯")
        .blockRespect(LAVA_SLAB, "熔岩台阶")
        .blockRespect(LAVA_FENCE, "熔岩栅栏")
        .blockRespect(LAVA_FENCE_GATE, "熔岩栅栏门")
        .blockRespect(LAVA_WALL, "熔岩墙")
        .blockRespect(WATER_BLOCK, "水方块")
        .blockRespect(CUSTOM_SLAB, "自定义台阶")
        .blockRespect(IRON_BLOCK, "铁块")
        .blockRespect(SMOOTH_STONE, "平滑石头")
    );
    final JLang twLang = new JLang()
        .blockRespect(HARDENABLE_BLOCK, "可硬方塊")
        .blockRespect(HARDENABLE_SLAB, "可硬台階")
        .blockRespect(LAVA_BLOCK, "熔岩方塊")
        .blockRespect(LAVA_STAIRS, "熔岩階梯")
        .blockRespect(LAVA_SLAB, "熔岩半磚")
        .blockRespect(LAVA_FENCE, "熔岩柵欄")
        .blockRespect(LAVA_FENCE_GATE, "熔岩柵欄門")
        .blockRespect(LAVA_WALL, "熔岩墻")
        .blockRespect(WATER_BLOCK, "水方塊")
        .blockRespect(CUSTOM_SLAB, "自訂半磚")
        .blockRespect(IRON_BLOCK, "鐵塊")
        .blockRespect(SMOOTH_STONE, "平滑石頭");
    PACK.addLang(new Identifier("brrp", "zh_tw"), twLang);
    PACK.addLang(new Identifier("brrp", "zh_hk"), twLang
        .blockRespect(LAVA_FENCE, "熔岩欄杆")
        .blockRespect(LAVA_FENCE_GATE, "熔岩閘門")
    );

    LAVA_BLOCK.writeResources(PACK, resourceType);
    LAVA_STAIRS.writeResources(PACK, resourceType);
    LAVA_SLAB.writeResources(PACK, resourceType);
    LAVA_FENCE.writeResources(PACK, resourceType);
    LAVA_FENCE_GATE.writeResources(PACK, resourceType);
    LAVA_WALL.writeResources(PACK, resourceType);
    WATER_BLOCK.writeResources(PACK, resourceType);
    CUSTOM_SLAB.writeResources(PACK, resourceType);
    SMOOTH_STONE.writeResources(PACK, resourceType);
    IRON_BLOCK.writeResources(PACK, resourceType);

    if (resourceType == ResourceType.CLIENT_RESOURCES) {
      final JVariants variants = JVariants
          .of(HARDENED, false, new JBlockModel("minecraft", "block/stone"))
          .addVariant(HARDENED, true, new JBlockModel("minecraft", "block/andesite"));
      PACK.addModel(new JModel("block/cube_all").addTexture("all", "block/iron_block").element(
          new JElement().from(0, 0, 0).to(16, 16, 16)
              .addAllFaces(direction -> new JFace("all").tintIndex(0).cullface(direction))
      ), new Identifier("brrp", "block/iron_block"));
      PACK.addBlockState(JBlockStates.ofVariants(variants), new Identifier("brrp", "hardenable_block"));
      PACK.addBlockState(JBlockStates.ofVariants(variants.composeToSlab(id -> id.brrp_append("_slab"), id -> id.brrp_append("_slab_top"))), new Identifier("brrp", "hardenable_slab"));
      PACK.addModel(new JModel("block/stone"), new Identifier("brrp", "item/hardenable_block"));
      PACK.addModel(new JModel("block/stone_slab"), new Identifier("brrp", "item/hardenable_slab"));

    } else {
      ((IdentifiedTag) new IdentifiedTag("blocks", new Identifier("fences")).addBlocks(LAVA_FENCE)).write(PACK);
      ((IdentifiedTag) new IdentifiedTag("blocks", new Identifier("fence_gates")).addBlocks(LAVA_FENCE_GATE)).write(PACK);
      ((IdentifiedTag) new IdentifiedTag("blocks", new Identifier("walls")).addBlock(LAVA_WALL)).write(PACK);
    }

    return PACK;
  }

  @Environment(EnvType.CLIENT)
  private static void client() {
    ColorProviderRegistry.BLOCK.register(
        (state, world, pos, tintIndex) -> {
          if (world == null || pos == null) {
            return BiomeColors.WATER_COLOR.getColor(BuiltinRegistries.BIOME.get(BiomeKeys.PLAINS), 0.5, 0.5);
          }
          return BiomeColors.getWaterColor(world, pos);
        },
        WATER_BLOCK
    );
    ColorProviderRegistry.BLOCK.register(
        (state, world, pos, tintIndex) -> {
          if (pos == null) return -1;
          float x = pos.getX();
          float y = pos.getY();
          float z = pos.getZ();
          return colorOf(x, y, z);
        },
        CUSTOM_SLAB, IRON_BLOCK
    );
    ColorProviderRegistry.ITEM.register(
        (stack, tintIndex) -> {
          final Block block = ((BlockItem) stack.getItem()).getBlock();
          final MinecraftClient instance = MinecraftClient.getInstance();
          return Objects.requireNonNull(ColorProviderRegistry.BLOCK.get(block)).getColor(block.getDefaultState(), instance.world, instance.cameraEntity != null ? instance.cameraEntity.getBlockPos() : null, tintIndex);
        },
        WATER_BLOCK
    );
    ColorProviderRegistry.ITEM.register(
        (stack, tintIndex) -> {
          final ClientPlayerEntity player = MinecraftClient.getInstance().player;
          if (player == null) return -1;
          final Vec3d pos = player.getPos();
          return colorOf((float) pos.x, (float) pos.y, (float) pos.z);
        },
        IRON_BLOCK
    );
    BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), WATER_BLOCK);
    BlockRenderLayerMap.INSTANCE.putBlock(CUSTOM_SLAB, RenderLayer.getCutout());
  }

  @Environment(EnvType.CLIENT)
  private static int colorOf(float x, float y, float z) {
    x += 4 * MathHelper.sin(0.152f * x + 0.08f * y + 0.071f * z + 5.15f);
    y += 4 * MathHelper.sin(0.152f * x + 0.04f * y + 0.059f * z + 3.55f);
    z += 4 * MathHelper.sin(0.152f * x + 0.03f * y + 0.082f * z - 4.27f);
    float i = MathHelper.sin(0.05f * x + 0.05f * y + 0.05f * z - 0.24f);
    z -= 4 * MathHelper.cos(0.108f * x + 0.07f * y + 0.054f * z + 3.33f);
    y -= 4 * MathHelper.cos(0.108f * x + 0.06f * y + 0.036f * z + 3.44f);
    x -= 4 * MathHelper.cos(0.108f * x + 0.07f * y + 0.052f * z - 3.55f);
    x += 3 * MathHelper.cos(0.1f * x - i + MathHelper.sin(i) + 2.44f);
    y += 3 * MathHelper.cos(0.1f * y - i + MathHelper.sin(i) + 0.55f);
    z += 3 * MathHelper.cos(0.1f * z - i + MathHelper.sin(i) - 1.66f);
    return Color.HSBtoRGB(0f * (x + y + z)
            + 0.75f * MathHelper.cos(0.0011f * x + 0.0005f * y + -0.007f * z)
            + 0.75f * MathHelper.cos(0.0005f * x + -0.007f * x + 0.0011f * z - 3)
            + 0.75f * MathHelper.cos(-0.007f * x + 0.0011f * y + 0.0005f * z - 6)
            + 0.69f * MathHelper.sin(-0.009f * x + 0.0056f * y + 0.0083f * z - 0.17f)
            + 0.69f * MathHelper.sin(0.0056f * x + 0.0083f * x + -0.009f * z - 0.19f)
            + 0.69f * MathHelper.sin(0.0083f * x + -0.009f * y + 0.0056f * z - 0.21f)
            + -0.85f * MathHelper.sin(0.0087f * x + -0.008f * y + 0.0083f * z - 1.61f)
            + -0.85f * MathHelper.sin(-0.008f * x + 0.0083f * x + 0.0087f * z - 2.01f)
            + -0.85f * MathHelper.sin(0.0083f * x + 0.0087f * y + -0.008f * z - 2.71f)
        ,
        0.7f
            + 0.15f * MathHelper.sin(0.225f * x + 0.25f * y + 0.175f * z + 2)
            + 0.15f * MathHelper.sin(0.175f * x + 0.25f * y + 0.225f * z + 2.14f),
        0.8f
            + 0.1f * MathHelper.sin(-0.031f * x + 0.053f * y + 0.062f * z + 3)
            + 0.1f * MathHelper.sin(0.062f * x + 0.053f * y + -0.031f * z + 3.14f)
    );
  }

  @Contract("_,_ -> param1")
  private static <T extends Block> T register(T block, String name) {
    return Registry.register(Registry.BLOCK, new Identifier("brrp", name), block);
  }
}
