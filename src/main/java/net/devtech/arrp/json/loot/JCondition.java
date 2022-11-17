package net.devtech.arrp.json.loot;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.*;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

/**
 * A <b>loot table condition</b> is also called a "predicate". It is complicated so <b>it is highly recommended to directly use {@link #delegate(LootCondition.Builder)} to use vanilla-type loot table conditions</b>.
 *
 * @see LootCondition
 */
@SuppressWarnings("unused")
public class JCondition implements Cloneable, JsonSerializable {
  /**
   * The id (as string) of the condition.
   */
  public String condition;
  public JsonObject parameters;

  public JCondition(String condition, JsonObject parameters) {
    this.condition = condition;
    this.parameters = parameters;
  }

  public JCondition(String condition) {
    this(condition, new JsonObject());
  }

  public JCondition() {
    this(new JsonObject());
  }

  public JCondition(JsonObject parameters) {
    this.parameters = parameters;
    if (parameters.has("condition")) this.condition = parameters.get("condition").getAsString();
  }

  /**
   * @see LootCondition.Builder#or(LootCondition.Builder)
   */
  @Contract("_ -> new")
  public static JCondition ofAlternative(Iterable<JCondition> conditions) {
    final JCondition result = new JCondition("alternative");
    result.parameters.add("terms", RuntimeResourcePackImpl.GSON.toJsonTree(conditions));
    return result;
  }

  /**
   * This method is kept just for compatibility.
   */
  @Contract(value = "_ -> new", pure = true)
  public static JCondition ofAlternative(Collection<JCondition> conditions) {
    return ofAlternative((Iterable<JCondition>) conditions);
  }

  /**
   * @see LootCondition.Builder#or(LootCondition.Builder)
   */
  @Contract(value = "_ -> new", pure = true)
  public static JCondition ofAlternative(JCondition... conditions) {
    return ofAlternative(Arrays.asList(conditions));
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> new", mutates = "this")
  public JCondition condition(String condition) {
    this.condition = condition;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> new", mutates = "this")
  public JCondition set(JsonObject parameters) {
    parameters.addProperty("condition", this.parameters.get("condition").getAsString());
    this.parameters = parameters;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JCondition parameter(String key, Number value) {
    return parameter(key, new JsonPrimitive(value));
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JCondition parameter(String key, JsonElement value) {
    this.parameters.add(key, value);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JCondition parameter(String key, Boolean value) {
    return parameter(key, new JsonPrimitive(value));
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JCondition parameter(String key, Character value) {
    return parameter(key, new JsonPrimitive(value));
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JCondition parameter(String key, Identifier value) {
    return parameter(key, value.toString());
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JCondition parameter(String key, String value) {
    return parameter(key, new JsonPrimitive(value));
  }

  /**
   * "or"'s the conditions together
   *
   * @deprecated Please use {@link #ofAlternative}.
   */
  @Deprecated
  public JCondition alternative(JCondition... conditions) {
    JsonArray array = new JsonArray();
    for (JCondition condition : conditions) {
      array.add(RuntimeResourcePackImpl.GSON.toJsonTree(condition));
    }
    this.parameters.add("terms", array);
    return this;
  }

  @Override
  public JCondition clone() {
    try {
      return (JCondition) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject parameters = this.parameters;
    parameters.addProperty("condition", condition);
    return parameters;
  }

  /**
   * @deprecated This class is kept for compatibility.
   */
  @Deprecated
  public static class Serializer implements JsonSerializer<JCondition> {
    @Override
    public JsonElement serialize(JCondition src, Type typeOfSrc, JsonSerializationContext context) {
      return src.serialize(typeOfSrc, context);
    }
  }

  /**
   * Create an object directly from vanilla loot condition. In this case, methods such as {@link #parameter} should <i>not</i> be used.
   *
   * @param vanillaCondition A vanilla loot condition object.
   * @return A new JCondition object that directly uses the serialization of vanilla loot condition.
   */
  @ApiStatus.AvailableSince("0.8.0")
  @Contract(value = "_ -> new", pure = true)
  public static JCondition delegate(LootCondition vanillaCondition) {
    return new Delegate(vanillaCondition);
  }

  /**
   * Create an object directly from vanilla loot condition. In this case, methods such as {@link #parameter} should <i>not</i> be used.
   *
   * @param vanillaCondition A vanilla loot condition object.
   * @return A new JCondition object that directly uses the serialization of vanilla loot condition.
   */
  @ApiStatus.AvailableSince("0.8.0")
  @Contract(value = "_ -> new", pure = true)
  public static JCondition delegate(LootCondition.Builder vanillaCondition) {
    return new Delegate(vanillaCondition.build());
  }

  @ApiStatus.Internal
  private static final class Delegate extends JCondition {
    private static final Gson GSON = LootGsons.getConditionGsonBuilder().create();
    private final LootCondition delegate;

    private Delegate(LootCondition delegate) {
      super(null, null);
      this.delegate = delegate;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return GSON.toJsonTree(delegate);
    }
  }
}
