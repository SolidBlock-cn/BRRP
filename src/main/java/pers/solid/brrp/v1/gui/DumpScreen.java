package pers.solid.brrp.v1.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * The screen to dump a runtime resource pack.
 */
@Environment(EnvType.CLIENT)
public class DumpScreen extends Screen {
  private static final Logger LOGGER = LogManager.getLogger("BRRP/DumpScreen");
  private final Screen parent;
  private final RuntimeResourcePack pack;
  private final int[] dumpStat = new int[3];
  private ButtonWidget dumpTypeButton;
  private @NotNull DumpType dumpType = DumpType.ALL;
  private Text dumpPathText;
  private TextFieldWidget dumpPathTextField;
  private Path dumpPath;
  private String dumpPathString;
  private InvalidPathException invalidPathException;
  private MultilineText dumpPathPreviewText;
  private Text summaryText;
  private Text dumpProgressText;
  private ButtonWidget dumpButton;
  private ButtonWidget interruptButton;
  private ButtonWidget backButton;
  private Thread dumpThread;

  protected DumpScreen(Screen parent, @NotNull RuntimeResourcePack pack) {
    super(new TranslatableText("brrp.dumpScreen.title", pack.getDisplayName()));
    this.parent = parent;
    this.pack = pack;
    dumpPath = RuntimeResourcePack.DEFAULT_OUTPUT.resolve(pack.getId().getNamespace() + "/" + pack.getId().getPath());
    dumpPathString = dumpPath.toString();
  }

