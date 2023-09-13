package pers.solid.brrp.v1;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
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

  @ApiStatus.Internal public static PlatformBridge __instance = null;

  public static PlatformBridge getInstance() {
    return __instance;
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
