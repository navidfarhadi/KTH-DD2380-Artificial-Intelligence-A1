import java.util.Arrays;
import java.util.Vector;

public class HMM
{
	
	/*
	 * This class should hold all attributes needed for storing and computing the
	 * parameters of an instance of a HMM.
	 * The methods for computing can be programmed as static members.
	 * All other things have to be private attributes for each instance.
	 */

	private static double[][] AMatTemplate, BMatTemplate;
	private static int states = 0;

	private double[][] AMat;
	private double[][] BMat;
	private double[] piVec;
	private Vector<int[]> oSeqs = new Vector<>();
	private double currentLogProb;
	private boolean trained;
	private boolean dirtyBit = false;

	public static void InitStates(int states)
	{
		HMM.states = states;
		AMatTemplate = new double[states][states];
		BMatTemplate = new double[states][9];
		double[][] AMat = AMatTemplate;
		double[][] BMat = BMatTemplate;

		System.err.println("HMM A INIT");
		for(int i = 0; i < states; i++) {
			double def = 1.0 / states;
			double var = def * 0.8;
			for(int f = 1; f < states; f += 2) {
				double r = (Math.random() * 2 - 1) * var;
				AMat[i][f - 1] = def - r;
				AMat[i][f] = def + r;
				System.err.print(AMat[i][f - 1] + " ");
				System.err.print(AMat[i][f] + " ");
			}
			if(states % 2 != 0) {
				AMat[i][states - 1] = def;
				System.err.println(AMat[i][states - 1]);
			}
		}
		System.err.println();

		System.err.println("HMM B INIT");
		for(int i = 0; i < states; i++) {
			double def = 1.0 / 9;
			double var = def * 0.8;
			for(int f = 1; f < 9; f++) {
				double r = (Math.random() * 2 - 1) * var;
				BMat[i][f - 1] = def - r;
				BMat[i][f] = def + r;
				System.err.print(BMat[i][f - 1] + " ");
				System.err.print(BMat[i][f] + " ");
			}
			BMat[i][9-1] = def;
			System.err.println(BMat[i][9 - 1]);
		}
		System.err.println();
	}

    public HMM()
	{
		AMat = new double[states][states];
		for(int i = 0; i < states; i++) {
			for(int f = 0; f < states; f++) {
				AMat[i][f] = AMatTemplate[i][f];
			}
		}

		BMat = new double[states][9];
		for(int i = 0; i < states; i++) {
			for(int f = 0; f < 9; f++) {
				BMat[i][f] = BMatTemplate[i][f];
			}
		}
		//AMat = new double[][] {{0.1,0.5,0.4}, {0.2,0.4,0.4}, {0.31,0.08,0.61}};
		//BMat = new double[][] {{0.12,0.15,0.17,0.06,0.06,0.21,0.02,0.07,0.14}, {0.22,0.05,0.07,0.06,0.26,0.01,0.12,0.17,0.04}, {0.02,0.01,0.27,0.1,0.16,0.11,0.12,0.07,0.14}};
		//piVec = new double[] {1.0,0.0,0.0};
		piVec = new double[states];
		piVec[0] = 1;
		trained = false;
	}

	public void addSeq(int[] newSeq){
		dirtyBit = true;
		oSeqs.add(newSeq);
	}

	// returns whether the HMM does already have some meaningful information
	public boolean ready(){
		return trained;
	}

	// computes the probability for a certain oSeq
	public double computeProb(int[] seq){
		double[][] alphaMatrix = alphaPass_ws(AMat, BMat, piVec, seq);
		double prob = 0.0;
		for(int i = 0; i < AMat.length; i++){
			prob += alphaMatrix[seq.length - 1][i];
		}
		return prob;
	}

