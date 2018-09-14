import java.util.*;

class Player {

    public Player() {
    }

    // have to change it later if it is not sufficient
    private static int NUMBER_STATES = 3;

    // should only get bigger
    private int numBirds;
    
    /**
     * Shoot!
     *
     * This is the function where you start your work.
     *
     * You will receive a variable pState, which contains information about all
     * birds, both dead and alive. Each bird contains all past moves.
     *
     * The state also contains the scores for all players and the number of
     * time steps elapsed since the last time this function was called.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return the prediction of a bird we want to shoot at, or cDontShoot to pass
     */
    public Action shoot(GameState pState, Deadline pDue) 
    {
        /*
         * Here you should write your clever algorithms to get the best action.
         * This skeleton never shoots.
         */

	List<HMM> hmmList = new LinkedList<HMM>();

	int newNumBirds = pState.getNumBirds();
	if(newNumBirds > numBirds){
		System.out.println("numBirds: "+numBirds);
		System.out.println("newNumBirds: "+newNumBirds);
		// create new HMMs for every Bird
		for(int i = numBirds; i < newNumBirds; i++){
			/*double[][] AMat = new double[NUMBER_STATES][NUMBER_STATES];
			double[][] BMAt = new double[NUMBER_STATES][NUMBER_STATES];
			double[][] piVec = new double[];*/
			//HMM hmm = HMM.init();
			//hmm.setOSeq();
		}
		numBirds = newNumBirds;
	}
        
        
        // This line chooses not to shoot.
        return cDontShoot;

        // This line would predict that bird 0 will move right and shoot at it.
        // return Action(0, MOVE_RIGHT);
    }

    /**
     * Guess the species!
     * This function will be called at the end of each round, to give you
     * a chance to identify the species of the birds for extra points.
     *
     * Fill the vector with guesses for the all birds.
     * Use SPECIES_UNKNOWN to avoid guessing.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return a vector with guesses for all the birds
     */
    public int[] guess(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to guess the species of
         * each bird. This skeleton makes no guesses, better safe than sorry!
         */

        int[] lGuess = new int[pState.getNumBirds()];
        for (int i = 0; i < pState.getNumBirds(); ++i)
            lGuess[i] = Constants.SPECIES_UNKNOWN;
        return lGuess;
    }

    /**
     * If you hit the bird you were trying to shoot, you will be notified
     * through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird the bird you hit
     * @param pDue time before which we must have returned
     */
    public void hit(GameState pState, int pBird, Deadline pDue) {
        System.err.println("HIT BIRD!!!");
    }

    /**
     * If you made any guesses, you will find out the true species of those
     * birds through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
    }

    public static final Action cDontShoot = new Action(-1, -1);
}
