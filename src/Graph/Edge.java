package Graph;

import Graph.Node;
import java.util.*;

public class Edge {
    private Node from;
    private Node to;
    private double cost;
    private double reliability;
    private int redundancy;
    
    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
        this.redundancy = 1;
    }
    
    public boolean isParallelizable() {
        return redundancy < 3;
    }
    
    public void parallelize() {
        redundancy++;
    }
    
    public int getRedundancy() {
        return redundancy;
    }
    
    public void setCost(double cost){
    	this.cost = cost;
    }
    
    public double getCost(){
    	return this.cost * this.redundancy;
    }
    
    public double getOriginalCost() {
        return this.cost;
    }
    
    public void setReliability(double reliability){
    	this.reliability = reliability;
    }
    
    public double getReliability(){        
        return 1 - Math.pow((1 - this.reliability), this.redundancy);
    }
    
    public double getRCRatio(){
    	return reliability/cost;
    }
    
    public double getReliabilityChange() {
        return ((1 - Math.pow((1 - this.reliability), this.redundancy + 1))/(1 - Math.pow((1 - this.reliability), this.redundancy)));
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
}
