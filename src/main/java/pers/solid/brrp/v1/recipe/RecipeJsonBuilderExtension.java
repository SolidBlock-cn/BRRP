package pers.solid.brrp.v1.recipe;

import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface RecipeJsonBuilderExtension<Self> {
  /**
   * The common bridge for various methods to call "criterion".
   */
  @SuppressWarnings("unchecked")
  @Contract("_, _ -> this")
  @ApiStatus.Internal
  default Self criterionMethodBridge(String criterionName, CriterionConditions criterionConditions) {
    return (Self) this;
  }

  /**
   * Add a criterion of obtaining the item ({@link RecipesProvider#conditionsFromItem(ItemConvertible)}), with the auto-decided criterion name. This is a convenient method.
   */
  default Self criterionFromItem(ItemConvertible item) {
    return criterionMethodBridge("has_" + Registry.ITEM.getId(item.asItem()).getPath(), RecipesProvider.conditionsFromItem(item));
  }

  /**
   * Add a criterion of obtaining the item ({@link RecipesProvider#conditionsFromItem(ItemConvertible)} with the specified criterion name.
   */
  default Self criterionFromItem(String criterionName, ItemConvertible item) {
    return criterionMethodBridge(criterionName, RecipesProvider.conditionsFromItem(item));
  }

  /**
   * Add a criterion of obtaining the item predicate ({@link RecipesProvider#conditionsFromItemPredicates(ItemPredicate...)} with the specified criterion name.
   */
  default Self criterionFromItemPredicates(String criterionName, ItemPredicate... predicates) {
    return criterionMethodBridge(criterionName, RecipesProvider.conditionsFromItemPredicates(predicates));
  }

  /**
   * Add a criterion of obtaining the item in the specified tag ({@link RecipesProvider#conditionsFromTag(Tag)} with the specified criterion name.
   */
  default Self criterionFromItemTag(String criterionName, Tag<Item> itemTag) {
    return criterionMethodBridge(criterionName, RecipesProvider.conditionsFromTag(itemTag));
  }

  /**
   * <p>Set a custom recipe category. The recipe category is used in the id of advancement. In vanilla minecraft, the id of the advancement of obtaining the recipe is the id of the recipe prefixed with <code>recipes/<var>itemGroup</var>/</code>, which is the group of the item If it is {@code null}, exceptions will be thrown.</p>
   * <p>If you need to specify a custom recipe category so that the advancement will be in the specified location, you may call this method. BRRP has made it possible to recognize your custom recipe category, which will be used in {@link RecipeJsonProvider#getAdvancementId()} via "offerTo" methods, such as {@link net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory#offerTo(Consumer, Identifier)}.</p>
   * <p>If the custom recipe category is {@code null}, it will perform as vanilla does. If it is an empty string (not null), the location of the advancement will not be suffixed with any recipe category.</p>
   * <table><caption>Recipe id and advancement id</caption>
   * <tr><th>Recipe id</th><th>Custom recipe category</th><th>Advancement id</th></tr>
   * <tr><td><code>namespace:path</code></td><td>{@code null}</td><td><code>namespace:recipes/<var>itemGroup</var>/path</code></td></tr>
   * <tr><td><code>namespace:path</code></td><td><code><var>customRecipeCategory</var></code</td><td><code>namespace:recipes/<var>customRecipeCategory</var>/path</code></td></tr>
   * <tr><td><code>namespace:path</code></td><td>empty string</td><td><code>namespace:recipes/path</code></td></tr>
   * </table>
   *
   * @param recipeCategory Your custom recipe category.
   */
  @SuppressWarnings("unchecked")
  default Self setCustomRecipeCategory(@Nullable String recipeCategory) {
    return (Self) this;
  }

  /**
   * Bypass validation when offering the recipe to the output. Usually when calling "offerTo" methods, the "validate" method will be called, such as checking whether the recipe is the advancement criterions so that you can obtain the recipe. If you set it to {@code true}, the validation will be skipped.
   */
  @SuppressWarnings("unchecked")
  default Self setBypassesValidation(boolean bypassesValidation) {
    return (Self) this;
  }
}
