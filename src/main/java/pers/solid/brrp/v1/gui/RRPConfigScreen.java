package pers.solid.brrp.v1.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import pers.solid.brrp.v1.PlatformBridge;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The configuration screen for the BRRP mod.
 */
@Environment(EnvType.CLIENT)
public class RRPConfigScreen extends Screen {
  protected final List<ResourcePack> beforeVanillaPacks, beforeUserPacks, afterVanillaPacks;
  private final Screen parent;
  protected PackListWidget beforeVanillaListWidget, beforeUserListWidget, afterVanillaListWidget;

  protected ButtonWidget beforeVanillaTabButton;
  protected ButtonWidget beforeUserTabButton;
  protected ButtonWidget afterVanillaTabButton;
  protected int currentTab = 0;
  protected ButtonWidget backButton;

  public RRPConfigScreen(Screen parent) {
    super(new TranslatableText("brrp.configScreen.title"));
    this.parent = parent;
    beforeVanillaPacks = new ArrayList<>();
    beforeUserPacks = new ArrayList<>();
    afterVanillaPacks = new ArrayList<>();
    PlatformBridge.getInstance().postBefore(null, beforeVanillaPacks);
    PlatformBridge.getInstance().postBeforeUser(null, beforeUserPacks);
    PlatformBridge.getInstance().postAfter(null, afterVanillaPacks);
  }

  @Override
  public void onClose() {
    if (client != null) {
      client.openScreen(parent);
    }
  }

  protected void setTab(int tab) {
    beforeVanillaTabButton.active = tab != 0;
    beforeUserTabButton.active = tab != 1;
    afterVanillaTabButton.active = tab != 2;
    if (getFocused() instanceof PackListWidget) {
      PackListWidget packListWidget = (PackListWidget) getFocused();
      setFocused(null);
      packListWidget.setFocused(null);
    }
    children.remove(beforeVanillaListWidget);
    children.remove(beforeUserListWidget);
    children.remove(afterVanillaListWidget);
    children.remove(backButton);  // to ensure it is the last child

    switch (tab) {
      case 0:
        addChild(beforeVanillaListWidget);
        break;
      case 1:
        addChild(beforeUserListWidget);
        break;
      case 2:
        addChild(afterVanillaListWidget);
        break;
    }
    addButton(backButton);
    currentTab = tab;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_TAB && hasControlDown()) {
      setTab((currentTab + 3 + (hasShiftDown() ? -1 : 1)) % 3);
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  protected void init() {
    addButton(beforeVanillaTabButton = new ButtonWidget(width / 2 - 120, 20, 80, 20, new TranslatableText("brrp.configScreen.tab.beforeVanilla", beforeVanillaPacks.size()), button -> setTab(0)));//.narrationSupplier(textSupplier -> new TranslatableText("brrp.configScreen.tab.description", textSupplier.get(), 1, 3)).dimensions().build());
    addButton(beforeUserTabButton = new ButtonWidget(width / 2 - 40, 20, 80, 20, new TranslatableText("brrp.configScreen.tab.beforeUser", beforeUserPacks.size()), button -> setTab(1)));//.narrationSupplier(textSupplier -> new TranslatableText("brrp.configScreen.tab.description", textSupplier.get(), 2, 3)).dimensions().build());
    addButton(afterVanillaTabButton = new ButtonWidget(width / 2 + 40, 20, 80, 20, new TranslatableText("brrp.configScreen.tab.afterVanilla", afterVanillaPacks.size()), button -> setTab(2)));//.narrationSupplier(textSupplier -> new TranslatableText("brrp.configScreen.tab.description", textSupplier.get(), 3, 3)).dimensions().build());

    beforeVanillaListWidget = new PackListWidget(beforeVanillaPacks);
    beforeUserListWidget = new PackListWidget(beforeUserPacks);
    afterVanillaListWidget = new PackListWidget(afterVanillaPacks);
    addButton(backButton = new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, ScreenTexts.BACK, button -> onClose()));

    setTab(currentTab);
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    this.renderBackgroundTexture(0);

    switch (currentTab) {
      case 0:
        beforeVanillaListWidget.render(matrices, mouseX, mouseY, delta);
        break;
      case 1:
        beforeUserListWidget.render(matrices, mouseX, mouseY, delta);
        break;
      case 2:
        afterVanillaListWidget.render(matrices, mouseX, mouseY, delta);
        break;
    }
    drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
    super.render(matrices, mouseX, mouseY, delta);
  }

