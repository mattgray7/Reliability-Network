package Graph;

import Graph.Node;
import Graph.Edge;

public class Graph {
	public Node[] nodes;
	
	public void createGraph(int size, double[][] costMatrix, double[][]relMatrix){
		this.nodes = new Node[size];
		for(int i=0; i < size; i++){
			//using index in matrices as the key for the nodes (using zero indexing)
			//ie cost at index 4,7 is the cost of bidirectional edge from node 4 to node 7
			this.nodes[i] = new Node(i);
		}
		
		int row = 0;
		int col = 0;
		while(row < size){
			while(col < size){
				if(row == col){
					col++;
					continue;
				}
				Edge newEdge = new Edge(this.nodes[row], this.nodes[col]);
				newEdge.setCost(costMatrix[row][col]);
				newEdge.setReliability(relMatrix[row][col]);
				this.nodes[row].addEdge(newEdge);
				this.nodes[col].addEdge(newEdge);
				System.out.println("Adding edge from node" + row + " to node" + col + " with R=" + relMatrix[row][col] + " and C=" + costMatrix[row][col]);
				col++;
			}
			row++;
			col = row;
		}
	}

	/*public void createGraph(){
		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		ArrayList<Node> tempNodes = new ArrayList<Node>();
		ArrayList<Edge> tempEdges = new ArrayList<Edge>();
		
		//add node objects
		File cityFile = new File("./src/Cities.txt");
		try (BufferedReader br = new BufferedReader(new FileReader(cityFile))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        String[] elements = line.split(" ");
		        List<Integer> edgeArrayList = new ArrayList<>();
		        
		        //cities with spaces (New York, Quebec City) have ? instead of ' ' in text file
		        String nodeName = elements[1].replace("?", " ");
		        
		        //add the integer keys of nodes connected
		        for(int i=4; i < elements.length; i++){
		        	edgeArrayList.add(Integer.parseInt(elements[i]));
		        }
		        
		        //convert array list to list to store in node
		        Integer[] edgeList = new Integer[edgeArrayList.size()];
		        edgeList = edgeArrayList.toArray(edgeList);
		        Node temp = new Node(Integer.parseInt(elements[0]), nodeName, 
		        					 Double.parseDouble(elements[2]), Double.parseDouble(elements[3]),
		        					 edgeList);
		        
		        //eventual nodes variable, needs to be array list during generation
		        tempNodes.add(temp);
		        
		        //dict of node keys to node objects, to add edges later
		        nodeMap.put(Integer.parseInt(elements[0]), temp);
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//store in class variable
		this.nodes = new Node[tempNodes.size()];
		this.nodes = tempNodes.toArray(this.nodes);
		
		int linkSpeed = 40000000;
		
		//once all node objects created, can add edge objects
		for(int i=0; i < this.nodes.length; i++){
			//get the key of neighboring nodes
			Integer[] currentNodeNeighbors = this.nodes[i].getEdgeKeys();
			Node currentNode = this.nodes[i];
			for(int j=0; j < currentNodeNeighbors.length; j++){
				//look in the hashmap to get the Node associated with the key
				Node destNode = nodeMap.get(currentNodeNeighbors[j]);
				
				//create the edge, add to temp edges list, and add to the node
				Edge tempEdge = new Edge(currentNode, destNode, linkSpeed);
				tempEdges.add(tempEdge);
				currentNode.addEdgeObject(tempEdge);
			}
		}
		
		//save the temp edges list in the class variable
		this.edges = new Edge[tempEdges.size()];
		this.edges = tempEdges.toArray(this.edges);
		
		//print what edges are connected to each node
		/*
		for(int i=0; i < this.nodes.length; i++){
			Node n = this.nodes[i];
			Edge[] tempEdgeList = n.getEdgeObjects();
			System.out.println("Node " + n.getName() + ":");
			for(int j=0; j < tempEdgeList.length; j++){
				System.out.println("Edge to " + tempEdgeList[j].getTo().getName());
			}
			System.out.println("\n");
		}*/
	//}
}
