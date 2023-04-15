package pers.solid.brrp.v1.recipe.mixin;

import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import pers.solid.brrp.v1.recipe.ShapedRecipeJsonBuilderExtension;

@Mixin(ShapedRecipeJsonBuilder.class)
public abstract class ShapedRecipeJsonBuilderMixin implements ShapedRecipeJsonBuilderExtension {
  @Shadow public abstract ShapedRecipeJsonBuilder criterion(String string, CriterionConditions arg);

  @Shadow public abstract ShapedRecipeJsonBuilder pattern(String patternStr);

  @Shadow @Final private RecipeCategory category;
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

  @ModifyArgs(method = "offerTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/server/recipe/ShapedRecipeJsonBuilder$ShapedRecipeJsonProvider;<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/item/Item;ILjava/lang/String;Lnet/minecraft/recipe/book/CraftingRecipeCategory;Ljava/util/List;Ljava/util/Map;Lnet/minecraft/advancement/Advancement$Builder;Lnet/minecraft/util/Identifier;Z)V"))
  public void modifyOfferTo(Args args) {
    if (customRecipeCategory != null) {
      args.set(8, args.<Identifier>get(0).withPrefixedPath("recipes/" + this.customRecipeCategory + (this.customRecipeCategory.isEmpty() ? "" : "/")));
    }
  }
}
