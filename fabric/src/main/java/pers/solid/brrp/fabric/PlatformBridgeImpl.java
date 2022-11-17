package pers.solid.brrp.fabric;

import com.google.common.collect.Lists;
import net.devtech.arrp.ARRP;
import net.devtech.arrp.BRRPDevelopment;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RRPCallbackConditional;
import net.devtech.arrp.api.RRPPreGenEntrypoint;
import net.devtech.arrp.api.SidedRRPCallback;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import pers.solid.brrp.PlatformBridge;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@ApiStatus.AvailableSince("0.8.1")
public class PlatformBridgeImpl extends PlatformBridge {
  private PlatformBridgeImpl() {
  }

  public static final PlatformBridgeImpl INSTANCE = new PlatformBridgeImpl();

  @SuppressWarnings("deprecation")
  @Override
  public void postBefore(ResourceType type, List<ResourcePack> packs) {
    SidedRRPCallback.BEFORE_VANILLA.invoker().insert(type, Lists.reverse(packs));
    RRPCallbackConditional.BEFORE_VANILLA.invoker().insertTo(type, Lists.reverse(packs));
  }

  @SuppressWarnings("deprecation")
  @Override
  public void postAfter(ResourceType type, List<ResourcePack> packs) {
    SidedRRPCallback.AFTER_VANILLA.invoker().insert(type, packs);
    RRPCallbackConditional.AFTER_VANILLA.invoker().insertTo(type, packs);
  }

  @Override
  public void prelaunch() {
    SidedRRPCallback.BEFORE_VANILLA.register((type, resources) -> RRPCallback.BEFORE_VANILLA.invoker().insert(resources));
    SidedRRPCallback.AFTER_VANILLA.register((type, resources) -> RRPCallback.AFTER_VANILLA.invoker().insert(resources));
    ARRP.LOGGER.info("BRRP data generation: PreLaunch");
    FabricLoader loader = FabricLoader.getInstance();
    List<Future<?>> futures = new ArrayList<>();
    for (RRPPreGenEntrypoint entrypoint : loader.getEntrypoints("rrp:pregen", RRPPreGenEntrypoint.class)) {
      futures.add(RuntimeResourcePackImpl.EXECUTOR_SERVICE.submit(entrypoint::pregen));
    }
    ARRP.futures = futures;
  }

  @Override
  public void onDevelopmentInitialize() {
    BRRPDevelopment.registerPacks();
  }

  @Override
  public Path getConfigDir() {
    return FabricLoader.getInstance().getConfigDir();
  }

  @Override
  public boolean isClientEnvironment() {
    return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
  }

  @Override
  public void registerBlock(Identifier identifier, Block block) {
    Registry.register(Registries.BLOCK, identifier, block);
  }

  @Override
  public void registerItem(Identifier identifier, Item item) {
    Registry.register(Registries.ITEM, identifier, item);
  }

  public static PlatformBridge getInstance() {
    return INSTANCE;
  }
}
