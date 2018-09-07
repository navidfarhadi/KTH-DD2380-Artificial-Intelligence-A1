import java.util.Locale;
import java.util.Scanner;
import java.util.Arrays;

public class HMM3
{
	public static void main(String[] args) 
	{
		Locale.setDefault(new Locale("en", "US"));

		Scanner sc = new Scanner(System.in);

		int AMatNumRows = sc.nextInt();
		int AMatNumCols = sc.nextInt();
		double[][] AMat = new double[AMatNumRows][AMatNumCols]; 

		for (int i = 0; i < AMatNumRows; i++) 
		{
			for (int j = 0; j < AMatNumCols; j++) 
			{
				AMat[i][j] = sc.nextDouble();
			}
		}

		int BMatNumRows = sc.nextInt();
		int BMatNumCols = sc.nextInt();
		double[][] BMat = new double[BMatNumRows][BMatNumCols]; 

		for (int i = 0; i < BMatNumRows; i++) 
		{
			for (int j = 0; j < BMatNumCols; j++) 
			{
				BMat[i][j] = sc.nextDouble();
			}
		}

		int piMatNumRows = sc.nextInt(); // Should always be a 1D Matrix
		int piMatNumEl = sc.nextInt();
		double[] piSequence = new double[piMatNumEl];

		for (int i = 0; i < piMatNumEl; i++) 
		{
			piSequence[i] = sc.nextDouble();
		}

		int OSequenceNumEl = sc.nextInt();
		int[] OSequence = new int[OSequenceNumEl];

		for (int i = 0; i < OSequenceNumEl; i++) 
		{
			OSequence[i] = sc.nextInt();
		}

		int T = OSequenceNumEl;
		int N = AMatNumRows;
		
		double[] cSequence = new double[T];

		int maxIters = 100;
		int iters = 0;
		double oldLogProb = Double.NEGATIVE_INFINITY;

		double[][] alphaMatrix;
		double[][] betaMatrix;
		double[][] gammaMatrix = new double[T][N];
		double[][][] digammaMatrix = new double[T][N][N];

		for (int i = iters; i < maxIters; i++) 
		{
			alphaMatrix = alphaPass(AMat, BMat, piSequence, OSequence, cSequence);
			betaMatrix = betaPass(AMat, BMat, OSequence, cSequence);
			computeGamma(AMat, BMat, OSequence, alphaMatrix, betaMatrix, gammaMatrix, digammaMatrix);
			re_estimate(AMat, BMat, piSequence, OSequence, gammaMatrix, digammaMatrix);
			double logProb = computeLogProb(cSequence);

			// System.out.println("Got here: " + i);
			// System.out.println(logProb);
			// System.out.println(oldLogProb);
			
			if(logProb <= oldLogProb)
			{
				oldLogProb = logProb;
				break;
			}
		}

		System.out.print(N + " " + N + " ");
		for (int i = 0; i < N; i++) 
		{
			for (int j = 0; j < N; j++) 
			{
				System.out.print(AMat[i][j] + " ");
			}
		}

		System.out.println();

		System.out.print(N + " " + BMatNumCols + " ");
		for (int i = 0; i < N; i++) 
		{
			for (int j = 0; j < BMatNumCols; j++) 
			{
				System.out.print(BMat[i][j] + " ");
			}
		}

		System.out.println();
	}

	public static double[][] alphaPass(double[][] AMat, double[][] BMat, double[] piSequence, int[] OSequence, double[] cSequence)
	{
		double[][] alphaMatrix = new double[OSequence.length][AMat.length];
		
		cSequence[0] = 0.0;

		for (int i = 0; i < AMat.length; i++) 
		{
			alphaMatrix[0][i] = piSequence[i] * BMat[i][OSequence[0]];
			cSequence[0] += alphaMatrix[0][i];
		}

		cSequence[0] = 1.0 / cSequence[0];

		for (int i = 0; i < AMat.length; i++) 
		{
			alphaMatrix[0][i] = cSequence[0] * alphaMatrix[0][i];
		}

		for (int t = 1; t < OSequence.length; t++) 
		{
			cSequence[t] = 0;
			for (int i = 0; i < AMat.length; i++) 
			{
				alphaMatrix[t][i] = 0;
				
				for (int j = 0; j < AMat.length; j++) 
				{
					alphaMatrix[t][i] += alphaMatrix[t-1][j] * AMat[j][i];
				}

				alphaMatrix[t][i] *= BMat[i][OSequence[t]];
				cSequence[t] += alphaMatrix[t][i];
			}

			cSequence[t] = 1.0 / cSequence[t];
			for (int i = 0; i < AMat.length; i++) 
			{
				alphaMatrix[t][i] *= cSequence[t];
			}
		}

		return alphaMatrix;
	}