  @Override
  protected void init() {
    super.init();
    dumpTypeButton = new ButtonWidget(width / 2 - 100, 30, 200, 20, new TranslatableText("brrp.dumpScreen.dumpType"), button -> dumpType = DumpType.values()[(dumpType.ordinal() + 1) % DumpType.values().length]) {
      @Override
      public Text getMessage() {
        return new TranslatableText("brrp.dumpScreen.dumpType." + dumpType.asString());
      }
    };
    addButton(dumpTypeButton);
    dumpPathText = new TranslatableText("brrp.dumpScreen.dumpPath.title");
    dumpPathTextField = new TextFieldWidget(textRenderer, 20, 85, width - 40, 20, new TranslatableText("brrp.dumpScreen.dumpPath.message"));
    dumpPathTextField.setMaxLength(64);
    dumpPathTextField.setChangedListener(s -> {
      dumpPathString = s;
      MutableText text;
      try {
        dumpPath = FileSystems.getDefault().getPath(s);
        invalidPathException = null;
        text = new TranslatableText("brrp.dumpScreen.dumpToPath", dumpPath.toAbsolutePath().toString()).formatted(Formatting.GREEN);
        dumpPathPreviewText = MultilineText.create(textRenderer, text, width - 20);
      } catch (InvalidPathException exception) {
        invalidPathException = exception;
        text = new TranslatableText("brrp.dumpScreen.dumpInvalidPath", invalidPathException.getMessage()).formatted(Formatting.RED);
        dumpPathPreviewText = MultilineText.create(textRenderer, text, width - 20);
        dumpButton.active = false;
      }
    });
    addButton(dumpPathTextField);

    dumpPathTextField.setText(dumpPathString);


    summaryText = new TranslatableText("brrp.configScreen.summary",
        RRPConfigScreen.singleOrPlural("brrp.configScreen.summary.rootResources.", pack.numberOfRootResources()),
        RRPConfigScreen.singleOrPlural("brrp.configScreen.summary.clientResources.", pack.numberOfClientResources()),
        RRPConfigScreen.singleOrPlural("brrp.configScreen.summary.serverData.", pack.numberOfServerData()));
    dumpProgressText = LiteralText.EMPTY;
    dumpButton = new ButtonWidget(width / 2 - 200, height - 53, 200, 20, new TranslatableText("brrp.dumpScreen.dump"), button -> runDump());
    addButton(dumpButton);
    interruptButton = new ButtonWidget(width / 2, height - 53, 200, 20, new TranslatableText("brrp.dumpScreen.interrupt"), button -> {
      if (dumpThread != null) {
        dumpThread.interrupt();
        try {
          dumpThread.join();
        } catch (InterruptedException e) {
          LOGGER.error("Interrupted dump screen:", e);
        }
      }
    }, (button, matrices, mouseX, mouseY) -> renderOrderedTooltip(matrices, textRenderer.wrapLines(new TranslatableText("brrp.dumpScreen.interrupt.tooltip"), 250), mouseX, mouseY));
    interruptButton.active = false;
    addButton(interruptButton);
    addButton(backButton = new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, ScreenTexts.BACK, button -> onClose()));
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    renderBackground(matrices);
    dumpPathPreviewText.drawCenterWithShadow(matrices, width / 2, 120, 16, 0xffffff);
    textRenderer.draw(matrices, dumpPathText, 20, 65, 0xcccccc);
    drawCenteredText(matrices, textRenderer, summaryText, width / 2, height - 89, 0xffcccccc);
    drawCenteredText(matrices, textRenderer, dumpProgressText, width / 2, height - 73, 0xffcccccc);
    drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
    super.render(matrices, mouseX, mouseY, delta);
    if (dumpPathTextField.isMouseOver(mouseX, mouseY)) {
      renderOrderedTooltip(matrices, textRenderer.wrapLines(new TranslatableText("brrp.dumpScreen.dumpPath.tooltip"), 250), mouseX, mouseY);
    }
  }

  @Override
  public void tick() {
    super.tick();
    dumpButton.active = invalidPathException == null && (dumpThread == null || !dumpThread.isAlive());
    if (dumpThread != null && dumpThread.isAlive()) {
      if (dumpStat[0] == -1) {
        dumpProgressText = new TranslatableText("brrp.dumpScreen.removeExisting");
      } else {
        dumpProgressText = new TranslatableText("brrp.dumpScreen.dumpSummary",
            RRPConfigScreen.singleOrPlural("brrp.configScreen.summary.rootResources.", dumpStat[0]),
            RRPConfigScreen.singleOrPlural("brrp.configScreen.summary.clientResources.", dumpStat[1]),
            RRPConfigScreen.singleOrPlural("brrp.configScreen.summary.serverData.", dumpStat[2]));
      }
    } else if (dumpThread != null) {
      dumpProgressText = ScreenTexts.DONE;
    }
  }

  private void runDump() {
    runDump(false);
  }

  private void runDump(boolean ignoreExistingWarning) {
    try {
      if (!ignoreExistingWarning && Files.isDirectory(dumpPath)) {
        final long size = Files.walk(dumpPath).limit(2001).count();
        if (size > 0 && client != null) {
          dumpProgressText = LiteralText.EMPTY;
          client.openScreen(new ConfirmScreen(value -> {
            if (client != null) {
              client.openScreen(this);
              if (value) {
                runDump(true);
              }
            }
          }, new TranslatableText("brrp.dumpScreen.existing.title"), new TranslatableText("brrp.dumpScreen.existing.message", size > 2000 ? new TranslatableText("brrp.dumpScreen.existing.size", 2000) : size, dumpPath.toAbsolutePath().toString())));
          return;
        }
      }
    } catch (IOException e) {
      LOGGER.error("Checking whether the dump file exists:", e);
    }
    dumpThread = new Thread(() -> {
      interruptButton.active = true;
      backButton.active = false;
      dumpButton.setMessage(new TranslatableText("brrp.dumpScreen.dumping"));
      Arrays.fill(dumpStat, 0);
      pack.dumpInPath(dumpPath, dumpType.resourceType, dumpStat);
      dumpButton.setMessage(new TranslatableText("brrp.dumpScreen.dump"));
      interruptButton.active = false;
      backButton.active = true;
    }, "Dump pack");
    dumpThread.start();
  }

  @Override
  public void onClose() {
    if (dumpThread != null && dumpThread.isAlive()) return;
    if (client != null) {
      client.openScreen(parent);
    }
  }

  @Environment(EnvType.CLIENT)
  public enum DumpType implements StringIdentifiable {
    ALL("all", null),
    CLIENT_RESOURCES("client_resources", ResourceType.CLIENT_RESOURCES),
    SERVER_DATA("server_data", ResourceType.SERVER_DATA);

    public final ResourceType resourceType;
    private final String name;

    DumpType(String name, ResourceType resourceType) {
      this.name = name;
      this.resourceType = resourceType;
    }

    @Override
    public String asString() {
      return name;
    }
  }
}
