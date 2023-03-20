package pers.solid.brrp.v1.model;

import com.mojang.datafixers.util.Either;
import net.fabricmc.api.EnvType;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.annotations.PreferredEnvironment;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.generator.BlockResourceGenerator;
import pers.solid.brrp.v1.mixin.ModelAccessor;

import java.util.HashMap;

/**
 * The utility class for model creation and runtime resource packs.
 */
@PreferredEnvironment(EnvType.CLIENT)
public final class ModelUtils {
  private ModelUtils() {
  }

  /**
   * Get the id from a vanilla {@link Model} object. For example:
   * <pre>{@code
   *   getId(Models.BUTTON_PRESSED); // returns new Identifier("minecraft", "block/button_pressed")
   * }</pre>
   *
   * @param model The vanilla {@link Model} object.
   * @return The model it that the vanilla object corresponds to.
   */
  @Contract(pure = true)
  public static Identifier getId(@NotNull Model model) {
    return ((ModelAccessor) model).getParent().orElse(null);
  }

  /**
   * Append the variant suffix to a model id, according to a vanilla {@link Model} object. If the vanilla {@link Model} object does not define a variant name, the id will not be modified. For example:
   * <pre>{@code
   *   Identifier id = new Identifier("my_mod", "block/example");
   *   appendVariant(id, Models.BUTTON); // returns id itself
   *   appendVariant(id, Models.BUTTON_PRESSED); // returns new Identifier("my_mod", "block/example_pressed");
   * }</pre>
   *
   * @param modelId The id of the model before adding suffix.
   * @param model   The vanilla {@link Model} object.
   * @return The model id with the specified suffix defined by {@code model}, or unchanged if it does not specify it.
   * @see net.minecraft.data.client.Models
   */
  @Contract(pure = true)
  public static @NotNull Identifier appendVariant(@NotNull Identifier modelId, @NotNull Model model) {
    return ((ModelAccessor) model).getVariant().map(modelId::brrp_suffixed).orElse(modelId);
  }

  /**
   * Get map for texture used for {@link ModelJsonBuilder}, according to the specified one or several texture keys. {@link BlockResourceGenerator#getTextureId(TextureKey)} will be respected.
   *
   * @see net.minecraft.data.client.TextureMap
   */
  @Contract(pure = true)
  public static HashMap<String, Either<Identifier, String>> getTextureMap(BlockResourceGenerator blockResourceGenerator, @NotNull TextureKey... textureKeys) {
    final HashMap<String, Either<Identifier, String>> map = new HashMap<>();
    for (TextureKey textureKey : textureKeys) {
      map.put(textureKey.getName(), Either.left(blockResourceGenerator.getTextureId(textureKey)));
    }
    return map;
  }

  /**
   * Write one or multiple models to a runtime resource pack, using the specified model parents. The model parent id and variant name will be used.
   *
   * @param pack    The runtime resource pack.
   * @param modelId The id of the model, before adding variant suffix.
   * @param model   The model object, in which the {@link ModelJsonBuilder#withParent(Model)} will be used for the model parent.
   * @param parent  The model parent. The id and variant names will be used.
   */
  @Contract(mutates = "param1")
  public static void writeModelsWithVariants(@NotNull RuntimeResourcePack pack, @NotNull Identifier modelId, @NotNull ModelJsonBuilder model, Model parent) {
    pack.addModel(appendVariant(modelId, parent), model.withParent(parent));
  }

  /**
   * Write one or multiple models to a runtime resource pack, using the specified model parents. The model parent id and variant name will be used.
   *
   * @param pack    The runtime resource pack.
   * @param modelId The id of the model, before adding variant suffix.
   * @param model   The model object, in which the {@link ModelJsonBuilder#withParent(Model)} will be used for each model parent.
   * @param parents The model parents. The id and variant names will be used.
   */
  @Contract(mutates = "param1")
  public static void writeModelsWithVariants(@NotNull RuntimeResourcePack pack, @NotNull Identifier modelId, @NotNull ModelJsonBuilder model, Model... parents) {
    for (Model parent : parents) {
      writeModelsWithVariants(pack, modelId, model, parent);
    }
  }

  /**
   * Create a model with a {@link BlockResourceGenerator} object and a model parent. The required texture keys of the {@link Model} will be used.
   *
   * @param blockResourceGenerator The {@link BlockResourceGenerator} object. Its {@link BlockResourceGenerator#getTextureId(TextureKey)} will be used for each texture id.
   * @param parent                 The model parent. The id and the required texture keys of the {@link Model} will be used.
   * @return The new model, with the parent id and textures.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public static ModelJsonBuilder createModelWithVariants(BlockResourceGenerator blockResourceGenerator, Model parent) {
    final ModelJsonBuilder model = ModelJsonBuilder.create(parent);
    for (TextureKey textureKey : ((ModelAccessor) parent).getRequiredTextures()) {
      model.addTexture(textureKey, blockResourceGenerator.getTextureId(textureKey));
    }
    return model;
  }
}
