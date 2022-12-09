package net.devtech.arrp.json.animation;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.JsonSerializable;
import net.fabricmc.api.EnvType;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * An <b>animation file</b> defines how a texture is animated.
 *
 * @see net.minecraft.client.texture.SpriteContents.Animation
 */
@SuppressWarnings("unused")
@PreferredEnvironment(EnvType.CLIENT)
public class JAnimation implements Cloneable, JsonSerializable {
  private Boolean interpolate;
  private Integer width;
  private Integer height;
  private Integer frametime;
  private List<JFrame> frames;

  public JAnimation() {
  }

  /**
   * @since 0.8.0 由 List<Integer> 改为 IntList。
   */
  private IntList defaultFrames;

  /**
   * @deprecated Please directly call the constructor {@link #JAnimation()}.
   */
  @Deprecated
  public static JAnimation animation() {
    return new JAnimation();
  }

  /**
   * @deprecated Please directly call the constructor {@link JFrame#JFrame(int)}.
   */
  @Deprecated
  public static JFrame frame(int index) {
    return new JFrame(index);
  }

  @Override
  public JAnimation clone() {
    try {
      return (JAnimation) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  /**
   * Enable the interpolation of the animation.
   */
  @CanIgnoreReturnValue
  @Contract(value = "-> this", mutates = "this")
  public JAnimation interpolate() {
    this.interpolate = true;
    return this;
  }

  /**
   * Set whether to interpolate the animation.
   *
   * @param interpolate Whether there is interpolation
   */
  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JAnimation interpolate(boolean interpolate) {
    this.interpolate = interpolate;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JAnimation width(int width) {
    this.width = width;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JAnimation height(int height) {
    this.height = height;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JAnimation frameTime(int time) {
    this.frametime = time;
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JAnimation add(int frame) {
    if (this.defaultFrames == null) {
      this.defaultFrames = new IntArrayList();
    }
    this.defaultFrames.add(frame);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JAnimation add(JFrame frame) {
    if (this.frames == null) {
      this.frames = new ArrayList<>();
    }
    this.frames.add(frame);
    return this;
  }

  @CanIgnoreReturnValue
  @Contract(value = "_ -> this", mutates = "this")
  public JAnimation addFrame(int index) {
    return this.add(new JFrame(index));
  }

  @CanIgnoreReturnValue
  @Contract(value = "_, _ -> this", mutates = "this")
  public JAnimation addFrame(int index, int time) {
    return this.add(new JFrame(index, time));
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    {
      JsonObject animation = new JsonObject();
      if (this.interpolate != null) {
        animation.addProperty("interpolate", this.interpolate);
      }
      if (this.width != null) {
        animation.addProperty("width", this.width);
      }
      if (this.height != null) {
        animation.addProperty("height", this.height);
      }
      if (this.frametime != null) {
        animation.addProperty("frametime", this.frametime);
      }
      JsonArray frames = new JsonArray();
      if (this.frames != null) {
        for (JFrame frame : this.frames) {
          frames.add(context.serialize(frame));
        }
      }
      if (this.defaultFrames != null) {
        for (int frame : this.defaultFrames) {
          frames.add(frame);
        }
      }
      if (frames.size() > 0) {
        animation.add("frames", frames);
      }

      object.add("animation", animation);
    }
    return object;
  }

  /**
   * This class is kept for only compatibility.
   */
  @Deprecated
  public static class Serializer implements JsonSerializer<JAnimation> {
    @Override
    public JsonElement serialize(JAnimation jAnimation, Type type, JsonSerializationContext jsonSerializationContext) {
      return jAnimation.serialize(type, jsonSerializationContext);
    }
  }
}
