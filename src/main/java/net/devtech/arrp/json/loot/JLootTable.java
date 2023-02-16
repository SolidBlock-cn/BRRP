package net.devtech.arrp.json.loot;

import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @see LootTable
 */
public class JLootTable implements Cloneable {
  /**
   * The type of the loot table. Allowed values can be found in {@link LootContextTypes}.
   */
  public final String type;
  /**
   * The list of loot table pools.
   */
  public List<JPool> pools;
  /**
   * The list of loot table functions.
   */
  public List<JFunction> functions;

  /**
   * Create a simple loot table.
   *
   * @param type The loot table type. Please refer to {@link #type}.
   */
  public JLootTable(String type) {
    this.type = type;
  }

  /**
   * Create a simple loot table with the specified type and pool.
   *
   * @param type The loot table type. Please refer to {@link #type}.
   * @param pool The single loot pool of the loot table.
   */
  public JLootTable(String type, JPool pool) {
    this(type, Lists.newArrayList(pool));
  }

  /**
   * Create a simple loot table with the specified type and pools.<p>
   * Please note that parameter {@code pools} is a "varargs", and will be used to create a new array list with {@link Lists#newArrayList}.
   *
   * @param type  The loot table type. Please refer to {@link #type}.
   * @param pools The loot table pools varargs.
   * @since 0.7.0 The {@code pools} field is no longer fixed-length.
   */
  public JLootTable(String type, JPool... pools) {
    this(type, Lists.newArrayList(pools));
  }

  /**
   * Create a simple loot table with the specified type and the list of pools.<p>
   * The parameter {@code pools} is directly used as a field. If it is unmodifiable, you should not call {@link #pool} to modify it.
   *
   * @param type  The loot table type. Please refer to {@link #type}.
   * @param pools The list of loot table pools. It will be directly used as the field.
   */
  public JLootTable(String type, List<JPool> pools) {
    this(type);
    this.pools = pools;
  }

  /**
   * Add a pool to the {@link #pools}.
   *
   * @param pool The loot table pool.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JLootTable pool(JPool pool) {
    if (this.pools == null) {
      this.pools = new ArrayList<>(1);
    }
    this.pools.add(pool);
    return this;
  }

  /**
   * Add a pool to the {@link #pools}. The method can be use for objects returned by {@link #delegate(LootTable.Builder)}.
   *
   * @param pool The loot table pool.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.0")
  public JLootTable pool(LootPool pool) {
    if (this.pools == null) {
      this.pools = new ArrayList<>(1);
    }
    this.pools.add(JPool.delegate(pool));
    return this;
  }

  /**
   * Add a pool builder to the {@link #pools} and build it. The method can be use for objects returned by {@link #delegate(LootTable.Builder)}.
   *
   * @param pool The loot table pool.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.0")
  public JLootTable pool(LootPool.Builder pool) {
    if (this.pools == null) {
      this.pools = new ArrayList<>(1);
    }
    this.pools.add(JPool.delegate(pool.build()));
    return this;
  }

  /**
   * Add a function to the {@link #functions}.
   *
   * @param function The loot table function.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JLootTable function(JFunction function) {
    if (this.functions == null) {
      this.functions = new ArrayList<>(1);
    }
    this.functions.add(function);
    return this;
  }

  /**
   * Add a vanilla-type function to the loot table.
   *
   * @param function The loot table function.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.0")
  public JLootTable function(LootFunction function) {
    return function(JFunction.delegate(function));
  }

  /**
   * Add a vanilla-type function to the loot table.
   *
   * @param function The loot table function.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.0")
  public JLootTable function(LootFunction.Builder function) {
    return function(JFunction.delegate(function));
  }

  /**
   * Create the simplest block loot table, which can be harvested by hand, and drops itself. The result is like this:
   * <pre>{@code
   * { "type": "minecraft:block",
   *   "pools": [{
   *     "rolls": 1.0,
   *     "bonus_rolls": 0.0,
   *     "entries": [{ "type": "minecraft:item", "name": "<blockId>"}],
   *     "conditions": [{"condition": "minecraft:survives_explosion"}]
   *   }]}
   * }</pre>
   *
   * @param blockId The id (as string) of the block.
   * @return The simplest block loot table.
   * @see net.minecraft.data.server.loottable.BlockLootTableGenerator#drops(ItemConvertible)
   */
  @Contract(value = "_ -> new", pure = true)
  public static JLootTable simple(String blockId) {
    return new JLootTable("minecraft:block").pool(JPool.simple(blockId).condition(new JCondition("survives_explosion")));
  }