	public void computeBaumWelch()
	{
		if(oSeqs.size() == 0 || !dirtyBit) return;
		trained = true;
		double[] cSequence = new double[oSeqs.get(0).length];

		int maxIters = 100;
		int iters = 0;
		double oldLogProb = Double.NEGATIVE_INFINITY;

		double[][] alphaMatrix;
		double[][] betaMatrix;
		double[][] gammaMatrix = new double[cSequence.length][AMat.length];
		double[][][] digammaMatrix = new double[cSequence.length][AMat.length][AMat.length];

		while(true)
        {
 	    	alphaMatrix = alphaPass(AMat, BMat, piVec, oSeqs, cSequence);
    	    betaMatrix = betaPass(AMat, BMat, oSeqs, cSequence);
        	computeGamma(AMat, BMat, oSeqs, alphaMatrix, betaMatrix, gammaMatrix, digammaMatrix);
        	re_estimate(AMat, BMat, piVec, oSeqs, gammaMatrix, digammaMatrix);
        	double logProb = computeLogProb(cSequence);

			//System.err.println("Baum-Welch Iteration: "+iters);
	        iters++;
    	    if(iters < maxIters && logProb > oldLogProb)
        	{
            	oldLogProb = logProb;
    	    }
        	else
        	{
            	// System.err.println("logProb = " + logProb);
            	// System.err.println("oldLogProb = " + oldLogProb);
				currentLogProb = oldLogProb;
            	break;
            }
        }
	}



