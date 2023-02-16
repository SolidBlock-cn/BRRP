package net.devtech.arrp.json.blockstate;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.fabricmc.api.EnvType;
import net.minecraft.block.Block;
import net.minecraft.data.client.TexturedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * The simple block model that usually represents as the value in {@link JVariants}. It defines the model id and some simple rotations of that model.
 * <p>
 * <B>Note: </B>This class is used as a model definition in the block states definition file. To represent a model's content, please use {@link net.devtech.arrp.json.models.JModel}.
 */
@SuppressWarnings("unused")
@PreferredEnvironment(EnvType.CLIENT)
public class JBlockModel implements Cloneable {
  /**
   * The model identifier. Usually compulsory. For example, {@code "minecraft:block/oak_slab_top"}.
   */
  public Identifier model;
  /**
   * The x rotation used. Must be times of 90.
   */
  public @Nullable Integer x;
  /**
   * The y rotation used. Must be times of 90.
   */
  public @Nullable Integer y;
  /**
   * Determines whether lock the uv of texture used.
   */
  public @Nullable Boolean uvlock;
  /**
   * This field is used in the element of block definition arrays, which affect the probability that it will be selected in the random list.
   */
  public @Nullable Integer weight;

  /**
   * This constructor method usually directly creates a full instance with parameters, so fields are usually nullable.
   */
  public JBlockModel(Identifier model, @Nullable Integer x, @Nullable Integer y, @Nullable Boolean uvlock, @Nullable Integer weight) {
    this.model = model;
    this.x = x;
    this.y = y;
    this.uvlock = uvlock;
    this.weight = weight;
  }

  /**
   * Create a new, empty model definition. This is usually not recommended, as you can directly assign a model id.
   */
  public JBlockModel() {
  }

  /**
   * @param modelId The identifier of the block model. For example, <code>{@code new Identifier("minecraft", "block/oak_slab_top")}</code>.
   * @see #JBlockModel(String, String)
   */
  public JBlockModel(Identifier modelId) {
    this.model = modelId;
  }

  /**
   * @param idNamespace The namespace of the block model id. For example, {@code "minecraft"}.
   * @param idPath      The path of the block model id. For example, {@code "block/oak_slab_top"}.
   */
  public JBlockModel(String idNamespace, String idPath) {
    this(new Identifier(idNamespace, idPath));
  }

  /**
   * Simple create an array block model definition with random rotations. For example,
   * <pre>
   *   {@code simpleRandom(new SimpleBlockModelDefinition(new Identifier("minecraft","block/dirt"))}
   * </pre>
   * which returns an array block model definition like this:
   * <pre>{@code
   *   [{ "model": "minecraft:block/dirt"  },
   *    { "model": "minecraft:block/dirt", "y": 90  },
   *    { "model": "minecraft:block/dirt", "y": 180  },
   *    { "model": "minecraft:block/dirt", "y": 270  }  ]
   * }</pre>
   *
   * @see net.minecraft.data.client.BlockStateModelGenerator#registerRandomHorizontalRotations(TexturedModel.Factory, Block...)
   */
  @Contract(value = "_ -> new", pure = true)
  public static JBlockModel[] simpleRandom(JBlockModel basicModel) {
    final JBlockModel[] multiple = new JBlockModel[4];
    for (int i = 0; i < 4; i++) {
      final JBlockModel cloned = basicModel.clone();
      cloned.y = 90 * i;
      multiple[i] = cloned;
    }
    return multiple;
  }

  @Override
  public JBlockModel clone() {
    try {
      return (JBlockModel) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  /**
   * Set the model id.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JBlockModel modelId(Identifier model) {
    this.model = model;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JBlockModel x(int x) {
    this.x = x;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JBlockModel y(int y) {
    this.y = y;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "-> this", mutates = "this")
  public JBlockModel uvlock() {
    this.uvlock = true;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JBlockModel uvlock(boolean uvlock) {
    this.uvlock = uvlock;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JBlockModel weight(int weight) {
    this.weight = weight;
    return this;
  }
}
