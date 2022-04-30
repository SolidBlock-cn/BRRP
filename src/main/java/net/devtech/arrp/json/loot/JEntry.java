package net.devtech.arrp.json.loot;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.entry.LootPoolEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry type(String type) {
    this.type = type;
    return this;
  }

  /**
   * @param name See {@link #name}.
   */
  @CanIgnoreReturnValue
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
  @CanIgnoreReturnValue
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

  @CanIgnoreReturnValue
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
  @CanIgnoreReturnValue
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
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry function(String function) {
    return function(new JFunction(function));
  }

  /**
   * Add a condition to the loot table entry.
   *
   * @param condition The loot table condition.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry condition(JCondition condition) {
    if (this.conditions == null) {
      this.conditions = new ArrayList<>();
    }
    this.conditions.add(condition);
    return this;
  }

  /**
   * Add a simple condition to the loot table entry.
   *
   * @param condition The id (as string) of the loot table condition.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry condition(String condition) {
    return condition(new JCondition(condition));
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JEntry weight(Integer weight) {
    this.weight = weight;
    return this;
  }

  @CanIgnoreReturnValue
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

  public static JEntry delegate(LootPoolEntry delegate) {
    return new Delegate(delegate);
  }
}
