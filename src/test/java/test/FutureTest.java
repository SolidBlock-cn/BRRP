package test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class FutureTest {
  public static void main(String[] args) {
    var service = Executors.newFixedThreadPool(10);
    var future1 = service.submit(() -> {
      try {
        Thread.sleep(2000);
        return "休息了2秒。";
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    var future2 = service.submit(() -> {
      try {
        Thread.sleep(4000);
        return "休息了4秒。";
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    try {
      System.out.printf("%s\t%s\n", future1.get(), System.currentTimeMillis());
      System.out.printf("%s\t%s%n", future2.get(), System.currentTimeMillis());
      System.out.printf("%s\t%s\n", future1.get(), System.currentTimeMillis());
      System.out.printf("%s\t%s%n", future2.get(), System.currentTimeMillis());
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }
}
