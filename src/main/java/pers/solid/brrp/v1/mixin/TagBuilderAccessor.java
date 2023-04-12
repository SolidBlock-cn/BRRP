package pers.solid.brrp.v1.mixin;

import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(TagBuilder.class)
public interface TagBuilderAccessor {
  @Accessor
  List<TagEntry> getEntries();
}
