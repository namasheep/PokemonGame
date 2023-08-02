/*
 *NPC.java
 *Will Jarvis-Cross and Namashi Sivaram
 *
 *This holds all the information for the NPCs. This includes their 
 *x and y position,if they are a boss or not, their pokemons, what 
 *they say before a battle, if they have been defeated and their sprites
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;

public class NPC {
	final boolean boss;
	int x,y;
	final Pokemon[] pokes;
	String text;
	String[] multipleText=new String[2];//gym bosses have 2 texts
	public boolean status;
	public Image[] walk=new Image[3];//they turn to face 3 different directions so they have 3 different sprited for that
	boolean beaten=false;
    public NPC(String gymBoss,int xSpot,int ySpot,Pokemon[] pokemons,String text1,boolean live,String sprite1,String sprite2,String sprite3) {
		boss=Boolean.valueOf(gymBoss);
		x=xSpot;
		y=ySpot;
		pokes=pokemons;
		if (!boss){//if it isn't a boss then they only have one line to say
			text=text1;
		}
		else{//if they are a boss then they say something before and after so they have 2 lines
			String[] multipleText2=text1.split("/");
			multipleText=multipleText2;
		}
		
		walk[0]=new ImageIcon(sprite1).getImage();
		walk[1]=new ImageIcon(sprite2).getImage();
		walk[2]=new ImageIcon(sprite3).getImage();
    }
    //SETTER AND GETTERS
    //////////////////////////
    public boolean getStatus(){
    	return status;
    }
    public void setStatus(boolean live){
    	status=live;
    }
    public Image getFirstSprite(){
    	return walk[0];
    }
    public Image[] getImages(){
    	return walk;
    }
    public int getX(){
    	return x;
    }
    public int getY(){
    	return y;
    }
    public String getText(){
    	return text;
    }
    public String getFirstText(){
    	return multipleText[0];
    }
    public String getSecondText(){
    	return multipleText[1];
    }
    public void setBeaten(boolean tf){
    	beaten=tf;
    }
    public boolean getBeaten(){
    	return beaten;
    }
}
