package net.devtech.arrp.json.blockstate;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.*;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.data.client.When;
import net.minecraft.state.property.Property;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>The map for property names and values. The key is the property name, and the value is the property value, or joined with {@code "|"} if there are multiple values.</p>
 *
 * @see net.minecraft.data.client.When
 */
@SuppressWarnings("unused")
public class JWhen extends ForwardingMap<String, String> implements Cloneable, JsonSerializable {
  /**
   * The alternatives of conditions. If the value is present, this condition will be met if any of the alternative conditions is met.
   *
   * @see net.minecraft.data.client.When.LogicalCondition
   */
  public final List<JWhen> alternatives;
  /**
   * The map storing its actual contents. It <b>can</b> be sometimes <b>immutable</b>, if it is created with {@link #JWhen(List)}.
   */
  public final Map<String, String> properties;
  /**
   * Field kept for compatibility, considering that some classes still apply mixins to it.
   *
   * @deprecated Please use {@link #alternatives}.
   */
  @SuppressWarnings({"DeprecatedIsStillUsed"})
  @Deprecated(forRemoval = true)
  private final List<Pair<String, String[]>> OR = new ArrayList<>();

  /**
   * Create a condition <i>without</i> `OR` field, which means a simple condition.
   */
  public JWhen() {
    this(new LinkedHashMap<>());
  }

  /**
   * Create a condition <i>without</i> `OR` field, with properties (represented as string map) initially loaded.
   *
   * @param properties The properties, represented as a map for strings. It <i>can</i> be immutable if you like, which means in this case you cannot call methods like {@link #add}.
   */
  public JWhen(Map<String, String> properties) {
    this.properties = properties;
    this.alternatives = null;
  }

  /**
   * Create a condition with an `OR` field. In this case, methods like {@link #add} should not be used, as it throws {@link UnsupportedOperationException}.
   *
   * @param alternatives The possible alternative situations, which is the {@link #alternatives "OR"} field. It <i>can</i> be immutable, if you like.
   */
  public JWhen(List<JWhen> alternatives) {
    this.alternatives = alternatives;
    properties = Collections.emptyMap();
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

  /**
   * Create a new {@link JWhen} object with empty alternatives specified, which is mutable.
   *
   * @param jWhens Multiple conditions.
   * @return A new object.
   */
  @Contract("_ -> new")
  public static @NotNull JWhen ofAlternatives(JWhen... jWhens) {
    return new JWhen(Lists.newArrayList(jWhens));
  }

  /**
   * Create a delegated object. It's serialization will be the same as "{@link When}", and any other operations will be ignored.
   *
   * @param delegate The delegate object whose serialization will be used.
   * @return The delegated {@link JWhen} object.
   */
  @Contract("_ -> new")
  private static @NotNull JWhen delegate(When delegate) {
    return new Delegate(delegate);
  }

  @Override
  protected @NotNull Map<String, String> delegate() {
    return properties;
  }

  /**
   * Add a matching property to this object.
   *
   * @param property The property of block states. Vanilla properties can be found in {@link Properties}.
   * @param value    The value of this property.
   * @return The object itself.
   * @throws UnsupportedOperationException if it's created with {@link #JWhen(List)}.
   */
  @CanIgnoreReturnValue
  @Contract("_, _ -> this")
  public <T extends Comparable<T>> JWhen add(Property<T> property, T value) {
    properties.put(property.getName(), property.name(value));
    return this;
  }

  /**
   * Add a matching property to this object.
   *
   * @param property The property of block states. Vanilla properties can be found in {@link Properties}.
   * @param values   The values of this property, representing that each one of the values will be matched. In this case, they will be joined with {@code "|"}.
   * @return The object itself.
   * @throws UnsupportedOperationException if it's created with {@link #JWhen(List)}.
   */
  @Contract("_, _ -> this")
  @SafeVarargs
  @CanIgnoreReturnValue
  public final <T extends Comparable<T>> JWhen add(Property<T> property, T... values) {
    properties.put(property.getName(), Arrays.stream(values).map(property::name).collect(Collectors.joining("|")));
    return this;
  }

  /**
   * Add a matching property to this object.
   *
   * @param property The property of block states, represented as string
   * @param value    The one or multiple values of this property, represented as string.
   * @return The object itself.
   * @throws UnsupportedOperationException if it's created with {@link #JWhen(List)}.
   */
  @CanIgnoreReturnValue
  @Contract("_, _->this")
  public JWhen add(String property, String value) {
    properties.put(property, value);
    return this;
  }

  /**
   * Add a matching property to this object.
   *
   * @param property The property of block states, represented as string
   * @param value    The value of this property, represented as a {@link StringIdentifiable} object.
   * @return The object itself.
   * @throws UnsupportedOperationException if it's created with {@link #JWhen(List)}.
   */
  @CanIgnoreReturnValue
  @Contract("_, _->this")
  public JWhen add(String property, StringIdentifiable value) {
    properties.put(property, value.asString());
    return this;
  }

  /**
   * Add a matching property to this object.
   *
   * @param property The property of block states, represented as string
   * @param values   The values of this property, representing that each one of the values will be matched. In this case, they will be joined with {@code "|"}.
   * @return The object itself.
   * @throws UnsupportedOperationException if it's created with {@link #JWhen(List)}.
   */
  @CanIgnoreReturnValue
  @Contract("_, _->this")
  public JWhen add(String property, String... values) {
    properties.put(property, String.join("|", values));
    return this;
  }

  /**
   * Add a matching property to this object.
   *
   * @param property The property of block states, represented as string
   * @param values   The values of this property, representing that each one of the values will be matched. In this case, they will be joined with {@code "|"}.
   * @return The object itself.
   * @throws UnsupportedOperationException if it's created with {@link #JWhen(List)}.
   */
  @CanIgnoreReturnValue
  @Contract("_, _->this")
  public JWhen add(String property, StringIdentifiable... values) {
    properties.put(property, Arrays.stream(values).map(StringIdentifiable::asString).collect(Collectors.joining("|")));
    return this;
  }

  @Override
  public JWhen clone() {
    try {
      return (JWhen) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject object = new JsonObject();
    if (alternatives != null || !OR.isEmpty()) {
      final JsonArray jsonArray = new JsonArray();
      if (alternatives != null) for (JWhen alternative : alternatives) {
        jsonArray.add(context.serialize(alternative));
      }
      for (Pair<String, String[]> pair : OR) {
        jsonArray.add(pair.getLeft() + "=" + String.join("|", pair.getRight()));
      }
      object.add("OR", jsonArray);
    } else forEach(object::addProperty);
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

  private static final class Delegate extends JWhen implements JsonSerializable, Supplier<JsonElement> {
    public final When delegate;

    private Delegate(When delegate) {
      this.delegate = delegate;
    }

    @Override
    public JsonElement get() {
      return delegate.get();
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return delegate.get();
    }
  }
}
