package walker.pack.classes;

import java.io.Serializable;

/**
 * Created by Olebogeng Malope on 2017/06/21.
 */
public class PlanNode implements Serializable {

    public int X, Y;
    String Node_Type, Node_ID;

    public PlanNode(int x, int y, String node_Type) {
        X = x;
        Y = y;
        Node_Type = node_Type;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public String getNode_Type() {
        return Node_Type;
    }

    public void setNode_Type(String node_Type) {
        Node_Type = node_Type;
    }

    public String getNode_ID() {
        return Node_ID;
    }

    public void setNode_ID(String node_ID) {
        Node_ID = node_ID;
    }

    @Override
    public String toString() {
        return "PlanNode{" +
                "X=" + X +
                ", Y=" + Y +
                ", Node_Type='" + Node_Type + '\'' +
                ", Node_ID='" + Node_ID + '\'' +
                '}';
    }
}
