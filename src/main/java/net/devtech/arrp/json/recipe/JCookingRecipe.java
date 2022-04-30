package net.devtech.arrp.json.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Contract;

/**
 * <p>A <b>cooking recipe</b> is a recipe used in a furnace. It has several types: {@linkplain JBlastingRecipe blasting}, {@linkplain JCampfireRecipe campfire_cooking}, {@linkplain JSmeltingRecipe smelting} and {@linkplain JSmokingRecipe smoking}.</p>
 * <p>A cooking recipe is composed of one or more ingredients, and a result, as well as a time length of cooking. It can have an optional experience.</p>
 *
 * @see net.minecraft.data.server.recipe.CookingRecipeJsonBuilder
 * @see net.minecraft.recipe.CookingRecipeSerializer
 */
@SuppressWarnings("unused")
public abstract class JCookingRecipe extends JRecipe {
  /**
   * The ingredient of the cooking recipe. It can be one or multiple items or item tags.
   */
  public final JIngredient ingredient;
  /**
   * The identifier (as string) of the resulting item. Only one item can be specified without providing the amount or nbt.
   */
  public final String result;

  /**
   * The experience generated when finishing cooking.
   */
  public Float experience;
  /**
   * The time duration spent on cooking the item.
   */
  public Integer cookingtime;

  /**
   * Creates the simplest cooking recipe, with the ingredient and result simply specified.
   *
   * @param type       The type of the cooking recipe.
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The identifier (as string) of the result.
   */
  protected JCookingRecipe(final String type, final JIngredient ingredient, final String result) {
    super(type);
    this.ingredient = ingredient;
    this.result = result;
  }

  /**
   * Creates a simple cooking recipe, with the ingredient and result simply specified.
   *
   * @param type       The type of the cooking recipe.
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The identifier of the result.
   */
  protected JCookingRecipe(final String type, final JIngredient ingredient, final Identifier result) {
    this(type, ingredient, result.toString());
  }

  /**
   * Creates a simple cooking recipe, with the ingredient and result specified.
   *
   * @param type       The type of the cooking recipe.
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The resulting item. You must ensure that it has been registered.
   */
  protected JCookingRecipe(final String type, final JIngredient ingredient, final Item result) {
    this(type, ingredient, Registry.ITEM.getId(result));
  }

  /**
   * Creates a simple cooking recipe, with the ingredient and result specified.
   *
   * @param type       The type of the cooking recipe.
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The resulting item or block. You must ensure that its item has been registered.
   */
  protected JCookingRecipe(final String type, final JIngredient ingredient, final ItemConvertible result) {
    this(type, ingredient, Registry.ITEM.getId(result.asItem()));
  }

  /**
   * Creates a simple cooking recipe, with the identifiers (as string) of single ingredient (item, not item tag) and result specified.
   *
   * @param type       The type of the cooking recipe.
   * @param ingredient The identifier (as string) of the ingredient.
   * @param result     The identifier (as string) of the result.
   */
  public JCookingRecipe(final String type, String ingredient, String result) {
    this(type, JIngredient.ofItem(ingredient), result);
  }

  /**
   * Creates a simple cooking recipe, with the identifiers of single ingredient (item, not item tag) and result specified.
   *
   * @param type       The type of the cooking recipe.
   * @param ingredient The identifier of the ingredient.
   * @param result     The identifier of the result.
   */
  public JCookingRecipe(final String type, Identifier ingredient, Identifier result) {
    this(type, JIngredient.ofItem(ingredient), result.toString());
  }

  /**
   * Creates a simple cooking recipe, with the identifiers of single ingredient and result specified.
   *
   * @param type       The type of the cooking recipe.
   * @param ingredient The ingredient item.
   * @param result     The result item.
   */
  public JCookingRecipe(final String type, ItemConvertible ingredient, ItemConvertible result) {
    this(type, JIngredient.ofItem(ingredient), Registry.ITEM.getId(result.asItem()).toString());
  }

  @Deprecated
  protected JCookingRecipe(final String type, final JIngredient ingredient, final JResult result) {
    super(type);

    this.ingredient = ingredient;
    this.result = result.item;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JCookingRecipe experience(final float experience) {
    this.experience = experience;

    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JCookingRecipe cookingTime(final int cookingtime) {
    this.cookingtime = cookingtime;

    return this;
  }

  @Override
  public JCookingRecipe group(final String group) {
    return (JCookingRecipe) super.group(group);
  }

  @Override
  public JCookingRecipe clone() {
    return (JCookingRecipe) super.clone();
  }
}
