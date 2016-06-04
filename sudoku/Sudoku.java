package sudoku;

import java.util.*;
import java.io.*;

public class Sudoku {
  public static void main(String[] args) throws IOException, Solver.IncorrectInitialPosition {
    Solver solver;
    {
      Position pos;
      try (Scanner scanner = new Scanner(new FileReader("sudoku.in"))) {
        pos = Position.read(scanner);
      }
      solver = new Solver(pos);
    }
    List<Position> res = solver.solve();
    for (Position pos : res) {
      pos.print(System.out);
    }
  }
}