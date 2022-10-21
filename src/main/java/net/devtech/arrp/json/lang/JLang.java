package net.devtech.arrp.json.lang;

import com.google.common.base.Suppliers;
import net.devtech.arrp.util.CanIgnoreReturnValue;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>The <b>language file</b> determines how Minecraft will translate strings according to their language settings.</p>
 */
@SuppressWarnings("unused")
public class JLang extends HashMap<String, String> implements Cloneable {

  public JLang() {
  }

  /**
   * Simply "upgrades" a simple string map to this object.
   *
   * @param map The string map.
   */
  public JLang(Map<? extends String, ? extends String> map) {
    this();
    putAll(map);
  }

  /**
   * @see #JLang()  JLang
   * @deprecated Please directly use the constructor method {@link #JLang()}.
   */
  @Deprecated
  @Contract("-> new")
  public static JLang lang() {
    return new JLang();
  }

  /**
   * Adds a custom entry to the lang file. (deprecated: renamed to a more intuitive name)
   *
   * @param in  the translation string
   * @param out the in-game name of the object
   * @return the file with the new entry.
   * @deprecated use {@link #entry(String, String)} instead.
   */
  @CanIgnoreReturnValue
  @Deprecated
  public JLang translate(String in, String out) {
    put(in, out);
    return this;
  }

  /**
   * @deprecated Ambiguous name and parameter name. Please use {@link #registryEntry(Registry, String, Object, String)}.
   */
  @CanIgnoreReturnValue
  @Deprecated
  private <T> JLang object(Registry<T> registry, String str, T t, String name) {
    return this.object(str,
        Objects.requireNonNull(registry.getId(t), "register your item before calling this"),
        name);
  }

