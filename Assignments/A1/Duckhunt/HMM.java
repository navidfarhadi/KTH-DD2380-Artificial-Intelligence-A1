public class HMM{

	
	/*
	 * This class should hold all attributes needed for storing and computing the
	 * parameters of an instance of a HMM.
	 * The methods for computing can be programmed as static members.
	 * All other things have to be private attributes for each instance.
	 */

	private double[][] AMat;
	private double[][] BMat;
	private double[]   piVec;
	// can be done in a good manner
	private double[]   oSeq = new double[100];

	public HMM(double[][] AMat, double[][] BMat, double[] piSeq){
		this.AMat = AMat;
		this.BMat = BMat;
		this.piSeq = piSeq;
	}

	public void computeBaumWelch(){
		//TODO
	}

	public void predictNextMove(){
		//TODO
	}

	public boolean compareHMM(HMM other){
		// compares wether this HMM and the other HMM perform in a similar way
		// What does in a similar way mean?
		//TODO
		return false;
	}

	/*
	 * Some other method that says whether this HMM depicts a
	 * certain kind of bird
	 */

}
