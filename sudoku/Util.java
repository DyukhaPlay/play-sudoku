package sudoku;

public class Util {
  private Util() {}
  public static int[] copyArr(int[] a) {
    int[] res = new int[a.length];
    for (int i = 0; i < a.length; i++) {
      res[i] = a[i];
    }
    return res;
  }

  public static int[][] copy2dArr(int[][] a) {
    
    int[][] res = new int[a.length][];
    for (int i = 0; i < a.length; i++) {
      res[i] = copyArr(a[i]);
    }
    return res;
  }

}