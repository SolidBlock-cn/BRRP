package net.devtech.arrp.json.animation;


import net.devtech.arrp.annotations.PreferredEnvironment;
import net.fabricmc.api.EnvType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * @see net.minecraft.client.texture.SpriteContents.AnimationFrame
 */
@PreferredEnvironment(EnvType.CLIENT)
public class JFrame implements Cloneable {
  public final int index;
  public Integer time;

  public JFrame(int index) {
    this.index = index;
  }

  public JFrame(int index, int time) {
    this(index);
    this.time = time;
  }

  /**
   * Added in BRRP 0.7.0 according to ARRP 0.6.2. Author: Devan Kerman.
   */
  @ApiStatus.AvailableSince("0.7.0")
  @Contract(value = "_ -> this", mutates = "this")
  public JFrame time(int time) {
    this.time = time;
    return this;
  }

  @Override
  public JFrame clone() {
    try {
      return (JFrame) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }
}
