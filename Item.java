/*
 *Item.java
 *Will Jarvis-Cross and Namashi Sivaram
 *Items are in the player's backpack and can be used in 
 *battle to heal pokemon or catch wild pokemon. Each 
 *item has its picture, price and description.
 */

import java.awt.*;
public class Item {
	final String name;
	final Image pic;
	final int price;
	final String[] descript;
	
    public Item(String itemName,Image itemPic,int itemPrice,String description) {
    	name=itemName;
    	pic=itemPic;
    	price=itemPrice;
    	descript=description.split(",");
    }
    public String getName(){
    	return name;
    }

}
