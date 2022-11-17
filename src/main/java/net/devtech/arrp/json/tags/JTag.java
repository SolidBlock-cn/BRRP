package net.devtech.arrp.json.tags;


import net.devtech.arrp.util.CanIgnoreReturnValue;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p><b>Tag</b>s are used to list block, entity types, functions, etc. The identifier of tag is <code style=color:maroon><i>namespace</i>:<i>type</i>/<i>pathContent</i></code>, where the <code><i>type</i></code> determines which object the tag contents refer to.</p>
 * <p>A tag itself has an identifier. It is determined when writing the tag into the resource pack. If you needs to predetermine its identifier, please use {@link IdentifiedTag}.</p>
 */
@SuppressWarnings("unused")
public class JTag {
  /**
   * <p>Determines whether the tag content replaces existing contents (if so), instead of appending it.</p>
   * <p>For example, assume if the values of {@code brrp:functions/example_tag} is {@code [id1, id2, id3]}, and your custom data pack also has the tag named {@code brrp:functions/example_tag}, and the values of the tag is {@code [id4, id5]}.</p>
   * <p>If {@code replace=false}, the {@code [id4, id5]} will be appended to the existing tag, which will be {@code [id1, id2, id3, id4, id5]}.</p>
   * <p>If {@code replace=true}, it directly replaces {@code [id1, id2, id3]}, which becomes {@code [id4, id5]}.</p>
   */
  public Boolean replace;
  /**
   * Values of this tag. They are stored in the form of string. They include identifiers and tag identifiers (prefixed by {@code "#"}).
   */
  public List<String> values = new ArrayList<>();

  /**
   * Create an empty tag object.
   */
  public JTag() {
  }

  /**
   * Create a new {@link IdentifiedTag} object with the specified type and identifier, and the same replace and values.
   *
   * @param type       The type of the tag.
   * @param identifier The identifier without type specification.
   * @return A new {@link IdentifiedTag} object.
   */
  @Contract("_, _ -> new")
  public IdentifiedTag identified(String type, Identifier identifier) {
    final IdentifiedTag identifiedTag = new IdentifiedTag(type, identifier);
    identifiedTag.replace = replace;
    identifiedTag.values = values;
    return identifiedTag;
  }

  /**
   * Create a new {@link IdentifiedTag} object with the specified type, and the namespace and path of the identifier, and the same replace and values.
   *
   * @param namespace The namespace of identifier.
   * @param type      The type of the tag.
   * @param path      The path without type specification.
   * @return A new {@link IdentifiedTag} object.
   */
  @Contract("_, _, _ -> new")
  public IdentifiedTag identified(String namespace, String type, String path) {
    final IdentifiedTag identifiedTag = new IdentifiedTag(namespace, type, path);
    identifiedTag.replace = replace;
    identifiedTag.values = values;
    return identifiedTag;
  }

  /**
   * @deprecated Please directly use {@code new JTag().replace()}.
   */
  @Deprecated
  public static JTag replacingTag() {
    return tag().replace();
  }

  /**
   * Set the {@link #replace} to {@code true}.
   *
   * @see #replace(boolean)
   */
  @CanIgnoreReturnValue
  @Contract(value = "-> this", mutates = "this")
  public JTag replace() {
    this.replace = true;
    return this;
  }

  /**
   * Set the {@link #replace} of the tag. You can also directly specify it when constructing.
   *
   * @param replace Whether the tag is replacing.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag replace(boolean replace) {
    this.replace = replace;
    return this;
  }

  /**
   * @deprecated Please directly use the constructor method {@link #JTag()}.
   */
  @Deprecated
  public static JTag tag() {
    return new JTag();
  }

