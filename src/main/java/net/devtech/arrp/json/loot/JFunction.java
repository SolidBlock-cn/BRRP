package net.devtech.arrp.json.loot;


import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.*;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>The <b>loot table function</b> is in essence an "item modifier". The field "function" is the identifier (as string) of the loot table function.</p>
 * <p>The loot table function is quite complicated, so <b>it is highly recommended to directly use {@link #delegate(LootFunction.Builder)} or {@link #delegate(LootFunction)} to use vanilla loot functions</b>.</p>
 *
 * @see LootFunction
 */
public class JFunction implements Cloneable, JsonSerializable {
  public List<JCondition> conditions;
  /**
   * The name of the loot table function. Possible values can be seen in {@link net.minecraft.loot.function.LootFunctionTypes}.
   */
  public String function;
  public JsonObject properties = new JsonObject();

  public JFunction(String function) {
    function(function);
  }

  /**
   * Create an object that directly uses the serialization of a vanilla-type {@link LootFunction} object.
   */
  @Contract("_ -> new")
  public static JFunction delegate(LootFunction delegate) {
    return new FromLootFunction(delegate);
  }

  /**
   * Create an object that directly uses the serialization of a vanilla-type {@link LootFunction.Builder} object.
   */
  @Contract("_ -> new")
  @ApiStatus.AvailableSince("0.8.0")
  public static JFunction delegate(LootFunction.Builder delegate) {
    return new FromLootFunctionBuilder(delegate);
  }

  /**
   * Set the name of the loot table function.
   *
   * @param function The function name, which is an identifier (as string).
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JFunction function(String function) {
    this.function = function;
    return this;
  }

  /**
   * Set the name of the loot table function.
   *
   * @param function The function name, which is an identifier.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.2")
  public JFunction function(Identifier function) {
    return function(function.toString());
  }

  /**
   * Set the name of the loot table function.
   *
   * @param function The loot function type, the id of which will be used.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.2")
  public JFunction function(LootFunctionType function) {
    return function(Objects.requireNonNull(Registries.LOOT_FUNCTION_TYPE.getId(function), "The loot function type is not registered."));
  }

  /**
   * Set all properties of the function, overriding existing ones, except {@link #function} and {@link #conditions}.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JFunction set(JsonObject properties) {
    this.properties = properties;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JFunction parameter(String key, JsonElement value) {
    this.properties.add(key, value);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JFunction parameter(String key, String value) {
    return parameter(key, new JsonPrimitive(value));
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JFunction parameter(String key, Number value) {
    return parameter(key, new JsonPrimitive(value));
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JFunction parameter(String key, Boolean value) {
    return parameter(key, new JsonPrimitive(value));
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JFunction parameter(String key, Identifier value) {
    return parameter(key, value.toString());
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JFunction parameter(String key, Character value) {
    return parameter(key, new JsonPrimitive(value));
  }

  /**
   * Add a condition to the function.
   *
   * @param condition The loot table condition.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JFunction condition(JCondition condition) {
    if (conditions == null) this.conditions = new ArrayList<>();
    this.conditions.add(condition);
    return this;
  }

  /**
   * Add a condition to the function.
   *
   * @see JFunction#condition(JCondition)
   * @deprecated unintuitive name
   */
  @Deprecated
  public JFunction add(JCondition condition) {
    return condition(condition);
  }

  @Override
  public JFunction clone() {
    try {
      return (JFunction) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    if (conditions != null) {
      properties.add("conditions", context.serialize(conditions));
    }
    if (function != null) {
      properties.addProperty("function", function);
    }
    return properties;
  }

  /**
   * This class is kept for compatibility.
   *
   * @deprecated
   */
  @Deprecated
  public static class Serializer implements JsonSerializer<JFunction> {
    @Override
    public JsonElement serialize(JFunction src, Type typeOfSrc, JsonSerializationContext context) {
      return src.serialize(typeOfSrc, context);
    }
  }

  /**
   * @author SolidBlock
   * @since 0.8.0 converted from an anonymous class to an internal class, avoiding absence with GSON serialization.
   */
  @ApiStatus.AvailableSince("0.8.0")
  @ApiStatus.Internal
  private static class FromLootFunction extends JFunction {
    private static final Gson GSON = LootGsons.getFunctionGsonBuilder().create();
    private final LootFunction delegate;

    public FromLootFunction(LootFunction delegate) {
      super(null);
      this.delegate = delegate;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return GSON.toJsonTree(delegate);
    }
  }

  @ApiStatus.AvailableSince("0.8.0")
  @ApiStatus.Internal
  private static class FromLootFunctionBuilder extends JFunction {
    private final LootFunction.Builder delegate;

    public FromLootFunctionBuilder(LootFunction.Builder delegate) {
      super(null);
      this.delegate = delegate;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return FromLootFunction.GSON.toJsonTree(delegate.build());
    }
  }
}
