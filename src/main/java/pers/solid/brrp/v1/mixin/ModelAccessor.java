package pers.solid.brrp.v1.mixin;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;
import java.util.Set;

@Mixin(Model.class)
public interface ModelAccessor {
  @Accessor
  Optional<Identifier> getParent();

  @Accessor
  Optional<String> getVariant();

  @Accessor
  Set<TextureKey> getRequiredTextures();
}
