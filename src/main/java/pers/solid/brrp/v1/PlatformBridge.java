package pers.solid.brrp.v1;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.TestOnly;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 * The class is used in the multi-platform development.
 */
@ApiStatus.Internal
public abstract class PlatformBridge {

  private static final class InstanceHolder {
    private static final PlatformBridge INSTANCE;

    static {
      try {
        final Class<?> c = Class.forName("pers.solid.brrp.v1.fabric.PlatformBridgeImpl");
        INSTANCE = (PlatformBridge) c.getMethod("getInstance").invoke(null, ArrayUtils.EMPTY_OBJECT_ARRAY);
      } catch (ReflectiveOperationException | ClassCastException e) {
        throw new RuntimeException("The Better Runtime Resource Pack mod is not correctly loaded. Please contact the mod author.", e);
      }
    }
  }

  public static PlatformBridge getInstance() {
    return InstanceHolder.INSTANCE;
  }

  public abstract void postBefore(ResourceType type, List<ResourcePack> packs);

  public abstract void postBeforeUser(ResourceType type, List<ResourcePack> packs);

  public abstract void postAfter(ResourceType type, List<ResourcePack> packs);

  public abstract boolean isDevelopmentEnvironment();

  public abstract Path getConfigDir();

  public abstract boolean isClientEnvironment();

  public abstract void registerBlock(Identifier identifier, Block block);

  public abstract void registerItem(Identifier identifier, Item item);

  @TestOnly
  @ApiStatus.Internal
  public abstract void setItemGroup(Collection<ItemStack> stacks);
}
