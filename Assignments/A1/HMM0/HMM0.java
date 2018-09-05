import java.util.Locale;
import java.util.Scanner;

public class HMM0
{
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));

		Scanner sc = new Scanner(System.in);

		int transitionMatNumRows = sc.nextInt();
		int transitionMatNumCols = sc.nextInt();
		double[][] transitionMat = new double[transitionMatNumRows][transitionMatNumCols]; 

		for (int i = 0; i < transitionMatNumRows; i++) {
			for (int j = 0; j < transitionMatNumCols; j++) {
				transitionMat[i][j] = sc.nextDouble();
			}
		}

		int emissionMatNumRows = sc.nextInt();
		int emissionMatNumCols = sc.nextInt();
		double[][] emissionMat = new double[emissionMatNumRows][emissionMatNumCols]; 

		for (int i = 0; i < emissionMatNumRows; i++) {
			for (int j = 0; j < emissionMatNumCols; j++) {
				emissionMat[i][j] = sc.nextDouble();
			}
		}

		int initStateMatNumRows = sc.nextInt();
		int initStateMatNumCols = sc.nextInt();
		double[][] initStateMat = new double[initStateMatNumRows][initStateMatNumCols];

		for (int i = 0; i < initStateMatNumCols; i++) {
			initStateMat[0][i] = sc.nextDouble();
		}
		
		double[][] firstTransition = Matrix.multiply(initStateMat, transitionMat);
		firstTransition = Matrix.multiply(firstTransition, emissionMat);

		System.out.print(firstTransition.length + " " + firstTransition[0].length + " ");

		for (int i = 0; i < firstTransition[0].length; i++) 
		{
			System.out.printf("%f ", firstTransition[0][i]);
		}

		System.out.println();
	}
}