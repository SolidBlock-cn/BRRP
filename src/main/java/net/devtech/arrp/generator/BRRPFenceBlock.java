package net.devtech.arrp.generator;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JShapedRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
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
 * This is a simple fence block which can be used for {@link BlockResourceGenerator}. You may modify its texture with {@link TextureRegistry}.
 */
public class BRRPFenceBlock extends FenceBlock implements BlockResourceGenerator {
  /**
   * The base block of the fence block.
   */
  public final @Nullable Block baseBlock;

  @Override
  public @Nullable Block getBaseBlock() {
    return baseBlock;
  }

  public BRRPFenceBlock(@Nullable Block baseBlock, Settings settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  public BRRPFenceBlock(@NotNull Block baseBlock) {
    this(baseBlock, Settings.copy(baseBlock));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability JBlockStates getBlockStates() {
    final Identifier blockModelId = getBlockModelId();
    return JBlockStates.delegate(BlockStateModelGenerator.createFenceBlockState(
        this,
        blockModelId.brrp_append("_post"),
        blockModelId.brrp_append("_side")
    ));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability JModel getBlockModel() {
    return new JModel().addTexture("texture", getTextureId(TextureKey.TEXTURE));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void writeBlockModel(RuntimeResourcePack pack) {
    final JModel blockModel = getBlockModel();
    final Identifier blockModelId = getBlockModelId();
    pack.addModel(blockModel.parent("block/fence_post"), blockModelId.brrp_append("_post"));
    pack.addModel(blockModel.parent("block/fence_side"), blockModelId.brrp_append("_side"));
  }

  @Environment(EnvType.CLIENT)
  @Override
  public @UnknownNullability JModel getItemModel() {
    return getBlockModel().parent("block/fence_inventory");
  }

  /**
   * This recipe uses the base block and stick as the ingredients.
   *
   * @see net.minecraft.data.server.RecipeProvider#createFenceRecipe(ItemConvertible, Ingredient)
   */
  @Override
  public @UnknownNullability("Null if the base block is null.") JRecipe getCraftingRecipe() {
    final Item secondIngredient = getSecondIngredient();
    return baseBlock == null || secondIngredient == null ? null :
        new JShapedRecipe(this)
            .resultCount(3)
            .pattern("W#W", "W#W")
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
