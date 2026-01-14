package com.tanxx.physics;

import com.tanxx.entities.Shape;
import java.util.ArrayList;
import java.util.List;

public class SpatialGrid {
    private final int cellSize;
    private final int cols;
    private final int rows;
    private final List<Shape>[][] grid;

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

    public void clear() {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].clear();
            }
        }
    }

    public void addShape(Shape s) {
        int cellX = (int) (s.getCenterX() / cellSize);
        int cellY = (int) (s.getCenterY() / cellSize);

        if (cellX >= 0 && cellX < cols && cellY >= 0 && cellY < rows) {
            grid[cellX][cellY].add(s);
        }
    }

    public void getPotentialCollisions(float x, float y, float radius, float maxObjectSize, List<Shape> result) {
        result.clear();
        int minX = Math.max(0, (int) ((x - radius - maxObjectSize) / cellSize));
        int maxX = Math.min(cols - 1, (int) ((x + radius + maxObjectSize) / cellSize));
        int minY = Math.max(0, (int) ((y - radius - maxObjectSize) / cellSize));
        int maxY = Math.min(rows - 1, (int) ((y + radius + maxObjectSize) / cellSize));

        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                result.addAll(grid[i][j]);
            }
        }
    }
}