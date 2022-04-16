package net.devtech.arrp.json.tags;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p><b>Tag</b>s are used to list block, entity types, functions, etc. The identifier of tag is <code><i>namespace</i>:<i>type</i>/<i>pathContent</i></code>, where the <code><i>type</i></code> determines which object the tag contents refer to, and can be one of the following values: {@code blocks entity_types fluids functions game_events items worldgen}.</p>
 */
public class JTag {
  /**
   * <p>Determines whether the tag content replaces existing contents (if so), instead of appending it.</p>
   * <p>For example, assume if the values of {@code brrp:functions/example_tag} is {@code [id1, id2, id3]}, and your custom data pack also has the tag named {@code brrp:functions/example_tag}, and the values of the tag is {@code [id4, id5]}.</p>
   * <p>If {@code replace=false}, the {@code [id4, id5]} will be appended to the existing tag, which will be {@code [id1, id2, id3, id4, id5]}.</p>
   * <p>If {@code replace=true}, it directly replaces {@code [id1, id2, id3]}, which becomes {@code [id4, id5]}.</p>
   */
  public Boolean replace;
  public List<String> values = new ArrayList<>();

  public JTag() {
  }

  /**
   * @deprecated Please directly use {@code new JTag().replace()}.
   */
  @Deprecated
  public static JTag replacingTag() {
    return tag().replace();
  }

  /**
   * whether or not this tag should override all super tags
   */
  @CanIgnoreReturnValue
  public JTag replace() {
    this.replace = true;
    return this;
  }

  @CanIgnoreReturnValue
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
  public JTag add(String identifier) {
    this.values.add(identifier);
    return this;
  }

  /**
   * Add the identifier of the entry to this tag.
   */
  @CanIgnoreReturnValue
  public JTag add(Identifier identifier) {
    this.values.add(identifier.toString());
    return this;
  }

  /**
   * Add identifiers of entries to this tag.
   */
  @CanIgnoreReturnValue
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
  public JTag addBlock(Block block) {
    add(Registry.BLOCK.getId(block));
    return this;
  }

  @CanIgnoreReturnValue
  public JTag addBlocks(Iterable<Block> blocks) {
    blocks.forEach(this::addBlock);
    return this;
  }

  @CanIgnoreReturnValue
  public JTag addBlocks(Block... blocks) {
    return addBlocks(Arrays.asList(blocks));
  }

  /**
   * Assume this tag is an item tag, query the item id and add to the tag. Please confirm that when calling this method, the item is correctly registered. If you haven't registered the item, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  public JTag addItem(Item item) {
    add(Registry.ITEM.getId(item));
    return this;
  }

  @CanIgnoreReturnValue
  public JTag addItems(Iterable<Item> items) {
    items.forEach(this::addItem);
    return this;
  }

  @CanIgnoreReturnValue
  public JTag addItems(Item... items) {
    return addItems(Arrays.asList(items));
  }

  /**
   * Assume this tag is a fluid tag, query the fluid id and add to the tag. Please confirm that when calling this method, the fluid is correctly registered. If you haven't registered the fluid, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  public JTag addFluid(Fluid fluid) {
    add(Registry.FLUID.getId(fluid));
    return this;
  }

  @CanIgnoreReturnValue
  public JTag addFluids(Iterable<Fluid> fluids) {
    fluids.forEach(this::addFluid);
    return this;
  }

  @CanIgnoreReturnValue
  public JTag addFluids(Fluid... fluids) {
    return this.addFluids(Arrays.asList(fluids));
  }

  /**
   * Assume this tag is an entity-type tag, query the entity type id and add to the tag. Please confirm that when calling this method, the entity type is correctly registered. If you haven't registered the entity type, you can register with {@link Registry#register} at first.
   */
  @CanIgnoreReturnValue
  public JTag addEntityType(EntityType<?> entityType) {
    add(Registry.ENTITY_TYPE.getId(entityType));
    return this;
  }

  @CanIgnoreReturnValue
  public JTag addEntityTypes(Iterable<EntityType<?>> entityTypes) {
    entityTypes.forEach(this::addEntityType);
    return this;
  }

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
  public JTag tag(Identifier tag) {
    this.values.add('#' + tag.getNamespace() + ':' + tag.getPath());
    return this;
  }

  /**
   * Add another tag to this tag. Please be warned that the "another tag" identifier does not specify type. For example, the block tag can {@code addTag(new Identifier("minecraft", "logs"))} instead of {@code addTag{new Identifier("minecraft", "blocks/logs")}}.
   *
   * @param tagIdentifier The identifier of the tag you added. It does not contain the type specification.
   * @return The JTag instance itself, makes it possible to chain-call.
   */
  @CanIgnoreReturnValue
  public JTag addTag(Identifier tagIdentifier) {
    this.values.add("#" + tagIdentifier.toString());
    return this;
  }

  @CanIgnoreReturnValue
  public JTag addTag(IdentifiedTag tag) {
    this.values.add("#" + tag.identifier.toString());
    return this;
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
