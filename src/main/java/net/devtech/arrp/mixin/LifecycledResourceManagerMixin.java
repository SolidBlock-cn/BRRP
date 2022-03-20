package net.devtech.arrp.mixin;

import com.google.common.collect.ImmutableList;
import net.devtech.arrp.api.RRPCallback;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(LifecycledResourceManagerImpl.class)
public class LifecycledResourceManagerMixin {
  private static final Logger ARRP_LOGGER = LoggerFactory.getLogger(LifecycledResourceManagerMixin.class);
  @Mutable
  @Shadow
  @Final
  private List<ResourcePack> packs;

  //  @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
  private Iterator<ResourcePack> injected(List<ResourcePack> instance) {
    return packs.iterator();
  }

  //  @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/resource/LifecycledResourceManagerImpl;packs:Ljava/util/List;"))
  private void injected1(LifecycledResourceManagerImpl instance, List<ResourcePack> value) {
    ImmutableList.Builder<ResourcePack> builder = new ImmutableList.Builder<>();
    List<ResourcePack> before = new ArrayList<>();
    RRPCallback.BEFORE_VANILLA.invoker().insert(before);
    builder.addAll(before);
    builder.addAll(value);
    List<ResourcePack> after = new ArrayList<>();
    RRPCallback.AFTER_VANILLA.invoker().insert(after);
    builder.addAll(after);
    ARRP_LOGGER.info("Before {} packs, after {} packs.", before.size(), after.size());
    packs = builder.build();
    ARRP_LOGGER.info("Instance = {}, packs.size() = {}", instance, packs.size());
  }
}
