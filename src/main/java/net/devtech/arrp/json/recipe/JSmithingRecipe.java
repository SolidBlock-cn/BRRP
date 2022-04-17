package net.devtech.arrp.json.recipe;

public class JSmithingRecipe extends JResultRecipe {
  public final JIngredient base;
  public final JIngredient addition;

  public JSmithingRecipe(final JIngredient base, final JIngredient addition, final JResult result) {
    super("smithing", result);

    this.base = base;
    this.addition = addition;
  }

  @Override
  public JSmithingRecipe group(final String group) {
    return (JSmithingRecipe) super.group(group);
  }

  @Override
  public JSmithingRecipe clone() {
    return (JSmithingRecipe) super.clone();
  }
}
