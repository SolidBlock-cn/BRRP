package net.devtech.arrp;

import net.devtech.arrp.api.RRPEventHelper;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.generator.*;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.recipe.JShapedRecipe;
import net.devtech.arrp.json.recipe.JStonecuttingRecipe;
import net.devtech.arrp.json.tags.IdentifiedTag;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.TestOnly;
import pers.solid.brrp.PlatformBridge;

/**
 * <p>This class is loaded only in development environment. So it is just for testing, and should not be seen as the real part of this mod. This class also servers as a simple example of the usage of APIs of this mod. Therefore, you can read the sources of the class to have a simple understanding of the usage.</p>
 * <p>To help readers better understand the usage, the comments in this class is detailed and written in both Chinese and English</p>
 * <p>本类仅在开发环境中加载。因此，本类仅用于测试，不应视为模组的真实部分。本类亦可用作本模组 API 的一些简单示例，因此你可以阅读本类的源代码以简单了解其用法。</p>
 * <p>为帮助读者更好地理解其用法，本类中的注释会比较详细，且使用中英双语编写。</p>
 * <p>For Fabric, this class can be seen as implemented {@code ModInitializer}, and {@link #registerPacks()} is the initialization method. Because the special structure of the mod, the implementation of interfaces is not shown in this code, but is implemented via mixin.</p>
 * <p>For Forge, the class is initialized when blocks are registered, because registries are not frozen at this time.</p>
 * <p>对于 Fabric 而言，本类可以视为实现了 {@code ModInitializer}，其中初始化方法为 {@link #registerPacks()}。由于模组的特殊结构，本代码中并不会显示其实现了接口，而是通过 mixin 来实现的。</p>
 * <p>对于 Forge 而言，本类在注册方块时初始化，因为此时注册表没有被冻结。</p>
 */
@TestOnly
@ApiStatus.Internal
public class BRRPDevelopment {
  static {
    Validate.isTrue(PlatformBridge.getInstance().isDevelopmentEnvironment(), "The class 'BRRPDevelopment' should not be loaded out of the development environment.");
  }

  /**
   * <p>The runtime resource pack that will be used in development environment, as a simple example. The object is created in the initialization of this class.
   * <p>开发环境中使用的运行时资源包，可用作简单的示例。此对象是在类初始化时创建的。
   */
  private static final RuntimeResourcePack PACK = RuntimeResourcePack.create(new Identifier("brrp", "test"));
  public static final BlockSoundGroup LAVA_SOUND_GROUP = new BlockSoundGroup(1, 1, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundEvents.BLOCK_LAVA_POP, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.BLOCK_LAVA_POP, SoundEvents.BLOCK_LAVA_AMBIENT);
  private static final Logger LOGGER = LogManager.getLogger(BRRPDevelopment.class);
  /**
   * <p>The block, as its name indicates, is a block with a texture of lava. It is an instance of {@link BRRPCubeBlock}, which can directly specify textures and can conveniently generation all resources of it, taking the convenience of {@link BlockResourceGenerator} interface, which is implemented by {@code BRRPCubeBlock}.
   * <p>Please do not think that objects that implement {@link BlockResourceGenerator} can auto generate resources. It just provides methods such as {@code writeResources} so you do not need to manually specify how to generate models, loot tables, etc. You still should invoke the methods that write resources into the runtime resource pack.
   * <p>这个方块，正如其名，是使用熔岩作为纹理的方块。它是 {@link BRRPCubeBlock} 的实例，这个类可以直接指定纹理并方便地生成所有资源，这是因为 {@code BRRPCubeBlock} 类实现了 {@link BlockResourceGenerator} 这个接口。
   * <p>请不要认为实现了 {@link BlockResourceGenerator} 的类就能自动生成资源。这个接口只是提供了一些方法，例如 {@code writeResources}，这样你可以不必手动指定如何生成模型、战利品表等。你依然需要调用将资源写入运行时资源包的方法。
   */
  public static final BRRPCubeBlock LAVA_BLOCK = register(BRRPCubeBlock.cubeAll(AbstractBlock.Settings.of(new Material.Builder(MapColor.BRIGHT_RED).allowsMovement().notSolid().liquid().build()).luminance(state -> 15).sounds(LAVA_SOUND_GROUP), "block/lava_still"), "lava_block");

