package sudokuSolver;

public class test {
    public static void main(String args[])
    {
        test t = new test();
        t.sudokuSolver();
    }

    private void sudokuSolver() {
        SudokuSolver s = new SudokuSolver();
        s.readTheProblem();
    }
}
