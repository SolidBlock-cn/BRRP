package net.devtech.arrp.json.blockstate;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.state.property.Property;
import net.minecraft.util.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The map for property names and values. The key is the property name, and the value is the property value, or joined with `|` if there are multiple values.
 */
public class JWhen extends LinkedHashMap<String, String> implements Cloneable, JsonSerializable {
  /**
   * @deprecated Please use {@link #alternatives}.
   */
  @SuppressWarnings({"DeprecatedIsStillUsed"})
  @Deprecated(forRemoval = true)
  private final List<Pair<String, String[]>> OR = new ArrayList<>();

  /**
   * The alternatives of conditions. If the value is present, this condition will be met if any of the alternative conditions is met.
   */
  public final List<JWhen> alternatives;

  /**
   * Create a condition without `OR` field, which means a simple condition.
   */
  public JWhen() {
    alternatives = null;
  }

  public static JWhen alternatives(JWhen... jWhens) {
    return new JWhen(Arrays.asList(jWhens));
  }

  /**
   * Create a condition with an `OR` field. In this case, methods like {@link #add} should not be used.
   */
  public JWhen(List<JWhen> alternatives) {
    this.alternatives = alternatives;
  }

  public JWhen(String property, String... value) {
    this();
    add(property, value);
  }

  @SafeVarargs
  public <T extends Comparable<T>> JWhen(Property<T> property, T... value) {
    this();
    add(property, value);
  }

  @CanIgnoreReturnValue
  public <T extends Comparable<T>> JWhen add(Property<T> property, T value) {
    put(property.getName(), value.toString());

    return this;
  }

  @SafeVarargs
  @CanIgnoreReturnValue
  public final <T extends Comparable<T>> JWhen add(Property<T> property, T... value) {
    put(property.getName(), Arrays.stream(value).map(Object::toString).collect(Collectors.joining("|")));
    return this;
  }

  public JWhen add(String property, String value) {
    put(property, value);
    OR.add(new Pair<>(property, new String[]{value}));
    return this;
  }

  public JWhen add(String property, String... value) {
    put(property, String.join("|", value));
    OR.add(new Pair<>(property, value));
    return this;
  }

  @Override
  public JWhen clone() {
    return (JWhen) super.clone();
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject object = new JsonObject();
    forEach(object::addProperty);
    if (alternatives != null) {
      object.add("OR", context.serialize(alternatives));
    }
    return object;
  }

  /**
   * @deprecated This class is kept for compatibility.
   */
  @Deprecated
  public static class Serializer implements JsonSerializer<JWhen> {
    @Override
    public JsonElement serialize(JWhen src, Type typeOfSrc, JsonSerializationContext context) {
      return src.serialize(typeOfSrc, context);
    }
  }
}
