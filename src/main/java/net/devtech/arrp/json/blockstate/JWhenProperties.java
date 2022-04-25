package net.devtech.arrp.json.blockstate;

import com.google.common.collect.ForwardingMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.data.client.model.When;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>A <b>JWhenProperties</b> represents a <b>when</b> object with one or multiple properties to match the block state. In the "multipart" object, if the block state matches the properties, the part will be used.</p>
 * <p>It's essentially a map, with keys being the name of properties, and values representing the matching rule of value of properties. Values can be directly a value, or multiple values joined with {@code "|"}, or one or more values prefixed by {@code "|"} representing negation.</p>
 * <p>This is a simple JSON example for a JWhenProperties object (note that comments are actually not allowed in JSONs):</p>
 * <pre>{@code
 * { "direction": "east",         // single value
 *        "type": "bottom|top",   // multiple alternative values
 *         "age": "!0",           // negated value
 *        "axis": "!x|y" }        // negated multiple values
 * }</pre>
 * <p>The object above can be generated with any of the following codes:</p>
 * <pre>{@code
 * // using pure strings
 * new JWhenProperties()
 *  .add("direction", "east")
 *  .add("type", "bottom", "top")     // or add("type", "bottom|top")
 *  .addNegated("age", "0")           // or add("age", "!0")
 *  .addNegated("axis", "x", "y");    // or add("axis", "!x|y")
 *
 * // using objects
 * new JWhenProperties()
 *  .add(Properties.DIRECTION, Direction.EAST)
 *  .add(Properties.SLAB_TYPE, SlabType.BOTTOM, SlabType.TOP)
 *  .addNegated(Properties.AGE, 0)
 *  .addNegated(Properties.AXIS, Direction.Axis.X, Direction.Axis.Y);
 * }</pre>
 * <p>The negation is defined in {@link PropertyCondition} for data generation but is actually not used. There is no guarantee that the usage of negation can take effect.</p>
 *
 * @see JWhen.PropertyCondition
 */
public class JWhenProperties extends ForwardingMap<String, String> implements When, JsonSerializable {
  /**
   * The delegate map storing properties and values, both of which represent as strings.
   */
  public final @NotNull Map<String, String> properties;

  /**
   * Create a new empty mutable JWhenProperties object. It takes the form of a linked hash map. If you want to initially specify one property, you may use {@link #of}.
   */
  public JWhenProperties() {
    this(new LinkedHashMap<>());
  }

  /**
   * Create a new empty JWhenProperties with a map for properties explicitly defined.
   *
   * @param properties The map storing properties. It will be directly used as the {@link #properties} field. It <i>can</i> be <i>immutable</i>, which means in this case methods like {@link #add} do not take effect.
   */
  public JWhenProperties(@NotNull Map<String, String> properties) {
    this.properties = properties;
  }

  /**
   * Create a new JWhenProperties with one property with one or multiple values initially defined.
   *
   * @param property The name of the property, representing as string.
   * @param values   The values of the property, representing as strings.
   */
  public static JWhenProperties of(String property, String... values) {
    return new JWhenProperties().add(property, values);
  }

  /**
   * Create a new JWhenProperties with one property with one or multiple values initially defined.
   *
   * @param property The name of the property, representing as string.
   * @param values   The values of the property, representing as {@code StringIdentifiable}s.
   */
  public static JWhenProperties of(String property, StringIdentifiable... values) {
    return new JWhenProperties().add(property, values);
  }

  /**
   * Create a new JWhenProperties with one property with one or multiple values initially defined.
   *
   * @param property The property. Vanilla properties can be found in {@link net.minecraft.state.property.Properties}.
   * @param value    The values of the property.
   */
  @SafeVarargs
  public static <T extends Comparable<T>> JWhenProperties of(Property<T> property, T... value) {
    return new JWhenProperties().add(property, value);
  }

