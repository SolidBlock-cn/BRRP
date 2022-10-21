package net.devtech.arrp.generator;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.loot.JLootTable;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JShapedRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * This is a simple extension of {@link SlabBlock} with the resource generation provided.
 */
public class BRRPSlabBlock extends SlabBlock implements BlockResourceGenerator {
  /**
   * The base block will be used to generate some files. It can be null.<br>
   * When the base block is null, the double-slab creates and uses the model or "slab_double" model, instead of directly using the model of base block.
   */
  public final @Nullable Block baseBlock;

  @Override
  public @Nullable Block getBaseBlock() {
    return baseBlock;
  }

  /**
   * Simply creates an instance with a given base block. The block settings of the base block will be used, so you do not need to provide it.
   */
  public BRRPSlabBlock(@NotNull Block baseBlock) {
    this(baseBlock, FabricBlockSettings.copyOf(baseBlock));
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

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability JBlockStates getBlockStates() {
    final Identifier id = getBlockModelId();
    return JBlockStates.simpleSlab(baseBlock != null ? ResourceGeneratorHelper.getBlockModelId(baseBlock) : id.brrp_append("_double"), id, id.brrp_append("_top"));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability JModel getBlockModel() {
    return new JModel("block/slab").textures(JTextures.ofSides(
        getTextureId(TextureKey.TOP),
        getTextureId(TextureKey.SIDE),
        getTextureId(TextureKey.BOTTOM)
    ));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final JModel model = getBlockModel();
    final Identifier id = getBlockModelId();
    pack.addModel(model, id);
    pack.addModel(model.clone().parent("block/slab_top"), id.brrp_append("_top"));
    if (baseBlock == null) {
      pack.addModel(model.clone().parent("block/cube_bottom_top"), id.brrp_append("_double"));
    }
  }

  @Override
  public JLootTable getLootTable() {
    return JLootTable.delegate(BlockLootTableGenerator.slabDrops(this));
  }

  /**
   * It slightly resembles , but bypasses validation.
   */
  @Override
  public @UnknownNullability("Null if the base block is null.") JRecipe getCraftingRecipe() {
    return baseBlock == null ? null : new JShapedRecipe(this)
        .resultCount(6)
        .pattern("###")
        .addKey("#", baseBlock)
        .addInventoryChangedCriterion("has_ingredient", baseBlock);
  }
}
