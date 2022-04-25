package net.devtech.arrp.json.recipe;

import com.google.gson.annotations.SerializedName;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the recipe for <b>shapeless crafting</b>.
 *
 * @see ShapelessRecipe
 * @see net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory
 */
public class JShapelessRecipe extends JResultRecipe {
  private static final String TYPE = "minecraft:crafting_shapeless";
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  protected transient final JIngredients ingredients;
  @SerializedName("ingredients")
  public final List<JIngredient> ingredientList;

  public JShapelessRecipe(String result, Collection<String> ingredientList) {
    super(TYPE, result);
    this.ingredientList = ingredientList.stream().map(JIngredient::ofItem).collect(Collectors.toList());
    ingredients = null;
  }

  public JShapelessRecipe(Identifier result, Identifier... ingredientList) {
    super(TYPE, result);
    this.ingredientList = Arrays.stream(ingredientList).map(JIngredient::ofItem).collect(Collectors.toList());
    ingredients = null;
  }

  public JShapelessRecipe(Item result, Item... ingredientList) {
    super(TYPE, result);
    this.ingredientList = Arrays.stream(ingredientList).map(JIngredient::ofItem).collect(Collectors.toList());
    ingredients = null;
  }

  public JShapelessRecipe(ItemConvertible result, ItemConvertible... ingredientList) {
    super(TYPE, result);
    this.ingredientList = Arrays.stream(ingredientList).map(JIngredient::ofItem).collect(Collectors.toList());
    ingredients = null;
  }

  public JShapelessRecipe(final JResult result, List<JIngredient> ingredientList) {
    super(TYPE, result);
    this.ingredientList = ingredientList;
    this.ingredients = null;
  }

  @Deprecated
  public JShapelessRecipe(final JResult result, final JIngredients ingredients) {
    super("minecraft:crafting_shapeless", result);
    this.ingredients = ingredients;
    this.ingredientList = ingredients.ingredients;
  }

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
