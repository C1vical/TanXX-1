import java.util.ArrayList;
import java.util.List;

// SpatialGrid is used for optimized collision detection
// It divides the world into a grid of cells, so we only check collisions between entities in nearby cells
public class SpatialGrid {
    private final int cellSize;
    private final int cols;
    private final int rows;
    private final List<Shape>[][] grid;

    // Initializes spatial grid with the given world dimensions and cell size
    @SuppressWarnings("unchecked")
    public SpatialGrid(int worldW, int worldH, int cellSize) {
        this.cellSize = cellSize;
        this.cols = (int) Math.ceil((double) worldW / cellSize);
        this.rows = (int) Math.ceil((double) worldH / cellSize);
        this.grid = new ArrayList[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j] = new ArrayList<>();
            }
        }
    }

    // Clear all cells in the grid (called every frame)
    public void clear() {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].clear();
            }
        }
    }

    // Add a shape to the grid based on its center position
    public void addShape(Shape s) {
        int cellX = (int) (s.getCenterX() / cellSize);
        int cellY = (int) (s.getCenterY() / cellSize);

        if (cellX >= 0 && cellX < cols && cellY >= 0 && cellY < rows) {
            grid[cellX][cellY].add(s);
        }
    }

    // Returns a list of potential shapes that could be colliding with a circle at (x,y)
    public void getPotentialCollisions(float x, float y, float radius, float maxObjectSize, List<Shape> result) {
        result.clear();
        // Calculate the range of cells to check based on the object's radius and the maximum possible size of other shapes
        int minX = Math.max(0, (int) ((x - radius - maxObjectSize) / cellSize));
        int maxX = Math.min(cols - 1, (int) ((x + radius + maxObjectSize) / cellSize));
        int minY = Math.max(0, (int) ((y - radius - maxObjectSize) / cellSize));
        int maxY = Math.min(rows - 1, (int) ((y + radius + maxObjectSize) / cellSize));

        // Add all shapes from the calculated cells to the result list
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                result.addAll(grid[i][j]);
            }
        }
    }
}