  /**
   * <p>The block is the stairs of {@link #LAVA_BLOCK}. When generating models, the textures identifier will be those of the base block. The generation of the block states, and model and recipe of stairs is defined in {@link BRRPStairsBlock}.
   * <p>这个方块是 {@link #LAVA_BLOCK} 的楼梯。生成模型时，会直接使用其基础方块的纹理 id。{@link BRRPStairsBlock} 中定义了如何生成楼梯的方块状态、模型和配方。
   */
  public static final BRRPStairsBlock LAVA_STAIRS = register(new BRRPStairsBlock(LAVA_BLOCK), "lava_stairs");
  public static final BRRPSlabBlock LAVA_SLAB = register(new BRRPSlabBlock(LAVA_BLOCK), "lava_slab");
  public static final BRRPFenceBlock LAVA_FENCE = register(new BRRPFenceBlock(LAVA_BLOCK), "lava_fence");
  public static final BRRPFenceGateBlock LAVA_FENCE_GATE = register(new BRRPFenceGateBlock(LAVA_BLOCK), "lava_fence_gate");
  public static final BRRPWallBlock LAVA_WALL = register(new BRRPWallBlock(LAVA_BLOCK), "lava_wall");
  /**
   * <p>The block specifies the texture of top, side and bottom via parameters of {@link BRRPCubeBlock#cubeBottomTop(AbstractBlock.Settings, String, String, String)}. {@link BRRPCubeBlock#getBlockModel()} will directly use the textures.
   * <p>这个方块通过 {@link BRRPCubeBlock#cubeBottomTop(AbstractBlock.Settings, String, String, String)} 的参数指定了其顶面、侧面和底面的纹理，{@link BRRPCubeBlock#getBlockModel()} 会直接使用到这些纹理。
   */
  public static final BRRPCubeBlock SMOOTH_STONE = register(BRRPCubeBlock.cubeBottomTop(AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE), "block/smooth_stone", "block/smooth_stone_slab_side", "block/smooth_stone"), "smooth_stone");

  static {
    // Create corresponding items for the blocks.
    // Note: The items do not implement ItemResourceGenerator. Models of items are generated in their corresponding block objects.
    // 为上面的这些方块创建对应的物品。
    // 注意：这些物品并没有实现 ItemResourceGenerator。物品的模型也是直接在其对应的方块对象中生成的。
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
    final BlockItem item = new BlockItem(block, new Item.Settings().group(ItemGroup.TRANSPORTATION));
    PlatformBridge.getInstance().registerItem(Registry.BLOCK.getId(block), item);
    return item;
  }

  /**
   * <p>This method clears resources of {@link #PACK} and regenerates it.
   * <p>此方法会清除 {@link #PACK} 中的资源并重新生成。
   *
   * @param clientIncluded Whether to clear and regenerate client resources of {@link #PACK}.
   * @param serverIncluded Whether to clear and regenerate server data of {@link #PACK}.
   */
  private static RuntimeResourcePack refreshPack(boolean clientIncluded, boolean serverIncluded) {
    LOGGER.info("Generating resources!");
    // When testing, to avoid potential bugs, duplicate resources are not allowed. That means once you have added a resource, you cannot add another resource with the same identifier.
    // 测试时，为了避免潜在的问题，因此不允许重复的资源。这意味着添加了一个资源之后，你无法再添加一个相同 id 的另一个资源。
    PACK.setForbidsDuplicateResource(true);
    if (clientIncluded) {
      PACK.clearResources(ResourceType.CLIENT_RESOURCES);
      // add language files to the runtime resource pack.
      // 往运行时资源包添加语言文件。
      PACK.mergeLang(new Identifier("brrp", "en_us"), new JLang()
          .blockRespect(LAVA_BLOCK, "Lava Block (Development Environment Only)")
          .blockRespect(LAVA_STAIRS, "Lava Stairs (Development Environment Only)")
          .blockRespect(LAVA_SLAB, "Lava Slab (Development Environment Only)")
          .blockRespect(LAVA_FENCE, "Lava Fence (Development Environment Only)")
          .blockRespect(LAVA_FENCE_GATE, "Lava Fence Gate (Development Environment Only)")
          .blockRespect(LAVA_WALL, "Lava Wall (Development Environment Only)")
          .blockRespect(SMOOTH_STONE, "Smooth Stone (Development Environment Only)")
      );
      // The difference of 'mergeLang' or 'addLang' is, if the same resource pack has already had a language file with a same name, 'addLang' will override the whole existing one, while 'mergeLang' simply merges it. You may refer to the source codes.
      // mergeLang 和 addLang 的不同之处在于，如果同一个资源包已经有了相同名称的语言文件，addLang 会直接将原有的全部覆盖，而 mergeLang 是将其合并。参见其方法的源代码。
      PACK.mergeLang(new Identifier("brrp", "zh_cn"), new JLang()
          .blockRespect(LAVA_BLOCK, "熔岩方块（仅限开发环境）")
          .blockRespect(LAVA_STAIRS, "熔岩楼梯（仅限开发环境）"));
      PACK.mergeLang(new Identifier("brrp", "zh_cn"), new JLang()
          .blockRespect(LAVA_SLAB, "熔岩台阶（仅限开发环境）")
          .blockRespect(LAVA_FENCE, "熔岩栅栏（仅限开发环境）"));
      PACK.mergeLang(new Identifier("brrp", "zh_cn"), new JLang()
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
      PACK.mergeLang(new Identifier("brrp", "zh_tw"), twLang);
      PACK.mergeLang(new Identifier("brrp", "zh_hk"), twLang
          .blockRespect(LAVA_FENCE, "熔岩欄杆（僅限開發環境）")
          .blockRespect(LAVA_FENCE_GATE, "熔岩閘門（僅限開發環境）")
      );
    }

    if (serverIncluded) {
      PACK.clearResources(ResourceType.SERVER_DATA);
      // Add some tags that contain blocks and items of this mod. In this example, we just use `IdentifiedTag#write`
      // 添加一些包含本模组方块和物品的标签。在这个例子中，我们直接使用 `IdentifiedTag#write`。
      ((IdentifiedTag) new IdentifiedTag(BlockTags.FENCES, "blocks").addBlock(LAVA_FENCE)).write(PACK);
      // the sentence above is equivalent to the following one:
      // 上面的句子和下面这个等价：
      // PACK.addTag(new Identifier("minecraft", "blocks/fences"), new JTag().addBlock(LAVA_FENCE));

      ((IdentifiedTag) new IdentifiedTag(BlockTags.FENCE_GATES, "blocks").addBlock(LAVA_FENCE_GATE)).write(PACK);
      ((IdentifiedTag) new IdentifiedTag(BlockTags.WALLS, "blocks").addBlock(LAVA_WALL)).write(PACK);
      ((IdentifiedTag) new IdentifiedTag(ItemTags.FENCES, "items").addItem(LAVA_FENCE)).write(PACK);
//      ((IdentifiedTag) new IdentifiedTag(ItemTags.FENCE_GATES).addItem(LAVA_FENCE_GATE)).write(PACK);
      ((IdentifiedTag) new IdentifiedTag(ItemTags.WALLS, "items").addItem(LAVA_WALL)).write(PACK);

      // The recipe of smooth stone is not generated in `BlockResourceGenerator#writeRecipes`, and there is also no need to create a subclass for it. Therefore, we just generate the recipe here.
      // 平滑石头的配方不会在 `BlockResourceGenerator#writeRecipes` 中生成，也没有必要专门为它创建子类，所以这里直接单独生成其配方。
      PACK.addRecipeAndAdvancement(new Identifier("brrp", "smooth_stone"), "transportation", new JShapedRecipe(Blocks.SMOOTH_STONE_SLAB).resultCount(6).pattern("###").addKey("#", SMOOTH_STONE).addInventoryChangedCriterion("has_smooth_stone", SMOOTH_STONE));
      PACK.addRecipeAndAdvancement(new Identifier("brrp", "smooth_stone_from_stonecutting"), "transportation", new JStonecuttingRecipe(SMOOTH_STONE, Blocks.SMOOTH_STONE_SLAB, 2).addInventoryChangedCriterion("has_smooth_stone", SMOOTH_STONE));
    }

    // The following code will generate all resources (including block states, block models, item models, loot tables, and optional recipes with the corresponding advancements) for the blocks.
    // 以下代码会为这些方块生成所有资源（包括方块状态、方块模型、物品模型、战利品表，以及部分还有配方及对应的进度）。
    LAVA_BLOCK.writeResources(PACK, clientIncluded, serverIncluded);
    LAVA_STAIRS.writeResources(PACK, clientIncluded, serverIncluded);
    LAVA_SLAB.writeResources(PACK, clientIncluded, serverIncluded);
    LAVA_FENCE.writeResources(PACK, clientIncluded, serverIncluded);
    LAVA_FENCE_GATE.writeResources(PACK, clientIncluded, serverIncluded);
    LAVA_WALL.writeResources(PACK, clientIncluded, serverIncluded);
    SMOOTH_STONE.writeResources(PACK, clientIncluded, serverIncluded);

    return PACK;
  }


  @Contract("_,_ -> param1")
  private static <T extends Block> T register(T block, String name) {
    PlatformBridge.getInstance().registerBlock(new Identifier("brrp", name), block);
    return block;
  }

  /**
   * <p>Register the runtime resource pack in this development environment. In this example, each time Minecraft is loading the resource pack, {@link #refreshPack} will be invoked, which generate corresponding resources and returns the resource pack.
   * <p>在开发环境中注册运行时资源包。在这个例子中，每次 Minecraft 加载资源包的时候，都会调用一次 {@link #refreshPack}，这个方法会生成对应的资源并返回这个资源包。
   * <p>In non-development environment, you usually should not generate the resource pack on each loading.
   * <p>在非开发环境中，通常不应该每次加载资源时都生成资源包。
   */
  @ApiStatus.Internal
  public static void registerPacks() {
    RRPEventHelper.BEFORE_VANILLA.registerPack(resourceType -> {
      final RuntimeResourcePack runtimeResourcePack;
      switch (resourceType) {
        case CLIENT_RESOURCES:
          runtimeResourcePack = refreshPack(true, false);
          break;
        case SERVER_DATA:
          runtimeResourcePack = refreshPack(false, true);
          break;
        default:
          throw new IllegalArgumentException();
      }
      return runtimeResourcePack;
    });


    // If it is in non-development environment, you should do the followings:
    // 在非开发环境，你应该这么做：

    // RRPEventHelper.BEFORE_VANILLA.registerPack(refreshPack(true, true));

    // In this case, the `refreshPack` will be invoked once when registering the pack. And then the refreshed pack will be directly used each time Minecraft loads resources.
    // 在这个例子中，注册资源包的时候会调用一次 `refreshPack`，然后每次 Minecraft 加载资源包的时候直接使用这些资源包。
  }
}