  @Environment(EnvType.CLIENT)
  public class PackListWidget extends ElementListWidget<PackListWidget.Entry> {
    private final List<ResourcePack> packList;
    private final @Nullable Text nothingHereText;

    public PackListWidget(List<ResourcePack> packList) {
      super(RRPConfigScreen.this.client, RRPConfigScreen.this.width, RRPConfigScreen.this.height, 40, RRPConfigScreen.this.height - 37, 36);
      this.packList = packList;
      for (ResourcePack resourcePack : packList) {
        addEntry(new Entry(resourcePack));
      }

      if (packList.isEmpty()) {
        this.nothingHereText = new TranslatableText("brrp.configScreen.nothing");
      } else {
        this.nothingHereText = null;
      }
    }

    @Override
    public int getRowWidth() {
      return width - 14;
    }

    @Override
    protected int getScrollbarPositionX() {
      return width - 6;
    }

    @Override
    protected boolean isSelectedEntry(int index) {
      return Objects.equals(getSelected(), children().get(index));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
      super.render(matrices, mouseX, mouseY, delta);
      if (nothingHereText != null) {
        drawCenteredText(matrices, textRenderer, nothingHereText, width / 2, height / 2 - 8, 0xffffff);
      }
    }

    @Override
    protected boolean isFocused() {
      return RRPConfigScreen.this.getFocused() == this;
    }

    @Override
    public void setFocused(@Nullable Element focused) {
      if (getFocused() != null && getFocused() != focused) {
        getFocused().setFocused(null);
      }
      super.setFocused(focused);
    }

    @Environment(EnvType.CLIENT)
    public class Entry extends ElementListWidget.Entry<Entry> {
      private final ButtonWidget regenerateButton;
      private final ButtonWidget dumpButton;
      private final ResourcePack resourcePack;

      private final Text titleText;
      private final Text description;
      private long lastHoveredTime;

      public Entry(ResourcePack resourcePack) {
        this.resourcePack = resourcePack;
        this.regenerateButton = new ButtonWidget(0, 0, 60, 20, new TranslatableText("brrp.configScreen.regenerate"), button -> {
          if (resourcePack instanceof RuntimeResourcePack) {
            RuntimeResourcePack runtimeResourcePack = (RuntimeResourcePack) resourcePack;
            client.openScreen(new RegenerateScreen(RRPConfigScreen.this, runtimeResourcePack));
          }
        }, (button, matrices, mouseX, mouseY) -> {
          if (button.isMouseOver(mouseX, mouseY)) renderOrderedTooltip(matrices, textRenderer.wrapLines(new TranslatableText("brrp.configScreen.regenerate.tooltip").append(resourcePack instanceof RuntimeResourcePack && ((RuntimeResourcePack) resourcePack).hasRegenerationCallback() ? new LiteralText("") : new LiteralText("\n").formatted(Formatting.RED).append(new TranslatableText("brrp.configScreen.regenerate.notSupported"))), 250), mouseX, mouseY);
        });
        this.dumpButton = new ButtonWidget(0, 0, 60, 20, new TranslatableText("brrp.configScreen.dump"), button -> {
          if (resourcePack instanceof RuntimeResourcePack) {
            RuntimeResourcePack runtimeResourcePack = (RuntimeResourcePack) resourcePack;
            client.openScreen(new DumpScreen(RRPConfigScreen.this, runtimeResourcePack));
          }
        }, (button, matrices, mouseX, mouseY) -> {
          if (button.isMouseOver(mouseX, mouseY)) renderOrderedTooltip(matrices, textRenderer.wrapLines(new TranslatableText("brrp.configScreen.dump.tooltip"), 250), mouseX, mouseY);
        });
        List<Text> descriptionList = new ArrayList<>();
        if (resourcePack instanceof RuntimeResourcePack) {
          RuntimeResourcePack runtimeResourcePack = (RuntimeResourcePack) resourcePack;
          titleText = runtimeResourcePack.getDisplayName();
          final Text description = runtimeResourcePack.getDescription();
          if (description != null) {
            descriptionList.add(new LiteralText("").append(description));
          }
          descriptionList.add(new TranslatableText("brrp.configScreen.summary",
              singleOrPlural("brrp.configScreen.summary.rootResources.", runtimeResourcePack.numberOfRootResources()),
              singleOrPlural("brrp.configScreen.summary.clientResources.", runtimeResourcePack.numberOfClientResources()),
              singleOrPlural("brrp.configScreen.summary.serverData.", runtimeResourcePack.numberOfServerData())));
        } else {
          titleText = new LiteralText(resourcePack.getName());
        }
        @NotNull Set<String> namespaces = Sets.newLinkedHashSet(Iterables.concat(resourcePack.getNamespaces(ResourceType.CLIENT_RESOURCES), resourcePack.getNamespaces(ResourceType.SERVER_DATA)));
        descriptionList.add(new TranslatableText("brrp.configScreen.namespaces", namespaces.isEmpty() ? new TranslatableText("gui.none") : new LiteralText(String.join(", ", namespaces)).styled(style -> style.withColor(TextColor.fromRgb(0xddc4aa)))).styled(style -> style.withColor(TextColor.fromRgb(0x909090))));
        this.description = Texts.join(descriptionList, text -> text.shallowCopy().append("\n"));
      }

