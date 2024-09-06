package pers.solid.brrp.v1.tag;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import pers.solid.brrp.v1.mixin.TagBuilderAccessor;

/**
 * The object contains the id itself, which can be used by some methods. It is convenient because you do not need to specify id when using adding them to runtime resource packs, or referring to them in other tags.
 *
 * @param <T> The type of the object.
 */
public class IdentifiedTagBuilder<T> extends ObjectTagBuilder<T, IdentifiedTagBuilder<T>> {
  /**
   * The registry it belongs to, which will be used to get the id of objects.
   */
  public final @Deprecated(forRemoval = true) Registry<T> registry;
  public final RegistryKey<? extends Registry<T>> registryKey;
  /**
   * The identifier of the tag itself.
   */
  public final Identifier identifier;

  /**
   * Create a builder with a specified registry and identifier.
   *
   * @param registry   The registry used to get the id of objects.
   * @param identifier The identifier of the tag.
   */
  public IdentifiedTagBuilder(Registry<T> registry, Identifier identifier) {
    super(registry::getId);
    this.registry = registry;
    this.registryKey = registry.getKey();
    this.identifier = identifier;
  }

  public IdentifiedTagBuilder(RegistryKey<Registry<T>> registryKey, Identifier identifier) {
    super(t -> {
      throw new UnsupportedOperationException("Can't add objects for IdentifierTagBuilder without builtin registry");
    });
    this.registry = null;
    this.registryKey = registryKey;
    this.identifier = identifier;
  }

  /**
   * Create a block tag builder with a specified id.
   */
  public static IdentifiedTagBuilder<Block> createBlock(Identifier identifier) {
    return new IdentifiedTagBuilder<>(Registries.BLOCK, identifier);
  }

  /**
   * Create a block tag builder from vanilla-type block tag object. The id of the vanilla-type tag will be used.
   */
  public static IdentifiedTagBuilder<Block> createBlock(TagKey<Block> blockTagKey) {
    return createBlock(blockTagKey.id());
  }

  /**
   * Create an item tag builder with a specified id.
   */
  public static IdentifiedTagBuilder<Item> createItem(Identifier identifier) {
    return new IdentifiedTagBuilder<>(Registries.ITEM, identifier);
  }

  /**
   * Create an item tag builder from vanilla-type item tag object. The id of the vanilla-type tag will be used.
   */
  public static IdentifiedTagBuilder<Item> createItem(TagKey<Item> itemTagKey) {
    return createItem(itemTagKey.id());
  }

  /**
   * Create an entity type tag builder with a specified id.
   */
  public static IdentifiedTagBuilder<EntityType<?>> createEntityType(Identifier identifier) {
    return new IdentifiedTagBuilder<>(Registries.ENTITY_TYPE, identifier);
  }


  /**
   * Create an entity type tag builder from vanilla-type entity type tag object. The id of the vanilla-type tag will be used.
   */
  public static IdentifiedTagBuilder<EntityType<?>> createEntityType(TagKey<EntityType<?>> entityTypeTagKey) {
    return createEntityType(entityTypeTagKey.id());
  }

  /**
   * Create a fluid tag builder with a specified id.
   */
  public static IdentifiedTagBuilder<Fluid> createFluid(Identifier identifier) {
    return new IdentifiedTagBuilder<>(Registries.FLUID, identifier);
  }


  /**
   * Create a fluid tag builder from vanilla-type fluid tag object. The id of the vanilla-type tag will be used.
   */
  public static IdentifiedTagBuilder<Fluid> createFluid(TagKey<Fluid> fluidTagKey) {
    return createFluid(fluidTagKey.id());
  }

  /**
   * Create an item tag builder, copying from another tag builder, which is usually for blocks, and have written some contents in it. The ids of its blocks and block tags should be the same for their items and item tags.
   *
   * @param itemTagKey The vanilla-type item tag.
   * @param copyFrom   Another tag builder (usually for blocks) that has added some blocks.
   * @return The new item tag builder that copied from the other tag builder.
   */
  public static IdentifiedTagBuilder<Item> createItemCopy(TagKey<Item> itemTagKey, TagBuilder copyFrom) {
    final IdentifiedTagBuilder<Item> identifiedTagBuilder = createItem(itemTagKey);
    ((TagBuilderAccessor) copyFrom).getEntries().forEach(identifiedTagBuilder::add);
    return identifiedTagBuilder;
  }

  /**
   * Create an item tag builder, copying from another block tag builder, with the identifier identical to it.
   *
   * @param copyFrom The block tag builder.
   * @return The new item tag builder, which the identifier identical to the block tag identifier, and copied from the block tag builder.
   */
  public static IdentifiedTagBuilder<Item> createItemCopy(IdentifiedTagBuilder<?> copyFrom) {
    final IdentifiedTagBuilder<Item> identifiedTagBuilder = createItem(copyFrom.identifier);
    ((TagBuilderAccessor) identifiedTagBuilder).getEntries().addAll(((TagBuilderAccessor) copyFrom).getEntries());
    return identifiedTagBuilder;
  }
}
