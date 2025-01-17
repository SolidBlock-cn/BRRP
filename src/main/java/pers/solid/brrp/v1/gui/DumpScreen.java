package pers.solid.brrp.v1.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

/**
 * The screen to dump a runtime resource pack.
 */
@Environment(EnvType.CLIENT)
public class DumpScreen extends Screen {
  private static final Logger LOGGER = LoggerFactory.getLogger("BRRP/DumpScreen");
  private final Screen parent;
  private final RuntimeResourcePack pack;
  private final int[] dumpStat = new int[3];
  private CyclingButtonWidget<DumpType> dumpTypeButton;
  @ApiStatus.AvailableSince("1.1.0")
  private CyclingButtonWidget<Boolean> dumpAsZipButton;
  private @NotNull DumpType dumpType = DumpType.ALL;
  private TextWidget dumpPathText;
  private TextFieldWidget dumpPathTextField;
  private boolean dumpAsZip = false;
  private Path dumpPath;
  private String dumpPathString;
  private InvalidPathException invalidPathException;
  private MultilineTextWidget dumpPathPreviewText;
  private TextWidget summaryText;
  private TextWidget dumpProgressText;
  private ButtonWidget dumpButton;
  @ApiStatus.AvailableSince("1.1.0")
  private ButtonWidget openButton;
  private ButtonWidget interruptButton;
  private ButtonWidget backButton;
  private Thread dumpThread;

  protected DumpScreen(Screen parent, @NotNull RuntimeResourcePack pack) {
    super(Text.translatable("brrp.dumpScreen.title", pack.getDisplayName()));
    this.parent = parent;
    this.pack = pack;
    dumpPath = RuntimeResourcePack.DEFAULT_OUTPUT.resolve(pack.getId().replace(':', '/'));
    dumpPathString = dumpPath.toString();
  }

