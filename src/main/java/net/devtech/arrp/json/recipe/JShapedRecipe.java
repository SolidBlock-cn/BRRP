package net.devtech.arrp.json.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * <p>This is a recipe for <b>shaped crafting</b>.</p>
 *
 * @see net.minecraft.recipe.ShapedRecipe
 * @see net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
 */
@SuppressWarnings("unused")
public class JShapedRecipe extends JResultRecipe {
  private static final String TYPE = "minecraft:crafting_shaped";
  protected JPattern pattern;
  protected JKeys key;

  public JShapedRecipe(JResult result) {
    super(TYPE, result);
  }

  public JShapedRecipe(String result) {
    super(TYPE, result);
  }

  public JShapedRecipe(Identifier result) {
    super(TYPE, result);
  }

  public JShapedRecipe(Item result) {
    super(TYPE, result);
  }

  public JShapedRecipe(ItemConvertible result) {
    super(TYPE, result);
  }

  public JShapedRecipe(JResult result, JPattern pattern, JKeys keys) {
    super(TYPE, result);
    this.pattern = pattern;
    this.key = keys;
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JShapedRecipe pattern(JPattern pattern) {
    this.pattern = pattern;
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JShapedRecipe pattern(String... pattern) {
    this.pattern = new JPattern(pattern);
    return this;
  }

  @Contract(value = "_,_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JShapedRecipe addKey(String key, JIngredient value) {
    if (this.key == null) {
      this.key = new JKeys();
    }
    this.key.key(key, value);
    return this;
  }

  @Contract(value = "_,_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JShapedRecipe addKey(String key, String value) {
    return this.addKey(key, JIngredient.ofItem(value));
  }

  @Contract(value = "_,_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JShapedRecipe addKey(String key, Identifier value) {
    return this.addKey(key, JIngredient.ofItem(value));
  }

  @Contract(value = "_,_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JShapedRecipe addKey(String key, Item value) {
    return this.addKey(key, JIngredient.ofItem(Registries.ITEM.getId(value)));
  }

  @Contract(value = "_,_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JShapedRecipe addKey(String key, ItemConvertible value) {
    return this.addKey(key, JIngredient.ofItem(value));
  }

  @Override
  public JShapedRecipe resultCount(int count) {
    return (JShapedRecipe) super.resultCount(count);
  }

  @Override
  public JShapedRecipe group(final String group) {
    return (JShapedRecipe) super.group(group);
  }

  @Override
  public JShapedRecipe recipeCategory(@Nullable RecipeCategory category) {
    return (JShapedRecipe) super.recipeCategory(category);
  }

  @Override
  public JShapedRecipe category(@Nullable CraftingRecipeCategory category) {
    return (JShapedRecipe) super.category(category);
  }

  @Override
  public JShapedRecipe clone() {
    return (JShapedRecipe) super.clone();
  }
}
