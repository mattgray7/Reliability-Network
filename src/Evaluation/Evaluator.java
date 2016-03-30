package Evaluation;

import Graph.Graph;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

public class Evaluator {
	
	public static int N;
	public static double[][] costMatrix;
	public static double[][] relMatrix;
	public static int problemType;
	public static double relLimit;
	public static int costLimit;
	public static String INPUT_FILE = "Prj1_input.txt";//"test_input.txt";
	public static Graph graph;

	/**
	 * Entrance to program, loads input file and calls necessary functions
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			//set the class variables based on the text file
			loadInputFile();
		}catch(Exception e){
			System.out.println(e);
			System.out.println("File could not be loaded. Exiting...");
			return;
		}
		
		graph = new Graph();
		graph.createGraph(N, costMatrix, relMatrix);
		Algorithms algorithms = new Algorithms();
		
		if(problemType == 0){
			System.out.println("Problem a: Create graph with reliability >= " + relLimit);
			algorithms.augmentToReliabilityConstraint(graph, relLimit).printGraph();
		}else if(problemType == 1){
			System.out.println("Problem b: Create graph with cost <= " + costLimit);
			algorithms.augmentToCostConstraint(graph, costLimit).printGraph();
		}
	}
	
	/**
	 * Parses the input text file and sets the class variables.
	 * @throws IOException
	 */
	public static void loadInputFile() throws IOException{
		FileReader input = new FileReader(INPUT_FILE);
		BufferedReader bufRead = new BufferedReader(input);
		String curLine = null;
		String[] linePrefixes = {"N=", "C=", "R=", "a_b=", "Req_Reliability=", "Req_Cost="};
		
		//parse line by line
		while((curLine = bufRead.readLine()) != null){
			//remove whitespace for to simplify parsing
			curLine = curLine.replace(" ", "");
			String currentPrefix = "";
			Boolean validLine = false;
			
			if(curLine.length() == 0){
				continue;
			}
			
			//parse the specified prefixes to see if line patches
			for(int i=0; i < linePrefixes.length; i++){
				currentPrefix = linePrefixes[i];
				if((curLine.toLowerCase().contains(currentPrefix.toLowerCase())) && 
				   (curLine.substring(0, currentPrefix.length()).equals(currentPrefix))){
					validLine = true;
					break;
				}
			}
			if((!validLine) || (currentPrefix.equals(""))) continue;
			
			//remove the prefix once it is known
			String lineValue = curLine.replace(currentPrefix, "");
			
			//set the variable based on the prefix
			if(currentPrefix.equals("N=")){
				N = Integer.parseInt(lineValue);
			}else if(currentPrefix.equals("C=")){
				costMatrix = convertListToMatrix(lineValue, currentPrefix);
			}else if(currentPrefix.equals("R=")){
				relMatrix = convertListToMatrix(lineValue, currentPrefix);
			}else if(currentPrefix.equals("a_b=")){
				problemType = Integer.parseInt(lineValue);
			}else if(currentPrefix.equals("Req_Reliability=")){
				relLimit = Double.parseDouble(lineValue);
			}else if(currentPrefix.equals("Req_Cost=")){
				costLimit = Integer.parseInt(lineValue);
			}
		}
	}
	
	/**
	 * Converts the single list to a 2D matrix based on the N class variable
	 * @param input: The input list
	 * @param prefix: The prefix used to determine on the diag
	 * @return
	 */
	public static double[][] convertListToMatrix(String input, String prefix){
		double diagValue;
		if(prefix.equals("C=")){
			diagValue = Double.POSITIVE_INFINITY;
		}else{
			diagValue = 1.0;
		}
		
		//split values into java list
		input = input.replace("[", "").replace("]", "");
		String[] inputList = input.split(",");
		double[][] retMatrix = new double[N][N];
		int row = 0;
		int col = 0;
		int listIndex = 0;
		
		//set values into 2d matrix
		while(row < N){
			//set the diagonal
			retMatrix[row][col] = diagValue;
			col++;
			while(col < N){
				retMatrix[row][col] = Double.parseDouble(inputList[listIndex]);
				listIndex++;
				col++;
			}
			row++;
			
			//set col to next row index so that only upper diagonal gets filled
			col=row;
		}
		return retMatrix;
	}
	
	/**
	 * Prints the input matrix with user friendly format
	 * @param mat: The 2D matrix to print
	 */
	public static void printMatrix(double[][] mat){
		for(int i=0; i < mat.length; i++){
			for (int j=0; j < mat[0].length; j++){
				System.out.print(mat[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
}
