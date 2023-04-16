package pers.solid.brrp.v1.recipe.mixin;

import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pers.solid.brrp.v1.recipe.ShapedRecipeJsonBuilderExtension;

@Mixin(ShapedRecipeJsonBuilder.class)
public abstract class ShapedRecipeJsonBuilderMixin implements ShapedRecipeJsonBuilderExtension {
  @Shadow
  public abstract ShapedRecipeJsonBuilder criterion(String string, CriterionConditions arg);

  @Shadow
  public abstract ShapedRecipeJsonBuilder pattern(String patternStr);

  @Shadow
  @Final
  private Item output;
  private boolean bypassesValidation;
  private @Nullable String customRecipeCategory;

  @SuppressWarnings("DataFlowIssue")
  private ShapedRecipeJsonBuilder self() {
    return (ShapedRecipeJsonBuilder) (Object) this;
  }

  @Override
  public ShapedRecipeJsonBuilder patterns(String... patterns) {
    for (String pattern : patterns) {
      this.pattern(pattern);
    }
    return self();
  }

  @Override
  public ShapedRecipeJsonBuilder criterionMethodBridge(String criterionName, CriterionConditions criterionConditions) {
    return criterion(criterionName, criterionConditions);
  }

  @Override
  public ShapedRecipeJsonBuilder setBypassesValidation(boolean bypassesValidation) {
    this.bypassesValidation = bypassesValidation;
    return self();
  }

  @Inject(method = "validate", at = @At("HEAD"), cancellable = true)
  private void bypassValidation(Identifier recipeId, CallbackInfo ci) {
    if (bypassesValidation) {
      ci.cancel();
    }
  }

  @Override
  public ShapedRecipeJsonBuilder setCustomRecipeCategory(@Nullable String recipeCategory) {
    this.customRecipeCategory = recipeCategory;
    return self();
  }

  @Redirect(method = "offerTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getName()Ljava/lang/String;"))
  public String redirectGetName(ItemGroup instance) {
    if (customRecipeCategory != null) {
      return customRecipeCategory;
    } else {
      return instance.getName();
    }
  }

  @ModifyArg(method = "offerTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;Ljava/lang/String;)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;getPath()Ljava/lang/String;")), index = 1)
  public String redundantSlash(String path) {
    if (customRecipeCategory != null && customRecipeCategory.isEmpty()) {
      return StringUtils.replaceOnce(path, "recipes//", "recipes/");
    } else {
      return path;
    }
  }
}
