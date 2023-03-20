package pers.solid.brrp.v1.model;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * This represents a face of a model element.
 *
 * @see net.minecraft.client.render.model.json.ModelElementFace
 */
public class ModelElementFaceBuilder implements Cloneable {
  @SerializedName("uv")
  public float @Nullable [] uvs;
  public int rotation;
  @SerializedName("cullface")

  public Direction cullFace;
  public int tintIndex;
  @SerializedName("texture")
  public String textureId;

  public ModelElementFaceBuilder uv(float x1, float y1, float x2, float y2) {
    this.uvs = new float[]{x1, y1, x2, y1};
    return this;
  }

  public ModelElementFaceBuilder uv(float[] uvs) {
    if (uvs != null) {
      Preconditions.checkArgument(uvs.length == 4, "Parameter uv must be an 4-element array, instead of %s!", uvs.length);
    }
    this.uvs = uvs;
    return this;
  }

  public ModelElementFaceBuilder rotation(int rotation) {
    this.rotation = rotation;
    return this;
  }

  public ModelElementFaceBuilder cullFace(Direction cullFace) {
    this.cullFace = cullFace;
    return this;
  }

  public ModelElementFaceBuilder tintIndex(int tintIndex) {
    this.tintIndex = tintIndex;
    return this;
  }

  public ModelElementFaceBuilder texture(String textureId) {
    this.textureId = textureId;
    return this;
  }

  /**
   * Convert this object to vanilla {@link ModelElementFace} object. This method is client-side only.
   *
   * @return The vanilla {@link ModelElementFace} object.
   */
  @Environment(EnvType.CLIENT)
  public ModelElementFace toModelElementFace() {
    return new ModelElementFace(cullFace, tintIndex, textureId, new ModelElementTexture(uvs, rotation));
  }

  @Override
  public ModelElementFaceBuilder clone() {
    try {
      return (ModelElementFaceBuilder) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
