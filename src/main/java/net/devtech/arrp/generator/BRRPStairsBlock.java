package net.devtech.arrp.generator;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JResult;
import net.devtech.arrp.json.recipe.JShapedRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class BRRPStairsBlock extends StairsBlock implements BlockResourceGenerator {
  public final @NotNull Block baseBlock;

  /**
   * @since 0.8.0 public. However, it is still not recommended.
   */
  public BRRPStairsBlock(BlockState baseBlockState, Settings settings) {
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
  public @UnknownNullability JBlockStates getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return JBlockStates.delegate(BlockStateModelGenerator.createStairsBlockState(this, blockModelId.brrp_append("_inner"), blockModelId, blockModelId.brrp_append("_outer")));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability JModel getBlockModel() {
    return new JModel("block/stairs").textures(JTextures.ofSides(getTextureId(TextureKey.TOP),
        getTextureId(TextureKey.SIDE),
        getTextureId(TextureKey.BOTTOM)));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final JModel blockModel = getBlockModel();
    final Identifier id = getBlockModelId();
    pack.addModel(blockModel, id);
    pack.addModel(blockModel.parent("block/inner_stairs"), id.brrp_append("_inner"));
    pack.addModel(blockModel.parent("block/outer_stairs"), id.brrp_append("_outer"));
  }

  /**
   * It slightly resembles {@link net.minecraft.data.server.recipe.RecipeProvider#createStairsRecipe(ItemConvertible, Ingredient)}, but bypasses validation so as not to come error.
   */
  @Override
  public @UnknownNullability JRecipe getCraftingRecipe() {
    return new JShapedRecipe(new JResult(this)
        .count(4))
        .recipeCategory(getRecipeCategory())
        .pattern("#  ", "## ", "###")
        .addKey("#", baseBlock)
        .addInventoryChangedCriterion("has_the_ingredient", baseBlock);
  }
}
