package walker.pack.classes;

import java.util.PriorityQueue;
import TurtlePackage.PlanNode;

/**
 * Created by s214108503 on 2017/06/22.
 */
public class AStarAlgorithm{
    public PriorityQueue<Cell> open;

    public PlanNode start_node, end_node;

    public final int diagonal_cost= 14;
    public final int v_h_cost = 10;

    public Cell[][] grid;

    public boolean closed[][];

    public AStarAlgorithm(Cell[][] grid) {
        this.grid = grid;
    }

    public void runAStar(){
        open.add(grid[start_node.getX()][start_node.getY()]);

        Cell cur;

        while (true){
            cur = open.poll();
            if(cur == null)
                break;

            closed[cur.getX()][cur.getY()] = true;

            if (cur.equals(grid[end_node.getX()][end_node.getY()]))
                return;

            Cell cell;
            if ((cur.getX() - 1) >= 0){
                cell = grid[cur.getX()-1][cur.getY()-1];
                checkAndUpdateCost(cur, cell, cur.getFinal_cost()+v_h_cost);

                if((cur.getY() - 1) >= 0){
                    cell = grid[cur.getX()-1][cur.getY()-1];
                    checkAndUpdateCost(cur, cell, cur.getFinal_cost()+diagonal_cost);
                }


                if((cur.getY() + 1) <grid[0].length){
                    cell = grid[cur.getX() - 1][cur.getY() + 1];
                    checkAndUpdateCost(cur, cell, cur.getFinal_cost()+diagonal_cost);
                }

            }

            if((cur.getY() - 1) >= 0){
                cell = grid[cur.getX()][cur.getY()- 1];
                checkAndUpdateCost(cur, cell, cur.getFinal_cost()+v_h_cost);
            }


            if((cur.getY() + 1) < grid[0].length){
                cell = grid[cur.getX()][cur.getY() + 1];
                checkAndUpdateCost(cur, cell, cur.getFinal_cost()+v_h_cost);
            }


            if((cur.getX()+ 1)<grid.length){
                cell = grid[cur.getX()+ 1][cur.getY()];
                checkAndUpdateCost(cur, cell, cur.getFinal_cost()+v_h_cost);

                if((cur.getY() - 1) >= 0){
                    cell = grid[cur.getX()+ 1][cur.getY()- 1];
                    checkAndUpdateCost(cur, cell, cur.getFinal_cost()+diagonal_cost);
                }

                if((cur.getY() + 1) <grid[0].length){
                    cell = grid[cur.getX() + 1][cur.getY() + 1];
                    checkAndUpdateCost(cur, cell, cur.getFinal_cost()+diagonal_cost);
                }
            }
        }
    }

    public void setCellAsBlocked(PlanNode cur) {
        grid[cur.getX()][cur.getY()] = null;
    }

    public void setStartingCell(PlanNode start_node){
        this.start_node = start_node;
    }

    public void setEndingCell(PlanNode end_node){
        this.end_node = end_node;
    }

    void checkAndUpdateCost(Cell cur, Cell cell, int cost) {

        if(cell == null || closed[cell.getX()][cell.getY()])
            return;
        int cell_final_cost = cell.getH_cost()+cost;

        boolean isCellInOpen = open.contains(cell);
        if (!isCellInOpen || cell_final_cost < cell.getFinal_cost()){
            cell.setFinal_cost(cell_final_cost);
            cell.setAncestor(cur);
            if(!isCellInOpen)
                open.add(cell);
        }
    }

    public void clear(){
        open.clear();
        start_node = null;
        end_node = null;
        grid = null;
        closed = null;
    }
}
