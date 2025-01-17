package pers.solid.brrp.v1.api;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.stat.StatType;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>An object representing a language file, with some methods conveniently to add some objects. Some of the methods are from Fabric Data Generation API.</p>
 * <p>It is essentially a map, in which the key is the translation key, and the value is the translated value. Both keys and values are strings.</p>
 * <p>This is a simple example:</p>
 * <pre>{@code
 * LanguageProvider enUs = LanguageProvider.create()
 *   .add("custom.key", "Custom Value")
 *   .add(MyModBlocks.EXAMPLE_BLOCK, "Example Block")
 *   .add(MyModItems.EXAMPLE_ITEM, "Example Item");
 *
 * LanguageProvider zhTw = LanguageProvider.create()
 *   .add("custom.value", "自訂值")
 *   .add(MyModBlocks.EXAMPLE_BLOCK, "示例方塊")
 *   .add(MyModBlocks.EXAMPLE_ITEM, "示例物品");
 *
 * myRuntimeResourcePack.addLang(Identifier.of("my_mod", "en_us"), enUs)
 * myRuntimeResourcePack.addLang(Identifier.of("my_mod", "zh_tw"), zhTw)
 * }
 * </pre>
 *
 * @see RuntimeResourcePack#addLang(Identifier, LanguageProvider)
 */
public interface LanguageProvider {
  @ApiStatus.AvailableSince("1.1.0")
  Codec<LanguageProvider> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(Impl::new, LanguageProvider::content);

  /**
   * Create a new object using a simple hash map. The key is not sorted.
   */
  @Contract("-> new")
  static Impl<HashMap<String, String>> create() {
    return new Impl<>(new HashMap<>());
  }

  /**
   * Create a new object using a tree map. The key is sorted. The sorting of keys does not matter, but may make it more readable when in form of files.
   */
  @Contract("-> new")
  static Impl<TreeMap<String, String>> createSorted() {
    return new Impl<>(new TreeMap<>());
  }

  /**
   * Create a new object that directly uses the map. The modification to that map will directly implement this object.
   */
  @Contract("_ -> new")
  static <T extends Map<String, String>> Impl<T> create(T content) {
    return new Impl<>(content);
  }

  /**
   * Add a key-value pair to this object.
   *
   * @param key   The translation key.
   * @param value The translated string.
   */
  @Contract(value = "_, _ -> this")
  LanguageProvider add(@NotNull String key, String value);

  /**
   * The content of the object, representing as a map. Mutations to that map will directly influence this object.
   */
  Map<String, String> content();

  /**
   * Adds a translation for an {@link Item}. Please ensure that the item is registered.
   *
   * @param item  The {@link Item} to get the translation key from.
   * @param value The translated name of the item.
   */
  @Contract(value = "_, _ -> this")
  default LanguageProvider add(@NotNull Item item, String value) {
    return add(item.getTranslationKey(), value);
  }

  /**
   * Adds a translation for a {@link Block}. Please ensure that the block is registered.
   *
   * @param block The {@link Block} to get the translation key from.
   * @param value The translated name of the block.
   */
  @Contract(value = "_, _ -> this")
  default LanguageProvider add(@NotNull Block block, String value) {
    return add(block.getTranslationKey(), value);
  }

  /**
   * Adds a translation for an {@link ItemGroup}.
   *
   * @param group The {@link ItemGroup} to get the translation key from.
   * @param value The translated name of the item group.
   */
  default LanguageProvider add(@NotNull ItemGroup group, String value) {
    final TextContent content = group.getDisplayName().getContent();

    if (content instanceof TranslatableTextContent translatableTextContent) {
      return add(translatableTextContent.getKey(), value);
    }

    throw new UnsupportedOperationException("Cannot add language entry for ItemGroup (%s) as the display name is not translatable.".formatted(group.getDisplayName().getString()));
  }

  /**
   * Adds a translation for an {@link EntityType}. The entity type should be registered.
   *
   * @param entityType The {@link EntityType} to get the translation key from.
   * @param value      The translated name of the entity type.
   */
  @Contract(value = "_, _ -> this")
  default LanguageProvider add(EntityType<?> entityType, String value) {
    return add(entityType.getTranslationKey(), value);
  }

  /**
   * Adds a translation for an {@link Enchantment}.
   *
   * @param enchantment The {@link Enchantment} to get the translation key from.
   * @param value       The translated name of the enchantment.
   */
  @Contract(value = "_, _ -> this")
  default LanguageProvider add(@NotNull RegistryKey<Enchantment> enchantment, String value) {
    return add(Util.createTranslationKey("enchantment", enchantment.getValue()), value);
  }

  /**
   * Adds a translation for an {@link EntityAttribute}.
   *
   * @param entityAttribute The {@link EntityAttribute} to get the translation key from.
   * @param value           The translated name of the entity attribute.
   */
  @Contract(value = "_, _ -> this")
  default LanguageProvider add(@NotNull EntityAttribute entityAttribute, String value) {
    return add(entityAttribute.getTranslationKey(), value);
  }

  /**
   * Adds a translation for a {@link StatType}.
   *
   * @param statType The {@link StatType} to get the translation key from.
   * @param value    The translated name of the stat type.
   */
  @Contract(value = "_, _ -> this")
  default LanguageProvider add(@NotNull StatType<?> statType, String value) {
    return add("stat_type." + Registries.STAT_TYPE.getId(statType).toString().replace(':', '.'), value);
  }

  /**
   * Adds a translation for a {@link StatusEffect}.
   *
   * @param statusEffect The {@link StatusEffect} to get the translation key from.
   * @param value        The translated name of the status effect.
   */
  @Contract(value = "_, _ -> this")
  default LanguageProvider add(StatusEffect statusEffect, String value) {
    return add(statusEffect.getTranslationKey(), value);
  }

  /**
   * Adds a translation for an {@link Identifier}.
   *
   * @param identifier The {@link Identifier} to get the translation key from.
   * @param value      The translated name of the identifier.
   */
  @Contract(value = "_, _ -> this")
  default LanguageProvider add(@NotNull Identifier identifier, String value) {
    return add(identifier.toTranslationKey(), value);
  }

  /**
   * Add all key-value pairs from another value to this object.
   */
  @Contract(value = "_ -> this")
  LanguageProvider addAll(@NotNull Map<String, String> map);


  /**
   * Add all key-value pairs from another value to this object.
   */
  @Contract(value = "_ -> this")
  default LanguageProvider addAll(LanguageProvider another) {
    return addAll(another.content());
  }

  @ApiStatus.AvailableSince("1.1.0")
  default Impl<ImmutableMap<String, String>> toImmutable() {
    return new Impl<>(ImmutableMap.copyOf(content()));
  }

  /**
   * The simplest implementation of this object.
   *
   * @param content The content storing the key-value pairs, which is a map.
   * @param <T>     The type of the map.
   */
  @ApiStatus.Internal
  record Impl<T extends Map<String, String>>(T content) implements LanguageProvider {
    @Override
    public Impl<T> add(@NotNull String key, String value) {
      content.put(key, value);
      return this;
    }

    @Override
    public Impl<T> addAll(@NotNull Map<String, String> map) {
      content.putAll(map);
      return this;
    }
  }
}
