package net.devtech.arrp.generator;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JShapedRecipe;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BRRPWallBlock extends WallBlock implements BlockResourceGenerator {
  public final @Nullable Block baseBlock;

  public BRRPWallBlock(@Nullable Block baseBlock, Settings settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  public BRRPWallBlock(@NotNull Block baseBlock) {
    this(baseBlock, AbstractBlock.Settings.copy(baseBlock));
  }

  @Nullable
  @Override
  public Block getBaseBlock() {
    return baseBlock;
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public @NotNull JBlockStates getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return JBlockStates.delegate(BlockStateModelGenerator.createWallBlockState(
        this,
        blockModelId.brrp_append("_post"),
        blockModelId.brrp_append("_side"),
        blockModelId.brrp_append("_side_tall")
    ));
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public @NotNull JModel getBlockModel() {
    return new JModel("block/template_wall_post").addTexture("wall", getTextureId(TextureKey.WALL));
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final Identifier blockModelId = getBlockModelId();
    final JModel blockModel = getBlockModel();
    pack.addModel(blockModel, blockModelId.brrp_append("_post"));
    pack.addModel(blockModel.parent("block/template_wall_side"), blockModelId.brrp_append("_side"));
    pack.addModel(blockModel.parent("block/template_wall_side_tall"), blockModelId.brrp_append("_side_tall"));
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public @NotNull JModel getItemModel() {
    return new JModel("block/wall_inventory").addTexture("wall", getTextureId(TextureKey.WALL));
  }

  /**
   * @see net.minecraft.data.server.RecipesProvider#getWallRecipe(ItemConvertible, Ingredient)
   */
  @Override
  public @Nullable JRecipe getCraftingRecipe() {
    return baseBlock == null ? null : new JShapedRecipe(this)
        .resultCount(6)
        .pattern("###", "###")
        .addKey("#", baseBlock)
        .addInventoryChangedCriterion("has_ingredient", baseBlock);
  }
}
