package net.devtech.arrp.json.recipe;

import com.google.common.collect.ForwardingMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class JKeys extends ForwardingMap<String, JIngredient> implements Cloneable, JsonSerializable {
  /**
   * The map storing its keys, used for the forwarding map. It's by default a {@link LinkedHashMap}.
   */
  protected final Map<String, JIngredient> keys;

  /**
   * Create a new, mutable {@code JKeys} object, with a new {@link LinkedHashMap} initialized.
   */
  public JKeys() {
    this.keys = new LinkedHashMap<>(9, 1);
  }

  /**
   * Create a new {@code JKeys} object with keys explicitly specified. You can create an immutable one by this. If you want to create an empty, mutable one, you should use {@link #JKeys()}.
   *
   * @param keys The map of keys and ingredients, which will be directly used as a field. You can make it immutable; in this case, methods such as {@link #key} should not be used, or {@link UnsupportedOperationException} may be thrown.
   */
  public JKeys(Map<String, JIngredient> keys) {
    this.keys = keys;
  }

  @Override
  protected @NotNull Map<String, JIngredient> delegate() {
    return keys;
  }


  /**
   * Add a key with the ingredient specified.
   *
   * @param key   The recipe key.
   * @param value The ingredient.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JKeys key(final String key, final JIngredient value) {
    this.keys.put(key, value);
    return this;
  }

  /**
   * Add a key with a simple item ingredient specified.
   *
   * @param key   The recipe key.
   * @param value The identifier (as string) of the ingredient item.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JKeys key(final String key, final String value) {
    return key(key, JIngredient.ofItem(value));
  }

  /**
   * Add a key with a simple item ingredient specified.
   *
   * @param key   The recipe key.
   * @param value The identifier of the ingredient item.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JKeys key(final String key, final Identifier value) {
    return key(key, JIngredient.ofItem(value));
  }

  /**
   * Add a key with a simple item ingredient specified.
   *
   * @param key   The recipe key.
   * @param value The ingredient item. Must be registered when calling this.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JKeys key(final String key, final ItemConvertible value) {
    return key(key, JIngredient.ofItem(value));
  }

  /**
   * Add a key with a simple item ingredient specified.
   *
   * @param key   The recipe key.
   * @param value The ingredient item. Must be registered when calling this.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JKeys key(final String key, final Item value) {
    return key(key, JIngredient.ofItem(Registries.ITEM.getId(value)));
  }

  @Override
  public JKeys clone() {
    try {
      return (JKeys) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject object = new JsonObject();

    keys.forEach((final String key, final JIngredient ingredient) -> object.add(key,
        context.serialize(ingredient)));

    return object;
  }
}