  /**
   * Serialize the object as JSON. In this case, the delegate field, {@link #properties}, will be used directly.
   *
   * @param typeOfSrc the actual type (fully genericized version) of the source object.
   */
  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(properties);
  }

  /**
   * Check the validation of this file. If it contains properties that the {@code stateManager} does not recognized, the validation fails.
   *
   * @throws IllegalStateException if it contains unrecognized properties.
   */
  @Override
  public void validate(StateManager<?, ?> stateManager) {
    List<String> list = this.properties.keySet().stream().filter(property -> stateManager.getProperty(property) != null).toList();
    if (!list.isEmpty()) {
      throw new IllegalStateException("Properties " + list + " are missing from " + stateManager);
    }
  }

  @Override
  public JsonElement get() {
    return RuntimeResourcePackImpl.GSON.toJsonTree(this);
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
   */

  @Contract("_, _ -> this")
  public <T extends Comparable<T>> JWhenProperties add(Property<T> property, T value) {
    return add(property.getName(), property.name(value));
  }

  /**
   * Add a matching property to this object, with multiple acceptable values given.
   *
   * @param property The property of block states. Vanilla properties can be found in {@link Properties}.
   * @param values   The values of this property, representing that each one of the values will be matched. In this case, they will be joined with {@code "|"}.
   * @return The object itself.
   */
  @Contract("_, _ -> this")
  @SafeVarargs

  public final <T extends Comparable<T>> JWhenProperties add(Property<T> property, T... values) {
    return add(property.getName(), Arrays.stream(values).map(property::name).collect(Collectors.joining("|")));
  }

  /**
   * Add a matching property to this object.
   *
   * @param property The property of block states, represented as string
   * @param value    The one or multiple values of this property, represented as string.
   * @return The object itself.
   */

  @Contract("_, _->this")
  public JWhenProperties add(String property, String value) {
    properties.put(property, value);
    return this;
  }

  /**
   * Add a negated property to this object.
   *
   * @param property The property of block states, represented as string
   * @param value    The one or multiple values of this property, represented as string.
   * @return The object itself.
   */

  @Contract("_, _->this")
  public JWhenProperties addNegated(String property, String value) {
    return add(property, "!" + value);
  }

  /**
   * Add a matching property to this object.
   *
   * @param property The property of block states, represented as string
   * @param value    The value of this property, represented as a {@link StringIdentifiable} object.
   * @return The object itself.
   */

  @Contract("_, _->this")
  public JWhenProperties add(String property, StringIdentifiable value) {
    return add(property, value.asString());
  }

  /**
   * Add a negated property to this object.
   *
   * @param property The property of block states, represented as string
   * @param value    The value of this property, represented as a {@link StringIdentifiable} object.
   * @return The object itself.
   */

  @Contract("_, _->this")
  public JWhenProperties addNegated(String property, StringIdentifiable value) {
    return add(property, "!" + value.asString());
  }

  /**
   * Add a matching property to this object, with multiple acceptable values given.
   *
   * @param property The property of block states, represented as string
   * @param values   The values of this property, representing that each one of the values will be matched. In this case, they will be joined with {@code "|"}.
   * @return The object itself.
   */

  @Contract("_, _->this")
  public JWhenProperties add(String property, String... values) {
    return add(property, String.join("|", values));
  }

  /**
   * Add a negated property to this object, with multiple negated values given.
   *
   * @param property The property of block states, represented as string
   * @param values   The values of this property, representing that each one of the values will be negated. In this case, they will be joined with {@code "|"}.
   * @return The object itself.
   */

  @Contract("_, _->this")
  public JWhenProperties addNegated(String property, String... values) {
    return add(property, "!" + String.join("|", values));
  }

  /**
   * Add a matching property to this object, with multiple acceptable values given.
   *
   * @param property The property of block states, represented as string
   * @param values   The values of this property, representing that each one of the values will be matched. In this case, they will be joined with {@code "|"}.
   * @return The object itself.
   */

  @Contract("_, _->this")
  public JWhenProperties add(String property, StringIdentifiable... values) {
    return add(property, Arrays.stream(values).map(StringIdentifiable::asString).collect(Collectors.joining("|")));
  }

  /**
   * Add a negated property to this object, with multiple negated values given.
   *
   * @param property The property of block states, represented as string
   * @param values   The values of this property, representing that each one of the values will be negated. In this case, they will be joined with {@code "|"}.
   * @return The object itself.
   */

  @Contract("_, _->this")
  public JWhenProperties addNegated(String property, StringIdentifiable... values) {
    return add(property, "!" + Arrays.stream(values).map(StringIdentifiable::asString).collect(Collectors.joining("|")));
  }
}
