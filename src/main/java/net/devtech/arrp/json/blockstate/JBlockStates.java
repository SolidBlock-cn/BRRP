package net.devtech.arrp.json.blockstate;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.ARRP;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.List;

/**
 * <p>A <b>"block states"</b> is the file in the {@code assets/<i>namespace</i>/blockstates} folder, which defines which models should be used when rendering a block state. It has two types:</p>
 * <ul>
 *   <li><b>variants</b> - Each block state corresponds to a block model definition ({@link JBlockModel}). You can create a variant definition through {@link #variants}.</li>
 *   <li><b>multipart</b> - Each part has a block model, and an optional condition ({@link JWhen}). If the condition is met (which means the actual block states matches to the condition), the part will be used. In this case, it's possible that one part, multiple parts or no parts will be used. You can create a multipart definition through {@link #ofMultiparts}.</li>
 * </ul>
 * <p>When adding the block states file to the resource pack, the identifier is equal to the block identifier.</p>
 * <p>This class is a simple improved version of {@link JState}. You can "upgrade" the deprecated <code>JState</code> through {@link #of(JState)}.</p>
 *
 * @author SolidBlock
 * @see net.minecraft.data.client.BlockStateModelGenerator
 * @see BlockStateSupplier
 * @since BRRP 0.6.0
 */
@SuppressWarnings("unused")
@PreferredEnvironment(Dist.CLIENT)
public class JBlockStates implements JsonSerializable {
  public final JVariants variants;
  public final List<JMultipart> multiparts;

  /**
   * The basic constructor method. Please don't directly call it. Please call {@link #ofVariants} or {@link #ofMultiparts}.
   *
   * @param variants   The variant definition. One of these two parameters must be {@code null}.
   * @param multiparts The list of multiparts. One of these two parameters must be {@code null}.
   */
  @ApiStatus.Internal
  private JBlockStates(JVariants variants, List<JMultipart> multiparts) {
    this.variants = variants;
    this.multiparts = multiparts;
  }

  /**
   * Create a new JBlockStates object with the specified variants. In this case, calling {@link #add} will throw an {@link UnsupportedOperationException}.
   *
   * @param variants The variant definition of the block states to be used.
   * @return A new JBlockStates object.
   */
  @Contract(value = "_ -> new", pure = true)
  public static JBlockStates ofVariants(JVariants variants) {
    return new JBlockStates(variants, null);
  }

  /**
   * Create a new JBlockStates object with the specified multiparts. The parameter {@code multiparts} will be directly used as a field. If it is unmodifiable, you shouldn't call methods like {@link #add}. If it is modifiable, calling {@link #add} will affect this parameter.
   *
   * @param multiparts The list of multiparts. In this case, the parameter will be directly used as the field.
   * @return A new JBlockStates object.
   */
  @Contract(value = "_ -> new", pure = true)
  public static JBlockStates ofMultiparts(List<JMultipart> multiparts) {
    return new JBlockStates(null, multiparts);
  }

  /**
   * Create a new JBlockStates object with varargs. The parameter {@code multiparts} is a vararg, and since 0.7.0 will be converted to an array list through {@link Lists#newArrayList}.<p>However, for BRRP versions before 0.7.0, it will be {@link java.util.Arrays#asList(Object[])}, which means it <i>will throw</i> {@link UnsupportedOperationException} if you call {@link #add}. This bug is fixed in 0.7.0. Therefore, <u>if you use this method and call {@link #add} thereafter, you're supposed to require at least BRRP 0.7.0</u> (which can be defined in {@code fabric.mod.json}).
   *
   * @param multiparts The varargs of multiparts.
   * @return A new JBlockStates object.
   * @since 0.7.0 The multiparts field is no longer a fixed-length list.
   */
  @Contract(value = "_ -> new", pure = true)
  public static JBlockStates ofMultiparts(JMultipart... multiparts) {
    return ofMultiparts(Lists.newArrayList(multiparts));
  }

  /**
   * Add a variant to the definition for a block states definition .
   *
   * @throws IllegalStateException if the block states definition is for multiparts (for example, created from {@link #ofMultiparts(JMultipart...)}).
   */
  @Contract(value = "_, _ -> this", mutates = "this")
  public JBlockStates addVariant(String variant, JBlockModel... modelDefinition) {
    if (variant == null) throw new IllegalStateException("A block state definition can only have either variants or multiparts, not both");
    variants.addVariant(variant, modelDefinition);
    return this;
  }

  /**
   * Add a multipart definition for a block states definition of multiparts.
   *
   * @throws IllegalStateException if the block states definition is for variants (for example, created from {@link #ofVariants(JVariants)}).
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JBlockStates add(JMultipart multipart) {
    if (multiparts == null) throw new IllegalStateException("A block state definition can only have either variants or multipart, not both");
    multiparts.add(multipart);
    return this;
  }

  /**
   * Simply "upgrades" the old version {@code JState} object to the improved version.
   */
  public static JBlockStates of(@SuppressWarnings("deprecation") JState jState) {
    final JBlockStates instance;
    if (jState.variants.isEmpty()) {
      // As 'variants' is empty, it's regarded as a 'multipart'.
      instance = ofMultiparts(jState.multiparts);
    } else {
      // It's regarded a 'variants'.
      instance = ofVariants(JVariants.upgrade(jState.variants.get(0)));
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
  public static JBlockStates simple(Identifier blockModelId) {
    return ofVariants(JVariants.ofNoVariants(new JBlockModel(blockModelId)));
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
  public static JBlockStates simpleRandomRotation(Identifier blockModelId) {
    return ofVariants(JVariants.ofNoVariants(JVariants.ofRandomRotation(new JBlockModel(blockModelId))));
  }

  /**
   * Quickly create a block states definition for horizontal facing blocks. The result is like this:
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
  public static JBlockStates simpleHorizontalFacing(Identifier blockModelId, boolean uvlock) {
    return ofVariants(JVariants.ofHorizontalFacing(new JBlockModel(blockModelId).uvlock(uvlock)));
  }

  /**
   * Quickly create a block states definition for a slab block. This does not support additional properties except {@code "type"}. If you want to make more complex ones, which allows slabs with additional properties, you may refer to {@link JVariants#composeToSlab}. The format is:
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
  public static JBlockStates simpleSlab(Identifier baseBlockModelId, Identifier bottomSlabModelId, Identifier topSlabModelId) {
    return ofVariants(JVariants.ofSlab(new JBlockModel(baseBlockModelId), bottomSlabModelId, topSlabModelId));
  }

  /**
   * Create a delegated block states object. The serialization of the delegate will be directly used.
   *
   * @param delegate The vanilla block state supplier, whose serialization will be directly used.
   * @return The delegated object.
   */
  @Contract("_ -> new")
  public static JBlockStates delegate(BlockStateSupplier delegate) {
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

  @ApiStatus.Internal
  private static final class Delegate extends JBlockStates {
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
