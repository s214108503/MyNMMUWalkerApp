package walker.pack.comparators;

import java.util.Comparator;

import walker.pack.classes.Cell;

/**
 * Created by s214108503 on 2017/06/22.
 */
public class CellComparator implements Comparator<Cell> {

    @Override
    public int compare(Cell o1, Cell o2) {
        return o1.getFinal_cost() < o2.getFinal_cost()?-1:
        o1.getFinal_cost()>o2.getFinal_cost()?1:0;
    }
}
