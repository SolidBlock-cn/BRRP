package pers.solid.brrp.v1.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;
import pers.solid.brrp.v1.model.ModelUtils;

/**
 * This is a simple fence block which can be used for {@link BlockResourceGenerator}. You may modify its texture with {@link TextureRegistry}.
 */
public class BRRPFenceBlock extends FenceBlock implements BlockResourceGenerator {
  /**
   * The base block of the fence block.
   */
  public final @Nullable Block baseBlock;

  public BRRPFenceBlock(@Nullable Block baseBlock, Settings settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  public BRRPFenceBlock(@NotNull Block baseBlock) {
    this(baseBlock, Settings.copy(baseBlock));
  }

  @Override
  public @Nullable Block getBaseBlock() {
    return baseBlock;
  }

  @Environment(EnvType.CLIENT)
  @Override
  public BlockStateSupplier getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return BlockStateModelGenerator.createFenceBlockState(
        this,
        blockModelId.brrp_suffixed("_post"),
        blockModelId.brrp_suffixed("_side")
    );
  }

  @Environment(EnvType.CLIENT)
  @Override
  public ModelJsonBuilder getBlockModel() {
    return ModelUtils.createModelWithVariants(this, Models.FENCE_POST);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final @NotNull ModelJsonBuilder blockModel = getBlockModel();
    final Identifier blockModelId = getBlockModelId();
    ModelUtils.writeModelsWithVariants(pack, blockModelId, blockModel, Models.FENCE_POST, Models.FENCE_SIDE);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public ModelJsonBuilder getItemModel() {
    return getBlockModel().parent(Models.FENCE_INVENTORY);
  }

  /**
   * This recipe uses the base block and stick as the ingredients.
   *
   * @see net.minecraft.data.server.recipe.RecipeProvider#createFenceRecipe(ItemConvertible, Ingredient)
   */
  @Override
  public CraftingRecipeJsonBuilder getCraftingRecipe() {
    final Item secondIngredient = getSecondIngredient();
    return baseBlock == null || secondIngredient == null ? null : ShapedRecipeJsonBuilder.create(getRecipeCategory(), this, 3)
        .pattern("W#W").pattern("W#W")
        .input('W', baseBlock)
        .input('#', secondIngredient)
        .criterion(RecipeProvider.hasItem(baseBlock), RecipeProvider.conditionsFromItem(baseBlock));
  }

  /**
   * The second ingredient used in the crafting recipe. It's by default a stick. In {@link #getCraftingRecipe()}, the crafting recipe is composed of 6 base blocks and 2 second ingredients.
   *
   * @return The second ingredient to craft.
   */
  public @Nullable Item getSecondIngredient() {
    return Items.STICK;
  }

  @Override
  public RecipeCategory getRecipeCategory() {
    return ITEM_TO_RECIPE_CATEGORY.getOrDefault(asItem(), RecipeCategory.DECORATIONS);
  }
}
