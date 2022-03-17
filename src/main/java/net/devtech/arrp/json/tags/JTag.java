package net.devtech.arrp.json.tags;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class JTag {
  public Boolean replace;
  public List<String> values = new ArrayList<>();

  public JTag() {
  }

  public static JTag replacingTag() {
    return tag().replace();
  }

  /**
   * whether or not this tag should override all super tags
   */
  public JTag replace() {
    this.replace = true;
    return this;
  }

  /**
   * @deprecated Please directly use the constructor method {@link #JTag()}.
   */
  @Deprecated
  public static JTag tag() {
    return new JTag();
  }

  /**
   * @implNote Usually you should add the identifier by calling {@link #add(Identifier)} or {@link #tag(Identifier)}.
   */
  public JTag add(String identifier) {
    this.values.add(identifier);
    return this;
  }

  /**
   * add a normal item to the tag
   */
  public JTag add(Identifier identifier) {
    this.values.add(identifier.toString());
    return this;
  }

  /**
   * add a tag to the tag
   */
  public JTag tag(Identifier tag) {
    this.values.add('#' + tag.getNamespace() + ':' + tag.getPath());
    return this;
  }

  @Override
  public JTag clone() {
    try {
      return (JTag) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }
}
