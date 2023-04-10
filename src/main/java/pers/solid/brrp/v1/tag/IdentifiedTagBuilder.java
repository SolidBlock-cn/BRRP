package pers.solid.brrp.v1.tag;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.PlatformBridge;
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
  public final Registry<T> registry;
  /**
   * The identifier of the tag itself.
   */
  public final Identifier identifier;
  public final Tag.Identified<T> tag;
  /**
   * The path that the tag is stored in the tag file. It is similar to {@link net.minecraft.tag.TagGroupLoader#dataType} and {@link net.minecraft.data.server.AbstractTagProvider#getOutput(Identifier)}. The example values of the field: {@code blocks}, {@code items}, {@code fluids}.
   */
  public final String dataType;

  /**
   * Create a builder with a specified registry and identifier.
   *
   * @param registry The registry used to get the id of objects.
   * @param tag      The vanilla-type tag object.
   */
  public IdentifiedTagBuilder(Registry<T> registry, Tag.Identified<T> tag, @NotNull String dataType) {
    super(registry::getId);
    this.registry = registry;
    this.identifier = tag.getId();
    this.tag = tag;
    this.dataType = dataType;
  }

  /**
   * Create a block tag builder with a specified id.
   */
  public static IdentifiedTagBuilder<Block> createBlock(Identifier identifier) {
    return createBlock(PlatformBridge.getInstance().createBlockTag(identifier));
  }

  /**
   * Create a block tag builder from vanilla-type block tag object. The id of the vanilla-type tag will be used.
   */
  public static IdentifiedTagBuilder<Block> createBlock(Tag.Identified<Block> blockTag) {
    return new IdentifiedTagBuilder<>(Registry.BLOCK, blockTag, "blocks");
  }

  /**
   * Create an item tag builder with a specified id.
   */
  public static IdentifiedTagBuilder<Item> createItem(Identifier identifier) {
    return createItem(PlatformBridge.getInstance().createItemTag(identifier));
  }

  /**
   * Create an item tag builder from vanilla-type item tag object. The id of the vanilla-type tag will be used.
   */
  public static IdentifiedTagBuilder<Item> createItem(Tag.Identified<Item> itemTag) {
    return new IdentifiedTagBuilder<>(Registry.ITEM, itemTag, "items");
  }

  /**
   * Create an entity type tag builder with a specified id.
   */
  public static IdentifiedTagBuilder<EntityType<?>> createEntityType(Identifier identifier) {
    return createEntityType(PlatformBridge.getInstance().createEntityTypeTag(identifier));
  }


  /**
   * Create an entity type tag builder from vanilla-type entity type tag object. The id of the vanilla-type tag will be used.
   */
  public static IdentifiedTagBuilder<EntityType<?>> createEntityType(Tag.Identified<EntityType<?>> entityTypeTag) {
    return new IdentifiedTagBuilder<>(Registry.ENTITY_TYPE, entityTypeTag, "entity_types");
  }

  /**
   * Create a fluid tag builder with a specified id.
   */
  public static IdentifiedTagBuilder<Fluid> createFluid(Identifier identifier) {
    return createFluid(PlatformBridge.getInstance().createFluidTag(identifier));
  }


  /**
   * Create a fluid tag builder from vanilla-type fluid tag object. The id of the vanilla-type tag will be used.
   */
  public static IdentifiedTagBuilder<Fluid> createFluid(Tag.Identified<Fluid> fluidTag) {
    return new IdentifiedTagBuilder<>(Registry.FLUID, fluidTag, "fluids");
  }

  /**
   * Create an item tag builder, copying from another tag builder, which is usually for blocks, and have written some contents in it. The ids of its blocks and block tags should be the same for their items and item tags.
   *
   * @param itemTag  The vanilla-type item tag.
   * @param copyFrom Another tag builder (usually for blocks) that has added some blocks.
   * @return The new item tag builder that copied from the other tag builder.
   */
  public static IdentifiedTagBuilder<Item> createItemCopy(Tag.Identified<Item> itemTag, Tag.Builder copyFrom) {
    final IdentifiedTagBuilder<Item> identifiedTagBuilder = createItem(itemTag);
    ((TagBuilderAccessor) identifiedTagBuilder).getEntries().addAll(((TagBuilderAccessor) copyFrom).getEntries());
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
    ((TagBuilderAccessor) copyFrom).getEntries().forEach(identifiedTagBuilder::add);
    return identifiedTagBuilder;
  }
}