      @Override
      public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (isMouseOver(mouseX, mouseY)) {
          lastHoveredTime = Util.getMeasuringTimeMs();
        }
        drawTextWithShadow(matrices, textRenderer, titleText, x + 5, y + 2, 0xFFFFFF);
        MultilineText.create(textRenderer, description, width - 151, 2).draw(matrices, x + 5, y + 12, 10, 0xaaaaaa);

        MutableText tooltipContent = new LiteralText("").append(titleText).append(new LiteralText("\n"));
        if (resourcePack instanceof RuntimeResourcePack) {
          RuntimeResourcePack runtimeResourcePack = (RuntimeResourcePack) resourcePack;
          tooltipContent.append(new LiteralText(runtimeResourcePack.getId().toString()).formatted(Formatting.GRAY)).append(new LiteralText("\n"));
        }
        tooltipContent.append(description.shallowCopy().formatted(Formatting.GRAY));
        if (isMouseOver(mouseX, mouseY) && !regenerateButton.isHovered() && !dumpButton.isHovered()) {
          renderOrderedTooltip(matrices, textRenderer.wrapLines(tooltipContent, 250), mouseX, mouseY);
        }

        regenerateButton.x = width - 136;
        regenerateButton.y = y + entryHeight / 2 - regenerateButton.getHeight() / 2;
        regenerateButton.active = resourcePack instanceof RuntimeResourcePack && ((RuntimeResourcePack) resourcePack).hasRegenerationCallback();
        regenerateButton.render(matrices, mouseX, mouseY, tickDelta);
        regenerateButton.active = true;
        dumpButton.x = width - 76;
        dumpButton.y = y + entryHeight / 2 - dumpButton.getHeight() / 2;
        dumpButton.render(matrices, mouseX, mouseY, tickDelta);
      }

      @Override
      public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
          setSelected(this);
          this.setFocused(null);
          PackListWidget.this.setFocused(this);
          return false;
        }
        return true;
      }

      @Override
      public void setFocused(@Nullable Element focused) {
        super.setFocused(focused);
        PackListWidget.this.setFocused(this);
        PackListWidget.this.setSelected(this);
      }

      @Override
      public List<? extends Element> children() {
        return ImmutableList.of(regenerateButton, dumpButton);
      }
    }
  }

  static MutableText singleOrPlural(String prefix, int number) {
    return new TranslatableText(prefix + (number == 1 ? "single" : "plural"), number);
  }
}
