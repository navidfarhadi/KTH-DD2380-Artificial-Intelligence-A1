import java.util.Locale;
import java.util.Scanner;
import java.util.Arrays;

public class HMMC
{
	public static void main(String[] args) 
	{
		Locale.setDefault(new Locale("en", "US"));

		Scanner sc = new Scanner(System.in);

		double[][] AMat = new double[][] {{0.54,0.26,0.2}, {0.19,0.53,0.28}, {0.22,0.18,0.6}}; 
		// double[][] AMat = new double[][] {{0.7,0.05,0.25}, {0.1,0.8,0.1}, {0.2,0.3,0.5}};
		// double[][] AMat = new double[][] {{0.3,0.3,0.4}, {0.3,0.4,0.3}, {0.4,0.3,0.3}}; 
		int AMatNumRows = AMat.length;
		int AMatNumCols = AMat[0].length;

		
		double[][] BMat = new double[][] {{0.5,0.2,0.11,0.19}, {0.22,0.28,0.23,0.27}, {0.19,0.21,0.15,0.45}};
		// double[][] BMat = new double[][] {{0.7,0.2,0.1,0.0}, {0.1,0.4,0.3,0.2}, {0,0.1,0.2,0.7}};
		// double[][] BMat = new double[][] {{0.2,0.2,0.3,0.3}, {0.2,0.3,0.3,0.2}, {0.3,0.3,0.2,0.2}};

		int BMatNumRows = BMat.length;
		int BMatNumCols = BMat[0].length;
		
		double[] piSequence = new double[] {0.3,0.2,0.5};
		// double[] piSequence = new double[] {1.0,0.0,0.0};
		int piMatNumEl = piSequence.length;

		int OSequenceNumEl = sc.nextInt();
		// sc.nextInt();
		// int OSequenceNumEl = 980;
		int[] OSequence = new int[OSequenceNumEl];

		for (int i = 0; i < OSequenceNumEl; i++) 
		{
			OSequence[i] = sc.nextInt();
		}

		int maxObsSeqLen = OSequenceNumEl;

		double[][] AMat_original = new double[][] {{0.7,0.05,0.25}, {0.1,0.8,0.1}, {0.2,0.3,0.5}};
		double[][] BMat_original = new double[][] {{0.7,0.2,0.1,0.0}, {0.1,0.4,0.3,0.2}, {0.0,0.1,0.2,0.7}};
		double[] piSequence_original = new double[] {1.0,0.0,0.0};

		double minSum = Double.POSITIVE_INFINITY;
		int indexOfMinSum = 0;

		for (int z = 10000; z <= 10000; z += 10) 
		{
			AMat = new double[][] {{0.54,0.26,0.2}, {0.19,0.53,0.28}, {0.22,0.18,0.6}};
			BMat = new double[][] {{0.5,0.2,0.11,0.19}, {0.22,0.28,0.23,0.27}, {0.19,0.21,0.15,0.45}};
			piSequence = new double[] {0.3,0.2,0.5};

			int[] OSequenceSubset = Arrays.copyOfRange(OSequence, 0, z);
			int OSequenceSubsetLen = OSequenceSubset.length;
			
			// System.out.println("OSequence: ");
			// if(z == 25)
			// {
			// 	for (int steak = 0; steak < OSequenceSubsetLen; steak++) 
			// 	{
			// 		System.out.print(OSequence[steak] + " ");
			// 	}
			// }
			// System.out.println();

			System.out.println("Observation sequence length: " + OSequenceSubsetLen);

			int T = OSequenceSubsetLen;
			int N = AMatNumRows;
			
			double[] cSequence = new double[T];

			int maxIters = 5000;
			int iters = 0;
			double oldLogProb = Double.NEGATIVE_INFINITY;

			double[][] alphaMatrix;
			double[][] betaMatrix;
			double[][] gammaMatrix = new double[T][N];
			double[][][] digammaMatrix = new double[T][N][N];

			// int x;

			// for (x = iters; x < maxIters; x++) 
			// {
			// 	alphaMatrix = alphaPass(AMat, BMat, piSequence, OSequenceSubset, cSequence);
			// 	betaMatrix = betaPass(AMat, BMat, OSequenceSubset, cSequence);
			// 	computeGamma(AMat, BMat, OSequenceSubset, alphaMatrix, betaMatrix, gammaMatrix, digammaMatrix);
			// 	re_estimate(AMat, BMat, piSequence, OSequenceSubset, gammaMatrix, digammaMatrix);
			// 	double logProb = computeLogProb(cSequence);

			// 	// System.out.println("Iteration: " + i);
			// 	// System.out.println("LogProb: " + logProb);
				
			// 	if(logProb <= oldLogProb)
			// 	{
			// 		oldLogProb = logProb;
			// 		break;
			// 	}
			// }

			while(true)
			{
				alphaMatrix = alphaPass(AMat, BMat, piSequence, OSequenceSubset, cSequence);
				betaMatrix = betaPass(AMat, BMat, OSequenceSubset, cSequence);
				computeGamma(AMat, BMat, OSequenceSubset, alphaMatrix, betaMatrix, gammaMatrix, digammaMatrix);
				re_estimate(AMat, BMat, piSequence, OSequenceSubset, gammaMatrix, digammaMatrix);
				double logProb = computeLogProb(cSequence);

				iters++;
				if(iters < maxIters && logProb > oldLogProb)
				{
					oldLogProb = logProb;
				}
				else
				{
					break;
				}
			}

			System.out.println("Iters: " + iters);

			System.out.println();

			System.out.println("A Matrix: ");
			for (int i = 0; i < N; i++) 
			{
				for (int j = 0; j < N; j++) 
				{
					System.out.print(AMat[i][j] + " ");
				}
				System.out.println();
			}

			System.out.println();

			System.out.println("B Matrix: ");
			for (int i = 0; i < N; i++) 
			{
				for (int j = 0; j < BMatNumCols; j++) 
				{
					System.out.print(BMat[i][j] + " ");
				}
				System.out.println();
			}

			System.out.println();

			System.out.println("pi sequence: ");

			for (int i = 0; i < piMatNumEl; i++) {
				System.out.println(piSequence[i]);
			}

			double sum = calculateLeastSquares(AMat, AMat_original);
			sum += calculateLeastSquares(BMat, BMat_original);

			if(minSum > sum)
			{
				minSum = sum;
				indexOfMinSum = z;
			}

			System.out.println();

			System.out.println("Sum: " + sum);
			
			System.out.println();

			System.out.println("Min Sum: " + minSum);

			System.out.println();

			

		}

		System.out.println("Observation Sequence Length: " + indexOfMinSum);

		// System.out.println("Observation sequence length: " + OSequenceNumEl);

		// int T = OSequenceNumEl;
		// int N = AMatNumRows;
		
		// double[] cSequence = new double[T];

		// int maxIters = 1000;
		// int iters = 0;
		// double oldLogProb = Double.NEGATIVE_INFINITY;

		// double[][] alphaMatrix;
		// double[][] betaMatrix;
		// double[][] gammaMatrix = new double[T][N];
		// double[][][] digammaMatrix = new double[T][N][N];

		// int x;

		// for (x = iters; x < maxIters; x++) 
		// {
		// 	alphaMatrix = alphaPass(AMat, BMat, piSequence, OSequence, cSequence);
		// 	betaMatrix = betaPass(AMat, BMat, OSequence, cSequence);
		// 	computeGamma(AMat, BMat, OSequence, alphaMatrix, betaMatrix, gammaMatrix, digammaMatrix);
		// 	re_estimate(AMat, BMat, piSequence, OSequence, gammaMatrix, digammaMatrix);
		// 	double logProb = computeLogProb(cSequence);

		// 	// System.out.println("Iteration: " + i);
		// 	// System.out.println("LogProb: " + logProb);
			
		// 	if(logProb <= oldLogProb)
		// 	{
		// 		oldLogProb = logProb;
		// 		break;
		// 	}
		// }

		// System.out.println("Iters: " + x);

		// System.out.println();

		// System.out.println("A Matrix: ");
		// for (int i = 0; i < N; i++) 
		// {
		// 	for (int j = 0; j < N; j++) 
		// 	{
		// 		System.out.print(AMat[i][j] + " ");
		// 	}
		// 	System.out.println();
		// }

		// System.out.println();

		// System.out.println("B Matrix: ");
		// for (int i = 0; i < N; i++) 
		// {
		// 	for (int j = 0; j < BMatNumCols; j++) 
		// 	{
		// 		System.out.print(BMat[i][j] + " ");
		// 	}
		// 	System.out.println();
		// }

		// System.out.println();

		// System.out.println("pi sequence: ");

		// for (int i = 0; i < piMatNumEl; i++) {
		// 	System.out.println(piSequence[i]);
		// }
	}

	public static double calculateLeastSquares(double[][] m1, double[][] m2)
	{
		double sum = 0;

		for (int i = 0; i < m1.length; i++) 
		{
			for (int j = 0; j < m1[0].length; j++) 
			{
				sum += (m1[i][j] - m2[i][j]) * (m1[i][j] - m2[i][j]);
				//System.out.println("m1[i][j] = " + m1[i][j] + " m2[i][j] = " + m2[i][j]);
			}	
		}

		return sum;
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