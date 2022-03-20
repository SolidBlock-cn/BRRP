package net.devtech.arrp.json.loot;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;

import java.lang.reflect.Type;

public interface ValueProvider {
  static ValueProvider of(int value) {
    return new Of(value);
  }

  static ValueProvider of(float value) {
    return new Of(value);
  }

  static ValueProvider constant(int value) {
    return new Constant(value);
  }

  static ValueProvider constant(float value) {
    return new Constant(value);
  }

  static ValueProvider uniform(int min, int max) {
    return new Uniform(of(min), of(max));
  }

  static ValueProvider uniform(float min, float max) {
    return new Uniform(of(min), of(max));
  }

  static ValueProvider uniform(ValueProvider min, ValueProvider max) {
    return new Uniform(min, max);
  }

  static ValueProvider binomial(int n, float p) {
    return new Binomial(of(n), of(p));
  }

  static ValueProvider binomial(ValueProvider n, ValueProvider p) {
    return new Binomial(n, p);
  }

  static Score score(String target) {
    return new Score(ScoreNameProvider.of(target));
  }

  static Score score(String target, String score) {
    return new Score(ScoreNameProvider.of(target), score);
  }

  static Score score(ScoreNameProvider target) {
    return new Score(target);
  }

  static Score score(ScoreNameProvider target, String score) {
    return new Score(target, score);
  }

  class Of implements JsonSerializable, ValueProvider {
    public final Number value;

    private Of(Number value) {
      this.value = value;
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(value);
    }
  }

  class Constant implements ValueProvider {
    public final Number value;
    public final String type = "constant";

    private Constant(Number value) {
      this.value = value;
    }
  }

  class Uniform implements ValueProvider {
    public final ValueProvider min;
    public final ValueProvider max;
    public final String type = "uniform";

    private Uniform(ValueProvider min, ValueProvider max) {
      this.min = min;
      this.max = max;
    }
  }

  class Binomial implements ValueProvider {
    public final ValueProvider n;
    public final ValueProvider p;
    public final String type = "binomial";

    private Binomial(ValueProvider n, ValueProvider p) {
      this.n = n;
      this.p = p;
    }
  }

  class Score implements ValueProvider {
    public final ScoreNameProvider target;
    public final String score;
    public Float scale;

    private Score(ScoreNameProvider target, String score) {
      this.target = target;
      this.score = score;
    }

    public Score(ScoreNameProvider target) {
      this(target, null);
    }

    @CanIgnoreReturnValue
    public Score scale(float scale) {
      this.scale = scale;
      return this;
    }
  }
}
