package pers.solid.brrp.v1.model;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
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
 * @see net.minecraft.data.client.Model
 * @see net.minecraft.client.render.block.BlockModels
 * @see net.minecraft.client.render.model.BakedModel
 * @see net.minecraft.client.render.model.BasicBakedModel
 * @see net.minecraft.client.render.model.UnbakedModel
 * @see net.minecraft.client.render.model.json.JsonUnbakedModel
 */
public class ModelJsonBuilder implements Cloneable {

  public List<ModelElementBuilder> elements;
  @SerializedName("gui_light")
  public GuiLight guiLight;
  @SerializedName("ambientocclusion")
  public Boolean ambientOcclusion;
  @SerializedName("display")
  public Map<ModelTransformationMode, TransformationBuilder> transformations;
  public List<ModelOverrideBuilder> overrides;
  /**
   * The textures of this model. The key is texture variable name (prefixed with '#"). The value is the identifier of the texture, of a reference to another model texture (prefixed with '#').
   */
  public Map<String, String> textures;
  @SerializedName("parent")
  public Identifier parentId;

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
   * Create a {@link ModelJsonBuilder} with a specified parent model id.
   *
   * @param parentId The id of the parent model.
   * @return The new {@link ModelJsonBuilder} object.
   */
  @NotNull
  @Contract(pure = true, value = "_ -> new")
  public static ModelJsonBuilder create(String parentId) {
    return create(new Identifier(parentId));
  }

  /**
   * Create a {@link ModelJsonBuilder} with a specific parent model id defined in {@link Model}.
   *
   * @param parentModel The parent model.
   * @return The new {@link ModelJsonBuilder} object.
   * @see net.minecraft.data.client.Models
   */
  @NotNull
  @Contract(pure = true, value = "_ -> new")
  public static ModelJsonBuilder create(Model parentModel) {
    return create(ModelUtils.getId(parentModel));
  }

  /**
   * Create a {@link ModelJsonBuilder} with a specific identifier of the parent
   *
   * @return The new {@link ModelJsonBuilder} object.
   */
  @NotNull
  @Contract(pure = true, value = "_, _ -> new")
  public static ModelJsonBuilder create(String namespace, String path) {
    return create(new Identifier(namespace, path));
  }

  /**
   * Set the all override rules of the model, replacing existing ones (if any). The parameter will be directly used.
   *
   * @param overrides The list of model overrides, which will be used directly.
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder setOverrides(List<ModelOverrideBuilder> overrides) {
    this.overrides = overrides;
    return this;
  }

  /**
   * Add an override rule.
   *
   * @param override The override rule to be added.
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder addOverride(ModelOverrideBuilder override) {
    if (overrides == null) {
      overrides = new ArrayList<>();
    }
    overrides.add(override);
    return this;
  }

  /**
   * Set the list of model element, replacing existing ones (if any). The parameter will be directly used.
   *
   * @param elements The list of model elements, which will be directly used.
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder setElements(List<ModelElementBuilder> elements) {
    this.elements = elements;
    return this;
  }

  /**
   * Add a model element.
   *
   * @param element The model element.
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder addElement(ModelElementBuilder element) {
    if (elements == null) elements = new ArrayList<>();
    elements.add(element);
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
    if (transformations == null) transformations = new LinkedHashMap<>();
    transformations.put(modelTransformationMode, transformation);
    return this;
  }

  @Contract(mutates = "this", value = "_-> this")
  public ModelJsonBuilder transformations(Map<ModelTransformationMode, TransformationBuilder> transformations) {
    this.transformations = transformations;
    return this;
  }

  /**
   * Add a texture using the specific id of the texture. If you want to refer to a texture variable, please use {@link #addTexture(TextureKey, String)} instead.
   *
   * @param textureKey      The texture key.
   * @param textureLocation The id of the texture, such as {@code new Identifier("minecraft", "stone")}.
   */
  @Contract(mutates = "this", value = "_, _ -> this")
  public ModelJsonBuilder addTexture(@NotNull TextureKey textureKey, @Nullable Identifier textureLocation) {
    return addTexture(textureKey.getName(), textureLocation);
  }

  /**
   * Add a texture using a reference to another texture. The texture reference can be prefixed with {@code "#"}. It will refer to another texture variable. If you are adding id of a texture directly, please use {@link #addTexture(TextureKey, Identifier)} instead.
   *
   * @param textureKey       The texture key.
   * @param textureReference The reference to another texture, with is the texture variable name prefixed with {@code "#"}.
   */
  @Contract(mutates = "this", value = "_, _ -> this")
  public ModelJsonBuilder addTexture(@NotNull TextureKey textureKey, @Nullable String textureReference) {
    return addTexture(textureKey.getName(), textureReference);
  }

  /**
   * Add a texture using the specific id of the texture. If you want to refer to a texture variable, please use {@link #addTexture(String, String)} instead.
   *
   * @param key             The texture key.
   * @param textureLocation The id of the texture, such as {@code new Identifier("minecraft", "stone")}.
   */
  @Contract(mutates = "this", value = "_, _ -> this")
  public ModelJsonBuilder addTexture(@NotNull String key, @Nullable Identifier textureLocation) {
    return addTexture(key, textureLocation == null ? null : textureLocation.toString());
  }

  /**
   * Add a texture using a reference to another texture. The texture reference can be prefixed with {@code "#"}. It will refer to another texture variable. If you are adding id of a texture directly, please use {@link #addTexture(String, Identifier)} instead.
   *
   * @param key              The texture key.
   * @param textureReference The reference to another texture, with is the texture variable name prefixed with {@code "#"}.
   */
  @Contract(mutates = "this", value = "_, _ -> this")
  public ModelJsonBuilder addTexture(@NotNull String key, @Nullable String textureReference) {
    if (textures == null) {
      textures = new LinkedHashMap<>();
    }
    this.textures.put(key, textureReference);
    return this;
  }

  /**
   * Set the textures from a specified {@link TextureMap}.
   *
   * @param textures The texture map.
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder setTextures(TextureMap textures) {
    ((TextureMapAccessor) textures).getEntries().forEach(this::addTexture);
    return this;
  }

  /**
   * Set the texture from a specified simple string map. The map will be directly used.
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder setTextures(Map<String, String> textures) {
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
   * Set the id of the model parent.
   *
   * @param parentId The parent model id.
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder parent(Identifier parentId) {
    this.parentId = parentId;
    return this;
  }

  /**
   * Set the id of the model parent.
   *
   * @param parentId The parent model id.
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder parent(String parentId) {
    return parent(new Identifier(parentId));
  }

  /**
   * Set the id of the model parent.
   */
  @Contract(mutates = "this", value = "_ -> this")
  public ModelJsonBuilder parent(String namespace, String path) {
    return parent(new Identifier(namespace, path));
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
   * @return A new model with the specified parent id, or the current object.
   */
  @Contract(pure = true)
  public ModelJsonBuilder withParent(String namespace, String path) {
    return this.withParent(new Identifier(namespace, path));
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

  /**
   * Return a new model with a specified parent id. If it equals the current parent id, the current one will be returned.
   *
   * @param parentId The id of the parent.
   * @return A new model with the specified parent id, or the current object.
   */
  @Contract(pure = true)
  public ModelJsonBuilder withParent(String parentId) {
    return withParent(new Identifier(parentId));
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