  /**
   * @implNote Usually you should add the identifier by calling {@link #add(Identifier)} or {@link #tag(Identifier)}.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag add(String identifier) {
    this.values.add(identifier);
    return this;
  }

  /**
   * Add the identifier of the entry to this tag.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag add(Identifier identifier) {
    this.values.add(identifier.toString());
    return this;
  }

  /**
   * Add identifiers of entries to this tag.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag add(Identifier... identifiers) {
    for (Identifier identifier : identifiers) {
      add(identifier);
    }
    return this;
  }

  /**
   * Assume this tag is a block tag, query the block id and add to the tag. Please confirm that when calling this method, the block is correctly registered. If you haven't registered the block, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addBlock(Block block) {
    add(Registries.BLOCK.getId(block));
    return this;
  }

  /**
   * Assume this tag is a block tag, query the identifiers of the blocks and add them to the tag. Please confirm that when calling this method, the blocks are correctly registered. If you haven't registered the blocks, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addBlocks(Iterable<Block> blocks) {
    blocks.forEach(this::addBlock);
    return this;
  }

  /**
   * Assume this tag is a block tag, query the identifiers of the blocks and add them to the tag. Please confirm that when calling this method, the blocks are correctly registered. If you haven't registered the blocks, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addBlocks(Block... blocks) {
    return addBlocks(Arrays.asList(blocks));
  }

  /**
   * Assume this tag is an item tag, query the item id and add to the tag. Please confirm that when calling this method, the item is correctly registered. If you haven't registered the item, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addItem(ItemConvertible item) {
    add(Registries.ITEM.getId(item.asItem()));
    return this;
  }

  /**
   * Assume this tag is an item tag, query the identifiers of items and add them the tag. Please confirm that when calling this method, the items are correctly registered. If you haven't registered the items, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addItems(Iterable<ItemConvertible> items) {
    items.forEach(this::addItem);
    return this;
  }

  /**
   * Assume this tag is an item tag, query the identifiers of items and add them the tag. Please confirm that when calling this method, the items are correctly registered. If you haven't registered the items, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addItems(ItemConvertible... items) {
    return addItems(Arrays.asList(items));
  }

  /**
   * Assume this tag is a fluid tag, query the fluid id and add to the tag. Please confirm that when calling this method, the fluid is correctly registered. If you haven't registered the fluid, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addFluid(Fluid fluid) {
    add(Registries.FLUID.getId(fluid));
    return this;
  }

  /**
   * Assume this tag is a fluid tag, query the identifiers of the fluids and add them to the tag. Please confirm that when calling this method, the fluids are correctly registered. If you haven't registered the fluids, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addFluids(Iterable<Fluid> fluids) {
    fluids.forEach(this::addFluid);
    return this;
  }

  /**
   * Assume this tag is a fluid tag, query the identifiers of the fluids and add them to the tag. Please confirm that when calling this method, the fluids are correctly registered. If you haven't registered the fluids, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addFluids(Fluid... fluids) {
    return this.addFluids(Arrays.asList(fluids));
  }

  /**
   * Assume this tag is an entity-type tag, query the entity type id and add to the tag. Please confirm that when calling this method, the entity type is correctly registered. If you haven't registered the entity type, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addEntityType(EntityType<?> entityType) {
    add(Registries.ENTITY_TYPE.getId(entityType));
    return this;
  }

  /**
   * Assume this tag is an entity-type tag, query the identifiers of the entity types and add to the tag. Please confirm that when calling this method, the entity types are correctly registered. If you haven't registered the entity types, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addEntityTypes(Iterable<EntityType<?>> entityTypes) {
    entityTypes.forEach(this::addEntityType);
    return this;
  }

  /**
   * Assume this tag is an entity-type tag, query the identifiers of the entity types and add to the tag. Please confirm that when calling this method, the entity types are correctly registered. If you haven't registered the entity types, you can register with {@link Registry#register} at first.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addEntityTypes(EntityType<?>... entityTypes) {
    return this.addEntityTypes(Arrays.asList(entityTypes));
  }

  /**
   * add a tag to the tag
   *
   * @deprecated Ambiguous name. Please use {@link #addTag(Identifier)}.
   */
  @Deprecated
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag tag(Identifier tag) {
    this.values.add('#' + tag.getNamespace() + ':' + tag.getPath());
    return this;
  }

  /**
   * Add another tag to this tag. Please be warned that the "another tag" identifier does not specify type. For example, the block tag can {@code addTag(new Identifier("minecraft", "logs"))} instead of {@code addTag{new Identifier("minecraft", "blocks/logs")}}.
   *
   * @param tagIdentifier The identifier of the tag you added. It does not contain the type specification.
   * @return The JTag instance itself, making it possible to chain-call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addTag(Identifier tagIdentifier) {
    this.values.add("#" + tagIdentifier.toString());
    return this;
  }

  /**
   * Add other tags to this tag. Please be warned that the "other tag" identifiers do not specify type.
   *
   * @param tagIdentifiers The identifiers of the tag you added. They do not contain the type specification.
   * @return The JTag instance itself, making it possible to chain-call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addTagIds(Iterable<Identifier> tagIdentifiers) {
    tagIdentifiers.forEach(this::addTag);
    return this;
  }

  /**
   * Add other tags to this tag. Please be warned that the "other tag" identifiers do not specify type.
   *
   * @param tagIdentifiers The identifiers of the tag you added. They do not contain the type specification.
   * @return The JTag instance itself, making it possible to chain-call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addTagIds(Identifier... tagIdentifiers) {
    return addTagIds(Arrays.asList(tagIdentifiers));
  }

  /**
   * Add another tag to this tag. In this method, the tag parameter is the tag used for BRRP, and you assume that the type of that tag matches the type of this. The object has already stored an identifier, so its identifier can be directly used.
   *
   * @param tag The tag you added. Its {@linkplain IdentifiedTag#identifier identifier without type specification} will be used.
   * @return The JTag instance itself, making it possible to chain-call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addTag(IdentifiedTag tag) {
    this.values.add("#" + tag.identifier.toString());
    return this;
  }

  /**
   * Add other tags to this tag. In this method, the "tags" parameter is the tags used for BRRP, and you assume that the type of these tags matches the type of this. Each of the objects has stored an identifier, so those identifiers can be directly used.
   *
   * @param tags The tags you added. Its {@linkplain IdentifiedTag#identifier identifier without type specification} will be used.
   * @return The JTag instance itself, making it possible to chain-call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addTags(Iterable<IdentifiedTag> tags) {
    tags.forEach(this::addTag);
    return this;
  }

  /**
   * Add other tags to this tag. In this method, the "tags" parameter is the tags used for BRRP, and you assume that the type of these tags matches the type of this. Each of the objects has stored an identifier, so those identifiers can be directly used.
   *
   * @param tags The tags you added. Its {@linkplain IdentifiedTag#identifier identifier without type specification} will be used.
   * @return The JTag instance itself, making it possible to chain-call.
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JTag addTags(IdentifiedTag... tags) {
    return addTags(Arrays.asList(tags));
  }

  @Override
  public JTag clone() {
    try {
      return (JTag) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }
}
