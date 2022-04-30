package net.devtech.arrp.generator;

import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockStates;
import net.devtech.arrp.json.loot.JLootTable;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.recipe.JRecipe;
import net.fabricmc.api.EnvType;
import net.minecraft.block.Block;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>The interface is used for blocks.</p>
 * <p>Your custom block class can implement this interface, and override some methods you need. For example:</p>
 * <p>This interface simply <i>extends</i> {@link ItemResourceGenerator}, as the resources of the block item related to it will be also generated. It's also possible that the block does not have a block item; in this case the recipe and item model should be ignored.</p>
 * <pre>{@code
 * public class MyBlock extends Block implements BlockResourceGenerator {
 *   [...]
 *   @Environment(EnvType.CLIENT)
 *   public JModel getBlockModel() {
 *     return [...]
 *   }
 *   [...]
 * }
 * }</pre>
 * <p>Also, your custom class can implement this interface. In this case, you <i>must</i> override {@link #getBlockId()} method, as it cannot be casted to {@code Block}.</p>
 * <p>It's highly recommended but not required to annotate methods related to client (block states, block models, item models) with {@code @}{@link net.fabricmc.api.Environment Environment}<code>({@link net.fabricmc.api.EnvType#CLIENT EnvType.CLIENT})</code>, as they are only used in the client version mod. The interface itself does not annotate it, in consideration of rare situations that the server really needs. But mostly these client-related methods are not needed in the server side.</p>
 */
public interface BlockResourceGenerator extends ItemResourceGenerator {
  /**
   * The base block of this block. You <i>should override</i> this method for block-based blocks, like stairs, slab, etc.
   *
   * @return The base block of this block.
   */
  @Contract(pure = true)
  default @Nullable Block getBaseBlock() {
    return null;
  }

  /**
   * Query the id of the block in {@link Registry#BLOCK}.<br>
   * You <i>must</i> override this method if you're implementing this interface on a non-{@code Block} class, or will use it when it is not yet registered.
   *
   * @return The id of the block.
   */
  default Identifier getBlockId() {
    return Registry.BLOCK.getId((Block) this);
  }

  /**
   * Query the id of the corresponding block item. You can override when needed, but most time there is no need.
   * <p>
   * Usually the block id is the same as the item id, but we do not assume that here.
   *
   * @return The id of the block item, or {@code null} if the block has no item.
   */
  @Override
  default Identifier getItemId() {
    if (this instanceof Block block && BlockItem.BLOCK_ITEMS.containsKey(block)) {
      return Registry.ITEM.getId(BlockItem.BLOCK_ITEMS.get(block));
    } else {
      return null;
    }
  }


  // CLIENT PART


  /**
   * The id of the block model. It is usually <code style="color: maroon"><i>namespace</i>:block/<i>path</i></code>. For example, the model id of stone is {@code minecraft:block/stone}.
   *
   * @return The id of the block model.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  default Identifier getBlockModelId() {
    return getBlockId().brrp_prepend("block/");
  }

  /**
   * <p>The texture used in models. It's usually in the format of {@code <i>namespace</i>:block/<i>path</i>}, which <i>mostly</i> equals to the block id. However, sometimes they differ. For example, the texture of <code style="color:maroon">minecraft:smooth_sandstone</code> is not <code style="color:maroon">minecraft:block/smooth_sandstone</code>; it's <code style="color:maroon">minecraft:block/sandstone_top</code>.</p>
   * <p>Some blocks have different textures in different parts. In this case, the parameter {@code type} is used. For example, a quartz pillar can have the following methods:</p>
   * <pre>{@code
   *   @Environment(EnvType.CLIENT) @Override
   *   public getTextureId(String type) {
   *     if ("end".equals(type)) {
   *       return new Identifier("minecraft", "block/quartz_pillar_top");
   *     } else {
   *       return new Identifier("minecraft", "block/quartz_pillar");
   *     }
   *   }
   * }</pre>
   *
   * @param textureKey The type used to distinguish texture.
   * @return The id of the texture.
   * @see TextureRegistry
   */
  @PreferredEnvironment(EnvType.CLIENT)
  default @NotNull String getTextureId(@NotNull TextureKey textureKey) {
    if (this instanceof Block thisBlock) {
      final Identifier texture = TextureRegistry.getTexture(thisBlock, textureKey);
      if (texture != null) return texture.toString();
      final @Nullable Block baseBlock = getBaseBlock();
      if (baseBlock != null) {
        return ResourceGeneratorHelper.getTextureId(baseBlock, textureKey);
      }
    }
    return getBlockId().brrp_prepend("block/").toString();
  }

  /**
   * The block states definition of the block. In the object, the returned value of {@link #getBlockModelId()} is often used.
   *
   * @return The block states definition of the block.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  default @Nullable JBlockStates getBlockStates() {
    return null;
  }

  /**
   * Write the block states definition (returned in {@link #getBlockStates}) to the runtime resource pack, if that is not {@code null}. Usually a block has one block states definition file, with the id identical to the block id.
   *
   * @param pack The runtime resource pack.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  default void writeBlockStates(RuntimeResourcePack pack) {
    final JBlockStates blockStates = getBlockStates();
    if (blockStates != null) pack.addBlockState(blockStates, getBlockId());
  }

  /**
   * The model of the block. If a block has multiple models, you may override this method for the most basic model, and override {@link #writeBlockModel}.
   *
   * @return The block model.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  default @Nullable JModel getBlockModel() {
    return null;
  }

  /**
   * Write the block model (returned in {@link #getBlockModel}) to the runtime resource pack. It does nothing if the returned model is {@code null}. If the block has multiple models, you may override this method.
   *
   * @param pack The runtime resource pack.
   */
  @PreferredEnvironment(EnvType.CLIENT)
  default void writeBlockModel(RuntimeResourcePack pack) {
    final JModel model = getBlockModel();
    if (model != null) pack.addModel(model, getBlockModelId());
  }

  /**
   * If the item id is null, which means the block item does not exist, the item model id will be null as well.
   *
   * @return The id of the block item model.
   */
  @Override
  @PreferredEnvironment(EnvType.CLIENT)
  default Identifier getItemModelId() {
    final Identifier itemId = getItemId();
    if (itemId == null) return null;
    return itemId.brrp_prepend("item/");
  }

  /**
   * If the item id is null, which means the block item does not exist, the item model will not be generated, let alone written.
   *
   * @param pack The runtime resource pack.
   */
  @Override
  @PreferredEnvironment(EnvType.CLIENT)
  default void writeItemModel(RuntimeResourcePack pack) {
    final Identifier itemModelId = getItemModelId();
    if (itemModelId != null) {
      final JModel model = getItemModel();
      if (model != null) {
        pack.addModel(model, itemModelId);
      }
    }
  }

  /**
   * The model of the block item. It probably directly inherits the block model. But sometimes it is a "layer0" with the texture (for example, grass panes). In that case you can override this model.<p>
   * If you do not need an item model, you can override it and make it return null.
   *
   * @return The item model.
   */
  @Override
  @PreferredEnvironment(EnvType.CLIENT)
  default @Nullable JModel getItemModel() {
    return new JModel(getBlockModelId());
  }

  /**
   * Write the block states definitions, block models and item models.
   *
   * @param pack The runtime resource pack.
   */
  @Override
  @PreferredEnvironment(EnvType.CLIENT)
  default void writeAssets(RuntimeResourcePack pack) {
    writeBlockStates(pack);
    writeBlockModel(pack);
    writeItemModel(pack);
  }


  // SERVER PART

  /**
   * Get the id of the block loot table. It's by default in the format of <code><i>namespace:</i>blocks/<i>path</i></code>, note its "blocks" instead of "block". The loot table is used when the block is broken.
   *
   * @return The id of the block loot table.
   */
  default Identifier getLootTableId() {
    return getBlockId().brrp_prepend("blocks/");
  }

  /**
   * Get the block loot table. It's by default the simplest loot table, which means one block of itself will be dropped when broken.
   *
   * @return The block loot table.
   */
  default JLootTable getLootTable() {
    return JLootTable.simple(getItemId().toString());
  }

  /**
   * Write the block loot table to the runtime resource pack.
   *
   * @param pack The runtime resource pack.
   */
  default void writeLootTable(RuntimeResourcePack pack) {
    final JLootTable lootTable = getLootTable();
    if (lootTable != null) {
      pack.addLootTable(getLootTableId(), lootTable);
    }
  }

  /**
   * Get the stonecutting recipe of the block. This is quite useful for block-based blocks, like stairs, slabs and fences.
   * <p>
   * <b>Note:</b> Stonecutting recipes will not be generated unless {@link #shouldWriteStonecuttingRecipe()} returns {@code true}.
   *
   * @return The stonecutting recipe.
   * @see net.devtech.arrp.json.recipe.JStonecuttingRecipe
   */
  @ApiStatus.AvailableSince("0.6.2")
  default @Nullable JRecipe getStonecuttingRecipe() {
    return null;
  }

  /**
   * Whether to write stonecutting recipe. <b>It's by default <code>false</code></b> and you can override this method according to your actual need.
   *
   * @return The boolean value indicating whether to write stonecutting recipes of the block in {@link #writeRecipes(RuntimeResourcePack)}.
   */
  @ApiStatus.AvailableSince("0.6.2")
  default boolean shouldWriteStonecuttingRecipe() {
    return false;
  }

  /**
   * For blocks, they may have stonecutting recipes. If you {@link #shouldWriteStonecuttingRecipe()} does not return {@code false} and {@link #getStonecuttingRecipe()} returns not null, the stonecutting recipe will be generated. The id of the stonecutting recipe is by default the crafting id appended with {@code "_from_stonecutting"}, but you can override it in {@link #getStonecuttingRecipeId()}.
   *
   * @param pack The runtime resource pack.
   */
  @ApiStatus.AvailableSince("0.6.2")
  @Override
  default void writeRecipes(RuntimeResourcePack pack) {
    ItemResourceGenerator.super.writeRecipes(pack);
    if (shouldWriteStonecuttingRecipe()) {
      final JRecipe stonecuttingRecipe = getStonecuttingRecipe();
      if (stonecuttingRecipe != null) {
        final Identifier stonecuttingRecipeId = getStonecuttingRecipeId();
        pack.addRecipe(stonecuttingRecipeId, stonecuttingRecipe);
        pack.addRecipeAdvancement(stonecuttingRecipeId, getAdvancementIdForRecipe(stonecuttingRecipeId), stonecuttingRecipe);
      }
    }
  }

  /**
   * @return The id of the stonecutting recipe. It is usually the recipe id appended {@code "_from_stonecutting"}.
   */
  @NotNull
  @ApiStatus.AvailableSince("0.6.2")
  private Identifier getStonecuttingRecipeId() {
    return getRecipeId().brrp_prepend("_from_stonecutting");
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
