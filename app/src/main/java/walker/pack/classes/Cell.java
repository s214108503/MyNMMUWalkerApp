package walker.pack.classes;

import java.io.Serializable;

/**
 * Created by s214108503 on 2017/06/24.
 */

public class Cell implements Serializable {
    private int h_cost = 0;
    private int final_cost = 0;
    private int x, y;
    private Cell ancestor;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public int getH_cost() {
        return h_cost;
    }

    public void setH_cost(int h_cost) {
        this.h_cost = h_cost;
    }

    public int getFinal_cost() {
        return final_cost;
    }

    public void setFinal_cost(int final_cost) {
        this.final_cost = final_cost;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Cell getAncestor() {
        return ancestor;
    }

    public void setAncestor(Cell ancestor) {
        this.ancestor = ancestor;
    }
}
