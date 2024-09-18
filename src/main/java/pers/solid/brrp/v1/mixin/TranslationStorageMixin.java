package pers.solid.brrp.v1.mixin;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.Resource;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.api.ImmediateInputSupplier;
import pers.solid.brrp.v1.api.LanguageProvider;

import java.io.InputStream;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

@Mixin(TranslationStorage.class)
public abstract class TranslationStorageMixin {
  @WrapOperation(method = "load(Ljava/lang/String;Ljava/util/List;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/Resource;getInputStream()Ljava/io/InputStream;"))
  private static InputStream wrapGetInputStream(Resource instance, Operation<InputStream> original, @Local(argsOnly = true) String langCode, @Local(argsOnly = true) Map<String, String> translations, @Share("shouldSkip") LocalBooleanRef ref) {
    if (((ResourceAccessor) instance).getInputSupplier() instanceof ImmediateInputSupplier<?> im) {
      final Object resource = im.resource();
      switch (resource) {
        case JsonObject jsonObject -> brrp$load(Iterables.transform(jsonObject.entrySet(), input -> Map.entry(input.getKey(), JsonHelper.asString(input.getValue(), input.getKey()))), translations::put);
        case LanguageProvider languageProvider -> brrp$load(languageProvider.content().entrySet(), translations::put);
        default -> BRRPMixins.LOGGER.warn("BRRP: Cannot read immediate language file (langCode = {}): {} is not a {}!", langCode, resource, LanguageProvider.class.getSimpleName());
      }
      ref.set(true);
      return null;
    } else {
      ref.set(false);
      return original.call(instance);
    }
  }

  @WrapWithCondition(method = "load(Ljava/lang/String;Ljava/util/List;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Language;load(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V"))
  private static boolean skipTraditionalLoad(InputStream inputStream, BiConsumer<String, String> entryConsumer, @Share("shouldSkip") LocalBooleanRef ref) {
    return !ref.get();
  }

  /**
   * from {@link Language#TOKEN_PATTERN}
   */
  @Unique
  private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");

  @Unique
  private static void brrp$load(Iterable<Map.Entry<String, String>> entries, BiConsumer<String, String> entryConsumer) {
    for (Map.Entry<String, String> entry : entries) {
      String string = TOKEN_PATTERN.matcher(entry.getValue()).replaceAll("%$1s");
      entryConsumer.accept(entry.getKey(), string);
    }
  }
}
