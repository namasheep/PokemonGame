/*Pokemon.java
 *Namashi Sivaram and Will Jarvis-Cross
 *creates a pokemon object to be used in the pokemon tron heroes game
 *each pokemon has its own moves(editable),type, and stats. Pokemon also have
 *iv values which influence thier final stat to give the same type of pokemon
 *individuality
 */

import java.awt.image.*;
import java.awt.*;
import java.util.LinkedList;
public class Pokemon {
	String name,type1,type2,ability,evolvefrom;
	private int atk,def,spatk,spdef,spd,expv,evolve;
	private int hp,exp,lvl,hpmax;
	final int catchrate;
	final int index;
	private Moves[] moves=new Moves[4];
	private int[] iv=new int[6];
	final Image frontsprite;
	final Image backsprite;
	Image minisprite;
	//stat modifiers
	int atkmod=6;
	int defmod=6;
	int spatkmod=6;
	int spdefmod=6;
	int spdmod=6;
	int evamod=6;
	int accmod=6;
	//status flags
	private boolean flinch=false;
	private boolean recharge=false;
	private boolean seeded=false;
	private boolean bind=false;
	private int bidedmg=0;
	private int bindturns=0;
	private int clampturns=0;
	private boolean poison=false;
	private int poiturns=0;
	private boolean sleep=false;
	private int sleepturns=0;
	private boolean para=false;
	private int paraturns=0;
	private boolean burn=false;
	private int burnturns=0;
	private boolean confuse=false;
	private boolean freeze=false;
	private int confuseturns=0;
	private int earnedexp=0;
	private boolean conversion=false;
	LinkedList<Pokemon> battledagainst=new LinkedList<Pokemon>();
	double[] statstages={2.0/8.0,2.0/7.0,2.0/6.0,2.0/5.0,2.0/4.0,2.0/3.0,2.0/2.0,3/2.0,4.0/2.0,5.0/2.0,6.0/2.0,7.0/2.0,8.0/2.0};
	double[] acceva={3.0/9.0,3.0/8.0,3.0/7.0,3.0/6.0,3.0/5.0,3.0/4.0,3.0/3.0,4.0/3.0,5.0/3.0,6.0/3.0,7.0/3.0,8.0/3.0,9.0/3.0};
	
