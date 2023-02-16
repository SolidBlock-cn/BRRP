package net.devtech.arrp.json.models;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.fabricmc.api.EnvType;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * A face on the specified direction of the {@link JElement}.
 */
@PreferredEnvironment(EnvType.CLIENT)
public class JFace implements Cloneable {
  /**
   * @since 0.8 not deprecated, not final, nullable
   */
  public float @Nullable [] uv = null;
  public final String texture;
  public String cullface;
  /**
   * The allowed values are: 0, 90, 180, 270.
   */
  public Integer rotation;
  public Integer tintindex;

  /**
   * @param textureVarName The variable name of the texture. <b>Not prefixed with {@code "#"}</b>, as it will be auto prefixed.
   */
  public JFace(String textureVarName) {
    this.texture = '#' + textureVarName;
  }

  /**
   * In this case, the {@link #uv} will be removed, so Minecraft automatically determines the uv according to the "from" and "to" in the {@link JElement}.<br>
   * Usually you needn't call this method, as it is undefined by default.
   */
  @CanIgnoreReturnValue
  @Contract(value = "-> this", mutates = "this")
  public JFace autoUv() {
    this.uv = null;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_,_,_,_ -> this", mutates = "this")
  public JFace uv(float x1, float y1, float x2, float y2) {
    this.uv = new float[]{x1, y1, x2, y2};
    return this;
  }

  /**
   * Set the cullface in the specified direction, or {@code null} if there is no cullface.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JFace cullface(@Nullable Direction direction) {
    this.cullface = direction == null ? null : direction.asString();
    return this;
  }

  /**
   * Set the rotation of the texture. It is usually 0, 90, 180 or 270.<br>
   * You can also call {@link #rot90()}, {@link #rot180()} or {@link #rot270()}.
   */

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JFace rotation(int rotation) {
    this.rotation = rotation;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "-> this", mutates = "this")
  public JFace rot90() {
    this.rotation = 90;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "-> this", mutates = "this")
  public JFace rot180() {
    this.rotation = 180;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "-> this", mutates = "this")
  public JFace rot270() {
    this.rotation = 270;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JFace tintIndex(int index) {
    this.tintindex = index;
    return this;
  }

  @Override
  public JFace clone() {
    try {
      return (JFace) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }
}
