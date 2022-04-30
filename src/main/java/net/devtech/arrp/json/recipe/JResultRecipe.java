package net.devtech.arrp.json.recipe;

import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;

/**
 * A <b>result recipe</b>, as it literally means, is a recipe with a result, which is a {@link JResult} object.
 */
public abstract class JResultRecipe extends JRecipe {
  /**
   * The result of the recipe. It is final, so it should be specified when the object is created.
   */
  public final JResult result;

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
  public JResultRecipe resultCount(int count) {
    this.result.count = count;
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  @Override
  public JResultRecipe group(final String group) {
    return (JResultRecipe) super.group(group);
  }

  @Override
  public JResultRecipe clone() {
    return (JResultRecipe) super.clone();
  }
}
