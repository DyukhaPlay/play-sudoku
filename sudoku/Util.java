package sudoku;

public class Util {
  private static int N = Position.N, S = Position.S;
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

  public static final class Pair {
    private int myFirst, mySecond;
    public Pair (int f, int s) {
      myFirst = f;
      mySecond = s;
    }
    public int first() { return myFirst; }
    public int second() { return mySecond; }
  }

  public static class Navigator {
    public static Pair[][] Block, Row, Col;
    public static Pair[][] All;
    public static Pair[][][][] Cell;

    static {
      Block = new Pair[N][N];
      Row = new Pair[N][N];
      Col = new Pair[N][N];
      All = new Pair[N * 3][N];
      Cell = new Pair[N][N][3][];
      int[][] cellCur = new int[N][N];
      int cur = 0;
      for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
          Row[i][j] = new Pair(i, j);
          Cell[i][j][cellCur[i][j]++] = Row[i];
        }
        All[cur++] = Row[i];

        for (int j = 0; j < N; j++) {
          Col[i][j] = new Pair(j, i);
          Cell[j][i][cellCur[j][i]++] = Col[i];
        }
        All[cur++] = Col[i];

        int si = i / S * S, sj = i % S * S;
        for (int j = 0; j < N; j++) {
          int pi = si + j / S, pj = sj + j % S;
          Block[i][j] = new Pair(pi, pj);
          Cell[pi][pj][cellCur[pi][pj]++] = Block[i];
        }
        All[cur++] = Block[i];
      }
    }
  }
}