package net.devtech.arrp.json.loot;

import com.google.gson.*;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

/**
 * A condition in a loot table is called "predicate"
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

  public static JCondition ofAlternative(Collection<JCondition> conditions) {
    final JCondition result = new JCondition("alternative");
    result.parameters.add("terms", RuntimeResourcePackImpl.GSON.toJsonTree(conditions));
    return result;
  }

  public static JCondition ofAlternative(JCondition... conditions) {
    return ofAlternative(Arrays.asList(conditions));
  }


  public JCondition condition(String condition) {
    this.condition = condition;
    return this;
  }


  public JCondition set(JsonObject parameters) {
    parameters.addProperty("condition", this.parameters.get("condition").getAsString());
    this.parameters = parameters;
    return this;
  }


  public JCondition parameter(String key, Number value) {
    return parameter(key, new JsonPrimitive(value));
  }


  public JCondition parameter(String key, JsonElement value) {
    this.parameters.add(key, value);
    return this;
  }


  public JCondition parameter(String key, Boolean value) {
    return parameter(key, new JsonPrimitive(value));
  }


  public JCondition parameter(String key, Character value) {
    return parameter(key, new JsonPrimitive(value));
  }


  public JCondition parameter(String key, Identifier value) {
    return parameter(key, value.toString());
  }


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

  private static final class Delegate extends JCondition {
    private static final Gson GSON = LootGsons.getConditionGsonBuilder().create();
    private final LootCondition delegate;

    private Delegate(LootCondition delegate) {
      this.delegate = delegate;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return GSON.toJsonTree(delegate);
    }
  }
}
