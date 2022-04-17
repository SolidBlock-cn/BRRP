package net.devtech.arrp;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.generator.*;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.blockstate.VariantDefinition;
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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.BuiltinBiomes;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BRRPDevelopment implements ModInitializer {
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
  public static final BRRPCubeBlock LAVA_BLOCK = register(BRRPCubeBlock.cubeAll(FabricBlockSettings.of(new FabricMaterialBuilder(MapColor.BRIGHT_RED).allowsMovement().lightPassesThrough().notSolid().liquid().build()).luminance(15).sounds(LAVA_SOUND_GROUP), "block/lava_still"), "lava_block");
  public static final BlockItem LAVA_BLOCK_ITEM = blockItem(LAVA_BLOCK);
  public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("brrp");
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
  public static final BlockItem HARDENABLE_BLOCK_ITEM = blockItem(HARDENABLE_BLOCK);
  public static final BlockItem HARDENABLE_SLAB_ITEM = blockItem(HARDENABLE_SLAB);

  public static final BRRPStairsBlock WATER_STAIRS = register(new BRRPStairsBlock(WATER_BLOCK), "water_stairs");
  public static final BRRPStairsBlock LAVA_STAIRS = register(new BRRPStairsBlock(LAVA_BLOCK), "lava_stairs");
  public static final BRRPSlabBlock WATER_SLAB = register(new BRRPSlabBlock(WATER_BLOCK), "water_slab");
  public static final BlockItem LAVA_STAIRS_ITEM = blockItem(LAVA_STAIRS);
  public static final BRRPSlabBlock LAVA_SLAB = register(new BRRPSlabBlock(LAVA_BLOCK), "lava_slab");
  public static final BlockItem LAVA_SLAB_ITEM = blockItem(LAVA_SLAB);
  public static final BRRPFenceBlock WATER_FENCE = register(new BRRPFenceBlock(WATER_BLOCK), "water_fence");
  public static final BRRPFenceBlock LAVA_FENCE = register(new BRRPFenceBlock(LAVA_BLOCK), "lava_fence");
  public static final BRRPFenceBlock WATER_FENCE_GATE = register(new BRRPFenceBlock(WATER_BLOCK), "water_fence_gate");
  public static final BRRPFenceGateBlock LAVA_FENCE_GATE = register(new BRRPFenceGateBlock(LAVA_BLOCK), "lava_fence_gate");
  private static final Logger LOGGER = LoggerFactory.getLogger(BRRPDevelopment.class);

  static {
    blockItem(LAVA_FENCE);
    blockItem(LAVA_FENCE_GATE);
    blockItem(WATER_BLOCK);
    blockItem(WATER_STAIRS);
    blockItem(WATER_SLAB);
    blockItem(WATER_FENCE);
    blockItem(WATER_FENCE_GATE);
  }

  public static final BRRPCubeBlock SMOOTH_STONE = register(BRRPCubeBlock.cubeBottomTop(FabricBlockSettings.copyOf(Blocks.SMOOTH_STONE), "block/smooth_stone", "block/smooth_stone_slab_side", "block/smooth_stone"), "smooth_stone");
  public static final BlockItem SMOOTH_STONE_ITEM = blockItem(SMOOTH_STONE);

  public static final BRRPSlabBlock CUSTOM_SLAB = register(new BRRPSlabBlock(FabricBlockSettings.of(Material.LEAVES)), "custom_slab");
  public static final BlockItem CUSTOM_SLAB_ITEM = blockItem(CUSTOM_SLAB);

  private static BlockItem blockItem(Block block) {
    return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), new BlockItem(block, new FabricItemSettings().group(ItemGroup.TRANSPORTATION)));
  }

  @Override
  public void onInitialize() {
    RRPCallback.AFTER_VANILLA.register(a -> a.add(refreshPack()));

    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      client();
    }
  }

  private static RuntimeResourcePack refreshPack() {
    LOGGER.info("Generating resources!");
    PACK.clearResources();

    PACK.addLang(new Identifier("brrp", "en_us"), new JLang()
        .blockRespect(LAVA_BLOCK, "Lava Block")
        .blockRespect(LAVA_STAIRS, "Lava Stairs")
        .blockRespect(LAVA_SLAB, "Lava Slab")
        .blockRespect(LAVA_FENCE, "Lava Fence")
        .blockRespect(LAVA_FENCE_GATE, "Lava Fence Gate")
    );
    PACK.addLang(new Identifier("brrp", "zh_cn"), new JLang()
        .blockRespect(LAVA_BLOCK, "熔岩方块")
        .blockRespect(LAVA_STAIRS, "熔岩楼梯")
        .blockRespect(LAVA_SLAB, "熔岩台阶")
        .blockRespect(LAVA_FENCE, "熔岩栅栏")
        .blockRespect(LAVA_FENCE_GATE, "熔岩栅栏门")
    );

    final VariantDefinition variants = VariantDefinition
        .of(HARDENED, false, new JBlockModel("minecraft", "block/stone"))
        .addVariant(HARDENED, true, new JBlockModel("minecraft", "block/andesite"));
    PACK.addBlockState(JBlockStates.ofVariants(variants), new Identifier("brrp", "hardenable_block"));
    PACK.addBlockState(JBlockStates.ofVariants(variants.composeToSlab(id -> id.brrp_append("_slab"), id -> id.brrp_append("_slab_top"))), new Identifier("brrp", "hardenable_slab"));
    PACK.addModel(new JModel("block/stone"), new Identifier("brrp", "item/hardenable_block"));
    PACK.addModel(new JModel("block/stone_slab"), new Identifier("brrp", "item/hardenable_slab"));

    TextureRegistry.register(CUSTOM_SLAB, "block/oak_leaves");

    LAVA_BLOCK.writeAll(PACK);
    LAVA_STAIRS.writeAll(PACK);
    LAVA_SLAB.writeAll(PACK);
    LAVA_FENCE.writeAll(PACK);
    LAVA_FENCE_GATE.writeAll(PACK);
    WATER_BLOCK.writeAll(PACK);
    WATER_STAIRS.writeAll(PACK);
    WATER_SLAB.writeAll(PACK);
    WATER_FENCE.writeAll(PACK);
    WATER_FENCE_GATE.writeAll(PACK);
    CUSTOM_SLAB.writeAll(PACK);
    SMOOTH_STONE.writeAll(PACK);

    ((IdentifiedTag) new IdentifiedTag("blocks", new Identifier("fences")).addBlocks(WATER_FENCE, LAVA_FENCE)).write(PACK);
    ((IdentifiedTag) new IdentifiedTag("blocks", new Identifier("fence_gates")).addBlocks(WATER_FENCE_GATE, LAVA_FENCE_GATE)).write(PACK);

    return PACK;
  }

  @Environment(EnvType.CLIENT)
  private static void client() {
    ColorProviderRegistry.BLOCK.register(
        (state, world, pos, tintIndex) -> {
          if (world == null || pos == null) {
            return BiomeColors.WATER_COLOR.getColor(BuiltinBiomes.getDefaultBiome().value(), 0.5, 0.5);
          }
          return BiomeColors.getWaterColor(world, pos);
        },
        WATER_BLOCK, WATER_STAIRS, WATER_SLAB, WATER_FENCE, WATER_FENCE_GATE
    );
    ColorProviderRegistry.ITEM.register(
        (stack, tintIndex) -> {
          final var block = ((BlockItem) stack.getItem()).getBlock();
          final MinecraftClient instance = MinecraftClient.getInstance();
          return ColorProviderRegistry.BLOCK.get(block).getColor(block.getDefaultState(), instance.world, instance.cameraEntity != null ? instance.cameraEntity.getBlockPos() : null, tintIndex);
        },
        WATER_BLOCK, WATER_STAIRS, WATER_SLAB, WATER_FENCE, WATER_FENCE_GATE
    );
    BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), WATER_BLOCK, WATER_STAIRS, WATER_SLAB, WATER_FENCE, WATER_FENCE_GATE);
  }

  @Contract("_,_ -> param1")
  private static <T extends Block> T register(T block, String name) {
    return Registry.register(Registry.BLOCK, new Identifier("brrp", name), block);
  }
}
