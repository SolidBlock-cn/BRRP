BRRP（Better Runtime Resource Pack，更好的运行时资源包），是基于 [ARRP](https://github.com/Devan-Kerman/ARRP) 模模组的库模组，并进行了一些增强。本模组提供 ARRP 的所有功能，并修复 ARRP 模组存在的一些问题，同时提供了一系列新的功能。

[TOC]

## 什么是运行时资源包？

运行时资源包是在运行时创建的 Minecraft 资源文件，从而避免一些重复的操作。对于存在大量相似方块和物品的模组而言，借助运行时资源包可以极大地减小模组包的大小。

例如，绝大多数方块的战利品表可以描述为“掉落方块本身”，少数方块需要精准采集才会掉落，而对于台阶方块，破坏后会掉落两倍的方块。在战利品表中，每个方块都要单独定义一次战利品表，这是显然非常费神的。而运行时资源包则会让这些内容在运行时生成在游戏内部，不需要打包在模组包中。

运行时资源包和常规的资源包在功能上一致的，常规的资源包（无论是模组自带资源包还是手动安装的资源包或数据包）都可以覆盖运行时资源包内容，亦可以与运行时资源包相互牵连。一个很典型的例子是，方块的模型由运行时资源包定义，而其纹理则是以传统的方式存储在模组文件中的（因为纹理文件并不适合在运行时生成）。

与传统资源包相比，运行时资源包节省了输入输出（I/O），但增加了对象序列化的过程，这是运行时资源包的一个缺点。资源包在生成时，会由游戏内对象转化为字节形式，通常是输出为 JSON，这一过程称为序列化。游戏在读取资源包时，又会重新解析这些字节形式的内容，以产生游戏内的对象，这一过程称为反序列化。传统的资源包中，所有的资源是已经序列化了的，游戏在加载资源包时只需要进行反序列化即可。而运行时资源包需要进行反序列化再序列化。

## 关于本模组

本模组可以视为 ARRP 的扩展，支持 ARRP 的所有功能。如果您安装了 BRRP 模组，又安装了依赖 ARRP（但不依赖 BRRP）的模组，游戏可以正常启动。但是，如果有模组将 ARRP 内嵌在 JAR 中，可能会存在一些问题，因此并不建议将本模组作为依赖内嵌在您的模组 JAR 中。

本模组尽可能避免对原版代码的更改，因此并没有删除不需要的类、字段或方法，而是将其弃用。如果有模组对 BRRP 进行了 mixin，则有可能发生一些问题，但这种情况的概率已经尽可能降低。

本模组目前仍在开发中，因此并不提供将模组作为依赖的方式，也不提供下载，但依然开放源代码，并在 MPL 版权协议下发布。

## 如何注册运行时资源包

请参考如下代码：

```java
public class MyClass implements ModInitializer {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create("my_pack");

  public void onInitialize() {
    RRPCallback.BEFORE_VANILLA.register(a -> a.add(PACK));
  }
}
```

您可以在游戏运行的任何时候生成数据并注册您的资源包。上述示例使用的是游戏初始化末期的 `main` 入口点。您亦可在 `preLaunch` 或 `rrp:pregen`（需实现 `RRPPreGenEntryPoint` 接口）生成数据，但这种情况不能使用游戏的某些内容，例如注册表。

除了添加常规资源外，您也可以添加异步资源，以允许在 `rrp:pregen` 的时候就加入游戏内需要的内容。`rrp:pregen` 中注册的运行时资源包会采用多线程的模式生成。

## 运行时资源包与数据生成

Minecraft 有原版的数据生成功能，Fabric API 对其进行了扩展。BRRP 正尽可能地在 ARRP 与 Minecraft 的数据生成之间搭建桥梁。

在 BRRP 中，部分 ARRP 对象可以直接使用 Minecraft 的原版对应的对象。例如，原版的 `BlockStateModelGenerator` 中，有很多用于直接生成方块定义对象的方法（这些方法原先是 private 的，Fabric Data Generation API 进行了访问拓宽。然后，可以直接使用 `JBlockStates.delegate` 以直接使用，代码片段如下：

```
JBlockStates.delegate(BlockStateModelGenerator.createStairsBlockState(
    (Block) this, 
    blockModelId.brrp_append("_inner"), 
    blockModelId, 
    blockModelId.brrp_append("_outer")))
```

这样，该对象在生成 JSON 时，直接使用 Minecraft 原版的 JSON 生成方式，非常方便。