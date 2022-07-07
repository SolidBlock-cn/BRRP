package net.devtech.arrp.json.models;

import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.minecraft.util.math.Direction;
import net.minecraftforge.api.distmarker.Dist;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * A face on the specified direction of the {@link JElement}.
 */
@PreferredEnvironment(Dist.CLIENT)
public class JFace implements Cloneable {
  /**
   * This field is deprecated because, it will always exist. The value is by default [0, 0, 0] which definitely cause bugs. Actually, in Minecraft, the uv can be missing, which allows Minecraft to automatically determine. Therefore, {@link #uvs} is used instead, which is a nullable {@link FloatArrayList}.
   */
  @Deprecated // forRemoval = true
  private transient final float[] uv = new float[4];
  @SerializedName("uv")
  public FloatArrayList uvs;
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
   * In this case, the {@link #uvs} will be removed, so Minecraft automatically determines the uv according to the "from" and "to" in the {@link JElement}.<br>
   * Usually you needn't call this method, as it is undefined by default.
   */
  @Contract(value = "-> this", mutates = "this")
  public JFace autoUv() {
    this.uvs = null;
    return this;
  }

  @Contract(value = "_,_,_,_ -> this", mutates = "this")
  public JFace uv(float x1, float y1, float x2, float y2) {
    this.uvs = FloatArrayList.wrap(new float[]{x1, y1, x2, y2});
    return this;
  }

  /**
   * Set the cullface in the specified direction, or {@code null} if there is no cullface.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JFace cullface(@Nullable Direction direction) {
    this.cullface = direction == null ? null : direction.asString();
    return this;
  }

  /**
   * Usually the cullface is a direction, so you should use {@link #cullface(Direction)}.
   */
  @Deprecated
  @Contract(value = "_ -> this", mutates = "this")
  public JFace cullface(String cullface) {
    this.cullface = cullface;
    return this;
  }

  /**
   * Set the rotation of the texture. It is usually 0, 90, 180 or 270.<br>
   * You can also call {@link #rot90()}, {@link #rot180()} or {@link #rot270()}.
   */

  @Contract(value = "_ -> this", mutates = "this")
  public JFace rotation(int rotation) {
    this.rotation = rotation;
    return this;
  }

  @Contract(value = "-> this", mutates = "this")
  public JFace rot90() {
    this.rotation = 90;
    return this;
  }

  @Contract(value = "-> this", mutates = "this")
  public JFace rot180() {
    this.rotation = 180;
    return this;
  }

  @Contract(value = "-> this", mutates = "this")
  public JFace rot270() {
    this.rotation = 270;
    return this;
  }

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
