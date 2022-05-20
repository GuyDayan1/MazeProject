
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Maze extends JFrame {

    private int[][] values;
    private boolean[][] visited;
    private int startRow;
    private int startColumn;
    private ArrayList<JButton> buttonList;
    private int rows;
    private int columns;
    private boolean backtracking;
    private int algorithm;
    private boolean solve= false;

    public Maze(int algorithm, int size, int startRow, int startColumn) {
        this.algorithm = algorithm;
        Random random = new Random();
        this.values = new int[size][];
        for (int i = 0; i < values.length; i++) {
            int[] row = new int[size];
            for (int j = 0; j < row.length; j++) {
                if (i > 1 || j > 1) {
                    row[j] = random.nextInt(8) % 7 == 0 ? Definitions.OBSTACLE : Definitions.EMPTY;
                } else {
                    row[j] = Definitions.EMPTY;
                }
            }
            values[i] = row;
        }
        values[0][0] = Definitions.EMPTY;
        values[size - 1][size - 1] = Definitions.EMPTY;
        this.visited = new boolean[this.values.length][this.values.length];
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.buttonList = new ArrayList<>();
        this.rows = values.length;
        this.columns = values.length;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setLocationRelativeTo(null);
        GridLayout gridLayout = new GridLayout(rows, columns);
        this.setLayout(gridLayout);
        for (int i = 0; i < rows * columns; i++) {
            int value = values[i / rows][i % columns];
            JButton jButton = new JButton(String.valueOf(i));
            if (value == Definitions.OBSTACLE) {
                jButton.setBackground(Color.BLACK);
            } else {
                jButton.setBackground(Color.WHITE);
            }
            this.buttonList.add(jButton);
            this.add(jButton);
        }
        this.setVisible(true);
        this.setSize(Definitions.WINDOW_WIDTH, Definitions.WINDOW_HEIGHT);
        this.setResizable(false);
    }

    public void checkWayOut() {
        new Thread(() -> {
            boolean result = false;
            switch (this.algorithm) {
                case Definitions.ALGORITHM_BRUTE_FORCE:
                    break;
                case Definitions.ALGORITHM_DFS:
                    dfs(new Vertex(this.startRow,this.startColumn));
                    result = solve;
                    break;
                case Definitions.ALGORITHM_BFS:
                    break;
            }
            JOptionPane.showMessageDialog(null, result ? "FOUND SOLUTION" : "NO SOLUTION FOR THIS MAZE");

        }).start();
    }

    public void dfs(Vertex currentVertex) {
        if (solve) {
            return;
        }
            if (!isVisited(currentVertex)) {
                setSquareAsVisited(currentVertex.getRow(), currentVertex.getColumn(), true);
                this.visited[currentVertex.getRow()][currentVertex.getColumn()] = true;
                if (currentVertex.getRow() == this.values.length - 1 && currentVertex.getColumn() == this.values.length - 1) {
                    solve =true;
                }
                for (Vertex nextVertex : getNeighbors(currentVertex)){
                    if (!isVisited(nextVertex)&&(checkIfEmpty(nextVertex))) {
                        dfs(nextVertex);
                    }
                }
        }
            if (!solve) {
                setSquareAsVisited(currentVertex.getRow(), currentVertex.getColumn(), false);
            }
    }

    public boolean isVisited(Vertex vertex){
        return this.visited[vertex.getRow()][vertex.getColumn()];
    }
    public List<Vertex>getNeighbors(Vertex currentVertex){
        List<Vertex> neighbors = new ArrayList<>();
        neighbors.add(new Vertex(currentVertex.getRow()+1,currentVertex.getColumn())); // down
        neighbors.add(new Vertex(currentVertex.getRow(),currentVertex.getColumn()-1)); // left
        neighbors.add(new Vertex(currentVertex.getRow(),currentVertex.getColumn()+1)); // right
        neighbors.add(new Vertex(currentVertex.getRow()-1,currentVertex.getColumn())); // up
        checkBounds(neighbors);
        return neighbors;
    }
    public void checkBounds(List<Vertex>neighbors){     // size = 3  row<=0 row>=2
        List<Vertex> copyList = new ArrayList<>(neighbors); // copy
        for (Vertex vertex : copyList){
            if ((vertex.getRow()<0) || (vertex.getRow()>this.values.length-1) || (vertex.getColumn()<0) ||
                    (vertex.getColumn()>this.values.length-1)){
                neighbors.remove(vertex);
            }
        }
    }
    public boolean checkIfEmpty(Vertex vertex){
        return (this.values[vertex.getRow()][vertex.getColumn()] == Definitions.EMPTY);
    }

    public void setSquareAsVisited(int x, int y, boolean visited) {
        try {
            if (visited) {
                if (this.backtracking) {
                    Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE * 5);
                    this.backtracking = false;
                }
                this.visited[x][y] = true;
                for (int i = 0; i < this.visited.length; i++) {
                    for (int j = 0; j < this.visited[i].length; j++) {
                        if (this.visited[i][j]) {
                            if (i == x && y == j) {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.RED);
                            } else {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.BLUE);
                            }
                        }
                    }
                }
            } else {
                this.visited[x][y] = false;
                this.buttonList.get(x * this.columns + y).setBackground(Color.WHITE);
                Thread.sleep(Definitions.PAUSE_BEFORE_BACKTRACK);
                this.backtracking = true;
            }
            if (!visited) {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE / 4);
            } else {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
