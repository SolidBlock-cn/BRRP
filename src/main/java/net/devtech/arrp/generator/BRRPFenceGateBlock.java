package net.devtech.arrp.generator;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JShapedRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * The fence gate with can be user for data generation. By default, a fence gate has four block models: common model, open-gate model, in-wall model, and in-wall open-gate model.
 */
public class BRRPFenceGateBlock extends FenceGateBlock implements BlockResourceGenerator {
  public final @Nullable Block baseBlock;

  public BRRPFenceGateBlock(@Nullable Block baseBlock, Settings settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  public BRRPFenceGateBlock(@NotNull Block baseBlock) {
    this(baseBlock, Settings.copy(baseBlock));
  }

  @Override
  public @Nullable Block getBaseBlock() {
    return baseBlock;
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability JBlockStates getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return JBlockStates.delegate(BlockStateModelGenerator.createFenceGateBlockState(
        this,
        blockModelId.brrp_append("_open"),
        blockModelId,
        blockModelId.brrp_append("_wall_open"),
        blockModelId.brrp_append("_wall")
    ));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability JModel getBlockModel() {
    return new JModel("block/template_fence_gate").addTexture("texture", getTextureId(TextureKey.TEXTURE));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final JModel blockModel = getBlockModel();
    final Identifier blockModelId = getBlockModelId();
    pack.addModel(blockModel, blockModelId);
    pack.addModel(blockModel.parent("block/template_fence_gate_open"), blockModelId.brrp_append("_open"));
    pack.addModel(blockModel.parent("block/template_fence_gate_wall"), blockModelId.brrp_append("_wall"));
    pack.addModel(blockModel.parent("block/template_fence_gate_wall_open"), blockModelId.brrp_append("_wall_open"));
  }

  /**
   * This recipe uses the base block and stick as the ingredients.
   *
   * @see net.minecraft.data.server.RecipeProvider#createFenceGateRecipe(ItemConvertible, Ingredient)
   */
  @Override
  public @UnknownNullability("It will be null if the base block is null.") JRecipe getCraftingRecipe() {
    final Item secondIngredient = getSecondIngredient();
    return baseBlock == null || secondIngredient == null ? null :
        new JShapedRecipe(this)
            .pattern("#W#", "#W#")
            .addKey("W", baseBlock)
            .addKey("#", secondIngredient)
            // The second ingredient does not matter for recipe.
            // Therefore, the recipe is unlocked when you obtain the base block, instead of the second ingredient.
            .addInventoryChangedCriterion("has_ingredient", baseBlock);
  }

  /**
   * The second ingredient used in the crafting recipe. It's by default a stick. In {@link #getCraftingRecipe()}, the crafting recipe is composed of 6 base blocks and 2 second ingredients.
   *
   * @return The second ingredient to craft.
   */
  public @Nullable Item getSecondIngredient() {
    return Items.STICK;
  }
}
