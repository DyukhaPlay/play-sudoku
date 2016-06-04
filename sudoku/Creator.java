package sudoku;

import static sudoku.Util.*;
import java.util.*;


public class Creator {
  private static Random r = new Random();
  private static final int n = Position.N;

  private static Position Init() {
    genAll:
    while (true) {
      System.err.print("I");
      Position pos = new Position();
      for (int t = 0; t < 30; t++) {
        while (true) {
          int i = r.nextInt(n);
          int j = r.nextInt(n);
          if (pos.get(i, j) != 0)
            continue;

          int lim = 10;
          int val;
          genNum:
          while (true) {
            val = r.nextInt(n);
            for (Pair[] nav : Navigator.Cell[i][j]) {
              for (Pair p : nav) {
                if (pos.get(p.first(), p.second()) == val) {
                  lim--;
                  if (lim > 0)
                    continue genNum;
                  continue genAll;
                }
              }
            }
            break;
          }
          pos.set(i, j, val);
          break;
        }
      }
      Solver initSolver = new Solver(pos);
      int res = initSolver.estimateSolutionCount();
      if (res == 0)
        continue;
      if (res == 1) {
        pos.print(System.out);
        return pos;
      }
    }
  }

  private static void AddWhileNecessary(Position pos) {
    while (true) {
      System.err.print("A");
      int i = r.nextInt(n);
      int j = r.nextInt(n);
      if (pos.get(i, j) != 0)
        continue;
      pos.set(i, j, r.nextInt(n));
      Solver solver = new Solver(pos);
      int res = solver.estimateSolutionCount();
      if (res == 0) {
        pos.unset(i,j);
        continue;
      }
      if (res == 1) {
        pos.print(System.out);
        return;
      }
      pos.print(System.out);
    }
  }

  private static void RemoveWhileCan(Position pos) {
    List<Pair> cells = new ArrayList<>();
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        if (pos.get(i,j) != 0)
          cells.add(new Pair(i, j));
    Collections.shuffle(cells, r);
    for (Pair p : cells) {
      int i = p.first();
      int j = p.second();
      if (pos.get(i, j) == 0)
        continue;
      System.err.print("R");
      int v = pos.get(i, j);
      pos.unset(i, j);
      Solver solver = new Solver(pos);
      int res = solver.estimateSolutionCount();
      assert res > 0;
      if (res > 1) {
        pos.set(i,j, v);
        continue;
      }
      pos.print(System.out);
    }
  }

  static void printMsg(String msg, boolean newLineBefore) {
    if (newLineBefore)
      System.err.println();
    System.err.println(msg);
    System.out.println(msg);
  }

  public static void main(String[] args) {
    long time = System.nanoTime();
    Position pos = Init();
    printMsg("After init", true);
    AddWhileNecessary(pos);
    printMsg("After adding", true);
    RemoveWhileCan(pos);
    printMsg("After remove", true);
    printMsg("Nums count: " + pos.numsCount(), false);
    printMsg("Time: " + ((System.nanoTime() - time) / 1e9), false);
  }
}