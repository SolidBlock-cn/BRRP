package pers.solid.brrp.v1.mixin;

import net.minecraft.data.client.model.Texture;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Texture.class)
public interface TextureMapAccessor {
  @Accessor
  Map<TextureKey, Identifier> getEntries();
}
