package pers.solid.brrp.v1.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.PlatformBridge;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author PTOM76
 * @author Devan-Kerman
 */
@Mixin(FileResourcePackProvider.class)
public class FileResourcePackProviderMixin {
  @Shadow
  @Final
  private ResourceType type;

  @Inject(method = "register", at = @At("HEAD"))
  public void register(Consumer<ResourcePackProfile> adder, CallbackInfo ci) {
    List<ResourcePack> list = new ArrayList<>();
    BRRPMixins.LOGGER.info("BRRP register - before user");
    PlatformBridge.getInstance().postBeforeUser(type, list);

    for (ResourcePack pack : list) {
      // The pack version of the runtime resource pack is always seen as compatible
      final ResourcePackProfile.Metadata metadata = ResourcePackProfile.loadMetadata(pack.getInfo(), new ResourcePackProfile.PackFactory() {
        @Override
        public ResourcePack open(ResourcePackInfo info) {
          return pack;
        }

        @Override
        public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
          return pack;
        }
      }, SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES));
      if (metadata == null) continue;
      adder.accept(new ResourcePackProfile(
          pack.getInfo(),
          new ResourcePackProfile.PackFactory() {
            @Override
            public ResourcePack open(ResourcePackInfo info) {
              return pack;
            }

            @Override
            public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
              return pack;
            }
          },
          new ResourcePackProfile.Metadata(pack instanceof final RuntimeResourcePack runtimeResourcePack && runtimeResourcePack.getDescription() != null ? runtimeResourcePack.getDescription() : metadata.description(), ResourcePackCompatibility.COMPATIBLE, metadata.requestedFeatures(), Collections.emptyList()),
          new ResourcePackPosition(false, ResourcePackProfile.InsertionPosition.TOP, false)
      ));
    }
  }
}