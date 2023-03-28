package net.devtech.arrp.json.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * The <b>smelting recipe</b>s are used for furnace blocks.
 */
@SuppressWarnings("unused")
public class JSmeltingRecipe extends JCookingRecipe {
  private static final String TYPE = Registries.RECIPE_SERIALIZER.getId(RecipeSerializer.SMELTING).toString();

  /**
   * Creates the simplest smelting recipe, with the ingredient and result simply specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The identifier (as string) of the result.
   */
  public JSmeltingRecipe(JIngredient ingredient, String result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smelting recipe, with the ingredient and result simply specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The identifier of the result.
   */
  public JSmeltingRecipe(JIngredient ingredient, Identifier result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smelting recipe, with the ingredient and result specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The resulting item. You must ensure that it has been registered.
   */
  public JSmeltingRecipe(JIngredient ingredient, Item result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smelting recipe, with the ingredient and result specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The resulting item or block. You must ensure that its item has been registered.
   */
  public JSmeltingRecipe(JIngredient ingredient, ItemConvertible result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smelting recipe, with the identifiers (as string) of single ingredient (item, not item tag) and result specified.
   *
   * @param ingredient The identifier (as string) of the ingredient.
   * @param result     The identifier (as string) of the result.
   */
  public JSmeltingRecipe(String ingredient, String result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smelting recipe, with the identifiers of single ingredient (item, not item tag) and result specified.
   *
   * @param ingredient The identifier of the ingredient.
   * @param result     The identifier of the result.
   */
  public JSmeltingRecipe(Identifier ingredient, Identifier result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smelting recipe, with the identifiers of single ingredient and result specified.
   *
   * @param ingredient The ingredient item.
   * @param result     The result item.
   */
  public JSmeltingRecipe(ItemConvertible ingredient, ItemConvertible result) {
    super(TYPE, ingredient, result);
  }

  @Override
  public JSmeltingRecipe experience(final float experience) {
    return (JSmeltingRecipe) super.experience(experience);
  }

  @Override
  public JSmeltingRecipe cookingTime(final int cookingtime) {
    return (JSmeltingRecipe) super.cookingTime(cookingtime);
  }

  @Override
  public JSmeltingRecipe group(final String group) {
    return (JSmeltingRecipe) super.group(group);
  }

  @Override
  public JSmeltingRecipe recipeCategory(RecipeCategory category) {
    return (JSmeltingRecipe) super.recipeCategory(category);
  }

  @Override
  public JSmeltingRecipe category(@Nullable CookingRecipeCategory category) {
    return (JSmeltingRecipe) super.category(category);
  }

  @Override
  public JSmeltingRecipe clone() {
    return (JSmeltingRecipe) super.clone();
  }
}
