import java.util.Vector;

public class HMM
{

	
	/*
	 * This class should hold all attributes needed for storing and computing the
	 * parameters of an instance of a HMM.
	 * The methods for computing can be programmed as static members.
	 * All other things have to be private attributes for each instance.
	 */

	public double[][] AMat;
	public double[][] BMat;
	private double[] piVec;
	private Vector<Integer> oSeq;
	private double currentLogProb;

	public HMM(double[][] AMat, double[][] BMat, double[] piVec, Vector<Integer> oSeq)
	{
		this.AMat = AMat;
		this.BMat = BMat;
		this.piVec = piVec;
		this.oSeq = oSeq;
	}

	public void computeBaumWelch()
	{
		double[] cSequence = new double[oSeq.capacity()];

		int maxIters = 100;
		int iters = 0;
		double oldLogProb = Double.NEGATIVE_INFINITY;

		double[][] alphaMatrix;
		double[][] betaMatrix;
		double[][] gammaMatrix = new double[oSeq.size()][AMat.length];
		double[][][] digammaMatrix = new double[oSeq.size()][AMat.length][AMat.length];

		for (int i = iters; i < maxIters; i++) 
		{
			alphaMatrix = alphaPass(AMat, BMat, piVec, oSeq, cSequence);
			betaMatrix = betaPass(AMat, BMat, oSeq, cSequence);
			computeGamma(AMat, BMat, oSeq, alphaMatrix, betaMatrix, gammaMatrix, digammaMatrix);
			re_estimate(AMat, BMat, piVec, oSeq, gammaMatrix, digammaMatrix);
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

	private double[][] alphaPass(double[][] AMat, double[][] BMat, double[] piVec, Vector<Integer> oSeq, double[] cSequence)
	{
		double[][] alphaMatrix = new double[oSeq.size()][AMat.length];
		
		cSequence[0] = 0.0;

		for (int i = 0; i < AMat.length; i++) 
		{
			alphaMatrix[0][i] = piVec[i] * BMat[i][oSeq.get(0)];
			cSequence[0] += alphaMatrix[0][i];
		}

		cSequence[0] = 1.0 / cSequence[0];

		for (int i = 0; i < AMat.length; i++) 
		{
			alphaMatrix[0][i] = cSequence[0] * alphaMatrix[0][i];
		}

		for (int t = 1; t < oSeq.size(); t++) 
		{
			cSequence[t] = 0;
			for (int i = 0; i < AMat.length; i++) 
			{
				alphaMatrix[t][i] = 0;
				
				for (int j = 0; j < AMat.length; j++) 
				{
					alphaMatrix[t][i] += alphaMatrix[t-1][j] * AMat[j][i];
				}

				alphaMatrix[t][i] *= BMat[i][oSeq.get(t)];
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

	private double[][] betaPass(double[][] AMat, double[][] BMat, Vector<Integer> oSeq, double[] cSequence)
	{
		double[][] betaMatrix = new double[oSeq.size()][AMat.length];

		for (int i = 0; i < AMat.length; i++) 
		{
			betaMatrix[oSeq.size() - 1][i] = cSequence[oSeq.size() - 1];
		}

		for (int t = oSeq.size() - 2; t >= 0; t--) 
		{
			for (int i = 0; i < AMat.length; i++) 
			{
				betaMatrix[t][i] = 0.0;
				for (int j = 0; j < AMat.length; j++) 
				{
					betaMatrix[t][i] += AMat[i][j] * BMat[j][oSeq.get(t+1)] * betaMatrix[t+1][j];
				}

				betaMatrix[t][i] *= cSequence[t];
			}
		}

		return betaMatrix;
	}

	private void computeGamma(double[][] AMat, double[][] BMat, Vector<Integer> oSeq, double[][] alphaMatrix, double[][] betaMatrix, double[][] gammaMatrix, double[][][] digammaMatrix)
	{
		double denom = 0;

		for (int t = 0; t < oSeq.size() - 1; t++) 
		{
			denom = 0;
			for (int i = 0; i < AMat.length; i++) 
			{
				for (int j = 0; j < AMat.length; j++) 
				{
					denom += alphaMatrix[t][i] * AMat[i][j] * BMat[j][oSeq.get(t+1)] * betaMatrix[t+1][j];	
				}	
			}

			for (int i = 0; i < AMat.length; i++) 
			{
				gammaMatrix[t][i] = 0;
				for (int j = 0; j < AMat.length; j++) 
				{
					digammaMatrix[t][i][j] = (alphaMatrix[t][i] * AMat[i][j] * BMat[j][oSeq.get(t+1)] * betaMatrix[t+1][j]) / denom;
					gammaMatrix[t][i] += digammaMatrix[t][i][j];
				}
			}
		}

		denom = 0;
		for (int i = 0; i < AMat.length; i++) 
		{
			denom += alphaMatrix[oSeq.size() - 1][i];	
		}

		for (int i = 0; i < AMat.length; i++) 
		{
			gammaMatrix[oSeq.size() - 1][i] = alphaMatrix[oSeq.size()-1][i] / denom;
		}
	}

	private void re_estimate(double[][] AMat, double[][] BMat, double[] piVec, Vector<Integer> oSeq, double[][] gammaMatrix, double[][][] digammaMatrix)
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

				for (int t = 0; t < oSeq.size() - 1; t++) 
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

				for (int t = 0; t < oSeq.size() - 1; t++) 
				{
					if(oSeq.get(t) == j)
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
