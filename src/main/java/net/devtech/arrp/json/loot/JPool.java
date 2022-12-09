package net.devtech.arrp.json.loot;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @see net.minecraft.loot.LootPool
 */
public class JPool implements Cloneable {
  /**
   * The list of conditions of the pool. When Minecraft is applying the loot table, the pool is selected only when the condition is met. If there are more than one conditions, they must be all met.
   */
  public List<JCondition> conditions;
  /**
   * The functions that will be applied to the item stack.
   */
  public List<JFunction> functions;
  /**
   * Entries of items that may be used.
   */
  public List<JEntry> entries;
  /**
   * The value provider of rolls.
   */
  @SerializedName("rolls")
  public LootNumberProvider rolls;
  /**
   * The value provider of bonus rolls;
   */
  @SerializedName("bonus_rolls")
  public LootNumberProvider bonusRolls;

  /**
   * Create a simple loot table pool with no entry. You may also consider other static methods, such as {@link #simple} or {@link #ofEntries}.
   */
  public JPool() {
  }

  /**
   * Create a simplest loot pool, with only one roll and a sole entry of item.
   *
   * @param itemId The identifier of the item.
   * @return A new JPool object.
   */
  @Contract(value = "_ -> new", pure = true)
  public static JPool simple(String itemId) {
    return ofEntries(new JEntry("item", itemId));
  }

  /**
   * Create a simple loot pool, with only one roll and the list of entries. The list will be directly used as a field.
   *
   * @param entries The list of loot pool entries. If will be directly used as the field. If it is unmodifiable, you should not call methods that modify it, such as {@link #entry}.
   * @return A new JPool object with the specified entries.
   */
  @Contract(value = "_ -> new", pure = true)
  public static JPool ofEntries(List<JEntry> entries) {
    final JPool pool = new JPool().rolls(1).bonus(1);
    pool.entries = entries;
    return pool;
  }

  /**
   * Create a simple loot pool, with only one roll and the specified entries.<p>Please note that parameter {@code entries} is a "varargs", and will be used to create a new array list with {@link Lists#newArrayList}. However, for BRRP versions before 0.7.0, it was a fixed-size list created by {@link java.util.Arrays#asList}, which will throw an {@link UnsupportedOperationException} if you add an element with {@link #entry(JEntry)}, and can be seen as <b>a bug before 0.7.0</b>. Therefore, <u>if you create an object with this method and adds elements to the list of entries thereafter, you'd better demand that the BRRP version is >=0.7.0</u>, which can be defined in your {@code fabric.mod.json}.
   *
   * @param entries The loot pool entries.
   * @return A new JPool object with the specified entries.
   * @since The {@link #entries} field is no longer fixed-length.
   */
  @Contract(value = "_ -> new", pure = true)
  public static JPool ofEntries(JEntry... entries) {
    return ofEntries(Lists.newArrayList(entries));
  }

  /**
   * Create a pool object, using the serialization of a vanilla LootPool object.
   *
   * @see #delegate(LootPool.Builder).
   */
  @Contract("_ -> new")
  public static JPool delegate(LootPool delegate) {
    return new Delegate(delegate);
  }

  /**
   * Create a pool object, using the serialization of a vanilla LootPool object.
   */
  @Contract("_ -> new")
  @ApiStatus.AvailableSince("0.8.0")
  public static JPool delegate(LootPool.Builder delegate) {
    return new DelegateFromBuilder(delegate);
  }

