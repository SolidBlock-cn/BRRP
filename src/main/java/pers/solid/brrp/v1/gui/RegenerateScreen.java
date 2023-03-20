package pers.solid.brrp.v1.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

/**
 * The screen to regenerate resources for a runtime resource pack.
 */
@Environment(EnvType.CLIENT)
public class RegenerateScreen extends Screen {
  protected static final Text STATE_REGEN_CLIENT = Text.translatable("brrp.regenerateScreen.state.client");
  protected static final Text STATE_REGEN_SERVER = Text.translatable("brrp.regenerateScreen.state.server");
  protected static final Text STATE_REGEN_ALL = Text.translatable("brrp.regenerateScreen.state.all");
  protected static final Text STATE_IDLE = Text.translatable("brrp.regenerateScreen.state.idle");
  private static final Logger LOGGER = LoggerFactory.getLogger("BRRP/RegenerateScreen");
  private final Screen parent;
  private final RuntimeResourcePack pack;
  protected Thread currentThread;
  private Text stateText;
  private Text summaryText;
  private ButtonWidget onlyRegenClientButton, onlyRegenServerButton, regenButton;
  private ButtonWidget interruptButton;
  private ButtonWidget backButton;

  protected RegenerateScreen(Screen parent, @NotNull RuntimeResourcePack pack) {
    super(Text.translatable("brrp.regenerateScreen.title", pack.getDisplayName()));
    this.parent = parent;
    this.pack = pack;
  }

  @Override
  public Text getNarratedTitle() {
    return super.getNarratedTitle().copy().append(ScreenTexts.LINE_BREAK).append(STATE_IDLE);
  }

  @Override
  protected void init() {
    super.init();
    stateText = STATE_IDLE;
    summaryText = ScreenTexts.EMPTY;
    onlyRegenClientButton = new ButtonWidget(width / 2 - 100, 90, 200, 20, Text.translatable("brrp.regenerateScreen.onlyRegenClient"), button -> regenClientOnly());
    onlyRegenServerButton = new ButtonWidget(width / 2 - 100, 110, 200, 20, Text.translatable("brrp.regenerateScreen.onlyRegenServer"), button -> regenServerOnly());
    regenButton = new ButtonWidget(width / 2 - 100, 130, 200, 20, Text.translatable("brrp.regenerateScreen.regenAll"), button -> regenAll());
    addDrawableChild(onlyRegenClientButton);
    addDrawableChild(onlyRegenServerButton);
    addDrawableChild(regenButton);
    addDrawableChild(interruptButton = new ButtonWidget(width / 2 - 100, height - 48, 200, 20, Text.translatable("brrp.regenerateScreen.interrupt"), button -> {
      if (currentThread != null) {
        if (currentThread.isAlive()) LOGGER.warn("Interrupting thread {}!", currentThread);
        currentThread.interrupt();
        try {
          currentThread.join();
        } catch (InterruptedException e) {
          LOGGER.warn("Waiting the thread to finish:", e);
        }
      }
    }, (button, matrices, mouseX, mouseY) -> renderOrderedTooltip(matrices, textRenderer.wrapLines(Text.translatable("brrp.regenerateScreen.interrupt.tooltip"), 250), mouseX, mouseY)));
    interruptButton.active = false;
    addDrawableChild(backButton = new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, ScreenTexts.BACK, button -> close()));
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    renderBackground(matrices);
    drawCenteredText(matrices, textRenderer, stateText, width / 2, 25, 0xeeee66);
    drawCenteredText(matrices, textRenderer, summaryText, width / 2, 45, 0xffcccccc);
    drawCenteredTextWithShadow(matrices, this.textRenderer, this.title.asOrderedText(), this.width / 2, 8, 0xFFFFFF);
    boolean isIdle = currentThread == null || !currentThread.isAlive();
    onlyRegenClientButton.active = isIdle && pack.hasSidedRegenerationCallback(ResourceType.CLIENT_RESOURCES);
    onlyRegenServerButton.active = isIdle && pack.hasSidedRegenerationCallback(ResourceType.SERVER_DATA);
    regenButton.active = isIdle && pack.hasRegenerationCallback();
    backButton.active = isIdle;
    interruptButton.active = !isIdle;
    if (isIdle) {
      stateText = STATE_IDLE;
    }
    super.render(matrices, mouseX, mouseY, delta);
  }

  private void regenClientOnly() {
    currentThread = new Thread(() -> {
      try {
        pack.regenerateSided(ResourceType.CLIENT_RESOURCES);
      } catch (InterruptedException e) {
        LOGGER.warn("Interrupted:", e);
      }
    }, "Regenerate client resources");
    stateText = STATE_REGEN_CLIENT;
    currentThread.start();
  }

  private void regenServerOnly() {
    currentThread = new Thread(() -> {
      try {
        pack.regenerateSided(ResourceType.SERVER_DATA);
      } catch (InterruptedException e) {
        LOGGER.warn("Interrupted:", e);
      }
    }, "Regenerate server data");
    stateText = STATE_REGEN_SERVER;
    currentThread.start();
  }

  private void regenAll() {
    currentThread = new Thread(() -> {
      try {
        pack.regenerate();
      } catch (InterruptedException e) {
        LOGGER.warn("Interrupted:", e);
      }
    }, "Regenerate all resources");
    stateText = STATE_REGEN_ALL;
    currentThread.start();
  }

  @Override
  public void tick() {
    super.tick();
    summaryText = Text.translatable("brrp.configScreen.summary",
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.rootResources.", pack.numberOfRootResources()),
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.clientResources.", pack.numberOfClientResources()),
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.serverData.", pack.numberOfServerData())).styled(style -> style.withColor(0xffcccccc));
  }

  @Override
  public void close() {
    if (currentThread != null && currentThread.isAlive()) return;
    if (client != null) {
      client.setScreen(parent);
    }
  }
}
