package net.devtech.arrp.json.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * <p>A <b>stonecutting recipe</b> is used for stonecutters. It has a simple one or multiple ingredients and a single result.</p>
 */
@SuppressWarnings("unused")
public class JStonecuttingRecipe extends JRecipe {
  private static final String TYPE = "stonecutting";
  /**
   * The ingredient to be cut. It can be single or multiple items or item tags.
   */
  public final JIngredient ingredient;
  /**
   * The identifier (as string) of the resulting item.
   */
  public final String result;
  /**
   * The count of the result. It is <i>not</i> optional.
   */
  public final int count;

  /**
   * Create a simple stonecutting recipe.
   *
   * @param ingredient The ingredient object of this recipe.
   * @param result     The identifier (as string) of the result.
   * @param count      The count of the result.
   */
  public JStonecuttingRecipe(final JIngredient ingredient, final String result, int count) {
    super(TYPE);
    this.ingredient = ingredient;
    this.result = result;
    this.count = count;
  }

  /**
   * Create a simple stonecutting recipe.
   *
   * @param ingredient The identifier (as string) of the ingredient item.
   * @param result     The identifier (as string) of the result.
   * @param count      The count of the result.
   */
  public JStonecuttingRecipe(final String ingredient, final String result, int count) {
    this(JIngredient.ofItem(ingredient), result, count);
  }

  /**
   * Create a simple stonecutting recipe.
   *
   * @param ingredient The identifier of the ingredient item.
   * @param result     The identifier of the result.
   * @param count      The count of the result.
   */
  public JStonecuttingRecipe(final Identifier ingredient, final Identifier result, int count) {
    this(JIngredient.ofItem(ingredient), result.toString(), count);
  }

  /**
   * Create a simple stonecutting recipe.
   *
   * @param ingredient The ingredient item, must be registered.
   * @param result     The result item, must be registered.
   * @param count      The count of the result.
   */
  public JStonecuttingRecipe(final Item ingredient, final Item result, int count) {
    this(JIngredient.ofItem(Registry.ITEM.getId(ingredient)), Registry.ITEM.getId(result).toString(), count);
  }

  /**
   * Create a simple stonecutting recipe.
   *
   * @param ingredient The ingredient item, must be registered.
   * @param result     The result item, must be registered.
   * @param count      The count of the result.
   */
  public JStonecuttingRecipe(final ItemConvertible ingredient, final ItemConvertible result, int count) {
    this(JIngredient.ofItem(ingredient), Registry.ITEM.getId(result.asItem()).toString(), count);
  }

  @Deprecated
  public JStonecuttingRecipe(final JIngredient ingredient, final JResult result) {
    this(ingredient, result.item, result.count);
  }

  @Deprecated
  public JStonecuttingRecipe(final JIngredient ingredient, final JStackedResult result) {
    this(ingredient, result.item, result.count);
  }

  @Override
  public JStonecuttingRecipe group(final String group) {
    return (JStonecuttingRecipe) super.group(group);
  }

  @Override
  public JStonecuttingRecipe clone() {
    return (JStonecuttingRecipe) super.clone();
  }
}
