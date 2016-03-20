package Graph;

import java.util.ArrayList;

public class Node{
    private int key;
    private ArrayList<Edge> connectedEdges = new ArrayList<Edge>();

    public Node(int key){
    	this.key = key;
    }
    
    public void addEdge(Edge e){
    	connectedEdges.add(e);
    }
}
