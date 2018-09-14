import java.util.*;

class Player {

    public Player() {
    }

    // have to change it later if it is not sufficient
    private static final int NUMBER_STATES = 3;

    // should only get bigger
    private int numBirds = 0;
    private HMM[] hmmArray = new HMM[20];

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



	// add all new observations
	if(numBirds > 0){	
		for(int i = 0; i < numBirds; i++){
			//System.err.println("i "+i);
			hmmArray[i].addObsSeq(pState.getBird(i).getLastObservation());
		}
	}
	int newNumBirds = pState.getNumBirds();
	if(newNumBirds > numBirds){
		System.err.println("numBirds: "+numBirds);
		System.err.println("newNumBirds: "+newNumBirds);
		// create new HMMs for every Bird
		for(int i = numBirds; i < newNumBirds; i++){
			//System.err.println("init i "+i);
			hmmArray[i] = new HMM(pState.getBird(i).getLastObservation());
		}
		numBirds = newNumBirds;
	}

        // if it is the first round, we will not shoot
	System.err.println("numbBirds "+pState.getNumBirds());	
	if(pState.getRound() == 0){
		return cDontShoot;
	}

	System.err.println("Round "+pState.getRound());


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
        for (int i = 0; i < pState.getNumBirds(); ++i){
		int random = (int)((Math.random() * 6));
            	lGuess[i] = random;
	}
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