  /**
   * Add an entry to the loot table. If {@link #entries} is null, it will be created as a new array list.
   *
   * @param entry The loot table entry.
   * @return The loot table itself, allowing chained call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JPool entry(JEntry entry) {
    if (this.entries == null) {
      this.entries = new ArrayList<>(4);
    }
    this.entries.add(entry);
    return this;
  }

  /**
   * Add an entry to the loot table. If {@link #entries} is null, it will be created as a new array list. This method can be called by objects returned by {@link #delegate(LootPool.Builder)} (not {@link #delegate(LootPool)}).
   *
   * @param entry The vanilla-type loot table entry.
   * @return The loot table itself, allowing chained call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.0")
  public JPool entry(LootPoolEntry entry) {
    return entry(JEntry.delegate(entry));
  }

  /**
   * Add an entry to the loot table. If {@link #entries} is null, it will be created as a new array list. This method can be called by objects returned by {@link #delegate(LootPool.Builder)} (not {@link #delegate(LootPool)}).
   *
   * @param entry The vanilla-type loot table entry.
   * @return The loot table itself, allowing chained call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.0")
  public <T extends LootPoolEntry.Builder<T>> JPool entry(LootPoolEntry.Builder<T> entry) {
    return entry(JEntry.delegate(entry));
  }

  /**
   * Add a condition to the loot table itself. If {@link #conditions} is null, it will be created as a new array list.
   *
   * @param condition The loot table condition.
   * @return The loot table itself, allowing chained call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JPool condition(JCondition condition) {
    if (this.conditions == null) {
      this.conditions = new ArrayList<>(4);
    }
    this.conditions.add(condition);
    return this;
  }

  /**
   * Add a condition to the loot table itself. If {@link #conditions} is null, it will be created as a new array list. This method can be called by objects returned by {@link #delegate(LootPool.Builder)} (not {@link #delegate(LootPool)}).
   *
   * @param condition The vanilla-type loot table condition.
   * @return The loot table itself, allowing chained call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.0")
  public JPool condition(LootCondition condition) {
    return condition(JCondition.delegate(condition));
  }

  /**
   * Add a condition to the loot table itself. If {@link #conditions} is null, it will be created as a new array list. This method can be called by objects returned by {@link #delegate(LootPool.Builder)} (not {@link #delegate(LootPool)}).
   *
   * @param condition The vanilla-type loot table condition.
   * @return The loot table itself, allowing chained call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.0")
  public JPool condition(LootCondition.Builder condition) {
    return condition(JCondition.delegate(condition));
  }

  /**
   * Add a function to the loot table itself. If {@link #functions} is null, it will be created as a new array list.
   *
   * @param function The loot table function.
   * @return The loot table itself, allowing chained call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JPool function(JFunction function) {
    if (this.functions == null) {
      this.functions = new ArrayList<>(4);
    }
    this.functions.add(function);
    return this;
  }

  /**
   * Add a function to the loot table itself. If {@link #functions} is null, it will be created as a new array list. This method can be called by objects returned by {@link #delegate(LootPool.Builder)} (not {@link #delegate(LootPool)}).
   *
   * @param function The vanilla-type loot table function.
   * @return The loot table itself, allowing chained call.
   */
  @CanIgnoreReturnValue
  @ApiStatus.AvailableSince("0.8.0")
  @Contract(value = "_ -> this", mutates = "this")
  public JPool function(LootFunction function) {
    return function(JFunction.delegate(function));
  }

  /**
   * Add a function to the loot table itself. If {@link #functions} is null, it will be created as a new array list. This method can be called by objects returned by {@link #delegate(LootPool.Builder)} (not {@link #delegate(LootPool)}).
   *
   * @param function The vanilla-type loot table function.
   * @return The loot table itself, allowing chained call.
   */
  @CanIgnoreReturnValue
  @ApiStatus.AvailableSince("0.8.0")
  @Contract(value = "_ -> this", mutates = "this")
  public JPool function(LootFunction.Builder function) {
    return function(JFunction.delegate(function));
  }

