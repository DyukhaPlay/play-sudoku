package sudoku;

import java.util.*;

public class Creator {
  public static void main(String[] args) {
    Random r = new Random();
    long time = System.nanoTime();
    all:
    while (true) {
      Position pos = new Position();
      final int n = Position.N;
      for (int t = 0; t < 28; t++) {
        while (true) {
          int i = r.nextInt(n);
          int j = r.nextInt(n);
          if (pos.get(i, j) != 0)
            continue;
          pos.set(i, j, r.nextInt(n));
          break;
        }
      }
      try {
        {
          System.err.print("I");
          Solver initSolver = new Solver(pos);
          List<Position> res = initSolver.solve();
          if (res.size() == 0)
            continue;
          if (res.size() == 1) {
            pos.print(System.out);
            break all;
          }
        }
        System.err.println("After init");
        while (true) {
          int i = r.nextInt(n);
          int j = r.nextInt(n);
          if (pos.get(i, j) != 0)
            continue;
          pos.set(i, j, r.nextInt(n));
          try {
            Solver solver = new Solver(pos);
            List<Position> res = solver.solve();
            if (res.size() == 0) {
              pos.unset(i,j);
              continue;
            }
            if (res.size() == 1) {
              pos.print(System.out);
              break all;
            }
            pos.print(System.out);
          } catch (Solver.IncorrectInitialPosition e) {
            pos.unset(i,j);
          }
        }
      } catch (Solver.IncorrectInitialPosition e) {
        
      }
    }
    System.err.println("Time: " + ((System.nanoTime() - time) / 1e9));
  }
}