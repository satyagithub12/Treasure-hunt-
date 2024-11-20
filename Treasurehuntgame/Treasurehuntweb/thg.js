const GRID_SIZE = 7;
const MAX_MOVES = 30;
const EMPTY = "empty";
const PLAYER = "player";
const TREASURE = "treasure";
const OBSTACLE = "obstacle";

let grid = Array.from({ length: GRID_SIZE }, () => Array(GRID_SIZE).fill(EMPTY));
let player = { x: 0, y: 0 };
let treasure = { x: Math.floor(Math.random() * GRID_SIZE), y: Math.floor(Math.random() * GRID_SIZE) };
let moves = 0;
let gameOver = false;
let algorithmSelected = false;

// Place treasure and obstacles
grid[treasure.x][treasure.y] = TREASURE;
placeObstacles(3);

function placeObstacles(numObstacles) {
    for (let i = 0; i < numObstacles; i++) {
        let x, y;
        do {
            x = Math.floor(Math.random() * GRID_SIZE);
            y = Math.floor(Math.random() * GRID_SIZE);
        } while (grid[x][y] !== EMPTY);
        grid[x][y] = OBSTACLE;
    }
}

// Update grid display
function updateGrid() {
    const gridContainer = document.getElementById("grid");
    gridContainer.innerHTML = "";
    for (let i = 0; i < GRID_SIZE; i++) {
        for (let j = 0; j < GRID_SIZE; j++) {
            const cell = document.createElement("div");
            cell.classList.add("cell", grid[i][j]);
            if (i === player.x && j === player.y) {
                cell.classList.add(PLAYER);
            }
            gridContainer.appendChild(cell);
        }
    }
    document.getElementById("moves").textContent = `Moves Left: ${MAX_MOVES - moves}`;
}

document.getElementById("bfs").addEventListener("click", () => setAlgorithm("BFS"));
document.getElementById("dfs").addEventListener("click", () => setAlgorithm("DFS"));

function setAlgorithm(algorithm) {
    document.getElementById("distance").textContent = `Algorithm set to ${algorithm}`;
    algorithmSelected = true;
}

// Handle keyboard movement
document.addEventListener("keydown", (event) => {
    if (!algorithmSelected || gameOver || moves >= MAX_MOVES) return;
    let { x, y } = player;
    if (event.key === "ArrowUp" && x > 0) x--;
    else if (event.key === "ArrowDown" && x < GRID_SIZE - 1) x++;
    else if (event.key === "ArrowLeft" && y > 0) y--;
    else if (event.key === "ArrowRight" && y < GRID_SIZE - 1) y++;

    if (grid[x][y] !== OBSTACLE) {
        player.x = x;
        player.y = y;
        moves++;
        if (x === treasure.x && y === treasure.y) {
            gameOver = true;
            document.getElementById("status").textContent = "Congratulations! You found the treasure!";
        } else if (moves >= MAX_MOVES) {
            document.getElementById("status").textContent = "Out of moves! Game over.";
            gameOver = true;
        }
        updateGrid();
    } else {
        document.getElementById("status").textContent = "Invalid move. Hit a wall or obstacle.";
    }
});

updateGrid();