  /**
   * Add a registry entry to this instance.
   *
   * @deprecated Ambiguous name and it does not use {@link Util#createTranslationKey}. Please use {@link #registryEntry(String, Identifier, String)}.
   */
  @Contract(value = "_, _, _ -> this", mutates = "this")
  @CanIgnoreReturnValue
  @Deprecated
  private JLang object(String type, Identifier identifier, String translation) {
    put(type + '.' + identifier.getNamespace() + '.' + identifier.getPath(), translation);
    return this;
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
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_,_,_ -> this", mutates = "this")
  public <T> JLang registryEntry(Registry<T> registry, String type, T t, String translation) {
    final RegistryKey<T> registryKey = registry.getKey(t).orElseThrow(Suppliers.ofInstance(new RuntimeException("Please register it first!")));
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
   * Adds a translation key for an item, using simple {@link Registry#getId(Object)}. Please ensure that the item has been registered.
   *
   * @see #itemRespect(Item, String)
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  @Deprecated
  public JLang item(Item item, String translation) {
    return this.registryEntry(Registry.ITEM, "item", item, translation);
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
   * Adds a translation key for a block, using simple {@link Registry#getId(Object)}. Please ensure that the block has been registered.
   *
   * @see #blockRespect(Block, String)
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  @Deprecated
  public JLang block(Block block, String translation) {
    return this.registryEntry(Registry.BLOCK, "block", block, translation);
  }

  /**
   * Adds a translation key for a fluid, using simple {@link Registry#getId(Object)}. Please ensure that the fluid has been registered.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang fluid(Fluid fluid, String translation) {
    return this.registryEntry(Registry.FLUID, "fluid", fluid, translation);
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
   * @see JLang#entityRespect(EntityType, String)
   */
  @Deprecated
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang entity(EntityType<?> type, String translation) {
    return this.object(Registry.ENTITY_TYPE, "entity_type", type, translation);
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
   * Adds a translation key for an enchantment, simply using {@link Registry#getId(Object)}.
   *
   * @see #enchantmentRespect(Enchantment, String)
   */
  @Deprecated
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang enchantment(Enchantment enchantment, String translation) {
    return this.object(Registry.ENCHANTMENT, "enchantment", enchantment, translation);
  }

  /**
   * Add an item entry with the identifier specified.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  @Deprecated
  public JLang item(Identifier item, String translation) {
    return this.registryEntry("item", item, translation);
  }

  /**
   * Add a block entry with the identifier specified.
   *
   * @see #blockRespect(Block, String)
   */
  @CanIgnoreReturnValue
  @Contract(value = "_,_ -> this", mutates = "this")
  @Deprecated
  public JLang block(Identifier block, String translation) {
    return this.registryEntry("block", block, translation);
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
   * @param id          The identifier of the item group specified in {@link net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder}.
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
   * Like {@link JLang#allPotion}, but it adds in the prefixes automatically. This only applies for English language.
   *
   * @deprecated Ambiguous translation keys and English-only potion names.
   */
  @CanIgnoreReturnValue
  @Deprecated
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang allPotionOf(Identifier id, String effectTranslation) {
    this.allPotion(id,
        "Potion of " + effectTranslation,
        "Splash Potion of " + effectTranslation,
        "Lingering Potion of " + effectTranslation,
        "Tipped Arrow of " + effectTranslation);
    return this;
  }

  /**
   * Add translation entries for drinkable potion, splash potion, lingering potion and tipped arrow.
   *
   * @deprecated Ambiguous translation keys.
   */
  @CanIgnoreReturnValue
  @Deprecated
  @Contract(value = "_,_,_,_,_ -> this", mutates = "this")
  public JLang allPotion(Identifier id,
                         String drinkablePotionName,
                         String splashPotionName,
                         String lingeringPotionName,
                         String tippedArrowName) {
    return this.drinkablePotion(id, drinkablePotionName).splashPotion(id, splashPotionName)
        .lingeringPotion(id, lingeringPotionName).tippedArrow(id, tippedArrowName);
  }

  /**
   * @deprecated Ambiguous translation key.
   */
  @CanIgnoreReturnValue
  @Deprecated
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang tippedArrow(Identifier id, String translation) {
    put("item.minecraft.tipped_arrow.effect." + id.getPath(), translation);
    return this;
  }

  /**
   * @deprecated Ambiguous translation key.
   */
  @CanIgnoreReturnValue
  @Deprecated
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang lingeringPotion(Identifier id, String name) {
    put("item.minecraft.lingering_potion.effect." + id.getPath(), name);
    return this;
  }

  /**
   * @deprecated Ambiguous translation key.
   */
  @CanIgnoreReturnValue
  @Deprecated
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang splashPotion(Identifier id, String name) {
    put("item.minecraft.splash_potion.effect." + id.getPath(), name);
    return this;
  }

  /**
   * @deprecated Ambiguous translation key and English-only potion names.
   */
  @CanIgnoreReturnValue
  @Deprecated
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang drinkablePotion(Identifier id, String name) {
    put("item.minecraft.potion.effect." + id.getPath(), "Potion of " + name);
    return this;
  }

  /**
   * @deprecated Ambiguous translation key and English-only potion names.
   * Like {@link JLang#drinkablePotion}, but it adds in the "Potion of" automatically.
   */
  @CanIgnoreReturnValue
  @Deprecated
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang drinkablePotionOf(Identifier id, String effectName) {
    put("item.minecraft.potion.effect." + id.getPath(), "Potion of " + effectName);
    return this;
  }

  /**
   * @deprecated Ambiguous translation key and English-only potion names.
   * <p>
   * Like {@link JLang#splashPotion}, but it adds in the "Splash Potion of" automatically.
   */
  @CanIgnoreReturnValue
  @Deprecated
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang splashPotionOf(Identifier id, String effectName) {
    put("item.minecraft.splash_potion.effect." + id.getPath(), "Splash Potion of " + effectName);
    return this;
  }

  /**
   * @deprecated Ambiguous translation key and English-only potion names.
   * <p>
   * Like {@link JLang#lingeringPotion}, but it adds in the "Lingering Potion of" automatically.
   */
  @CanIgnoreReturnValue
  @Deprecated
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang lingeringPotionOf(Identifier id, String effectName) {
    put("item.minecraft.lingering_potion.effect." + id.getPath(), "Lingering Potion of " + effectName);
    return this;
  }

  /**
   * @deprecated Ambiguous translation key and English-only potion names.
   * <p>
   * Like {@link JLang#tippedArrow}, but it adds in the "Tipped Arrow of" automatically.
   */
  @CanIgnoreReturnValue
  @Deprecated
  @Contract(value = "_,_ -> this", mutates = "this")
  public JLang tippedArrowOf(Identifier id, String effectName) {
    put("item.minecraft.tipped_arrow.effect." + id.getPath(), "Tipped Arrow of " + effectName);
    return this;
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

  /**
   * @deprecated Useless method. Kept for only compatibility.
   */
  @Deprecated
  public Map<String, String> getLang() {
    return this;
  }

  @Override
  public JLang clone() {
    return (JLang) super.clone();
  }
}