	// returns the most likely emission to happen
	public int predictNextMove(int[] seq)
	{
		//TODO
		// we need to compare alpha - know distribution for every state
		// from this state we want to get the most likely observation
		Vector<int[]> seqs = new Vector<>(1);
		seqs.add(seq);
		double[] cSeq = new double[seq.length];
		double[][] alphaMatrix = alphaPass(AMat, BMat, piVec, seqs, cSeq);
		double[] obsProbVec = new double[BMat[0].length];
		for(int j = 0; j < AMat.length; j++){
			double prob = 0;
			for(int i = 0; i < AMat.length; i++){
				prob += alphaMatrix[seq.length-1][i] * AMat[i][j];	
			}
			for(int v = 0; v < obsProbVec.length; v++){
				obsProbVec[v] += prob * BMat[j][v];
			}
		}

		int mLikelyObs = -1;
		double highestProb = Double.NEGATIVE_INFINITY;
		for(int v = 0; v < obsProbVec.length; v++){
			if(obsProbVec[v] > highestProb){
				highestProb = obsProbVec[v];
				mLikelyObs = v;
			}
		}

		return mLikelyObs;
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

	private double[][] alphaPass(double[][] AMat, double[][] BMat, double[] piVec, Vector<int[]> oSeqs, double[] cSequence)
	{
		double[][] alphaMatrix = new double[cSequence.length][AMat.length];
		
		cSequence[0] = 0.0;

		for (int i = 0; i < AMat.length; i++) 
		{
			alphaMatrix[0][i] = 0;
			for(int[] oSeq : oSeqs) {
				alphaMatrix[0][i] += piVec[i] * BMat[i][oSeq[0]];
			}
			alphaMatrix[0][i] /= oSeqs.size();
			cSequence[0] += alphaMatrix[0][i];
		}

		cSequence[0] = 1.0 / cSequence[0];

		for (int i = 0; i < AMat.length; i++) 
		{
			alphaMatrix[0][i] = cSequence[0] * alphaMatrix[0][i];
		}

		for (int t = 1; t < cSequence.length; t++) 
		{
			cSequence[t] = 0;
			for (int i = 0; i < AMat.length; i++) 
			{
				alphaMatrix[t][i] = 0;
				
				for (int j = 0; j < AMat.length; j++) 
				{
					alphaMatrix[t][i] += alphaMatrix[t-1][j] * AMat[j][i];
				}

				double factor = 0;
				for(int[] oSeq : oSeqs) {
					factor += BMat[i][oSeq[t]];
				}
				factor /= oSeqs.size();

				alphaMatrix[t][i] *= factor;
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

	// is an implementation of the alpha pass without scaling
	// FOR BAUM-WELCH, DO NOT USE THIS IMPLEMENTATION
	private double[][] alphaPass_ws(double[][] AMat, double[][] BMat, double[] piVec, int[] oSeq)
	{
		double[][] alphaMatrix = new double[oSeq.length][AMat.length];

		for (int i = 0; i < AMat.length; i++) 
		{
			alphaMatrix[0][i] = piVec[i] * BMat[i][oSeq[0]];
		}

		for (int t = 1; t < oSeq.length; t++) 
		{
			for (int i = 0; i < AMat.length; i++) 
			{
				alphaMatrix[t][i] = 0;
				
				for (int j = 0; j < AMat.length; j++) 
				{
					alphaMatrix[t][i] += alphaMatrix[t-1][j] * AMat[j][i];
				}

				alphaMatrix[t][i] *= BMat[i][oSeq[t]];
			}

		}

		return alphaMatrix;
	}

	private double[][] betaPass(double[][] AMat, double[][] BMat, Vector<int[]> oSeqs, double[] cSequence)
	{
		double[][] betaMatrix = new double[cSequence.length][AMat.length];

		for (int i = 0; i < AMat.length; i++) 
		{
			betaMatrix[cSequence.length - 1][i] = cSequence[cSequence.length - 1];
		}

		for (int t = cSequence.length - 2; t >= 0; t--) 
		{
			for (int i = 0; i < AMat.length; i++) 
			{
				betaMatrix[t][i] = 0.0;
				for (int j = 0; j < AMat.length; j++) 
				{
					double factor = 0;
					for(int[] oSeq : oSeqs) {
						factor += BMat[j][oSeq[t+1]];
					}
					factor /= oSeqs.size();
					betaMatrix[t][i] += AMat[i][j] * factor * betaMatrix[t+1][j];
				}

				betaMatrix[t][i] *= cSequence[t];
			}
		}

		return betaMatrix;
	}

	private void computeGamma(double[][] AMat, double[][] BMat, Vector<int[]> oSeqs, double[][] alphaMatrix, double[][] betaMatrix, double[][] gammaMatrix, double[][][] digammaMatrix)
	{
		double denom = 0;

		for (int t = 0; t < oSeqs.get(0).length - 1; t++) 
		{
			denom = 0;
			for (int i = 0; i < AMat.length; i++) 
			{
				for (int j = 0; j < AMat.length; j++) 
				{
					double factor = 0;
					for(int[] oSeq : oSeqs) {
						factor += BMat[j][oSeq[t+1]];
					}
					factor /= oSeqs.size();
					denom += alphaMatrix[t][i] * AMat[i][j] * factor * betaMatrix[t+1][j];	
				}	
			}

			for (int i = 0; i < AMat.length; i++) 
			{
				gammaMatrix[t][i] = 0;
				for (int j = 0; j < AMat.length; j++) 
				{
					double factor = 0;
					for(int[] oSeq : oSeqs) {
						factor += BMat[j][oSeq[t+1]];
					}
					factor /= oSeqs.size();
					digammaMatrix[t][i][j] = (alphaMatrix[t][i] * AMat[i][j] * factor * betaMatrix[t+1][j]) / denom;
					gammaMatrix[t][i] += digammaMatrix[t][i][j];
				}
			}
		}

		denom = 0;
		for (int i = 0; i < AMat.length; i++) 
		{
			denom += alphaMatrix[oSeqs.get(0).length - 1][i];	
		}

		for (int i = 0; i < AMat.length; i++) 
		{
			gammaMatrix[oSeqs.get(0).length - 1][i] = alphaMatrix[oSeqs.get(0).length-1][i] / denom;
		}
	}

	private void re_estimate(double[][] AMat, double[][] BMat, double[] piVec, Vector<int[]> oSeqs, double[][] gammaMatrix, double[][][] digammaMatrix)
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

				for (int t = 0; t < oSeqs.get(0).length - 1; t++) 
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

				for (int t = 0; t < oSeqs.get(0).length - 1; t++) 
				{
					boolean found = false;
					for(int[] oSeq : oSeqs) {
						if(oSeq[t] == j) {
							found = true;
							break;
						}
					}
					if(found)
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
