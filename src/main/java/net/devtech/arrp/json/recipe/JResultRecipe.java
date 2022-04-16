package net.devtech.arrp.json.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;

public abstract class JResultRecipe extends JRecipe {
  public final JResult result;

  protected JResultRecipe(final String type, final JResult result) {
    super(type);
    this.result = result;
  }

  protected JResultRecipe(String type, String result) {
    this(type, new JResult(result));
  }

  protected JResultRecipe(String type, Identifier result) {
    this(type, new JResult(result));
  }

  protected JResultRecipe(String type, Item result) {
    this(type, new JResult(result));
  }

  protected JResultRecipe(String type, ItemConvertible result) {
    this(type, new JResult(result));
  }

  @Contract("_ -> this")
  @CanIgnoreReturnValue
  public JResultRecipe resultCount(int count) {
    this.result.count = count;
    return this;
  }

  @Override
  public JResultRecipe group(final String group) {
    return (JResultRecipe) super.group(group);
  }

  @Override
  public JResultRecipe clone() {
    return (JResultRecipe) super.clone();
  }
}
