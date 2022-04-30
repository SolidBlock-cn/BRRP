package net.devtech.arrp.json.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;

/**
 * <p>A <b>smoking</b> recipe is used for smokers.</p>
 */
@SuppressWarnings("unused")
public class JSmokingRecipe extends JCookingRecipe {
  private static final String TYPE = "smoking";

  /**
   * Creates the simplest smoking recipe, with the ingredient and result simply specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The identifier (as string) of the result.
   */
  public JSmokingRecipe(JIngredient ingredient, String result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smoking recipe, with the ingredient and result simply specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The identifier of the result.
   */
  public JSmokingRecipe(JIngredient ingredient, Identifier result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smoking recipe, with the ingredient and result specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The resulting item. You must ensure that it has been registered.
   */
  public JSmokingRecipe(JIngredient ingredient, Item result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smoking recipe, with the ingredient and result specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The resulting item or block. You must ensure that its item has been registered.
   */
  public JSmokingRecipe(JIngredient ingredient, ItemConvertible result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smoking recipe, with the identifiers (as string) of single ingredient (item, not item tag) and result specified.
   *
   * @param ingredient The identifier (as string) of the ingredient.
   * @param result     The identifier (as string) of the result.
   */
  public JSmokingRecipe(String ingredient, String result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple smoking recipe, with the identifiers of single ingredient (item, not item tag) and result specified.
   *
   * @param ingredient The identifier of the ingredient.
   * @param result     The identifier of the result.
   */
  public JSmokingRecipe(Identifier ingredient, Identifier result) {
    super(TYPE, ingredient, result);
  }


  /**
   * Creates a simple smoking recipe, with the identifiers of single ingredient and result specified.
   *
   * @param ingredient The ingredient item.
   * @param result     The result item.
   */
  public JSmokingRecipe(ItemConvertible ingredient, ItemConvertible result) {
    super(TYPE, ingredient, result);
  }

  @Deprecated
  public JSmokingRecipe(final JIngredient ingredient, final JResult result) {
    super(TYPE, ingredient, result);
  }

  @Override
  public JSmokingRecipe experience(final float experience) {
    return (JSmokingRecipe) super.experience(experience);
  }

  @Override
  public JSmokingRecipe cookingTime(final int cookingtime) {
    return (JSmokingRecipe) super.cookingTime(cookingtime);
  }

  @Override
  public JSmokingRecipe group(final String group) {
    return (JSmokingRecipe) super.group(group);
  }

  @Override
  public JSmokingRecipe clone() {
    return (JSmokingRecipe) super.clone();
  }
}
