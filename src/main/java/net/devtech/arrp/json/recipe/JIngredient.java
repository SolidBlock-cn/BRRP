package net.devtech.arrp.json.recipe;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.json.tags.IdentifiedTag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>An <b>ingredient</b>, as it literally means, is used in several types of recipes. It has either an item, or an item tag.</p>
 * <p>It takes two forms: a single ingredient, with an item or tag specified, and a <i>list</i> of ingredients, with multiple single ingredients specified.</p>
 *
 * @see net.minecraft.recipe.Ingredient
 */
@SuppressWarnings("unused")
public class JIngredient implements Cloneable, JsonSerializable {
  /**
   * The identifier of the {@link Item}. It should exist only when {@link #tag} and {@link #ingredients} are null.
   */
  protected String item;
  /**
   * The identifier of the item tag. It should exist only when {@link #item} and {@link #ingredients} are null.
   */
  protected String tag;
  /**
   * The list of ingredients. If this field exists, the {@link #item} and {@link #tag} fields will be ignored and should be null, and this object will be serialized as a {@link com.google.gson.JsonArray}.
   */
  protected List<JIngredient> ingredients;

  /**
   * Create an empty ingredient object.
   *
   * @see #ofItem
   * @see #ofTag
   * @see #ofItems
   * @see #ofTags
   */
  @ApiStatus.Internal
  public JIngredient() {
  }

  /**
   * When calling this constructor, either {@link #item} or {@link #tag} should be null.
   *
   * @param item The identifier (as string) of the item.
   * @param tag  The identifier (as tag) of the item tag.
   */
  protected JIngredient(String item, String tag) {
    this.item = item;
    this.tag = tag;
  }

  /**
   * Create an empty object with the list of ingredients set.
   *
   * @param ingredients The list of ingredients. It will be directly used as the field.
   */
  @ApiStatus.Internal
  protected JIngredient(List<JIngredient> ingredients) {
    this.ingredients = ingredients;
  }

  /**
   * @deprecated Please directly call {@link #JIngredient()}. It's public now.
   */
  @Deprecated
  public static JIngredient ingredient() {
    return new JIngredient();
  }

