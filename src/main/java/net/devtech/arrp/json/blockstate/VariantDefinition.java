package net.devtech.arrp.json.blockstate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JSONSerializable;
import net.minecraft.state.property.Property;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * A simple extended version for {@link JVariant}.<br>
 * It's a simple hash map for the key-value pairs. The key is a string specifying the variant conditions, which can be null, a single or multiple property key-value pairs(s). The value is an array of block model definitions, which can be sometimes a singleton.
 */
@SuppressWarnings("unused")
public class VariantDefinition extends HashMap<String, JBlockModel[]> implements JSONSerializable {
  /**
   * Add a simple variant situation.
   *
   * @param variant         The string describing the whole variant. It can be empty string {@code ""}, or one or more key-value pairs, for example {@code "snowy=false"}, or {@code "facing=south,half=top"}.
   * @param modelDefinition The block model definition.
   */
  public VariantDefinition addVariant(String variant, JBlockModel... modelDefinition) {
    put(variant, modelDefinition);
    return this;
  }

  /**
   * Add a simple variant situation with a property and boolean value. In this method, a single key-value pair is used.
   *
   * @param property        A block state property. Vanilla properties can be found in {@link net.minecraft.state.property.Properties}.
   * @param value           The value that corresponds to the property.
   * @param modelDefinition The block model definition.
   */
  public <T extends Comparable<T>> VariantDefinition addVariant(Property<T> property, T value, JBlockModel... modelDefinition) {
    return addVariant(property.getName() + "=" + value.toString(), modelDefinition);
  }

  /**
   * Simple 'upgrades' the deprecated jVariant to the improved version.
   */
  public static VariantDefinition of(JVariant jVariant) {
    final VariantDefinition instance = new VariantDefinition();
    jVariant.models.forEach((k, v) -> instance.put(k, new JBlockModel[]{v}));
    return instance;
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject object = new JsonObject();
    this.forEach((key, value) -> object.add(key, context.serialize(value.length == 1 ? value[0] : value)));
    return object;
  }
}
