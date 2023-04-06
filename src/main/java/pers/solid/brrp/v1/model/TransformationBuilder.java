package pers.solid.brrp.v1.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.Transformation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import pers.solid.brrp.v1.annotations.PreferredEnvironment;

/**
 * Similar to {@link Transformation}, but allows calling methods to change it, and allows modifying. It represents a transformation of a model, including rotation, translation, and scale.
 */
@PreferredEnvironment(EnvType.CLIENT)
public class TransformationBuilder {
  public static final Vector3f EMPTY_VECTOR = new Vector3f(0, 0, 0);
  public static final Vector3f UNIT_VECTOR = new Vector3f(1, 1, 1);
  public @NotNull Vector3f rotation;
  public @NotNull Vector3f translation;
  public @NotNull Vector3f scale;

  /**
   * Create a new transformation with the specified rotation, translation, and scale.
   */
  public TransformationBuilder(@NotNull Vector3f rotation, @NotNull Vector3f translation, @NotNull Vector3f scale) {
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
    return rotation(new Vector3f(x, y, z));
  }

  @Contract(value = "_ -> this", mutates = "this")
  public TransformationBuilder rotation(Vector3f rotation) {
    this.rotation = rotation;
    return this;
  }


  @Contract(value = "_, _, _ -> this", mutates = "this")
  public TransformationBuilder translation(float x, float y, float z) {
    return translation(new Vector3f(x, y, z));
  }

  @Contract(value = "_ -> this", mutates = "this")
  public TransformationBuilder translation(Vector3f translation) {
    this.translation = translation;
    return this;
  }

  @Contract(value = "_, _, _ -> this", mutates = "this")
  public TransformationBuilder scale(float x, float y, float z) {
    return scale(new Vector3f(x, y, z));
  }

  @Contract(value = "_ -> this", mutates = "this")
  public TransformationBuilder scale(Vector3f scale) {
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
