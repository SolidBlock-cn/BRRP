package net.devtech.arrp.json.loot;

import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
   * @param type The loot table type. Allowed values: {@code minecraft:empty}, {@code minecraft:entity}, {@code minecraft:block}, {@code minecraft:chest}, {@code minecraft:fishing}, {@code minecraft:advancement_reward}, {@code minecraft:barter}, {@code minecraft:command}, {@code minecraft:selector}, {@code minecraft:advancement_entity}, and {@code minecraft:generic}.
   */
  public JLootTable(String type) {
    this.type = type;
  }

  public JLootTable(String type, JPool pool) {
    this(type, Lists.newArrayList(pool));
  }

  public JLootTable(String type, JPool... pools) {
    this(type, Arrays.asList(pools));
  }

  public JLootTable(String type, List<JPool> pools) {
    this(type);
    this.pools = pools;
  }

  /**
   * @deprecated Please directly call the constructor {@link #JLootTable(String)}.
   */
  public static JLootTable loot(String type) {
    return new JLootTable(type);
  }

  /**
   * @deprecated Please directly call {@link JEntry#JEntry() new JEntry()}.
   */
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
   * @deprecated Please directly call {@link JRoll#JRoll(int, int)}.
   */
  @Deprecated
  public static JRoll roll(int min, int max) {
    return new JRoll(min, max);
  }

  /**
   * Add a pool to the {@link #pools}.
   *
   * @param pool The loot table pool.
   */
  @CanIgnoreReturnValue
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
  @CanIgnoreReturnValue
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
  public static JLootTable simple(String blockId) {
    return new JLootTable("minecraft:block").pool(JPool.simple(blockId).condition(new JCondition("survives_explosion")));
  }

  public static JLootTable ofPools(String type, List<JPool> pools) {
    final JLootTable lootTable = new JLootTable(type);
    lootTable.pools = pools;
    return lootTable;
  }

  public static JLootTable ofPools(String type, JPool... pools) {
    return ofPools(type, Arrays.asList(pools));
  }

  public static JLootTable ofEntries(String type, List<JEntry> entries) {
    return ofPools(type, JPool.ofEntries(entries));
  }

  public static JLootTable ofEntries(String type, JEntry... entries) {
    return ofPools(type, JPool.ofEntries(entries));
  }

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
