package net.devtech.arrp.json.blockstate;

import net.devtech.arrp.util.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.JsonSerializable;
import net.fabricmc.api.EnvType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Please use {@link JBlockStates}, which is an improved version.
 */
@SuppressWarnings({"DeprecatedIsStillUsed", "unused"})
@Deprecated
@PreferredEnvironment(EnvType.CLIENT)
public final class JState implements JsonSerializable {
  final List<JVariant> variants = new ArrayList<>();
  final List<JMultipart> multiparts = new ArrayList<>();

  public JState() {
  }

  @Deprecated
  public static JState state() {
    return new JState();
  }

  /**
   * @deprecated use {@link JBlockStates#ofVariants(JVariants)}
   */
  @Deprecated
  public static JState state(JVariant... variants) {
    JState state = new JState();
    for (JVariant variant : variants) {
      state.add(variant);
    }
    return state;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JState add(JVariant variant) {
    if (!this.multiparts.isEmpty()) {
      throw new IllegalStateException("BlockStates can only have variants *or* multiparts, not both");
    }
    this.variants.add(variant);
    return this;
  }

  /**
   * @deprecated use {@link JBlockStates#ofMultiparts(JMultipart...)}
   */
  @Deprecated
  public static JState state(JMultipart... parts) {
    JState state = new JState();
    for (JMultipart part : parts) {
      state.add(part);
    }
    return state;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JState add(JMultipart multiparts) {
    if (!this.variants.isEmpty()) {
      throw new IllegalStateException("BlockStates can only have variants *or* multiparts, not both");
    }
    this.multiparts.add(multiparts);
    return this;
  }

  /**
   * @deprecated Please directly use {@link JVariant#JVariant()}
   */
  @Deprecated
  public static JVariant variant() {
    return new JVariant();
  }

  @Deprecated
  public static JVariant variant(JBlockModel model) {
    JVariant variant = new JVariant();
    variant.put("", model);
    return variant;
  }

  /**
   * @deprecated Please directly use the constructor method {@link JBlockModel#JBlockModel()}.
   */
  @Deprecated
  public static JBlockModel model(String id) {
    return new JBlockModel(id);
  }

  /**
   * @deprecated Please directly use the constructor method {@link JBlockModel#JBlockModel()}.
   */
  @Deprecated
  public static JBlockModel model(Identifier id) {
    return new JBlockModel(id);
  }

  /**
   * @deprecated Please directly use the constructor method {@link JMultipart }.
   */
  @Deprecated
  public static JMultipart multipart(JBlockModel... models) {
    JMultipart multipart = new JMultipart();
    for (JBlockModel model : models) {
      multipart.addModel(model);
    }
    return multipart;
  }

  /**
   * @deprecated Please directly use the constructor method {@link JWhen}.
   */
  @Deprecated
  public static JWhen when() {
    return new JWhen();
  }

  /**
   * @author Devan Kerman
   */
  public static JWhen.StateBuilder whenStateBuilder() {
    return new JWhen.StateBuilder();
  }

  @Deprecated
  @Override
  public JState clone() {
    try {
      return (JState) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    if (!this.variants.isEmpty()) {
      if (this.variants.size() == 1) {
        json.add("variants", context.serialize(this.variants.get(0)));
      } else {
        json.add("variants", context.serialize(this.variants));
      }
    }
    if (!this.multiparts.isEmpty()) {
      json.add("multipart", context.serialize(this.multiparts));
    }
    return json;
  }
}
