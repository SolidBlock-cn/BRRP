package net.devtech.arrp.json.models;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.annotations.SerializedName;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.fabricmc.api.EnvType;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Contract;

/**
 * Defines a rotation in the {@link JElement}. For example,
 * <pre>{@code
 * { "origin": [8, 8, 8],
 *   "axis": "x",
 *   "angle": 45,
 *   "rescale": false }
 *   }
 * </pre>
 * can be produced with
 * <pre>{@code
 * new JRotation(Direction.Axis.X, 8, 8, 8, 45).rescale(true);
 * }</pre>
 */
@PreferredEnvironment(EnvType.CLIENT)
public class JRotation implements Cloneable {
  public final float[] origin = new float[3];
  /**
   * @deprecated Please use {@link #rotationAxis}.
   */
  @Deprecated(forRemoval = true)
  private transient final char axis = ' ';
  @SerializedName("axis")
  public final String rotationAxis;
  /**
   * The rotation angle. It is usually in the increment of 22.5.
   */
  public Float angle;
  public Boolean rescale;

  public JRotation(Direction.Axis axis) {
    this.rotationAxis = axis.asString();
  }

  public JRotation(Direction.Axis axis, float x, float y, float z, float angle) {
    this(axis);
    origin(x, y, z);
    angle(angle);
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _, _ -> this", mutates = "this")
  public JRotation origin(float x, float y, float z) {
    this.origin[0] = x;
    this.origin[1] = y;
    this.origin[2] = z;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JRotation angle(Float angle) {
    this.angle = angle;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JRotation rescale(boolean rescale) {
    this.rescale = rescale;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "-> this", mutates = "this")
  public JRotation rescale() {
    this.rescale = true;
    return this;
  }

  @Override
  public JRotation clone() {
    try {
      return (JRotation) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }
}
