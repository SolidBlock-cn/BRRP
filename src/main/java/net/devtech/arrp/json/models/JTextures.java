package net.devtech.arrp.json.models;

import com.google.common.collect.ForwardingMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.JsonSerializable;
import net.fabricmc.api.EnvType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Specifies the model texture variables of the model. The key is the name of the texture variable (not prefixed by {@code #}), and the value is the identifier of the texture (for example, {@code minecraft:block/lava_still}, or another texture variable (prefixed by {@code #})).
 * <p>The field {@link #textures} exists only for compatibility. It equals to the JTextures itself.
 * <p>As inherited in {@link Map}, methods like {@code put}, {@code putAll} do not return the object itself. However, new methods like {@code var} and {@code particle} return the object itself, making it possible to chain call.
 * <p>You can simply call {@link #of(String, String)} or {@link #of(String...)} to quickly create an instance with one or several variables defined.
 *
 * @since 0.8.0 It is cloneable now.
 */
@SuppressWarnings("unused")
@PreferredEnvironment(EnvType.CLIENT)
public class JTextures extends ForwardingMap<String, String> implements Cloneable, JsonSerializable {
  /**
   * The map containing the values. It is usually a {@link LinkedHashMap}, as specified in {@link #JTextures()}.<p>
   * This field it private. To get it please use {@link #delegate()}.
   */
  private final Map<String, String> textures;

  /**
   * This is the basic constructor method. However, if you want to add some texture variables after constructing, you may directly use {@link #of}, {@link #ofAll}, {@link #ofLayer0} and {@link #ofSides}.
   */
  public JTextures() {
    this(new LinkedHashMap<>());
  }

  /**
   * Create a {@code JTextures} object with custom texture map specified. If you're just considering creating an empty mutable one, you can call {@link #JTextures()}.
   *
   * @param textures The map of textures. It can be <i>immutable</i>; in this case, methods such as {@link #var} should not be used, or {@link UnsupportedOperationException} will be thrown.
   */
  public JTextures(Map<String, String> textures) {
    this.textures = textures;
  }

  /**
   * <p>Conveniently create an instance with one texture variable defined. </p>
   * <p>For example,</p>
   * <pre>{@code new JTextures().var("name", "var")}</pre>
   * <p>is identical to:</p>
   * <pre>{@code JTextures.of("name", "var)}</pre>
   * <p>If you want to specify multiple texture variables, you can call {@link #var(String, String)} or {@link #vars(String...)} to the instance, or just use {@link #of(String...)}. If the texture variable you add is named {@code "all"}, you can directly call {@link #ofAll(String)}; if named {@code "layer0"}, you can directly call {@link #ofLayer0(String)}.</p>
   */
  @Contract("_, _ -> new")
  public static JTextures of(String name, String val) {
    return new JTextures().var(name, val);
  }

  /**
   * <p>Conveniently create an instance with multiple texture variables defined.</p>
   * <p>If you want to specify {@code "top"}, {@code "side"} and {@code "bottom"} at one time, you can see {@link #ofSides(String, String, String)}.</p>
   */
  @Contract("_ -> new")
  public static JTextures of(String... strings) {
    return new JTextures().vars(strings);
  }

  /**
   * Quickly creates an instance with variable {@code "all"} specified.<br>
   * The following codes are identical:
   * <pre>{@code
   * new JTextures().var ("all", "block/stone");
   * JTextures.of        ("all", "block/stone");
   * new JTextures().all ("block/stone");
   * JTextures.ofAll     ("block/stone");
   * }</pre>
   * It's obvious that the last one is the most convenient.
   *
   * @see #all(String)
   * @see #of(String, String)
   */
  @Contract("_ -> new")
  public static JTextures ofAll(String all) {
    return new JTextures().all(all);
  }

  /**
   * <p>Quickly creates an instance with variable {@code "top"}, {@code "side"} and {@code "bottom"} specified. Many models use this set of variables, for instance, models based on {@code "minecraft:block/stairs"}.</p>
   * <p>This static method is a convenient way to construct a new JTextures object and add these three variables to it.</p>
   * <p>The following codes are identical:</p>
   * <pre>{@code
   * new JTextures().var ("top",    "block/sandstone_top")
   *                .var ("side",   "block/sandstone")
   *                .var ("bottom", "block/sandstone_bottom")
   * new JTextures().vars("top",    "block/sandstone_top",
   *                      "side",   "block/sandstone",
   *                      "bottom", "block/sandstone_bottom")
   * JTextures.of        ("top",    "block/sandstone_top",
   *                      "side",   "block/sandstone",
   *                      "bottom", "block/sandstone_bottom")
   * new JTextures().sides         ("block/sandstone_top",
   *                                "block/sandstone",
   *                                "block/sandstone_bottom")
   * JTextures.ofSides             ("block/sandstone_top",
   *                                "block/sandstone",
   *                                "block/sandstone_bottom")
   * }</pre>
   *
   * @see #sides(String, String, String)
   */
  @Contract("_, _, _ -> new")
  public static JTextures ofSides(String top, String side, String bottom) {
    return new JTextures().sides(top, side, bottom);
  }

  /**
   * Quickly creates an instance with variable {@code "layer0"} specified. This is usually used by models based on {@code "minecraft:item/generated"}.
   *
   * @see #layer0(String)
   */
  @Contract("_ -> new")
  public static JTextures ofLayer0(String layer0) {
    return new JTextures().layer0(layer0);
  }

  @Override
  public @NotNull Map<String, String> delegate() {
    return textures;
  }

  /**
   * <p>Add a texture variable. If you want to create a new textures instance with one variable created, you may call {@link #of(String, String)}. If you want to add multiple texture variables, you can chain call this method, or just call {@link #vars(String...)}.</p>
   * <p>This method is a bit like {@link Map#put(Object, Object)}, but it returns the instance itself.</p>
   * <p>For convenient, for some frequently used names, you can call their own corresponding methods. For example, {@link #all(String) all}{@code ("block/stone")} can be seen as a simplified version for {@code var("all", "block/stone")}.</p>
   *
   * @param name The name of the texture variable.
   * @param val  The identifier of the texture (for example {@code "minecraft:block/lava_still"}) or another texture variable (for example {@code "#top"}).
   * @see #of(String, String)
   * @see #all(String)
   * @see #particle(String)
   * @see #layer0(String)
   * @see #vars(String...)
   */
  @Contract(value = "_, _ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JTextures var(String name, String val) {
    put(name, val);
    return this;
  }

  /**
   * <p>Add multiple texture variables. This is a convenient way. You have to make sure the strings are even number.</p>
   * <p>For example:</p>
   * <pre>{@code
   * jTexture.var("name1", "val1")
   *         .var("name2", "val2")
   *         .var("name3", "val3")
   * }</pre>
   * is identical to:
   * <pre>{@code
   * jTexture.vars("name1", "val1",
   *               "name2", "val2",
   *               "name3", "val3")
   * }</pre>
   * <p>If you are adding one texture variable, it's better call {@link #var(String, String)}.</p>
   * <p>You can also directly call {@link #of(String...)} if you want to construct a new JTextures object with multiple textures defined. For textures named {@code "top"}, {@code "side"} and {@code "bottom"}, you can see {@link #sides} and {@link #ofSides}.</p>
   *
   * @param strings The texture name and values.
   * @see #var(String, String)
   * @see #sides(String, String, String)
   * @see #of(String...)
   */
  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JTextures vars(String... strings) {
    for (int i = 0; i < strings.length; i += 2) {
      final String name = strings[i];
      final String val = strings[i + 1];
      put(name, val);
    }
    return this;
  }

  /**
   * Adds a variable {@code "all"} specified. This is usually used models based on {@code "minecraft:block/cube_all"}. You can also directly call {@link #ofAll(String)} to create an instance with these three variables specified.
   *
   * @see #ofAll(String)
   */
  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JTextures all(String all) {
    return var("all", all);
  }

  /**
   * <p>Quickly adds variables {@code "top"}, {@code "side"} and {@code "bottom"}. You can also directly call {@link #ofSides(String, String, String)} to create an instance with these three variables specified.</p>
   *
   * @see #ofSides(String, String, String)
   */
  @Contract(value = "_, _, _ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JTextures sides(String top, String side, String bottom) {
    return var("top", top).var("side", side).var("bottom", bottom);
  }

  /**
   * Add the {@code "particle"} texture variable. Identical to {@link #var}{@code ("particle", val)}.
   */
  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JTextures particle(String val) {
    put("particle", val);
    return this;
  }

  /**
   * Add the {@code "layer0"} texture variable. Identical to {@link #var}{@code ("layer0", val)}.
   *
   * @see #ofLayer0(String)
   */
  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JTextures layer0(String val) {
    put("layer0", val);
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JTextures layer1(String val) {
    put("layer1", val);
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JTextures layer2(String val) {
    put("layer2", val);
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JTextures layer3(String val) {
    put("layer3", val);
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JTextures layer4(String val) {
    put("layer4", val);
    return this;
  }

  @Override
  public JTextures clone() {
    try {
      return (JTextures) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    forEach(json::addProperty);
    return json;
  }
}
