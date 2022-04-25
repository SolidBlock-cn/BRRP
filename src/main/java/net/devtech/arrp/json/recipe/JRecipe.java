package net.devtech.arrp.json.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>A <b>recipe</b> defines item conversion rules such as crafting, cooking, smithing or stonecutting. A recipe has a type, which defines which type of the recipe it belongs to, and an optional group, which states that different recipes with the equal group will be displayed together.</p>
 *
 * @see net.minecraft.recipe.Recipe
 * @see net.minecraft.recipe.RecipeSerializer
 * @see net.minecraft.data.server.recipe.ShapedRecipeJsonFactory
 */
public abstract class JRecipe implements Cloneable {
  /**
   * The type of the recipe. It is in essence in the format of {@link Identifier}, but here it is as string. Possible values include {@code blasting, campfire_cooking, crafting_shaped, crafting_shapeless, smelting, smithing, smoking, stonecutting}, which are defined by those subtypes.
   */
  public final String type;

  /**
   * The optional recipe group. If defined, in the recipe book, different recipes with equal group will be shown together.
   */
  public String group;

  public JRecipe(final String type) {
    this.type = type;
  }

  /**
   * @deprecated Please directly call the constructor. In BRRP mod, they are public now.
   */
  @Deprecated
  public static JSmithingRecipe smithing(final JIngredient base, final JIngredient addition, final JResult result) {
    return new JSmithingRecipe(base, addition, result);
  }

  /**
   * @deprecated Please directly call the constructor. In BRRP mod, they are public now.
   */
  @Deprecated
  public static JStonecuttingRecipe stonecutting(final JIngredient ingredient, final JStackedResult result) {
    return new JStonecuttingRecipe(ingredient, result);
  }

  // crafting

  /**
   * @deprecated Please directly call the constructor. In BRRP mod, they are public now.
   */
  @Deprecated
  public static JShapedRecipe shaped(final JPattern pattern, final JKeys keys, final JResult result) {
    return new JShapedRecipe(result, pattern, keys);
  }

  /**
   * @deprecated Please directly call the constructor. In BRRP mod, they are public now.
   */
  @Deprecated
  public static JShapelessRecipe shapeless(final JIngredients ingredients, final JResult result) {
    return new JShapelessRecipe(result, ingredients);
  }

  // cooking

  /**
   * @deprecated Please directly call the constructor. In BRRP mod, they are public now.
   */
  @Deprecated
  public static JBlastingRecipe blasting(final JIngredient ingredient, final JResult result) {
    return new JBlastingRecipe(ingredient, result);
  }

  /**
   * @deprecated Please directly call the constructor. In BRRP mod, they are public now.
   */
  @Deprecated
  public static JSmeltingRecipe smelting(final JIngredient ingredient, final JResult result) {
    return new JSmeltingRecipe(ingredient, result);
  }

  /**
   * @deprecated Please directly call the constructor. In BRRP mod, they are public now.
   */
  @Deprecated
  public static JCampfireRecipe campfire(final JIngredient ingredient, final JResult result) {
    return new JCampfireRecipe(ingredient, result);
  }

  /**
   * @deprecated Please directly call the constructor. In BRRP mod, they are public now.
   */
  @Deprecated
  public static JSmokingRecipe smoking(final JIngredient ingredient, final JResult result) {
    return new JSmokingRecipe(ingredient, result);
  }

  /**
   * Set the recipe group.
   */

  public JRecipe group(final String group) {
    this.group = group;
    return this;
  }

  @Override
  public JRecipe clone() {
    try {
      return (JRecipe) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  /**
   * Create a delegated JRecipe, whose serialization is identical to the delegate.
   *
   * @param delegate The RecipeJsonProvider whose serialization will be directly used.
   * @return The delegated JRecipe.
   */
  public static JRecipe delegate(final RecipeJsonProvider delegate) {
    return new Delegate(delegate);
  }

  /**
   * Create a delegated JRecipe, whose serialization is identical to the delegate.
   *
   * @param delegate The CraftingRecipeJsonBuilder whose serialization will be directly used.
   * @return The delegated JRecipe.
   */
  public static JRecipe delegate(final ShapedRecipeJsonFactory delegate) {
    AtomicReference<RecipeJsonProvider> jsonProvider = new AtomicReference<>();
    delegate.offerTo(jsonProvider::set);
    return delegate(jsonProvider.get());
  }

  @ApiStatus.Internal
  private static final class Delegate extends JRecipe implements JsonSerializable {
    public final RecipeJsonProvider delegate;

    private Delegate(RecipeJsonProvider delegate) {
      super(null);
      this.delegate = delegate;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return delegate.toJson();
    }
  }
}
