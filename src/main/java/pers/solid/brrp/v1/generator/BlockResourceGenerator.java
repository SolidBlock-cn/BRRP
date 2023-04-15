package pers.solid.brrp.v1.generator;

import net.fabricmc.api.EnvType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.*;
import pers.solid.brrp.v1.BRRPUtils;
import pers.solid.brrp.v1.annotations.PreferredEnvironment;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;

/**
 * <p>The interface is used for blocks. Your custom block class can implement this interface, and override some methods you need.</p>
 * <p>This interface simply <em>extends</em> {@link ItemResourceGenerator}, as the resources of the block item related to it will be also generated. It's also possible that the block does not have a block item; in this case the recipe and item model should be ignored. <strong>Please notice that the "{@code get}" methods are used for data generation. instead of being directly used in the gameplay.</strong></p>
 * <p>You can override the methods so that it can generate data in your specified ways. Here is an example:</p>
 * <pre>{@code
 * public class MyBlock extends Block implements BlockResourceGenerator {
 *   [...]
 *   @Environment(EnvType.CLIENT)
 *   public ModelBuilder getBlockModel() {
 *     return [...]
 *   }
 *
 *   public LootTable.Builder getLootTable() {
 *     [...]
 *   }
 *   [...]
 * }
 * }</pre>
 * <p>Also, your custom class (if not extending {@code Block}) can implement this interface. In this case, you <em>have to</em> override {@link #getBlockId()} method, as it cannot be casted to {@code Block}.</p>
 * <p>It's highly recommended but not required to annotate methods related to client (block states, block models, item models) with {@code @}{@link net.fabricmc.api.Environment Environment}<code>({@link net.fabricmc.api.EnvType#CLIENT EnvType.CLIENT})</code> or {@code @OnlyIn(Dist.CLIENT)}, as they are only used in the client environment. The interface itself does not annotate it, in consideration of rare situations where the server really needs. But mostly these client-related methods are not needed in the server side.</p>
 * <p>After implementing this interface and appropriately overriding some methods, you can do the following to quickly add resources to a runtime resource pack:</p>
 * <pre>{@code
 * // 'block' is an instance of BlockResourceGenerator
 * // 'pack' is an instance of RuntimeResourcePack
 * block.writeAssets(pack);
 * block.writeData(pack);}</pre>
 * <p>Note that some methods (such as {@link #getLootTable()}) will have values by default. If you do not want to generate some resources within that method, you can override it and make it return {@code null}.</pre>
 * }
 */
public interface BlockResourceGenerator extends ItemResourceGenerator {
  /**
   * The base block of this block. You <em>should override</em> this method for block-based blocks, like stairs, slab, etc.
   *
   * @return The base block of this block.
   */
  @Contract(pure = true)
  default @Nullable Block getBaseBlock() {
    return null;
  }

  /**
   * Query the id of the block in {@link Registry#BLOCK}.<br>
   * You <em>have to</em> override this method if you're implementing this interface on a non-{@code Block} class, or you intend to generate resources before registration.
   *
   * @return The id of the block.
   */
  @Contract(pure = true)
  default Identifier getBlockId() {
    return Registry.BLOCK.getId((Block) this);
  }

  /**
   * Query the id of the corresponding block item. You can override when needed, but most time there is no need.
   * <p>
   * Usually the block id is the same as the item id, but we do not assume that here.
   *
   * @return The id of the block item, or {@code null} if the block has no item.
   * @since 0.8.0 It respects {@link Block#asItem()} now.
   */
  @Override
  @Contract(pure = true)
  default Identifier getItemId() {
    if (this instanceof Block block) {
      final Item item = block.asItem();
      if (item == Items.AIR) {
        if (BlockItem.BLOCK_ITEMS.containsKey(block)) {
          // 考虑到方块自身是空气的情况，虽然这种情况不太可能。
          return Registry.ITEM.getId(BlockItem.BLOCK_ITEMS.get(block));
        } else {
          // 方块没有物品。
          return null;
        }
      }
      return Registry.ITEM.getId(item);
    } else {
      return null;
    }
  }


  // CLIENT PART


