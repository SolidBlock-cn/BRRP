package pers.solid.brrp.v1.recipe.mixin;

import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import pers.solid.brrp.v1.recipe.ShapelessRecipeJsonBuilderExtension;

@Mixin(ShapelessRecipeJsonBuilder.class)
public abstract class ShapelessRecipeJsonBuilderMixin implements ShapelessRecipeJsonBuilderExtension {
  @Shadow
  public abstract ShapelessRecipeJsonBuilder criterion(String string, CriterionConditions criterionConditions);

  private boolean bypassesValidation;
  private @Nullable String customRecipeCategory;

  private ShapelessRecipeJsonBuilder self() {
    return (ShapelessRecipeJsonBuilder) (Object) this;
  }

  @Override
  public ShapelessRecipeJsonBuilder criterionMethodBridge(String criterionName, CriterionConditions criterionConditions) {
    return criterion(criterionName, criterionConditions);
  }

  @Override
  public ShapelessRecipeJsonBuilder setBypassesValidation(boolean bypassesValidation) {
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
  public ShapelessRecipeJsonBuilder setCustomRecipeCategory(@Nullable String recipeCategory) {
    this.customRecipeCategory = recipeCategory;
    return self();
  }

  @ModifyArgs(method = "offerTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/server/recipe/ShapelessRecipeJsonBuilder$ShapelessRecipeJsonProvider;<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/item/Item;ILjava/lang/String;Ljava/util/List;Lnet/minecraft/advancement/Advancement$Builder;Lnet/minecraft/util/Identifier;)V"))
  public void modifyOfferTo(Args args) {
    if (customRecipeCategory != null) {
      final Identifier recipeId = args.get(0);
      args.set(6, new Identifier(recipeId.getNamespace(), "recipes/" + this.customRecipeCategory + (this.customRecipeCategory.isEmpty() ? "" : "/") + recipeId.getPath()));
    }
  }

  @Redirect(method = "offerTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getGroup()Lnet/minecraft/item/ItemGroup;"))
  public ItemGroup redirectGetName(Item instance) {
    if (instance.getGroup() == null && customRecipeCategory != null) {
      return ItemGroup.BUILDING_BLOCKS;  // avoid NPEs
    } else {
      return instance.getGroup();
    }
  }
}