    public Pokemon(String[] stats,int lvl,Moves[] moves,int[] ivs, Image fsprt,Image bsprt,Image msprt) {
    	index=Integer.parseInt(stats[1])-1;
    	minisprite=msprt;
    	name=stats[2];
    	hp=Integer.parseInt(stats[3]);
    	
    	atk=Integer.parseInt(stats[4]);
    	def=Integer.parseInt(stats[5]);
    	spatk=Integer.parseInt(stats[6]);
    	spdef=Integer.parseInt(stats[7]);
    	spd=Integer.parseInt(stats[8]);
    	type1=stats[10].toUpperCase();
    	type2=stats[11].toUpperCase();
    	System.out.println(type2);
    	ability=stats[13];
    	expv=Integer.parseInt(stats[19]);
    	catchrate=Integer.parseInt(stats[25]);
    	exp=Integer.parseInt(stats[26]);
    	if(!stats[27].equals("")&&!stats[27].equals("N")){
    		if (stats[27].split(" ")[0].equals("Lv.")){
    			evolve=Integer.parseInt(stats[27].split(" ")[1]);
    		}
    		
    	}
    	evolvefrom=stats[34];
    	this.lvl=lvl;
    	this.moves=moves;
    	frontsprite=fsprt;
    	backsprite=bsprt;
    	iv=ivs;
    	hp=(int)(((2*hp+iv[0]+(42.0/4.0))*lvl)/100)+10+lvl;
    	System.out.println(hp);
    	atk=(int)(((2*atk+iv[1]+(42.0/4.0))*lvl)/100)+5;
    	System.out.println(atk);
    	def=(int)(((2*def+iv[2]+(42.0/4.0))*lvl)/100)+5;
    	System.out.println(def);
    	spatk=(int)(((2*spatk+iv[3]+(42.0/4.0))*lvl)/100)+5;
    	System.out.println(spatk);
    	spdef=(int)(((2*spdef+iv[4]+(42.0/4.0))*lvl)/100)+5;
    	System.out.println(spdef);
    	spd=(int)(((2*spd+iv[5]+(42.0/4.0))*lvl)/100.0)+5;
    	System.out.println(spd);
    	hpmax=hp;
    	setExp();
    }
    public String toString(){
    	String ans="";
    	ans+=name+": ";
    	ans+=hp+"\n";
    	for (int i=0;i<4;i++){
    		if (moves[i]!=null){
    			ans+=i+". "+moves[i].toString()+"\n";
    			
    		}
    		
    	}
    	return ans;
    }
    //setters and getters
    public String getName(){
    	return name;
    }
    public String getType1(){
    	if(conversion){
    		return moves[0].getType();
    	}
    	return type1;
    }
    public String getType2(){
    	return type2;
    }
    public int getAtk(){
    	if (burn){
    		return atk/2;
    	}
    	return atk;
    }
    public int getDef(){
    	return def;
    }
    public int getSpatk(){
    	return spatk;
    }
    public int getSpdef(){
    	return spdef;
    }
    public int getSpd(){
    	if (para){
    		return spd/2;
    	}
    	return spd;
    }
    public int getLvl(){
    	return lvl;
    }
    public double getAtkMod(){
    	System.out.println(atkmod+","+statstages[atkmod]);
    	return statstages[atkmod];
    }
    public double getDefMod(){
    	return statstages[defmod];
    }
    public double getSpatkMod(){
    	return statstages[spatkmod];
    }
    public double getSpdefMod(){
    	return statstages[spdefmod];
    }
    public double getSpdMod(){
    	return statstages[spdmod];
    }
    public double getEvaMod(){
    	return acceva[evamod];
    }
    public double getAccMod(){
    	return acceva[accmod];
    }
	public boolean getFlinch(){
		return flinch;
	}
	public void setFlinch(boolean tf){
		flinch=tf;
	}
    public boolean getPoison(){
    	return poison;
    }
    public boolean getBurn(){
    	return burn;
    }
    public boolean getPara(){
    	return para;
    }
    public boolean getSleep(){
    	return sleep;
    }
    public boolean getFreeze(){
    	return freeze;
    }
    public boolean getConfuse(){
    	return confuse;
    }
    public boolean getStatus(){
    	if(para||burn||poison||freeze||sleep){
    		return true;
    	}
    	return false;
    }
    public void setSeeded(boolean t){
    	seeded=t;
    }
    public boolean getSeeded(){
    	return seeded;
    }
    public void setPoison(boolean t){
    	poison=t;
    }
    public void setBurn(boolean t){
    	burn=t;
    }
    public void setPara(boolean t){
    	para=t;
    }
    public void setSleep(boolean t){
    	sleep=t;
    }
    public void setConfuse(boolean t){
    	confuse=t;
    }
    public void setFreeze(boolean t){
    	freeze=t;
    }
    public int getPoisonTurns(){
    	return poiturns;
    }
    public int getBurnTurns(){
    	return burnturns;
    }
    public int getParaTurns(){
    	return paraturns;
    }
    public int getSleepTurns(){
    	return sleepturns;
    }
    public int getConfuseTurns(){
    	return confuseturns;
    }

