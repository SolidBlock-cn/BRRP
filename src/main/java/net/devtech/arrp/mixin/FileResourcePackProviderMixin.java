package net.devtech.arrp.mixin;

import com.mojang.bridge.game.PackType;
import net.devtech.arrp.ARRP;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pers.solid.brrp.PlatformBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author PTOM76
 * @author Devan-Kerman
 */
@Mixin(FileResourcePackProvider.class)
public class FileResourcePackProviderMixin {
  @Shadow
  @Final
  private ResourceType type;
  private static final ResourcePackSource RUNTIME = ResourcePackSource.create(getSourceTextSupplier(), true);
  private static final Logger ARRP_LOGGER = LoggerFactory.getLogger("ARRP/FileResourcePackProviderMixin");


  private static UnaryOperator<Text> getSourceTextSupplier() {
    Text text = Text.translatable("pack.source.runtime");
    return name -> Text.translatable("pack.nameAndSource", name, text).formatted(Formatting.GRAY);
  }

  @Inject(method = "register", at = @At("HEAD"))
  public void register(
      Consumer<ResourcePackProfile> adder,
      CallbackInfo ci
  ) throws ExecutionException, InterruptedException {
    List<ResourcePack> list = new ArrayList<>();
    ARRP.waitForPregen();
    ARRP_LOGGER.info("ARRP register - before user");
    PlatformBridge.getInstance().postBeforeUser(type, list);

    for (ResourcePack pack : list) {
      // The pack version of the runtime resource pack is always seen as compatible
      final ResourcePackProfile.Metadata metadata = ResourcePackProfile.loadMetadata(pack.getName(), name -> pack);
      if (metadata == null) continue;
      adder.accept(ResourcePackProfile.of(
          pack.getName(),
          pack instanceof final RuntimeResourcePack runtimeResourcePack ? runtimeResourcePack.getDisplayName() : Text.literal(pack.getName()), // the line modified by SolidBlock
          false,
          name -> pack,
          new ResourcePackProfile.Metadata(pack instanceof final RuntimeResourcePack runtimeResourcePack ? runtimeResourcePack.getDescription() : metadata.description(), SharedConstants.getGameVersion().getPackVersion(type == ResourceType.SERVER_DATA ? PackType.DATA : PackType.RESOURCE), metadata.requestedFeatures()),
          this.type,
          ResourcePackProfile.InsertionPosition.TOP,
          false,
          RUNTIME
      ));
    }
  }
}