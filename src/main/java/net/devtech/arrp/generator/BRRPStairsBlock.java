package net.devtech.arrp.generator;

import com.google.common.base.Suppliers;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JResult;
import net.devtech.arrp.json.recipe.JShapedRecipe;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BRRPStairsBlock extends StairsBlock implements BlockResourceGenerator {
  public final Supplier<Block> baseBlockSupplier;

  private BRRPStairsBlock(Settings settings, Supplier<BlockState> stateSupplier) {
    super(stateSupplier, settings);
    this.baseBlockSupplier = () -> stateSupplier.get().getBlock();
  }

  public BRRPStairsBlock(Supplier<Block> baseBlockSupplier, Settings settings) {
    super(() -> baseBlockSupplier.get().getDefaultState(), settings);
    this.baseBlockSupplier = baseBlockSupplier;
  }

  public BRRPStairsBlock(Block baseBlock) {
    this(Suppliers.ofInstance(baseBlock), AbstractBlock.Settings.copy(baseBlock));
  }

  @Override
  public @Nullable Block getBaseBlock() {
    return baseBlockSupplier.get();
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public @NotNull JBlockStates getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return JBlockStates.delegate(BlockStateModelGenerator.createStairsBlockState(this, blockModelId.brrp_append("_inner"), blockModelId, blockModelId.brrp_append("_outer")));
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public @NotNull JModel getBlockModel() {
    return new JModel("block/stairs").textures(JTextures.ofSides(getTextureId(TextureKey.TOP),
        getTextureId(TextureKey.SIDE),
        getTextureId(TextureKey.BOTTOM)));
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final JModel blockModel = getBlockModel();
    final Identifier id = getBlockModelId();
    pack.addModel(blockModel, id);
    pack.addModel(blockModel.parent("block/inner_stairs"), id.brrp_append("_inner"));
    pack.addModel(blockModel.parent("block/outer_stairs"), id.brrp_append("_outer"));
  }

  /**
   * It slightly resembles {@link RecipesProvider#createStairsRecipe(ItemConvertible, Ingredient)}, but bypasses validation so as not to come error.
   */
  @Override
  public @Nullable JRecipe getCraftingRecipe() {
    final Block baseBlock = getBaseBlock();
    return baseBlock == null ? null :
        new JShapedRecipe(new JResult(this)
            .count(4))
            .pattern("#  ", "## ", "###")
            .addKey("#", baseBlock)
            .addInventoryChangedCriterion("has_the_ingredient", baseBlock);
  }
}
