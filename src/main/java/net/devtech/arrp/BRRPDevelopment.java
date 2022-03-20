package net.devtech.arrp;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.generator.SmartCubeBlock;
import net.devtech.arrp.generator.SmartSlabBlock;
import net.devtech.arrp.generator.TextureRegistry;
import net.devtech.arrp.json.blockstate.BlockStatesDefinition;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.VariantDefinition;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.models.JModel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BRRPDevelopment implements ModInitializer {
  public static final SmartCubeBlock LAVA_BLOCK = Registry.register(Registry.BLOCK, new Identifier("brrp", "lava_block"), SmartCubeBlock.cubeAll(FabricBlockSettings.of(Material.LAVA).luminance(15), "block/lava_still"));
  public static final BlockItem LAVA_BLOCK_ITEM = blockItem(LAVA_BLOCK);
  public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("brrp");
  public static final BooleanProperty HARDENED = BooleanProperty.of("hardened");
  public static final Block HARDENABLE_BLOCK = Registry.register(Registry.BLOCK, new Identifier("brrp", "hardenable_block"), new Block(FabricBlockSettings.of(Material.WOOL)) {
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      super.appendProperties(builder);
      builder.add(HARDENED);
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
      return state.get(HARDENED) ? 3f : 0.5f;
    }

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

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
      return HARDENABLE_BLOCK.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      return HARDENABLE_BLOCK.onUse(state, world, pos, player, hand, hit);
    }
  });
  public static final BlockItem HARDENABLE_BLOCK_ITEM = blockItem(HARDENABLE_BLOCK);
  public static final BlockItem HARDENABLE_SLAB_ITEM = blockItem(HARDENABLE_SLAB);

  public static final SmartSlabBlock LAVA_SLAB = Registry.register(Registry.BLOCK, new Identifier("brrp", "lava_slab"), new SmartSlabBlock(LAVA_BLOCK));
  public static final BlockItem LAVA_SLAB_ITEM = blockItem(LAVA_SLAB);

  public static final SmartCubeBlock SMOOTH_STONE = Registry.register(Registry.BLOCK, new Identifier("brrp", "smooth_stone"), SmartCubeBlock.cubeBottomTop(FabricBlockSettings.copyOf(Blocks.SMOOTH_STONE), "block/smooth_stone", "block/smooth_stone_slab_side", "block/smooth_stone"));
  public static final BlockItem SMOOTH_STONE_ITEM = blockItem(SMOOTH_STONE);

  public static final SmartSlabBlock CUSTOM_SLAB = Registry.register(Registry.BLOCK, new Identifier("brrp", "custom_slab"), new SmartSlabBlock(FabricBlockSettings.of(Material.LEAVES)));
  public static final BlockItem CUSTOM_SLAB_ITEM = blockItem(CUSTOM_SLAB);

  private static BlockItem blockItem(Block block) {
    return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), new BlockItem(block, new FabricItemSettings().group(ItemGroup.TRANSPORTATION)));
  }

  @Override
  public void onInitialize() {
    // lava block
//    PACK.addBlockState(
//        BlockStatesDefinition.ofVariants(VariantDefinition.ofNoVariants(new JBlockModel("brrp", "block/lava_block"))),
//        new Identifier("brrp", "lava_block"));
//    PACK.addModel(new JModel("block/cube_all").addTexture("all", "block/lava_still"), new Identifier("brrp", "block/lava_block"));
//    PACK.addModel(new JModel("brrp:block/lava_block"), new Identifier("brrp", "item/lava_block"));
    PACK.addLang(new Identifier("brrp", "en_us"), new JLang()
        .block(new Identifier("brrp", "lava_block"), "Lava Block")
        .block(new Identifier("brrp", "lava_slab"), "Lava Slab"));

    // hardenable block
    final VariantDefinition variants = VariantDefinition.of(HARDENED, false, new JBlockModel("minecraft", "block/stone")).addVariant(HARDENED, true, new JBlockModel("minecraft", "block/andesite"));
    PACK.addBlockState(BlockStatesDefinition.ofVariants(variants), new Identifier("brrp", "hardenable_block"));
    PACK.addBlockState(BlockStatesDefinition.ofVariants(variants.composeToSlab(id -> ((IdentifierExtension) id).append("_slab"), id -> ((IdentifierExtension) id).append("_slab_top"))), new Identifier("brrp", "hardenable_slab"));
    PACK.addModel(new JModel("block/stone"), new Identifier("brrp", "item/hardenable_block"));
    PACK.addModel(new JModel("block/stone_slab"), new Identifier("brrp", "item/hardenable_slab"));

    TextureRegistry.register(CUSTOM_SLAB, "block/oak_leaves");
//    TextureRegistry.register(SMOOTH_STONE, "block/smooth_stone_slab_double");

    LAVA_BLOCK.writeAll(PACK);
    LAVA_SLAB.writeAll(PACK);
    CUSTOM_SLAB.writeAll(PACK);
    SMOOTH_STONE.writeAll(PACK);

    RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
  }
}
