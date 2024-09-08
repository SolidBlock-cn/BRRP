package pers.solid.brrp.v1.mixin;

import com.google.gson.Gson;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JsonUnbakedModel.class)
public interface JsonUnbakedModelAccessor {
  @Accessor
  static Gson getGSON() {
    throw new UnsupportedOperationException();
  }
}
