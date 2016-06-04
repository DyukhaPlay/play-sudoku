package sudoku;

import static sudoku.Util.*;
import static sudoku.Util.BitHelper.*;

import java.util.*;
import java.io.*;


public class Solver {
  private final static int N = Position.N, S = Position.S;

  boolean next(int[] a) {
    int cur = N;
    for (int i = a.length - 1; i >= 0; i--) {
      if (a[i] != cur) {
        a[i]++;
        for (int j = i+1; j < a.length; j++) {
          a[j] = a[j-1] + 1;
        }
        return true;
      }
      cur--;
    }
    return false;
  }

  private static int getBlock(int i, int j) {
    return i / S * S + j / S;
  }

  private Position pos, initPos;
  int[] rowMask, colMask, blockMask;
  int[] rowCnt, colCnt, blockCnt;
  int[][] mask;
  boolean isDoomed = false;

  public Solver(Position pos) {
    this.pos = pos.copy();
    this.initPos = pos.copy();
    rowMask = new int[N];
    colMask = new int[N];
    blockMask = new int[N];
    rowCnt = new int[N];
    colCnt = new int[N];
    blockCnt = new int[N];
    for (int i = 0; i < N; i++) {
      rowMask[i] = FULL_MASK;
      rowCnt[i] = 0;
      for (Pair p : Navigator.Row[i]) {
        int v = pos.get(p.first(), p.second());
        if (v != 0) {
          rowMask[i] = unsetBit(rowMask[i], v);
          rowCnt[i]++;
        }
      }
    }

    for (int i = 0; i < N; i++) {
      colMask[i] = FULL_MASK;
      colCnt[i] = 0;
      for (Pair p : Navigator.Col[i]) {
        int v = pos.get(p.first(), p.second());
        if (v != 0) {
          colMask[i] = unsetBit(colMask[i], v);
          colCnt[i]++;
        }
      }
    }

    for (int i = 0; i < N; i++) {
      blockMask[i] = FULL_MASK;
      blockCnt[i] = 0;
      for (Pair p : Navigator.Block[i]) {
        int v = pos.get(p.first(), p.second());
        if (v != 0) {
          blockMask[i] = unsetBit(blockMask[i], v);
          blockCnt[i]++;
        }
      }
    }

    for (int i = 0; i < N; i++)
      if (rowCnt[i] != N - bitCount(rowMask[i]))
        isDoomed = true;
    for (int i = 0; i < N; i++)
      if (colCnt[i] != N - bitCount(colMask[i]))
        isDoomed = true;
    for (int i = 0; i < N; i++)
      if (blockCnt[i] != N - bitCount(blockMask[i]))
        isDoomed = true;

    mask = new int[N][N];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        updateCell(i, j);
      }
    }
  }

  private Solver(Solver other) {
    rowMask = copyArr(other.rowMask);
    colMask = copyArr(other.colMask);
    blockMask = copyArr(other.blockMask);
    rowCnt = copyArr(other.rowCnt);
    colCnt = copyArr(other.colCnt);
    blockCnt = copyArr(other.blockCnt);
    mask = copy2dArr(other.mask);
    pos = other.pos.copy();
    initPos = other.initPos;
  }

  private final boolean isOnlyOneValue(int i, int j) {
    return bitCount(mask[i][j]) == 1;
  }

  private final void updateCell(int i, int j) {
    if (pos.get(i,j) != 0)
      mask[i][j] = fromBit(pos.get(i, j));
    else {
      mask[i][j] = rowMask[i] & colMask[j] & blockMask[getBlock(i, j)];
      isDoomed |= mask[i][j] == 0;
    }
  }

  private void setVal(int i, int j, int v) {
    assert pos.get(i, j) == 0;
    assert v != 0;
    int block = getBlock(i,j);
    pos.set(i, j, v);
    rowCnt[i]++;
    rowMask[i] = unsetBit(rowMask[i], v);
    colCnt[j]++;
    colMask[j] = unsetBit(colMask[j], v);
    blockCnt[block]++;
    blockMask[block] = unsetBit(blockMask[block], v);
    for (Pair[] nav : Navigator.Cell[i][j])
      for (Pair p : nav)
        updateCell(p.first(), p.second());
  }

  boolean removeOthers(int m, Pair ... cells) {
    boolean res = false;
    for (Pair[] nav : Navigator.All) {
      int cnt = 0;
      for (Pair p : nav) {
        for (Pair c : cells) {
          if (p.eq(c)) {
            cnt++;
            break;
          }
        }
      }
      if (cnt != cells.length)
        continue;
      for (Pair p : nav) {
        boolean other = true;
        for (Pair c : cells) {
          if (p.eq(c)) {
            other = false;
            break;
          }
        }
        if (other) {
          res |= change(p.first(), p.second(), p.inArr(mask) & ~m);
        }
      }
    }
    return res;
  }

  private boolean findOnlyOnes() {
    boolean res = false;
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        if (pos.get(i,j) == 0 && isOnlyOneValue(i, j)) {
          res = true;
          setVal(i, j, getSingleBit(mask[i][j]));
          //pos.print(System.out);
        }
      }
    }
    return res;
  }

  boolean change(int i, int j, int v) {
    int m = mask[i][j];
    if ((m & v) == m)
      return false;
    mask[i][j] = m & v;
    isDoomed |= mask[i][j] == 0;
    return true;
  }

  private boolean updatePossibleValues(int n) {
    boolean flag = false;
    int[] a = new int[n];
    for (int i = 0; i < n; i++)
      a[i] = i + 1;
    do {
      int m = fromBits(a);
      for (Pair[] nav : Navigator.All) {
        int cnt = 0;
        outer:
        for (Pair p : nav) {
          for (int b : a) {
            if (pos.get(p.first(), p.second()) == b) {
              cnt = -100;
              break outer;
            }
          }
          for (int b : a) {
            if (containsBit(p.inArr(mask), b)) {
              cnt++;
              break;
            }
          }
        }
        if (cnt < 0)
          continue;
        Pair[] ps = new Pair[n];
        int cur = 0;
        if (cnt == n) {
          for (Pair p : nav) {
            int pMask = p.inArr(mask);
            for (int b : a) {
              if (containsBit(pMask, b)) {
                flag |= change(p.first(), p.second(), m);
                ps[cur++] = p;
                break;
              }
            }
          }
          flag |= removeOthers(m, ps);
        }
      }
    } while (next(a));
    return flag;
  }

  void dfs(int i, int j, int bit, int color, int[][] colors) {
    assert colors[i][j] == 0;
    colors[i][j] = color;
    int newColor = 3 - color;
    for (Pair[] nav : Navigator.Cell[i][j]) {
      boolean visited = false;
      int cnt = 0;
      for (Pair p : nav) {
        visited |= p.first() == i && p.second() == j;
        if (containsBit(p.inArr(mask), bit) && p.inArr(colors) == 0) {
          cnt++;
        }
      }
      assert visited;
      if (cnt == 1) {
        for (Pair p : nav) {
          if (containsBit(p.inArr(mask), bit) && p.inArr(colors) == 0) {
            dfs(p.first(), p.second(), bit, newColor, colors);
          }
        }
      }
    }
  }

  boolean tryColors(int i, int j) {
    boolean flag = false;
    for (int b = 1; b <= N; b++) {
      if (!containsBit(mask[i][j], b))
        continue;
      int[][] colors = new int[N][N];
      dfs(i, j, b, 1, colors);
      int trueColor = 0;
      for (Pair[] nav : Navigator.All) {
        int fst = 0, snd = 0;
        for (Pair p : nav) {
          if (p.inArr(colors) == 1)
            fst++;
          if (p.inArr(colors) == 2)
            snd++;
        }
        if (fst > 1)
          trueColor = 2;
        if (snd > 1)
          trueColor = 1;
      }
      if (trueColor != 0) {
        for (int s = 0; s < N; s++) {
          for (int t = 0; t < N; t++) {
            if (colors[s][t] != 0) {
              if (colors[s][t] == trueColor) {
                flag |= change(s, t, fromBit(b));
              } else {
                flag |= change(s, t, ~fromBit(b));
              }
            }
          }
        }
      }
      if (flag)
        return true;
    }
    return flag;
  }

  boolean colors() {
    if (pos.numsCount() < N / 2 )
      return false;
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++)
        if (pos.get(i,j) == 0 && tryColors(i, j))
          return true;

    return false;
  }

  private boolean checkSolution() {
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        if (initPos.get(i, j) != 0 && initPos.get(i, j) != pos.get(i, j))
          return false;
        if (pos.get(i, j) == 0)
          return false;
      }
    }
    for (Pair[] nav : Navigator.All) {
      int m = 0;
      for (Pair p : nav) {
        m = setBit(m, pos.get(p.first(), p.second()));
      }
      if (m != FULL_MASK)
        return false;
    }
    return true;
  }

  private List<Solver> getNextSolversForBruteForce() {
    outer:
    while (true) {
      if (isDoomed)
        return new ArrayList<>();
      if (findOnlyOnes())
        continue;
      for (int i = 1; i <= 2; i++)
        if (updatePossibleValues(i))
          continue outer;
      if (colors())
        continue;
      break;
    }
    final int inf = 1000;
    int mini = -1, minj = -1, minc = inf;
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        if (pos.get(i, j) == 0 && bitCount(mask[i][j]) < minc) {
          minc = bitCount(mask[i][j]);
          mini = i;
          minj = j;
        }
      }
    }
    List<Solver> res = new ArrayList<>();
    if (minc == inf)
      return res;
    int cur = 0;
    int m = mask[mini][minj];

    for (int b = 1; b <= N; b++) {
      if (containsBit(m, b)) {
        Solver solver = new Solver(this);
        solver.setVal(mini, minj, b);
        res.add(solver);
      }
    }
    return res;
  }

  // 0 for 0, 1 for 1, >1 for other cases
  public int estimateSolutionCount() {
    List<Solver> solvers = getNextSolversForBruteForce();
    if (solvers.isEmpty())
      return checkSolution() ? 1 : 0;
    int cur = 0;
    for (Solver solver : solvers) {
      cur += solver.estimateSolutionCount();
      if (cur > 1)
        return cur;
    }
    return cur;
    //return new ArrayList<>();
  }

  public List<Position> solve() {
    List<Solver> solvers = getNextSolversForBruteForce();
    if (solvers.isEmpty()) {
      return checkSolution() ? Arrays.asList(pos) : new ArrayList<>();
    }
    List<Position> ans = new ArrayList<>();
    for (Solver solver : solvers) {
      ans.addAll(solver.solve());
    }
    return ans;
  }
}