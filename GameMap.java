/*
 *GameMap.java
 *Will Jarvis-Cross and Namashi Sivaram
 *The game map is the image on the background of the screen. 
 *It holds the x and y position the image is drawn at
 */
import java.awt.image.*;
import java.awt.*;
public class GameMap {
	final Image mask,mainmap;//the mask is an image which depicts the walkable and non walkable areas of the map and warp spots and so on
	int x,y;
	final int UP=1;
	final int LEFT=2;
	final int DOWN=3;
	final int RIGHT=4;
 	final NPC[] enemies;
    public GameMap(int x, int y, Image mainmap, BufferedImage mask,NPC[] badPokes) {
    	this.mask=mask;
    	this.mainmap=mainmap;
    	this.x=x;
    	this.y=y;
		enemies=badPokes;
    }
    //SETTER AND GETTERS
    /////////////////////////
    public Image getMain(){
    	return mainmap;
    }
    public Image getMask(){
    	return mask;
    }
    public int getX(){
    	return x;
    }
    public int getY(){
    	return y;
    }
    public void setY(int y){
    	this.y=y;
    }
    public void setX(int x){
    	this.x=x;
    }
    public void move(int dir,int amount){//moves the position of where the image is drawn at
    	if (dir==UP){
    		y+=amount;
    	}
    	else if (dir==DOWN){
    		y-=amount;
    	}
    	else if (dir==LEFT){
    		x+=amount;
    	}
    	else if (dir==RIGHT){
    		x-=amount;
    	}
    }
    
    
}