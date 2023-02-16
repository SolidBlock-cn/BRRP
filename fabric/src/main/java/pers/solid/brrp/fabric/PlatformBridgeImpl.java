package pers.solid.brrp.fabric;

import com.google.common.collect.Lists;
import net.devtech.arrp.ARRP;
import net.devtech.arrp.api.RRPPreGenEntrypoint;
import net.devtech.arrp.api.SidedRRPCallback;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import pers.solid.brrp.PlatformBridge;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

@ApiStatus.AvailableSince("0.8.1")
public class PlatformBridgeImpl extends PlatformBridge {
  private PlatformBridgeImpl() {
  }

  public static final PlatformBridgeImpl INSTANCE = new PlatformBridgeImpl();

  @Override
  public void postBefore(ResourceType type, List<ResourcePack> packs) {
    SidedRRPCallback.BEFORE_VANILLA.invoker().insert(type, Lists.reverse(packs));
  }

  @Override
  public void postAfter(ResourceType type, List<ResourcePack> packs) {
    SidedRRPCallback.AFTER_VANILLA.invoker().insert(type, packs);
  }

  @Override
  public void prelaunch() {
    ARRP.LOGGER.info("BRRP data generation: PreLaunch");
    FabricLoader loader = FabricLoader.getInstance();
    List<Future<?>> futures = new ArrayList<>();
    for (RRPPreGenEntrypoint entrypoint : loader.getEntrypoints("rrp:pregen", RRPPreGenEntrypoint.class)) {
      futures.add(RuntimeResourcePackImpl.EXECUTOR_SERVICE.submit(entrypoint::pregen));
    }
    ARRP.futures = futures;
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return FabricLoader.getInstance().isDevelopmentEnvironment();
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

  @Override
  public void setItemGroup(Collection<ItemStack> stacks) {
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.addAll(stacks));
  }

  public static PlatformBridge getInstance() {
    return INSTANCE;
  }
}