	public static double[][] betaPass(double[][] AMat, double[][] BMat, int[] OSequence, double[] cSequence)
	{
		double[][] betaMatrix = new double[OSequence.length][AMat.length];

		for (int i = 0; i < AMat.length; i++) 
		{
			betaMatrix[OSequence.length - 1][i] = cSequence[OSequence.length - 1];
		}

		for (int t = OSequence.length - 2; t >= 0; t--) 
		{
			for (int i = 0; i < AMat.length; i++) 
			{
				betaMatrix[t][i] = 0.0;
				for (int j = 0; j < AMat.length; j++) 
				{
					betaMatrix[t][i] += AMat[i][j] * BMat[j][OSequence[t+1]] * betaMatrix[t+1][j];
				}

				betaMatrix[t][i] *= cSequence[t];
			}
		}

		return betaMatrix;
	}

	public static void computeGamma(double[][] AMat, double[][] BMat, int[] OSequence, double[][] alphaMatrix, double[][] betaMatrix, double[][] gammaMatrix, double[][][] digammaMatrix)
	{
		double denom = 0;

		for (int t = 0; t < OSequence.length - 1; t++) 
		{
			denom = 0;
			for (int i = 0; i < AMat.length; i++) 
			{
				for (int j = 0; j < AMat.length; j++) 
				{
					denom += alphaMatrix[t][i] * AMat[i][j] * BMat[j][OSequence[t+1]] * betaMatrix[t+1][j];	
				}	
			}

			for (int i = 0; i < AMat.length; i++) 
			{
				gammaMatrix[t][i] = 0;
				for (int j = 0; j < AMat.length; j++) 
				{
					digammaMatrix[t][i][j] = (alphaMatrix[t][i] * AMat[i][j] * BMat[j][OSequence[t+1]] * betaMatrix[t+1][j]) / denom;
					gammaMatrix[t][i] += digammaMatrix[t][i][j];
				}
			}
		}

		denom = 0;
		for (int i = 0; i < AMat.length; i++) 
		{
			denom += alphaMatrix[OSequence.length - 1][i];	
		}

		for (int i = 0; i < AMat.length; i++) 
		{
			gammaMatrix[OSequence.length - 1][i] = alphaMatrix[OSequence.length-1][i] / denom;
		}
	}

	public static void re_estimate(double[][] AMat, double[][] BMat, double[] piSequence, int[] OSequence, double[][] gammaMatrix, double[][][] digammaMatrix)
	{
		for (int i = 0; i < AMat.length; i++) 
		{
			piSequence[i] = gammaMatrix[0][i];
		}

		for (int i = 0; i < AMat.length; i++) 
		{
			for (int j = 0; j < AMat.length; j++) 
			{
				double numer = 0.0;
				double denom = 0.0;

				for (int t = 0; t < OSequence.length - 1; t++) 
				{
					numer += digammaMatrix[t][i][j];
					denom += gammaMatrix[t][i];
				}

				AMat[i][j] = numer / denom;
			}
		}

		for (int i = 0; i < AMat.length; i++) 
		{
			for (int j = 0; j < BMat[0].length; j++) 
			{
				double numer = 0.0;
				double denom = 0.0;

				for (int t = 0; t < OSequence.length - 1; t++) 
				{
					if(OSequence[t] == j)
						numer += gammaMatrix[t][i];

					denom += gammaMatrix[t][i];
				}

				BMat[i][j] = numer / denom;
			}
		}
	}

	public static double computeLogProb(double[] cSequence)
	{
		double logProb = 0;

		for (int i = 0; i < cSequence.length; i++) 
		{
			logProb += Math.log(cSequence[i]) / Math.log(2.0);	
		}

		return (-logProb);
	}
}