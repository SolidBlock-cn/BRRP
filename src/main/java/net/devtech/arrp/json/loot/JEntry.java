package net.devtech.arrp.json.loot;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.devtech.arrp.util.CanIgnoreReturnValue;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This represents an <b>entry of a loot pool</b>. You may also directly use {@link LootPoolEntry}, converting from "{@link #delegate}" method.
 *
 * @see LootPoolEntry
 */
@SuppressWarnings("unused")
public class JEntry implements Cloneable {
  /**
   * The identifier (as string) of the type of the entry.<br>
   * Allowed values: {@code minecraft:item}, {@code minecraft:tag}, {@code minecraft:loot_table}, {@code minecraft:group}, {@code minecraft:alternatives}, {@code minecraft:sequence}, {@code minecraft:dynamic}, and {@code minecraft:empty}.
   */
  public String type;
  /**
   * The name of the entry.<p>
   * If {@code type} is {@code "item"}, the item id.<br>
   * If {@code type} is {@code "tag"}, the item tag id.<br>
   * If {@code type} is {@code "loot_table"}, the id of the loot table.
   */
  public String name;
  /**
   * The list of loot table entries.
   */
  public List<JEntry> children;
  public Boolean expand;
  public List<JFunction> functions;
  public List<JCondition> conditions;
  /**
   * The weight the item will be selected.
   */
  public Integer weight;
  public Integer quality;

  /**
   * Create an empty loot table entry.
   */
  public JEntry() {
  }

  public JEntry(String type, String name) {
    this();
    this.type = type;
    this.name = name;
  }

  /**
   * Set the type of loot table.
   *
   * @param type See {@link #type}.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Set the type of loot table from a vanilla-typed {@link LootPoolEntryType} object.
   *
   * @param type See {@link #type}.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  @ApiStatus.AvailableSince("0.8.0")
  public JEntry type(LootPoolEntryType type) {
    this.type = Objects.requireNonNull(Registry.LOOT_POOL_ENTRY_TYPE.getId(type), "The loot pool entry type seems not registered yet!").toString();
    return this;
  }

  /**
   * @param name See {@link #name}.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Add a child to the loot table.
   *
   * @param child Another loot table entry. Cannot be the loot table entry itself.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry child(JEntry child) {
    if (this == child) {
      throw new IllegalArgumentException("Can't add entry as its own child!");
    }
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(child);
    return this;
  }

  /**
   * @see JEntry#child(JEntry)
   * @deprecated unintuitive to use
   */
  @Deprecated
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry child(String child) {
    return child(RuntimeResourcePackImpl.GSON.fromJson(child, JEntry.class));
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JEntry expand(Boolean expand) {
    this.expand = expand;
    return this;
  }

  /**
   * Add a function to the loot table entry. Please do not confuse with {@link JLootTable#function(JFunction)}, which will add a function to the whole loot table.
   *
   * @param function The loot table function.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry function(JFunction function) {
    if (this.functions == null) {
      this.functions = new ArrayList<>();
    }
    this.functions.add(function);
    return this;
  }

  /**
   * Add a simple loot table function.
   *
   * @param function The id (as string) of the loot table function.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry function(String function) {
    return function(new JFunction(function));
  }

  /**
   * Add a condition to the loot table entry.
   *
   * @param condition The loot table condition.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry condition(JCondition condition) {
    if (this.conditions == null) {
      this.conditions = new ArrayList<>();
    }
    this.conditions.add(condition);
    return this;
  }

  /**
   * Add a condition to the loot table entry.
   *
   * @param condition The loot table condition.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry condition(LootCondition condition) {
    if (this.conditions == null) {
      this.conditions = new ArrayList<>();
    }
    this.conditions.add(JCondition.delegate(condition));
    return this;
  }

  /**
   * Add a condition to the loot table entry. This method can be called for objects return by {@link #delegate(LootPoolEntry.Builder)} (not {@link #delegate(LootPoolEntry)}).
   *
   * @param condition The loot table condition.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry condition(LootCondition.Builder condition) {
    if (this.conditions == null) {
      this.conditions = new ArrayList<>();
    }
    this.conditions.add(JCondition.delegate(condition));
    return this;
  }

  /**
   * Add a simple condition to the loot table entry.
   *
   * @param condition The id (as string) of the loot table condition.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry condition(String condition) {
    return condition(new JCondition(condition));
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JEntry weight(Integer weight) {
    this.weight = weight;
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JEntry quality(Integer quality) {
    this.quality = quality;
    return this;
  }

  @Override
  public JEntry clone() {
    try {
      return (JEntry) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @ApiStatus.Internal
  private static final class Delegate extends JEntry implements JsonSerializable {
    private static final Gson GSON = LootGsons.getTableGsonBuilder().create();
    private final LootPoolEntry delegate;

    private Delegate(LootPoolEntry delegate) {
      this.delegate = delegate;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return GSON.toJsonTree(delegate);
    }
  }

  /**
   * Create a special object that directly uses the serialization of vanilla-type {@link LootPoolEntry} object.
   *
   * @see #delegate(LootPoolEntry.Builder)}
   */
  @ApiStatus.AvailableSince("0.8.0")
  @Contract("_ -> new")
  public static JEntry delegate(LootPoolEntry delegate) {
    return new Delegate(delegate);
  }

  @ApiStatus.Internal
  @ApiStatus.AvailableSince("0.8.0")
  private static final class DelegateFromBuilder<T extends LootPoolEntry.Builder<T>> extends JEntry implements JsonSerializable {
    private final LootPoolEntry.Builder<T> delegate;

    private DelegateFromBuilder(LootPoolEntry.Builder<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public JEntry condition(LootCondition.Builder condition) {
      delegate.conditionally(condition);
      return this;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return Delegate.GSON.toJsonTree(delegate.build());
    }

    @Override
    public JEntry weight(Integer weight) {
      Preconditions.checkNotNull(weight, "The weight must not be null");
      if (delegate instanceof LeafEntry.Builder<?> leafEntryBuilder) {
        leafEntryBuilder.weight(weight);
      } else {
        throw new UnsupportedOperationException("Only LeafEntry.Builder can set weight!");
      }
      return this;
    }

    @Override
    public JEntry quality(Integer quality) {
      Preconditions.checkNotNull(weight, "The quality must not be null");
      if (delegate instanceof LeafEntry.Builder<?> leafEntryBuilder) {
        leafEntryBuilder.quality(quality);
      } else {
        throw new UnsupportedOperationException("Only LeafEntry.Builder can set quality!");
      }
      return this;
    }
  }

  /**
   * Create a special object that directly uses the serialization of vanilla-type {@link LootPoolEntry.Builder} object.
   */
  @ApiStatus.AvailableSince("0.8.0")
  @Contract("_ -> new")
  public static <T extends LootPoolEntry.Builder<T>> JEntry delegate(LootPoolEntry.Builder<T> delegate) {
    return new DelegateFromBuilder<>(delegate);
  }
}
