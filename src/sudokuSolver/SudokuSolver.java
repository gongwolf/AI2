package sudokuSolver;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SudokuSolver {
    String srcFile = "problem.txt";
    int column = -1, rows;
    int[][] arr;
    boolean[][] fixed;
    private int n;

    public void readTheProblem() {
        System.out.println("Enter your the problem file (default is problem.txt): ");
        Scanner scanner = new Scanner(System.in);
        String filename = scanner.nextLine();
        if (!filename.equals("")) {
            this.srcFile = filename;
        }
        System.out.println("Your problem is :" + this.srcFile);
        readTheArray(this.srcFile);
        System.out.println(getTheCosts());
    }

    private int getTheCosts() {
        int cost = 0;
        for(int i =0;i<rows;i++)
        {
            ArrayList<Integer> rowsInfo = new ArrayList<>();
            for(int j = 0; j<column;j++)
            {
                if(!rowsInfo.contains(this.arr[i][j]))
                {
                    rowsInfo.add(this.arr[i][j]);
                }
            }
            cost+=n*n-rowsInfo.size();

        }

        System.out.println("\r\n---------------------------\r\n");
        for(int i =0;i<column;i++)
        {
            ArrayList<Integer> colInfo = new ArrayList<>();
            for(int j = 0; j<rows;j++)
            {
                if(!colInfo.contains(this.arr[j][i]))
                {
                    colInfo.add(this.arr[j][i]);
                }
            }
            cost+=n*n-colInfo.size();

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

            printArray(this.arr);
            randomFill();
            printArray(this.arr);

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
        ArrayList<Integer> alreadyContains = new ArrayList<>();
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

        int result = getRandomNumberInRange(1, this.n);
        if (alreadyContains.size() < 4) {
            while (alreadyContains.contains(result)) {
                result = getRandomNumberInRange(1, this.n);
            }
            return result;
        } else {
            for (int i = 1; i <= n * n; i++) {
                if (!alreadyContains.contains(i)) {
                    return i;
                }
            }
        }

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

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
