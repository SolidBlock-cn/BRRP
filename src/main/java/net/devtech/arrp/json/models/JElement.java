package net.devtech.arrp.json.models;

import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.function.Function;

/**
 * Represents an element of a model. It is the entry in {@link JModel#elements elements} field of a {@link JModel model}. For example, <pre>{@code
 * {
 *   "from": [ 8, 0, 0.8 ],
 *   "to": [ 8, 16, 15.2 ],
 *   "rotation": { "origin": [ 8, 8, 8 ], "axis": "y", "angle": 45, "rescale": true },
 *   "shade": false,
 *   "faces": {
 *     "west": { "uv": [ 0, 0, 16, 16 ], "texture": "#cross" },
 *     "east": { "uv": [ 0, 0, 16, 16 ], "texture": "#cross" }
 *   }
 * }}</pre>
 * can be represented by
 * <pre>{@code
 * JElement.of(8, 0, 0.8, 8, 16, 15.2)
 *   .rotation(new JRotation(Direction.Axis.Y, 8, 8, 8, 45).rescale(true))
 *   .shade(false)
 *   .addFace(Direction.WEST, new JFace("cross").uv(0, 0, 16, 16))
 *   .addFace(Direction.EAST, new JFace("cross").uv(0, 0, 16, 16))
 * }</pre>
 */
@SuppressWarnings("unused")
public class JElement implements Cloneable {
  /**
   * The [x, y, z] of the position where the cuboid starts.
   */
  public final float[] from = new float[3];
  /**
   * The [x, y, z] of the position where the cuboid ends.
   */
  public final float[] to = new float[3];
  public JRotation rotation;
  public Boolean shade;
  public JFaces faces;

  /**
   * <p>Create a new object.</p>
   * <p>If you need to specify the two points, you can directly to {@link #of(float, float, float, float, float, float)}.</p>
   *
   * @see #of(float, float, float, float, float, float)
   */
  @ApiStatus.Internal
  public JElement() {
  }

  /**
   * Create a new object with the two positions set.
   *
   * @return A new object.
   */
  @Contract("_,_,_,_,_,_ -> new")
  public static JElement of(float x1, float y1, float z1,
                            float x2, float y2, float z2) {
    return new JElement().from(x1, y1, z1).to(x2, y2, z2);
  }

  @Contract("_,_,_ -> this")
  public JElement from(float x, float y, float z) {
    this.from[0] = x;
    this.from[1] = y;
    this.from[2] = z;
    return this;
  }

  @Contract("_,_,_ -> this")
  public JElement to(float x, float y, float z) {
    this.to[0] = x;
    this.to[1] = y;
    this.to[2] = z;
    return this;
  }

  @Contract("_ -> this")
  public JElement rotation(JRotation rotation) {
    this.rotation = rotation;
    return this;
  }

  @Contract("_ -> this")
  public JElement shade(boolean shade) {
    this.shade = shade;
    return this;
  }

  /**
   * This method sets the {@link #shade} to <code>false</code>, not <code>true</code>.
   *
   * @deprecated Please use {@link #shade(boolean) shade}{@code (false)}.
   */
  @Deprecated
  @Contract("-> this")
  public JElement shade() {
    this.shade = false;
    return this;
  }

  @Contract("_ -> this")
  public JElement faces(JFaces faces) {
    this.faces = faces;
    return this;
  }

  @Contract("_, _ -> this")
  public JElement addFace(Direction direction, JFace face) {
    if (this.faces == null) {
      this.faces = new JFaces();
    }
    faces.set(direction, face);
    return this;
  }

  @Contract("_ -> this")
  public JElement addAllFaces(JFace face) {
    if (this.faces == null) this.faces = new JFaces();
    this.faces.setAllFaces(face);
    return this;
  }

  @Contract("_ -> this")
  public JElement addAllFaces(Function<Direction, JFace> faces) {
    if (this.faces == null) this.faces = new JFaces();
    this.faces.setAllFaces(faces);
    return this;
  }

  @Override
  public JElement clone() {
    try {
      return (JElement) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }
}
