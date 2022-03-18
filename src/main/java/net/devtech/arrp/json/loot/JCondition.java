package net.devtech.arrp.json.loot;

import com.google.gson.*;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;


public class JCondition implements Cloneable, JsonSerializable {
  public JsonObject parameters = new JsonObject();

  public JCondition(String condition) {
    if (condition != null) {
      this.condition(condition);
    }
  }

  public JCondition() {
    this(new JsonObject());
  }

  public JCondition(JsonObject parameters) {
    this.parameters = parameters;
  }

  public JCondition condition(String condition) {
    this.parameters.addProperty("condition", condition);
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
   */
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
    return parameters;
  }
}
