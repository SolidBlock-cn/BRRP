package pers.solid.brrp;

import com.google.common.base.Suppliers;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

@ApiStatus.AvailableSince("0.8.1")
public abstract class PlatformBridge {
  private static final Logger LOGGER = LoggerFactory.getLogger(PlatformBridge.class);
  private static final Supplier<PlatformBridge> instanceSupplier = Suppliers.memoize(() -> {
    try {
      final Class<?> fabricImpl = Class.forName("pers.solid.brrp.fabric.PlatformBridgeFabricImpl");
      return (PlatformBridge) fabricImpl.getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException e) {
      // 说明没有加载 Fabric，忽略
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
      LOGGER.error("The environment seems to be Fabric, but the loading of BRRP mod seems to have some errors:", e);
    }
    try {
      final Class<?> forgeImpl = Class.forName("pers.solid.brrp.forge.PlatformBridgeForgeImpl");
      return (PlatformBridge) forgeImpl.getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException e) {
      // 说明没有加载 Forge，忽略
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
      LOGGER.error("The environment seems to be Forge, but the loading of BRRP mod seems to have some errors:", e);
    }
    throw new IllegalStateException();
  });

  public abstract void postBefore(ResourceType type, List<ResourcePack> packs);

  public abstract void postAfter(ResourceType type, List<ResourcePack> packs);

  public static PlatformBridge getInstance() {
    return instanceSupplier.get();
  }

  public abstract void prelaunch();

  public abstract void onDevelopmentInitialize();

  public abstract Path getConfigDir();

  public abstract boolean isClientEnvironment();

  public abstract void registerBlock(Identifier identifier, Block block);

  public abstract void registerItem(Identifier identifier, Item item);
}
