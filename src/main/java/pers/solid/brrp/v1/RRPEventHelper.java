package pers.solid.brrp.v1;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.Collection;
import java.util.function.Function;

/**
 * This is a useful class that can work for both Fabric and Forge. It can help you register runtime resource packs according to the platform using, taking advantage of the Architectury plugin.
 */
public abstract class RRPEventHelper {
  /**
   * Register your resource pack that will be read <strong>before</strong> Minecraft and regular resources are loaded. If Minecraft vanilla resources or other non-runtime resources exist in the same resource location, that will be used instead. Therefore, resource packs registered here cannot override non-runtime resources.
   */
  public static final RRPEventHelper BEFORE_VANILLA = getBeforeVanilla();
  @ApiStatus.Experimental
  public static final RRPEventHelper BEFORE_USER = getBeforeUser();

  /**
   * Register your resource pack that will be read <strong>after</strong> Minecraft and regular resources are loaded. If Minecraft vanilla resources or other non-runtime resources exist in the same resource location, they will be overridden by this runtime resource. Therefore, if you want to override Minecraft vanilla resources and other non-runtime resources, you can register here.
   */
  public static final RRPEventHelper AFTER_VANILLA = getAfterVanilla();

  @ApiStatus.Internal
  @ExpectPlatform
  public static RRPEventHelper getBeforeVanilla() {
    throw new AssertionError();
  }

  @ApiStatus.Internal
  @ApiStatus.Experimental
  @ExpectPlatform
  public static RRPEventHelper getBeforeUser() {
    throw new AssertionError();
  }

  @ApiStatus.Internal
  @ExpectPlatform
  public static RRPEventHelper getAfterVanilla() {
    throw new AssertionError();
  }

  /**
   * Register a simple resource pack regardless of the resource type.
   */
  public abstract void registerPack(RuntimeResourcePack pack);

  /**
   * Register a simple resource pack on the specific resource type.
   */
  public abstract void registerSidedPack(ResourceType resourceType, RuntimeResourcePack pack);

  /**
   * Register a resource pack function. Each time loading resources, the function will be applied, taking the resourceType as a parameter. You can make it generate resources each time loading resources so that some modifications can be "hot-swapped", which is not recommended unless in development environment. The function can return a {@code null}, which means in this case the pack will not be added.
   */
  public abstract void registerPack(@NotNull Function<ResourceType, @Nullable RuntimeResourcePack> packFunction);

  /**
   * Register a function that return a collection of resource packs. Each time loading resources, the function will be applied, taking the resourceType as the parameter. Like {@link #registerPack(Function)}, you can make it generate resources each time loading resources. The collection returned may be empty, but should not be null.
   */
  public abstract void registerPacks(@NotNull Function<ResourceType, @NotNull Collection<RuntimeResourcePack>> packsFunction);
}
