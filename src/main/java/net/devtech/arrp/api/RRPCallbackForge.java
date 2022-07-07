package net.devtech.arrp.api;

import com.google.common.collect.ImmutableList;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This callback detects the environment type of the resource loaded. If you want to generate or add resource packs according to the resource loading environment, you can use this.
 *
 * @author SolidBlock
 * @since forge
 */
public final class RRPCallbackForge {
  /**
   * Register your type-specific resource pack at a higher priority than minecraft and mod resources.
   */
  public static final ImmutableList.Builder<Function<ResourceType, @Nullable ResourcePack>> BEFORE_VANILLA = new ImmutableList.Builder<>();
  /**
   * Register your type-specific resource pack at a lower priority than minecraft and mod resources.
   */
  public static final ImmutableList.Builder<Function<ResourceType, @Nullable ResourcePack>> AFTER_VANILLA = new ImmutableList.Builder<>();
}
