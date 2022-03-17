package net.devtech.arrp.json.blockstate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JSONSerializable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple improved version of {@link JState}.
 */
public class BlockStatesDefinition implements JSONSerializable {
  public final List<VariantDefinition> variants;
  public final List<JMultipart> multiparts;

  private BlockStatesDefinition(List<VariantDefinition> variants, List<JMultipart> multiparts) {
    this.variants = variants;
    this.multiparts = multiparts;
  }

  public static BlockStatesDefinition variants(List<VariantDefinition> variants) {
    return new BlockStatesDefinition(variants, null);
  }

  public static BlockStatesDefinition multipart(List<JMultipart> multiparts) {
    return new BlockStatesDefinition(null, multiparts);
  }

  public static BlockStatesDefinition variants(VariantDefinition... variants) {
    return variants(Arrays.asList(variants));
  }

  public static BlockStatesDefinition multipart(JMultipart... multiparts) {
    return multipart(Arrays.asList(multiparts));
  }

  /**
   * Add a variant definition for a block states definition of variants.
   *
   * @throws IllegalStateException if the block states definition is for multiples (for example, created from {@link #multipart(JMultipart...)}.
   */
  public BlockStatesDefinition add(VariantDefinition variant) {
    if (variant == null) throw new IllegalStateException("A block state definition can only have either variants or multiparts, not both");
    variants.add(variant);
    return this;
  }

  /**
   * Add a variant definition for a block states definition of variants.
   *
   * @throws IllegalStateException if the block states definition is for multiples (for example, created from {@link #variants(VariantDefinition...)}.
   */
  public BlockStatesDefinition add(JMultipart multipart) {
    if (multiparts == null) throw new IllegalStateException("A block state definition can only have either variants or multiparts, not both");
    multiparts.add(multipart);
    return this;
  }

  /**
   * Simple "upgrade" the old version jState to the improved version.
   */
  public static BlockStatesDefinition of(JState jState) {
    final BlockStatesDefinition instance;
    if (jState.variants.isEmpty()) {
      // As 'variants' is empty, it's regarded as a 'multipart'.
      instance = multipart(jState.multiparts);
    } else {
      // It's regarded a 'variants'.
      instance = variants(jState.variants.stream().map(VariantDefinition::of).collect(Collectors.toList()));
    }
    return instance;
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject object = new JsonObject();
    if (variants != null) {
      object.add("variants", context.serialize(variants));
    }
    if (multiparts != null) {
      object.add("multipart", context.serialize(multiparts));
    }
    return object;
  }
}
