package test;

import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JMultipart;
import net.devtech.arrp.json.blockstate.JWhenProperties;
import net.minecraft.util.Identifier;

public class TestWhen {
  public static void main(String[] args) {
    final JMultipart multipart = new JMultipart(JWhenProperties.of("property", "value"), new JBlockModel(new Identifier("test", "model_id")));
    System.out.println(
        RuntimeResourcePackImpl.GSON.toJson(multipart));
  }
}
