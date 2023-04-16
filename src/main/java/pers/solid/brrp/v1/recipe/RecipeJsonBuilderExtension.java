package pers.solid.brrp.v1.recipe;

import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.RecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
   * <p>Set a custom recipe category. The recipe category is used in the id of advancement. In vanilla minecraft, the id of the advancement of obtaining the recipe is the id of the recipe prefixed with <code>recipes/<var>recipeCategory</var>/</code>. However, {@link RecipeCategory} is an enum, and only supports specified values.</p>
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
  @SuppressWarnings("unchecked")
  default Self setCustomRecipeCategory(@Nullable String recipeCategory) {
    return (Self) this;
  }

  /**
   * <p>Set a custom crafting recipe category. Note that it differs from the "recipe category".</p>
   * <p>Usually, for versions 1.19.3 and above, the crafting category is dependent on its recipe category, according to {@link RecipeJsonBuilder#getCraftingCategory(RecipeCategory)}. However, if it is {@code null}, it will throw an NPE.</p>
   * <p>The recipe category is given when you create a new recipe builder, and you can also config a custom recipe category. In this case, the crafting category is determined by the vanilla recipe category you provided initially, and the custom recipe category does not affect the crafting category. If your vanilla recipe category is null, you <em>have to</em> specify the custom recipe category.</p>
   *
   * @param customCraftingCategory Your custom crafting category.
   * @see RecipeJsonBuilder#getCraftingCategory(RecipeCategory)
   */
  @SuppressWarnings("unchecked")
  default Self setCustomCraftingCategory(@Nullable CraftingRecipeCategory customCraftingCategory) {
    return (Self) this;
  }

  /**
   * Bypass validation when offering the recipe to the output. Usually when calling "offerTo" methods, the "validate" method will be called, such as checking whether the recipe is the advancement criteria so that you can obtain the recipe. If you set it to {@code true}, the validation will be skipped.
   */
  @SuppressWarnings("unchecked")
  default Self setBypassesValidation(boolean bypassesValidation) {
    return (Self) this;
  }

  /**
   * The method is used for mixins to call to {@link RecipeJsonBuilder#getCraftingCategory(RecipeCategory)}.
   */
  static @NotNull RecipeCategory invertGetCraftingCategory(@NotNull CraftingRecipeCategory craftingRecipeCategory) {
    return switch (craftingRecipeCategory) {
      case BUILDING -> RecipeCategory.BUILDING_BLOCKS;
      case EQUIPMENT -> RecipeCategory.TOOLS;
      case REDSTONE -> RecipeCategory.REDSTONE;
      default -> RecipeCategory.MISC;
    };
  }
}
