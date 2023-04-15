package pers.solid.brrp.v1.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.loottable.VanillaBlockLootTableGenerator;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.BRRPUtils;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;
import pers.solid.brrp.v1.model.ModelUtils;

/**
 * This is a simple extension of {@link SlabBlock} with the resource generation provided.
 */
public class BRRPSlabBlock extends SlabBlock implements BlockResourceGenerator {
  /**
   * The base block will be used to generate some files. It can be null.<p>
   * When the base block is null, the double-slab creates and uses the model or "slab_double" model, instead of directly using the model of base block.
   */
  public final @Nullable Block baseBlock;

  /**
   * Simply creates an instance with a given base block. The block settings of the base block will be used, so you do not need to provide it.
   */
  public BRRPSlabBlock(@NotNull Block baseBlock) {
    this(baseBlock, Settings.copy(baseBlock));
  }

  public BRRPSlabBlock(@Nullable Block baseBlock, Settings settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  /**
   * Directly creates an instance without giving the base block.
   */
  public BRRPSlabBlock(Settings settings) {
    this(null, settings);
  }

  @Override
  public @Nullable Block getBaseBlock() {
    return baseBlock;
  }

  @Environment(EnvType.CLIENT)
  @Override
  public BlockStateSupplier getBlockStates() {
    final Identifier id = getBlockModelId();
    final Identifier baseBlockModelId = baseBlock != null ? BRRPUtils.getBlockModelId(baseBlock) : id.brrp_suffixed("_double");
    final Identifier topSlabModelId = id.brrp_suffixed("_top");
    return BlockStateModelGenerator.createSlabBlockState(this, id, topSlabModelId, baseBlockModelId);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public ModelJsonBuilder getBlockModel() {
    return ModelUtils.createModelWithVariants(this, Models.SLAB);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final ModelJsonBuilder model = getBlockModel();
    final Identifier id = getBlockModelId();
    ModelUtils.writeModelsWithVariants(pack, id, model, Models.SLAB, Models.SLAB_TOP);
    if (baseBlock == null) {
      pack.addModel(id.brrp_suffixed("_double"), model.withParent(Models.CUBE_BOTTOM_TOP));
    }
  }

  @Override
  public LootTable.Builder getLootTable() {
    return new VanillaBlockLootTableGenerator().slabDrops(this);
  }

  @Override
  public CraftingRecipeJsonBuilder getCraftingRecipe() {
    return baseBlock == null ? null : RecipeProvider.createSlabRecipe(getRecipeCategory(), this, Ingredient.ofItems(baseBlock)).criterion(RecipeProvider.hasItem(baseBlock), RecipeProvider.conditionsFromItem(baseBlock));
  }
}
