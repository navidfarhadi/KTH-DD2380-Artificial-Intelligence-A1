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

    // holds oSeqs for one round
    private int[][] tempOArray = new int[20][100];
    private int tempOArrayCounter = 0;
    // if we successfully shot a bird there will the species
    private int[] birdShot;

    private int currRound = 0;

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
            tempOArray = new int[20][100];
            birdShot = new int[pState.getNumBirds()];
            Arrays.fill(birdShot,-1);
            tempOArrayCounter = 0;
        }

        // store every emission in the array

        for(int i = tempOArrayCounter; i < (tempOArrayCounter + pState.getNumNewTurns()); i++){
            for(int j = 0; j < pState.getNumBirds(); j++){
                tempOArray[j][i] = pState.getBird(j).getObservation(i);
            }
        }

        tempOArrayCounter += pState.getNumNewTurns();

	
        if(pState.getRound() == 0){
            // we decided to shoot not in round 0
            return cDontShoot;
        }
        else{
            // it is time to hunt
        
            // first we wait for timestep 10 to get a meaningful result
            /*if(currRound > 9){
                // let's shoot
                for(int j = 0; j < 20; j++){
                    if(birdShot[j] > -1){
                        double prob = Double.NEGATIVE_INFINITY;
                        int probIndex = -1;
                        for(int i = 0; i < speciesArray.length; i++){
                            if(speciesArray[i].ready()){
                                double tempProb = speciesArray[i].computeProb(Arrays.copyOfRange(tempOArray[j],0,currRound));
                                if(tempProb > prob){
                                    prob =tempProb;
                                    probIndex = i;
                                }
                            }
                        }
                        // CAN BE DONE BETTER
                        int move = speciesArray[probIndex].predictNextMove(Arrays.copyOfRange(tempOArray[j],0,currRound));
                        // has to be done better
                        return new Action(j,move);
                    }
                }
            }*/
        
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
	    if(pState.getRound() == 0){
            // random guesses in round 0
            for (int i = 0; i < pState.getNumBirds(); ++i){
        	    lGuess[i] = (int)((Math.random() * 6));
	        }
        }
        else{
            // for every other round we use our knowledge
            lGuess = birdShot;
            for(int i = 0; i < pState.getNumBirds(); i++){
                if(birdShot[i] == -1){
                    // otherwise we already shot that bird correctly
                    // so we already know its species
                    int maxIndex = -1;
                    double maxProb = Double.NEGATIVE_INFINITY;
                    int[] obsArray = Arrays.copyOfRange(tempOArray[i],0,99);
                    //printArray(obsArray);
                    for(int j = 0; j < speciesArray.length; j++){
                        if(speciesArray[j].ready()){
                            double prob = speciesArray[j].computeProb(obsArray);
                            //System.err.println("Prob: "+prob);
                            if(prob > maxProb){
                                maxProb = prob;
                                maxIndex = j;
                            }
                        }
                    }
                    System.err.println("Prob: "+maxProb);
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
            //if(speciesArray[pSpecies[i]].ready()) continue;
            //System.err.println("TRAIN model "+pSpecies[i]);
            speciesArray[pSpecies[i]].addSeq(Arrays.copyOfRange(tempOArray[i],0,99));
            //speciesArray[pSpecies[i]].computeBaumWelch();
            //speciesArray[pSpecies[i]].computeBaumWelch(Arrays.copyOfRange(tempOArray[i],0,99));
        }

        for(int i = 0; i < speciesArray.length; i++) {
            speciesArray[i].computeBaumWelch();
        }

        System.err.println("Reveal for round "+pState.getRound());

        for(int i = 0; i < pSpecies.length; i++){
		    System.err.print(pSpecies[i]+" ");
	    }
        System.err.println();
        System.err.println("time: "+pDue.remainingMs());

    }

    public static final Action cDontShoot = new Action(-1, -1);
}
