package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.devtech.arrp.api.JSONSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.loot.JCondition;
import net.devtech.arrp.json.models.JModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.devtech.arrp.json.blockstate.JState.*;
import static net.devtech.arrp.json.loot.JLootTable.*;
import static net.devtech.arrp.json.models.JModel.model;
import static net.devtech.arrp.json.models.JModel.*;

public class RRPPreTest {
  public static void main(String[] args) {
    JState iron_block = state(variant(JState.model("block/iron_block")));
    JState oak_fence = state(multipart(JState.model("block/oak_fence_post")),
        multipart(JState.model("block/oak_fence_side").uvlock()).when(when().add("north", "true")),
        multipart(JState.model("block/oak_fence_side").y(90).uvlock()).when(when().add("east", "true")),
        multipart(JState.model("block/oak_fence_side").y(180).uvlock()).when(when().add("south", "true")),
        multipart(JState.model("block/oak_fence_side").y(270).uvlock()).when(when().add("west", "true")));

    JModel model = model().textures(textures().var("all", "block/bamboo_stalk").particle("block/bamboo_stalk"))
        .element(element().from(7, 0, 7)
            .to(9, 16, 9)
            .faces(faces().down(face("all").cullface(Direction.DOWN).uv(13, 4, 15, 6))
                .up(face("all").cullface(Direction.UP).uv(13, 0, 15, 2))
                .north(face("all").uv(9, 0, 11, 16))
                .south(face("all").uv(9, 0, 11, 16))
                .west(face("all").uv(9, 0, 11, 16))
                .east(face("all").uv(9, 0, 11, 16))));

    Gson gson = new GsonBuilder().registerTypeAdapter(JSONSerializable.class, JSONSerializable.SERIALIZER)
        .setPrettyPrinting()
        .create();

    JLang lang = new JLang().allPotionOf(new Identifier("mod_id", "potion_id"), "Example");

    System.out.println(RuntimeResourcePackImpl.GSON.toJson(loot("minecraft:block").pool(pool().rolls(1)
        .entry(entry().type("minecraft:item").name("minecraft:diamond"))
        .condition(new JCondition("minecraft:survives_explosion")))));
    //System.out.println(gson.toJson(iron_block));
    //System.out.println(gson.toJson(oak_fence));
    System.out.println(gson.toJson(model));

    System.out.println(gson.toJson(lang));
  }
}
