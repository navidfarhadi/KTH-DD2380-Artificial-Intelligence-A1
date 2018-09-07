import java.util.Locale;
import java.util.Scanner;
import java.util.Arrays;

public class HMM2
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
		double[][] piSequence = new double[1][piMatNumEl];

		for (int i = 0; i < piMatNumEl; i++) 
		{
			piSequence[0][i] = sc.nextDouble();
		}

		int OSequenceNumEl = sc.nextInt();
		int[] OSequence = new int[OSequenceNumEl];

		for (int i = 0; i < OSequenceNumEl; i++) 
		{
			OSequence[i] = sc.nextInt();
		}

		int T = OSequenceNumEl;
		int N = AMatNumRows;
		double[][] deltaMat = new double[T][N]; 

		for (int i = 0; i < N; i++) 
		{
			deltaMat[0][i] = piSequence[0][i] * BMat[i][OSequence[0]];
		}

		int[][] deltaIndexMat = new int[T][N];

		for (int i = 0; i < T; i++) 
		{
			Arrays.fill(deltaIndexMat[i],-1);	
		}

		// System.out.println("Initial Delta Index matrix:");
		// for (int t = 0; t < T; t++) 
		// {
		// 	for (int i = 0; i < N; i++) 
		// 	{
		// 		System.out.print(deltaIndexMat[t][i] + " ");
		// 	}
		// 	System.out.println();
		// }
		
		for (int t = 1; t < T; t++) 
		{
			for(int i = 0; i < N; i++)
			{
				double temp1 = 0; // variable to hold maximum
				int index = 0;
				for (int j = 0; j < N; j++) 
				{
					double temp2 = AMat[j][i] * deltaMat[t-1][j] * BMat[i][OSequence[t]]; // variable to hold maximum
					if(temp2 >= temp1)
					{
						temp1 = temp2;
						index = j;
						//System.out.println("current max: " + temp1 + " " + "current index: " + index + " " + "i: " + i + " " + "j: " + j);
					}
				}
				
				deltaMat[t][i] = temp1;
				if(temp1 > 0)
					deltaIndexMat[t][i] = index;
				//System.out.println(index);
			}
		}

		// System.out.println("Delta matrix:");
		// for (int t = 0; t < T; t++) 
		// {
		// 	for (int i = 0; i < N; i++) 
		// 	{
		// 		System.out.print(deltaMat[t][i] + " ");
		// 	}
		// 	System.out.println();
		// }

		// System.out.println("Delta Index matrix:");
		// for (int t = 0; t < T; t++) 
		// {
		// 	for (int i = 0; i < N; i++) 
		// 	{
		// 		System.out.print(deltaIndexMat[t][i] + " ");
		// 	}
		// 	System.out.println();
		// }

		int[] result = new int[OSequenceNumEl];

		int tempIndex = -1;
		double tempMax = -1.0;
		for(int i = 0; i < N; i++)
		{
			if(deltaMat[T-1][i] > tempMax)
			{
				tempMax = deltaMat[T-1][i];
				tempIndex = i;
			}
		}
		result[OSequenceNumEl-1] = tempIndex;

		for(int i = OSequenceNumEl-1; i > 0; i--)
		{
			tempIndex = deltaIndexMat[i][tempIndex];
			result[i-1] = tempIndex;
		}

		// System.out.println("---");

		for (int i = 0; i < OSequenceNumEl; i++) 
		{
			System.out.print(result[i] + " ");
		}
		
		System.out.println();
	}
}