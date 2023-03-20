package pers.solid.brrp.v1.forge;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import pers.solid.brrp.v1.RRPEventHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>The Forge event of runtime resource pack loading. It will be posted each time loading resources (both client and server).
 * <p>In the development environment, this class is placed in the {@code common} submodule to avoid conflict with Fabric submodule (because it shares a same package name with common part). This class should neither exist nor be used in Fabric.
 * <p>If you're seeking something that supports both Fabric and Forge, you may see {@link RRPEventHelper}.
 */
public abstract class RRPEvent extends Event implements IModBusEvent {
  private final List<ResourcePack> runtimeResourcePacks;
  public final ResourceType resourceType;

  public RRPEvent(List<ResourcePack> pack, ResourceType resourceType) {
    this.runtimeResourcePacks = pack;
    this.resourceType = resourceType;
  }

  @Deprecated
  public RRPEvent(List<ResourcePack> pack) {
    this(pack, null);
  }

  public void addPack(ResourcePack pack) {
    runtimeResourcePacks.add(pack);
  }

  public void addPack(ResourcePack... packs) {
    runtimeResourcePacks.addAll(Arrays.asList(packs));
  }

  public void addPack(Collection<? extends ResourcePack> packs) {
    runtimeResourcePacks.addAll(packs);
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

  @ApiStatus.Experimental
  public static class BeforeUser extends RRPEvent {
    public BeforeUser(List<ResourcePack> pack, ResourceType resourceType) {
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