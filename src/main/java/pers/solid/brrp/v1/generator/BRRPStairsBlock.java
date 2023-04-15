package pers.solid.brrp.v1.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;
import pers.solid.brrp.v1.model.ModelUtils;
import pers.solid.brrp.v1.util.RecipeJsonFactory;

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
  public BlockStateSupplier getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return BlockStateModelGenerator.createStairsBlockState(this, ModelUtils.appendVariant(blockModelId, Models.INNER_STAIRS), blockModelId, ModelUtils.appendVariant(blockModelId, Models.OUTER_STAIRS));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public ModelJsonBuilder getBlockModel() {
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
  public RecipeJsonFactory getCraftingRecipe() {
    return ShapedRecipeJsonFactory.create(this, 4)
        .input('#', baseBlock)
        .pattern("#  ").pattern("## ").pattern("###")
        .criterion("has_" + Registry.ITEM.getId(baseBlock.asItem()).getPath(), RecipesProvider.conditionsFromItem(baseBlock))::offerTo;
  }
}
