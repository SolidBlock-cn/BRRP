package pers.solid.brrp.v1.model;

import com.google.gson.annotations.SerializedName;
import com.mojang.datafixers.util.Either;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.data.client.model.Model;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.client.model.Texture;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.mixin.TextureMapAccessor;

import java.util.*;

/**
 * <p>This is the class representing the models in resource packs. It includes block models and item models.</p>
 *
 * @see Models
 * @see net.minecraft.client.render.block.BlockModels
 * @see net.minecraft.client.render.model.BakedModel
 * @see net.minecraft.client.render.model.BasicBakedModel
 * @see net.minecraft.client.render.model.UnbakedModel
 * @see net.minecraft.client.render.model.json.JsonUnbakedModel
 */
public class ModelJsonBuilder implements Cloneable {

  public List<ModelElementBuilder> elements;
  @Nullable
  @SerializedName("gui_light")
  public GuiLight guiLight;
  @Nullable
  @SerializedName("ambientocclusion")
  public Boolean ambientOcclusion;
  @SerializedName("display")
  public Map<ModelTransformationMode, TransformationBuilder> transformations;
  public List<ModelOverrideBuilder> overrides;
  /**
   * The textures of this model. The key is texture variable name (prefixed with '#"). The value is the identifier of the texture, of a reference to another model texture (prefixed with '#').
   */
  public Map<String, Either<Identifier, String>> textures;
  @Nullable
  @SerializedName("parent")
  protected Identifier parentId;

  /**
   * Create a {@link ModelJsonBuilder} with a specified parent model id.
   *
   * @param parentId The id of the parent model.
   * @return The new {@link ModelJsonBuilder} object.
   */
  @NotNull
  @Contract(pure = true, value = "_ -> new")
  public static ModelJsonBuilder create(Identifier parentId) {
    final ModelJsonBuilder modelJsonBuilder = new ModelJsonBuilder();
    modelJsonBuilder.parentId = parentId;
    return modelJsonBuilder;
  }

  /**
   * Create a {@link ModelJsonBuilder} with a specific parent model id defined in {@link Model}.
   *
   * @param parentModel The parent model.
   * @return The new {@link ModelJsonBuilder} object.
   * @see Models
   */
  @NotNull
  @Contract(pure = true, value = "_ -> new")
  public static ModelJsonBuilder create(Model parentModel) {
    return create(ModelUtils.getId(parentModel));
  }

  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder overrides(List<ModelOverrideBuilder> overrides) {
    this.overrides = overrides;
    return this;
  }

  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder addOverride(ModelOverrideBuilder override) {
    if (overrides == null) {
      overrides = new ArrayList<>();
    }
    overrides.add(override);
    return this;
  }

  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder addElement(ModelElementBuilder element) {
    if (elements == null) elements = new ArrayList<>();
    elements.add(element);
    return this;
  }

  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder setElements(List<ModelElementBuilder> elements) {
    this.elements = elements;
    return this;
  }

  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder guiLight(GuiLight guiLight) {
    this.guiLight = guiLight;
    return this;
  }

  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder ambientOcclusion(Boolean ambientOcclusion) {
    this.ambientOcclusion = ambientOcclusion;
    return this;
  }

  @Contract(mutates = "this", value = "_, _ -> this")
  public ModelJsonBuilder transformation(ModelTransformationMode modelTransformationMode, TransformationBuilder transformation) {
    if (transformations == null) transformations = new HashMap<>();
    transformations.put(modelTransformationMode, transformation);
    return this;
  }

  @Contract(mutates = "this", value = "_-> this")
  public ModelJsonBuilder transformations(Map<ModelTransformationMode, TransformationBuilder> transformations) {
    this.transformations = transformations;
    return this;
  }

  @Contract(mutates = "this", value = "_, _ -> this")
  public ModelJsonBuilder addTexture(@NotNull TextureKey textureKey, @NotNull Identifier textureLocation) {
    return addTexture(textureKey.getName(), textureLocation);
  }

  @Contract(mutates = "this", value = "_, _ -> this")
  public ModelJsonBuilder addTexture(@NotNull TextureKey textureKey, @NotNull String textureReference) {
    return addTexture(textureKey.getName(), textureReference);
  }

  @Contract(mutates = "this", value = "_, _ -> this")
  public ModelJsonBuilder addTexture(@NotNull String key, @NotNull Identifier textureLocation) {
    return addTexture(key, Either.left(textureLocation));
  }

  @Contract(mutates = "this", value = "_, _ -> this")
  public ModelJsonBuilder addTexture(@NotNull String key, @NotNull String textureReference) {
    return addTexture(key, Either.right(textureReference));
  }

  @Contract(mutates = "this", value = "_, _ -> this")
  protected ModelJsonBuilder addTexture(@NotNull String key, @NotNull Either<Identifier, String> value) {
    if (textures == null) textures = new HashMap<>();
    textures.put(key, value);
    return this;
  }

  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder setTextures(Texture textures) {
    ((TextureMapAccessor) textures).getEntries().forEach(this::addTexture);
    return this;
  }

  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder setTextures(Map<String, Either<Identifier, String>> textures) {
    this.textures = textures;
    return this;
  }

  /**
   * Set the id of the model parent. Common vanilla models can be found in {@link Models}.
   *
   * @param model The parent model.
   * @see Models
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder parent(Model model) {
    return parent(ModelUtils.getId(model));
  }

  /**
   * Set the id of the model parent. Common vanilla models can be found in {@link Models}.
   *
   * @param parentId The parent model id.
   * @see Models
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder parent(Identifier parentId) {
    this.parentId = parentId;
    return this;
  }

  /**
   * Return a new model with a specified parent id. If it equals the current parent id, the current one will be returned.
   *
   * @param model The id of the parent.
   * @return A new model with the specified parent id, or the current object.
   */
  @Contract(pure = true)
  public ModelJsonBuilder withParent(Model model) {
    return this.withParent(ModelUtils.getId(model));
  }

  /**
   * Return a new model with a specified parent id. If it equals the current parent id, the current one will be returned.
   *
   * @param parentId The id of the parent.
   * @return A new model with the specified parent id, or the current object.
   */
  @Contract(pure = true)
  public ModelJsonBuilder withParent(Identifier parentId) {
    if (Objects.equals(this.parentId, parentId)) {
      return this;
    }
    return clone().parent(parentId);
  }

  @Override
  public ModelJsonBuilder clone() {
    try {
      final ModelJsonBuilder clone = (ModelJsonBuilder) super.clone();
      if (clone.elements != null) {
        clone.elements = new ArrayList<>(this.elements);
      }
      if (clone.overrides != null) {
        clone.overrides = new ArrayList<>(this.overrides);
      }
      if (clone.transformations != null) {
        clone.transformations = new HashMap<>(transformations);
      }
      if (clone.textures != null) {
        clone.textures = new HashMap<>(textures);
      }
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  /**
   * Similar to {@link JsonUnbakedModel.GuiLight}, but not client-side only.
   */
  public enum GuiLight implements StringIdentifiable {
    FRONT("front"), SIDE("side");

    private final String name;

    GuiLight(String name) {
      this.name = name;
    }

    /**
     * This method can only be used in client.
     *
     * @return The vanilla client-only object that corresponds to it.
     */
    @Environment(EnvType.CLIENT)
    public JsonUnbakedModel.GuiLight asVanillaGuiLight() {
      return this == FRONT ? JsonUnbakedModel.GuiLight.ITEM : JsonUnbakedModel.GuiLight.BLOCK;
    }

    @Override
    public String asString() {
      return name;
    }
  }
}
