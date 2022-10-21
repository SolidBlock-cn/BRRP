package net.devtech.arrp.json.models;

import net.devtech.arrp.util.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.JsonSerializable;
import net.fabricmc.api.EnvType;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.EnumMap;

/**
 * <p>Specifies the translation, rotation and scalesof a models in different {@link DisplayPosition positions}.</p><p>
 * It's actually a map, whose key is a {@link DisplayPosition display position} (like {@link DisplayPosition#FIRSTPERSON_LEFTHAND "firstperson_lefthand"} pr {@link DisplayPosition#FIXED "fixed}), and value is the {@link JPosition} specifying the rotation, translation and scale.
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
 *   .setThirdperson_righthand(new JPosition()
 *     .rotation(-90, 0, 0)
 *     .translation(0, 1, -3)
 *     .scale(0.55, 0.55, 0.55))
 *   .setFirstperson_lefthand(new JPosition()
 *     .rotation(0, -135, 25)
 *     .translation(0, 4, 2)
 *     .scale(1.7, 1.7, 1.7))
 * }</pre>
 * <p>by the way, the {@link JModel} has a {@link JModel#addDisplay(DisplayPosition, JPosition)} method, which can be used to quickly set the display without creating the instance. For example:</p>
 * <pre>{@code
 * new JModel()
 *  .addDisplay(THIRDPERSON_RIGHTHAND, new JPosition()
 *    .rotation(-90, 0, 0)
 *    .translation(0, 1, -3)
 *    .scale(0.55, 0.55, 0.55))
 *  .addDisplay(FIRSTPERSON_LEFTHAND, new JPosition()
 *    .rotation(0, -135, 25)
 *    .translation(0, 4, 2)
 *    .scale(1.7, 1.7, 1.7))
 * }</pre>
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
@PreferredEnvironment(EnvType.CLIENT)
public class JDisplay extends EnumMap<JDisplay.DisplayPosition, JPosition> implements Cloneable, JsonSerializable {

  public JDisplay() {
    super(DisplayPosition.class);
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JDisplay setThirdperson_righthand(JPosition thirdperson_righthand) {
    put(DisplayPosition.THIRDPERSON_RIGHTHAND, thirdperson_righthand);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JDisplay setThirdperson_lefthand(JPosition thirdperson_lefthand) {
    put(DisplayPosition.THIRDPERSON_LEFTHAND, thirdperson_lefthand);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JDisplay setFirstperson_righthand(JPosition firstperson_righthand) {
    put(DisplayPosition.FIRSTPERSON_RIGHTHAND, firstperson_righthand);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JDisplay setFirstperson_lefthand(JPosition firstperson_lefthand) {
    put(DisplayPosition.FIRSTPERSON_LEFTHAND, firstperson_lefthand);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JDisplay setGui(JPosition gui) {
    put(DisplayPosition.GUI, gui);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JDisplay setHead(JPosition head) {
    put(DisplayPosition.HEAD, head);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JDisplay setGround(JPosition ground) {
    put(DisplayPosition.GROUND, ground);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JDisplay setFixed(JPosition fixed) {
    put(DisplayPosition.FIXED, fixed);
    return this;
  }

  /**
   * This method quite resembles {@link EnumMap#put(Enum, Object)}, but returns the instance itself, making it possible to chain call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JDisplay set(DisplayPosition displayPosition, JPosition position) {
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

  /**
   * @see net.minecraft.client.render.model.json.ModelTransformation.Mode
   */
  public enum DisplayPosition implements StringIdentifiable {
    THIRDPERSON_RIGHTHAND("thirdperson_righthand"),
    THIRDPERSON_LEFTHAND("thirdperson_lefthand"),
    FIRSTPERSON_RIGHTHAND("firstperson_righthand"),
    FIRSTPERSON_LEFTHAND("firstperson_lefthand"),
    GUI("gui"),
    HEAD("head"),
    GROUND("ground"),
    FIXED("fixed");

    private final String name;

    DisplayPosition(String name) {
      this.name = name;
    }

    @Override
    public String asString() {
      return name;
    }
  }
}
