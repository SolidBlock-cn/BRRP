package net.devtech.arrp.json.models;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.JsonSerializable;
import net.fabricmc.api.EnvType;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.EnumMap;

/**
 * <p>Specifies the translation, rotation and scales of a models in different {@link ModelTransformationMode model transformation mode}.</p><p>
 * It's actually a map, whose key is a {@link ModelTransformationMode model transformation mode} (like {@link ModelTransformationMode#FIRST_PERSON_LEFT_HAND "firstperson_lefthand"} pr {@link ModelTransformationMode#FIXED "fixed}), and value is the {@link JPosition} specifying the rotation, translation and scale.
 * </p>
 * <p>For example, the display of a torch is:</p>
 * <pre>{@code
 * {
 *   "thirdperson_righthand": {
 *     "rotation": [ -90, 0, 0 ],
 *     "translation": [ 0, 1, -3 ],
 *     "scale": [ 0.55, 0.55, 0.55 ]
 *   },
 *   "firstperson_lefthand": {
 *     "rotation": [ 0, -135, 25 ],
 *     "translation": [ 0, 4, 2 ],
 *     "scale": [ 1.7, 1.7, 1.7 ]
 *   }
 * }
 * }</pre>
 * <p>which can be defined as</p>
 * <pre>{@code
 * new JDisplay()
 *   .set(ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, new JPosition()
 *     .rotation(-90, 0, 0)
 *     .translation(0, 1, -3)
 *     .scale(0.55, 0.55, 0.55))
 *   .set(ModelTransformationMode.FIRST_PERSON_LEFT_HAND, new JPosition()
 *     .rotation(0, -135, 25)
 *     .translation(0, 4, 2)
 *     .scale(1.7, 1.7, 1.7))
 * }</pre>
 * <p>by the way, the {@link JModel} has a {@link JModel#addDisplay(ModelTransformationMode, JPosition)} method, which can be used to quickly set the display without creating the instance. For example:</p>
 * <pre>{@code
 * new JModel()
 *  .addDisplay(ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, new JPosition()
 *    .rotation(-90, 0, 0)
 *    .translation(0, 1, -3)
 *    .scale(0.55, 0.55, 0.55))
 *  .addDisplay(ModelTransformationMode.FIRST_PERSON_LEFT_HAND, new JPosition()
 *    .rotation(0, -135, 25)
 *    .translation(0, 4, 2)
 *    .scale(1.7, 1.7, 1.7))
 * }</pre>
 */
@SuppressWarnings("unused")
@PreferredEnvironment(EnvType.CLIENT)
public class JDisplay extends EnumMap<ModelTransformationMode, JPosition> implements Cloneable, JsonSerializable {

  public JDisplay() {
    super(ModelTransformationMode.class);
  }

  /**
   * This method quite resembles {@link EnumMap#put(Enum, Object)}, but returns the instance itself, making it possible to chain call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JDisplay set(ModelTransformationMode displayPosition, JPosition position) {
    put(displayPosition, position);
    return this;
  }

  @Override
  public JDisplay clone() {
    return (JDisplay) super.clone();
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject object = new JsonObject();
    forEach((key, value) -> object.add(key.asString(), context.serialize(value)));
    return object;
  }
}
