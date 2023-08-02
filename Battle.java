/*Battle.java
 *Namashi Sivaram and Will Jarvis-Cross
 *battle class that handles and keeps track
 *of a pokemon battles in stages consisting of the foes team and the players
 *main functions: calculates damage, applies move effects, and switches pokemon
 */
import java.util.Random;
import java.util.LinkedList;
import java.util.Arrays;
public class Battle {
	private int currentmon=0;
	private int foesmon=0;
	final Pokemon[] team;
	final Pokemon[] foe; 
	private int lightscreenp,reflectp,lightscreenf,reflectf=0;//variables pertaining to if a pokemons Lightscreen move or reflect move is still active
	//final move variables indicating what stage the battle is in
	
	final int MOVESELECT=1;
	final int EXECUTETURN=2;
	final int ACTIONSELECT=3;
	final int TEXTDISPLAYPLAYER=4;
	final int TEXTDISPLAYFOE=5;
	final int FOEATTACK=6;
	final int PLAYERATTACK=7;
	final int PARTYSELECT=8;
	final int ENDTURNTEXT=9;
	final int BATTLEOVER=10;
	final int FOEDEFEATED=11;
	final int CATCHING=12;
	final int LEARNINGMOVE=13;
	int stage=ACTIONSELECT;
	//final player variables
	final int PLAYER=1;
	final int FOE=2;
	private Moves playlastmove;//used for disabling moves and copying moves through mirror move
	private Moves foelastmove;
	boolean[][] battled=new boolean[6][6];//keeps track of who battled who to give exp at the end
	private double dmg;
	final String[][] typeeff;//array of type matchups
	
