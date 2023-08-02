/*
 *Warp.java
 *Will Jarvis-Cross
 *
 *Holds the information to where the user is warping to. 
 *This is used when the user enters or exits a building and it changes the map
 */

import java.awt.*;
public class Warp {
	public int warptox,warptoy,mapto;
	Color tileCol;
    public Warp(Color tile,int warpx,int warpy,int othermap) {
		tileCol=tile;
    	warptox=warpx;
    	warptoy=warpy;
    	mapto=othermap;
    }

    public int getMapto(){
    	return mapto;
    }
    
}