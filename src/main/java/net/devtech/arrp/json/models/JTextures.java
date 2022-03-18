package net.devtech.arrp.json.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Specifies the model texture variables of the model. The key is the name of the texture variable (not prefixed by {@code #}), and the value is the identifier of the texture (for example, {@code minecraft:block/lava_still}, or another texture variable (prefixed by {@code #}).
 * <p>The field {@link #textures} exists only for compatibility. It equals to the JTextures itself.
 * <p>As inherited in {@link Map}, methods like {@code put}, {@code putAll} does not return the object itself. However, new methods like {@code var} and {@code particle} return the object itself, making it possible to chain call.
 */
@SuppressWarnings("unused")
public class JTextures extends LinkedHashMap<String, String> implements JsonSerializable {
  /**
   * This field exists for compatibility.
   */
  @Deprecated
  private final Map<String, String> textures;

  /**
   * @see JModel#textures()
   */
  public JTextures() {
    this.textures = this;
  }

  public JTextures(Map<? extends String, ? extends String> stringMap) {
    this();
    putAll(stringMap);
  }

  /**
   * Add a texture variable.
   *
   * @param name The name of the texture variable.
   * @param val  The identifier of the texture (for example {@code "minecraft:block/lava_still"}) or another texture variable (for example {@code "#top"}).
   */
  public JTextures var(String name, String val) {
    put(name, val);
    return this;
  }

  public static JTextures of(String name, String val) {
    return new JTextures().var(name, val);
  }

  /**
   * Adds a variable {@code "all"} specified. This is usually used models based on {@code "minecraft:block/cube_all"}.
   */
  public JTextures all(String all) {
    return var("all", all);
  }

  /**
   * Quickly creates an instance with variable {@code "all"} specified.
   */
  public static JTextures ofAll(String all) {
    return new JTextures().all(all);
  }

  /**
   * Quickly adds variables {@code "top"}, {@code "side"} and {@code "bottom"}. You can also directly call {@link #ofSides(String, String, String)} to create an instance with these three variables specified.
   */
  public JTextures sides(String top, String side, String bottom) {
    return var("top", top).var("side", side).var("bottom", bottom);
  }

  /**
   * Quickly creates an instance with variable {@code "top"}, {@code "side"} and {@code "bottom"} specified. Many models use this set of variables, for instance, models based on {@code "minecraft:block/stairs"}.
   */
  public JTextures ofSides(String top, String side, String bottom) {
    return new JTextures().sides(top, side, bottom);
  }

  public JTextures particle(String val) {
    put("particle", val);
    return this;
  }

  public JTextures layer0(String val) {
    put("layer0", val);
    return this;
  }

  /**
   * Quickly creates an instance with variable {@code "layer0"} specified. This is usually used by models based on {@code "minecraft:item/generated"}.
   */
  public static JTextures ofLayer0(String layer0) {
    return new JTextures().layer0(layer0);
  }

  public JTextures layer1(String val) {
    put("layer1", val);
    return this;
  }

  public JTextures layer2(String val) {
    put("layer2", val);
    return this;
  }

  public JTextures layer3(String val) {
    put("layer3", val);
    return this;
  }

  public JTextures layer4(String val) {
    put("layer4", val);
    return this;
  }

  @Override
  public JTextures clone() {
    return (JTextures) super.clone();
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    forEach(json::addProperty);
    return json;
  }
}
