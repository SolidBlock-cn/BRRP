BRRP（Better Runtime Resource Pack，更好的运行时资源包），是基于 [ARRP](https://github.com/Devan-Kerman/ARRP) 模组的库模组，并进行了一些增强。本模组提供 ARRP 的所有功能，并修复 ARRP 模组存在的一些问题，同时提供了一系列新的功能。

## 什么是运行时资源包？

运行时资源包是在运行时创建的 Minecraft 资源文件，从而避免一些重复的操作。对于存在大量相似方块和物品的模组而言，借助运行时资源包可以极大地减小模组文件的大小。

例如，绝大多数方块的战利品表可以描述为“掉落方块本身”，少数方块需要精准采集才会掉落，而对于台阶方块，破坏双台阶后会掉落两倍的方块。在战利品表中，每个方块都要单独定义一个战利品表 JSON 文件，这是显然非常费神的。又如，几乎每个方块都需要定义方块状态、方块模型和物品模型，其中方块模型可能还不止一个，如果再加上战利品表和配方（若有）的话，每个方块都需要至少 5 个 JSON 文件才能实现完整的功能。而运行时资源包则会让这些内容在运行时生成在游戏内部，不需要打包在模组 JAR 文件中。

运行时资源包和常规的资源包在功能上一致的，常规的资源包（无论是模组自带资源包还是手动安装的资源包或数据包）都可以覆盖运行时资源包内容，亦可以与运行时资源包相互牵连。一个很典型的例子是，方块的模型由运行时资源包定义，而其纹理则是以传统的方式存储在模组文件中的（因为纹理文件并不适合在运行时生成）。

与传统资源包相比，运行时资源包节省了输入输出（I/O），但增加了对象序列化的过程，这是运行时资源包的一个缺点。资源包在生成时，会由游戏内对象转化为字节形式，通常是输出为 JSON，这一过程称为序列化。游戏在读取资源包时，又会重新解析这些字节形式的内容，以产生游戏内的对象，这一过程称为反序列化。传统的资源包中，所有的资源是已经序列化了的，游戏在加载资源包时只需要进行反序列化即可。而运行时资源包需要进行反序列化再序列化。

这里再梳理一遍传统资源包和运行时资源包在游戏内的读取过程：

- *传统的资源包* 文件 → （读取）字节形式 →（ 反序列化）游戏内对象
- *运行时资源包* 代码 → ARRP 内对象 → （序列化）字节形式 → （反序列化）游戏内对象

未来的版本，将会考虑直接生成游戏内的有关对象并直接使用，不经序列化和反序列化的途径。这个理论上已经可以做到，但是如果要作为资源包的形式允许传统的资源包或者数据包覆盖则还需要进行进一步研究才能完成。

## 关于本模组

本模组（BRRP）可以视为 ARRP 的扩展，支持 ARRP 的所有功能。如果您安装了 BRRP 模组，又安装了依赖 ARRP（但不依赖 BRRP）的模组，游戏可以正常启动，因此请*不要*同时安装 BRRP 和 ARRP 两个模组。但是，如果有模组将 ARRP 嵌入在这些模组的 JAR 文件中，可能会存在一些问题，因此并不建议将本模组嵌入在您的模组 JAR 中。

本模组尽可能避免对 ARRP 进行不兼容的更改，因此并没有删除不需要的类、字段或方法，而是将其弃用。如果有模组对 ARRP 进行了 mixin，则该模组在有 BRRP 时运行则有可能发生一些问题，但这种情况的概率已经尽可能降低。

本模组目前仍在开发中，因此并不提供将模组作为依赖的方式，也不提供下载，但依然开放源代码，并在 MPL 版权协议下发布。

本模组与 ARRP 的关系如下所示：

| 安装的其他模组：  | 仅依赖 ARRP | 需要依赖 BRRP |
|-----------|----------|-----------|
| 仅安装了 ARRP | 可以正常运行   | 不能正常运行    |
| 仅安装了 BRRP | 通常可以正常运行 | 可以正常运行    |

## 如何注册运行时资源包

运行时资源包在创建和写入内容后，需要注册才能在加载时生效。注册方法如下：

```java
public class MyClass implements ModInitializer {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create("my_pack");

  public void onInitialize() {
    pack.writeXXX('...');
    RRPCallback.BEFORE_VANILLA.register(a -> a.add(PACK));
  }
}
```

您可以在游戏运行的任何时候生成资源并注册您的资源包。上述示例使用的是游戏初始化末期的 `main` 入口点。您亦可在 `preLaunch` 或 `rrp:pregen`（需实现 `RRPPreGenEntryPoint` 接口）生成资源，但这种情况不能使用 Minecraft 的某些内容，例如游戏注册表。

除了添加常规资源外，您也可以添加异步资源，以允许在 `rrp:pregen` 的时候就加入游戏内需要的内容。`rrp:pregen` 中注册的运行时资源包会采用多线程的模式生成。

## 运行时资源包与数据生成

Minecraft 有原版的数据生成功能，Fabric API 对其进行了扩展。BRRP 正尽可能地在 ARRP 与 Minecraft 的原版类之间搭建桥梁。

在 BRRP 中，部分 ARRP 对象可以直接使用 Minecraft 的原版对应的对象。例如，原版的 `BlockStateModelGenerator` 中，有很多用于直接生成方块状态对象的方法（这些方法原先是 private 的，Fabric Data Generation API 进行了访问拓宽）。然后，可以直接使用 `JBlockStates.delegate` 以直接使用，代码片段如下：

```
JBlockStates.delegate(BlockStateModelGenerator.createStairsBlockState(
    (Block) this, 
    blockModelId.brrp_append("_inner"), 
    blockModelId, 
    blockModelId.brrp_append("_outer")))
```

这样，该对象在生成 JSON 时，直接使用 Minecraft 原版的 JSON 生成方式。

BRRP 在 ARRP 与 Minecraft 的原版类之间搭建桥梁的另一个例子是，在战利品表中，涉及数值提供器（value provider）的地方，不再推荐使用 ARRP 中的 `JRoll`，而是使用 Minecraft 原版的 `LootNumberProvider`，该类可以直接序列化为需要的 JSON 格式。

## 热交换

资源包创建之后，一般不支持热交换，这是因为资源包在被注册之前，其内容就已经写好了。如果您希望让您的运行时资源包能够热交换，可以参考以下做法：

```java
public class MyClass implements ModInitializer {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create("my_pack");

  public void onInitialize() {
    RRPCallback.BEFORE_VANILLA.register(a -> {
      pack.clearResources();
      pack.addXXX('...');
    });
  }
}
```

这样，Minecraft 每加载一次资源，运行时的资源包都会重新生成一次。但是，在模组发布时请不要这么做，因为这会导致每次重新加载资源包（包括游戏初始化和 F3+T 快捷键）或数据包（包括进入世界或者运行 `/reload`）都重新生成一次资源，这显然是不必要的。因此，模组发布时，建议在注册资源包之前就将资源生成好。