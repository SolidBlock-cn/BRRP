package net.devtech.arrp.json.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.json.tags.IdentifiedTag;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTrimRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * A <b>smithing recipe</b> refers to a recipe used in a smithing table. It's usually the recipe to upgrade diamond armors to netherite armors.
 *
 * @see net.minecraft.recipe.SmithingRecipe
 * @see net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder.SmithingTransformRecipeJsonProvider
 */
@SuppressWarnings("unused")
public class JSmithingRecipe extends JResultRecipe {
  public final JIngredient base;
  public final JIngredient addition;
  public static final String TYPE = Registries.RECIPE_SERIALIZER.getId(RecipeSerializer.SMITHING).toString();

  public JSmithingRecipe(final JIngredient base, final JIngredient addition, final JResult result) {
    super(TYPE, result);

    this.base = base;
    this.addition = addition;
  }

  public JSmithingRecipe(final ItemConvertible base, final ItemConvertible addition, final ItemConvertible result) {
    this(JIngredient.ofItem(base), JIngredient.ofItem(addition), new JResult(result));
  }

  public JSmithingRecipe(final String base, final String addition, final String result) {
    this(JIngredient.ofItem(base), JIngredient.ofItem(addition), new JResult(result));
  }

  public JSmithingRecipe(final Identifier base, final Identifier addition, final Identifier result) {
    this(JIngredient.ofItem(base), JIngredient.ofItem(addition), new JResult(result));
  }

  public JSmithingRecipe(final IdentifiedTag base, final IdentifiedTag addition, final Identifier result) {
    this(JIngredient.ofTag(base), JIngredient.ofTag(addition), new JResult(result));
  }

  public JSmithingRecipe(final IdentifiedTag base, final IdentifiedTag addition, final String result) {
    this(JIngredient.ofTag(base), JIngredient.ofTag(addition), new JResult(result));
  }

  public JSmithingRecipe(final IdentifiedTag base, final IdentifiedTag addition, final ItemConvertible result) {
    this(JIngredient.ofTag(base), JIngredient.ofTag(addition), new JResult(result));
  }

  /**
   * Create a "delegated" JSmithingRecipe object from a vanilla {@link SmithingTransformRecipeJsonBuilder.SmithingTransformRecipeJsonProvider} object. Its json serialization will be the same as the delegate.
   *
   * @param delegate The vanilla {@link SmithingTransformRecipeJsonBuilder.SmithingTransformRecipeJsonProvider} object. When serializing, its serialization will be directly used.
   * @return A "delegated" object.
   * @see net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder.SmithingTransformRecipeJsonProvider
   */
  public static JSmithingRecipe delegate(SmithingTransformRecipeJsonBuilder.SmithingTransformRecipeJsonProvider delegate) {
    return new Delegate(delegate);
  }

  /**
   * Create a "delegated" JSmithingRecipe object from a vanilla {@link SmithingTrimRecipeJsonBuilder.SmithingTrimRecipeJsonProvider} object. Its json serialization will be the same as the delegate.
   *
   * @param delegate The vanilla {@link SmithingTrimRecipeJsonBuilder.SmithingTrimRecipeJsonProvider} object. When serializing, its serialization will be directly used.
   * @return A "delegated" object.
   * @see net.minecraft.data.server.recipe.SmithingTrimRecipeJsonBuilder.SmithingTrimRecipeJsonProvider
   */
  public static JSmithingRecipe delegate(SmithingTrimRecipeJsonBuilder.SmithingTrimRecipeJsonProvider delegate) {
    return new Delegate(delegate);
  }

  @CanIgnoreReturnValue
  @Contract("_ -> this")
  @Override
  public JSmithingRecipe group(final String group) {
    return (JSmithingRecipe) super.group(group);
  }

  @CanIgnoreReturnValue
  @Contract("_ -> this")
  @Override
  public JSmithingRecipe recipeCategory(@Nullable RecipeCategory category) {
    return (JSmithingRecipe) super.recipeCategory(category);
  }

  @Override
  public JSmithingRecipe clone() {
    return (JSmithingRecipe) super.clone();
  }

  @ApiStatus.Internal
  private static final class Delegate extends JSmithingRecipe implements JsonSerializable {
    public final RecipeJsonProvider delegate;
    public static final RecipeSerializer<SmithingTransformRecipe> SERIALIZER = RecipeSerializer.SMITHING_TRANSFORM;

    private Delegate(RecipeJsonProvider delegate) {
      super((JIngredient) null, null, null);
      this.delegate = delegate;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return delegate.toJson();
    }
  }
}
