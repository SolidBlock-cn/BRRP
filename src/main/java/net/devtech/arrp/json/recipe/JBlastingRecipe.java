package net.devtech.arrp.json.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * A <b>blasting recipe</b> is a recipe for blast furnace.
 *
 * @see net.minecraft.recipe.BlastingRecipe
 */
@SuppressWarnings("unused")
public class JBlastingRecipe extends JCookingRecipe {

  private static final String TYPE = Registries.RECIPE_SERIALIZER.getId(RecipeSerializer.BLASTING).toString();

  public JBlastingRecipe(JIngredient ingredient, String result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple blasting recipe, with the ingredient and result simply specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The identifier of the result.
   */
  public JBlastingRecipe(JIngredient ingredient, Identifier result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple blasting recipe, with the ingredient and result specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The resulting item. You must ensure that it has been registered.
   */
  public JBlastingRecipe(JIngredient ingredient, Item result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple blasting recipe, with the ingredient and result specified.
   *
   * @param ingredient The ingredient, which can be one or more items or item tags.
   * @param result     The resulting item or block. You must ensure that its item has been registered.
   */
  public JBlastingRecipe(JIngredient ingredient, ItemConvertible result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple blasting recipe, with the identifiers (as string) of single ingredient (item, not item tag) and result specified.
   *
   * @param ingredient The identifier (as string) of the ingredient.
   * @param result     The identifier (as string) of the result.
   */
  public JBlastingRecipe(String ingredient, String result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple blasting recipe, with the identifiers of single ingredient (item, not item tag) and result specified.
   *
   * @param ingredient The identifier of the ingredient.
   * @param result     The identifier of the result.
   */
  public JBlastingRecipe(Identifier ingredient, Identifier result) {
    super(TYPE, ingredient, result);
  }

  /**
   * Creates a simple blasting recipe, with the identifiers of single ingredient and result specified.
   *
   * @param ingredient The ingredient item.
   * @param result     The result item.
   */
  public JBlastingRecipe(ItemConvertible ingredient, ItemConvertible result) {
    super(TYPE, ingredient, result);
  }

  @CanIgnoreReturnValue
  @Override
  public JBlastingRecipe experience(final float experience) {
    return (JBlastingRecipe) super.experience(experience);
  }

  @CanIgnoreReturnValue
  @Override
  public JBlastingRecipe cookingTime(final int cookingtime) {
    return (JBlastingRecipe) super.cookingTime(cookingtime);
  }

  @CanIgnoreReturnValue
  @Override
  public JBlastingRecipe group(final String group) {
    return (JBlastingRecipe) super.group(group);
  }

  @CanIgnoreReturnValue
  @Override
  public JBlastingRecipe recipeCategory(RecipeCategory category) {
    return (JBlastingRecipe) super.recipeCategory(category);
  }

  @Override
  public JBlastingRecipe category(@Nullable CookingRecipeCategory category) {
    return (JBlastingRecipe) super.category(category);
  }

  @Override
  public JBlastingRecipe clone() {
    return (JBlastingRecipe) super.clone();
  }
}
