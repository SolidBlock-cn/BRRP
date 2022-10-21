package net.devtech.arrp.json.recipe;

import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the recipe for <b>shapeless crafting</b>. That means, in the crafting table, as long as you placed the enough ingredient, no matter what shape, it can be crafted.
 *
 * @see ShapelessRecipe
 * @see ShapelessRecipeJsonBuilder
 */
@SuppressWarnings("unused")
public class JShapelessRecipe extends JResultRecipe {
  private static final String TYPE = "minecraft:crafting_shapeless";
  /**
   * The ingredient list of this recipe.
   */
  @ApiStatus.AvailableSince("0.8.0")
  public final List<JIngredient> ingredients;

  /**
   * Create a new object with the specified result, and the list of ingredient.
   *
   * @param result      The identifier (as string) of the result item of this shapeless recipe.
   * @param ingredients The collection of the identifiers (as string) of ingredient items. In this case, it should not contain item tags.
   * @since 0.6.2 It's not advised to call this method; you may call {@link #JShapelessRecipe(String, String...)} instead, as it uses varargs.
   */
  @ApiStatus.Internal
  public JShapelessRecipe(String result, Collection<String> ingredients) {
    super(TYPE, result);
    this.ingredients = ingredients.stream().map(JIngredient::ofItem).collect(Collectors.toList());
  }

  /**
   * Create a new object with the specified result, and the list of ingredient.
   *
   * @param result      The identifier (as string) of the result item of this shapeless recipe.
   * @param ingredients The identifiers (as string) of ingredient items. In this case, it should not contain item tags.
   */
  @ApiStatus.AvailableSince("0.6.2")
  public JShapelessRecipe(String result, String... ingredients) {
    super(TYPE, result);
    this.ingredients = Arrays.stream(ingredients).map(JIngredient::ofItem).collect(Collectors.toList());
  }

  /**
   * Create a new object with the specified result, and the list of ingredient.
   *
   * @param result      The identifier (as string) of the result item of this shapeless recipe.
   * @param ingredients The collection of the identifiers of ingredient items. In this case, it should not contain item tags.
   */
  public JShapelessRecipe(Identifier result, Identifier... ingredients) {
    super(TYPE, result);
    this.ingredients = Arrays.stream(ingredients).map(JIngredient::ofItem).collect(Collectors.toList());
  }

  /**
   * Create a new object with the specified result, and the list of ingredient.
   *
   * @param result      The identifier (as string) of the result item of this shapeless recipe.
   * @param ingredients The collection of the ingredient items. In this case, it should not contain item tags.
   */
  public JShapelessRecipe(ItemConvertible result, ItemConvertible... ingredients) {
    super(TYPE, result);
    this.ingredients = Arrays.stream(ingredients).map(JIngredient::ofItem).collect(Collectors.toList());
  }

  /**
   * Create a new shapeless recipe object, with the specified result and ingredient list. The two parameters will be directly used.
   *
   * @param result      The result of the recipe.
   * @param ingredients The list of ingredients. It will be directly used as the field.
   */
  @ApiStatus.Internal
  public JShapelessRecipe(final JResult result, List<JIngredient> ingredients) {
    super(TYPE, result);
    this.ingredients = ingredients;
  }

  /**
   * @deprecated Please use {@link #JShapelessRecipe(JResult, List)}.
   */
  @Deprecated
  public JShapelessRecipe(final JResult result, final JIngredients ingredients) {
    super("minecraft:crafting_shapeless", result);
    this.ingredients = ingredients.ingredients;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JShapelessRecipe resultCount(int count) {
    return (JShapelessRecipe) super.resultCount(count);
  }

  @Override
  public JShapelessRecipe group(final String group) {
    return (JShapelessRecipe) super.group(group);
  }

  @Override
  public JShapelessRecipe clone() {
    return (JShapelessRecipe) super.clone();
  }
}
