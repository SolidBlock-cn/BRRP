package pers.solid.brrp.v1.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;
import pers.solid.brrp.v1.model.ModelUtils;

/**
 * A wall block by default has three block models. You can specify its texture (if different from base block) through {@link TextureRegistry}.
 */
public class BRRPWallBlock extends WallBlock implements BlockResourceGenerator {
  public final @Nullable Block baseBlock;

  public BRRPWallBlock(@Nullable Block baseBlock, Settings settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  public BRRPWallBlock(@NotNull Block baseBlock) {
    this(baseBlock, Settings.copy(baseBlock));
  }

  @Nullable
  @Override
  public Block getBaseBlock() {
    return baseBlock;
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability BlockStateSupplier getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return BlockStateModelGenerator.createWallBlockState(
        this,
        ModelUtils.appendVariant(blockModelId, Models.TEMPLATE_WALL_POST),
        ModelUtils.appendVariant(blockModelId, Models.TEMPLATE_WALL_SIDE),
        ModelUtils.appendVariant(blockModelId, Models.TEMPLATE_WALL_SIDE_TALL)
    );
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability ModelJsonBuilder getBlockModel() {
    return ModelUtils.createModelWithVariants(this, Models.TEMPLATE_WALL_POST);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final Identifier blockModelId = getBlockModelId();
    final @UnknownNullability ModelJsonBuilder blockModel = getBlockModel();
    ModelUtils.writeModelsWithVariants(pack, blockModelId, blockModel, Models.TEMPLATE_WALL_POST, Models.TEMPLATE_WALL_SIDE, Models.TEMPLATE_WALL_SIDE_TALL);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability ModelJsonBuilder getItemModel() {
    return ModelUtils.createModelWithVariants(this, Models.WALL_INVENTORY);
  }

  /**
   * @see net.minecraft.data.server.RecipeProvider#getWallRecipe(ItemConvertible, Ingredient)
   */
  @Override
  public @Nullable CraftingRecipeJsonBuilder getCraftingRecipe() {
    return baseBlock == null ? null : RecipeProvider.getWallRecipe(this, Ingredient.ofItems(baseBlock)).criterion(RecipeProvider.hasItem(baseBlock), RecipeProvider.conditionsFromItem(baseBlock));
  }
}
