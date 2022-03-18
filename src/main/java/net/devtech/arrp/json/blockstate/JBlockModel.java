package net.devtech.arrp.json.blockstate;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * The simple block model that usually represents as the value in {@link JVariant}.<br>
 * <B>Note: </B>This class is used as a model definition in the block states definition file. To represent a model's content, please use {@link net.devtech.arrp.json.models.JModel}.
 */
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
   * Simple create an array block model definition with random rotations. For example,
   * <pre>
   *   {@code simpleRandom(new SimpleBlockModelDefinition(new Identifier("minecraft","block/dirt"))}
   * </pre>
   * which returns an array block model definition like this:
   * <pre>
   *   [{  "model": "minecraft:block/dirt"  },
   *    {  "model": "minecraft:block/dirt",  "y": 90  },
   *    {  "model": "minecraft:block/dirt",  "y": 180  },
   *    {  "model": "minecraft:block/dirt",  "y": 270  }  ]
   * </pre>
   */
  public static JBlockModel[] simpleRandom(JBlockModel basicModel) {
    final JBlockModel[] multiple = new JBlockModel[4];
    for (int i = 0; i < 4; i++) {
      final JBlockModel cloned = basicModel.clone();
      cloned.y = 90 * i;
      multiple[i] = cloned;
    }
    return multiple;
  }

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

  public JBlockModel() {
  }

  @Deprecated
  public JBlockModel(String model) {
    this(new Identifier(model));
  }

  /**
   * @see JState#model(String)
   */
  public JBlockModel(Identifier model) {
    this.model = model;
  }

  @Override
  public JBlockModel clone() {
    try {
      return (JBlockModel) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  public JBlockModel x(int x) {
    this.x = x;
    return this;
  }

  public JBlockModel y(int y) {
    this.y = y;
    return this;
  }

  public JBlockModel uvlock() {
    this.uvlock = true;
    return this;
  }

  public JBlockModel uvlock(boolean uvlock) {
    this.uvlock = uvlock;
    return this;
  }

  public JBlockModel weight(int weight) {
    this.weight = weight;
    return this;
  }
}
