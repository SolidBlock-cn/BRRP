package net.devtech.arrp.json.lang;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>The <b>language file</b> determines how Minecraft will translate strings according to their language settings. It is essentially a hash map.</p>
 */
@SuppressWarnings("unused")
public class JLang extends HashMap<String, String> implements Cloneable {

  public JLang() {
  }

  /**
   * Simply "upgrades" a simple string map to this object. The content of the {@code map} parameter will be just copied.
   *
   * @param map The string map.
   */
  public JLang(Map<? extends String, ? extends String> map) {
    this();
    putAll(map);
  }

  /**
   * Add a registry entry to this instance, using {@link Util#createTranslationKey(String, Identifier)}.
   *
   * @param type        The name specification of the registry type. For example, {@code "block"}.
   * @param identifier  The identifier of the object. For example, {@code new Identifier("minecraft", "stone")}.
   * @param translation The translated name, such as {@code "Stone"}.
   * @author SolidBlock
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_,_ -> this", mutates = "this")
  public JLang registryEntry(String type, Identifier identifier, String translation) {
    put(Util.createTranslationKey(type, identifier), translation);
    return this;
  }

  /**
   * Add a registry entry to this instance, using {@link Registry#getId(Object)}.
   *
   * @since 0.8.2 Fixed the issue that the exception object will be created even if it will not be thrown.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_,_,_ -> this", mutates = "this")
  public <T> JLang registryEntry(Registry<T> registry, String type, T t, String translation) {
    final RegistryKey<T> registryKey = registry.getKey(t).orElseThrow(() -> new RuntimeException("Please register it first!"));
    return registryEntry(type, registryKey.getValue(), translation);
  }


  /**
   * Add a language entry to this language file.
   *
   * @param entry       The key. For most registrable contents, it is <code><i>type</i>.<i>namespace</i>.<i>path</i></code>, for example, <code>block.minecraft.stone</code>. Customized keys are also OK.
   * @param translation The translated words.
   * @return The instance itself, making it possible to chain-call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang entry(String entry, String translation) {
    put(entry, translation);
    return this;
  }

  /**
   * Adds a translation key for an item, respects {@link Item#getTranslationKey()}. Please ensure that the item has been registered.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang itemRespect(Item item, String translation) {
    put(item.getTranslationKey(), translation);
    return this;
  }

  /**
   * Adds a translation key for an item stack (usually identical to that item), respected {@link ItemStack#getTranslationKey()}. Typically, you should ensure that the item has been registered.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang item(ItemStack stack, String translation) {
    put(stack.getTranslationKey(), translation);
    return this;
  }

  /**
   * Adds a translation key for a block, respects {@link Block#getTranslationKey()}. Please ensure that the block has been registered.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang blockRespect(Block block, String translation) {
    put(block.getTranslationKey(), translation);
    return this;
  }

  /**
   * Adds a translation key for a fluid, using simple {@link Registry#getId(Object)}. Please ensure that the fluid has been registered.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang fluid(Fluid fluid, String translation) {
    return this.registryEntry(Registries.FLUID, "fluid", fluid, translation);
  }

  /**
   * Adds a translation key for an entity type, respects {@link EntityType#getTranslationKey()}. Please ensure that the entity has been registered.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang entityRespect(EntityType<?> type, String translation) {
    put(type.getTranslationKey(), translation);
    return this;
  }

  /**
   * Adds a translation key for an enchantment, respects {@link Enchantment#getTranslationKey()}.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang enchantmentRespect(Enchantment enchantment, String translation) {
    put(enchantment.getTranslationKey(), translation);
    return this;
  }

  /**
   * Add a fluid entry with the identifier specified.
   *
   * @see #fluid(Fluid, String)
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang fluid(Identifier id, String translation) {
    return this.registryEntry("fluid", id, translation);
  }

  /**
   * Add an entity-type entry with the identifier specified.
   *
   * @see #entityRespect(EntityType, String)
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang entity(Identifier id, String translation) {
    return this.registryEntry("entity_type", id, translation);
  }

  /**
   * Add an enchantment entry with the identifier specified.
   *
   * @see #enchantmentRespect(Enchantment, String)
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang enchantment(Identifier id, String translation) {
    return this.registryEntry("enchantment", id, translation);
  }

  /**
   * Add an item-group entry with the identifier specified.
   *
   * @param id          The identifier of the item group specified in {@link net.minecraft.item.ItemGroup}.
   * @param translation The translated name of the item group.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang itemGroup(Identifier id, String translation) {
    return this.registryEntry("itemGroup", id, translation);
  }

  /**
   * Add a sound event with the identifier specified.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang sound(Identifier id, String translation) {
    return this.registryEntry("sound_event", id, translation);
  }

  /**
   * Add a mob effect with the identifier specified.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang status(Identifier id, String translation) {
    return this.registryEntry("mob_effect", id, translation);
  }

  /**
   * Add a biome entry to this instance with the identifier specified.
   *
   * @param id          The identifier of the biome.
   * @param translation The translated name of the biome.
   */
  @CanIgnoreReturnValue
  public JLang biome(Identifier id, String translation) {
    return this.registryEntry("biome", id, translation);
  }

  @Override
  public JLang clone() {
    return (JLang) super.clone();
  }
}
