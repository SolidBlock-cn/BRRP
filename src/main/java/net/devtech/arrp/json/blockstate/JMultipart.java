package net.devtech.arrp.json.blockstate;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An entry of the {@link JBlockStates#multiparts multipart} field of a {@link JBlockStates}. Note: this class represents an entry, not a list of it.<br>
 * The multipart entry consists of the following fields: <ul>
 * <li><b>{@code apply}</b> - The {@link JBlockModel block model definition} that will be used, or a list of it to randomly choose.</li>
 * <li><b>{@code when}</b> - The {@link JWhen condition} that the part will be used. Optional.</li>
 * </ul>
 */
public class JMultipart implements Cloneable, JsonSerializable {
  // one or list
  public final List<JBlockModel> apply;
  public JWhen when;

  public JMultipart() {
    this.apply = new ArrayList<>();
  }

  /**
   * Specify a simplest situation of a part, which will always be used (or always choose a random one from the array).
   */
  public JMultipart(JBlockModel... apply) {
    this.apply = Arrays.asList(apply);
  }

  public JMultipart(List<JBlockModel> apply) {
    this.apply = apply;
  }

  /**
   * Specify a conditioned situation of a part, which will be used when the condition is met.
   */
  public JMultipart(JWhen when, JBlockModel... apply) {
    this(apply);
    this.when = when;
  }

  @Override
  public JMultipart clone() {
    try {
      return (JMultipart) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @CanIgnoreReturnValue
  @Contract("_ -> this")
  public JMultipart when(JWhen when) {
    this.when = when;
    return this;
  }

  /**
   * Add a model in the {@link #apply} list.<br>
   * If the list contains multiple elements, Minecraft will choose a random one in it when rendering.
   */
  @CanIgnoreReturnValue
  @Contract("_ -> this")
  public JMultipart addModel(JBlockModel model) {
    this.apply.add(model);
    return this;
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    JsonObject obj = new JsonObject();
    if (this.apply.size() == 1) {
      obj.add("apply", context.serialize(this.apply.get(0)));
    } else {
      obj.add("apply", context.serialize(this.apply));
    }
    obj.add("when", context.serialize(this.when));
    return obj;
  }
}
