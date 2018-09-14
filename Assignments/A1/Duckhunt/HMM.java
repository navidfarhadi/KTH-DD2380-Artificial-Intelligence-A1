import java.util.Vector;

public class HMM
{
	
	/*
	 * This class should hold all attributes needed for storing and computing the
	 * parameters of an instance of a HMM.
	 * The methods for computing can be programmed as static members.
	 * All other things have to be private attributes for each instance.
	 */

	private double[][] AMat;
	private double[][] BMat;
	private double[] piVec;
	private int[] oSeq;
	private double currentLogProb;
	private int ObsSeqCounter = 0;

	public HMM()
	{
		AMat = new double[][] {{0.1,0.5,0.4}, {0.2,0.4,0.4}, {0.31,0.08,0.61}};
		BMat = new double[][] {{0.12,0.15,0.17,0.06,0.06,0.21,0.02,0.07,0.14}, {0.22,0.05,0.07,0.06,0.26,0.01,0.12,0.17,0.04}, {0.02,0.01,0.27,0.1,0.16,0.11,0.12,0.07,0.14}};
		piVec = new double[] {1.0,0.0,0.0};
		oSeq = new int[100];
	}

	public void addObsSeq(int newObs)
	{
		oSeq[ObsSeqCounter++] = newObs;
	}

	public void computeBaumWelch()
	{
		double[] cSequence = new double[oSeq.length];

		int maxIters = 100;
		int iters = 0;
		double oldLogProb = Double.NEGATIVE_INFINITY;

		double[][] alphaMatrix;
		double[][] betaMatrix;
		double[][] gammaMatrix = new double[oSeq.length][AMat.length];
		double[][][] digammaMatrix = new double[oSeq.length][AMat.length][AMat.length];

		for (int i = iters; i < maxIters; i++) 
		{
			alphaMatrix = alphaPass(AMat, BMat, piVec, oSeq, cSequence);
			betaMatrix = betaPass(AMat, BMat, oSeq, cSequence);
			computeGamma(AMat, BMat, oSeq, alphaMatrix, betaMatrix, gammaMatrix, digammaMatrix);
			re_estimate(AMat, BMat, piVec, oSeq, gammaMatrix, digammaMatrix);
			double logProb = computeLogProb(cSequence);
			
			if(logProb <= oldLogProb)
			{
				oldLogProb = logProb;
				currentLogProb = oldLogProb;
				break;
			}
		}
	}

	public void predictNextMove()
	{
		//TODO
	}

	public boolean compareHMM(HMM other)
	{
		// compares wether this HMM and the other HMM perform in a similar way
		// What does in a similar way mean?
		//TODO
		return false;
	}

	/*
	 * Some other method that says whether this HMM depicts a
	 * certain kind of bird
	 */

	private double[][] alphaPass(double[][] AMat, double[][] BMat, double[] piVec, int[] oSeq, double[] cSequence)
	{
		double[][] alphaMatrix = new double[oSeq.length][AMat.length];
		
		cSequence[0] = 0.0;

		for (int i = 0; i < AMat.length; i++) 
		{
			alphaMatrix[0][i] = piVec[i] * BMat[i][oSeq[0]];
			cSequence[0] += alphaMatrix[0][i];
		}

		cSequence[0] = 1.0 / cSequence[0];

		for (int i = 0; i < AMat.length; i++) 
		{
			alphaMatrix[0][i] = cSequence[0] * alphaMatrix[0][i];
		}

		for (int t = 1; t < oSeq.length; t++) 
		{
			cSequence[t] = 0;
			for (int i = 0; i < AMat.length; i++) 
			{
				alphaMatrix[t][i] = 0;
				
				for (int j = 0; j < AMat.length; j++) 
				{
					alphaMatrix[t][i] += alphaMatrix[t-1][j] * AMat[j][i];
				}

				alphaMatrix[t][i] *= BMat[i][oSeq[t]];
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

	private double[][] betaPass(double[][] AMat, double[][] BMat, int[] oSeq, double[] cSequence)
	{
		double[][] betaMatrix = new double[oSeq.length][AMat.length];

		for (int i = 0; i < AMat.length; i++) 
		{
			betaMatrix[oSeq.length - 1][i] = cSequence[oSeq.length - 1];
		}

		for (int t = oSeq.length - 2; t >= 0; t--) 
		{
			for (int i = 0; i < AMat.length; i++) 
			{
				betaMatrix[t][i] = 0.0;
				for (int j = 0; j < AMat.length; j++) 
				{
					betaMatrix[t][i] += AMat[i][j] * BMat[j][oSeq[t+1]] * betaMatrix[t+1][j];
				}

				betaMatrix[t][i] *= cSequence[t];
			}
		}

		return betaMatrix;
	}

	private void computeGamma(double[][] AMat, double[][] BMat, int[] oSeq, double[][] alphaMatrix, double[][] betaMatrix, double[][] gammaMatrix, double[][][] digammaMatrix)
	{
		double denom = 0;

		for (int t = 0; t < oSeq.length - 1; t++) 
		{
			denom = 0;
			for (int i = 0; i < AMat.length; i++) 
			{
				for (int j = 0; j < AMat.length; j++) 
				{
					denom += alphaMatrix[t][i] * AMat[i][j] * BMat[j][oSeq[t+1]] * betaMatrix[t+1][j];	
				}	
			}

			for (int i = 0; i < AMat.length; i++) 
			{
				gammaMatrix[t][i] = 0;
				for (int j = 0; j < AMat.length; j++) 
				{
					digammaMatrix[t][i][j] = (alphaMatrix[t][i] * AMat[i][j] * BMat[j][oSeq[t+1]] * betaMatrix[t+1][j]) / denom;
					gammaMatrix[t][i] += digammaMatrix[t][i][j];
				}
			}
		}

		denom = 0;
		for (int i = 0; i < AMat.length; i++) 
		{
			denom += alphaMatrix[oSeq.length - 1][i];	
		}

		for (int i = 0; i < AMat.length; i++) 
		{
			gammaMatrix[oSeq.length - 1][i] = alphaMatrix[oSeq.length-1][i] / denom;
		}
	}

	private void re_estimate(double[][] AMat, double[][] BMat, double[] piVec, int[] oSeq, double[][] gammaMatrix, double[][][] digammaMatrix)
	{
		for (int i = 0; i < AMat.length; i++) 
		{
			piVec[i] = gammaMatrix[0][i];
		}

		for (int i = 0; i < AMat.length; i++) 
		{
			for (int j = 0; j < AMat.length; j++) 
			{
				double numer = 0.0;
				double denom = 0.0;

				for (int t = 0; t < oSeq.length - 1; t++) 
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

				for (int t = 0; t < oSeq.length - 1; t++) 
				{
					if(oSeq[t] == j)
						numer += gammaMatrix[t][i];

					denom += gammaMatrix[t][i];
				}

				BMat[i][j] = numer / denom;
			}
		}
	}

	private double computeLogProb(double[] cSequence)
	{
		double logProb = 0;

		for (int i = 0; i < cSequence.length; i++) 
		{
			logProb += Math.log(cSequence[i]) / Math.log(2.0);	
		}

		return (-logProb);
	}

}
