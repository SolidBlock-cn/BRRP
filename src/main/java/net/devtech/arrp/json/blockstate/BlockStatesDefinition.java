package net.devtech.arrp.json.blockstate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.ARRP;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * <p>A <b>block states definition</b> is the file in the {@code assets/<i>namespace</i>/blockstates} folder, which defines which models should be used when rendering a block state. It has two types:</p>
 * <ul>
 *   <li><b>variants</b> - Each block state corresponds to a block model definition ({@link JBlockModel}). You can create a variant definition through {@link #variants}.</li>
 *   <li><b>multipart</b> - Each part has a block model, and an optional condition ({@link JWhen}). If the condition is met (which means the actual block states matches to the condition), the part will be used. In this case, it's possible that one part, multiple parts or no parts will be used. You can create a multipart definition through {@link #ofMultiparts}.</li>
 * </ul>
 * <p>When adding the block states file to the resource pack, the identifier is equal to the block identifier.</p>
 * <p>This class is a simple improved version of {@link JState}. You can "upgrade" the deprecated <code>JState</code> through {@link #of(JState)}.</p>
 *
 * @author SolidBlock
 * @since BRRP 0.6.0
 */
public class BlockStatesDefinition implements JsonSerializable {
  public final VariantDefinition variants;
  public final List<JMultipart> multiparts;

  /**
   * The basic constructor method. Please don't directly call it. Please call {@link #ofVariants} or {@link #ofMultiparts}.
   *
   * @param variants   The variant definition. One of these two parameters must be {@code null}.
   * @param multiparts The list of multiparts. One of these two parameters must be {@code null}.
   */
  private BlockStatesDefinition(VariantDefinition variants, List<JMultipart> multiparts) {
    this.variants = variants;
    this.multiparts = multiparts;
  }

  public static BlockStatesDefinition ofVariants(VariantDefinition variants) {
    return new BlockStatesDefinition(variants, null);
  }

  public static BlockStatesDefinition ofMultiparts(List<JMultipart> multiparts) {
    return new BlockStatesDefinition(null, multiparts);
  }

  public static BlockStatesDefinition ofMultiparts(JMultipart... multiparts) {
    return ofMultiparts(Arrays.asList(multiparts));
  }

  /**
   * Add a variant to the definition for a block states definition .
   *
   * @throws IllegalStateException if the block states definition is for multiparts (for example, created from {@link #ofMultiparts(JMultipart...)}.
   */
  public BlockStatesDefinition addVariant(String variant, JBlockModel... modelDefinition) {
    if (variant == null) throw new IllegalStateException("A block state definition can only have either variants or multiparts, not both");
    variants.addVariant(variant, modelDefinition);
    return this;
  }

  /**
   * Add a variant definition for a block states definition of variants.
   *
   * @throws IllegalStateException if the block states definition is for multiples (for example, created from {@link #ofVariants(VariantDefinition)}.
   */
  public BlockStatesDefinition add(JMultipart multipart) {
    if (multiparts == null) throw new IllegalStateException("A block state definition can only have either variants or multipart, not both");
    multiparts.add(multipart);
    return this;
  }

  /**
   * Simple "upgrade" the old version jState to the improved version.
   */
  public static BlockStatesDefinition of(@SuppressWarnings("deprecation") JState jState) {
    final BlockStatesDefinition instance;
    if (jState.variants.isEmpty()) {
      // As 'variants' is empty, it's regarded as a 'multipart'.
      instance = ofMultiparts(jState.multiparts);
    } else {
      // It's regarded a 'variants'.
      instance = ofVariants(VariantDefinition.of(jState.variants.get(0)));
      if (jState.variants.size() > 1) {
        ARRP.LOGGER.warn("Only one variant definition is allowed, but provided multiple. Only the first one will be used.");
      }
    }
    return instance;
  }

  /**
   * Quickly create a block states definition for blocks that use single model regardless of variants. The result is like this:
   * <pre>{@code
   * { "variants": {"": {"model": "<blockModelId>"}}}
   * }</pre>
   */
  public static BlockStatesDefinition simple(Identifier blockModelId) {
    return ofVariants(VariantDefinition.ofNoVariants(new JBlockModel(blockModelId)));
  }

  /**
   * Quickly create a block states definition for blocks that use single model regardless of variants, but will have a random rotation. The result is like this:
   * <pre>{@code
   * {"variants":
   *   [ {"model": "<blockModelId>", "y": 0  },
   *     {"model": "<blockModelId>", "y": 90 },
   *     {"model": "<blockModelId>", "y": 180},
   *     {"model": "<blockModelId>", "y": 270} ] }
   * }</pre>
   */
  public static BlockStatesDefinition simpleRandomRotation(Identifier blockModelId) {
    return ofVariants(VariantDefinition.ofNoVariants(VariantDefinition.randomRotation(new JBlockModel(blockModelId))));
  }

  /**
   * Quickly create a block states definition for horizontal facing blocks. The result it like this:
   * <pre>{@code
   * {"variants":
   *   {"facing=south":
   *        {"model": "<blockModelId>", "uvlock": true, "y": 0  },
   *    "facing=west":
   *        {"model": "<blockModelId>", "uvlock": true, "y": 90 },
   *    "facing=north":
   *        {"model": "<blockModelId>", "uvlock": true, "y": 180},
   *    "facing=east":
   *        {"model": "<blockModelId>", "uvlock": true, "y": 270} }}
   * }</pre>
   *
   * @param blockModelId The identifier of the block model.
   * @param uvlock       The uvlock of the model. For blocks that can be identified rotation through their textures (for example, dispenser), it should be <code>false</code>. For blocks that can be identified rotation through shapes, and the texture of which does not matter (for example, stairs, vertical slab, button), it can be <code>true</code>.
   * @return The block states definition for the horizontal facing block.
   */
  public static BlockStatesDefinition simpleHorizontalFacing(Identifier blockModelId, boolean uvlock) {
    return ofVariants(VariantDefinition.ofHorizontalFacing(new JBlockModel(blockModelId).uvlock(uvlock)));
  }

  /**
   * Quickly create a block states definition for a slab block. This does not support additional properties except {@code "type"}. If you want to make more complex ones, which allows slabs with additional properties, you may refer to {@link VariantDefinition#composeToSlab}. The format is:
   * <pre>{@code
   * {"variants":
   *   {"type=bottom": {"model": "<bottomSlabModelId>" },
   *    "type=top":    {"model": "<topSlabModelId>"    },
   *    "type=double": {"model": "<baseSlabModelId>"   }}}}</pre>
   *
   * @param baseBlockModelId  The identifier of the base block model.
   * @param bottomSlabModelId The identifier of the bottom block model.
   * @param topSlabModelId    The identifier of the top block model
   * @return The block states definition for the slab block.
   */
  public static BlockStatesDefinition simpleSlab(Identifier baseBlockModelId, Identifier bottomSlabModelId, Identifier topSlabModelId) {
    return ofVariants(VariantDefinition.ofSlab(new JBlockModel(baseBlockModelId), bottomSlabModelId, topSlabModelId));
  }

  public static BlockStatesDefinition delegate(BlockStateSupplier delegate) {
    return new Delegate(delegate);
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

  private static final class Delegate extends BlockStatesDefinition {
    private final BlockStateSupplier delegate;

    private Delegate(BlockStateSupplier delegate) {
      super(null, null);
      this.delegate = delegate;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return delegate.get();
    }
  }
}
