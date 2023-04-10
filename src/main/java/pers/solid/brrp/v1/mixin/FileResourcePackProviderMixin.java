package pers.solid.brrp.v1.mixin;

import com.google.common.base.Suppliers;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pers.solid.brrp.v1.PlatformBridge;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author PTOM76
 * @author Devan-Kerman
 */
@Mixin(FileResourcePackProvider.class)
public class FileResourcePackProviderMixin {
  private static final Logger LOGGER = LoggerFactory.getLogger("FileResourcePackProviderMixin");


  @Inject(method = "register", at = @At("HEAD"))
  public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory, CallbackInfo ci) {
    List<ResourcePack> list = new ArrayList<>();
    LOGGER.info("BRRP register - before user");
    PlatformBridge.getInstance().postBeforeUser(null, list);

    for (ResourcePack pack : list) {
      // The pack version of the runtime resource pack is always seen as compatible
      profileAdder.accept(ResourcePackProfile.of(
          pack.getName(),
          false,
          Suppliers.ofInstance(pack),
          factory,
          ResourcePackProfile.InsertionPosition.TOP,
          RuntimeResourcePack.RUNTIME
      ));
    }
  }
}