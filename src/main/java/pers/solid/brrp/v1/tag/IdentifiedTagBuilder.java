package pers.solid.brrp.v1.tag;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

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
    // TODO: 2023/4/10, 010 optimize
    blockTagBuilder.build(identifier1 -> {
      itemTagBuilder.addTag(identifier1);
      return new Tag<>(List.of());
    }, identifier1 -> {
      itemTagBuilder.addTag(identifier1);
      return Registry.ITEM.get(identifier1);
    });
    return itemTagBuilder;
  }
}
