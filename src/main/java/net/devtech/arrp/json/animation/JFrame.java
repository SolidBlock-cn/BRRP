package net.devtech.arrp.json.animation;


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

  @Override
  public JFrame clone() {
    try {
      return (JFrame) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }
}
