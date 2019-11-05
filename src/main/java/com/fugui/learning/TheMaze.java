package com.fugui.learning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class TheMaze {

    public static void main(String[] args) {
        System.out.println("This is a program that finds the cheese and then an exit point (if\n"
            + "possible) of a given maze. If the search for the cheese and an exit is successful,\n"
            + "it'll print the maze showing the path (the shortest path) that has been found. ");

        Scanner in = new Scanner(System.in);
        do {
            System.out.println("if want to exit pls enter 'exit'; if not, pls enter 'go on'");
            if (!"go on".equals(in.nextLine().trim())) {
                System.out.println("Thanks for using the program, bye!");
                break;
            }

            System.out.println("Please input the file name(the original maze layout information):");
            try {
                BufferedReader input = new BufferedReader(new FileReader(in.nextLine().trim()));
                TheMaze maze = new TheMaze(input);
                maze.play();
            } catch (Exception e) {
                System.out.println("The file is bad : " + e.getMessage());
            }
        } while (true);
    }

    private final static int MAZE_LINES = 8;

    private final static int MAZE_COLUMNS = 12;

    private char[][] maze = new char[MAZE_LINES][MAZE_COLUMNS];

    private Point ratPoint, cheesePoint, exitPoint;

    private TheMaze(BufferedReader input) throws IOException {
        for (int i = 0; i < MAZE_LINES; i++) {
            String word = input.readLine(); //Read the lines.
            if (word == null || word.length() < MAZE_COLUMNS) {
                throw new RuntimeException("Maze layout is not correct.");
            }
            for (int j = 0; j < MAZE_COLUMNS; j++) {
                char c = word.charAt(j); //load the file into the array
                maze[i][j] = c;

                if (c == 'R' || c == 'M') {
                    ratPoint = new Point(i, j);
                } else if (c == 'C') {
                    cheesePoint = new Point(i, j);
                } else if (c == 'X') {
                    exitPoint = new Point(i, j);
                }
            }
        }

        if (ratPoint == null || cheesePoint == null || exitPoint == null) {
            throw new RuntimeException("Maze layout is not correct( rat, cheese and/or exit missed.");
        }
    }

    private void play() {
        Point[] shortestPathToCheese = calcShortestPath(ratPoint, cheesePoint);
        if (shortestPathToCheese == null) {
            return;
        }
        Point[] shortestPathToExit = calcShortestPath(cheesePoint, exitPoint);
        if (shortestPathToExit == null) {
            return;
        }

        for (Point point : shortestPathToCheese) {
            setMazeValue(point, '*');
        }

        for (Point point : shortestPathToExit) {
            setMazeValue(point, '*');
        }

        System.out.println("Rat to Cheese, shortest path is " + (shortestPathToCheese.length + 1) + " steps.");
        System.out.println("From Cheese to Exit, shortest path is " + (shortestPathToExit.length + 1) + " steps.");
        for (int line = 0; line < MAZE_LINES; line++) {
            for (int column = 0; column < MAZE_COLUMNS; column++) {
                System.out.print(maze[line][column]);
            }
            System.out.println();
        }
    }

    private Point[] calcShortestPath(Point from, Point target) {
        findGoal(target, from, 0);
        Point point = getMinSteps(from);
        int stepCountToCheese = getMazeValue(point) - 1;
        if (stepCountToCheese >= ('.' - 1)) {
            System.out.println("Couldn't find a way to Cheese.");
            return null;
        }
        Point[] shortestPathToCheese = new Point[stepCountToCheese];
        shortestPathToCheese[0] = point;
        for (int i = 1; i < stepCountToCheese; i++) {
            shortestPathToCheese[i] = getMinSteps(shortestPathToCheese[i - 1]);
        }
        cleanMaze();
        return shortestPathToCheese;
    }

    private void cleanMaze() {
        for (int line = 0; line < MAZE_LINES; line++) {
            for (int column = 0; column < MAZE_COLUMNS; column++) {
                if (maze[line][column] < '.') {
                    maze[line][column] = '.';
                }
            }
        }
    }

    private Point getMinSteps(Point point) {
        Point up = point.translate(-1, 0);
        Point left = point.translate(0, -1);
        Point down = point.translate(1, 0);
        Point right = point.translate(0, 1);

        int min1 = Math.min(getMazeValue(up), getMazeValue(left));
        int min2 = Math.min(getMazeValue(down), getMazeValue(right));
        int min = Math.min(min1, min2);
        if (min == getMazeValue(up)) {
            return up;
        }
        if (min == getMazeValue(left)) {
            return left;
        }
        if (min == getMazeValue(down)) {
            return down;
        }
        return right;
    }

    private void findGoal(Point from, Point target, int steps) {
        if (from.equals(target) || isOutWall(from)) {
            return;
        }
        char c = getMazeValue(from);
        if (c > '.' && steps > 0) {
            return;
        }

        steps++;
        if (c <= steps) {
            return;
        }
        if (steps > 1) {
            setMazeValue(from, steps);
        }

        findGoal(from.translate(-1, 0), target, steps);
        findGoal(from.translate(0, -1), target, steps);
        findGoal(from.translate(1, 0), target, steps);
        findGoal(from.translate(0, 1), target, steps);
    }

    private void setMazeValue(Point point, int steps) {
        maze[point.line][point.column] = (char) steps;
    }

    private char getMazeValue(Point point) {
        if (isOutWall(point)) {
            return 'Z';
        }
        return maze[point.line][point.column];
    }

    private boolean isOutWall(Point point) {
        if (point.line < 0 || point.line >= MAZE_LINES) {
            return true;
        }
        if (point.column < 0 || point.column >= MAZE_COLUMNS) {
            return true;
        }
        return false;
    }

    static class Point {
        Point(int line, int column) {
            this.line = line;
            this.column = column;
        }

        int line;

        int column;

        Point translate(int dx, int dy) {
            return new Point(line + dx, column + dy);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) {
                return false;
            }
            Point p = (Point) obj;
            if (p.line == this.line && p.column == this.column) {
                return true;
            }
            return false;
        }
    }
}