  @Override
  protected void init() {
    super.init();
    dumpTypeButton = new CyclingButtonWidget.Builder<DumpType>(dumpType -> Text.translatable("brrp.dumpScreen.dumpType." + dumpType.asString()))
        .values(DumpType.values())
        .initially(dumpType)
        .tooltip(value -> Tooltip.of(Text.translatable("brrp.dumpScreen.dumpType.tooltip." + value.asString())))
        .build(width / 2 - 200, 30, 200, 20, Text.translatable("brrp.dumpScreen.dumpType"), (button, value) -> dumpType = value);
    addDrawableChild(dumpTypeButton);
    dumpAsZipButton = CyclingButtonWidget.onOffBuilder()
        .initially(dumpAsZip)
        .tooltip(value -> Tooltip.of(Text.translatable("brrp.dumpScreen.dumpAsZip.tooltip." + value)))
        .build(width / 2, 30, 200, 20, Text.translatable("brrp.dumpScreen.dumpAsZip"), (button, value) -> {
          if (dumpAsZip != value) {
            dumpAsZip = value;
            // 修改后，更新显示的导出路径的信息
            this.dumpPathTextField.setCursor(this.dumpPathTextField.getCursor(), false);
          }
        });
    addDrawableChild(dumpAsZipButton);
    dumpPathText = new TextWidget(width / 2 - 100, 65, 200, 20, Text.translatable("brrp.dumpScreen.dumpPath.title"), textRenderer).setTextColor(0xcccccc).alignCenter();
    addDrawableChild(dumpPathText);
    dumpPathTextField = new TextFieldWidget(textRenderer, 20, 85, width - 40, 20, Text.translatable("brrp.dumpScreen.dumpPath.message"));
    dumpPathTextField.setTooltip(Tooltip.of(Text.translatable("brrp.dumpScreen.dumpPath.tooltip")));
    dumpPathTextField.setMaxLength(64);
    dumpPathTextField.setChangedListener(s -> {
      dumpPathString = s;
      try {
        dumpPath = getDumpPath(s);
        invalidPathException = null;
        dumpPathPreviewText.setMessage(Text.translatable("brrp.dumpScreen.dumpToPath", dumpPath.toAbsolutePath().toString()).formatted(Formatting.GREEN));
      } catch (InvalidPathException exception) {
        invalidPathException = exception;
        dumpPathPreviewText.setMessage(Text.translatable("brrp.dumpScreen.dumpInvalidPath", invalidPathException.getMessage()).formatted(Formatting.RED));
        dumpButton.active = false;
      }
      dumpPathPreviewText.setX(width / 2 - dumpPathPreviewText.getWidth() / 2);
      dumpPathTextField.setTooltip(Tooltip.of(Text.translatable("brrp.dumpScreen.dumpPath.tooltip"), dumpPathPreviewText.getMessage().copy().append(ScreenTexts.LINE_BREAK).append(Text.translatable("brrp.dumpScreen.dumpPath.tooltip"))));
    });
    addDrawableChild(dumpPathTextField);

    dumpPathPreviewText = new MultilineTextWidget(20, 120, ScreenTexts.EMPTY, textRenderer).setMaxWidth(width - 40).setCentered(true);
    dumpPathTextField.setText(dumpPathString);
    addDrawableChild(dumpPathPreviewText);


    summaryText = new TextWidget(20, height - 89, width - 40, 20, Text.translatable("brrp.configScreen.summary",
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.rootResources.", pack.numberOfRootResources()),
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.clientResources.", pack.numberOfClientResources()),
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.serverData.", pack.numberOfServerData())), textRenderer).alignCenter().setTextColor(0xffcccccc);
    addDrawableChild(summaryText);
    dumpProgressText = new TextWidget(20, height - 73, width - 40, 20, ScreenTexts.EMPTY, textRenderer).alignCenter();
    addDrawableChild(dumpProgressText);
    dumpButton = ButtonWidget.builder(Text.translatable("brrp.dumpScreen.dump"), button -> runDump()).dimensions(width / 2 - 210, height - 53, 140, 20).build();
    addDrawableChild(dumpButton);
    openButton = ButtonWidget.builder(Text.translatable("brrp.dumpScreen.open"), button -> Util.getOperatingSystem().open(dumpAsZip ? dumpPath.getParent() : dumpPath)).dimensions(width / 2 - 70, height - 53, 140, 20).tooltip(Tooltip.of(Text.translatable("brrp.dumpScreen.open.tooltip"))).build();
    addDrawableChild(openButton);
    interruptButton = ButtonWidget.builder(Text.translatable("brrp.dumpScreen.interrupt"), button -> {
      if (dumpThread != null) {
        dumpThread.interrupt();
        try {
          dumpThread.join();
        } catch (InterruptedException e) {
          LOGGER.error("Interrupted dump screen:", e);
        }
      }
    }).tooltip(Tooltip.of(Text.translatable("brrp.dumpScreen.interrupt.tooltip"))).dimensions(width / 2 + 70, height - 53, 140, 20).build();
    interruptButton.active = false;
    addDrawableChild(interruptButton);
    addDrawableChild(backButton = ButtonWidget.builder(ScreenTexts.BACK, button -> close()).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());
  }

  /**
   * @param s The dump path as string from the text box
   * @return The dump path of the directory or zip.
   * @throws InvalidPathException if the path is invalid.
   */
  private @NotNull Path getDumpPath(String s) {
    if (this.dumpAsZip) {
      s = StringUtils.appendIfMissingIgnoreCase(s, ".zip");
    }
    return Path.of(s);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
  }

  @Override
  public void tick() {
    super.tick();
    dumpButton.active = invalidPathException == null && (dumpThread == null || !dumpThread.isAlive());
    if (dumpThread != null && dumpThread.isAlive()) {
      if (dumpStat[0] == -1) {
        dumpProgressText.setMessage(Text.translatable("brrp.dumpScreen.removeExisting"));
      } else {
        dumpProgressText.setMessage(Text.translatable("brrp.dumpScreen.dumpSummary",
            RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.rootResources.", dumpStat[0]),
            RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.clientResources.", dumpStat[1]),
            RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.serverData.", dumpStat[2])));
      }
    } else if (dumpThread != null) {
      dumpProgressText.setMessage(ScreenTexts.DONE);
    }
  }

  private void runDump() {
    runDump(false);
  }

  private void runDump(boolean ignoreExistingWarning) {
    try {
      final boolean exists;
      if (dumpAsZip) {
        exists = !ignoreExistingWarning && Files.exists(this.dumpPath);
      } else {
        exists = !ignoreExistingWarning && Files.isDirectory(this.dumpPath) && !PathUtils.isEmptyDirectory(this.dumpPath);
      }
      if (exists) {
        if (dumpAsZip) {
          if (client != null) {
            dumpProgressText.setMessage(ScreenTexts.EMPTY);
            client.setScreen(new ConfirmScreen(value -> {
              if (client != null) {
                client.setScreen(this);
                if (value) {
                  runDump(true);
                }
              }
            }, Text.translatable("brrp.dumpScreen.existing.title"), Text.translatable("brrp.dumpScreen.existing.message.zip", this.dumpPath.toAbsolutePath().toString())));
            return;
          }
        } else {
          final long size;
          try (Stream<Path> walk = Files.walk(this.dumpPath)) {
            size = walk.limit(2001).count();
          }
          if (client != null) {
            dumpProgressText.setMessage(ScreenTexts.EMPTY);
            client.setScreen(new ConfirmScreen(value -> {
              if (client != null) {
                client.setScreen(this);
                if (value) {
                  runDump(true);
                }
              }
            }, Text.translatable("brrp.dumpScreen.existing.title"), Text.translatable("brrp.dumpScreen.existing.message", size > 2000 ? Text.translatable("brrp.dumpScreen.existing.size", 2000) : size, this.dumpPath.toAbsolutePath().toString())));
            return;
          }
        }
      }
    } catch (IOException e) {
      LOGGER.error("Checking whether the dump file exists:", e);
    }
    dumpThread = new Thread(() -> {
      interruptButton.active = true;
      backButton.active = false;
      dumpButton.setMessage(Text.translatable("brrp.dumpScreen.dumping"));
      Arrays.fill(dumpStat, 0);
      if (dumpAsZip) {
        try (final ZipOutputStream stream = new ZipOutputStream(Files.newOutputStream(dumpPath))) {
          pack.dump(stream, dumpType.resourceType, dumpStat);
        } catch (IOException e) {
          LOGGER.error("Cannot dump as zip:", e);
        }
      } else {
        pack.dumpInPath(dumpPath, dumpType.resourceType, dumpStat);
      }
      dumpButton.setMessage(Text.translatable("brrp.dumpScreen.dump"));
      interruptButton.active = false;
      backButton.active = true;
    }, "Dump pack");
    dumpThread.start();
  }

  @Override
  public void close() {
    if (dumpThread != null && dumpThread.isAlive()) return;
    if (client != null) {
      client.setScreen(parent);
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
