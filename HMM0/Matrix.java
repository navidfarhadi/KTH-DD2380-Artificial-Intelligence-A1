import java.util.Arrays; 

public class Matrix
{
    public static double[][] multiply(double[][] matA, double[][] matB)
    {
        int matARowLen = matA.length;
        int matAColLen = matA[0].length;
        int matBRowLen = matB.length;
        int matBColLen = matB[0].length;

        double[][] matC = new double[matARowLen][matBColLen];

        for (int i = 0; i < matARowLen; i++) {
            Arrays.fill(matC[i],0.0);
        }

        for(int i = 0; i < matARowLen; i++)
        {
            for(int j = 0; j < matBColLen; j++)
            {
                for(int k = 0; k < matAColLen; k++)
                {
                    matC[i][j] += matA[i][k] * matB[k][j];
                }
            }
        }

        return matC;
    }
}