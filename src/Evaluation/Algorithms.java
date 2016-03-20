package Evaluation;

import Graph.*;
import java.util.*;

public class Algorithms {
    
    public Algorithms(){
        
    };
    
    public void sortCostThenReliability(ArrayList<Edge> input) {
        quickSort(input, 0, input.size() - 1);
    }
    
    private void quickSort(ArrayList<Edge> input, int left, int right) {
        int p;
        if (left < right) {
            p = partition(input, left, right);
            quickSort(input, left, p - 1);
            quickSort(input, p + 1, right);
        }
    }
    
    private int partition(ArrayList<Edge> input, int left, int right) {
        Edge pivot = input.get(right);
        Edge temp;
        
        for (int i = left; i < right; i++) {
            if (input.get(i).getCost() < pivot.getCost() || 
                       (input.get(i).getCost() == pivot.getCost() && input.get(i).getReliability() > pivot.getReliability())) {
                temp = input.get(i);
                input.set(i, input.get(left));
                input.set(left, temp);
                left++;
            }
        }
        
        temp = input.get(left);
        input.set(left, input.get(right));
        input.set(right, temp);
        return left;
    }

    public Graph primsMinCost(Graph input) {
        boolean[] visited = new boolean[input.nodes.size()];
        Graph mst = new Graph();
        input.remainingEdges = new ArrayList<Edge>(input.edges);
        
        sortCostThenReliability(input.edges);
        
        mst.addNode(input.edges.get(0).getFrom());
        visited[input.edges.get(0).getFrom().key] = true;
        
        Edge nextEdge;
        for (int i = 0; i < input.edges.size(); i++) {
            nextEdge = input.edges.get(i);
            if (visited[nextEdge.getTo().key] != true) {
                addEdgeAndNode(mst, nextEdge.getTo(), nextEdge);
                input.remainingEdges.remove(nextEdge);
                visited[nextEdge.getTo().key] = true;

                if (allNodesReached(visited)) {
                    break;
                }
                continue;
            } else if (visited[nextEdge.getFrom().key] != true){
                addEdgeAndNode(mst, nextEdge.getFrom(), nextEdge);
                input.remainingEdges.remove(nextEdge);
                visited[nextEdge.getFrom().key] = true;

                if (allNodesReached(visited)) {
                    break;
                }
                continue;
            }
        }
        return mst;
    }
    
    public void addEdgeAndNode(Graph graph, Node node, Edge edge) {
        graph.addEdge(edge);
        graph.addNode(node);
    }
    
    public Graph primsMaxReliability(Graph input) {
        return input;
    }
    
    public boolean allNodesReached(boolean[] visited) {
        for (int i = 0; i < visited.length; i++) {
            if (visited[i] == false) {
                return false; 
            }
        }
        return true;
    }
}
