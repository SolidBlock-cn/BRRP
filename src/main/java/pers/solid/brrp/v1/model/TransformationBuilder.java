package pers.solid.brrp.v1.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.annotations.PreferredEnvironment;

/**
 * Similar to {@link Transformation}, but allows calling methods to change it, and allows modifying. It represents a transformation of a model, including rotation, translation, and scale.
 */
@PreferredEnvironment(EnvType.CLIENT)
public class TransformationBuilder {
  public static final Vec3f EMPTY_VECTOR = new Vec3f(0, 0, 0);
  public static final Vec3f UNIT_VECTOR = new Vec3f(1, 1, 1);
  public @NotNull Vec3f rotation;
  public @NotNull Vec3f translation;
  public @NotNull Vec3f scale;

  /**
   * Create a new transformation with the specified rotation, translation, and scale.
   */
  public TransformationBuilder(@NotNull Vec3f rotation, @NotNull Vec3f translation, @NotNull Vec3f scale) {
    this.rotation = rotation;
    this.translation = translation;
    this.scale = scale;
  }

  /**
   * Create a new transformation with default parameters. The rotation and translation are zero, and the scale is one.
   */
  public TransformationBuilder() {
    this(EMPTY_VECTOR, EMPTY_VECTOR, UNIT_VECTOR);
  }


  @Contract(value = "_, _, _ -> this", mutates = "this")
  public TransformationBuilder rotation(float x, float y, float z) {
    return rotation(new Vec3f(x, y, z));
  }

  @Contract(value = "_ -> this", mutates = "this")
  public TransformationBuilder rotation(Vec3f rotation) {
    this.rotation = rotation;
    return this;
  }


  @Contract(value = "_, _, _ -> this", mutates = "this")
  public TransformationBuilder translation(float x, float y, float z) {
    return translation(new Vec3f(x, y, z));
  }

  @Contract(value = "_ -> this", mutates = "this")
  public TransformationBuilder translation(Vec3f translation) {
    this.translation = translation;
    return this;
  }

  @Contract(value = "_, _, _ -> this", mutates = "this")
  public TransformationBuilder scale(float x, float y, float z) {
    return scale(new Vec3f(x, y, z));
  }

  @Contract(value = "_ -> this", mutates = "this")
  public TransformationBuilder scale(Vec3f scale) {
    this.scale = scale;
    return this;
  }

  /**
   * The method can be used only in client.
   *
   * @return The vanilla object that corresponds to it, which exists only in client.
   */
  @Environment(EnvType.CLIENT)
  public Transformation asVanillaTransformation() {
    return new Transformation(rotation, translation, scale);
  }
}
