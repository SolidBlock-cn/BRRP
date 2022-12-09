package net.devtech.arrp.json.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * A <b>result recipe</b>, as it literally means, is a recipe with a result, which is a {@link JResult} object.
 */
public abstract class JResultRecipe extends JRecipe {
  /**
   * The result of the recipe. It is final, so it should be specified when the object is created.
   */
  public final JResult result;
  /**
   * The category in the recipe book. It exists only for shapeless and shaped crafting recipes.
   */
  public @Nullable CraftingRecipeCategory category;

  /**
   * Create a new recipe with the specified type and result.
   *
   * @param type   The identifier of the type of the recipe, as string.
   * @param result The result of the recipe.
   */
  protected JResultRecipe(final String type, final JResult result) {
    super(type);
    this.result = result;
  }

  /**
   * Create a new recipe with the specified type and result.
   *
   * @param type   The identifier of the type of the recipe, as string.
   * @param result The identifier (as string) of the result item.
   */
  protected JResultRecipe(String type, String result) {
    this(type, new JResult(result));
  }

  /**
   * Create a new recipe with the specified type and result.
   *
   * @param type   The identifier of the type of the recipe, as string.
   * @param result The identifier of the result item.
   */
  protected JResultRecipe(String type, Identifier result) {
    this(type, new JResult(result));
  }

  /**
   * Create a new recipe with the specified type and result.
   *
   * @param type   The identifier of the type of the recipe, as string.
   * @param result The simple the result item.
   */
  protected JResultRecipe(String type, ItemConvertible result) {
    this(type, new JResult(result));
  }

  /**
   * Set the count of the result item.
   */
  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JResultRecipe resultCount(int count) {
    this.result.count = count;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @Override
  public JResultRecipe group(final String group) {
    return (JResultRecipe) super.group(group);
  }

  /**
   * Set the recipe category as well as the crafting category for this recipe.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @Override
  public JResultRecipe recipeCategory(@Nullable RecipeCategory category) {
    this.category = category == null ? null : switch (category) {
      case BUILDING_BLOCKS -> CraftingRecipeCategory.BUILDING;
      case TOOLS, COMBAT -> CraftingRecipeCategory.EQUIPMENT;
      case REDSTONE -> CraftingRecipeCategory.REDSTONE;
      default -> CraftingRecipeCategory.MISC;
    };
    return (JResultRecipe) super.recipeCategory(category);
  }

  /**
   * Set the crafting category of this recipe, which will be used in the display of recipe books.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JRecipe category(@Nullable CraftingRecipeCategory category) {
    this.category = category;
    return this;
  }

  @Override
  public JResultRecipe clone() {
    return (JResultRecipe) super.clone();
  }
}
