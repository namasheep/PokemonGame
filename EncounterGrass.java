/*
 *EncounterGrass.java
 *Will Jarvis-Cross and Namashi Sivaram
 *Holds all the properties of the tall grass sections. 
 *They spawn specific pokemon according to their area with different levels.
 *If the player walks on the tall grass then there is 
 *a chance of the pokemon spawning and fighting you.
 */

import java.awt.*;
import java.util.ArrayList;
public class EncounterGrass {
	final Color grassCol;//the color of the tiles in the mask where the tall grass is
	final int lvlMin,lvlMax;
	final ArrayList<Integer> pokemons;
    public EncounterGrass(Color tileCol,int levelMin,int levelMax,ArrayList<Integer> pokes) {
    	grassCol=tileCol;
    	lvlMin=levelMin;
    	lvlMax=levelMax;
    	
    	pokemons=pokes;
    }
    
    public int getRandLvl(){//getting the level of the pokemon made for the battle
    	return (int)(Math.random()*(lvlMax-lvlMin))+lvlMin;
    }
    public int getRandPoke(){//getting the random pokemon for the battle

    	return pokemons.get((int)(Math.random()*pokemons.size()));
    }

}
