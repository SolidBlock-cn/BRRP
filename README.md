# 更好的运行时资源包

[Click here for English version of this document.](README-en.md)

BRRP（Better Runtime Resource Pack，更好的运行时资源包），是可用于在运行时创建游戏资源的前置模组，同时是 [ARRP](https://github.com/Devan-Kerman/ARRP)（高级运行时资源包）模组的一个分支。

欢迎加入QQ群**587928350**或 KOOK（开黑啦）频道邀请码**KlFS0n**体验本模组的最新更新。

注意：自版本 1.0.0 开始，本模组的 ID 由 `better_runtime_resource_pack` 更改为 `brrp_v1`，并进行大量重构，不向下兼容，但可以于旧版本共存。如非需要，请不要再使用旧版本的 BRRP 模组。

## 什么是运行时资源包？

运行时资源包是在运行时创建的 Minecraft 资源文件，从而避免一些重复的操作。对于存在大量相似方块和物品的模组而言，借助运行时资源包可以极大地减小模组文件的大小。

例如，绝大多数方块的战利品表可以描述为“掉落方块本身”，少数方块需要精准采集才会掉落，而对于台阶方块，破坏双台阶后会掉落两倍的方块。在战利品表中，每个方块都要单独定义一个战利品表 JSON 文件，这是显然非常费神的。又如，几乎每个方块都需要定义方块状态、方块模型和物品模型，其中方块模型可能还不止一个，如果再加上战利品表和配方（若有）的话，每个方块都需要至少 5 个 JSON 文件才能实现完整的功能。而运行时资源包则会让这些内容在运行时生成在游戏内部，不需要打包在模组 JAR 文件中。

运行时资源包和常规的资源包在功能上一致的，常规的资源包（无论是模组自带资源包还是手动安装的资源包或数据包）都可以覆盖运行时资源包内容，亦可以与运行时资源包相互牵连。一个很典型的例子是，方块的模型由运行时资源包定义，而其纹理则是以传统的方式存储在模组文件中的（因为纹理文件并不适合在运行时生成）。

与传统资源包相比，运行时资源包节省了输入输出（I/O），但增加了对象序列化的过程，这是运行时资源包的一个缺点。资源包在生成时，会由游戏内对象转化为字节形式，通常是输出为 JSON，这一过程称为序列化。游戏在读取资源包时，又会重新解析这些字节形式的内容，以产生游戏内的对象，这一过程称为反序列化。传统的资源包中，所有的资源是已经序列化了的，游戏在加载资源包时只需要进行反序列化即可。而运行时资源包需要进行反序列化再序列化。

这里再梳理一遍传统资源包和运行时资源包在游戏内的读取过程：

- *传统的资源包* 文件 → （读取）字节形式 → （反序列化）游戏内对象
- *运行时资源包* 代码 → ARRP 内对象 → （序列化）字节形式 → （反序列化）游戏内对象

未来的版本，将会考虑直接生成游戏内的有关对象并直接使用，不经序列化和反序列化的途径。这个理论上已经可以做到，但是如果要作为资源包的形式允许传统的资源包或者数据包覆盖则还需要进行进一步研究才能完成。

## 关于本模组

本模组（BRRP）可以是 ARRP 的分支，自 1.0.0 版本开始，已经从 ARRP 模组独立出去，不再提供 ARRP 的功能，且理论上可以同 ARRP 共存。

本模组开放源代码，并在 MPLv2 版权协议下发布。

## 如何注册运行时资源包

运行时资源包在创建和写入内容后，需要注册才能在加载时生效。注册方法如下：

### Fabric

```java
public class MyClass implements ModInitializer {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create(new Identifier("my_mod", "my_pack"));

  @Override
  public void onInitialize() {
    // 你可以在此处调用 pack 的 write 方法以向资源包中写入内容。

    RRPCallback.BEFORE_VANILLA.register(resources -> resources.add(pack));
  }
}
```

### Forge

对于 Forge 版本，你可以使用 `RRPEvent` 来在模组的事件总线上注册资源包，方法如下：

```java

@Mod("my_mod_id")
public class MyClass {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create(new Identifier("my_mod", "my_pack"));

  public MyClass() {
    // 你可以在此处调用 pack 的 write 方法以向资源包中写入内容。

    FMLJavaModLoadingContext.get().getModEventBus().addListener((RRPEvent.BeforeVanilla event) -> event.addPack(pack));
  }
}
```

### 同时支持 Forge 和 Fabric

本模组提供的 `RRPEventHelper` 可以同时支持 Forge 和 Fabric。例如：

```java
public class MyClass implements ModInitializer {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create(new Identifier("my_mod", "my_pack"));

  @Override
  public void onInitialize() {
    // 你可以在此处调用 pack 的 write 方法以向资源包中写入内容。

    RRPEventHelper.BEFORE_VANILLA.registerPack(pack);
  }
}
```

## 什么是资源与数据

通常，在 Minecraft 中资源（assets）包括客户端使用的一切用于渲染、显示或播放的文件，包括方块状态（"block states"）、模型（model）、纹理（texture）、语言文件（lang）、声音等。在 Minecraft 中，储存这些内容的包称为“资源包（resource pack）”。这里所说的资源包包括 Minecraft 自带的原版资源包以及 Fabric Loader 和模组提供的资源包，不一定是 `resourcepacks` 文件夹中的。资源包只会在客户端使用，服务器不使用，但是玩家加入服务器时可能会被要求使用服务器提供的资源包以在客户端安装。语言文件是唯一一个服务器需要使用的资源。

而数据（data）则包括所有服务器使用的文件，包括配方、战利品表、标签、进度等。服务器创建时（包括单人游戏），数据才会加载。数据包（data pack）存储的数据是服务器使用的，这里所说的数据包包括 Minecraft 原版自带的数据包、Fabric Loader 和模组提供的数据包以及存储在服务器存档（包括玩家单人地图存档）的 `data` 文件夹中的可安装的自定义数据包。

这里整理一下：资源（assets）由资源包（resource pack）提供，用于客户端。语言文件是服务器唯一使用的资源。数据（data）由数据包提供（data pack）提供，用于服务器。

不过涉及模组开发时，这些概念就有些混乱了。开发模组时，前面所说的“资源”（assets）和“数据”（data）都统称为“资源”（resources），这里所说的“资源包”是模组文件的 `resources` 文件夹，包括了前文提及的客户端资源和服务器数据（你应该看到了，这里总是会加上“客户端”和“服务器”修饰以避免歧义）。本模组的“运行时资源包”自然也包括客户端资源和服务器数据。

Minecraft 和 Fabric API 的数据生成器（data generator）生成的也不只是数据，而是包括了所有客户端资源和服务器数据的。

这里再整理一下，在涉及模组开发时，资源（resources）和数据（data）的意思其实是一样的，都包括客户端资源服务器数据这两者。为避免歧义，如确实需要表述其中一端使用的，就加上“客户端”与“服务器”修饰。

## 如何将本模组用作您的项目的依赖

如果需要将本模组作为您的依赖，并使用本模组的 API，可以有以下几种方法。

### 方法一：使用 GitHub 中的仓库

在 `build.gradle` 的 `repositories` 和 `dependencies` 部分分别加入以下内容：

```groovy
repositories {
    maven {
        url 'https://raw.githubusercontent.com/SolidBlock-cn/mvn-repo/main'

        // 如果上面的网址连接不成功，可以尝试使用镜像站，例如：
        // url 'https://raw.nuaa.cf/SolidBlock-cn/mvn-repo/main'
        // 注：不确保镜像站能稳定连接且内容不被篡改，上述镜像站地址亦只是示例。
    }
}

dependencies {
    modImplementation "pers.solid:brrp-fabric:模组版本-MC版本"
    // 注意：对于 Forge 版本，请将上面的 fabric 改为 forge。
    // 对于 0.8.1 及以下的版本，请将 pers.solid 改为 net.devtech。但是，不建议使用旧版本。
    // modImplementation 和 modApi 的区别在于，当其他项目依赖你的项目，如果你的项目使用的是 modApi，那么其他项目会自动加载你依赖的内容，如果是 modImplementation 则不会。你可以自行选择。
}
```

注意：自从 1.0.0 版本开始，对于 Fabric 版本，本模组开始依赖 Mod Menu，因此在 Fabric 的情况下，你还需要：
- 在上面的 `repositories` 中再加入以下内容：
```groovy
    // Mod Menu 所需要的存储库（仅限 Fabric）
    maven { url "https://maven.terraformersmc.com/releases/" }
```
- **或者**将 `dependencies` 中的内容改为：
```groovy
    // 不加载 Mod Menu
    modImplementation("pers.solid:brrp-fabric:模组版本-MC版本") {
        transitive false
    }
```

### 方法二：将文件下载到本地

您需要先下载 <code>**brrp-模组版本-MC版本.jar**</code> 和 <code>**brrp-模组版本-MC版本-sources.jar**</code> 这两个文件（可以在 GitHub 的 releases 部分，或者从 Modrinth 中下载），并放在您的设备存储的任意地方（建议直接存储在项目文件夹内或者附近，两个文件都应该放在同一个文件夹内，并且，如有需要，可将下载到的文件加入 `.gitignore`）。

然后，在 `build.gradle` 的 `repositories` 和 `dependencies` 部分分别加入以下内容：

```groovy
repositories {
    // dir 中的字符串是存储这些文件的文件夹位置，可以是相对位置。
    flatDir { dir "存储上述两个 .jar 文件的文件夹路径" }
}


dependencies {
    modImplementation "pers.solid:brrp-fabric:模组版本-MC版本"
}
```
上面的“方法一”中的注意事项，对“方法二”也适用。

### 检查项目是否配置完成

刷新项目，然后检查该库是否被正常加载。例如，在 IntelliJ IDEA 中，您可以双击 Shift，输入 `RuntimeResourcePack`，如果找到了这个类，并且类的代码和注释都能够正常加载，说明项目加载正常了。

然后，在您的 Fabric 项目的 `fabric.mod.json` 中的 `depends` 部分添加本模组：

```json5
{
  // [...]
  "depends": {
    // [...]

    // “*”表示“任意版本”。您亦可指定特定版本，如“>=1.0.0”。
    // 注意不要在 JSON 里面留下这些注释。
    "brrp_v1": "*"
  }
}
```

或者，在 Forge 项目的 `mods.toml` 中，加入：

```toml
[[dependencies.'你的模组的id']]
modId = "brrp_v1"
mandatory = true
versionRange = "[1.0.0,)"
ordering = "NONE"
side = "BOTH"
```
