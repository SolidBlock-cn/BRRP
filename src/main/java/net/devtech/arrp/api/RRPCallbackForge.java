package net.devtech.arrp.api;

import com.google.common.collect.ImmutableList;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This callback detects the environment type of the resource loaded. If you want to generate or add resource packs according to the resource loading environment, you can use this.
 * <p>Note: this class only exists in Forge.
 *
 * @author SolidBlock
 * @since forge
 * @deprecated Please use {@link RRPEvent} or {@link RRPEventHelper}.
 */
@ApiStatus.ScheduledForRemoval(inVersion = "0.9.0")
@Deprecated
public final class RRPCallbackForge {
  /**
   * Register your type-specific resource pack at a higher priority than minecraft and mod resources.
   *
   * @deprecated use {@link RRPEvent.BeforeVanilla} or {@link RRPEventHelper#BEFORE_VANILLA}.
   */
  @Deprecated
  public static final ImmutableList.Builder<Function<ResourceType, @Nullable ResourcePack>> BEFORE_VANILLA = new ImmutableList.Builder<>();
  /**
   * Register your type-specific resource pack at a lower priority than minecraft and mod resources.
   *
   * @deprecated use {@link RRPEvent.AfterVanilla} or {@link RRPEventHelper#AFTER_VANILLA}.
   */
  @Deprecated
  public static final ImmutableList.Builder<Function<ResourceType, @Nullable ResourcePack>> AFTER_VANILLA = new ImmutableList.Builder<>();
}
