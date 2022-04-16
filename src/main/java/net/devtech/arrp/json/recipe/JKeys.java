package net.devtech.arrp.json.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JKeys extends HashMap<String, JIngredient> implements Cloneable, JsonSerializable {
  /**
   * @deprecated It's equal to the object itself, so you do not need it anymore. It's kept for only compatibility.
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  protected final Map<String, JIngredient> keys;
  /**
   * @deprecated You do not need it anymore.
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated(forRemoval = true)
  @ApiStatus.ScheduledForRemoval(inVersion = "unsure")
  protected final Map<String, List<JIngredient>> acceptableKeys;

  public JKeys() {
    super(9, 1);
    this.keys = this;
    this.acceptableKeys = new HashMap<>();
  }

  /**
   * @deprecated Please directly call {@link #JKeys()}.
   */
  @Deprecated
  public static JKeys keys() {
    return new JKeys();
  }

  /**
   * Add a key with the ingredient specified.
   *
   * @param key   The recipe key.
   * @param value The ingredient.
   */
  @CanIgnoreReturnValue
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
  public JKeys key(final String key, final Item value) {
    return key(key, JIngredient.ofItem(value));
  }

  @Override
  public JKeys clone() {
    return (JKeys) super.clone();
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject object = new JsonObject();

    keys.forEach((final String key, final JIngredient ingredient) -> object.add(key,
        context.serialize(ingredient)));
    acceptableKeys.forEach((final String key, final List<JIngredient> acceptableIngredients) -> object.add(
        key,
        context.serialize(acceptableIngredients)));

    return object;
  }

  public static class Serializer implements JsonSerializer<JKeys> {
    @Override
    public JsonElement serialize(final JKeys src, final Type typeOfSrc, final JsonSerializationContext context) {
      return src.serialize(typeOfSrc, context);
    }
  }
}
