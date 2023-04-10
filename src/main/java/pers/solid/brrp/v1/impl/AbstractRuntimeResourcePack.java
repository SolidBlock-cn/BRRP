package pers.solid.brrp.v1.impl;

import com.mojang.bridge.game.PackType;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.util.FailableRunnable;

public abstract class AbstractRuntimeResourcePack implements RuntimeResourcePack {
  private final Identifier id;
  public int packVersion = SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE);
  protected boolean allowsDuplicateResource = false;
  protected FailableRunnable<InterruptedException> regenerationCallback;
  protected FailableRunnable<InterruptedException> clientResourceRegenerationCallback, serverDataRegenerationCallback;
  private Text displayName;
  private Text description;

  protected AbstractRuntimeResourcePack(Identifier id) {
    this.id = id;
  }

  @Override
  public boolean hasRegenerationCallback() {
    return ObjectUtils.anyNotNull(regenerationCallback, clientResourceRegenerationCallback, serverDataRegenerationCallback);
  }

  @Override
  public boolean hasSidedRegenerationCallback(@Nullable ResourceType resourceType) {
    if (resourceType == null) {
      return regenerationCallback != null;
    } else {
      return switch (resourceType) {
        case CLIENT_RESOURCES -> clientResourceRegenerationCallback != null;
        case SERVER_DATA -> serverDataRegenerationCallback != null;
      };
    }
  }

  @Override
  public void setRegenerationCallback(FailableRunnable<InterruptedException> regenerationCallback) {
    this.regenerationCallback = regenerationCallback;
  }

  @Override
  public void setSidedRegenerationCallback(@NotNull ResourceType resourceType, FailableRunnable<InterruptedException> regenerationCallback) {
    switch (resourceType) {
      case SERVER_DATA -> serverDataRegenerationCallback = regenerationCallback;
      case CLIENT_RESOURCES -> clientResourceRegenerationCallback = regenerationCallback;
    }
  }

  @Override
  public void regenerate() throws InterruptedException {
    if (regenerationCallback != null) {
      regenerationCallback.run();
    } else {
      if (clientResourceRegenerationCallback != null) clientResourceRegenerationCallback.run();
      if (serverDataRegenerationCallback != null) serverDataRegenerationCallback.run();
    }
  }

  @Override
  public void regenerateSided(@NotNull ResourceType resourceType) throws InterruptedException {
    switch (resourceType) {
      case CLIENT_RESOURCES -> clientResourceRegenerationCallback.run();
      case SERVER_DATA -> serverDataRegenerationCallback.run();
    }
  }

  @Override
  public Identifier getId() {
    return this.id;
  }

  @Override
  public int getPackVersion() {
    return packVersion;
  }

  @Override
  public void setPackVersion(int packVersion) {
    this.packVersion = packVersion;
  }

  @Override
  public void setAllowsDuplicateResource(boolean b) {
    allowsDuplicateResource = b;
  }


  @Override
  public Text getDisplayName() {
    if (displayName != null) return displayName;
    return RuntimeResourcePack.super.getDisplayName();
  }

  @Override
  public void setDisplayName(Text name) {
    this.displayName = name;
  }

  @Override
  public @Nullable Text getDescription() {
    return description;
  }

  @Override
  public void setDescription(Text description) {
    this.description = description;
  }
}
