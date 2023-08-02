/**
 * @(#)Rewrite.java
 *
 *
 * @author 
 * @version 1.00 2018/5/18
 */

import java.util.Scanner;
import java.io.*;
public class Rewrite {
	static String[] finalarray=new String[620];
    public Rewrite() {
    }
    public static void main(String[] args){
    	try{
    		Scanner inFile=new Scanner(new BufferedReader(new FileReader("Pokemon moves.txt")));//reads in the pokemon.txt file 
	    	for (int i=0;i<620;i++){
	    		finalarray[i]=(inFile.nextLine());// splits at commas to seperate between different stats and adds it as a String[] to "allpokemon"
	    		
	    	}

    	}
    	catch(IOException ex){
    		System.out.println("Make sure mkmkm is in the same folder as this file");
    	}
    	try{
    		PrintWriter writer = new PrintWriter("blank.txt", "UTF-8");
    		for (int i=0;i<620;i++){
				if(!(finalarray[i].charAt(0)+"").equals(",")){
					String[] n=finalarray[i].split("\"");
					int count=0;
					for(String x:finalarray[i].split("")){
						if ((x+"").equals(",")){
							count+=1;
						}
					}
					for(int q=0;q<102-count;q++){
						finalarray[i]+=",";
					}
					if (n.length==3){
						String[] m=n[1].split(", ");
						String ans="";
						String sa="Sand Attack";
						for (int j=0;j<m.length;j++){
							if(m[j].equals("Sand-Attack")){
								m[j]=sa;
							}
							if(j!=m.length-1){
								ans+=m[j]+"-";
							}
							else{
								ans+=m[j];
							}
						}
						writer.println(n[0]+ans+n[2]);
					}
					else{
						writer.println(finalarray[i]);
					}
					
					
				}
		}
		writer.close();

    	}
    	catch(IOException ex){
    		System.out.println("Make sure is in the same folder as this file");
    	}
    	
		
    }
    
}