package sudoku;

import static sudoku.Util.*;

import java.io.*;
import java.util.*;

public final class Position {
  public static final int S = 3, N = S * S;

  int[][] a;

  public Position(int a[][]) {
    this.a = copy2dArr(a);
  }

  public Position() {
    this.a = new int[N][N];
  }

  public static Position read(Scanner scanner) {
    int[][] a = new int[N][];
    for (int i = 0; i < N; i++) {
      a[i] = new int[N];
      for (int j = 0; j < N; j++) {
        a[i][j] = scanner.nextInt();
      }
    }
    return new Position(a);
  }

  public void print(PrintStream out) {
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        if (a[i][j] == 0)
          out.print(".");
        else
          out.print(a[i][j]);
        out.print(" ");
        if (j % S == S - 1)
          out.print(" ");
      }
      out.println();
      if (i % S == S - 1)
        out.println();
    }
    out.println();
  }

  public int numsCount() {
    int res = 0;
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++)
        if (a[i][j] > 0)
          res++;
    return res;
  }

  public int get(int i, int j) {
    return a[i][j];
  }

  public Position copy() {
    return new Position(a);
  }

  public void set(int i, int j, int x) {
    if (a[i][j] != 0)
      throw new IllegalArgumentException("i, j");
    a[i][j] = x;
  }

  public void unset(int i, int j) {
    if (a[i][j] == 0)
      throw new IllegalArgumentException("i, j");
    a[i][j] = 0;
  }

}
