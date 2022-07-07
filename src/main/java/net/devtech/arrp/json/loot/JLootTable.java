package net.devtech.arrp.json.loot;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTable;
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
   * The type of the loot table. Allowed values: {@code minecraft:empty}, {@code minecraft:entity}, {@code minecraft:block}, {@code minecraft:chest}, {@code minecraft:fishing}, {@code minecraft:advancement_reward}, {@code minecraft:barter}, {@code minecraft:command}, {@code minecraft:selector}, {@code minecraft:advancement_entity}, and {@code minecraft:generic}.
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
   * Please note that parameter {@code pools} is a "varargs", and will be used to create a new array list with {@link Lists#newArrayList}. However, for BRRP versions before 0.7.0, it was a fixed-size list created by {@link java.util.Arrays#asList}, which will throw an {@link UnsupportedOperationException} if you add an element with {@link #pool(JPool)}, and can be seen as <b>a bug before 0.7.0</b>. Therefore, <u>if you create an object with this method and adds elements to the pool list thereafter, you'd better demand that the BRRP version is >=0.7.0</u>, which can be defined in your {@code fabric.mod.json}.
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
   * @deprecated Please directly call the constructor {@link #JLootTable(String)}.
   */
  @Deprecated
  public static JLootTable loot(String type) {
    return new JLootTable(type);
  }

  /**
   * @deprecated Please directly call {@link JEntry#JEntry() new JEntry()}.
   */
  @Deprecated
  public static JEntry entry() {
    return new JEntry();
  }

  /**
   * @see JCondition#JCondition(String)  JCondition
   * @deprecated unintuitive name
   */
  @Deprecated
  public static JCondition condition(String condition) {
    return new JCondition(condition);
  }

  /**
   * @param condition the predicate's condition identifier
   * @deprecated Please use {@link JCondition#JCondition()}
   */
  @Deprecated
  public static JCondition predicate(String condition) {
    return new JCondition(condition);
  }

  /**
   * @deprecated Please directly call {@link JFunction#JFunction(String)}.
   */
  @Deprecated
  public static JFunction function(String function) {
    return new JFunction(function);
  }

  /**
   * @deprecated Please directly call {@link JPool#JPool()}.
   */
  @Deprecated
  public static JPool pool() {
    return new JPool();
  }

  /**
   * Add a pool to the {@link #pools}.
   *
   * @param pool The loot table pool.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JLootTable pool(JPool pool) {
    if (this.pools == null) {
      this.pools = new ArrayList<>(1);
    }
    this.pools.add(pool);
    return this;
  }

  /**
   * Add a function to the {@link #functions}.
   *
   * @param function The loot table function.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JLootTable function(JFunction function) {
    if (this.functions == null) {
      this.functions = new ArrayList<>(1);
    }
    this.functions.add(function);
    return this;
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
   * Create a delegated loot table object, whose serialization will be directly used.
   *
   * @param delegate The vanilla loot table. Its serialization will be directly used when serializing.
   * @return A new object.
   */
  @Contract("_ -> new")
  public static JLootTable delegate(LootTable delegate) {
    return new FromLootTable(delegate);
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
}
