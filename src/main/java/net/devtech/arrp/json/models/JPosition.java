package net.devtech.arrp.json.models;

import net.devtech.arrp.annotations.PreferredEnvironment;
import net.fabricmc.api.EnvType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * <p>Specifies the rotation, translation and scale of a model in a specified {@link JDisplay.DisplayPosition}.</p>
 * <p>For example, the position of torch in {@link net.devtech.arrp.json.models.JDisplay.DisplayPosition#THIRDPERSON_RIGHTHAND "thirdperson_righthand"} is:</p>
 * <pre>{@code
 * {
 *   "rotation": [ -90, 0, 0 ],
 *   "translation": [ 0, 1, -3 ],
 *   "scale": [ 0.55, 0.55, 0.55 ]
 * }
 * }</pre>
 * <p>which can be defined as:</p>
 * <pre>{@code
 * JPosition.ofRotation(-90, 0, 0).translation(0, 1, -3).scale(0.55, 0.55, 0.55);
 * }</pre>
 */
@SuppressWarnings("unused")
@PreferredEnvironment(EnvType.CLIENT)
public class JPosition implements Cloneable {
  /**
   * The rotation of the model, in the format of [x, y, z]. Rotation degrees will be applied around the corresponding axis. For example, a rotation of [0, 45, 0] will rotate the model around Y axis.
   */
  public float[] rotation;
  /**
   * The translation of the model, in the format of [x, y, z]. Model will be offset at the corresponding axis. Each value should be between -80 and 80. However, this class dos not inspect it.
   */
  public float[] translation;
  /**
   * The scale of the model, in the format of [x, y, z]. Each value should be no larger than 4. However, this class does not inspect it.
   */
  public float[] scale;

  /**
   * Create a new empty object, in which the parameters are empty.
   */
  public JPosition() {
    this.rotation = new float[3];
    this.translation = new float[3];
    this.scale = new float[3];
  }

  /**
   * Create an object with the specific parameters. The parameters will be directly used as the field.
   *
   * @param rotation    The rotation of the model.
   * @param translation The translation of the model.
   * @param scale       The scale of the model.
   */
  @ApiStatus.AvailableSince("0.8.2")
  public JPosition(float[] rotation, float[] translation, float[] scale) {
    this.rotation = rotation;
    this.translation = translation;
    this.scale = scale;
  }

  /**
   * Create an object with the specific rotation. Other parameters will be kept default.
   */
  @Contract("_,_,_ -> new")
  public static JPosition ofRotation(float x, float y, float z) {
    return new JPosition(new float[]{x, y, z}, new float[3], new float[3]);
  }

  /**
   * Create an object with the specific translation. Other parameters will be kept default.
   */
  @Contract("_,_,_ -> new")
  public static JPosition ofTranslation(float x, float y, float z) {
    return new JPosition(new float[3], new float[]{x, y, z}, new float[3]);
  }

  /**
   * Create an object with the specific scale. Other parameters will be kept default.
   */
  @Contract("_,_,_ -> new")
  public static JPosition ofScale(float x, float y, float z) {
    return new JPosition(new float[3], new float[3], new float[]{x, y, z});
  }

  /**
   * Set the rotation of the model. Note that it directly mutates the {@link #rotation} field, and may fail if it is null.
   */
  @Contract(value = "_,_,_ -> this", mutates = "this")
  public JPosition rotation(float x, float y, float z) {
    this.rotation[0] = x;
    this.rotation[1] = y;
    this.rotation[2] = z;
    return this;
  }

  /**
   * Set the translation of the model. Note that it directly mutates the {@link #translation} field, and may fail if it is null.
   */
  @Contract(value = "_,_,_ -> this", mutates = "this")
  public JPosition translation(float x, float y, float z) {
    this.translation[0] = x;
    this.translation[1] = y;
    this.translation[2] = z;
    return this;
  }

  /**
   * Set the scale of the model. Note that it directly mutates the {@link #scale} field, and may fail if it is null.
   */
  @Contract(value = "_,_,_ -> this", mutates = "this")
  public JPosition scale(float x, float y, float z) {
    this.scale[0] = x;
    this.scale[1] = y;
    this.scale[2] = z;
    return this;
  }

  /**
   * Set the rotation of the model. The parameter will be directly used.
   */
  @ApiStatus.AvailableSince("0.8.2")
  @Contract(value = "_ -> this", mutates = "this")
  public JPosition rotation(float[] rotation) {
    this.rotation = rotation;
    return this;
  }

  /**
   * Set the translation of the model. The parameter will be directly used.
   */
  @ApiStatus.AvailableSince("0.8.2")
  @Contract(value = "_ -> this", mutates = "this")
  public JPosition translation(float[] translation) {
    this.translation = translation;
    return this;
  }

  /**
   * Set the scale of the model. The parameter will be directly used.
   */
  @ApiStatus.AvailableSince("0.8.2")
  @Contract(value = "_ -> this", mutates = "this")
  public JPosition scale(float[] scale) {
    this.scale = scale;
    return this;
  }

  @Override
  public JPosition clone() {
    try {
      return (JPosition) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }
}
