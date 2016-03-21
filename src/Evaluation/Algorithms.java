package Evaluation;

import Graph.*;
import java.util.*;

public class Algorithms {
    
    public Algorithms(){
        
    };
    
    private void sortCostThenReliability(ArrayList<Edge> input) {
        quickSort(input, 0, input.size() - 1, true);
    }
    
    private void sortReliabilityThenCost(ArrayList<Edge> input) {
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
    
    public Graph constrainedMinCost(Graph input, double reliabilityConstraint) {
        Graph cmst = new Graph();
        boolean[] visited = new boolean[input.nodes.size()];
        sortCostThenReliability(input.edges);
        cmst.addNode(input.edges.get(0).getFrom());
        visited[input.edges.get(0).getFrom().key] = true;
        cmst = constrainedMinCostHelper(input.edges, cmst, visited, reliabilityConstraint);
        return cmst;
    }
    
    public Graph constrainedMinCostHelper(ArrayList<Edge> sortedSet, Graph current, boolean[] visited, double reliabilityConstraint) {
        if (current == null) {
            return null;
        }

        Graph currentWithEdge = new Graph(current);
        if (allNodesReached(visited) && current.totalReliability >= reliabilityConstraint) {
            current.setComplete(true);
            return current;
        }
        
        if (sortedSet.isEmpty() || current.totalReliability < reliabilityConstraint) {
            return null;
        }
        
        Edge nextEdge = sortedSet.remove(0);
        ArrayList<Edge> sortedSetWithEdge = new ArrayList<Edge>();
        for (int i = 0; i < sortedSet.size(); i++) {
            sortedSetWithEdge.add(sortedSet.get(i));
        }
        boolean[] visitedWithEdge = new boolean[visited.length];
        for (int i = 0; i < visited.length; i++) {
            visitedWithEdge[i] = visited[i];
        }

        if (visitedWithEdge[nextEdge.getTo().key] != true) {
            addEdgeAndNode(currentWithEdge, nextEdge.getTo(), nextEdge);
            visitedWithEdge[nextEdge.getTo().key] = true;
        } else if (visitedWithEdge[nextEdge.getFrom().key] != true) {
            addEdgeAndNode(currentWithEdge, nextEdge.getFrom(), nextEdge);
            visitedWithEdge[nextEdge.getFrom().key] = true;
        }
        
        currentWithEdge = constrainedMinCostHelper(sortedSetWithEdge, currentWithEdge, visitedWithEdge, reliabilityConstraint);
        current = constrainedMinCostHelper(sortedSet, current, visited, reliabilityConstraint);
        
        if (current != null && currentWithEdge != null) {
            if (current.totalCost < currentWithEdge.totalCost) {
                return current;
            } else {
                return currentWithEdge;
            }
        } else if (current != null) {
            return current;
        } else {
            return currentWithEdge;
        }
    }

    public Graph prims(Graph input, boolean minCost) {
        boolean[] visited = new boolean[input.nodes.size()];
        Graph mst = new Graph();
        ArrayList<Edge> sortedSet = new ArrayList<Edge>();
        for (int i = 0; i < input.edges.size(); i++) {
            sortedSet.add(input.edges.get(i));
        }
        
        if (minCost) {
            sortCostThenReliability(sortedSet);
        } else {
            sortReliabilityThenCost(sortedSet);
        }
        mst.addNode(input.edges.get(0).getFrom());
        visited[input.edges.get(0).getFrom().key] = true;
        
        Edge nextEdge;
        while(mst.nodes.size() < input.nodes.size()) {
            for (int i = 0; i < sortedSet.size(); i++) {
                nextEdge = sortedSet.get(i);
                if (visited[nextEdge.getFrom().key] && !visited[nextEdge.getTo().key]) {
                    addEdgeAndNode(mst, nextEdge.getTo(), nextEdge);
                    visited[nextEdge.getTo().key] = true;
                    sortedSet.remove(i);
                    break;
                } else if (visited[nextEdge.getTo().key] && !visited[nextEdge.getFrom().key]) {
                    addEdgeAndNode(mst, nextEdge.getFrom(), nextEdge);
                    visited[nextEdge.getFrom().key] = true;
                    sortedSet.remove(i);
                    break;
                }
            }
            
        }
        mst.complete = true;
        return mst;
    }
    
    private void addEdgeAndNode(Graph graph, Node node, Edge edge) {
        graph.addEdge(edge);
        graph.addNode(node);
    }
    
    private boolean allNodesReached(boolean[] visited) {
        for (int i = 0; i < visited.length; i++) {
            if (visited[i] == false) {
                return false; 
            }
        }
        return true;
    }
}