  /**
   * The id of the block model. It is usually <code><var>namespace</var>:block/<var>path</var></code>. For example, the model id of stone is {@code minecraft:block/stone}.
   *
   * @return The id of the block model.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  default Identifier getBlockModelId() {
    return getBlockId().brrp_prefixed("block/");
  }

  /**
   * <p>The texture used in models. It's usually in the format of <code><var>namespace</var>:block/<var>path</var></code>, which <em>mostly</em> equals to the block id. However, sometimes they differ. For example, the texture of <code>minecraft:smooth_sandstone</code> is not <code>minecraft:block/smooth_sandstone</code>; it's <code>minecraft:block/sandstone_top</code>.</p>
   * <p><strong>The method is used for data generation, and just respects the block's id and {@link TextureRegistry}. It does not consider what texture is actually used in the game.</strong> Of course, you can override this method to apply different behaviours.</p>
   * <p>Some blocks have different textures in different parts. In this case, the parameter {@code type} is used. For example, a quartz pillar can have the following methods:</p>
   * <pre>{@code
   *   @Environment(EnvType.CLIENT) @Override
   *   public getTextureId(TextureKey type) {
   *     if (type == TextureKey.END) {
   *       return new Identifier("minecraft", "block/quartz_pillar_top");
   *     } else {
   *       return new Identifier("minecraft", "block/quartz_pillar");
   *     }
   *   }
   * }</pre>
   * <p>Besides, you can also use {@link TextureRegistry}, which supports fallbacks of texture keys.</p>
   *
   * @param textureKey The type used to distinguish texture.
   * @return The id of the texture.
   * @see TextureRegistry
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default @NotNull Identifier getTextureId(@NotNull TextureKey textureKey) {
    if (this instanceof Block thisBlock) {
      final Identifier texture = TextureRegistry.getTexture(thisBlock, textureKey);
      if (texture != null) return texture;
      final @Nullable Block baseBlock = getBaseBlock();
      if (baseBlock != null) {
        return BRRPUtils.getTextureId(baseBlock, textureKey);
      }
    }
    return getBlockId().brrp_prefixed("block/");
  }

  /**
   * The block states definition of the block. In the object, the returned value of {@link #getBlockModelId()} is often used.
   *
   * @return The block states definition of the block.
   * @see net.minecraft.data.client.BlockStateModelGenerator
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default BlockStateSupplier getBlockStates() {
    return null;
  }

  /**
   * Write the block states definition (returned in {@link #getBlockStates}) to the runtime resource pack, if that is not {@code null}. Usually a block has one block states definition, with the id identical to the block id.
   *
   * @param pack The runtime resource pack.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(mutates = "param1")
  default void writeBlockStates(RuntimeResourcePack pack) {
    final BlockStateSupplier blockStates = getBlockStates();
    if (blockStates != null) pack.addBlockState(getBlockId(), blockStates);
  }

  /**
   * The model of the block. If a block has multiple models, you may override this method for the most basic model, and override {@link #writeBlockModel} to generate and write other models.
   *
   * @return The block model.
   * @see net.minecraft.data.client.BlockStateModelGenerator
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default ModelJsonBuilder getBlockModel() {
    return null;
  }

  /**
   * Write the block model (returned in {@link #getBlockModel}) to the runtime resource pack. It does nothing if the returned model is {@code null}. If the block has multiple models, you may override this method to write more models.
   *
   * @param pack The runtime resource pack.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(mutates = "param1")
  default void writeBlockModel(RuntimeResourcePack pack) {
    final @Nullable ModelJsonBuilder model = getBlockModel();
    if (model != null) pack.addModel(getBlockModelId(), model);
  }

  /**
   * If the item id is null, which means the block item does not exist, the item model id will be null as well.
   *
   * @return The id of the block item model.
   */
  @Override
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default Identifier getItemModelId() {
    final Identifier itemId = getItemId();
    if (itemId == null) return null;
    return itemId.brrp_prefixed("item/");
  }

  /**
   * The model of the block item. It probably directly inherits the block model. But sometimes it is a "layer0" with the texture (for example, grass panes). In that case you can override this model.<p>
   * If you do not need an item model, you can override it and make it return null.
   *
   * @return The item model.
   */
  @Override
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(pure = true)
  default ModelJsonBuilder getItemModel() {
    return ModelJsonBuilder.create(getBlockModelId());
  }

  /**
   * If the item model id is null, which means the block item does not exist, the item model will not be generated, let alone written.
   *
   * @param pack The runtime resource pack.
   */
  @Override
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(mutates = "param1")
  default void writeItemModel(RuntimeResourcePack pack) {
    final Identifier itemModelId = getItemModelId();
    if (itemModelId != null) {
      final @Nullable ModelJsonBuilder model = getItemModel();
      if (model != null) {
        pack.addModel(itemModelId, model);
      }
    }
  }

  /**
   * Write the block states definitions, block models and item models.
   *
   * @param pack The runtime resource pack.
   */
  @Override
  @PreferredEnvironment(EnvType.CLIENT)
  @Contract(mutates = "param1")
  default void writeAssets(RuntimeResourcePack pack) {
    writeBlockStates(pack);
    writeBlockModel(pack);
    writeItemModel(pack);
  }


