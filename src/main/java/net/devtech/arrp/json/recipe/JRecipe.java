package net.devtech.arrp.json.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>A <b>recipe</b> defines item conversion rules such as crafting, cooking, smithing or stonecutting. A recipe has a type, which defines which type of the recipe it belongs to, and an optional group, which states that different recipes with the equal group will be displayed together.</p>
 *
 * @see net.minecraft.recipe.Recipe
 * @see net.minecraft.recipe.RecipeSerializer
 * @see net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder
 */
public abstract class JRecipe implements Cloneable {
  /**
   * The type of the recipe. It is in essence in the format of {@link Identifier}, but here it is as string. Possible values include <code style=color:navy>blasting, campfire_cooking, crafting_shaped, crafting_shapeless, smelting, smithing, smoking, stonecutting</code>, which are defined by those subtypes.
   */
  public final String type;

  /**
   * The optional recipe group. If defined, in the recipe book, different recipes with equal group will be shown together.
   */
  public String group;

  /**
   * <p>The advancement of this recipe. It usually triggers when you unlocked the recipe or obtained an ingredient, and rewards you with unlocking this recipe.</p>
   * <p>For example, for the smelting recipe of glass, when you obtain a sands block, the advancement will be achieved and the recipe of glass will be unlocked.</p>
   */
  public final transient Advancement.Builder advancementBuilder = Advancement.Builder.create().parent(new Identifier("minecraft", "recipes/root")).criteriaMerger(CriterionMerger.OR);

  /**
   * Create a new simple recipe object.
   *
   * @param type The type of the recipe. It is the identifier in the format of string. See {@link #type}.
   */
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
  @CanIgnoreReturnValue
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
  public static JRecipe delegate(final CraftingRecipeJsonBuilder delegate) {
    AtomicReference<RecipeJsonProvider> jsonProvider = new AtomicReference<>();
    delegate.offerTo(jsonProvider::set);
    return delegate(jsonProvider.get());
  }

  /**
   * The corresponding advancement builder of this recipe. By default, it is an advancement that triggers when the player obtains a specific ingredient or craft an item, and rewards the player with the recipe.
   *
   * @return The advancement builder of the recipe advancement.
   */
  public @Nullable Advancement.Builder asAdvancement() {
    return advancementBuilder;
  }

  /**
   * Add a criterion of obtaining the advancement, that you get an item. The item is usually one of the ingredients. That means, when you gain that item, no matter in which way, you will achieve the advancement and unlock this recipe.
   *
   * @param criterionName The name of the advancement criterion. It is usually a short, descriptive name, such as {@code "has_stone"}.
   * @param item          The item that when obtained, the criterion will be triggered.
   */
  @CanIgnoreReturnValue
  @Contract("_, _ -> this")
  public JRecipe addInventoryChangedCriterion(String criterionName, ItemConvertible item) {
    advancementBuilder.criterion(criterionName, InventoryChangedCriterion.Conditions.items(item));
    return this;
  }

  /**
   * Prepare the advancement of the recipe.
   *
   * @param recipeId The id of the recipe.
   */
  @CanIgnoreReturnValue
  @Contract("_ -> this")
  public JRecipe prepareAdvancement(Identifier recipeId) {
    advancementBuilder
        .rewards(new AdvancementRewards.Builder().addRecipe(recipeId))
        .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId));
    return this;
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
