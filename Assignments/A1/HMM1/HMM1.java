import java.util.Locale;
import java.util.Scanner;

public class HMM1
{
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));

		Scanner sc = new Scanner(System.in);

		int AMatNumRows = sc.nextInt();
		int AMatNumCols = sc.nextInt();
		double[][] AMat = new double[AMatNumRows][AMatNumCols]; 

		for (int i = 0; i < AMatNumRows; i++) {
			for (int j = 0; j < AMatNumCols; j++) {
				AMat[i][j] = sc.nextDouble();
			}
		}

		int BMatNumRows = sc.nextInt();
		int BMatNumCols = sc.nextInt();
		double[][] BMat = new double[BMatNumRows][BMatNumCols]; 

		for (int i = 0; i < BMatNumRows; i++) {
			for (int j = 0; j < BMatNumCols; j++) {
				BMat[i][j] = sc.nextDouble();
			}
		}

		int piMatNumRows = sc.nextInt(); // Should always be a 1D Matrix
		int piMatNumEl = sc.nextInt();
		double[][] piSequence = new double[1][piMatNumEl];

		for (int i = 0; i < piMatNumEl; i++) {
			piSequence[0][i] = sc.nextDouble();
		}

		int OSequenceNumEl = sc.nextInt();
		int[] OSequence = new int[OSequenceNumEl];

		for (int i = 0; i < OSequenceNumEl; i++) {
			OSequence[i] = sc.nextInt();
		}

		// alphaMat is TxN where T = length of the observation sequence and N = number of states in the HMM
		int T = OSequenceNumEl;
		int N = AMatNumRows;
		double[][] alphaMat = new double[T][N]; 

		for (int i = 0; i < N; i++) {
			alphaMat[0][i] = piSequence[0][i] * BMat[i][OSequence[0]];
			System.out.println(alphaMat[0][i]);
		}
		
		// double[][] firstTransition = Matrix.multiply(initStateMat, transitionMat);
		// firstTransition = Matrix.multiply(firstTransition, emissionMat);

		// System.out.print(firstTransition.length + " " + firstTransition[0].length + " ");

		// for (int i = 0; i < firstTransition[0].length; i++) 
		// {
		// 	System.out.printf("%f ", firstTransition[0][i]);
		// }

		// System.out.println();
	}
}