	Random rand=new Random();
	private LinkedList<String> battletext=new LinkedList<String>();//text to display at text display stages
	int turn=0;
	final String battype;//wild or trainer
	public Battle(Pokemon[] mons,Pokemon[] foemon,String[][] type,String battletype){
		team=mons;
		foe=foemon;
		battype=battletype;
		typeeff=type;
		for(int j=0;j<6;j++){//finds first alive pokemon on players team and makes that the one you send out
			if(mons[j]!=null&&mons[j].getHP()>0){
				currentmon=j;
				break;
			}
		}
		for (boolean[] row : battled)
            Arrays.fill(row, false);
	}
	/*attack:
	 *calculates if a move hits then how much damage it does then deals it
	 */
    public boolean attack(Moves move,int atkuser){
    	
    	Pokemon atker;//pokemon using attack
    	Pokemon dfder;//pokemon taking attack
    	double mod=0.85+(0.15)*rand.nextDouble();//some times moves will do more damage or less damage based on this mod
    	if (atkuser==PLAYER){//your attacking
    		atker=team[currentmon];
    		dfder=foe[foesmon];
    		
    		if(atker.getFlinch()){//if your flinched by your opponent eariler you cant attack
    			addText(atker.getName()+" FLINCHED AND COULDN'T MOVE");
    		
    			return false;
    		}
    		if (atker.getStatus()||atker.getConfuse()){//confusion will some times stop you from attacking and deal damage to you
    			if(moveWorked(atker)==false){//move didnt work
    				return false;
    			}	
    		}
    		
    		battletext.add(atker.name+" USED "+move.toString()+".");//move hits
    		move.setPP(move.getPP()-1);//uses up pp
    		if(move.equals("Dream Eater")){//special case, this move only works on asleep pokemon
    			if(dfder.getSleep()==false){
    				battletext.add("BUT IT FAILED");
    				return false;
    			}
    		}
    		if(move.getDisable()>0){//if your move is disabled it wont work
    			battletext.add("BUT IT FAILED");
    			return false;
    		}
    	}
    	else{//same as above but displays different text for foe if foe is attacking
    		dfder=team[currentmon];
    		atker=foe[foesmon];
    		
    		if(atker.getFlinch()){
    			addText(atker.getName()+" FLINCHED AND COULDN'T MOVE");
    			
    			return false;
    			
    		}
    		if (atker.getStatus()||atker.getConfuse()){
    			if(moveWorked(atker)==false){
    				return false;
    			}	
    		}
    		battletext.add("THE FOE "+" USED "+move.toString()+".");
    		move.setPP(move.getPP()-1);
    		if(move.equals("Dream Eater")){
    			if(dfder.getSleep()==false){
    				battletext.add("BUT IT FAILED");
    				return false;
    			}
    		}
    		if(move.getDisable()>0){
    			battletext.add("BUT IT FAILED");
    			return false;
    		}
    	}
    	if(atkuser==PLAYER){
    		playlastmove=move;//sets the move you just used to your last move
    	}
    	else{
    		foelastmove=move;
    	}
    	if(moveHit(move,atker,dfder)==false){//if you missed your move based on accuracy
    		if(move.effects!=null&&move.effects[0].equals("miss")){//special case, some moves if they miss deal damage to you
    			battletext.add(atker.getName()+" KEPT GOING AND CRASHED");
    			atker.setHP(Math.max(atker.getHP()-atker.getHPMax()/2,0));
    		}
    		return false;
    	}
    	double atkstat=atker.getAtkMod()*(double)atker.getAtk();//if the move is a physical move, uses the atkers attack stat and defenders defense stat
    	double defstat=dfder.getDefMod()*(double)dfder.getDef();
    	if(move.getMoveType().equals("Special")){
    		atkstat=atker.getSpatkMod()*(double)atker.getSpatk();//otherwise uses thier special defence and special attack stat
    		defstat=dfder.getSpdefMod()*(double)dfder.getSpdef();
    		mod*=typeEffect(move,dfder,true);//gets the type effectivness of the move eg water is good against rock
    		if(lightscreenf>0&&atkuser==PLAYER){//light screen cuts damage by special moves in half, one for each team as both teams can have a light screen up
    			mod*=0.5;
    		}
    		else if(lightscreenp>0&&atkuser==FOE){
    			mod*=0.5;
    		}
    	}
    	else if (move.getMoveType().equals("Physical")){
    		mod*=typeEffect(move,dfder,true);
    		if(atker.getBurn()){///burn cuts physical attack damage in half
    			mod*=0.5;
    		}
    		if(reflectf>0&&atkuser==PLAYER){//same as light screen but halves physical dmg
    			mod*=0.5;
    		}
    		else if(reflectp>0&&atkuser==FOE){
    			mod*=0.5;
    		}
    	}
    	else if(move.getMoveType().equals("Status")){//status moves do no damage
    		mod=0;
    		
    	}
    	if(atker.stab(move.getMoveType())){//if a pokemon uses a move of its same type, the move does more damage
    		mod*=1.5;
    	}
    	
    	int crit=rand.nextInt(16);//random chance of a move doing a lot more damage
    	if (crit==0&&!move.getMoveType().equals("Status")&&mod!=0.0){
    		mod*=1.5;
    		addText("ITS A CRITICAL HIT");
    	}
    	dmg=((((((double)2*(double)atker.getLvl())/5.0)+2)*move.getPow()*(atkstat/defstat)/50)+2)*mod;//pokemon formaula for calculating damage
    	
    	dmg=Math.min(dfder.getHP(),dmg);//cant have the pokemon go into negative hp
    	
    	
    	if(moveSpecialDmg(move,atker,dfder)){//some moves do a special type of damage eg fixed damage
    		return true;
    	}
    	dfder.setHP(dfder.getHP()-(int)dmg);//deals damage
    	
    	
		return true;//the move was succesfull
    }
    private boolean moveHit(Moves move,Pokemon user,Pokemon target){
    	
    	if(rand.nextDouble()*100<=move.getAcc()*(user.getAccMod()/target.getEvaMod())){//pokemon formual for if a move hits
    		return true;//move hits
    	}
    	if(!move.getMoveType().equals("Status")&&typeEffect(move,target,false)==0){//if the pokemon isnt affected at all by the moves type 
    		battletext.add("BUT IT HAD NO EFFECT");
    		return false;
    	}
    	battletext.add("BUT IT MISSED");//move didnt hit
    	return false;
    }
    private boolean moveSpecialDmg(Moves move,Pokemon atker,Pokemon dfder){
    	if(move.effects!=null){//if the move has an affect
    		if(move.effects[0].equals("multihit")){//move hits multiple times
    			double mod=0.85+(0.15)*rand.nextDouble();
    			mod*=typeEffect(move,dfder,true);
    			double atkstat=atker.getAtkMod()*(double)atker.getAtk();
    			double defstat=dfder.getDefMod()*(double)dfder.getDef();
    			for(int i=0;i<Integer.parseInt(move.effects[2]);i++){//range of how many times the move can hit
    				if(i>1&&rand.nextDouble()>0.25){//the move stops hitting
    					battletext.add("IT HIT"+i+"TIME(S)");
    					break;
    				}
    				dmg=((((((double)2*(double)atker.getLvl())/5.0)+2)*move.getPow()*(atkstat/defstat)/50)+2)*mod;//deals dmg every hit
    				dmg=Math.min(dfder.getHP(),dmg);
    				System.out.println(dmg+",multi");
    				dfder.setHP(dfder.getHP()-(int)dmg);
    			}
    			return true;
    		}
    		else if(move.effects[0].equals("fixed")){//move alwalys does fixed damage
    			if(move.effects[1].equals("lvl")){
    				dfder.setHP(Math.max(dfder.getHP()-atker.getLvl(),0));
    				return true;
    			}
    			dfder.setHP(Math.max(dfder.getHP()-Integer.parseInt(move.effects[1]),0));
    			return true;
    		}
    		else if(move.effects[0].equals("recoil")){//user gets hurt by its own move
    			double mod=0.85+(0.15)*rand.nextDouble();
    			mod*=typeEffect(move,dfder,true);
    			double atkstat=atker.getAtkMod()*(double)atker.getAtk();
    			double defstat=dfder.getDefMod()*(double)dfder.getDef();
    			dmg=((((((double)2*(double)atker.getLvl())/5.0)+2)*move.getPow()*(atkstat/defstat)/50)+2)*mod;
    			dmg=Math.min(dfder.getHP(),dmg);
    			dfder.setHP(dfder.getHP()-(int)dmg);
    			addText(atker.getName()+" WAS HURT BY RECOIL");
    			atker.setHP(Math.max(atker.getHP()-(int)(dmg/4),0));//subtracts some hp from the user depending on how much damage was dealt
    			return true;
    		}
    		else if(move.effects[0].equals("1hko")){//some moves are instant killers
    			dfder.setHP(0);
    			battletext.add("IT'S A 1-HIT KO!");
    			return true;
    		}

    	}
    	if(move.toString().equals("Super Fang")){//special case 
    		dfder.setHP(dfder.getHP()/2);//alwasy does halve remaining hp
    	}
    	return false;//the move passed in wasnt applicabel to any of these effects
    	
    }
    public Pokemon getMon(int player){//returns the pokemon being used by the foe or player
    	if (player==PLAYER){
    		return team[currentmon];
    	}
    	else{
    		return foe[foesmon];
    	}
    }
    public Pokemon getMon(int player,int pos){//returns a pokemon in a specific place on fighters team
    	if (player==PLAYER){
    		return team[pos];
    	}
    	else{
    		return foe[pos];
    	}
    }
    public boolean switchPokes(int player,int pos){//trys to switch the pokemon being used with the one specified
    	if (player==PLAYER){
    		if (pos==currentmon||team[pos]==null||team[pos].getHP()<=0){//if the pokemon trying to be swithced to is dead or doensnt exist
    			return false;
    		}
    		else{
    		getMon(PLAYER).reset();//restes stat changes
    		playlastmove=null;
    		battletext.add("COME BACK "+team[currentmon].getName()+"!");
    		battletext.add("GO "+team[pos].getName()+"!");
    		currentmon=pos;//switches pokemon
    		return true;//switch was successful
    		}
    	}
    	else{//foe is trying to switch
    		
    		if(pos>5||foe[pos]==null){//if the position is out of the team or the pokemon thier doesnt exist
    			
    			for(int i=0;i<6;i++){//finds first alive pokemon and switches into that
    				if(foe[i]!=null&&foe[i].getHP()>0){
    					pos=i;
    				}
    			}
    		}
    		
    		battletext.add("THE FOE SENT OUT "+foe[pos].getName());
    		
    		getMon(FOE).reset();
    		foelastmove=null;
    		foesmon=pos;
    		return true;
    	}
    }
    private boolean moveWorked(Pokemon p){//status effccst get in the way of a move being executed but the move can still work; checks if it worked
    	if(p.getPara()&&rand.nextDouble()<0.25){//if your paralyzed
    		battletext.add(p.getName()+" IS PARALYZED AND CANT MOVE");
    		return false;
    	}
    	else if(p.getSleep()){//if your asleep
    		p.setSleepTurns(p.getSleepTurns()+1);
    		if (p.getSleepTurns()==5){//if youve reached the max amount of turns to be asleep
    			p.setSleepTurns(0);
    			p.setSleep(false);//wakes you up
    			battletext.add(p.getName()+" WOKE UP");
    			return true;//can act now
    		}
    		else if(rand.nextDouble()>0.33){//chance you didnt wake up
    			
    			battletext.add(p.getName()+" IS ASLEEP");
    			return false;//cant act
    		
    		}
    		else{
    			p.setSleepTurns(0);
    			p.setSleep(false);//woke up
    			battletext.add(p.getName()+" WOKE UP");
    			return true;
    		}
    		
    	}
    	else if(p.getFreeze()){//if your frozen
    		if(rand.nextDouble()>0.20){//chance of escaping
    			
    			battletext.add(p.getName()+" IS FROZEN");
    			return false;
    		
    		}
    		else{
    			
    			p.setFreeze(false);
    			battletext.add(p.getName()+" THAWED OUT");
    			return true;
    		}
    	}
    	if (p.getConfuse()){//if your confused
    		battletext.add(p.getName()+" IS CONFUSED");
    		p.setConfuseTurns(p.getConfuseTurns()+1);
    		if(p.getConfuseTurns()==5||(rand.nextDouble()>0.2&&p.getConfuseTurns()!=1)){//if you reached the max confuse turns or got lucky
    			p.setConfuse(false);
    			p.setConfuseTurns(0);//no longer confused
    			battletext.add(p.getName()+" SNAPPED OUT OF IT'S CONFUSION");
    			return true;//can act
    		}
    			
    		if(rand.nextDouble()<0.2){
    			return true;
    		}
    		else{//you hit yourself
    			battletext.add(p.getName()+" HURT ITSELF IN CONFUSION");
    			double atkstat=p.getAtkMod()*(double)p.getAtk();
    			double defstat=p.getDefMod()*(double)p.getDef();
    			dmg=((((((double)2*(double)p.getLvl())/5.0)+2)*40*(atkstat/defstat)/50)+2);//deals damage to yourself equals to a 40 power move
    			dmg=Math.min(p.getHP(),dmg);
    			p.setHP(p.getHP()-(int)dmg);
    			return false;//move didnt work
    		}
    	}
    		
    		
    	return true;//move worked
    }
    	
    
    public double typeEffect(Moves move,Pokemon dfder,boolean forbattle){//calculates super effective, not very effective and no effect
    	String typem=move.getType();
    	String type1=dfder.getType1();
    	String type2=dfder.getType2();
    	double multip=1.0;//base effectivness damage multiplier
    	int typeind=0;
    	for(int i=0;i<typeeff.length;i++){//finds the attacking moves attacking effectivness from 2d array
    		if (typeeff[i][0].equals(typem)){
    			typeind=i;
    			break;
    		}
    	}
    	for (int j=0;j<3;j++){
    		for (int k=1;k<typeeff[typeind+j].length;k++){
    			if(typeeff[typeind+j][k].equals(type1)||typeeff[typeind+j][k].equals(type2)){//if one of the moves effectivness's matches the pokemons type
    				if(j==0){//the move has no effect on the mons type
    					multip=0.0;
    					
    				}
    				else if(j==1){//move isnt very effective
    					multip*=0.5;
    				}
    				else if(j==2){//move is super effective
    					multip*=2;
    				}
    			}
    		}
    	}
    	if(multip>1.0&&forbattle){
    		battletext.add("IT'S SUPER EFFECTIVE!");
    	}
    	else if (multip==0&&forbattle){
    		battletext.add("BUT IT HAD NO EFFECT");
    	}
    	else if(multip<1&&forbattle){
    		battletext.add("BUT IT'S NOT VERY EFFECTIVE");
    	}
    	
    	return multip;//returns multiplier
    }
    public int getStage(){//gets battle stage
    	return stage;
    }
    public void setStage(int s){
    	stage=s;
    }
    public String moveEffects(Moves move,int user,int effstartpos){//applies basic move effects
    	
    	Pokemon atker;
    	Pokemon dfder;
    	if (user==PLAYER){
    		atker=team[currentmon];
    		dfder=foe[foesmon];
    	}
    	else{
    		dfder=team[currentmon];
    		atker=foe[foesmon];
    	}
    	Pokemon target=dfder;//the pokemon being affected
    	String ans="";//tells text to add at end of applications
    	
    	
    	if(move.effects!=null){//if the move has an effect
    		String[] eff=move.effects;
    		if (eff[0].equals("s")){//if its a special case
    			
    			specialMoveCases(user,move);//applies the special case
    			return ans;
    		}
    		else if (eff[effstartpos].equals("user")){//otherwise 
    			target=atker;//if it does something to the user eg boost attack
    		}
    		if (eff[effstartpos+1].equals("hp")){//hp healing moves
    				if(eff[effstartpos+2].split("/")[0].equals("dmg")){//heals hp based on a percentage of dmg dealt
    					atker.setHP(Math.min((int)atker.getHPMax(),(int)(atker.getHP()+dmg/Integer.parseInt(eff[effstartpos+2].split("/")[1]))));//heals hp
    					battletext.add(atker.getName()+" GAINED SOME HP");
    					return ans;
    				}
    				else if((eff[effstartpos+2].split("/")[0].equals("hp"))){//heals a certain percent of max hp
    					atker.setHP(Math.min((int)atker.getHPMax(),(int)(atker.getHP()+atker.getHPMax()/Integer.parseInt(eff[effstartpos+2].split("/")[1]))));
    					battletext.add(atker.getName()+" GAINED SOME HP");
    					return ans;
    				}
    			}
    			else if(eff[effstartpos+1].equals("atk")){//affects attack stat
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+3])){//if the effect worked based on the moves effect chance 
    					target.atkmod=Math.min(target.atkmod+Integer.parseInt(eff[effstartpos+2]),12);//applies the attack mod, helpful or not
    					ans+="ATTACK "+Integer.parseInt(eff[effstartpos+2]);
    					
    					if(target.atkmod<0){target.atkmod=0;}//attack mod points to a multiplier in an array so has to stay in bounds
    				}
    			}
    			//following are exact same as attack mod but for different stats by changing a different variable in the pokemon object
    			else if(eff[effstartpos+1].equals("def")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+3])){
    					target.defmod=Math.min(target.defmod+Integer.parseInt(eff[effstartpos+2]),12);
    					ans+="DEFENSE "+Integer.parseInt(eff[effstartpos+2]);
    					if(target.defmod<0){target.defmod=0;}
    				}
    			}
    			else if(eff[effstartpos+1].equals("spatk")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+3])){
    					target.spatkmod=Math.min(target.spatkmod+Integer.parseInt(eff[effstartpos+2]),12);
    					ans+="SP.ATTACK "+Integer.parseInt(eff[effstartpos+2]);
    					if(target.spatkmod<0){target.spatkmod=0;}
    				}
    			}
    			else if(eff[effstartpos+1].equals("spdef")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+3])){
    					target.spdefmod=Math.min(target.spdefmod+Integer.parseInt(eff[effstartpos+2]),12);
    					ans+="SP.DEFENSE "+Integer.parseInt(eff[effstartpos+2]);
    					if(target.spdefmod<0){target.spdefmod=0;}
    				}
    			}
    			else if(eff[effstartpos+1].equals("spd")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+3])){
    					target.spdmod=Math.min(target.spdmod+Integer.parseInt(eff[effstartpos+2]),12);
    					ans+="SPEED "+Integer.parseInt(eff[effstartpos+2]);
    					if(target.spdmod<0){target.spdmod=0;}
    				}
    			}
    			else if(eff[effstartpos+1].equals("acc")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+3])){
    					target.accmod=Math.min(target.accmod+Integer.parseInt(eff[effstartpos+2]),12);
    					ans+="ACCURACY "+Integer.parseInt(eff[effstartpos+2]);
    					if(target.accmod<0){target.accmod=0;}
    				}
    			}
    			else if(eff[effstartpos+1].equals("eva")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+3])){
    					target.evamod=Math.min(target.evamod+Integer.parseInt(eff[effstartpos+2]),12);
    					ans+="EVASION "+Integer.parseInt(eff[effstartpos+2]);
    					if(target.evamod<0){target.evamod=0;}
    				}
    			}
    			//status effcts
    			else if(eff[effstartpos+1].equals("poison")){//move poisons foe
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+2])){//if the chance of poisoning based on the move was hit
    					if(target.getStatus()==false){//if the target isnt affected by any status conditions already eg sleep
    						target.setPoison(true);//makes them poisoned
    						target.setPoisonTurns(0);
    						ans+=target.getName()+" IS NOW POISONED.";
    						
    					}
    					else if(target.getStatus()){
    						ans+=target.getName()+" IS ALREADY POISONED.";
    					}
    					battletext.add(ans);
    					return ans;
    					
    				}
    			}
    			//following are exaxt same as applying poison but for different status such as para(paralyze) only diferece is variables changed and text
    			else if(eff[effstartpos+1].equals("para")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+2])){
    					if(target.getStatus()==false){
    						target.setPara(true);
    						ans+=target.getName()+" IS NOW PARALYZED.";
    						
    					}
    					else if(target.getStatus()||Double.parseDouble(eff[effstartpos+2])<1.0){
    						ans+=target.getName()+" IS ALREADY PARALYZED.";
    					}
    					battletext.add(ans);
    					return ans;
    					
    				}
    			}
    			else if(eff[effstartpos+1].equals("burn")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+2])){
    					if(target.getStatus()==false){
    						target.setBurn(true);
    						ans+=target.getName()+" IS NOW BURNED.";
    						
    					}
    					else if(target.getStatus()||Double.parseDouble(eff[effstartpos+2])<1.0){
    						ans+=target.getName()+" IS ALREADY BURNED.";
    					}
    					battletext.add(ans);
    					return ans;
    					
    				}
    			}
    			else if(eff[effstartpos+1].equals("confuse")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+2])){
    					if(target.getStatus()==false){
    						target.setConfuse(true);
    						ans+=target.getName()+" IS NOW CONFUSED.";
    						
    					}
    					else if(target.getConfuse()||Double.parseDouble(eff[effstartpos+2])<1.0){
    						ans+=target.getName()+" IS ALREADY CONFUSED.";
    					}
    					battletext.add(ans);
    					return ans;
    					
    				}
    			}
    			else if(eff[effstartpos+1].equals("sleep")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+2])){
    					if(target.getStatus()==false){
    						target.setSleep(true);
    						ans+=target.getName()+" IS NOW ASLEEP.";
    						
    					}
    					else if(target.getStatus()||Double.parseDouble(eff[effstartpos+2])<1.0){
    						ans+=target.getName()+" IS ALREADY ASLEEP.";
    					}
    					battletext.add(ans);
    					return ans;
    					
    				}
    			}
    			else if(eff[effstartpos+1].equals("freeze")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+2])){
    					if(target.getStatus()==false){
    						target.setFreeze(true);
    						ans+=target.getName()+" IS NOW FROZEN.";
    						
    					}
    					else if(target.getStatus()||Double.parseDouble(eff[effstartpos+2])<1.0){
    						ans+=target.getName()+" IS ALREADY FROZEN.";
    					}
    					battletext.add(ans);
    					return ans;
    					
    				}
    			}
    			else if(eff[effstartpos+1].equals("flinch")){
    				if(rand.nextDouble()<=Double.parseDouble(eff[effstartpos+2])){//flinch isnt a status effect so desnt check for already affected
    					
    					target.setFlinch(true);
    						
    					

    					
    					return ans;
    					
    				}
    			}
    		
    	
    	
    		if(!ans.equals("")){
    			String finalans="";
    			if ((user==PLAYER&&eff[effstartpos].equals("foe"))||(user==FOE&&eff[effstartpos].equals("user"))){
    				finalans+="FOE "+foe[foesmon].getName()+"'s "+ans.split(" ")[0]+"\n";
    				if(Integer.parseInt(ans.split(" ")[1])<0){//diplays whether the affect from a stat moding move was bad or good
    					finalans+=" FELL";
    				}
    				else{
    					finalans+="INCREASED.";
    				}
    			}
    			else if ((user==PLAYER&&eff[effstartpos].equals("user"))||(user==FOE&&eff[effstartpos].equals("foe"))){//same as above but text change for foe
    				finalans+=team[currentmon].getName()+"'s "+ans.split(" ")[0]+"\n";
    				if(Integer.parseInt(ans.split(" ")[1])<0){
    					finalans+=" FELL";
    				}
    				else{
    					finalans+=" INCREASED.";
    				}
    			}
    			battletext.add(finalans);//adds the text to display
    			if (effstartpos+4<eff.length){//if the move has more effects
    				moveEffects(move,user,effstartpos+4);//goes again starting from a new checking point
    			}
    		}
    		else if (eff[effstartpos].equals("recharge")){//the move makes you recharge next turn(cant attack)
    			atker.setRecharge(true);
    		}
    		
    	}
    		
    	return ans;	
    }
    	
    
    public boolean deathCheck(int playorfoe){//checks if the players pokemon is fainted or the foes
    	if (playorfoe==PLAYER){
    		if (team[currentmon].getHP()==0){
    			battletext.add(team[currentmon].getName()+" FAINTED.");
    			return true;
    		}
    	}
    	else if(playorfoe==FOE){
    		if (foe[foesmon].getHP()==0){
    			battletext.add("THE FOE'S "+foe[foesmon].getName()+" FAINTED.");
    			return true;
    		}
    	}
    	return false;
    }
    public LinkedList<String> getText(){//returns the text to be displayed 
    	return battletext;
    }
    
    public int getCurrentMon(){//get position on team of the fightint mon for foe or player
    	return currentmon;
    }
    public int getFoesMon(){
    	return foesmon;
    }
    public boolean battleOver() {//if the foes team is defeated
    	for (int i=0;i<6;i++){
    		if(foe[i]!=null){
    			System.out.println(foe[i].getHP());
    			if(foe[i].getHP()>0){
    				
    				return false;
    			}
    		}
    	}
    	
    	
    	return true;
    }
    public boolean teamDead(){//if your team is defeated
    	for (int i=0;i<6;i++){
    		if(team[i]!=null){
    			
    			if(team[i].getHP()>0){
    				
    				return false;
    			}
    		}
    	}
    	
    	
    	return true;
    	
    }
    public void addText(String t){
    	battletext.add(t);
    }
    private void specialMoveCases(int user,Moves move){//moves with special effects that are not generic
    	Pokemon atker=getMon(PLAYER);
    	Pokemon dfder=getMon(FOE);
    	if (user==FOE){
    		atker=getMon(FOE);
    		dfder=getMon(PLAYER);
    	}
    	if (move.toString().equals("Bind")){
    		if(dfder.getbindTurns()==0){//will do damage to affected every turn at end of turn, initates this
    			
    			dfder.setbindTurns(dfder.getbindTurns()+1);
    		}
    		
    	}
    	if (move.toString().equals("Clamp")){//same as bind but different vairable as these affects are stackable
    		if(dfder.getclampTurns()==0){

    			dfder.setclampTurns(dfder.getclampTurns()+1);
    		}
    		
    	}
    	if(move.toString().equals("Conversion")){//makes the pokemons type that of its first move
    		atker.setConversion(true);//will return that moves type if this is true
    		battletext.add(atker.getName()+" BECAME A "+atker.getMoves()[0].getType()+" TYPE");
    	}
    	if(move.toString().equals("Disable")){//makes a move unusable for a certain amount of turns
    		if(user==PLAYER){
    			if(foelastmove==null){battletext.add("BUT IT FAILED");}//if the foe hasnt used a move yet
    			else if(foelastmove.getDisable()>0){battletext.add("BUT IT FAILED");}//if the move is already disabled
    			else{foelastmove.setDisable(1);battletext.add("THE FOE's "+foelastmove+" IS NOW DISABLED");}//works
    		}
    		else{
    			if(playlastmove==null){battletext.add("BUT IT FAILED");}//same but used by foe
    			else if(playlastmove.getDisable()>0){battletext.add("BUT IT FAILED");}
    			else{playlastmove.setDisable(1);battletext.add(getMon(PLAYER).getName()+" "+playlastmove+" IS NOW DISABLED");}
    		}
    		
    	}
    	if(move.toString().equals("Dream Eater")){//heals some hp on a sleeping foe
    		atker.setHP(Math.min(atker.getHP()+(int)(dmg/2),atker.getHPMax()));
    		battletext.add(atker.getName()+" GAINED SOME HP");//heals
    	}
    	if(move.toString().equals("Explosion")||move.toString().equals("Selfdestruct")){//pokemon dies after using
    		System.out.println("will");
    		atker.setHP(0);
    		
    	}
    	if(move.toString().equals("Haze")){//resets all stat mods on both pokemon out
    		getMon(FOE).statReset();
    		getMon(PLAYER).statReset();
    		battletext.add("STATS WERE RESET");
    	}
    	else if(move.toString().equals("Leech Seed")){//heals some hp from foe every turn
    		if(dfder.getSeeded()==false){
    			dfder.setSeeded(true);
    			battletext.add(dfder.getName()+" WAS SEEDED");
    		}
    		else{//the target is already seeded
    			battletext.add("BUT IT FAILED");
    		}
    	}
    	else if(move.toString().equals("Reflect")){
    		if(user==FOE){
    			reflectf=1;
    			
    		}
    		else{
    			reflectp=1;
    		}
    	}
    	else if(move.toString().equals("Light Screen")){
    		if(user==FOE){
    			lightscreenf=1;
    			
    		}
    		else{
    			lightscreenp=1;
    		}
    	}
    	else if(move.toString().equals("Mirror Move")){//uses the last move by the opponent
    		if(playlastmove!=null&&user==FOE&&!playlastmove.toString().equals("Mirror Move")){//if thier was a move used and it wasnt mirror move and used by foe
    			Moves movebefore=playlastmove;
    			int ppbefore=playlastmove.getPP();//dont want to use pp from the actually move so resets after done
    			movebefore.setPP(movebefore.getPP()+1);
    			int wasdisabled=movebefore.getDisable();//mirror move can be used on disabled moves so undoes the disable and remembers how long the move was disabled for to reset it back
    			movebefore.setDisable(0);
    			if(attack(movebefore,FOE)){//uses the attack
    				moveEffects(movebefore,FOE,0);//applies its effects
    			}
    			movebefore.setDisable(wasdisabled);//rests the diable
    			foelastmove=move;
    			movebefore.setPP(ppbefore);//restes the pp
    			
    		}
    		else if(foelastmove!=null&&user==PLAYER&&!foelastmove.toString().equals("Mirror Move")){//same but if used by player
    			Moves movebefore=foelastmove;
    			int ppbefore=foelastmove.getPP();
    			movebefore.setPP(movebefore.getPP()+1);
    			int wasdisabled=movebefore.getDisable();
    			movebefore.setDisable(0);
    			if(attack(movebefore,PLAYER)){
    				moveEffects(movebefore,PLAYER,0);
    			}
    			movebefore.setDisable(wasdisabled);
    			playlastmove=move;
    			movebefore.setPP(ppbefore);
    		}
    		else{
    			addText("BUT IT FAILED");
    		}
    		
    	}
    	else if(move.toString().equals("Whirlwind")||move.toString().equals("Roar")){//switches out a pokemon into a diffrent random one from the affecteds team
    		if(user==FOE){//foe is using it
    			if(battype.equals("Wild")){
    				setStage(BATTLEOVER);
    			}
    			else{
	    			LinkedList<Integer>pokeswitch=new LinkedList<Integer>();//list of possible switches
	    			for(int j=0;j<6;j++){
	    				if(j!=currentmon&&team[j]!=null&&team[j].getHP()>0){//adds all alive pokemon to the list except the pokemon currentlt out
	    					pokeswitch.add(j);
	    				}
	    			}
	    			if(pokeswitch.size()>0){//if there are any switches possible
	    				int poksw=rand.nextInt(pokeswitch.size());
	    				currentmon=pokeswitch.get(poksw);//swithces the pokemon
	    				addText(getMon(PLAYER).getName()+" WAS DRAGGED OUT");
	    				turn=2;
	    			}
	    			else{
	    				addText("BUT IT FAILED");
	    			}
    			}
    			
    		}
    		else{//same but if used by player
    			if(battype.equals("Wild")){
    				
    				setStage(BATTLEOVER);
    			}
    			else{
	    			LinkedList<Integer>pokeswitch=new LinkedList<Integer>();
	    			for(int j=0;j<6;j++){
	    				if(j!=foesmon&&foe[j]!=null&&foe[j].getHP()>0){
	    					pokeswitch.add(j);
	    				}
	    			}
	    			if(pokeswitch.size()>0){
	    				int poksw=rand.nextInt(pokeswitch.size());
	    				foesmon=pokeswitch.get(poksw);
	    				addText(getMon(FOE).getName()+" WAS DRAGGED OUT");
	    				turn=2;
	    			}
	    			else{
	    				addText("BUT IT FAILED");
	    			}
    			}
    		}
    	}
    	else if(move.toString().equals("Rest")){//puts the pokemon to sleep and heals its hp to full
    		if(atker.getHP()<atker.getHPMax()){//if the mon has any hp to heal
    			atker.setHP(atker.getHPMax());//heals them completly
    			atker.statusHeal();
    			atker.setSleepTurns(1);//puts them to sleep
    			atker.setSleep(true);
    			battletext.add(atker.getName()+" SLEPT AND BECAME HEALTHY");
    		}
    		else{
    			battletext.add("BUT IT FAILED");
    		}
    	}
    }
    public void statusPoisonBurn(){//used to deal effect damge at the end of a battle turn
    	Pokemon applymon=foe[foesmon];
    	int on=FOE;
    	for(int j=0;j<2;j++){
    		if(applymon.getSeeded()){//pokemon is leech seeded
    			int seeddmg=Math.min(applymon.getHP(),applymon.getHPMax()/8);//damage delat by leech seed
    			applymon.setHP(Math.max(0,applymon.getHP()-seeddmg));
    			battletext.add(applymon.getName()+" HAD ITS ENERGY DRAINED");
    			if(on==FOE){
    				getMon(PLAYER).setHP(Math.min(getMon(PLAYER).getHPMax(),getMon(PLAYER).getHP()+seeddmg));//heals the opposing pokemon by the amount dealt
    				battletext.add(getMon(PLAYER).getName()+" GAINED SOME HP");
    			}
    			else{
    				getMon(FOE).setHP(Math.min(getMon(FOE).getHPMax(),getMon(FOE).getHP()+seeddmg));
    				battletext.add(getMon(FOE).getName()+" GAINED SOME HP");
    			}
    		}
    		if(applymon.getFlinch()){//flinches dont carry on between turns
    			applymon.setFlinch(false);
    		}
    		if (applymon.getPoison()){
    			applymon.setHP(Math.max(0,applymon.getHP()-applymon.getHPMax()/8));//poison damage
    			battletext.add(applymon.getName()+" IS HURT BY IT'S POISON.");
    		}
    		else if (applymon.getBurn()){
    			applymon.setHP(Math.max(0,applymon.getHP()-applymon.getHPMax()/8));//burn damage
    			battletext.add(applymon.getName()+" IS HURT BY IT'S BURN.");
    		}
    		if(applymon.getclampTurns()>0){
    			applymon.setclampTurns(applymon.getclampTurns()+1);
    			if(applymon.getclampTurns()==4){//if max clamp turns was reached
    				applymon.setclampTurns(0);//ends the clamp
    				battletext.add(applymon.getName()+" BROKE FREE OF THE CLAMP");
    			}
    			else{
    				applymon.setHP(Math.max(0,applymon.getHP()-applymon.getHPMax()/16));//otherwise deals clamp dmg
    				battletext.add(applymon.getName()+" IS HURT BY THE CLAMP");
    			}
    		}
    		if(applymon.getbindTurns()>0){
    			applymon.setbindTurns(applymon.getbindTurns()+1);
    			if(applymon.getbindTurns()==4){//if max bind turns was reached
    				applymon.setbindTurns(0);//ends the bind
    				battletext.add(applymon.getName()+" BROKE FREE OF THE BIND");
    			}
    			else{
    				applymon.setHP(Math.max(0,applymon.getHP()-applymon.getHPMax()/16));//otherwise deals bind dmg
    				battletext.add(applymon.getName()+" IS HURT BY THE BIND");
    			}
    		}
    		
    		for(int i=0;i<applymon.getMoves().length;i++){//goes through moves and undoes disables where applicable
    			if(applymon.getMoves()[i]!=null&&applymon.getMoves()[i].getDisable()>0){
    				applymon.getMoves()[i].setDisable(applymon.getMoves()[i].getDisable()+1);
    				if(applymon.getMoves()[i].getDisable()==4){//the move was disabled and its max disable turns were reached
    					applymon.getMoves()[i].setDisable(0);//ends the disable
    					battletext.add(applymon.getName()+" "+applymon.getMoves()[i]+" IS NO LONGER DISABLED");
    				}
    			}
    		}

    		applymon=team[currentmon];//now switches and applies the effects to the player
    		on=PLAYER;
    	}
    	//checks if light screen and reflect turns have expired for both teams
    	if(lightscreenp>0){
    		lightscreenp+=1;
    		if(lightscreenp==6){
    			addText("YOUR LIGHTSCREEN WORE OFF");
    			lightscreenp=0;
    		}
    		
    	}
    	if(lightscreenf>0){
    		lightscreenf+=1;
    		if(lightscreenf==6){
    			addText("THE FOE'S LIGHTSCREEN WORE OFF");
    			lightscreenf=0;
    		}
    		
    	}
    	if(reflectp>0){
    		reflectp+=1;
    		if(reflectp==6){
    			addText("YOUR REFLECT WORE OFF");
    			reflectp=0;
    		}
    		
    	}
    	if(reflectf>0){
    		reflectf+=1;
    		if(reflectf==6){
    			addText("THE FOE'S REFLECT WORE OFF");
    			reflectf=0;
    		}
    		
    	}
    	
    	
    	
    }
    
    
}