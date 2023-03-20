package pers.solid.brrp.v1.tag;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class IdentifiedTagBuilder<T> extends ObjectTagBuilder<T, IdentifiedTagBuilder<T>> {
  public final Registry<T> registry;
  public final Identifier identifier;

  public IdentifiedTagBuilder(Registry<T> registry, Identifier identifier) {
    super(registry::getId);
    this.registry = registry;
    this.identifier = identifier;
  }

  public static IdentifiedTagBuilder<Block> createBlock(TagKey<Block> blockTagKey) {
    return new IdentifiedTagBuilder<>(Registry.BLOCK, blockTagKey.id());
  }

  public static IdentifiedTagBuilder<Item> createItem(TagKey<Item> itemTagKey) {
    return new IdentifiedTagBuilder<>(Registry.ITEM, itemTagKey.id());
  }

  public static IdentifiedTagBuilder<Item> copy(IdentifiedTagBuilder<Block> blockTagBuilder) {
    final IdentifiedTagBuilder<Item> itemTagBuilder = new IdentifiedTagBuilder<>(Registry.ITEM, blockTagBuilder.identifier);
    blockTagBuilder.build().forEach(itemTagBuilder::add);
    return itemTagBuilder;
  }
}
