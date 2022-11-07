package net.devtech.arrp.api.forge;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Arrays;
import java.util.List;

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

  public void addPacks(List<ResourcePack> packs) {
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