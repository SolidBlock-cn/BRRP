package pers.solid.brrp.v1.mixin;

import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TextureMap.class)
public interface TextureMapAccessor {
  @Accessor
  Map<TextureKey, Identifier> getEntries();
}
