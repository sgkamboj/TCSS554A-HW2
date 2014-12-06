import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;


/**
 * This program is a Page Rank algorithm that read a file and finds the score of each document.
 * 
 *@author Sagar Kamboj
 *@date: 12-5-14 
 */
public class PageRank {
	
	/**
	 * Is the given alpha.
	 */
	private static final  double ALPHA = .15; //P
	
	/**
	 * The number of elements.
	 */
	private static final double N = 6; //elements
	
	/**
	 * The main method which runs the program.
	 * @param args
	 */
	public static void main(String[] args)
	{
		//6x6
		double[][] adjMtx = new double[(int) N][(int) N]; //A
		double[][] onesMtx = new double[(int) N][(int) N]; 

		readFile(adjMtx); //reads a file and fills the adj matrix
				
		TransitionMatrix(adjMtx); //Makes a Transitive Matrix
		
		Matrix trans = new CRSMatrix(adjMtx);
		
		Matrix transition = trans.transpose(); //transposes the matrix
		
		transition = transition.multiply(1-ALPHA); //Multiply (1-alpha) which is (1-.85)
		
		Matrix ones = new CRSMatrix(onesMtx);
		ones = ones.add((1.0/N)); //Make B part of equation : ones = (1/N x (vector with all 1s))

		ones = ones.multiply(ALPHA); // P x B
		
		Matrix M = transition.add(ones); // M = (1-P)A + PB
		
		double[] vec = new double[(int) N];
		Vector vector = makeV(vec); //makes V Vector
		
		Vector ranks = FindRank(M, vector); //returns back the ranks
		
		double[] arrayRanks = new double[6];
		
		for(int i = 0; i < ranks.length(); i++) //change ranks to array so we can sort it
		{
			arrayRanks[i] = ranks.get(i);
		}
				
		Arrays.sort(arrayRanks); //sort the ranks
		
		for(int i = 0; i < 6; i++)
		{
			System.out.println(arrayRanks[i]); //Prints the rankings
		}
	}
	
	/**
	 * Reads in a file and fills the Adjacency Matrix 2D array
	 * 
	 * @param adjMtx The adjacency matrix to fill up form the file
	 */
	private static void readFile(double[][] adjMtx)
	{
		Scanner s = null;
		
		try {
			s = new Scanner(new File("graph"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(s.hasNextLine())
		{
			String line = s.nextLine();
			
			String[] split = line.split(" ");
			
			String row = split[0]; //the rows
			String column = split[1]; //the columns
			String value = split[2]; // always 1
			
			adjMtx[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] = Integer.parseInt(value);
		}
	}
	
	/**
	 * Makes a Transition Matrix form the Adjacency Matrix.
	 * 
	 * @param adjMtx The matrix to be multiplied one
	 */
	private static void TransitionMatrix(double[][] adjMtx)
	{
		double one_count = 0;
		for(int row = 0; row < adjMtx.length; row++)
		{
			one_count = 0;
			for(int column = 0; column < adjMtx[row].length; column++)
			{
				if(adjMtx[row][column] == 1.0)
				{
					one_count++;
				}
			}
			
			for(int column = 0; column < adjMtx[row].length; column++)
			{
				if(one_count != 0)
				{	
					if(adjMtx[row][column] !=0)
					{
						adjMtx[row][column] = (1/one_count);
					}
				}
				else
				{
					adjMtx[row][column] = (1/N);
				}
			}
		}
	}
	
	/**
	 * Makes a vector V from the equation
	 * @param vector The vector to be made
	 * @return the filled vector
	 */
	private static Vector makeV(double[] vector)
	{
		for(int i = 0; i < vector.length; i++)
		{
			vector[i] = (1/N);
		}
		
		Vector vec = new BasicVector(vector);
		
		return vec;
	}
	
	/**
	 * Finds and returns the ranks of the documents
	 * 
	 * @param M Matrix M form equation
	 * @param V Matrix V form equation
	 * @return the Vector containing the ranks
	 */
	private static Vector FindRank(Matrix M, Vector V)
	{
		boolean isEqual = false;
		int madeIt = 0;
		
		Vector front = null; //leading one; such as M^2 x V
		Vector back = null; //following one; such as M x V
		
		front = M.multiply(V);
		
		while(!isEqual)
		{ 
			madeIt = 0;
			back = front;
			
			front = M.multiply(front);
			
			for(int i = 0; i < front.length(); i++)
			{
				if(front.get(i) != back.get(i))
				{
					madeIt = 1;
					break;
				}
			}
			
			if(madeIt == 0)
			{
				isEqual = true;
			}
		}
		return front;
	}
}
