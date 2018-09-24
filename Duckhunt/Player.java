import java.util.*;

class Player {

    public Player() {
        HMM.InitStates(5);
        for(int i = 0; i < 6; i++){
            this.speciesArray[i] = new HMM();
        }
        //Arrays.fill(birdShot,-1);
    }

    // have to change it later if it is not sufficient
    private static final int NUMBER_STATES = 3;

    // should only get bigger
    private int numBirds = 0;

    //final array for all species
    private HMM[] speciesArray = new HMM[6];

    // if we successfully shot a bird there will the species
    private int[] birdShot;

    private HMM[] shootModels;

    private int currRound = -1;
    private int counter = 0;
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

        if(pState.getRound() != currRound){
            // we are in a new round and need to refresh our data
            currRound = pState.getRound();
            birdShot = new int[20];
            counter = 0;
            Arrays.fill(birdShot,-1);
            shootModels = new HMM[20];
            for (int i = 0; i < shootModels.length; i++) 
            {
                shootModels[i] = new HMM();
            }
        }

        counter += pState.getNumNewTurns();

	
        if(pState.getRound() == 0 || !speciesArray[Constants.SPECIES_BLACK_STORK].ready() || counter < (100 - 10 * pState.getNumNewTurns())){
            // we decided to shoot not in round 0
            return cDontShoot;
        }
        else
        {
            for(int i = 0; i < pState.getNumBirds(); i++)
            {
                if(pState.getBird(i).isDead()) continue;
                int[] seq = new int[pState.getBird(i).getSeqLength()];
                for(int j = 0; j < seq.length; j++){
                    seq[j] = pState.getBird(i).getObservation(j);
                }
                double maxProb = Double.NEGATIVE_INFINITY;
                int maxIndex = -1;
                for(int j = 0; j < speciesArray.length; j++){
                    double prob = speciesArray[i].computeProb(seq);
                    if(prob > maxProb){
                        maxProb = prob;
                        maxIndex = j;
                    }
                }
                if(maxIndex == Constants.SPECIES_BLACK_STORK) continue;
                shootModels[i].addSeq(seq);
                double[][] alphaMatrix = shootModels[i].computeBaumWelch();
                int move = shootModels[i].predictNextMove_wa(alphaMatrix);
                return new Action(i,move);
            }
        }
        return cDontShoot;
    }

    // This line chooses not to shoot.
//    return cDontShoot;

        // This line would predict that bird 0 will move right and shoot at it.
        // return Action(0, MOVE_RIGHT);
    
    private int[] getBirdSeq(Bird b)
    {
        int[] ret = new int[b.getSeqLength()];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = b.getObservation(i);
        }
        return ret;
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
	    if(pState.getRound() == 0){
            // random guesses in round 0
            for (int i = 0; i < pState.getNumBirds(); ++i){
        	    lGuess[i] = (int)((Math.random() * 6));
	        }
        }
        else{
            // for every other round we use our knowledge
            //lGuess = birdShot;
            for(int i = 0; i < pState.getNumBirds(); i++){
                if(pState.getBird(i).isAlive()){
                    // otherwise we already shot that bird correctly
                    // so we already know its species
                    int maxIndex = -1;
                    double maxProb = Double.NEGATIVE_INFINITY;
                    double secProb = 0.0;
                    int[] obsArray = getBirdSeq(pState.getBird(i));
                    //printArray(obsArray);
                    for(int j = 0; j < speciesArray.length; j++){
                        if(speciesArray[j].ready()){
                            double prob = speciesArray[j].computeProb(obsArray);
                            //double prob = 0;
                            //System.err.println("Prob: "+prob);
                            if(prob > maxProb){
                                secProb = maxProb;
                                maxProb = prob;
                                maxIndex = j;
                            } else if(prob > secProb) {
                                secProb = prob;
                            }
                        }
                    }
                    double safeness = (maxProb - secProb) / maxProb;
                    //System.err.println("Safeness: "+safeness);
                    //System.err.println();
                    lGuess[i] = maxIndex;
                }else{
                    lGuess[i] = birdShot[i];
                }

                //System.err.println();
            }
        }

        System.err.println("Guesses for round "+pState.getRound()+"\n");
        for(int i = 0; i < pState.getNumBirds(); i++){
            System.err.print(lGuess[i] + " ");
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

    public void printArray(int[] array){
        for(int i = 0; i < array.length; i++){
            System.err.print(array[i] + " ");
        }
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

	    //System.err.println("reveal Birds: "+pState.getNumBirds());
        System.err.println();
	    
        
        /*if(pState.getRound() == 0){
            // add observations to the speciesArray
            for(int i = 0; i < pState.getNumBirds(); i++){
                for(int j = 0; j < 99; j++){
                    speciesArray[pSpecies[i]].addObsSeq(pState.getBird(i).getObservation(j));
                }
            }
            for(int i = 0; i < speciesArray.length; i++){
                if(speciesArray[i].ready()){
                    //System.err.println("\nTRAIN: "+i+"\n");
                    speciesArray[i].computeBaumWelch();
                }
            }
        }*/

        for(int i = 0; i < pState.getNumBirds(); i++){
            if(pSpecies[i] == -1 || pState.getBird(i).isDead()) continue;
            //if(speciesArray[pSpecies[i]].ready()) continue;
            //System.err.println("TRAIN model "+pSpecies[i]);
            speciesArray[pSpecies[i]].addSeq(getBirdSeq(pState.getBird(i)));
            //speciesArray[pSpecies[i]].computeBaumWelch(Arrays.copyOfRange(tempOArray[i],0,99));
        }
        for(int i = 0; i < speciesArray.length; i++) {
            speciesArray[i].computeBaumWelch();
        }

        /*for(int i = 0; i < speciesArray.length; i++) {
            speciesArray[i].computeBaumWelch();
        }*/

        System.err.println("Reveal for round "+pState.getRound());

        for(int i = 0; i < pSpecies.length; i++){
		    System.err.print(pSpecies[i]+" ");
	    }
        System.err.println();
        System.err.println("time: "+pDue.remainingMs());

    }

    public static final Action cDontShoot = new Action(-1, -1);
}
