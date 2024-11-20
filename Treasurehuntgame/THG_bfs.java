import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class THG_bfs extends JFrame {
    private static final int GRID_SIZE = 10;
    private static final int MAX_MOVES = 15;
    private static final char EMPTY = ' ';
    private static final char PLAYER = 'P';
    private static final char TREASURE = 'T';
    private static final char OBSTACLE = 'O';

    private char[][] grid = new char[GRID_SIZE][GRID_SIZE];
    private Player player = new Player(0, 0);
    private int treasureX, treasureY;
    private int moves = 0;
    private boolean gameOver = false;

    private JLabel[][] gridLabels;
    private JLabel statusLabel, movesLabel, distanceLabel;
    private int obstacleHits = 0;
    private Timer blinkTimer, fastBlinkTimer;
    private boolean playerVisible = true;

    static class Player {
        int x, y;
        Player(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public THG_bfs() {
        setTitle("Treasure Hunt Game");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        initGrid();
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridLabels = new JLabel[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridLabels[i][j] = new JLabel("", SwingConstants.CENTER);
                gridLabels[i][j].setOpaque(true);
                gridLabels[i][j].setBackground(Color.DARK_GRAY);
                gridLabels[i][j].setForeground(Color.WHITE);
                gridLabels[i][j].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                gridPanel.add(gridLabels[i][j]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(3, 1));
        controlPanel.setBackground(Color.BLACK);
        statusLabel = new JLabel("Welcome to the Treasure Hunt Game!", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        movesLabel = new JLabel("Moves Left: " + (MAX_MOVES - moves), SwingConstants.CENTER);
        movesLabel.setForeground(Color.WHITE);
        distanceLabel = new JLabel("Move around to find the treasure", SwingConstants.CENTER);
        distanceLabel.setForeground(Color.WHITE);

        controlPanel.add(statusLabel);
        controlPanel.add(movesLabel);
        controlPanel.add(distanceLabel);

        add(controlPanel, BorderLayout.SOUTH);
        gridPanel.setFocusable(true);
        gridPanel.requestFocusInWindow();
        gridPanel.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                handleMove(e);
            }
        });
        gridPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                gridPanel.requestFocusInWindow();
            }
        });

        updateGrid();
        startBlinkingAnimation();
    }

    private void initGrid() {
        Random rand = new Random();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = EMPTY;
            }
        }
        treasureX = rand.nextInt(GRID_SIZE);
        treasureY = rand.nextInt(GRID_SIZE);
        grid[treasureX][treasureY] = TREASURE;
        placeObstacles(GRID_SIZE);
    }

    private void placeObstacles(int numObstacles) {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == EMPTY) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        Random rand = new Random();
        for (int i = 0; i < numObstacles && !emptyCells.isEmpty(); i++) {
            int index = rand.nextInt(emptyCells.size());
            int[] cell = emptyCells.get(index);
            grid[cell[0]][cell[1]] = OBSTACLE;
            emptyCells.remove(index);
        }
    }

    private void updateGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (i == player.x && j == player.y && playerVisible) {
                    gridLabels[i][j].setText(String.valueOf(PLAYER));
                    gridLabels[i][j].setBackground(Color.GREEN);
                } else {
                    gridLabels[i][j].setText(String.valueOf(grid[i][j]));
                    if (grid[i][j] == TREASURE) {
                        gridLabels[i][j].setBackground(Color.YELLOW);
                    } else if (grid[i][j] == OBSTACLE) {
                        gridLabels[i][j].setBackground(Color.RED);
                    } else {
                        gridLabels[i][j].setBackground(Color.DARK_GRAY);
                    }
                }
            }
        }
        movesLabel.setText("Moves Left: " + (MAX_MOVES - moves));
    }

    private void handleMove(KeyEvent e) {
        if (gameOver || moves >= MAX_MOVES) {
            statusLabel.setText("Game over. No more moves allowed.");
            stopBlinkingAnimation();
            return;
        }

        int newX = player.x;
        int newY = player.y;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP:    newX--; break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN:  newX++; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:  newY--; break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: newY++; break;
            default: return;
        }

        if (newX >= 0 && newX < GRID_SIZE && newY >= 0 && newY < GRID_SIZE && grid[newX][newY] != OBSTACLE) {
            player.x = newX;
            player.y = newY;
            moves++;

            if (moves == 1) { // Stop blinking after the first move
                stopBlinkingAnimation();
            }

            if (player.x == treasureX && player.y == treasureY) {
                gameOver = true;
                stopBlinkingAnimation(); // Stop normal blinking
                startFastBlinkingAnimation();
                statusLabel.setText("Congratulations! You found the treasure in " + moves + " moves.");
                hideControls();
            } else if (moves >= MAX_MOVES) {
                statusLabel.setText("Out of moves! Game over.");
                gameOver = true;
            } else {
                distanceLabel.setText("Move around to find the treasure");
            }
            obstacleHits = 0;
        } else {
            obstacleHits++;
            if (obstacleHits < 3) {
                statusLabel.setText("You hit an obstacle! (" + obstacleHits + "/3)");
            } else {
                statusLabel.setText("You hit obstacles too many times! No more moves allowed.");
                gameOver = true;
            }
        }

        updateGrid();
    }

    private void startBlinkingAnimation() {
        blinkTimer = new Timer(500, e -> {
            playerVisible = !playerVisible;
            updateGrid();
        });
        blinkTimer.start();
    }

    private void stopBlinkingAnimation() {
        if (blinkTimer != null) {
            blinkTimer.stop();
            playerVisible = true;
        }
        if (fastBlinkTimer != null) {
            fastBlinkTimer.stop();
            playerVisible = true;
        }
    }

    private void startFastBlinkingAnimation() {
        fastBlinkTimer = new Timer(100, e -> {
            playerVisible = !playerVisible;
            updateGrid();
        });
        fastBlinkTimer.start();
    }

    private void hideControls() {
        movesLabel.setVisible(false);
        distanceLabel.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            THG_bfs frame = new THG_bfs();
            frame.setVisible(true);
        });
    }
}
