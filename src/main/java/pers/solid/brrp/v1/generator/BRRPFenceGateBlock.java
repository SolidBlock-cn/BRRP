package pers.solid.brrp.v1.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.FenceGateBlock;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;
import pers.solid.brrp.v1.model.ModelUtils;

/**
 * The fence gate which can be used for data generation. By default, a fence gate has four block models: common model, open-gate model, in-wall model, and in-wall open-gate model.
 */
public class BRRPFenceGateBlock extends FenceGateBlock implements BlockResourceGenerator {
  public final @Nullable Block baseBlock;

  public BRRPFenceGateBlock(@Nullable Block baseBlock, Settings settings, SoundEvent closeSound, SoundEvent openSound) {
    super(settings, closeSound, openSound);
    this.baseBlock = baseBlock;
  }

  public BRRPFenceGateBlock(@NotNull Block baseBlock, SoundEvent closeSound, SoundEvent openSound) {
    this(baseBlock, Settings.copy(baseBlock), closeSound, openSound);
  }

  @Override
  public @Nullable Block getBaseBlock() {
    return baseBlock;
  }

  @Environment(EnvType.CLIENT)
  @Override
  public BlockStateSupplier getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return BlockStateModelGenerator.createFenceGateBlockState(
        this,
        ModelUtils.appendVariant(blockModelId, Models.TEMPLATE_FENCE_GATE_OPEN),
        ModelUtils.appendVariant(blockModelId, Models.TEMPLATE_FENCE_GATE),
        ModelUtils.appendVariant(blockModelId, Models.TEMPLATE_FENCE_GATE_WALL_OPEN),
        ModelUtils.appendVariant(blockModelId, Models.TEMPLATE_FENCE_GATE_WALL),
        true
    );
  }

  @Environment(EnvType.CLIENT)
  @Override
  public ModelJsonBuilder getBlockModel() {
    return ModelUtils.createModelWithVariants(this, Models.TEMPLATE_FENCE_GATE);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final @UnknownNullability ModelJsonBuilder blockModel = getBlockModel();
    final Identifier blockModelId = getBlockModelId();
    ModelUtils.writeModelsWithVariants(pack, blockModelId, blockModel, Models.TEMPLATE_FENCE_GATE, Models.TEMPLATE_FENCE_GATE_OPEN, Models.TEMPLATE_FENCE_GATE_WALL, Models.TEMPLATE_FENCE_GATE_WALL_OPEN);
  }

  /**
   * This recipe uses the base block and stick as the ingredients.
   *
   * @see RecipeProvider#createFenceGateRecipe(ItemConvertible, Ingredient)
   */
  @Override
  public CraftingRecipeJsonBuilder getCraftingRecipe() {
    final Item secondIngredient = getSecondIngredient();
    return baseBlock == null || secondIngredient == null ? null : ShapedRecipeJsonBuilder.create(getRecipeCategory(), this)
        .input('#', secondIngredient)
        .input('W', baseBlock)
        .pattern("#W#")
        .pattern("#W#")
        .criterion(RecipeProvider.hasItem(baseBlock), RecipeProvider.conditionsFromItem(baseBlock));
  }

  @Override
  public RecipeCategory getRecipeCategory() {
    return ITEM_TO_RECIPE_CATEGORY.getOrDefault(asItem(), RecipeCategory.REDSTONE);
  }

  /**
   * The second ingredient used in the crafting recipe. It's by default a stick. In {@link #getCraftingRecipe()}, the crafting recipe is composed of 6 base blocks and 2 second ingredients. <em>If it returns null, the crafting recipe will not be generated, unless you override {@link #getCraftingRecipe()}.</em>
   *
   * @return The second ingredient to craft.
   */
  public @Nullable Item getSecondIngredient() {
    return Items.STICK;
  }
}
