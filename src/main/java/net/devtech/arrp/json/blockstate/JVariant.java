package net.devtech.arrp.json.blockstate;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.JsonSerializable;
import net.fabricmc.api.EnvType;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A variant file in a block state definition. A simple example can be:
 * <pre>
 *   {
 *     "snowy=false": {"model": "block/grass_block"},
 *     "snowy=true": {"model": "block/grass_block_snowy"}
 *   }
 * </pre>
 *
 * @deprecated use {@link JVariants}
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
@PreferredEnvironment(EnvType.CLIENT)
public final class JVariant implements Cloneable, JsonSerializable {
  public final Map<String, JBlockModel> models = new HashMap<>();

  public JVariant() {
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JVariant put(String key, JBlockModel model) {
    this.models.put(key, model);
    return this;
  }

  /**
   * boolean block properties
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _, _ -> this", mutates = "this")
  public JVariant put(String property, boolean value, JBlockModel builder) {
    this.models.put(property + '=' + value, builder);
    return this;
  }

  /**
   * int block properties
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _, _ -> this", mutates = "this")
  public JVariant put(String property, int value, JBlockModel builder) {
    this.models.put(property + '=' + value, builder);
    return this;
  }

  /**
   * other block properties
   *
   * @see Direction
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _, _ -> this", mutates = "this")
  public JVariant put(String property, StringIdentifiable value, JBlockModel builder) {
    this.models.put(property + '=' + value.asString(), builder);
    return this;
  }

  /**
   * everything else
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _, _ -> this", mutates = "this")
  public JVariant put(String property, String value, JBlockModel builder) {
    this.models.put(property + '=' + value, builder);
    return this;
  }

  @Override
  public JVariant clone() {
    try {
      return (JVariant) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    models.forEach((s, m) -> object.add(s, context.serialize(m)));
    return object;
  }
}