  // SERVER PART

  /**
   * Get the id of the block loot table. It's by default in the format of <code><var>namespace:</var>blocks/<var>path</var></code>, note its "{@code blocks}" instead of "{@code block}". The loot table is used when the block is broken. This method respects {@link Block#getLootTableId()}, which may be influenced by {@link Block.Settings#dropsNothing()} or {@link Block.Settings#dropsLike(Block)}.
   *
   * @return The id of the block loot table.
   */
  @Contract(pure = true)
  @ApiStatus.NonExtendable
  default Identifier getLootTableId() {
    if (this instanceof AbstractBlock block) {
      return block.getLootTableId();
    }
    return getBlockId().brrp_prefixed("blocks/");
  }

  /**
   * Get the block loot table. It's by default the simplest loot table, which means one block of itself will be dropped when broken. <em>Note that this method is used just for data generation, instead of real usage in the gameplay. You should override this if this object is not {@link Block} or {@link Item}.</em>
   *
   * @return The block loot table. If you do not need to generate the loot table, you can make it return {@code null}.
   */
  @Contract(pure = true)
  default LootTable.Builder getLootTable() {
    return BlockLootTableGenerator.drops((ItemConvertible) this);
  }

  /**
   * Write the block loot table to the runtime resource pack. If the block drops nothing ({@link Block.Settings#dropsNothing()}, the loot table will not be generated. However, if the block drops other's loot table ({@link Block.Settings#dropsLike(Block)}, the loot table will be written in <em>that</em> id, and please notice of potential duplication.
   *
   * @param pack The runtime resource pack.
   */
  @Contract(mutates = "param1")
  default void writeLootTable(RuntimeResourcePack pack) {
    final Identifier lootTableId = getLootTableId();
    if (lootTableId.equals(LootTables.EMPTY)) {
      // If the loot table is empty, don't write.
      return;
    }
    final LootTable.Builder lootTable = getLootTable();
    if (lootTable != null) {
      pack.addLootTable(lootTableId, lootTable);
    }
  }

  /**
   * Get the stonecutting recipe of the block. This is quite useful for block-based blocks, like stairs, slabs and fences.
   * <p>
   * <strong>Note:</strong> Stonecutting recipes will not be generated unless {@link #shouldWriteStonecuttingRecipe()} returns {@code true}.
   *
   * @return The stonecutting recipe.
   */
  @Contract(pure = true)
  default SingleItemRecipeJsonBuilder getStonecuttingRecipe() {
    return null;
  }

  /**
   * Whether to write stonecutting recipe. <strong>It's by default <code>false</code></strong> and you can override this method according to your actual need.
   *
   * @return The boolean value indicating whether to write stonecutting recipes of the block in {@link #writeRecipes(RuntimeResourcePack)}.
   */
  @Contract(pure = true)
  default boolean shouldWriteStonecuttingRecipe() {
    return false;
  }

  /**
   * For blocks, they may have stonecutting recipes. If you {@link #shouldWriteStonecuttingRecipe()} does not return {@code false} and {@link #getStonecuttingRecipe()} returns not null, the stonecutting recipe will be generated. The id of the stonecutting recipe is by default the crafting id appended with {@code "_from_stonecutting"}, but you can override it in {@link #getStonecuttingRecipeId()}. <b>The advancement will not be written if it does not have an item group.</b>
   *
   * @param pack The runtime resource pack.
   */
  @Override
  default void writeRecipes(RuntimeResourcePack pack) {
    ItemResourceGenerator.super.writeRecipes(pack);
    if (shouldWriteStonecuttingRecipe()) {
      final SingleItemRecipeJsonBuilder stonecuttingRecipe = getStonecuttingRecipe();
      if (stonecuttingRecipe != null) {
        pack.addRecipeAndAdvancement(getStonecuttingRecipeId(), stonecuttingRecipe);
      }
    }
  }

  /**
   * @return The id of the stonecutting recipe. It is usually the recipe id appended {@code "_from_stonecutting"}.
   */
  @NotNull
  @Contract(pure = true)
  default Identifier getStonecuttingRecipeId() {
    return getRecipeId().brrp_suffixed("_from_stonecutting");
  }

  /**
   * {@inheritDoc}
   *
   * @param pack The runtime resource pack.
   */
  default void writeData(RuntimeResourcePack pack) {
    writeLootTable(pack);
    writeRecipes(pack);
  }
}
