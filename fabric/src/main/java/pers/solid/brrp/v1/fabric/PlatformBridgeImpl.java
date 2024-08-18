package pers.solid.brrp.v1.fabric;

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
import pers.solid.brrp.v1.PlatformBridge;
import pers.solid.brrp.v1.fabric.api.SidedRRPCallback;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class PlatformBridgeImpl extends PlatformBridge {
  public static final PlatformBridgeImpl INSTANCE = new PlatformBridgeImpl();

  private PlatformBridgeImpl() {
  }

  public static PlatformBridge getInstance() {
    return INSTANCE;
  }

  @Override
  public void postBefore(ResourceType type, List<ResourcePack> packs) {
    SidedRRPCallback.BEFORE_VANILLA.invoker().insert(type, packs);
  }

  @Override
  public void postBeforeUser(ResourceType type, List<ResourcePack> packs) {
    SidedRRPCallback.BEFORE_USER.invoker().insert(type, packs);
  }

  @Override
  public void postAfter(ResourceType type, List<ResourcePack> packs) {
    SidedRRPCallback.AFTER_VANILLA.invoker().insert(type, packs);
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
}
