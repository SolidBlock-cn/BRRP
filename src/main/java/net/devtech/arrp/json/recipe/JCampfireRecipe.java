package net.devtech.arrp.json.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * <p>A <b>campfire recipe</b> is used for a campfire block.</p>
 */
@SuppressWarnings("unused")
public class JCampfireRecipe extends JCookingRecipe {
  private static final String TYPE = "campfire_cooking";

  /**
   * Creates the simplest campfire recipe, with the ingredient and result simply specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The identifier (as string) of the result.
   */
  public JCampfireRecipe(JIngredient ingredient, String result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple campfire recipe, with the ingredient and result simply specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The identifier of the result.
   */
  public JCampfireRecipe(JIngredient ingredient, Identifier result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple campfire recipe, with the ingredient and result specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The resulting item. You must ensure that it has been registered.
   */
  public JCampfireRecipe(JIngredient ingredient, Item result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple campfire recipe, with the ingredient and result specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The resulting item or block. You must ensure that its item has been registered.
   */
  public JCampfireRecipe(JIngredient ingredient, ItemConvertible result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple campfire recipe, with the identifiers (as string) of single ingredient (item, not item tag) and result specified.
   *
   * @param ingredient The identifier (as string) of the ingredient.
   * @param result     The identifier (as string) of the result.
   */
  public JCampfireRecipe(String ingredient, String result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple campfire recipe, with the identifiers of single ingredient (item, not item tag) and result specified.
   *
   * @param ingredient The identifier of the ingredient.
   * @param result     The identifier of the result.
   */
  public JCampfireRecipe(Identifier ingredient, Identifier result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple campfire recipe, with the identifiers of single ingredient and result specified.
   *
   * @param ingredient The ingredient item.
   * @param result     The result item.
   */
  public JCampfireRecipe(ItemConvertible ingredient, ItemConvertible result) {
    super(TYPE, ingredient, result);
  }

  @Override
  public JCampfireRecipe experience(final float experience) {
    return (JCampfireRecipe) super.experience(experience);
  }

  @Override
  public JCampfireRecipe cookingTime(final int cookingtime) {
    return (JCampfireRecipe) super.cookingTime(cookingtime);
  }

  @Override
  public JCampfireRecipe group(final String group) {
    return (JCampfireRecipe) super.group(group);
  }

  @Override
  public JCampfireRecipe recipeCategory(RecipeCategory category) {
    return (JCampfireRecipe) super.recipeCategory(category);
  }

  @Override
  public JCampfireRecipe category(@Nullable CookingRecipeCategory category) {
    return (JCampfireRecipe) super.category(category);
  }

  @Override
  public JCampfireRecipe clone() {
    return (JCampfireRecipe) super.clone();
  }
}
