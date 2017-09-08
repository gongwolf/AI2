package sudokuSolver;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class SudokuSolver {
    String srcFile = "problem.txt";
    int column = -1, rows;
    int[][] arr;
    int[][] initArr;
    boolean[][] fixed;
    private int n;
    private Random r;


    public void readTheProblem() {
//        System.out.println("Enter your the problem file (default is problem.txt): ");
//        Scanner scanner = new Scanner(System.in);
//        String filename = scanner.nextLine();
//        if (!filename.equals("")) {
//            this.srcFile = filename;
//        }
//        System.out.println("Your problem is :" + this.srcFile);
        r = new Random(System.currentTimeMillis());
        readTheArray(this.srcFile);
        System.out.println("After random fill the sudoku:");
        printArray(this.arr);
        long run1 = System.currentTimeMillis();
        solver();
        System.out.println("It use "+(System.currentTimeMillis()-run1)+" ms to solve the problem.");
    }

    private void solver() {
        int ini_t=4;

        double t = ini_t;
        double alpha = 0.8;
        boolean reheat=true;

        int cost_no_changed = 0;
        int iter = 0;
        while(getTheCosts(this.arr)!=0)
        {
            iter++;
            if(iter%1000000==0)
            {
                System.out.println("iterations : "+iter+".................");
            }
            int cost_prv = getTheCosts(this.arr);
            if(reheat)
            {
                t=ini_t;
                reheat=false;
            }else
            {
                t=alpha*t;
            }
            randomAndSwap(t);
            int cost_after = getTheCosts(this.arr);
            if(cost_after==cost_prv)
            {
                cost_no_changed++;
                if(cost_no_changed==2000)
                {
//                    System.out.println("reheat");
                    this.arr = cloneArray(this.initArr);
                    randomFill();
                    cost_no_changed=0;
                    reheat = true;
                }
            }
        }
//        for(int i = 0 ; i < 20;i++) {
//            printArray(this.arr);
//
//            System.out.println(getTheCosts(this.arr));
//            randomAndSwap(4);
//            System.out.println(getTheCosts(this.arr));
//            printArray(this.arr);
//
//            System.out.println("=============================");
//        }
        System.out.println("The result is :");
        printArray(this.arr);

    }

    private void randomAndSwap(double t_s) {
        int rand_i=getRandomNumberInRange(0,n*n-1);
        int rand_j=getRandomNumberInRange(0,n*n-1);

        // if the random chosen element is fixed, choose another one.
        while(this.fixed[rand_i][rand_j])
        {
             rand_i=getRandomNumberInRange(0,n*n-1);
             rand_j=getRandomNumberInRange(0,n*n-1);
        }

        int square_i = rand_i/n;
        int square_j = rand_j/n;
        int suffle_i = getRandomNumberInRange(square_i*n,(square_i+1)*n-1);
        int suffle_j = getRandomNumberInRange(square_j*n,(square_j+1)*n-1);

        //if the suffle element is equal to random element or the suffle element is fixed
        //choose another one
        while(this.arr[rand_i][rand_j]==this.arr[suffle_i][suffle_j]||this.fixed[suffle_i][suffle_j])
        {
             suffle_i = getRandomNumberInRange(square_i*n,(square_i+1)*n-1);
             suffle_j = getRandomNumberInRange(square_j*n,(square_j+1)*n-1);
        }

        int[][] copyArr = cloneArray(this.arr);
        int t = copyArr[rand_i][rand_j];
        copyArr[rand_i][rand_j]=copyArr[suffle_i][suffle_j];
        copyArr[suffle_i][suffle_j]=t;

//        Random r = new Random(System.currentTimeMillis());
        double mySample = r.nextDouble();
        double prob = Math.exp(-Math.abs(getTheCosts(this.arr)-getTheCosts(copyArr))/t_s);
//        System.out.println(mySample+"  "+prob);
        if(getTheCosts(this.arr)>getTheCosts(copyArr)||mySample<prob)
        {
            this.arr = copyArr;
        }

    }

    private int getTheCosts(int[][] arr) {
        int cost = 0;
        for (int i = 0; i < rows; i++) {
            ArrayList<Integer> rowsInfo = new ArrayList<>();
            for (int j = 0; j < column; j++) {
                if (!rowsInfo.contains(arr[i][j])) {
                    rowsInfo.add(arr[i][j]);
                }
            }
            cost += n * n - rowsInfo.size();

        }

//        System.out.println("\r\n---------------------------\r\n");
        for (int i = 0; i < column; i++) {
            ArrayList<Integer> colInfo = new ArrayList<>();
            for (int j = 0; j < rows; j++) {
                if (!colInfo.contains(arr[j][i])) {
                    colInfo.add(arr[j][i]);
                }
            }
            cost += n * n - colInfo.size();

        }

        return cost;
    }

    private void readTheArray(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            ArrayList<String> lines = new ArrayList<>();
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                lines.add(line);
                this.rows++;
                if (this.column == -1)
                    this.column = line.length();
            }

            if (rows != column) {
                System.out.println("Rows number is not match column number");
            }

            this.arr = new int[rows][column];
            this.fixed = new boolean[rows][column];
            this.n = (int) Math.sqrt(rows);

            int i = 0;
            for (String l : lines) {
                for (int j = 0; j < l.length(); j++) {
                    char c = l.charAt(j);
                    int d = (c - '0');
                    this.arr[i][j] = (c - '0');
                    if (d != 0) {
                        this.fixed[i][j] = true;
                    }
                }
                i++;
            }

//            printArray(this.arr);
            this.initArr=cloneArray(this.arr);
            randomFill();
//            printArray(this.arr);
//            this.arr = cloneArray(initArr);
//            randomFill();
//            printArray(this.arr);
//            printArray(initArr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void randomFill() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {
                if (this.arr[i][j] == 0 && this.fixed[i][j] == false)
                    findInTheSquare(i, j);
            }
        }
    }

    private void findInTheSquare(int i, int j) {
        int square_i = i / n;
        int square_j = j / n;
        this.arr[i][j] = getRandomNumberInSquaure(square_i, square_j);
    }

    private int getRandomNumberInSquaure(int square_i, int square_j) {
        HashSet<Integer> alreadyContains = new HashSet<>();
        HashSet<Integer> allNumber = new HashSet<>();

        for (int i = 1; i <= n * n; i++) {
            allNumber.add(i);
        }
//        System.out.println(square_i + " " + square_j);
        for (int i = square_i * n; i < (square_i + 1) * n; i++) {
            for (int j = square_j * n; j < (square_j + 1) * n; j++) {
                if (arr[i][j] != 0) {
                    alreadyContains.add(arr[i][j]);
                }
//                System.out.print(arr[i][j]+" ");
            }
//            System.out.print("\n");
        }

        for (int i : alreadyContains) {
            allNumber.remove(i);
        }

        ArrayList<Integer> ai = new ArrayList<>(allNumber);
        int result;
        if (ai.size() != 1) {
            result = ai.get(getRandomNumberInRange(0, ai.size() - 1));
        } else {
            result = ai.get(0);
        }
//        int result = getRandomNumberInRange(1, this.n);
//        if (alreadyContains.size() < 4) {
//            while (alreadyContains.contains(result)) {
//                result = getRandomNumberInRange(1, this.n);
//            }
//            return result;
//        } else {
//            for (int i = 1; i <= n * n; i++) {
//                if (!alreadyContains.contains(i)) {
//                    return i;
//                }
//            }
//        }

        return result;
    }


    private void printArray(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.out.println("-----------------------------");
    }

    private int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return r.nextInt((max - min) + 1) + min;
    }

    public int[][] cloneArray(int[][] src) {
        int length = src.length;
        int[][] target = new int[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }
}