  /**
   * Create a simple loot table with the specified type and the list of pools.<p>
   * The parameter {@code pools} is directly used as a field. If it is unmodifiable, you should not call {@link #pool} to modify it.
   *
   * @param type  The loot table type. Please refer to {@link #type}.
   * @param pools The list of loot table pools. It will be directly used as the field.
   * @return A new loot table.
   */
  @Contract(value = "_, _ -> new", pure = true)
  public static JLootTable ofPools(String type, List<JPool> pools) {
    return new JLootTable(type, pools);
  }

  /**
   * Create a simple loot table with the specified type and pools.<p>
   * Please note that parameter {@code pools} is a "varargs", and will be used to create a new array list with {@link Lists#newArrayList}. However, for BRRP versions before 0.7.0, it was a fixed-size list created by {@link java.util.Arrays#asList}, which will throw an {@link UnsupportedOperationException} if you add an element with {@link #pool(JPool)}, and can be seen as <b>a bug before 0.7.0</b>. Therefore, <u>if you create an object with this method and adds elements to the pool list thereafter, you'd better demand that the BRRP version is >=0.7.0</u>, which can be defined in your {@code fabric.mod.json}.
   *
   * @param type  The loot table type. Please refer to {@link #type}.
   * @param pools The loot table pools varargs.
   * @return A new loot table.
   * @since 0.7.0 The {@code pools} field is no longer fixed-length.
   */
  @Contract(value = "_, _ -> new", pure = true)
  public static JLootTable ofPools(String type, JPool... pools) {
    return ofPools(type, Lists.newArrayList(pools));
  }

  /**
   * Create a simple loot table with the specified type, and a sole pool with the specified entries.
   *
   * @param type    The loot table type.
   * @param entries The list of entries of the pool.
   * @return A new JLootTable object which has one pool.
   */
  @Contract(value = "_, _ -> new", pure = true)
  public static JLootTable ofEntries(String type, List<JEntry> entries) {
    return ofPools(type, JPool.ofEntries(entries));
  }

  /**
   * Create a simple loot table with the specified type, and a sole pool with the specified entries.
   *
   * @param type    The loot table type.
   * @param entries The varargs entries of the pool.
   * @return A new JLootTable object which has one pool.
   * @since 0.7.0 The list of entries is no longer fixed-length. See {@link JPool#ofEntries(JEntry...)}.
   */
  @Contract(value = "_, _ -> new", pure = true)
  public static JLootTable ofEntries(String type, JEntry... entries) {
    return ofPools(type, JPool.ofEntries(entries));
  }

  /**
   * Create a delegated loot table object, whose serialization will be directly used. Methods like {@link #function(LootFunction)}, {@link #pool(LootPool)} will directly apply to that loot table object.
   *
   * @param delegate The vanilla loot table. Its serialization will be directly used when serializing.
   * @return A new object.
   */
  @Contract("_ -> new")
  @ApiStatus.AvailableSince("0.8.0")
  public static JLootTable delegate(LootTable.Builder delegate) {
    return new FromLootTableBuilder(delegate);
  }

  @Override
  public JLootTable clone() {
    try {
      return (JLootTable) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @ApiStatus.Internal
  private static final class FromLootTable extends JLootTable implements JsonSerializable {
    private transient final LootTable delegate;
    private static final Gson GSON = LootGsons.getTableGsonBuilder().create();

    public FromLootTable(LootTable lootTable) {
      super(null);
      delegate = lootTable;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return GSON.toJsonTree(delegate);
    }
  }

  @ApiStatus.Internal
  @ApiStatus.AvailableSince("0.8.0")
  private static final class FromLootTableBuilder extends JLootTable implements JsonSerializable {
    private transient final LootTable.Builder delegate;

    private FromLootTableBuilder(LootTable.Builder delegate) {
      super(null);
      this.delegate = delegate;
    }

    @Override
    public JLootTable function(JFunction function) {
      throw new UnsupportedOperationException();
    }

    @Override
    public JLootTable function(LootFunction function) {
      delegate.apply(() -> function);
      return this;
    }

    @Override
    public JLootTable function(LootFunction.Builder function) {
      delegate.apply(function);
      return this;
    }

    @Override
    public JLootTable pool(LootPool pool) {
      delegate.pool(new LootPool.Builder() {
        @Override
        public LootPool build() {
          return pool;
        }
      });
      return this;
    }

    @Override
    public JLootTable pool(LootPool.Builder pool) {
      delegate.pool(pool);
      return this;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return FromLootTable.GSON.toJsonTree(delegate.build());
    }
  }
}
