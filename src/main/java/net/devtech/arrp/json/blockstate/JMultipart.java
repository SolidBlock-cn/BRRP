package net.devtech.arrp.json.blockstate;

import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.annotations.SerializedName;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.data.client.When;
import net.minecraftforge.api.distmarker.Dist;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>An entry of the {@link JBlockStates#multiparts multipart} field of a {@link JBlockStates}. Note: this class represents an entry, not a list of it.</p>
 * <p>The multipart entry consists of the following fields: </p><ul>
 * <li><b>{@code apply}</b> - The {@link JBlockModel block model definition} that will be used, or a list of it to randomly choose.</li>
 * <li><b>{@code when}</b> - The {@link When condition} that the part will be used. Optional.</li>
 * </ul>
 *
 * @see net.minecraft.data.client.model.MultipartBlockStateSupplier
 * @see net.minecraft.client.render.model.MultipartBakedModel
 * @see net.minecraft.client.render.model.MultipartUnbakedModel
 * @see net.minecraft.client.render.model.json.MultipartModelSelector
 * @see net.minecraft.client.render.model.json.MultipartModelComponent
 */
@SuppressWarnings("unused")
@PreferredEnvironment(Dist.CLIENT)
public class JMultipart implements Cloneable, JsonSerializable {
  /**
   * The model that will be used, if the condition is met. It can be one of multiple block models.
   */
  public final List<JBlockModel> apply;
  @SerializedName("when")
  public When condition;

  /**
   * Create an empty multipart object, whose models are empty and condition is null. If you have some models to be initialized, you can use {@link #JMultipart(JBlockModel...)}, or {@link #JMultipart(When, JBlockModel...)} with condition specified.
   */
  public JMultipart() {
    this.apply = new ArrayList<>();
  }

  /**
   * Specify the simplest situation of a part, which will always be used (or always choose a random one from the array). If you want to specify a condition, you can call {@link #when(When)}, or directly use {@link #JMultipart(When, JBlockModel...)}.
   */
  public JMultipart(JBlockModel... apply) {
    this.apply = Lists.newArrayList(apply);
  }

  /**
   * Create a simple multipart object. The parameter {@code apply} will be directly used. It's usually not recommended to call this method, unless in some specific situations, for example you want multiple objects share a same one, or make it immutable.
   *
   * @param apply The list of block models. If you're just initializing it without other specifications, you should use {@link #JMultipart()} or {@link #JMultipart(JBlockModel...)}.
   */
  @ApiStatus.Internal
  public JMultipart(List<JBlockModel> apply) {
    this.apply = apply;
  }

  /**
   * Specify a conditioned situation of a part, which will be used when the condition is met.
   */
  public JMultipart(When when, JBlockModel... apply) {
    this(apply);
    this.condition = when;
  }

  @Override
  public JMultipart clone() {
    try {
      return (JMultipart) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  /**
   * Set the condition of the multipart object. If a condition is already specified, it will be overridden.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JMultipart when(When when) {
    this.condition = when;
    return this;
  }

  /**
   * Add a model in the {@link #apply} list.<br>
   * If the list contains multiple elements, Minecraft will choose a random one in it when rendering.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
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
    obj.add("when", context.serialize(this.condition));
    return obj;
  }
}
