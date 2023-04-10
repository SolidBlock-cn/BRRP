package pers.solid.brrp.v1.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallBlock;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;
import pers.solid.brrp.v1.model.ModelUtils;
import pers.solid.brrp.v1.util.RecipeJsonFactory;

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
   *
   */
  @Override
  public @Nullable RecipeJsonFactory getCraftingRecipe() {
    return baseBlock == null ? null : ShapedRecipeJsonFactory.create(this, 6).input('#', baseBlock).pattern("###").pattern("###").criterion("has_" + Registry.ITEM.getId(baseBlock.asItem()).getPath(), RecipesProvider.conditionsFromItem(Blocks.BRICKS))::offerTo;
  }
}
