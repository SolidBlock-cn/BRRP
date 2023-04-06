package pers.solid.brrp.v1.model;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import pers.solid.brrp.v1.annotations.PreferredEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * The element of a model, which is used for data generation.
 *
 * @see net.minecraft.client.render.model.json.ModelElement
 */
@PreferredEnvironment(EnvType.CLIENT)
public class ModelElementBuilder implements Cloneable {
  public Vector3f from;
  public Vector3f to;
  public Map<Direction, ModelElementFaceBuilder> faces;
  public ModelRotation rotation;
  public boolean shade;

  public ModelElementBuilder from(float x, float y, float z) {
    return from(new Vector3f(x, y, z));
  }

  public ModelElementBuilder from(Vector3f from) {
    this.from = from;
    return this;
  }

  public ModelElementBuilder to(float x, float y, float z) {
    return to(new Vector3f(x, y, z));
  }

  public ModelElementBuilder to(Vector3f to) {
    this.to = to;
    return this;
  }

  public ModelElementBuilder face(Direction direction, ModelElementFaceBuilder face) {
    if (faces == null) faces = new HashMap<>();
    faces.put(direction, face);
    return this;
  }

  public ModelElementBuilder faces(Map<Direction, ModelElementFaceBuilder> faces) {
    this.faces = faces;
    return this;
  }

  public ModelElementBuilder rotation(ModelRotation rotation) {
    this.rotation = rotation;
    return this;
  }

  public ModelElementBuilder rotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
    return rotation(new ModelRotation(origin, axis, angle, rescale));
  }

  public ModelElementBuilder shade(boolean shade) {
    this.shade = shade;
    return this;
  }

  @Override
  public ModelElementBuilder clone() {
    try {
      return (ModelElementBuilder) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  /**
   * Convert the object to the vanilla {@link ModelElement} object. Please notice that this method is client-side only.
   *
   * @return The vanilla {@link ModelElement} object.
   */
  @Environment(EnvType.CLIENT)
  public ModelElement toModelElement() {
    return new ModelElement(from, to, Maps.transformValues(faces, ModelElementFaceBuilder::toModelElementFace), rotation.asVanilla(), shade);
  }

  /**
   * Similar to {@link net.minecraft.client.render.model.json.ModelRotation}, but not client-side only.
   *
   * @see net.minecraft.client.render.model.json.ModelRotation
   */
  @PreferredEnvironment(EnvType.CLIENT)
  public record ModelRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
    @Environment(EnvType.CLIENT)
    public net.minecraft.client.render.model.json.ModelRotation asVanilla() {
      return new net.minecraft.client.render.model.json.ModelRotation(origin, axis, angle, rescale);
    }
  }
}
