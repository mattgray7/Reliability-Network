package Evaluation;

import Graph.*;
import java.util.*;

public class Algorithms {
	
	/**
	 * Performs prims algorithm to compute the MST of the input graph, based on cost
	 * @param input: The graph to create the MST for
	 * @return: Graph 
	 */
    public Graph prims(Graph input) {
        boolean[] visited = new boolean[input.nodes.size()];
        Graph mst = new Graph();
        
        //add all edges of the input graph to the unvisited list of the mst's edges
        for (int i = 0; i < input.edges.size(); i++) {
            mst.remainingEdges.add(input.edges.get(i));
        }
        
        //sort the edges to get the lowest cost first
        sortCostThenReliability(mst.remainingEdges);
        mst.addNode(input.edges.get(0).getFrom());
        visited[input.edges.get(0).getFrom().key] = true;
        
        //perform the algorithm
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
    
    /**
     * Performs the algorithm to create a graph with high reliability and cost less than costConstraint
     * @param input: The input graph with all nodes connected
     * @param costConstraint: The maximum cost of the return graph
     * @return: The final graph
     */
    public Graph buildGraphForCostConstraint(Graph input, double costConstraint){
    	//perform prims to get the MST
    	Graph minCost = prims(input);
        if (minCost.totalCost > costConstraint){
        	System.out.println("Min cost tree is over cost limit");
            return minCost;
        }
        
        //all edges are parallelizable to start since MST doesn't add duplicate edges
        ArrayList<Edge> parallelizableEdges = new ArrayList<Edge>();
        for (int i = 0; i < minCost.edges.size(); i++) {
            parallelizableEdges.add(minCost.edges.get(i));
        }
        
        //sort the parallelizable edges by which edge will help the most if parallelized
        double reliabilityFactor;
        double costChange;
        sortReliabilityOverCost(parallelizableEdges);
        while (!parallelizableEdges.isEmpty()) {
            if ((parallelizableEdges.get(0).isParallelizable()) && 
            	((minCost.totalCost + parallelizableEdges.get(0).getOriginalCost()) <= costConstraint)) {
            	//parallelizing will not put cost over limit, so duplicate edge
                reliabilityFactor = 1 / parallelizableEdges.get(0).getReliability();
                costChange = parallelizableEdges.get(0).getCost() * -1;
                parallelizableEdges.get(0).parallelize();
                costChange += parallelizableEdges.get(0).getCost();
                reliabilityFactor *= parallelizableEdges.get(0).getReliability();
                minCost.totalReliability *= reliabilityFactor;
                minCost.totalCost += costChange;
                insertFirstEdge(parallelizableEdges);
            } else {
            	//parallelizing will put over limit, so dont consider this edge again
                parallelizableEdges.remove(0);
            }
        }
        return minCost;
    }
    
    /**
     * Performs the algorithm to create a graph with low cost and reliability at least as good as reliability Constraint
     * @param input: Input graph with all nodes connected
     * @param reliabilityConstraint: Min reliability of final graph
     * @return: The final graph
     */
    public Graph buildGraphForReliabilityConstraint(Graph input, double reliabilityConstraint) {
        Graph minCost = prims(input);
        if (minCost.totalReliability >= reliabilityConstraint){
        	//Min cost tree meets reliability condition, and is min cost, so were done.
            return minCost;
        }
        
        ArrayList<Edge> parallelizableEdges = new ArrayList<Edge>();
        for (int i = 0; i < minCost.edges.size(); i++) {
            parallelizableEdges.add(minCost.edges.get(i));
        }

        //sort by which edge duplication will increase the reliability the most
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
    
    /**
     * Insert an edge into the rest of the list of edges, based on its reliability improvement ratio
     * @param edges: The list to add edge into, first element is edge to add
     */
    private void insertFirstEdge(ArrayList<Edge> edges) {
        Edge temp = edges.get(0);
        int index = 1;
        while (index < edges.size()) {
        	//sort by (change in R / change in C)
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
    
    /**
     * Recursive method to sort the input array list in place
     * @param input
     * @param left
     * @param right
     * @param costFirst
     */
    private void quickSort(ArrayList<Edge> input, int left, int right, boolean costFirst) {
        int p;
        if (left < right) {
            p = partition(input, left, right, costFirst);
            quickSort(input, left, p - 1, costFirst);
            quickSort(input, p + 1, right, costFirst);
        }
    }
    
    /**
     * Helper method for the quicksort method
     * @param input
     * @param left
     * @param right
     * @param costFirst
     * @return
     */
    private int partition(ArrayList<Edge> input, int left, int right, boolean costFirst) {
        Edge pivot = input.get(right);
        Edge temp;
        
        for (int i = left; i < right; i++) {
            if ((costFirst && costFirstComparison(pivot, input.get(i))) || 
            	(!costFirst && reliabilityChangeComparison(pivot, input.get(i)))) {
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
    
    private void sortCostThenReliability(ArrayList<Edge> input) {
        quickSort(input, 0, input.size() - 1, true);
    }
    
    private void sortReliabilityOverCost(ArrayList<Edge> input) {
        quickSort(input, 0, input.size() - 1, false);
    }
    
    private boolean costFirstComparison(Edge pivot, Edge comparedTo) {
        return (comparedTo.getCost() < pivot.getCost() || 
                (comparedTo.getCost() == pivot.getCost() && comparedTo.getReliability() > pivot.getReliability()));
    }

    private boolean reliabilityChangeComparison(Edge pivot, Edge comparedTo) {
        return (((comparedTo.getReliabilityChange() - 1)/comparedTo.getOriginalCost()) > 
        		 ((pivot.getReliabilityChange() - 1)/pivot.getOriginalCost()));
    }
    
    private void addEdgeAndNode(Graph graph, Node node, Edge edge) {
        graph.addEdge(edge);
        graph.addNode(node);
    }
}
