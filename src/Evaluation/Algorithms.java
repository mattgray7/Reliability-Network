package Evaluation;

import Graph.*;
import java.util.*;

public class Algorithms {
    
    public Algorithms(){
        
    };
    
    public void sortCostThenReliability(ArrayList<Edge> input) {
        quickSort(input, 0, input.size() - 1, true);
    }
    
    public void sortReliabilityThenCost(ArrayList<Edge> input) {
        quickSort(input, 0, input.size() - 1, false);
    }
    
    private void quickSort(ArrayList<Edge> input, int left, int right, boolean costFirst) {
        int p;
        if (left < right) {
            p = partition(input, left, right, costFirst);
            quickSort(input, left, p - 1, costFirst);
            quickSort(input, p + 1, right, costFirst);
        }
    }
    
    private boolean costFirstComparison(Edge pivot, Edge comparedTo) {
        return (comparedTo.getCost() < pivot.getCost() || 
                (comparedTo.getCost() == pivot.getCost() && comparedTo.getReliability() > pivot.getReliability()));
    }
    private boolean reliabilityFirstComparison(Edge pivot, Edge comparedTo) {
        return (comparedTo.getReliability() > pivot.getReliability() || 
                (comparedTo.getReliability() == pivot.getReliability() && comparedTo.getCost() < pivot.getCost()));
    }
    
    private int partition(ArrayList<Edge> input, int left, int right, boolean costFirst) {
        Edge pivot = input.get(right);
        Edge temp;
        
        for (int i = left; i < right; i++) {
            if ((costFirst && costFirstComparison(pivot, input.get(i))) || !costFirst && reliabilityFirstComparison(pivot, input.get(i))) {
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

    public Graph prims(Graph input, boolean minCost) {
        boolean[] visited = new boolean[input.nodes.size()];
        Graph mst = new Graph();
        input.remainingEdges = new ArrayList<Edge>(input.edges);
        
        if (minCost) {
            sortCostThenReliability(input.edges);
        } else {
            sortReliabilityThenCost(input.edges);
        }
        
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
