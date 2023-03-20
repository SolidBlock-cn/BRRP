package pers.solid.brrp.v1.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;
import pers.solid.brrp.v1.model.ModelUtils;

/**
 * A stairs block that implements {@link BlockResourceGenerator} so that you can conveniently generate resources. By default, a stairs block has four block models: common model, inner model, and an outer model. Each stairs block should be specified a base block, which cannot be {@code null}.
 */
public class BRRPStairsBlock extends StairsBlock implements BlockResourceGenerator {
  public final @NotNull Block baseBlock;

  /**
   * It is not recommended, but it what vanilla Minecraft uses.
   */
  public BRRPStairsBlock(@NotNull BlockState baseBlockState, Settings settings) {
    super(baseBlockState, settings);
    this.baseBlock = baseBlockState.getBlock();
  }

  public BRRPStairsBlock(@NotNull Block baseBlock, Settings settings) {
    super(baseBlock.getDefaultState(), settings);
    this.baseBlock = baseBlock;
  }

  public BRRPStairsBlock(Block baseBlock) {
    this(baseBlock, Settings.copy(baseBlock));
  }

  @Override
  public @NotNull Block getBaseBlock() {
    return baseBlock;
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability BlockStateSupplier getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return BlockStateModelGenerator.createStairsBlockState(this, ModelUtils.appendVariant(blockModelId, Models.INNER_STAIRS), blockModelId, ModelUtils.appendVariant(blockModelId, Models.OUTER_STAIRS));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability ModelJsonBuilder getBlockModel() {
    return ModelUtils.createModelWithVariants(this, Models.STAIRS);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final ModelJsonBuilder blockModel = getBlockModel();
    final Identifier id = getBlockModelId();
    ModelUtils.writeModelsWithVariants(pack, id, blockModel, Models.STAIRS, Models.INNER_STAIRS, Models.OUTER_STAIRS);
  }

  @Override
  public @Nullable CraftingRecipeJsonBuilder getCraftingRecipe() {
    return RecipeProvider.createStairsRecipe(this, Ingredient.ofItems(baseBlock)).criterion(RecipeProvider.hasItem(baseBlock), RecipeProvider.conditionsFromItem(baseBlock));
  }
}
