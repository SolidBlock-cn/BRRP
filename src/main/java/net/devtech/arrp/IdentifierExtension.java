package net.devtech.arrp;

import net.minecraft.util.Identifier;

public interface IdentifierExtension {
  Identifier append(String s);

  Identifier prepend(String s);
}
