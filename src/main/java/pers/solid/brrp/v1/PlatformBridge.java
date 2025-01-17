package pers.solid.brrp.v1;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.TestOnly;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 * The class is used in the multi-platform development.
 */
@ApiStatus.Internal
public abstract class PlatformBridge {

  @SuppressWarnings("Contract")
  @Contract("-> _")
  @ExpectPlatform
  public static PlatformBridge getInstance() {
    throw new AssertionError();
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
