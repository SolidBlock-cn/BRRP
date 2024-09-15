package pers.solid.brrp.v1;

import com.google.common.collect.Collections2;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WoodType;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.server.recipe.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.entity.ApplyMobEffectEnchantmentEffect;
import net.minecraft.enchantment.effect.value.MultiplyEnchantmentEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.v1.api.LanguageProvider;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.generator.*;
import pers.solid.brrp.v1.impl.BRRPBlockLootTableGenerator;
import pers.solid.brrp.v1.model.ModelJsonBuilder;
import pers.solid.brrp.v1.model.ModelOverrideBuilder;
import pers.solid.brrp.v1.model.TransformationBuilder;
import pers.solid.brrp.v1.tag.IdentifiedTagBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p><strong>This class is loaded only in development environment. </strong>Therefore, it is just for testing, and should not be seen as the real part of this mod. This class also serves as a simple example of the usage of APIs of this mod. Therefore, you can read the sources of the class to have a simple understanding of the usage.</p>
 * <p>For Fabric, this class can be seen as implemented {@code ModInitializer}, and {@link #registerPacks()} is the initialization method. Because the special structure of the mod, the implementation of interfaces is not shown in this code, but is implemented via mixin.</p>
 * <p>For Forge, the class is initialized when blocks are registered, because registries are not frozen at this time.</p>
 */
@TestOnly
@ApiStatus.Internal
public class BRRPTest {
  private static final BlockSoundGroup LAVA_SOUND_GROUP = new BlockSoundGroup(1, 1, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundEvents.BLOCK_LAVA_POP, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.BLOCK_LAVA_POP, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA);
  /**
   * <p>The block, as its name indicates, is a block with a texture of lava. It is an instance of {@link BRRPCubeBlock}, which can directly specify textures and can conveniently generation all resources of it, taking the convenience of {@link BlockResourceGenerator} interface, which is implemented by {@code BRRPCubeBlock}.</p>
   * <p>Please do not think that objects that implement {@link BlockResourceGenerator} can auto generate resources. It just provides methods such as {@code writeResources} so you do not need to manually specify how to generate models, loot tables, etc. You still should invoke the methods that write resources into the runtime resource pack.</p>
   */
  public static final BRRPCubeBlock LAVA_BLOCK = register(BRRPCubeBlock.cubeAll(AbstractBlock.Settings.create().liquid().luminance(state -> 15).sounds(LAVA_SOUND_GROUP), Identifier.of("block/lava_still")), "lava_block");
  /**
   * <p>The block is the stairs of {@link #LAVA_BLOCK}. When generating models, the textures identifier will be those of the base block. The generation of the block states, and model and recipe of stairs is defined in {@link BRRPStairsBlock}.</p>
   */
  public static final BRRPStairsBlock LAVA_STAIRS = register(new BRRPStairsBlock(LAVA_BLOCK), "lava_stairs");
  public static final BRRPSlabBlock LAVA_SLAB = register(new BRRPSlabBlock(LAVA_BLOCK), "lava_slab");
  public static final BRRPFenceBlock LAVA_FENCE = register(new BRRPFenceBlock(LAVA_BLOCK), "lava_fence");
  public static final BRRPFenceGateBlock LAVA_FENCE_GATE = register(new BRRPFenceGateBlock(LAVA_BLOCK, WoodType.MANGROVE), "lava_fence_gate");
  public static final BRRPWallBlock LAVA_WALL = register(new BRRPWallBlock(LAVA_BLOCK), "lava_wall");
  /**
   * <p>The block specifies the texture of top, side and bottom via parameters of {@link BRRPCubeBlock#cubeBottomTop(AbstractBlock.Settings, Identifier, Identifier, Identifier)}. {@link BRRPCubeBlock#getBlockModel()} will directly use the textures.</p>
   */
  public static final BRRPCubeBlock SMOOTH_STONE = register(BRRPCubeBlock.cubeBottomTop(AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE), Identifier.of("block/smooth_stone"), Identifier.of("block/smooth_stone_slab_side"), Identifier.of("block/smooth_stone")), "smooth_stone");
  public static final RegistryKey<Enchantment> POWER_DIAMOND = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("brrp", "power_diamond"));
  public static final RegistryKey<Enchantment> POWER_GEM = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("brrp", "power_gem"));
  /**
   * <p>The runtime resource pack that will be used in development environment, as a simple example. The object is created in the initialization of this class.</p>
   */
  private static final RuntimeResourcePack PACK = RuntimeResourcePack.create(Identifier.of("brrp", "test"));
  private static final Logger LOGGER = LoggerFactory.getLogger(BRRPTest.class);

  static {
    Validate.validState(PlatformBridge.getInstance().isDevelopmentEnvironment(), "The class 'BRRPDevelopment' should not be loaded outside of the development environment.");
  }

  static {
    // Create corresponding items for the blocks.
    // Note: The items do not implement ItemResourceGenerator. Models of items are generated in their corresponding block objects.
    blockItem(LAVA_BLOCK);
    blockItem(LAVA_STAIRS);
    blockItem(LAVA_SLAB);
    blockItem(LAVA_FENCE);
    blockItem(LAVA_FENCE_GATE);
    blockItem(LAVA_WALL);
    blockItem(SMOOTH_STONE);
    LAVA_BLOCK.setRecipeCategory(RecipeCategory.BUILDING_BLOCKS);
    LAVA_STAIRS.setRecipeCategory(RecipeCategory.BUILDING_BLOCKS);
    LAVA_SLAB.setRecipeCategory(RecipeCategory.BUILDING_BLOCKS);
    LAVA_FENCE.setRecipeCategory(RecipeCategory.DECORATIONS);
    LAVA_FENCE_GATE.setRecipeCategory(RecipeCategory.DECORATIONS);
    LAVA_WALL.setRecipeCategory(RecipeCategory.DECORATIONS);
    SMOOTH_STONE.setRecipeCategory(RecipeCategory.BUILDING_BLOCKS);

    // add them to vanilla item groups
    PlatformBridge.getInstance().setItemGroup(Collections2.transform(List.of(LAVA_BLOCK, LAVA_STAIRS, LAVA_SLAB, LAVA_FENCE, LAVA_FENCE_GATE, LAVA_WALL, SMOOTH_STONE), ItemStack::new));
  }

  /**
   * Create and register block items for the block.
   */
  @SuppressWarnings("UnusedReturnValue")
  private static BlockItem blockItem(Block block) {
    final BlockItem item = new BlockItem(block, new Item.Settings());
    PlatformBridge.getInstance().registerItem(Registries.BLOCK.getId(block), item);
    return item;
  }

  /**
   * <p>This method clears resources of {@link #PACK} and regenerates it.</p>
   *
   * @param clientIncluded Whether to clear and regenerate client resources of {@link #PACK}.
   * @param serverIncluded Whether to clear and regenerate server data of {@link #PACK}.
   */
  private static void refreshPack(boolean clientIncluded, boolean serverIncluded) {
    LOGGER.info("Generating resources for the development environment.");
    PACK.addRootResource("test.txt", "Hello world! 你好，世界！".getBytes(StandardCharsets.UTF_8));
    if (clientIncluded) {
      PACK.clearResources(ResourceType.CLIENT_RESOURCES);
      // add language files to the runtime resource pack.
      // 往运行时资源包添加语言文件。
      PACK.addLang(Identifier.of("brrp", "en_us"), LanguageProvider.create()
          .add(LAVA_BLOCK, "Lava Block (Development Environment Only)")
          .add(LAVA_STAIRS, "Lava Stairs (Development Environment Only)")
          .add(LAVA_SLAB, "Lava Slab (Development Environment Only)")
          .add(LAVA_FENCE, "Lava Fence (Development Environment Only)")
          .add(LAVA_FENCE_GATE, "Lava Fence Gate (Development Environment Only)")
          .add(LAVA_WALL, "Lava Wall (Development Environment Only)")
          .add(SMOOTH_STONE, "Smooth Stone (Development Environment Only)")
          .add(POWER_DIAMOND, "Power diamond")
          .add(POWER_GEM, "Power gem")
      );
      // The difference of 'mergeLang' or 'addLang' is, if the same resource pack has already had a language file with a same name, 'addLang' will override the whole existing one, while 'mergeLang' simply merges it. You may refer to the source codes.
      // mergeLang 和 addLang 的不同之处在于，如果同一个资源包已经有了相同名称的语言文件，addLang 会直接将原有的全部覆盖，而 mergeLang 是将其合并。参见其方法的源代码。
      PACK.addLang(Identifier.of("brrp", "zh_cn"), LanguageProvider.create()
          .add(LAVA_BLOCK, "熔岩方块（仅限开发环境）")
          .add(LAVA_STAIRS, "熔岩楼梯（仅限开发环境）")
          .add(LAVA_SLAB, "熔岩台阶（仅限开发环境）")
          .add(LAVA_FENCE, "熔岩栅栏（仅限开发环境）")
          .add(LAVA_FENCE_GATE, "熔岩栅栏门（仅限开发环境）")
          .add(LAVA_WALL, "熔岩墙（仅限开发环境）")
          .add(SMOOTH_STONE, "平滑石头（仅限开发环境）")
          .add(POWER_DIAMOND, "强化钻石")
          .add(POWER_GEM, "强化宝石")
      );
      final LanguageProvider twLang = LanguageProvider.create()
          .add(LAVA_BLOCK, "熔岩方塊（僅限開發環境）")
          .add(LAVA_STAIRS, "熔岩階梯（僅限開發環境）")
          .add(LAVA_SLAB, "熔岩半磚（僅限開發環境）")
          .add(LAVA_FENCE, "熔岩柵欄（僅限開發環境）")
          .add(LAVA_FENCE_GATE, "熔岩柵欄門（僅限開發環境）")
          .add(LAVA_WALL, "熔岩墻（僅限開發環境）")
          .add(SMOOTH_STONE, "平滑石頭（僅限開發環境）")
          .add(POWER_DIAMOND, "強化鑽石")
          .add(POWER_GEM, "強化寶石");
      PACK.addLang(Identifier.of("brrp", "zh_tw"), twLang);
      PACK.addLang(Identifier.of("brrp", "zh_hk"), twLang
          .add(LAVA_FENCE, "熔岩欄杆（僅限開發環境）")
          .add(LAVA_FENCE_GATE, "熔岩閘門（僅限開發環境）")
      );
    }

    if (serverIncluded) {
      PACK.clearResources(ResourceType.SERVER_DATA);
      final IdentifiedTagBuilder<Block> stairs = IdentifiedTagBuilder.createBlock(BlockTags.STAIRS).add(LAVA_STAIRS);
      PACK.addTag(stairs);
      PACK.addTag(IdentifiedTagBuilder.createItemCopy(ItemTags.STAIRS, stairs));
      final IdentifiedTagBuilder<Block> slabs = IdentifiedTagBuilder.createBlock(BlockTags.SLABS).add(LAVA_SLAB);
      PACK.addTag(slabs);
      PACK.addTag(IdentifiedTagBuilder.createItemCopy(ItemTags.SLABS, slabs));
      final IdentifiedTagBuilder<Block> fences = IdentifiedTagBuilder.createBlock(BlockTags.FENCES).add(LAVA_FENCE);
      PACK.addTag(fences);
      PACK.addTag(IdentifiedTagBuilder.createItemCopy(ItemTags.FENCES, fences));
      final IdentifiedTagBuilder<Block> fenceGates = IdentifiedTagBuilder.createBlock(BlockTags.FENCE_GATES).add(LAVA_FENCE_GATE);
      PACK.addTag(fenceGates);
      PACK.addTag(IdentifiedTagBuilder.createItemCopy(ItemTags.FENCE_GATES, fenceGates));
      final IdentifiedTagBuilder<Block> walls = IdentifiedTagBuilder.createBlock(BlockTags.WALLS).add(LAVA_WALL);
      PACK.addTag(walls);
      PACK.addTag(IdentifiedTagBuilder.createItemCopy(ItemTags.WALLS, walls));
      PACK.addTag(IdentifiedTagBuilder.createBlock(BlockTags.PICKAXE_MINEABLE).add(SMOOTH_STONE));

      PACK.addRecipeAndAdvancement(Identifier.of("brrp", "smooth_stone_slab"), ShapedRecipeJsonBuilder.create(null, Blocks.SMOOTH_STONE_SLAB, 6).pattern("###").input('#', SMOOTH_STONE).criterionFromItem(SMOOTH_STONE).setCustomCraftingCategory(CraftingRecipeCategory.MISC).setCustomRecipeCategory("brrp_custom"));
      PACK.addRecipeAndAdvancement(Identifier.of("brrp", "smooth_stone_slab_from_stonecutting"), StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(SMOOTH_STONE), null, Blocks.SMOOTH_STONE_SLAB).criterionFromItem(SMOOTH_STONE).setCustomRecipeCategory("brrp_custom"));

      final ComponentChanges bedrockTestComponentChanges = ComponentChanges.builder()
          .add(DataComponentTypes.FOOD, new FoodComponent.Builder().nutrition(20).saturationModifier(20).alwaysEdible().snack().statusEffect(new StatusEffectInstance(StatusEffects.WIND_CHARGED, 60), 1).build())
          .add(DataComponentTypes.ITEM_NAME, Text.literal("Unobtainable!"))
          .build();

      PACK.addRecipeAndAdvancement(Identifier.of("brrp", "cook_bedrock"), CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.BEDROCK), null, Items.BARRIER, 1000, 2)
          .setCustomRecipeCategory("brrp_custom")
          .criterionFromItem(Items.BEDROCK)
          .setComponentChanges(bedrockTestComponentChanges));
      PACK.addAdvancement(Identifier.of("brrp", "test_advancement"), registryLookup -> Advancement.Builder.create().criterion("in_mesa_biome", TickCriterion.Conditions.createLocation(EntityPredicate.Builder.create().location(LocationPredicate.Builder.create().biome(registryLookup.getWrapperOrThrow(RegistryKeys.BIOME).getOrThrow(ConventionalBiomeTags.IS_SNOWY))))).build(Identifier.of("brrp", "test_advancement")).value());
      PACK.addRecipeAndAdvancement(Identifier.of("brrp", "unobtainable_shaped"), ShapedRecipeJsonBuilder.create(null, Items.BEDROCK).patterns("xx", "xx").input('x', Items.BEDROCK).setBypassesValidation(true).setCustomRecipeCategory("brrp_custom").setCustomCraftingCategory(CraftingRecipeCategory.MISC).setComponentChanges(bedrockTestComponentChanges));
      PACK.addRecipeAndAdvancement(Identifier.of("brrp", "unobtainable_shapeless"), ShapelessRecipeJsonBuilder.create(null, Items.BEDROCK).input(Items.STONE, 5).criterionFromItem(Items.STONE).setCustomRecipeCategory("brrp_custom").setBypassesValidation(true).setCustomCraftingCategory(CraftingRecipeCategory.MISC).setComponentChanges(bedrockTestComponentChanges));
      PACK.addRecipeAndAdvancement(Identifier.of("brrp", "unobtainable_single_item"), StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Items.STONE), null, Items.BEDROCK).setCustomRecipeCategory("brrp_custom").setBypassesValidation(true).setComponentChanges(bedrockTestComponentChanges));
      PACK.addRecipeAndAdvancement(Identifier.of("brrp", "smithing_transform"), SmithingTransformRecipeJsonBuilder.create(Ingredient.ofItems(Items.DIAMOND), Ingredient.ofItems(Items.STONE), Ingredient.ofItems(Items.LAPIS_LAZULI), null, Items.BEDROCK).setCustomRecipeCategory("brrp_custom").setBypassesValidation(true).setComponentChanges(bedrockTestComponentChanges));
      PACK.addRecipeAndAdvancement(Identifier.of("brrp", "smithing_trim"), SmithingTrimRecipeJsonBuilder.create(Ingredient.ofItems(Items.DIAMOND), Ingredient.ofItems(Items.STONE), Ingredient.ofItems(Items.REDSTONE), RecipeCategory.BUILDING_BLOCKS).setCustomRecipeCategory("brrp_custom").setBypassesValidation(true));

      PACK.addTag(new IdentifiedTagBuilder<>(RegistryKeys.BIOME, Identifier.of("brrp", "some_biomes")).add(BiomeKeys.THE_VOID, BiomeKeys.PLAINS, BiomeKeys.DESERT).addTag(BiomeTags.NETHER_FORTRESS_HAS_STRUCTURE));

      PACK.addDynamicRegistryContent(POWER_DIAMOND, Enchantment.CODEC, Enchantment.builder(Enchantment.definition(RegistryEntryList.of(Items.DIAMOND.getRegistryEntry()),
              1,
              5,
              new Enchantment.Cost(1, 5),
              new Enchantment.Cost(2, 4),
              1,
              AttributeModifierSlot.ANY))
          .addEffect(EnchantmentEffectComponentTypes.DAMAGE, new MultiplyEnchantmentEffect(EnchantmentLevelBasedValue.linear(114514)))
          .build(POWER_DIAMOND.getValue()));
      PACK.addDynamicRegistryContentFunction(POWER_GEM, Enchantment.CODEC, registryLookup -> Enchantment.builder(Enchantment.definition(
              registryLookup.getWrapperOrThrow(RegistryKeys.ITEM).getOrThrow(ConventionalItemTags.GEMS),
              1,
              8,
              Enchantment.constantCost(1),
              Enchantment.constantCost(100),
              1,
              AttributeModifierSlot.ANY
          ))
          .addEffect(EnchantmentEffectComponentTypes.TICK, new ApplyMobEffectEnchantmentEffect(RegistryEntryList.of(StatusEffects.WIND_CHARGED, StatusEffects.WEAVING, StatusEffects.INFESTED, StatusEffects.OOZING), EnchantmentLevelBasedValue.linear(2, 3),
              EnchantmentLevelBasedValue.linear(5, 7),
              EnchantmentLevelBasedValue.linear(1, 3),
              EnchantmentLevelBasedValue.linear(3, 3)))
          .build(POWER_GEM.getValue()));
    }

    // The following code will generate all resources (including block states, block models, item models, loot tables, and optional recipes with the corresponding advancements) for the blocks.
    LAVA_BLOCK.writeResources(PACK, clientIncluded, serverIncluded);
    LAVA_STAIRS.writeResources(PACK, clientIncluded, serverIncluded);
    LAVA_SLAB.writeResources(PACK, clientIncluded, serverIncluded);
    LAVA_FENCE.writeResources(PACK, clientIncluded, serverIncluded);
    LAVA_FENCE_GATE.writeResources(PACK, clientIncluded, serverIncluded);
    LAVA_WALL.writeResources(PACK, clientIncluded, serverIncluded);
    SMOOTH_STONE.writeResources(PACK, clientIncluded, serverIncluded);

    LOGGER.info("Resources generation finished for the development environment.");
  }


  @Contract("_,_ -> param1")
  private static <T extends Block> T register(T block, String name) {
    PlatformBridge.getInstance().registerBlock(Identifier.of("brrp", name), block);
    return block;
  }

  /**
   * Register the runtime resource pack in this development environment. The content of the resource pack will be generated initially. After calling {@link RuntimeResourcePack#setSidedRegenerationCallback}, you can regenerate the resources in the {@link pers.solid.brrp.v1.gui.RegenerateScreen}.
   */
  public static void registerPacks() {
    refreshPack(true, true);
    RRPEventHelper.BEFORE_VANILLA.registerPack(PACK);
    PACK.setDisplayName(Text.translatable("brrp.pack.test.name"));
    PACK.setDescription(Text.translatable("brrp.pack.test.description"));
    PACK.setSidedRegenerationCallback(ResourceType.CLIENT_RESOURCES, () -> refreshPack(true, false));
    PACK.setSidedRegenerationCallback(ResourceType.SERVER_DATA, () -> refreshPack(false, true));
    RuntimeResourcePack emptyPack = RuntimeResourcePack.create(Identifier.of("brrp", "empty"));
    emptyPack.setDisplayName(Text.translatable("brrp.pack.empty.name"));
    emptyPack.setDescription(Text.translatable("brrp.pack.empty.description"));
    RRPEventHelper.BEFORE_VANILLA.registerPack(emptyPack);

    final RuntimeResourcePack beforeUser = RuntimeResourcePack.create(Identifier.of("brrp", "test_before_user"));
    beforeUser.setDisplayName(Text.translatable("brrp.pack.test_before_user.name"));
    beforeUser.setDescription(Text.translatable("brrp.pack.test_before_user.description"));
    beforeUser.addModel(Identifier.of("minecraft", "item/yellow_wool"), ModelJsonBuilder.create(Models.HANDHELD).addTexture(TextureKey.LAYER0, Identifier.of("block/yellow_wool")));
    beforeUser.addAsyncResource(ResourceType.CLIENT_RESOURCES, Identifier.of("models/item/gold_ingot.json"), input -> {
      LOGGER.info("async resource!");
      return beforeUser.serialize(ModelJsonBuilder.create(Models.HANDHELD).addTexture(TextureKey.LAYER0, Identifier.of("item/gold_ingot")).transformation(ModelTransformationMode.GROUND, new TransformationBuilder().translation(0, 4.5f, 0).scale(9f, 9, 9f)));
    });
    beforeUser.addLazyResource(ResourceType.CLIENT_RESOURCES, Identifier.of("models/item/diamond.json"), (pack, identifier) -> {
      LOGGER.info("lazy resource!");
      return beforeUser.serialize(ModelJsonBuilder.create(Models.HANDHELD).addTexture(TextureKey.LAYER0, Identifier.of("item/diamond")).transformation(ModelTransformationMode.GROUND, new TransformationBuilder().translation(0, 4.5f, 0).scale(10.85f, 10.85f, 10.6f)));
    });
    beforeUser.addLang(Identifier.of("minecraft", "en_us"), LanguageProvider.create().add(Blocks.YELLOW_WOOL, "The model is modified by a 'before-user' runtime resource pack."));
    beforeUser.addLootTable(Blocks.YELLOW_WOOL.getLootTableKey().getValue(), lookup -> new BRRPBlockLootTableGenerator(lookup).drops(Blocks.YELLOW_WOOL, ConstantLootNumberProvider.create(3)).build());
    beforeUser.addModel(Identifier.of("minecraft", "item/bow"), ModelJsonBuilder.create(Identifier.of("minecraft", "item/white_concrete"))
        .addOverride(ModelOverrideBuilder.of(Identifier.of("item/yellow_concrete"), Identifier.of("pulling"), 1))
        .addOverride(ModelOverrideBuilder.of(Identifier.of("item/orange_concrete"), Identifier.of("pulling"), 1).addCondition(Identifier.of("pull"), 0.65f))
        .addOverride(ModelOverrideBuilder.of(Identifier.of("item/red_concrete"), Identifier.of("pulling"), 1).addCondition(Identifier.of("pull"), 0.9f)));
    beforeUser.addLootTable(Blocks.CYAN_WOOL.getLootTableKey().getValue(), registryLookup -> LootTable.builder()
        .pool(LootPool.builder()
            .with(TagEntry.builder(ItemTags.WOOL))
            .conditionally(LocationCheckLootCondition.builder(LocationPredicate.Builder.create()
                .biome(registryLookup.getWrapperOrThrow(RegistryKeys.BIOME).getOrThrow(ConventionalBiomeTags.IS_SNOWY))))
            .build())
        .build());
    RRPEventHelper.BEFORE_USER.registerPack(beforeUser);

    final RuntimeResourcePack test2 = RuntimeResourcePack.create(Identifier.of("brrp", "test2"));
    test2.setSidedRegenerationCallback(ResourceType.CLIENT_RESOURCES, () -> {
      test2.clearResources(ResourceType.CLIENT_RESOURCES);
      for (int i = 0; i < 1024; i++) {
        Thread.sleep(ThreadLocalRandom.current().nextLong(20));
        test2.addAsset(Identifier.of("brrp", "test/" + ThreadLocalRandom.current().nextInt()), new byte[16]);
      }
    });
    test2.setSidedRegenerationCallback(ResourceType.SERVER_DATA, () -> {
      test2.clearResources(ResourceType.SERVER_DATA);
      for (int i = 0; i < 1024; i++) {
        Thread.sleep(ThreadLocalRandom.current().nextLong(20));
        test2.addData(Identifier.of("brrp", "test/" + ThreadLocalRandom.current().nextInt()), new byte[16]);
      }
    });

    RRPEventHelper.BEFORE_VANILLA.registerPack(test2);
  }
}
