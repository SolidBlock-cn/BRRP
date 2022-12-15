package net.devtech.arrp.api;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>The Forge event of runtime resource pack loading. It will be posted each time loading resources (both client and server).
 * <p>In the development environment, this class is placed in the {@code common} submodule to avoid conflict with Fabric submodule (because it shares a same package name with common part). This class should neither exist nor be used in Fabric.
 * <p>If you're seeking something that supports both Fabric and Forge, you may see {@link RRPEventHelper}.
 */
public class RRPEvent extends Event implements IModBusEvent {
  private final List<ResourcePack> runTimeResourcePacks;
  public final ResourceType resourceType;

  public RRPEvent(List<ResourcePack> pack, ResourceType resourceType) {
    this.runTimeResourcePacks = pack;
    this.resourceType = resourceType;
  }

  @Deprecated
  public RRPEvent(List<ResourcePack> pack) {
    this(pack, null);
  }

  public void addPack(ResourcePack pack) {
    runTimeResourcePacks.add(pack);
  }

  public void addPacks(ResourcePack... packs) {
    runTimeResourcePacks.addAll(Arrays.asList(packs));
  }

  /**
   * This method is kept for compatibility.
   */
  public void addPacks(List<? extends ResourcePack> packs) {
    runTimeResourcePacks.addAll(packs);
  }

  public void addPacks(Collection<? extends ResourcePack> packs) {
    runTimeResourcePacks.addAll(packs);
  }

  public static class BeforeVanilla extends RRPEvent {
    @Deprecated
    public BeforeVanilla(List<ResourcePack> pack) {
      super(pack);
    }

    public BeforeVanilla(List<ResourcePack> pack, ResourceType resourceType) {
      super(pack, resourceType);
    }
  }

  public static class AfterVanilla extends RRPEvent {
    public AfterVanilla(List<ResourcePack> pack, ResourceType resourceType) {
      super(pack, resourceType);
    }

    @Deprecated
    public AfterVanilla(List<ResourcePack> pack) {
      super(pack, null);
    }
  }
}