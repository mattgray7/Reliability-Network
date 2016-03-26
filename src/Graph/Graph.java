package Graph;

import Graph.*;
import java.util.*;

public class Graph {
	public ArrayList<Node> nodes;
	public ArrayList<Edge> edges;
	public double totalCost;
	public double totalReliability;
	public ArrayList<Edge> remainingEdges;
	public boolean complete;
	
	public Graph() {
	    this.nodes = new ArrayList<Node>();
	    this.edges = new ArrayList<Edge>();
	    this.remainingEdges = new ArrayList<Edge>();
	    this.totalCost = 0;
	    this.totalReliability = 1.0;
	    this.complete = false;
	}
	
	/**
	 * Constructor to duplicate a graph from existing graph
	 * @param graph: The source graph
	 */
	public Graph(Graph graph) {
	    this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
        this.remainingEdges = new ArrayList<Edge>();
        for (int i = 0; i < graph.nodes.size(); i++) {
            this.nodes.add(graph.nodes.get(i));
        }
        for (int i = 0; i < graph.edges.size(); i++) {
            this.edges.add(graph.edges.get(i));
        }
        for (int i = 0; i < graph.remainingEdges.size(); i++) {
            this.remainingEdges.add(graph.remainingEdges.get(i));
        }
        this.totalCost = graph.totalCost;
        this.totalReliability = graph.totalReliability;
        this.complete = graph.complete;
	}

	/**
	 * Creates a graph based on the input cost and reliability parameters
	 * @param size: Number of nodes in the graph to create
	 * @param costMatrix: Cost relations
	 * @param relMatrix: Reliability relations
	 */
	public void createGraph(int size, double[][] costMatrix, double[][]relMatrix){
		//add nodes to the graph
		for(int i=0; i < size; i++){
			//using index in matrices as the key for the nodes (using zero indexing)
			this.nodes.add(new Node(i));
		}
		
		//add edges with cost and reliabilities based on input matrices
		int row = 0;
		int col = 0;
		while(row < size){
			while(col < size){
				if(row == col){
					//dont add edge on diagonals, assuming nodes cant have edges to themself
					col++;
					continue;
				}
				Edge newEdge = new Edge(this.nodes.get(row), this.nodes.get(col));
				newEdge.setCost(costMatrix[row][col]);
				newEdge.setReliability(relMatrix[row][col]);
				newEdge.setFrom(this.nodes.get(row));
				newEdge.setTo(this.nodes.get(col));
				edges.add(newEdge);
				col++;
			}
			row++;
			
			//only parse the upper triangle of the matrices, since bottom triangle is duplicate
			col = row;
		}
	}
	
	public void setComplete(boolean complete) {
	    this.complete = complete;
	}
	
	public void addNode(Node newNode) {
	    nodes.add(newNode);
	}
	
	public void addEdge(Edge newEdge) {
	    edges.add(newEdge);
	    totalCost += newEdge.getCost();
	    totalReliability *= newEdge.getReliability();
	}
	
	public void setCost(double cost) {
	    this.totalCost = cost;
	}
	
	public void printGraph() {
	    System.out.println("Edges:");
        for (int i = 0; i < edges.size(); i++) {
            System.out.println("Edge from " + edges.get(i).getFrom().key + " to " + edges.get(i).getTo().key + " with redundancy " + edges.get(i).getRedundancy() + " and cost " + edges.get(i).getCost() + " and reliability " + edges.get(i).getReliability());
        }
        System.out.println("Cost: " + totalCost);
        System.out.println("Reliability: " + totalReliability);
	}
	

}
