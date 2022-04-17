package net.devtech.arrp.json.loot;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
   * The exact value of rolls.
   *
   * @deprecated Please use {@link #rollsProvider}.
   */
  @Deprecated
  public transient Integer rolls;
  /**
   * The uniform value provider of rolls.
   *
   * @deprecated Please use {@link #rollsProvider}.
   */
  @Deprecated
  public transient JRoll roll;
  /**
   * The value provider of rolls.
   */
  @SerializedName("rolls")
  public LootNumberProvider rollsProvider;
  /**
   * The exact value of bonus rolls.
   *
   * @deprecated Please use {@link #bonusRollsProvider}.
   */
  @Deprecated
  public transient Integer bonus_rolls;
  /**
   * The uniform value provider of bonus rolls.
   *
   * @deprecated Please use {@link #bonusRollsProvider}.
   */
  @Deprecated
  public transient JRoll bonus_roll;
  /**
   * The value provider of bonus rolls;
   */
  @SerializedName("bonus_rolls")
  public LootNumberProvider bonusRollsProvider;

  public JPool() {
  }

  public static JPool simple(String itemId) {
    return new JPool().rolls(1).bonus(1).entry(new JEntry("item", itemId));
  }

  public static JPool ofEntries(List<JEntry> entries) {
    final JPool pool = new JPool().rolls(1).bonus(1);
    pool.entries = entries;
    return pool;
  }

  public static JPool ofEntries(JEntry... entries) {
    return ofEntries(Arrays.asList(entries));
  }

  public static JPool delegate(LootPool delegate) {
    return new Delegate(delegate);
  }

  public JPool entry(JEntry entry) {
    if (this.entries == null) {
      this.entries = new ArrayList<>(1);
    }
    this.entries.add(entry);
    return this;
  }

  public JPool condition(JCondition condition) {
    if (this.conditions == null) {
      this.conditions = new ArrayList<>(1);
    }
    this.conditions.add(condition);
    return this;
  }

  public JPool function(JFunction function) {
    if (this.functions == null) {
      this.functions = new ArrayList<>(1);
    }
    this.functions.add(function);
    return this;
  }

  /**
   * This method is kept for compatibility.
   *
   * @deprecated Please use {@link #rolls(int)} or {@link #rolls(float)}.
   */
  @Deprecated
  @CanIgnoreReturnValue
  public JPool rolls(Integer rolls) {
    this.rollsProvider = ConstantLootNumberProvider.create(rolls);
    return this;
  }

  @CanIgnoreReturnValue
  public JPool rolls(int rolls) {
    this.rollsProvider = ConstantLootNumberProvider.create(rolls);
    return this;
  }

  @CanIgnoreReturnValue
  public JPool rolls(float rolls) {
    this.rollsProvider = ConstantLootNumberProvider.create(rolls);
    return this;
  }

  @CanIgnoreReturnValue
  public JPool rolls(LootNumberProvider rolls) {
    this.rollsProvider = rolls;
    return this;
  }

  /**
   * @deprecated Please use {@link #rolls(LootNumberProvider)}.
   */
  @Deprecated
  public JPool rolls(JRoll roll) {
    this.roll = roll;
    return this;
  }

  /**
   * This method is kept for compatibility.
   *
   * @deprecated Please use {@link #bonus(int)} or {@link #bonus(float)}.
   */
  @Deprecated
  @CanIgnoreReturnValue
  public JPool bonus(Integer bonus_rolls) {
    this.bonusRollsProvider = ConstantLootNumberProvider.create(bonus_rolls);
    return this;
  }

  @CanIgnoreReturnValue
  public JPool bonus(int bonus_rolls) {
    this.bonusRollsProvider = ConstantLootNumberProvider.create(bonus_rolls);
    return this;
  }

  @CanIgnoreReturnValue
  public JPool bonus(float bonus_rolls) {
    this.bonusRollsProvider = ConstantLootNumberProvider.create(bonus_rolls);
    return this;
  }

  @CanIgnoreReturnValue
  public JPool bonus(LootNumberProvider bonusRollsProvider) {
    this.bonusRollsProvider = bonusRollsProvider;
    return this;
  }

  /**
   * @deprecated Please use {@link #bonus(LootNumberProvider)}.
   */
  @Deprecated
  public JPool bonus(JRoll bonus_roll) {
    this.bonusRollsProvider = (LootNumberProvider) bonus_roll;
    return this;
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
}