  /**
   * This method is kept for compatibility.
   *
   * @deprecated Please use {@link #rolls(int)} or {@link #rolls(float)}.
   */
  @Deprecated
  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JPool rolls(Integer rolls) {
    return rolls(ConstantLootNumberProvider.create(rolls));
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JPool rolls(int rolls) {
    return rolls(ConstantLootNumberProvider.create(rolls));
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JPool rolls(float rolls) {
    return rolls(ConstantLootNumberProvider.create(rolls));
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JPool rolls(LootNumberProvider rolls) {
    this.rolls = rolls;
    return this;
  }

  /**
   * @deprecated Please use {@link #rolls(LootNumberProvider)}.
   */
  @Deprecated
  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JPool rolls(JRoll roll) {
    return rolls(roll.asLootNumberProvider());
  }

  /**
   * This method is kept for compatibility.
   *
   * @deprecated Please use {@link #bonus(int)} or {@link #bonus(float)}.
   */
  @Deprecated
  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JPool bonus(Integer bonus_rolls) {
    return bonus(ConstantLootNumberProvider.create(bonus_rolls));
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JPool bonus(int bonus_rolls) {
    return bonus(ConstantLootNumberProvider.create(bonus_rolls));
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JPool bonus(float bonus_rolls) {
    return bonus(ConstantLootNumberProvider.create(bonus_rolls));
  }

  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JPool bonus(LootNumberProvider bonusRollsProvider) {
    this.bonusRolls = bonusRollsProvider;
    return this;
  }

  /**
   * @deprecated Please use {@link #bonus(LootNumberProvider)}.
   */
  @Deprecated
  @Contract(value = "_ -> this", mutates = "this")
  @CanIgnoreReturnValue
  public JPool bonus(JRoll bonus_roll) {
    return bonus(bonus_roll.asLootNumberProvider());
  }

  @Override
  public JPool clone() {
    try {
      return (JPool) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  /**
   * This class is kept for compatibility.
   */
  @Deprecated
  public static class Serializer implements JsonSerializer<JPool> {
    @Override
    public JsonElement serialize(JPool src, Type typeOfSrc, JsonSerializationContext context) {
      return RuntimeResourcePackImpl.GSON.toJsonTree(src, typeOfSrc);
    }
  }

  @ApiStatus.Internal
  private static final class Delegate extends JPool implements JsonSerializable {
    private final LootPool delegate;
    private static final Gson GSON = LootGsons.getTableGsonBuilder().create();

    private Delegate(LootPool delegate) {
      this.delegate = delegate;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return GSON.toJsonTree(delegate);
    }
  }

  @ApiStatus.Internal
  private static final class DelegateFromBuilder extends JPool implements JsonSerializable {
    private final LootPool.Builder delegate;

    private DelegateFromBuilder(LootPool.Builder delegate) {
      this.delegate = delegate;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return Delegate.GSON.toJsonTree(delegate.build());
    }

    @Override
    public JPool rolls(LootNumberProvider rolls) {
      delegate.rolls(rolls);
      return this;
    }

    @Override
    public JPool bonus(LootNumberProvider bonusRollsProvider) {
      delegate.bonusRolls(bonusRollsProvider);
      return this;
    }

    @Override
    public JPool condition(LootCondition condition) {
      delegate.conditionally(() -> condition);
      return this;
    }

    @Override
    public JPool condition(LootCondition.Builder condition) {
      delegate.conditionally(condition);
      return this;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public JPool entry(LootPoolEntry entry) {
      delegate.with(new LootPoolEntry.Builder() {
        @Override
        protected LootPoolEntry.Builder getThisBuilder() {
          return this;
        }

        @Override
        public LootPoolEntry build() {
          return entry;
        }
      });
      return this;
    }

    @Override
    public <T extends LootPoolEntry.Builder<T>> JPool entry(LootPoolEntry.Builder<T> entry) {
      delegate.with(entry);
      return this;
    }

    @Override
    public JPool function(LootFunction function) {
      delegate.apply(() -> function);
      return this;
    }

    @Override
    public JPool function(LootFunction.Builder function) {
      delegate.apply(function);
      return this;
    }
  }
}
