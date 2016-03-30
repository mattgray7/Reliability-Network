package Evaluation;

import Graph.*;
import java.util.*;

public class Algorithms {

    public Algorithms(){};
    
    private void sortCostThenReliability(ArrayList<Edge> input) {
        quickSort(input, 0, input.size() - 1, true);
    }
    
    private void sortReliabilityThenCost(ArrayList<Edge> input) {
        quickSort(input, 0, input.size() - 1, false);
    }
    
    private void sortReliabilityOverCost(ArrayList<Edge> input) {
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
            if ((input.get(i).getReliabilityChange() - 1)/input.get(i).getOriginalCost() > (pivot.getReliabilityChange() - 1)/pivot.getOriginalCost()) {
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
    
    private void insertFirstEdge(ArrayList<Edge> edges) {
        Edge temp = edges.get(0);
        int index = 1;
        while (index < edges.size()) {
            if ((temp.getReliabilityChange() - 1)/temp.getOriginalCost() < (edges.get(index).getReliabilityChange() - 1)/edges.get(index).getOriginalCost()) {
                edges.set(index - 1, edges.get(index));
            } else {
                edges.set(index - 1, temp);
                return;
            }
            index++;
        }
        edges.set(index - 1, temp);
    }

    private void printEdges(ArrayList<Edge> edges) {
        for (int i = 0; i < edges.size(); i++) {
            System.out.println("Edge from " + edges.get(i).getFrom().key + " to " + edges.get(i).getTo().key + " with redundancy " + edges.get(i).getRedundancy() + " and cost " + edges.get(i).getCost() + " and reliability " + edges.get(i).getReliability() + " and ratio of " + (edges.get(i).getReliabilityChange() - 1)/edges.get(i).getOriginalCost());
        }
        System.out.println();
    }
    public Graph prims(Graph input, boolean minCost) {
        boolean[] visited = new boolean[input.nodes.size()];
        Graph mst = new Graph();
        for (int i = 0; i < input.edges.size(); i++) {
            mst.remainingEdges.add(input.edges.get(i));
        }
        
        sortCostThenReliability(mst.remainingEdges);
        mst.addNode(input.edges.get(0).getFrom());
        visited[input.edges.get(0).getFrom().key] = true;
        
        Edge nextEdge;
        while(mst.nodes.size() < input.nodes.size()) {
            for (int i = 0; i < mst.remainingEdges.size(); i++) {
                nextEdge = mst.remainingEdges.get(i);
                if (visited[nextEdge.getFrom().key] && !visited[nextEdge.getTo().key]) {
                    addEdgeAndNode(mst, nextEdge.getTo(), nextEdge);
                    visited[nextEdge.getTo().key] = true;
                    mst.remainingEdges.remove(i);
                    break;
                } else if (visited[nextEdge.getTo().key] && !visited[nextEdge.getFrom().key]) {
                    addEdgeAndNode(mst, nextEdge.getFrom(), nextEdge);
                    visited[nextEdge.getFrom().key] = true;
                    mst.remainingEdges.remove(i);
                    break;
                }
            }
            
        }
        mst.complete = true;
        return mst;
    }
    
    public Graph augmentToCostConstraint(Graph input, double costConstraint){
    	Graph minCost = prims(input, true);
        if (minCost.totalCost > costConstraint){
        	System.out.println("Min cost tree is over cost limit");
            return minCost;
        }
        
        ArrayList<Edge> parallelizableEdges = new ArrayList<Edge>();
        for (int i = 0; i < minCost.edges.size(); i++) {
            parallelizableEdges.add(minCost.edges.get(i));
        }
        
        double reliabilityFactor;
        double costChange;
        sortReliabilityOverCost(parallelizableEdges);
        while (!parallelizableEdges.isEmpty()) {
            if ((parallelizableEdges.get(0).isParallelizable()) && 
            	((minCost.totalCost + parallelizableEdges.get(0).getOriginalCost()) <= costConstraint)) {
                reliabilityFactor = 1 / parallelizableEdges.get(0).getReliability();
                costChange = parallelizableEdges.get(0).getCost() * -1;
                parallelizableEdges.get(0).parallelize();
                costChange += parallelizableEdges.get(0).getCost();
                reliabilityFactor *= parallelizableEdges.get(0).getReliability();
                minCost.totalReliability *= reliabilityFactor;
                minCost.totalCost += costChange;
                insertFirstEdge(parallelizableEdges);
            } else {
                parallelizableEdges.remove(0);
            }
        }
        return minCost;
    	
    }
    
    public Graph augmentToReliabilityConstraint(Graph input, double reliabilityConstraint) {
        Graph minCost = prims(input, true);
        if (minCost.totalReliability >= reliabilityConstraint){
            return minCost;
        }
        ArrayList<Edge> parallelizableEdges = new ArrayList<Edge>();
        for (int i = 0; i < minCost.edges.size(); i++) {
            parallelizableEdges.add(minCost.edges.get(i));
        }

        double reliabilityFactor;
        double costChange;
        sortReliabilityOverCost(parallelizableEdges);
        while (minCost.totalReliability < reliabilityConstraint && !parallelizableEdges.isEmpty()) {
            if (parallelizableEdges.get(0).isParallelizable()) {
                reliabilityFactor = 1 / parallelizableEdges.get(0).getReliability();
                costChange = parallelizableEdges.get(0).getCost() * -1;
                parallelizableEdges.get(0).parallelize();
                costChange += parallelizableEdges.get(0).getCost();
                reliabilityFactor *= parallelizableEdges.get(0).getReliability();
                minCost.totalReliability *= reliabilityFactor;
                minCost.totalCost += costChange;
                insertFirstEdge(parallelizableEdges);
            } else {
                parallelizableEdges.remove(0);
            }
        }
        
        return minCost;
    }
    
    private void addEdgeAndNode(Graph graph, Node node, Edge edge) {
        graph.addEdge(edge);
        graph.addNode(node);
    }
}
