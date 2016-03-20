package Evaluation;

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
	public static String INPUT_FILE = "Prj1_input.txt";

	public static void main(String[] args) {
		try{
			loadInputFile();
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
	
	public static void loadInputFile() throws IOException{
		FileReader input = new FileReader(INPUT_FILE);
		BufferedReader bufRead = new BufferedReader(input);
		String curLine = null;
		String[] linePrefixes = {"N=", "C=", "R=", "a_b=", "Req_Reliability=", "Req_Cost="};
		
		while((curLine = bufRead.readLine()) != null){
			curLine = curLine.replace(" ", "");
			String currentPrefix = "";
			Boolean validLine = false;
			
			if(curLine.length() == 0){
				continue;
			}
			
			for(int i=0; i < linePrefixes.length; i++){
				currentPrefix = linePrefixes[i];
				if((curLine.toLowerCase().contains(currentPrefix.toLowerCase())) && 
				   (curLine.substring(0, currentPrefix.length()).equals(currentPrefix))){
					validLine = true;
					break;
				}
			}
			if((!validLine) || (currentPrefix.equals(""))) continue;
			
			String lineValue = curLine.replace(currentPrefix, "");
						
			if(currentPrefix.equals("N=")){
				N = Integer.parseInt(lineValue);
			}else if(currentPrefix.equals("C=")){
				costMatrix = convertListToMatrix(lineValue, currentPrefix);
				System.out.println("\nCost matrix: ");
				printMatrix(costMatrix);
			}else if(currentPrefix.equals("R=")){
				relMatrix = convertListToMatrix(lineValue, currentPrefix);
				System.out.println("\nReliability matrix: ");
				printMatrix(relMatrix);
			}else if(currentPrefix.equals("a_b=")){
				problemType = Integer.parseInt(lineValue);
			}else if(currentPrefix.equals("Req_Reliability=")){
				relLimit = Double.parseDouble(lineValue);
				//costLimit = 0;
			}else if(currentPrefix.equals("Req_Cost=")){
				costLimit = Integer.parseInt(lineValue);
				//relLimit = 0;
			}
		}
		System.out.println("\nN IS " + N);
		System.out.println("a_b IS " + problemType);
		System.out.println("relLimit IS " + relLimit);
		System.out.println("costLimit IS " + costLimit);
	}
	
	public static double[][] convertListToMatrix(String input, String prefix){
		double diagValue;
		if(prefix.equals("C=")){
			diagValue = Double.POSITIVE_INFINITY;
		}else{
			//diagonal for reliability should be 1
			diagValue = 1.0;
		}
		
		input = input.replace("[", "").replace("]", "");
		String[] inputList = input.split(",");
		double[][] retMatrix = new double[N][N];
		int row = 0;
		int col = 0;
		int listIndex = 0;
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
			col=row;
		}
		return retMatrix;
	}
	
	public static void printMatrix(double[][] mat){
		for(int i=0; i < mat.length; i++){
			for (int j=0; j < mat[0].length; j++){
				System.out.print(mat[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
}
