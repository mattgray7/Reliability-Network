package Graph;

import Graph.Node;

public class Edge {
    private Node from;
    private Node to;
    private double cost;
    private double reliability;
    
    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }
    
    public void setCost(double cost){
    	this.cost = cost;
    }
    
    public double getCost(){
    	return this.cost;
    }
    
    public void setReliability(double reliability){
    	this.reliability = reliability;
    }
    
    public double getReliability(){
    	return this.reliability;
    }
    
    public Node getFrom() {
        return from;
    }
    
    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }
    
    public double get2ParallelCost(){
    	return 2*cost;
    }
    
    public double get3ParallelCost(){
    	return 3*cost;
    }
    
    public double get2ParallelReliability(){
    	return (1-((1-reliability)*(1-reliability)));
    }
    
    public double get3ParallelReliability(){
    	return (1-((1-reliability)*(1-reliability)*(1-reliability)));
    }
    
    public double getRCRatio(){
    	return reliability/cost;
    }
}
