package pers.solid.brrp.v1.recipe;

import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface RecipeJsonBuilderExtension<Self> {
  /**
   * The common bridge for various methods to call "criterion".
   */
  @Contract("_, _ -> this")
  @ApiStatus.Internal
  default Self criterionMethodBridge(String criterionName, CriterionConditions criterionConditions) {
    throw new AssertionError();
  }

  /**
   * Add a criterion of obtaining the item ({@link RecipeProvider#conditionsFromItem(ItemConvertible)}), with the auto-decided criterion name ({@link RecipeProvider#hasItem(ItemConvertible)}. This is a convenient method.
   */
  default Self criterionFromItem(ItemConvertible item) {
    return criterionMethodBridge(RecipeProvider.hasItem(item), RecipeProvider.conditionsFromItem(item));
  }

  /**
   * Add a criterion of obtaining the item ({@link RecipeProvider#conditionsFromItem(ItemConvertible)} with the specified criterion name.
   */
  default Self criterionFromItem(String criterionName, ItemConvertible item) {
    return criterionMethodBridge(criterionName, RecipeProvider.conditionsFromItem(item));
  }

  /**
   * Add a criterion of obtaining the item ({@link RecipeProvider#conditionsFromItem(NumberRange.IntRange, ItemConvertible)} with the specified criterion name.
   */
  default Self criterionFromItem(String criterionName, NumberRange.IntRange count, ItemConvertible item) {
    return criterionMethodBridge(criterionName, RecipeProvider.conditionsFromItem(count, item));
  }

  /**
   * Add a criterion of obtaining the item predicate ({@link RecipeProvider#conditionsFromItemPredicates(ItemPredicate...)} with the specified criterion name.
   */
  default Self criterionFromItemPredicate(String criterionName, ItemPredicate... predicates) {
    return criterionMethodBridge(criterionName, RecipeProvider.conditionsFromItemPredicates(predicates));
  }

  /**
   * Add a criterion of obtaining the item in the specified tag ({@link RecipeProvider#conditionsFromTag(TagKey)} with the specified criterion name.
   */
  default Self criterionFromItemTag(String criterionName, TagKey<Item> itemTag) {
    return criterionMethodBridge(criterionName, RecipeProvider.conditionsFromTag(itemTag));
  }

  /**
   * <p>Set a custom recipe category. The recipe category is used in the id of advancement. In vanilla minecraft, the id of the advancement of obtaining the recipe is the id of the recipe prefixed with <code>recipes/<var>recipeCategory</var>/</code>. However, {@link RecipeCategory} is an enum, and only supports spcified values.</p>
   * <p>If you need to specify a custom recipe category so that the advancement will be in the specified location, you may call this method. BRRP has made it possible to recognize your custom recipe category, which will be used in {@link RecipeJsonProvider#getAdvancementId()} via "offerTo" methods, such as {@link net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder#offerTo(Consumer, Identifier)}.</p>
   * <p>If the custom recipe category is {@code null}, it will perform as vanilla does. If it is an empty string (not null), the location of the advancement will not be suffixed with any recipe category.</p>
   * <table><caption>Recipe id and advancement id</caption>
   * <tr><th>Recipe id</th><th>Custom recipe category</th><th>Advancement id</th></tr>
   * <tr><td><code>namespace:path</code></td><td>{@code null}</td><td><code>namespace:recipes/<var>vanillaRecipeCategory</var>/path</code></td></tr>
   * <tr><td><code>namespace:path</code></td><td><code><var>customRecipeCategory</var></code</td><td><code>namespace:recipes/<var>customRecipeCategory</var>/path</code></td></tr>
   * <tr><td><code>namespace:path</code></td><td>empty string</td><td><code>namespace:recipes/path</code></td></tr>
   * </table>
   *
   * @param recipeCategory Your custom recipe category.
   */
  default Self setCustomRecipeCategory(@Nullable String recipeCategory) {
    throw new UnsupportedOperationException();
  }

  /**
   * Bypass validation when offering the recipe to the output. Usually when calling "offerTo" methods, the "validate" method will be called, such as checking whether the recipe is the advancement criterions so that you can obtain the recipe. If you set it to {@code true}, the validation will be skipped.
   */
  default Self setBypassesValidation(boolean bypassesValidation) {
    throw new UnsupportedOperationException();
  }
}