    public void setPoisonTurns(int i){
    	poiturns=i;
    }
    public void setBurnTurns(int i){
    	burnturns=i;
    }
    public void setParaTurns(int i){
    	paraturns=i;
    }
    public void setSleepTurns(int i){
    	sleepturns=i;
    }
    public void setConfuseTurns(int i){
    	confuseturns=i;
    }
    public void setHP(int h){
    	hp=h;
    	
    }
    public int getHP(){
    	return hp;
    	
    }
    public int getHPMax(){
    	return hpmax;
    	
    }
    private void setExp(){
    	if (exp==800000){
    		 earnedexp=(int)(4*Math.pow(lvl,3))/5;
    	}
    	else if (exp==1059860){
    		 earnedexp=(int)Math.abs((6.0/5.0)*Math.pow(lvl,3)-15*Math.pow(lvl,2)+100*(lvl)-140);
    	}
    	else if (exp==1250000){
    		 earnedexp=(int)(5*Math.pow(lvl,3))/4;
    	}
    	else {
    		 earnedexp=(int)Math.pow(lvl,3);
    	}
    	
    }
    public boolean Levelup(){//returns if a pokemon can level up and applies it
    	if(getExpEarned()>=toNextLvl()){
    		lvl+=1;
    		return true;
    	}
    	return false;
    }
    public int toNextLvl(){//returns how much exp is needed for a mon based on exp growth rate formulas
    	if (lvl==100){
    		return 0;
    	}
    	if (exp==800000){
    		return (int)(4*Math.pow(lvl+1,3))/5-(int)(4*Math.pow(lvl,3))/5;
    	}
    	else if (exp==1059860){
    		
    		return (int)Math.abs((6.0/5.0)*Math.pow(lvl+1,3)-15*Math.pow(lvl+1,2)+100*(lvl+1)-140)-(int)Math.abs((6.0/5.0)*Math.pow(lvl,3)-15*Math.pow(lvl,2)+100*(lvl)-140);
    	}
    	else if (exp==1250000){
    		return (int)(5*Math.pow(lvl+1,3))/4-(int)(5*Math.pow(lvl,3))/4;
    	}
    	else {
    		return (int)Math.pow(lvl+1,3)-(int)Math.pow(lvl,3);
    	}
    	
    	
    }
    public int getExpEarned(){//returns how much exp a pokemon has at its lvl based on the mons exp growth rate
    	if (lvl==100){
    		return 0;
    	}
    	if (exp==800000){
    		return earnedexp-(int)(4*Math.pow(lvl,3))/5;
    	}
    	else if (exp==1059860){
    		
    		return earnedexp-(int)Math.abs((6.0/5.0)*Math.pow(lvl,3)-15*Math.pow(lvl,2)+100*(lvl)-140);
    		
    	}
    	else if (exp==1250000){
    		return earnedexp-(int)(5*Math.pow(lvl,3))/4;
    	}
    	else {
    		return earnedexp-(int)Math.pow(lvl,3);
    	}
    }
    public int getExpVal(){
    	return expv;
    }
    public void addBattled(Pokemon foe){
    	battledagainst.add(foe);
    }
    public int[] getIvs(){
    	return iv;
    }
    public void setIvs(int[] i){
    	iv=i;
    }
    public void setMoves(Moves[] i){
    	moves=i;
    }
    public Moves[] getMoves(){
    	return moves;
    }
    public void addExp(int ex){
    	earnedexp+=ex;
    }
    public void setExp(int ex){
    	earnedexp=ex;
    }
    public int getbindTurns(){
    	return bindturns;
    }
    public void setbindTurns(int t){
    	bindturns=t;
    }
    public int getclampTurns(){
    	return clampturns;
    }
    public void setclampTurns(int t){
    	clampturns=t;
    }
    public void setConversion(boolean tf){
    	conversion=tf;
    }
    public boolean getConversion(){
    	return conversion;
    }
    public void reset(){//used when a pokemon is switched out of battle
    	flinch=false;
    	seeded=false;
    	confuse=false;
    	confuseturns=0;
    	atkmod=6;
    	defmod=6;
    	spdefmod=6;
    	spatkmod=6;
    	spdmod=6;
    	clampturns=0;
    	bindturns=0;
    	evamod=6;
    	accmod=6;
    	recharge=false;
    	for(Moves m:moves){
    		if(m!=null){
    			m.setDisable(0);
    		}
    		
    		
    	}
    	conversion=false;
    }
    public void statReset(){//restes all stat mods
    	atkmod=6;
    	defmod=6;
    	spdefmod=6;
    	spatkmod=6;
    	spdmod=6;
    	evamod=6;
    	accmod=6;
    }
    public boolean getRecharge(){
    	return recharge;
    }
    public void setRecharge(boolean tf){
    	recharge=tf;
    }
    public void heal(){//fully heals them
    	reset();
    	poison=false;
    	burn=false;
    	para=false;
    	confuse=false;
    	freeze=false;
    	for(Moves m:moves){
    		if(m!=null){
    			m.setPP(m.getPPMax());
    		}
    	}
    	hp=hpmax;
    	
    }
    public void statusHeal(){//heals a pokemons status's
    	poison=false;
    	para=false;
    	freeze=false;
    	burn=false;
    	sleep=false;
    }
    public boolean noMoves(){//whether the pokemon has any usable moves left (have pp and arnet disabled)
    	if((getMoves()[0]==null||getMoves()[0].getPP()<=0)&&(getMoves()[1]==null||getMoves()[1].getPP()<=0)&&(getMoves()[2]==null||getMoves()[2].getPP()<=0)&&(getMoves()[3]==null||getMoves()[3].getPP()<=0)){
    		return true;
    	}
    	return false;
    }
    public boolean stab(String mvtype){//checks if move type matches pokemon type
    	if(type1.equals(mvtype)){
    		return true;
    	}
    	if(type2.equals(mvtype)){
    		return true;
    	}
    	if(getType1().equals(mvtype)){
    		return true;
    	}
    	return false;
    	
    }
    
}////////////////////////////////////////////////////////////////////////////////////////////////add move pp reset