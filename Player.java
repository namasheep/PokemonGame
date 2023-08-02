/**
 * @(#)Player.java
 *
 *
 * @author 
 * @version 1.00 2018/5/4
 */

import java.awt.image.*;
import java.awt.image.*;
import java.awt.*;
import java.util.*;
public class Player {
	public int money=1000;
	Image[] walks;
	int direct=3;
	final int UP=1;
	final int LEFT=2;
	final int DOWN=3;
	final int RIGHT=4;
	String name="RED";
	Pokemon[] poke=new Pokemon[6];
	int[] backpack = {0,0,0,0};
	Pokemon[][] pcpokes=new Pokemon[9][12];
    public Player(Image[] sprites) {
    	walks=sprites;
    }
    public Image move(int frame,int dir){
    	if (dir==UP){
    		return walks[frame+7];
    
		}
		else if (dir==DOWN){
    		return walks[frame-1];
    
		}
		else if (dir==RIGHT){
    		return walks[frame+3];
    
		}
		else{
			return walks[frame+11];
		}
	}
	public void addPoke(Pokemon p){
		
		for (int i=0;i<6;i++){
			if (poke[i]==null){
				poke[i]=p;
				return;
			}
		}
		for(int i=0;i<9;i++){
			for (int j=0;j<12;j++){
				if(pcpokes[i][j]==null){
					pcpokes[i][j]=p;
					return;
				}
			}
		}
		
	}
	public boolean pokesAlive(){
		for(Pokemon p:poke){
			if(p!=null&&p.getHP()>0){
				return true;
			}
		}
		return false;
	}
	public Image stand(int dir){
		if (dir==UP){
    		return walks[9];
    
		}
		else if (dir==DOWN){
    		return walks[1];
    
		}
		else if (dir==RIGHT){
    		return walks[7];
    
		}
		else{	
			return walks[15];
		}
	}
	public Pokemon[] getPokes(){
		return poke;
	}
	
	public int getMoney(){
    	return money;
    }
    public void setMoney(int mon){
    	money=mon;
    }
    public String getStringMoney(){
    	return Integer.toString(money);
    }
    public void addItem(Item thing,int amount){
    	if(thing.getName().equals("Potion")){
			backpack[1]+=amount;				
		}
		else if(thing.getName().equals("Super Potion")){
			backpack[2]+=amount;				
		}
		else if(thing.getName().equals("Max Potion")){
			backpack[3]+=amount;				
		}
		else if(thing.getName().equals("Pokeball")){
			backpack[0]+=amount;				
		}
    }
	public int[] getBackpack(){
		return backpack;
	}
	public Pokemon[][] getPcPokes(){
		return pcpokes;
	}
	/*public void addItem(Item thing){
		backpack.add(thing);
	}
	public void removeItem(Item thing){
		for (int c=0;c<backpack.size();c++){
			if (backpack.get(c).equals(thing)){
				backpack.remove(c);
			}
		}
	}*/
}	