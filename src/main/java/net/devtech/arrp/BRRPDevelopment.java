package net.devtech.arrp;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.api.SidedRRPCallback;
import net.devtech.arrp.generator.*;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.recipe.JShapedRecipe;
import net.devtech.arrp.json.recipe.JStonecuttingRecipe;
import net.devtech.arrp.json.tags.IdentifiedTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.TestOnly;

/**
 * This class is loaded only in development environment. So it is just for testing, not the real part of this mod.
 */
@TestOnly
@ApiStatus.Internal
public class BRRPDevelopment implements ModInitializer {
  static {
    if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
      throw new RuntimeException("This code can only exist in the development environment!");
    }
  }

  public static final RuntimeResourcePack PACK = RuntimeResourcePack.create(new Identifier("brrp", "test"));
  public static final BlockSoundGroup LAVA_SOUND_GROUP = new BlockSoundGroup(1, 1, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundEvents.BLOCK_LAVA_POP, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.BLOCK_LAVA_POP, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA);
  private static final Logger LOGGER = LogManager.getLogger(BRRPDevelopment.class);
  public static final BRRPCubeBlock LAVA_BLOCK = register(BRRPCubeBlock.cubeAll(FabricBlockSettings.of(new FabricMaterialBuilder(MapColor.BRIGHT_RED).allowsMovement().lightPassesThrough().notSolid().liquid().build()).luminance(15).sounds(LAVA_SOUND_GROUP), "block/lava_still"), "lava_block");

  public static final BRRPStairsBlock LAVA_STAIRS = register(new BRRPStairsBlock(LAVA_BLOCK), "lava_stairs");
  public static final BRRPSlabBlock LAVA_SLAB = register(new BRRPSlabBlock(LAVA_BLOCK), "lava_slab");
  public static final BRRPFenceBlock LAVA_FENCE = register(new BRRPFenceBlock(LAVA_BLOCK), "lava_fence");
  public static final BRRPFenceGateBlock LAVA_FENCE_GATE = register(new BRRPFenceGateBlock(LAVA_BLOCK), "lava_fence_gate");
  public static final BRRPWallBlock LAVA_WALL = register(new BRRPWallBlock(LAVA_BLOCK), "lava_wall");
  public static final BRRPCubeBlock SMOOTH_STONE = register(BRRPCubeBlock.cubeBottomTop(FabricBlockSettings.copyOf(Blocks.SMOOTH_STONE), "block/smooth_stone", "block/smooth_stone_slab_side", "block/smooth_stone"), "smooth_stone");

  static {
    blockItem(LAVA_BLOCK);
    blockItem(LAVA_STAIRS);
    blockItem(LAVA_SLAB);
    blockItem(LAVA_FENCE);
    blockItem(LAVA_FENCE_GATE);
    blockItem(LAVA_WALL);
    blockItem(SMOOTH_STONE);
  }

  @SuppressWarnings("UnusedReturnValue")
  private static BlockItem blockItem(Block block) {
    return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), new BlockItem(block, new FabricItemSettings().group(ItemGroup.TRANSPORTATION)));
  }

  @Override
  public void onInitialize() {
    SidedRRPCallback.BEFORE_VANILLA.register(
        (resourceType, builder) -> builder.add(refreshPack(resourceType))
    );

    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      client();
    }
  }

  private static RuntimeResourcePack refreshPack(ResourceType resourceType) {
    LOGGER.info("Generating resources!");
    PACK.setForbidsDuplicateResource(true);
    PACK.clearResources(resourceType);
    if (resourceType == ResourceType.CLIENT_RESOURCES) {
      PACK.addLang(new Identifier("brrp", "en_us"), new JLang()
          .blockRespect(LAVA_BLOCK, "Lava Block (Development Environment Only)")
          .blockRespect(LAVA_STAIRS, "Lava Stairs (Development Environment Only)")
          .blockRespect(LAVA_SLAB, "Lava Slab (Development Environment Only)")
          .blockRespect(LAVA_FENCE, "Lava Fence (Development Environment Only)")
          .blockRespect(LAVA_FENCE_GATE, "Lava Fence Gate (Development Environment Only)")
          .blockRespect(LAVA_WALL, "Lava Wall (Development Environment Only)")
          .blockRespect(SMOOTH_STONE, "Smooth Stone (Development Environment Only)")
      );
      PACK.addLang(new Identifier("brrp", "zh_cn"), new JLang()
          .blockRespect(LAVA_BLOCK, "熔岩方块（仅限开发环境）")
          .blockRespect(LAVA_STAIRS, "熔岩楼梯（仅限开发环境）")
          .blockRespect(LAVA_SLAB, "熔岩台阶（仅限开发环境）")
          .blockRespect(LAVA_FENCE, "熔岩栅栏（仅限开发环境）")
          .blockRespect(LAVA_FENCE_GATE, "熔岩栅栏门（仅限开发环境）")
          .blockRespect(LAVA_WALL, "熔岩墙（仅限开发环境）")
          .blockRespect(SMOOTH_STONE, "平滑石头（仅限开发环境）")
      );
      final JLang twLang = new JLang()
          .blockRespect(LAVA_BLOCK, "熔岩方塊（僅限開發環境）")
          .blockRespect(LAVA_STAIRS, "熔岩階梯（僅限開發環境）")
          .blockRespect(LAVA_SLAB, "熔岩半磚（僅限開發環境）")
          .blockRespect(LAVA_FENCE, "熔岩柵欄（僅限開發環境）")
          .blockRespect(LAVA_FENCE_GATE, "熔岩柵欄門（僅限開發環境）")
          .blockRespect(LAVA_WALL, "熔岩墻（僅限開發環境）")
          .blockRespect(SMOOTH_STONE, "平滑石頭（僅限開發環境）");
      PACK.addLang(new Identifier("brrp", "zh_tw"), twLang);
      PACK.addLang(new Identifier("brrp", "zh_hk"), twLang
          .blockRespect(LAVA_FENCE, "熔岩欄杆（僅限開發環境）")
          .blockRespect(LAVA_FENCE_GATE, "熔岩閘門（僅限開發環境）")
      );
    }

    LAVA_BLOCK.writeResources(PACK, resourceType);
    LAVA_STAIRS.writeResources(PACK, resourceType);
    LAVA_SLAB.writeResources(PACK, resourceType);
    LAVA_FENCE.writeResources(PACK, resourceType);
    LAVA_FENCE_GATE.writeResources(PACK, resourceType);
    LAVA_WALL.writeResources(PACK, resourceType);
    SMOOTH_STONE.writeResources(PACK, resourceType);

    if (resourceType == ResourceType.SERVER_DATA) {
      ((IdentifiedTag) new IdentifiedTag("blocks", new Identifier("fences")).addBlocks(LAVA_FENCE)).write(PACK);
      ((IdentifiedTag) new IdentifiedTag("blocks", new Identifier("fence_gates")).addBlocks(LAVA_FENCE_GATE)).write(PACK);
      ((IdentifiedTag) new IdentifiedTag("blocks", new Identifier("walls")).addBlock(LAVA_WALL)).write(PACK);

      PACK.addRecipeAndAdvancement(new Identifier("brrp", "smooth_stone"), "transportation", new JShapedRecipe(Blocks.SMOOTH_STONE_SLAB).resultCount(6).pattern("###").addKey("#", SMOOTH_STONE).addInventoryChangedCriterion("has_smooth_stone", SMOOTH_STONE));
      PACK.addRecipeAndAdvancement(new Identifier("brrp", "smooth_stone_from_stonecutting"), "transportation", new JStonecuttingRecipe(SMOOTH_STONE, Blocks.SMOOTH_STONE_SLAB, 2).addInventoryChangedCriterion("has_smooth_stone", SMOOTH_STONE));
    }

    return PACK;
  }

  @Environment(EnvType.CLIENT)
  private static void client() {
  }

  @Contract("_,_ -> param1")
  private static <T extends Block> T register(T block, String name) {
    return Registry.register(Registry.BLOCK, new Identifier("brrp", name), block);
  }
}