  /**
   * Set the item identifier of this ingredient. You must ensure that the item has been registered when calling this.
   *
   * @param item The item.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JIngredient item(Item item) {
    return this.item(Preconditions.checkNotNull(ForgeRegistries.ITEMS.getKey(item)).toString());
  }

  /**
   * Set the item identifier of this ingredient. You must ensure that the item has been registered when calling this.
   *
   * @param itemConvertible The item. It can be a block.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JIngredient item(ItemConvertible itemConvertible) {
    return item(itemConvertible.asItem());
  }

  /**
   * Set the item identifier of this ingredient.
   *
   * @param id The identifier as string.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JIngredient item(String id) {
    if (this.isDefined()) {
      return this.add(JIngredient.ofItem(id));
    }

    this.item = id;

    return this;
  }

  /**
   * Set the item identifier of this ingredient.
   *
   * @param id The identifier of the item.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JIngredient item(Identifier id) {
    return item(id.toString());
  }

  @Contract("_ -> new")
  public static JIngredient ofItem(ItemConvertible itemConvertible) {
    return ofItem(Preconditions.checkNotNull(ForgeRegistries.ITEMS.getKey(itemConvertible.asItem())));
  }

  @Contract("_ -> new")
  public static JIngredient ofItem(String id) {
    return new JIngredient(id, null);
  }

  @Contract("_ -> new")
  public static JIngredient ofItem(Identifier id) {
    return ofItem(id.toString());
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JIngredient tag(String tagId) {
    if (this.isDefined()) {
      return this.add(JIngredient.ingredient().tag(tagId));
    }

    this.tag = tagId;

    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JIngredient tag(Identifier tagId) {
    return tag(tagId.toString());
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JIngredient tag(IdentifiedTag tag) {
    return tag(tag.identifier);
  }

  @Contract("_ -> new")
  public static JIngredient ofTag(String tagId) {
    return new JIngredient(null, tagId);
  }

  @Contract("_ -> new")
  public static JIngredient ofTag(Identifier tagId) {
    return ofTag(tagId.toString());
  }

  @Contract("_ -> new")
  public static JIngredient ofTag(IdentifiedTag tag) {
    return ofTag(tag.identifier);
  }

  @Contract("_ -> new")
  public static JIngredient ofMultipleIngredients(List<JIngredient> ingredients) {
    return new JIngredient(ingredients);
  }

  @Contract("_ -> new")
  public static JIngredient ofItems(String... ids) {
    return new JIngredient(Arrays.stream(ids).map(JIngredient::ofItem).collect(Collectors.toList()));
  }

  @Contract("_ -> new")
  public static JIngredient ofItems(Identifier... ids) {
    return new JIngredient(Arrays.stream(ids).map(JIngredient::ofItem).collect(Collectors.toList()));
  }

  @Contract("_ -> new")
  public static JIngredient ofItems(Item... items) {
    return new JIngredient(Arrays.stream(items).map(item1 -> ofItem(Preconditions.checkNotNull(ForgeRegistries.ITEMS.getKey(item1)))).collect(Collectors.toList()));
  }

  @Contract("_ -> new")
  public static JIngredient ofItems(ItemConvertible... itemConvertibles) {
    return new JIngredient(Arrays.stream(itemConvertibles).map(JIngredient::ofItem).collect(Collectors.toList()));
  }

  @Contract("_ -> new")
  public static JIngredient ofTags(String... tagIds) {
    return new JIngredient(Arrays.stream(tagIds).map(JIngredient::ofTag).collect(Collectors.toList()));
  }

  @Contract("_ -> new")
  public static JIngredient ofTags(Identifier... tagIds) {
    return new JIngredient(Arrays.stream(tagIds).map(JIngredient::ofTag).collect(Collectors.toList()));
  }

  @Contract("_ -> new")
  public static JIngredient ofTags(IdentifiedTag... tags) {
    return new JIngredient(Arrays.stream(tags).map(JIngredient::ofTag).collect(Collectors.toList()));
  }

  /**
   * Create a "delegated" JIngredient object from a vanilla Ingredient object. Its json serialization will be the same as the delegate.
   *
   * @param delegate The vanilla Ingredient object. When serializing, its serialization will be directly used.
   * @return A "delegated" JIngredient object.
   * @see Ingredient
   */
  @Contract("_ -> new")
  public static JIngredient delegate(Ingredient delegate) {
    return new Delegate(delegate);
  }

  /**
   * Add another ingredient to this ingredient. In this case, the {@link #ingredients} field will be non-null, and this object will be serialized to a {@link com.google.gson.JsonArray}.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JIngredient add(final JIngredient ingredient) {
    if (this.ingredients == null) {
      final List<JIngredient> ingredients = new ArrayList<>();

      if (this.isDefined()) {
        ingredients.add(this.clone());
      }

      this.ingredients = ingredients;
    }

    this.ingredients.add(ingredient);

    return this;
  }

  @Contract(pure = true)
  private boolean isDefined() {
    return this.item != null || this.tag != null;
  }

  @Override
  public JIngredient clone() {
    try {
      return (JIngredient) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    if (this.ingredients != null) {
      return context.serialize(this.ingredients);
    }

    final JsonObject object = new JsonObject();

    object.add("item", context.serialize(this.item));
    object.add("tag", context.serialize(this.tag));

    return object;
  }

  /**
   * @deprecated This class is kept for only compatibility.
   */
  @Deprecated
  public static class Serializer implements JsonSerializer<JIngredient> {
    @Override
    public JsonElement serialize(final JIngredient src,
                                 final Type typeOfSrc,
                                 final JsonSerializationContext context) {
      return src.serialize(typeOfSrc, context);
    }
  }

  @ApiStatus.Internal
  private static final class Delegate extends JIngredient implements JsonSerializable, Predicate<ItemStack> {
    public final Ingredient delegate;

    private Delegate(Ingredient delegate) {
      this.delegate = delegate;
    }

    @Override
    public boolean test(ItemStack itemStack) {
      return delegate.test(itemStack);
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return delegate.toJson();
    }
  }
}
