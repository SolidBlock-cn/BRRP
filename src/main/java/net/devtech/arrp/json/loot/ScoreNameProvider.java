package net.devtech.arrp.json.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;

import java.lang.reflect.Type;

public interface ScoreNameProvider {
  static ScoreNameProvider of(String value) {
    return new Of(value);
  }

  static ScoreNameProvider fixed(String name) {
    return new Fixed(name);
  }

  static ScoreNameProvider context(String target) {
    return new Context(target);
  }

  class Of implements ScoreNameProvider, JsonSerializable {
    public final String value;

    private Of(String value) {
      this.value = value;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(value);
    }
  }

  class Fixed implements ScoreNameProvider {
    public final String type = "fixed";
    public final String name;

    private Fixed(String name) {
      this.name = name;
    }
  }

  class Context implements ScoreNameProvider {
    public final String type = "context";
    public final String target;

    private Context(String target) {
      this.target = target;
    }
  }
}
