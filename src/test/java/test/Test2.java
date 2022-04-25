package test;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionFile;

import java.io.*;

public class Test2 {
  public static void main(String[] args) {
    for (int x = -1; x < 2; x++)
      for (int y = -1; y < 2; y++) {
        try {
          final File file = new File(String.format("r.%s.%s.mca", x, y));
          final RegionFile regionFile = new RegionFile(new File(file.getPath()), new File(""), true);
          for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
              removeBiomes(regionFile, new ChunkPos(i, j));
            }
          }
          regionFile.close();
        } catch (FileNotFoundException e) {//ignore
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
  }

  private static void removeBiomes(RegionFile regionFile, ChunkPos chunkPos) throws IOException {
    if (!regionFile.hasChunk(chunkPos)) {
      return;
    }
    final DataInputStream chunkInputStream = regionFile.getChunkInputStream(chunkPos);
    if (chunkInputStream == null) return;
    final NbtCompound read = NbtIo.read(chunkInputStream);
    chunkInputStream.close();
    if (read == null) return;
    replaceBiomesField(read);
    final DataOutputStream chunkOutputStream = regionFile.getChunkOutputStream(chunkPos);
    NbtIo.write(read, chunkOutputStream);
    chunkOutputStream.close();
  }

  public static void replaceBiomesField(NbtElement element) {
    if (element instanceof NbtCompound) {
      NbtCompound compound = (NbtCompound) element;
      if (compound.contains("biomes", NbtType.COMPOUND)) {
        System.out.printf("Removed compound field 'biomes': %s%n", compound.get("biomes"));
        compound.remove("biomes");
      } else {
        for (String key : compound.getKeys()) {
          replaceBiomesField(compound.get(key));
        }
      }
    } else if (element instanceof NbtList) {
      NbtList list = (NbtList) element;
      list.forEach(Test2::replaceBiomesField);
    }
  }
}
