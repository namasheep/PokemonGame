/*Moves.java
 *Namashi Sivaram and Will Jarvis-Cross
 *creates a move object that has a specified power, accurcy,
 *damage and effect as well as PP and type
 */
public class Moves {
	int dmg,pp,acc,ppmax;
	String name,movetype,type;//attacks name,wheter its physical special or status, its type
	int disable=0;//if the moves disabled unusable
	final String[] effects;
    public Moves(String[] info) {
    	//System.out.println(info[0]);
    	name=info[0];
    	type=info[1];
    	movetype=info[2];
    	if (!info[3].equals("—")){dmg=Integer.parseInt(info[3]);}
    	else{dmg=-1;}
    	if (!info[4].equals("—")){acc=Integer.parseInt(info[4]);}
    	else{acc=1000;}
    	if (!info[5].equals("—")){pp=Integer.parseInt(info[5]);ppmax=pp;}
    	else{pp=-1;}
    	if (info.length==8){
    		effects=info[7].split(" ");			
    	}
    	else{
    		effects=null;
    	}
    	
    }
    //setters and getters
    public int hashCode(){
    	return name.hashCode();
    }
    public String toString(){
    	return name;
    }
    public int getPP(){
    	return pp;
    }
    public void setPP(int i){
    	pp=i;
    }
    public int getPPMax(){
    	return ppmax;
    }
    public int getPow(){
    	return dmg;
    }
    public int getAcc(){
    	return acc;
    }
    public String getType(){
    	return type;
    }
    public String getMoveType(){
    	return movetype;
    }
    public int getDisable(){
    	return disable;
    }
    public void setDisable(int tf){
    	disable=tf;
    }
    
    
}