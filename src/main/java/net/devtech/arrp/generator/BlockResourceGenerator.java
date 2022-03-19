package net.devtech.arrp.generator;

import net.devtech.arrp.IdentifierExtension;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.BlockStatesDefinition;
import net.devtech.arrp.json.models.JModel;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>The interface is used for blocks.</p>
 * <p>As implemented in {@link net.devtech.arrp.mixin.BlockMixin}, this interface is by default implemented by {@link Block}s. The compiler does not think so, as it is not declared in the class, but you can simply cast a block object and call methods of it. For example: </p>
 * <pre>{@code
 * return ((BlockResourceGenerator) Blocks.STONE).getId();
 * }</pre>
 * <p>Your custom block class can implement this interface, and override some methods you need. For example:</p>
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
 * <p>It's highly recommended but not required to annotate methods related to client (block states definitions, block models, item models) as {@code @}{@link net.fabricmc.api.Environment Environment}<code>({@link net.fabricmc.api.EnvType#CLIENT EnvType.CLIENT})</code>, as they are only used in the client version mod. The interface itself does not annotate it, in consideration of rare situations that the server really needs. But mostly these client-related methods are not needed in the server side.</p>
 */
public interface BlockResourceGenerator {
  /**
   * Query the id of the block in {@link Registry#BLOCK}.<br>
   * You <i>must</i> override this method if you're implementing this interface on a non-{@code Block} class.
   *
   * @return The id of the block.
   */
  default Identifier getBlockId() {
    return Registry.BLOCK.getId((Block) this);
  }

  /**
   * Query the id of the corresponding block item. We assume that the block item has the same id as that of the block, and does not check if the block item really exists. You can override when needed, but most time there is no need.
   *
   * @return The id of the block item.
   */
  default Identifier getItemId() {
    return getBlockId();
  }


  // CLIENT PART


  /**
   * The id of the block model. It is usually {@code <i>namespace</i>:block/<i>path</i>}. For example, the model id of stone is {@code minecraft:block/stone}.
   *
   * @return The id of the block model.
   */
  default Identifier getBlockModelId() {
    return ((IdentifierExtension) getBlockId()).prepend("block/");
  }

  /**
   * The id of the model of its block item. It is usually {@code <i>namespace</i>:item/<i>path</i>}.
   *
   * @return The id of the item model.
   */
  default Identifier getItemModelId() {
    return ((IdentifierExtension) getItemId()).prepend("item/");
  }

  /**
   * The texture that used in models. It's usually in the format of {@code <i>namespace</i>:block/<i>path</i>}, which <i>mostly</i> equals to the block id. However, sometimes they differ. For example, the texture of {@code minecraft:smooth_sandstone} is not {@code minecraft:block/smooth_sandstone}; it's {@code minecraft:block/sandstone_top}.<p>
   * Some blocks have different textures in different parts. In this case, the parameter {@code type} is used. For example, a quartz pillar can have the following methods:
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
   * @param type The type used to distinguish texture.
   * @return The id of the texture.
   */
  default @NotNull String getTextureId(@Nullable String type) {
    if (this instanceof Block) {
      final String texture = TextureRegistry.getTexture((Block) this, type);
      if (texture != null) return texture;
    }
    return ((IdentifierExtension) getBlockId()).prepend("block/").toString();
  }

  /**
   * The block states definition of the block. In the object, the returned value of {@link #getBlockModelId()} is often used.
   *
   * @return The block states definition of the block.
   */
  default @Nullable BlockStatesDefinition getBlockStatesDefinition() {
    return null;
  }

  /**
   * Write the block states definition (returned in {@link #getBlockStatesDefinition}) to the runtime resource pack, if that is not {@code null}. Usually a block has one block states definition file, with the id identical to the block id.
   *
   * @param pack The runtime resource pack.
   */
  default void writeBlockStatesDefinition(RuntimeResourcePack pack) {
    pack.addBlockState(getBlockStatesDefinition(), getBlockId());
  }

  /**
   * The model of the block. If a block has multiple models, you may override this method for the most basic model, and override {@link #writeBlockModel}.
   *
   * @return The block model.
   */
  default @Nullable JModel getBlockModel() {
    return null;
  }

  /**
   * Write the block model (returned in {@link #getBlockModel}) to the runtime resource pack. It does nothing if the returned model is {@code null}. If the block has multiple models, you may override this method.
   *
   * @param pack The runtime resource pack.
   */
  default void writeBlockModel(RuntimeResourcePack pack) {
    final JModel model = getBlockModel();
    if (model != null) pack.addModel(model, getBlockModelId());
  }

  /**
   * The model of the block item. It probably directly inherits the block model. But sometimes it is a "layer0" with the texture (for example, grass panes). In that case you can override this model.<p>
   * If you do not need an item model, you can override it and make it return null.
   *
   * @return The item model.
   */
  default @Nullable JModel getItemModel() {
    return new JModel(getBlockModelId());
  }

  /**
   * Write the item model (returned in {@link #getItemModel} to the runtime resource pack. It does nothing if the returned model is {@code null}.
   *
   * @param pack The runtime resource pack.
   */
  default void writeItemModel(RuntimeResourcePack pack) {
    final JModel model = getItemModel();
    if (model != null) pack.addModel(model, getItemModelId());
  }

  /**
   * Write the block states definitions, block models and item models.
   *
   * @param pack The runtime resource pack.
   */
  default void writeAssets(RuntimeResourcePack pack) {
    writeBlockStatesDefinition(pack);
    writeBlockModel(pack);
    writeItemModel(pack);
  }


  // SERVER PART
}
