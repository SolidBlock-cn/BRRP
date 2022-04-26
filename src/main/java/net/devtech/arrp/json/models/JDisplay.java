package net.devtech.arrp.json.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
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
public class JDisplay extends EnumMap<JDisplay.DisplayPosition, JPosition> implements Cloneable, JsonSerializable {
  // The following fields exist for only compatibility.
  @Deprecated(forRemoval = true)
  private JPosition thirdperson_righthand;
  @Deprecated(forRemoval = true)
  private JPosition thirdperson_lefthand;
  @Deprecated(forRemoval = true)
  private JPosition firstperson_righthand;
  @Deprecated(forRemoval = true)
  private JPosition firstperson_lefthand;
  @Deprecated(forRemoval = true)
  private JPosition gui;
  @Deprecated(forRemoval = true)
  private JPosition head;
  @Deprecated(forRemoval = true)
  private JPosition ground;
  @Deprecated(forRemoval = true)
  private JPosition fixed;

  public JDisplay() {
    super(DisplayPosition.class);
  }

  @Contract("_ -> this")
  public JDisplay setThirdperson_righthand(JPosition thirdperson_righthand) {
    put(DisplayPosition.THIRDPERSON_RIGHTHAND, thirdperson_righthand);
    return this;
  }

  @Contract("_ -> this")
  public JDisplay setThirdperson_lefthand(JPosition thirdperson_lefthand) {
    put(DisplayPosition.THIRDPERSON_LEFTHAND, thirdperson_lefthand);
    return this;
  }

  @Contract("_ -> this")
  public JDisplay setFirstperson_righthand(JPosition firstperson_righthand) {
    put(DisplayPosition.FIRSTPERSON_RIGHTHAND, firstperson_righthand);
    return this;
  }

  @Contract("_ -> this")
  public JDisplay setFirstperson_lefthand(JPosition firstperson_lefthand) {
    put(DisplayPosition.FIRSTPERSON_LEFTHAND, firstperson_lefthand);
    return this;
  }

  @Contract("_ -> this")
  public JDisplay setGui(JPosition gui) {
    put(DisplayPosition.GUI, gui);
    return this;
  }

  @Contract("_ -> this")
  public JDisplay setHead(JPosition head) {
    put(DisplayPosition.HEAD, head);
    return this;
  }

  @Contract("_ -> this")
  public JDisplay setGround(JPosition ground) {
    put(DisplayPosition.GROUND, ground);
    return this;
  }

  @Contract("_ -> this")
  public JDisplay setFixed(JPosition fixed) {
    put(DisplayPosition.FIXED, fixed);
    return this;
  }

  /**
   * This method quite resembles {@link EnumMap#put(Enum, Object)}, but returns the instance itself, making it possible to chain call.
   */
  @Contract("_, _ -> this")
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
