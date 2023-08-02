/*
 *PokemonTronHeroes.java
 *Will Jarvis-Cross and Namashi Sivaram
 *
 *This game is based off of pokemon. The player starts with some low level
 *pokemon and they travel throughout the substantial map battling trainers
 *and wild pokemon to get experience. They can buy items in the shop that
 *they can use in the game such as healing potions and pokeballs to catch
 *the 151 available wild pokemon. They can heal their pokemon at the
 *pokecenter and put extra pokemon in the pc if they've run out of spots.
 *The player's ultimate goal is to beat the four gyms which increase in difficulty.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Hashtable;
import java.applet.*;
public class PokemonTronHeroes extends JFrame implements ActionListener, KeyListener{
	Timer myTimer;
	GamePanel game;


    public PokemonTronHeroes() {
    	super("Move the Box");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(16*16,16*12);
		addKeyListener(this);

		myTimer = new Timer(10, this);	 // trigger every 100 ms
		myTimer.start();

		game = new GamePanel();
		add(game);

		setResizable(false);
		setVisible(true);
    }

    public void keyTyped(KeyEvent e) {}//checks to see if something was typed

    public void keyPressed(KeyEvent e) {//checks to see if a key was pressed
    	game.setKey(e.getKeyCode(),true);
    }
    public void keyReleased(KeyEvent e) {//checks to see if a key was released
    	game.setKey(e.getKeyCode(),false);
    }
    public void actionPerformed(ActionEvent evt){
		if(game!= null){

			game.refresh();
			game.repaint();
		}
	}

    public static void main(String[] arguments) {

		PokemonTronHeroes frame = new PokemonTronHeroes();
    }


}
class GamePanel extends JPanel implements MouseListener{
	GameMap main,home,pokeCenter,shop,trainerHouse,gym,gym2,gym3,gym4;//map objects for overworld
	Item pokeball,potion,superPotion,maxPotion;//items used in battle
	public boolean ready=false;
	private boolean[] keys;//used to see what keys are pressed
	//directions the player is moveing in
	int movedir=3;
	final int UP=1;
	final int LEFT=2;
	final int DOWN=3;
	final int RIGHT=4;
	//options for the player when in battle
	final int FIGHT=0;
	final int PKMN=2;
	final int RUN=3;
	final int BAG=1;
	boolean moving,textTalk=false;//whether your moveing,talking
	int framecount,trainerFrame,trainerMoveDir=0;
	boolean printed,savegame=false;
	int battleselx=0;//used to locate a selection in a 2d array
	int battlesely=0;
	//pokemon information
	String[][] allpokes=fileRead("text files/pokemonVals.txt",151,",");
	String[][] almoves=fileRead("text files/Moves.txt",165,",");
	String[][] lvlmoves=fileRead("text files/Pokemon moves.txt",151,",");
	String[][] typeeffect=fileRead("text files/types.txt",51,",");

	Hashtable<String, String[]> allmoves=new Hashtable<String, String[]>();
	Random rand=new Random();
	Player p;
	int battexttodisp,gymsCompleted=0;//used to find which text to display in a array of text during battle, tells how far in game player has progressed
	Pokemon[] playerpokes;
	Pokemon[] enemypokes=new Pokemon[6];//array for foes team
	//pokemon sprites
	Image[] minisprites=new Image[151];
	Image[] frontsprite=new Image[151];
	Image[] backsprites=new Image[151];

	Image battleback=new ImageIcon("images/Battle Backs/back1.png").getImage();
	Image menu=new ImageIcon("images/menus/main menu.png").getImage();
	Image fightmenu=new ImageIcon("images/menus/fight menu.png").getImage();
	Image attackmenu=new ImageIcon("images/menus/attack menu.png").getImage();
	Image selector=new ImageIcon("images/button.png").getImage();
	Image partyback=new ImageIcon("images/party menu/party menu.png").getImage();
	Image highfirstparty=new ImageIcon("images/party menu/1st party select h.png").getImage();
	Image firstparty=new ImageIcon("images/party menu/1st party select.png").getImage();
	Image highregparty=new ImageIcon("images/party menu/h regular party.png").getImage();
	Image regparty=new ImageIcon("images/party menu/regular party.png").getImage();
	Image playerhpbar=new ImageIcon("images/player hp.png").getImage();
	Image foehpbar=new ImageIcon("images/foe hp.png").getImage();
	Image poisonicon=new ImageIcon("images/status/poison.png").getImage();
	Image paraicon=new ImageIcon("images/status/para.png").getImage();
	Image freezeicon=new ImageIcon("images/status/freeze.png").getImage();
	Image sleepicon=new ImageIcon("images/status/sleep.png").getImage();
	Image burnicon=new ImageIcon("images/status/burn.png").getImage();
	Image moneyRec=new ImageIcon("images/shopStuff/moneyRec.png").getImage();
	Image money=new ImageIcon("images/shopStuff/money.png").getImage();
	Image itemRec=new ImageIcon("images/shopStuff/textBox2.png").getImage();
	Image speechRec=new ImageIcon("images/shopStuff/textBox.png").getImage();
	Image moneyBox=new ImageIcon("images/shopStuff/moneyBox.png").getImage();
	Image shopBack=new ImageIcon("images/shopStuff/shopBack.jpg").getImage();
	Image whiteBox=new ImageIcon("images/shopStuff/whiteBox.png").getImage();
	Image learningmenu=new ImageIcon("images/menus/learningmenu.png").getImage();
	Image bagmenu=new ImageIcon("images/menus/bag menu.png").getImage();
	Image bag=new ImageIcon("images/menus/bag.png").getImage();
	Image pcbox=new ImageIcon("images/pc/pc box.png").getImage();
	Image pcparty=new ImageIcon("images/pc/pc party.png").getImage();
	Image pcback=new ImageIcon("images/pc/pcback.png").getImage();
	Image overMap=new ImageIcon("images/maps/overMap.png").getImage();

	int selectedpokemony=0;//used to specify what switching pokemon location when using pc to swap out pokemon
	int selectedpokemonx=0;
	Pokemon switchingpokemon;
	boolean chosepcpoke=false;//using the pc
	boolean running=false;//whether player is running in overworld
	ArrayList<Pokemon> pokelearning=new ArrayList<Pokemon>();//array of pokemon that need to learn a move
	ArrayList<Moves> movelearning=new ArrayList<Moves>();//the move the learnig pokemon is learning
	ArrayList<Pokemon> pokeevolve=new ArrayList<Pokemon>();// pokemon that need to evolve
	int evolvepoke=0;//pokemon currently evolving
	int actionframes=0;///for animations
	boolean turnone=true;//first turn of battle
	int learningm=0;//current move being learnt by a pokemon
	boolean bagopen=false;///////////////////////////////////////
	int itemusing=-1;/////////
	boolean catching=true;/////
	boolean caught,changePokemon,pokeHeal=false;//////////////////////////////////////
	int purchaseNum=1;
	int mx,my;
	Pokemon pok;
	Pokemon partypok;
	int opt=0;//the option chosen when using 2d array option selection method
	int itemNum=0;
	int textdispframes=0;//used to auto read through text
	Battle bat;
	Hashtable<Color,Warp> tileLoc=new Hashtable<Color,Warp>();//holds the colour of a tile and the spot where you warp to according to the colour (when going to a different map)
	Hashtable<Color,EncounterGrass> grassColour=new Hashtable<Color,EncounterGrass>();////holds the colour of the tile and the type and level of pokemon you can encounter when on that tile

	GameMap[] maps=new GameMap[9];
	public int mapNum=1;//the map that's being displayed on the screen
	public Color maskCol;//the colour tile the player's on
	BufferedImage[] bufferedImages=new BufferedImage[9];//these are the masks of the maps
	Item[] allItems=new Item[4];//the items that the user can use during battle
	boolean speech,shopMenu,buyNum=false;
	NPC npcDraw,person=null;//npcDraw is the npc that is on the screen
	NPC[] characters=new NPC[16];//includes both trainers and gymPeople
	NPC[] trainers=new NPC[12];
	NPC[] gymPeople=new NPC[4];

	Color red=new Color(254,0,0);
	Color green=new Color(60,241,14);
	Color orange=new Color(255,156,0);
	Color lightBlue=new Color(1,255,229);
	Color darkBlue=new Color(0,36,255);
	Color purple=new Color(197,0,255);
	Color magenta=new Color(255,0,156);
	Color black=new Color(0,0,0);
	Color white2=new Color(231,237,249);
	Color pink=new Color(226,62,125);
	Color lightPink=new Color(255,148,171);
	Color yellow=new Color(245,255,0);
	Color lightBrown=new Color(188,155,48);
	Color gymCol1=new Color(226,62,125);
	Color gymCol2=new Color(255,148,171);
	Color gymCol3=new Color(125,40,82);
	Color gymCol4=new Color(254,147,31);

	Color[] gymCols=new Color[4];


	boolean battle=false;

	public GamePanel(){

		try {
	    	bufferedImages[0]=ImageIO.read(new File("images/maps/mainMapMask.png"));
	    	bufferedImages[1]=ImageIO.read(new File("images/maps/homeMask.png"));
	    	bufferedImages[2]=ImageIO.read(new File("images/maps/pokeCenterMask.png"));
	    	bufferedImages[3]=ImageIO.read(new File("images/maps/shopMask.png"));
	    	bufferedImages[4]=ImageIO.read(new File("images/maps/trainerHouseMask.png"));

	    	bufferedImages[5]=ImageIO.read(new File("images/maps/gymMask.png"));
	    	bufferedImages[6]=ImageIO.read(new File("images/maps/gym2Mask.png"));
	    	bufferedImages[7]=ImageIO.read(new File("images/maps/gym3Mask.png"));
	    	bufferedImages[8]=ImageIO.read(new File("images/maps/gym4Mask.png"));
		}
		catch (IOException e) {
		}
		for(int n=0;n<151;n++){//smaller versions of all the pokemon
			minisprites[n]=new ImageIcon("pokemon/mini sprites/"+(n+1)+".png").getImage();
		}

		gymCols[0]=gymCol1;
		gymCols[1]=gymCol2;
		gymCols[2]=gymCol3;
		gymCols[3]=gymCol4;

		String[][] allNPC=fileRead("text files/allNPC.txt",16,",");//contains all info for NPCs including trainers and gym bosses
		String[][] NPCtext=fileRead("text files/NPCtext.txt",16,"\n");//the text that the NPCs say before a battle
		String[][] itemsDescript=fileRead("text files/itemsDescription.txt",4,"\n");//each item has a descriptio of what they do
		int count=0;
		setSize(16*16,16*12);
		keys=new boolean[KeyEvent.KEY_LAST+1];

		for (int i=0;i<165;i++){//filling the hashtable with moves
			allmoves.put(almoves[i][0],almoves[i]);
		}
		Image[] walks=new Image[16];
		//sprites for walking
    	for (int i=0;i<16;i++){
    		walks[i]=new ImageIcon("sprites/player walk/Walk"+i+".png").getImage();
    	}
    	for (int j=0;j<151;j++){
    		frontsprite[j]=new ImageIcon("pokemon/main-sprites/firered-leafgreen/"+(j+1)+".png").getImage();
    	}
    	for (int k=0;k<151;k++){
    		backsprites[k]=new ImageIcon("pokemon/main-sprites/firered-leafgreen/back/"+(k+1)+".png").getImage();
    	}
		p=new Player(walks);


    	playerpokes=p.getPokes();

		for (String[] arr:allNPC){//creates all the NPC objects
			Pokemon[] enemyPokes=new Pokemon[6];//they have a max of 6 pokemon
			for (int i=0;i<6;i++){//getting all the enemy's pokemon

				if (arr[3+2*i].equals("-1")){//means they don't have a pokemon in that spot
					enemyPokes[i]=null;
				}
				else{
					enemyPokes[i]=makePoke(Integer.parseInt(arr[3+2*i]),Integer.parseInt(arr[4+2*i]));//making the pokemon with the given info
				}
			}

			person=new NPC(arr[0],Integer.parseInt(arr[1]),Integer.parseInt(arr[2]),enemyPokes,NPCtext[count][0],true,arr[15],arr[16],arr[17]);//creating the NPC object
			characters[count]=person;
			count++;
		}
		for (int i=0;i<characters.length;i++){//separating the trainers from the gym bosses
			if (i<12){
				trainers[i]=characters[i];
				////System.out.println(trainers[i].getImages()[1]);
			}
			else{
				gymPeople[i-12]=characters[i];
			}
		}

		//creating all the maps that the player could be on
		main=new GameMap(-128,-1296,new ImageIcon("images/maps/mainMap.png").getImage(),bufferedImages[0],getSmallArray(characters,0,6));
        home=new GameMap(-224,-208,new ImageIcon("images/maps/home.png").getImage(),bufferedImages[1],null);
        pokeCenter=new GameMap(-112,-224,new ImageIcon("images/maps/pokeCenter.png").getImage(),bufferedImages[2],null);
        shop=new GameMap(-176,-224,new ImageIcon("images/maps/shop.png").getImage(),bufferedImages[3],null);
        trainerHouse=new GameMap(-240,-304,new ImageIcon("images/maps/trainerHouse.png").getImage(),bufferedImages[4],null);

        gym=new GameMap(-256,-320,new ImageIcon("images/maps/gym.png").getImage(),bufferedImages[5],getSmallArray(characters,6,7));
        gym2=new GameMap(-256,-320,new ImageIcon("images/maps/gym.png").getImage(),bufferedImages[6],getSmallArray(characters,7,8));
        gym3=new GameMap(-256,-320,new ImageIcon("images/maps/gym.png").getImage(),bufferedImages[7],getSmallArray(characters,8,9));
        gym4=new GameMap(-256,-320,new ImageIcon("images/maps/gym.png").getImage(),bufferedImages[8],getSmallArray(characters,9,10));

        maps[0]=main;
        maps[1]=home;
        maps[2]=pokeCenter;
        maps[3]=shop;
        maps[4]=trainerHouse;
        maps[5]=gym;
        maps[6]=gym2;
        maps[7]=gym3;
        maps[8]=gym4;

		//creating the item objects
        pokeball=new Item("Pokeball",new ImageIcon("images/items/pokeball.png").getImage(),200,itemsDescript[0][0]);
        potion=new Item("Potion",new ImageIcon("images/items/potion.png").getImage(),300,itemsDescript[1][0]);
       	superPotion=new Item("Super Potion",new ImageIcon("images/items/superPotion.png").getImage(),700,itemsDescript[2][0]);
     	maxPotion=new Item("Max Potion",new ImageIcon("images/items/maxPotion.png").getImage(),2500,itemsDescript[3][0]);

     	allItems[0]=pokeball;
     	allItems[1]=potion;
     	allItems[2]=superPotion;
     	allItems[3]=maxPotion;
     	//loading a save file
        try{
			Scanner inFile=new Scanner(new BufferedReader(new FileReader("save.txt")));//loading your party pokemon
	    	for (int m=0;m<114;m++){
	    		String[] baseinfo=inFile.nextLine().split(",");
	    		String[] moveinfo=inFile.nextLine().split(",");
	    		String[] ivinfo=inFile.nextLine().split(",");
	    		if(baseinfo.length>1){
	    			int startindex=Integer.parseInt(baseinfo[0]);
	    			int startlvl=Integer.parseInt(baseinfo[1]);
	    			Moves[] startmonmoves=new Moves[4];

	    			int[] startivs=new int[6];
	    			for(int g=0;g<6;g++){
	    				startivs[g]=Integer.parseInt(ivinfo[g]);
	    			}
	    			for(int q=0;q<moveinfo.length;q++){
	    				startmonmoves[q]=new Moves(allmoves.get(moveinfo[q]));
	    			}
	    			if(m<6){
	    				//party pokemon
	    				p.getPokes()[m]=new Pokemon(allpokes[startindex],startlvl,startmonmoves,startivs,frontsprite[startindex],backsprites[startindex],minisprites[startindex]);// splits at commas to seperate between different stats and adds it as a String[] to "allpokemon"
	    			}
	    			else{
	    				//pc pokemon
	    				p.getPcPokes()[(m-6)/12][(m-6)/9]=new Pokemon(allpokes[startindex],startlvl,startmonmoves,startivs,frontsprite[startindex],backsprites[startindex],minisprites[startindex]);// splits at commas to seperate between different stats and adds it as a String[] to "allpokemon"
	    			}

	    		}


	    	}

	    	String[] mapinfo=inFile.nextLine().split(",");
	    	mapNum=Integer.parseInt(mapinfo[0]);//loading where you saved
	    	maps[mapNum].setX(Integer.parseInt(mapinfo[1]));
	    	maps[mapNum].setY(Integer.parseInt(mapinfo[2]));
	    	gymsCompleted=Integer.parseInt(inFile.nextLine());

		}//if there is no save file starts a new game
		catch(IOException e){
			p.addPoke(makePoke(0,5));
    		p.addPoke(makePoke(3,5));
    		p.addPoke(makePoke(6,5));

		}
        String[][] tilesCol2=fileRead("text files/maskCols.txt",8,",");//the tiles that warp the player to different spots on the map

		for (String[] arr:tilesCol2){//filling hash table with all the warp spots
			Color tileCol=new Color(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]),Integer.parseInt(arr[2]));
			Warp tile=new Warp(tileCol,Integer.parseInt(arr[3]),Integer.parseInt(arr[4]),Integer.parseInt(arr[5]));
			tileLoc.put(tileCol,tile);
		}

		String[][] grassCol=fileRead("text files/grassSpots.txt",5,",");//info for areas with random pokemon encounters

		for (String[] arr:grassCol){//filling hash table with info for areas with random pokemon encounters
			ArrayList<Integer> possiblePokes=new ArrayList<Integer>();//possible pokemon you can encounter in that area
			for (int i=5;i<arr.length;i++){
				possiblePokes.add(Integer.parseInt(arr[i]));
			}
			Color tileCol=new Color(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]),Integer.parseInt(arr[2]));
			EncounterGrass grass=new EncounterGrass(tileCol,Integer.parseInt(arr[3]),Integer.parseInt(arr[4]),possiblePokes);
			grassColour.put(tileCol,grass);
		}

		try {
   			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
 			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("PKMN RBYGSC.TTF")));
		}
		catch (IOException|FontFormatException e) {
 			//Handle exception
		}




	}





    public void setKey(int k, boolean v) {
    	keys[k] = v;
    }
    public void refresh() {
    	////System.out.println(maskCol);
    	////System.out.println(main.getY());
    	////////System.out.println(npcDraw);

		if(pokeevolve.size()==0){//if theres no evolution animation

			maskCol=getPixel(bufferedImages[mapNum],Math.abs(maps[mapNum].getX())+120,Math.abs(maps[mapNum].getY())+88);//colour of the tile of the spot you are on in the mask

			trainerFrame=Math.abs(trainerFrame+trainerMoveDir);//shows which direction the npc will face

			if (trainerFrame==899){//every 300 times the counter goes up the dirrection will be different
				trainerFrame=600;
				trainerMoveDir=-1;
			}
			else if (trainerFrame==0){
			  	trainerFrame=300;
			   	trainerMoveDir=1;
			}
			for (NPC npcTrainer:trainers){//goes through all the trainers to find out if any of them should be on the screen. It also checks to see if a battle should start
				if ((npcTrainer.getY()+maps[mapNum].getY())>-112 && (npcTrainer.getY()+maps[mapNum].getY())<96 && (npcTrainer.getX()+maps[mapNum].getX())>-144 && (npcTrainer.getX()+maps[mapNum].getX())<128 && mapNum==0){
					npcDraw=npcTrainer;

					if ((int)trainerFrame/300==0&&npcDraw.getBeaten()==false&&p.pokesAlive()){//facing down and if they are within 4 blocks of that direction that the battle will happen
						if (Math.abs(maps[mapNum].getX())==npcDraw.getX() && Math.abs(maps[mapNum].getY())-npcDraw.getY()<=64 && Math.abs(maps[mapNum].getY())-npcDraw.getY()>=0){
							textTalk=true;
							movedir=UP;
						}
					}
					else if ((int)trainerFrame/300==1&&npcDraw.getBeaten()==false&&p.pokesAlive()){//facing left and if they are within 4 blocks of that direction that the battle will happen
						if (Math.abs(maps[mapNum].getY())==npcDraw.getY() && Math.abs(maps[mapNum].getX())-npcDraw.getX()>=-64 && Math.abs(maps[mapNum].getX())-npcDraw.getX()<=0){
							textTalk=true;
							movedir=RIGHT;
						}
					}
					else if(npcDraw.getBeaten()==false&&p.pokesAlive()){//facing up and if they are within 4 blocks of that direction that the battle will happen
						if (Math.abs(maps[mapNum].getX())==npcDraw.getX() && Math.abs(maps[mapNum].getY())-npcDraw.getY()>=-64 && Math.abs(maps[mapNum].getY())-npcDraw.getY()<=0){
							textTalk=true;
							movedir=DOWN;
						}
					}
				}

			}
			if (mapNum>=5){//checking to see if the location of the gym boss npc is on the screen when the player is in the gym

				if ((gymPeople[mapNum-5].getY()+maps[mapNum].getY())>-112 && (gymPeople[mapNum-5].getY()+maps[mapNum].getY())<96 && (gymPeople[mapNum-5].getX()+maps[mapNum].getX())>-144 && (gymPeople[mapNum-5].getX()+maps[mapNum].getX())<128){
					npcDraw=gymPeople[mapNum-5];
					if (maskCol.equals(gymCols[mapNum-5])&&npcDraw.getBeaten()==false){//when they enter the arena to fight the boss

						textTalk=true;


					}
				}

			}
			else if (tileLoc.get(maskCol)!=null && !shopMenu){//when they step on a tile in tileLoc

				if (mapNum==0){//changes map to the map you're going to
					maps[mapNum].setY(maps[mapNum].getY()-16);
					mapNum=tileLoc.get(maskCol).getMapto();
				}
				else if (mapNum==3 && keys[KeyEvent.VK_C]){//when they go to the cashier at the shop and press "c" the shopmenu opens up
					shopMenu=true;
					keys[KeyEvent.VK_C]=false;
				}
				else if (mapNum==2 && keys[KeyEvent.VK_C]&&changePokemon==false){//in the pokecenter
					if (pokeHeal){//already healed and you press "c" to exit the speech than you can walk out
						pokeHeal=false;
					}
					else{
						if (maskCol.equals(Color.BLACK)){//healing pokemon
							pokeHeal=true;
							for (Pokemon poke:p.getPokes()){
								if (poke!=null){
									poke.heal();
								}

							}
						}
						else if (maskCol.equals(white2)&&changePokemon==false){//at the pc
							changePokemon=true;
						}

					}
					keys[KeyEvent.VK_C]=false;

				}

			}

			else if(grassColour.get(maskCol)!=null && moving && !battle){//random pokemon encounter
				if (rand.nextInt(100)<=15&&p.pokesAlive()&&(framecount==15&&moving||(framecount==7&&running&&moving))){//chance,after a full walking frame
					Pokemon[] wildmons=new Pokemon[6];

					wildmons[0]=makePoke(grassColour.get(maskCol).getRandPoke(),grassColour.get(maskCol).getRandLvl());
					startBattle("Wild",wildmons);
				}
			}
			if (changePokemon){//if you're using the pc
				pcSwap();
			}
			if (pokeHeal && keys[KeyEvent.VK_C]){//done healing
				pokeHeal=false;
			}
			if(savegame&&keys[KeyEvent.VK_C]){//get rid of the save the game message
				savegame=false;
			}
			if (textTalk&&battle==false){//when an npc is talking to you
				if (keys[KeyEvent.VK_C]){
					if (npcDraw.boss && (mapNum-5)!=gymsCompleted&&npcDraw.getBeaten()==false){//after the boss battle it starts the player outside
						////System.out.println("DW");
						maps[mapNum].setX(-256);
						maps[mapNum].setY(-336);
						mapNum=0;
						maps[mapNum].setY(maps[mapNum].getY()+4);
						movedir=DOWN;
						startBattle("Trainer",npcDraw.pokes);
						//System.out.println(maps[mapNum].getY());
					}
					else if(npcDraw.getBeaten()==false&&p.pokesAlive()){//trainer battle
					for (int w=0;w<6;w++){
						System.out.println(npcDraw.pokes[w]);
					}
					System.out.println(";;;;;;");

						trainerFrame=0;
						startBattle("Trainer",npcDraw.pokes);
					}
					textTalk=false;
					keys[KeyEvent.VK_C]=false;

				}
			}
			if (battle){//self explanatory
				battle();
			}




			else if(shopMenu){

				if (keys[KeyEvent.VK_X] && !buyNum){//exiting the shop menu
					shopMenu = false;
					battlesely=0;
					battleselx=0;
					keys[KeyEvent.VK_X]=false;
				}
				if (keys[KeyEvent.VK_C] && !buyNum){//buying an item
					buyNum=true;
					battlesely=0;
					battleselx=0;
					keys[KeyEvent.VK_C]=false;
				}
				if (buyNum){//this is where you can choose how many of an item to buy
					if (keys[KeyEvent.VK_X]){//not buying it
						buyNum=false;
						battlesely=0;
						battleselx=0;
						keys[KeyEvent.VK_X]=false;
						opt=0;
						purchaseNum=1;
					}
					if (keys[KeyEvent.VK_C] && p.getMoney()>=allItems[itemNum].price*purchaseNum){//purchasing the items
						p.setMoney(p.getMoney()-allItems[itemNum].price*purchaseNum);
						p.addItem(allItems[itemNum],purchaseNum);
						opt=0;
						buyNum=false;
						battlesely=0;
						battleselx=0;
						keys[KeyEvent.VK_C]=false;
					}
					int[][] optionselect={{-1,1}};//allows them to choose the amount to buy with arrow keys
					opt=options(optionselect);

					if (keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_LEFT]){//doesn't let you buy over 99 or below 1
						purchaseNum+=opt;
						if (purchaseNum<1)
							purchaseNum=1;
						else if (purchaseNum > 99)
							purchaseNum = 99;

						keys[KeyEvent.VK_RIGHT]=false;
						keys[KeyEvent.VK_LEFT]=false;
					}

				}
				else{//this allows the user to go through the items to pick one
					int[][] optionselect={{0},{1},{2},{3}};
					opt=options(optionselect);
					itemNum=opt;
				}

			}

			else if (maskCol.equals(yellow)){//leaving a map
				maps[mapNum].setY(maps[mapNum].getY()+8);
				mapNum=0;
				maps[mapNum].setY(maps[mapNum].getY()+8);
			}
			else{//moveing the actual player, cant be done while talking or in battle
				if(keys[KeyEvent.VK_SPACE]&&moving==false){//to run
					running=true;
				}
		    	if(keys[KeyEvent.VK_UP]&&moving==false && !textTalk&&!savegame&&!pokeHeal){
		    		if (!getPixel(bufferedImages[mapNum],Math.abs(maps[mapNum].getX())+120,Math.abs(maps[mapNum].getY())+72).equals(red)){
			    		movedir=UP;
			    		moving=true;
		    		}


		    	}
		    	else if(keys[KeyEvent.VK_LEFT]&&moving==false && !textTalk&&!savegame&&!pokeHeal){
		    		if (!getPixel(bufferedImages[mapNum],Math.abs(maps[mapNum].getX())+104,Math.abs(maps[mapNum].getY())+88).equals(red)){
			    		movedir=LEFT;
			    		moving=true;
		    		}

		    	}
		    	else if(keys[KeyEvent.VK_DOWN]&&moving==false && !textTalk&&!savegame&&!pokeHeal){
		    		if (!getPixel(bufferedImages[mapNum],Math.abs(maps[mapNum].getX())+120,Math.abs(maps[mapNum].getY())+104).equals(red)){
			    		movedir=DOWN;
			    		moving=true;
		    		}
		    	}
		    	else if(keys[KeyEvent.VK_RIGHT]&&moving==false && !textTalk&&!savegame&&!pokeHeal){
		    		if (!getPixel(bufferedImages[mapNum],Math.abs(maps[mapNum].getX())+136,Math.abs(maps[mapNum].getY())+88).equals(red)){
		    			movedir=RIGHT;
		    			moving=true;
		    		}

		    	}//saving your game
		    	else if(keys[KeyEvent.VK_S]&&!savegame&&!pokeHeal&&moving==false){
		    		savegame=true;
					if(mapNum==0){//you can only save on main map

						File fnew=new File("save.txt");
						try {
							/*
							 *saves in the format;
							 *party pokemon:
							 *index number,lvl
							 *moves
							 *ivs
							 *pc pokemon
							 *index number,lvl
							 *moves
							 *ivs
							 *gamemap:
							 *gamemap number,map x, map y
							 *gyms beaten
							 */
						    FileWriter f2 = new FileWriter(fnew, false);
						    for(int i=0;i<6;i++){
						    	String ans="";
						    	if(p.getPokes()[i]!=null){
						    		String strmoves="";
						    		//writing base pokemon info (lvl,index)
						    		ans+=p.getPokes()[i].index+","+p.getPokes()[i].getLvl()+System.getProperty( "line.separator" );
						    		f2.write(ans);
						    		//writing pokemons moves
						    		for(int t=0;t<4;t++){
						    			if(p.getPokes()[i].getMoves()[t]!=null){
						    				strmoves+=p.getPokes()[i].getMoves()[t].toString()+",";
						    			}
						    		}
						    		strmoves+=System.getProperty( "line.separator" );
						    		f2.write(strmoves);
						    		String fileivs="";
						    		//writing pokemons specific ivs
						    		for(int l=0;l<6;l++){
						    			fileivs+=p.getPokes()[i].getIvs()[l]+",";
						    		}
						    		fileivs+=System.getProperty( "line.separator" );
						    		f2.write(fileivs);
						    	}
						    	//if there was no pokemon in that party space, puts 3 empty lines in the save file
						    	else{
						    		f2.write(System.getProperty( "line.separator" ));
						    		f2.write(System.getProperty( "line.separator" ));
						    		f2.write(System.getProperty( "line.separator" ));
						    	}

						    }
						    for (int z=0;z<9;z++){//saving pc pokemon
						    	for(int c=0;c<12;c++){
						    		String ans="";
							    	System.out.println(z%8+","+z%12);
							    	if(p.getPcPokes()[z][c]!=null){
							    		String strmoves="";
							    		//writing base pokemon info (lvl,index)
							    		ans+=p.getPcPokes()[z][c].index+","+p.getPcPokes()[z][c].getLvl()+System.getProperty( "line.separator" );
							    		f2.write(ans);
							    		//writing pokemons moves
							    		for(int t=0;t<4;t++){
							    			if(p.getPcPokes()[z][c].getMoves()[t]!=null){
							    				strmoves+=p.getPcPokes()[z][c].getMoves()[t].toString()+",";
							    			}
							    		}
							    		strmoves+=System.getProperty( "line.separator" );
							    		f2.write(strmoves);
							    		String fileivs="";
							    		//writing pokemons specific ivs
							    		for(int l=0;l<6;l++){
							    			fileivs+=p.getPcPokes()[z][c].getIvs()[l]+",";
							    		}
							    		fileivs+=System.getProperty( "line.separator" );
							    		f2.write(fileivs);
							    	}
							    	//if there was no pokemon in that pc space, puts 3 empty lines in the save file
							    	else{
							    		f2.write(System.getProperty( "line.separator" ));
							    		f2.write(System.getProperty( "line.separator" ));
							    		f2.write(System.getProperty( "line.separator" ));
							    	}
						    	}


						    }
						    //writing mapnumber and locations and gyms beaten
						    f2.write(mapNum+","+maps[mapNum].getX()+","+maps[mapNum].getY());
						    f2.write(System.getProperty( "line.separator" ));
						    f2.write(gymsCompleted+"");
						    f2.close();
						}
						catch (IOException e) {
						   e.printStackTrace();
						}
					}
		    	}

			}
		}
		//////////System.out.println(opt);
    }
    /*startBattle: sets up and creates a new battle object for use in the battle
     */
    public void startBattle(String battype,Pokemon[] foespokemon){//battype is wild or trainer for knowing if you can catch

		bat=new Battle(p.getPokes(),foespokemon,typeeffect,battype);//new battle object
		for(Pokemon pppp:enemypokes){
			System.out.println(pppp);
		}
		bat.setStage(bat.ACTIONSELECT);
		battleselx=0;
		battlesely=0;
		battle=true;
    }
    /*giveExp: used after a for pokemon is defeated. distributes experiance points
     *for all pokemon that participated in the battle. also adds pokemon to a list if they are learning a new
     *move or just teaches them the move if there is space
     */
    private void giveExp(){
    	for(int j=0;j<6;j++) {
				if (bat.getMon(bat.PLAYER,j)!=null&&bat.battled[bat.getFoesMon()][j]&&bat.getMon(bat.PLAYER,j).getHP()>0) {
					bat.getMon(bat.PLAYER,j).addExp((int)(((double)bat.getMon(bat.FOE).getExpVal()*(double)bat.getMon(bat.FOE).getLvl()*1.5)/7.0));//adds the exp
					if(bat.getMon(bat.PLAYER,j).Levelup()){//if the pokemon can level up
						bat.addText(bat.getMon(bat.PLAYER,j).getName()+" GREW TO LEVEL "+bat.getMon(bat.PLAYER,j).getLvl());

						if(!lvlmoves[bat.getMon(bat.PLAYER,j).index][2+bat.getMon(bat.PLAYER,j).getLvl()].equals(" ")){//if they learn a move at that level
							boolean knowmove=false;
							for (int i=0;i<4;i++){//checks if they already know the move

								if(bat.getMon(bat.PLAYER,j).getMoves()[i]!=null&&bat.getMon(bat.PLAYER,j).getMoves()[i].toString().equals(lvlmoves[bat.getMon(bat.PLAYER,j).index][2+bat.getMon(bat.PLAYER,j).getLvl()])){
									knowmove=true;
									break;
								}
							}
							if(knowmove==false){//if they dont know the move
								for(int l=0;l<4;l++){//goes through pokemons moves and adds the new move if there is space (the pokemon doesnt know 4 moves yet)
									if(bat.getMon(bat.PLAYER,j).getMoves()[l]==null){//if there is an empty move space
										bat.getMon(bat.PLAYER,j).getMoves()[l]=new Moves(allmoves.get(lvlmoves[bat.getMon(bat.PLAYER,j).index][2+bat.getMon(bat.PLAYER,j).getLvl()]));
										bat.addText(bat.getMon(bat.PLAYER,j).getName()+" LEARNED "+bat.getMon(bat.PLAYER,j).getMoves()[l]);
										knowmove=true;
										break;
									}
								}

							}
							if(knowmove==false){//if theres no space adds the move to list so player can choose at the end if they want to keep it
								pokelearning.add(bat.getMon(bat.PLAYER,j));
								movelearning.add(new Moves(allmoves.get(lvlmoves[bat.getMon(bat.PLAYER,j).index][2+bat.getMon(bat.PLAYER,j).getLvl()])));
							}
							knowmove=false;


						}
					}


				}
				//.out.println(bat.battleOver());
			}
    }
    /*pcSwap:
     *used to manage the player choosing and switching out pokemon from the pc
     */
    private void pcSwap(){

    	if (chosepcpoke==false){//if you havn't chosen a pokemon to swap from the pc
    		Pokemon hoveringmon=options(p.getPcPokes());//gets a pokemon from the pc using selecyion method

    		if(keys[KeyEvent.VK_C]){
    			pok=hoveringmon;//if you press c sets that pokemon as the one your going to swap
    			if(pok!=null){//if you didn't choose an empty space
    				//makes a new copy of that pokemon
    				pok=new Pokemon(allpokes[hoveringmon.index],hoveringmon.getLvl(),hoveringmon.getMoves(),hoveringmon.getIvs(),frontsprite[hoveringmon.index],backsprites[hoveringmon.index],minisprites[hoveringmon.index]);
    			}
    			selectedpokemonx=battleselx;//keeps track of the mos position in the pc so it can switch it after the other pokemon is chosen
    			selectedpokemony=battlesely;
    			chosepcpoke=true;
    			keys[KeyEvent.VK_C]=false;
    			battleselx=0;
    			battlesely=0;
    		}
    		else if(keys[KeyEvent.VK_R]){//used to release a pokemon
    			p.getPcPokes()[battlesely][battleselx]=null;
    			keys[KeyEvent.VK_R]=false;
    		}
    		else if(keys[KeyEvent.VK_X]){//exit
    			changePokemon=false;
    			keys[KeyEvent.VK_X]=false;
    			battleselx=0;
    			battlesely=0;
    		}
    	}
    	else{
    		int[][]optionselect={{0,1},{0,2},{0,3},{0,4},{0,5}};
    		opt=options(optionselect);//gets a pokemon from selection method
    		partypok=p.getPokes()[opt];//sets the other switching mon

    		if(keys[KeyEvent.VK_C]){
    			chosepcpoke=false;
    			partypok=p.getPokes()[opt];
    			if(partypok!=null){//if its not an empty space
    				//makes a new copy of the selected pokemon
    				partypok=new Pokemon(allpokes[p.getPokes()[opt].index],p.getPokes()[opt].getLvl(),p.getPokes()[opt].getMoves(),p.getPokes()[opt].getIvs(),frontsprite[p.getPokes()[opt].index],backsprites[p.getPokes()[opt].index],minisprites[p.getPokes()[opt].index]);
    			}

    			//swaps the two selected pokemon
    			p.getPcPokes()[selectedpokemony][selectedpokemonx]=partypok;
    			p.getPokes()[opt]=pok;


    			selectedpokemony=0;
    			selectedpokemonx=0;
    			keys[KeyEvent.VK_C]=false;
    		}
    		else if(keys[KeyEvent.VK_X]){//go back
    			chosepcpoke=false;
    			selectedpokemonx=0;
    			selectedpokemony=0;
    			keys[KeyEvent.VK_X]=false;
    		}
    	}
    }
    /*battle:
     *method that manages the battle and the sequence of a battle, dealing damage at the right stage
     *applying status' and displaying text in between
     *usual sequence:
	     *Action select:(attack, switch pokemon, use item, run)
	     *if action was run,item or switch you go first
	     *otherwise use speed to determine attack order
	     *if you go first;
		     *player attacks
		     *display players text
		     *enemy attacks
		     *display enemy text

		 *if foe goes first
		 	*enemy attacks
		 	*display enemy text
		 	*player attacks
		 	*display players text
		 *display end turn text
     */
	public void battle(){
		if(bat.getStage()==bat.BATTLEOVER){
			if(bat.teamDead()){//if your team is beaten you lost
				System.out.println("LOST");
			}
			//goes through pokemon and checks if they can evolve

			int teamcount=0;
			for (Pokemon pok:p.getPokes()){
				if(pok!=null){
					if (allpokes[pok.index+1][27].split(" ")[0].equals("Lv.")){//if the next pokemon in the array has a evolution lvl requirement equal to or less than the pokemons level
						if (Integer.parseInt(allpokes[pok.index+1][27].split(" ")[1])<=pok.getLvl()){
							int faint=1;
							if(pok.getHP()==0){
								faint=0;
							}
							//makes an evolved form of the pokemon previously at the location
							p.getPokes()[teamcount]=new Pokemon(allpokes[pok.index+1],pok.getLvl(),pok.getMoves(),pok.getIvs(),frontsprite[pok.index+1],backsprites[pok.index+1],minisprites[pok.index+1]);
							pokeevolve.add(p.getPokes()[teamcount]);//adds the pokemon to a list for displaying the evolution animation after the battle
							p.getPokes()[teamcount].setHP(p.getPokes()[teamcount].getHP()*faint);//if the pokemon was fainted before, resets them to fainted after evolution
						}
					}
				}

				teamcount+=1;
			}
			if(bat.battype.equals("Trainer")){//heals all the opponents pokemon
				for(int k=0;k<6;k++){
					if(bat.getMon(bat.FOE,k)!=null){
						bat.getMon(bat.FOE,k).heal();
					}
				}
			}
			battle=false;// ends battle
			battleselx=0;
			battlesely=0;
		}
		if(bagopen&&bat.getStage()!=bat.PARTYSELECT){//getting an item in the bag
			int[][] optionselect={{0},{1},{2},{3}};//selecting item
			opt=options(optionselect);
			if(keys[KeyEvent.VK_C]){
				if(p.getBackpack()[opt]>0){//if you have any of the item your trying to use
					itemusing=opt;

					if(opt>=1){//if your using a potion
						bat.setStage(bat.PARTYSELECT);
					}
					else if(bat.battype.equals("Wild")){//if your using a pokeball
						p.getBackpack()[opt]-=1;//uses up the item
						catching=true;
						bat.addText("YOU USED A POKEBALL");
						//calculating if it is caught using actuall pokemon game formula
						int n=rand.nextInt(255);
						int statusthresh=0;
						if(bat.getMon(bat.FOE).getFreeze()||bat.getMon(bat.FOE).getSleep()){//pokemon is easier to catch with status ailment
							statusthresh=25;
							if(n<=25){
								caught=true;
							}

						}
						else if(bat.getMon(bat.FOE).getPara()||bat.getMon(bat.FOE).getBurn()||bat.getMon(bat.FOE).getPoison()){//pokemon is easier to catch with status ailment
							statusthresh=12;
							if(n<=12){
								caught=true;
							}

						}
						else{
							if(n-statusthresh<bat.getMon(bat.FOE).catchrate){
								caught=true;
							}
							else{
								int m=rand.nextInt(255);
								if((bat.getMon(bat.FOE).getHPMax()*255)/(bat.getMon(bat.FOE).getHP()*12)>=m){
									caught=true;
								}
							}

						}
						if(caught){bat.addText("YOU CAUGHT THE "+bat.getMon(bat.FOE).getName());p.addPoke(bat.getMon(bat.FOE));}//adds the pokemon to your team if you caught it

						else{
							bat.addText("THE POKEMON BROKE FREE");//continues battle otherwise
						}
						bagopen=false;
						bat.setStage(bat.TEXTDISPLAYPLAYER);//uses up your turn
						bat.turn=1;
					}
				}
				keys[KeyEvent.VK_C]=false;
				battleselx=0;
				battlesely=0;
			}

			if (keys[KeyEvent.VK_X]){//go back
				bat.setStage(bat.ACTIONSELECT);
				keys[KeyEvent.VK_X]=false;
				bagopen=false;
				battleselx=0;
				battlesely=0;
			}
		}

		else if (bat.getStage()==bat.ACTIONSELECT&&bat.turn==0){//selecting to fight, run use item, or switch pokemon
			if (bat.battled[bat.getFoesMon()][bat.getCurrentMon()]==false){
				bat.battled[bat.getFoesMon()][bat.getCurrentMon()]=true;
			}
			int[][] optionselect={{FIGHT,BAG},{PKMN,RUN}};//choosing action
			opt=options(optionselect);

			if (keys[KeyEvent.VK_C]){
				if (opt==FIGHT){

					if(bat.getMon(bat.PLAYER).getRecharge()){//cant attack if used a recharge move last turn
						bat.addText(bat.getMon(bat.PLAYER).getName()+" MUST RECHARGE");
						bat.getMon(bat.PLAYER).setRecharge(false);
						bat.turn=2;//skips your attack turn
						bat.setStage(bat.FOEATTACK);
					}
					else{//if your not recharging, lets you choose your move
						bat.setStage(bat.MOVESELECT);
					}

				}
				else if (opt==BAG){
					bagopen=true;//opens bag

				}
				else if (opt==RUN&&bat.battype.equals("Wild")){//only can run againts wild pokemon

					battle=false;
				}
				else if (opt==PKMN){//switching pokemon

					bat.setStage(bat.PARTYSELECT);
				}
				keys[KeyEvent.VK_C]=false;
				battleselx=0;
				battlesely=0;

			}
		}
		else if (bat.getStage()==bat.MOVESELECT&&bat.turn==0){
			int[][] optionselect={{0,1},{2,3}};
			opt=options(optionselect);

			if (keys[KeyEvent.VK_C]){
				//if the move is usable(not null,has pp and isnt disabled) or if non of your moves can be used,will use struggle
				if ((bat.getMon(bat.PLAYER).getMoves()[opt]!=null&&bat.getMon(bat.PLAYER).getMoves()[opt].getDisable()==0&&bat.getMon(bat.PLAYER).getMoves()[opt].getPP()>0)||bat.getMon(bat.PLAYER).noMoves()){
					bat.turn+=1;
					if (bat.getMon(bat.PLAYER).getSpd()*bat.getMon(bat.PLAYER).getSpdMod()<bat.getMon(bat.FOE).getSpd()*bat.getMon(bat.FOE).getSpdMod()){//deciding who will attack first
						bat.setStage(bat.FOEATTACK);//if foe is faster


					}
					else{
						bat.setStage(bat.PLAYERATTACK);//if player is faster
					}
				}

				keys[KeyEvent.VK_C]=false;
				battleselx=0;
				battlesely=0;
			}
			else if (keys[KeyEvent.VK_X]){//go back
				itemusing=-1;
				bat.setStage(bat.ACTIONSELECT);
				keys[KeyEvent.VK_X]=false;
				battleselx=0;
				battlesely=0;
			}

		}
		else if (bat.getStage()==bat.PARTYSELECT){

			int[][] optionselect={{0,1},{0,2},{0,3},{0,4},{0,5}};
			opt=options(optionselect);

			if (keys[KeyEvent.VK_C]){
				if(bagopen){//if your using a potion
				//only works on alive pokemon
					if(bat.getMon(bat.PLAYER,opt)!=null&&itemusing==1&&bat.getMon(bat.PLAYER,opt).getHP()>0){//regular potion
						bat.getMon(bat.PLAYER,opt).setHP(Math.min(bat.getMon(bat.PLAYER,opt).getHPMax(),bat.getMon(bat.PLAYER,opt).getHP()+20));//heals 20 hp
					}
					else if(bat.getMon(bat.PLAYER,opt)!=null&&itemusing==2&&bat.getMon(bat.PLAYER,opt).getHP()>0){//super potion
						bat.getMon(bat.PLAYER,opt).setHP(Math.min(bat.getMon(bat.PLAYER,opt).getHPMax(),bat.getMon(bat.PLAYER,opt).getHP()+50));//heals 50 hp
					}
					else if(bat.getMon(bat.PLAYER,opt)!=null&&itemusing==3&&bat.getMon(bat.PLAYER,opt).getHP()>0){//max potion
						bat.getMon(bat.PLAYER,opt).setHP(bat.getMon(bat.PLAYER,opt).getHPMax());//heals all hp
					}
					itemusing=-1;//uses up item
					bat.turn=2;//uses up your turn
					bat.addText(bat.getMon(bat.PLAYER,opt).getName()+" WAS HEALED");
					bagopen=false;
					bat.setStage(bat.FOEATTACK);//lets the opponent act now

				}
				else{//if your just switching out pokemon
					if (bat.switchPokes(bat.PLAYER,opt)){//checks if you can switch the pokemon you chose
						bat.turn+=1;//uses up your turn
						bat.setStage(bat.TEXTDISPLAYPLAYER);
						battleselx=0;
						battlesely=0;
						turnone=true;


					}
				battleselx=0;
				battlesely=0;
				}

				keys[KeyEvent.VK_C]=false;

			}
			else if (keys[KeyEvent.VK_X]&&bat.getMon(bat.PLAYER).getHP()>0){//go back
				battleselx=0;
				battlesely=0;
				bat.setStage(bat.ACTIONSELECT);
				keys[KeyEvent.VK_X]=false;
			}


		}
		else if(bat.getStage()==bat.PLAYERATTACK){//if your attacking
			if(bat.getMon(bat.PLAYER).noMoves()){//if none of your moves can be used
				if(bat.attack(new Moves(allmoves.get("Struggle")),bat.PLAYER)){//uses struggle
					bat.moveEffects(new Moves(allmoves.get("Struggle")),bat.PLAYER,0);
				}
			}
			else{//if you can use a move, uses the one you chose
				if(bat.attack(bat.getMon(bat.PLAYER).getMoves()[opt],bat.PLAYER)){//uses move
					bat.moveEffects(bat.getMon(bat.PLAYER).getMoves()[opt],bat.PLAYER,0);//applies the moves effect if the move hit
				}
			}


			if(bat.getStage()==bat.BATTLEOVER){//if you used roar or whirlwind will end battle as per effect(but only in wild)
				System.out.println("Muuu");
				battle=false;
			}
			bat.setStage(bat.TEXTDISPLAYPLAYER);




		}
		else if(bat.getStage()==bat.FOEATTACK){//foes attacking turn
			if(bat.getMon(bat.FOE).noMoves()){//if the foe cant act (no usable moves)
				if(bat.attack(new Moves(allmoves.get("Struggle")),bat.FOE)){//uses struggle
					bat.moveEffects(new Moves(allmoves.get("Struggle")),bat.FOE,0);
				}
			}
			else{//otherwise uses ai to choose move
				int using=enemyMove(turnone);//gets the move to use based on ai
				if(turnone){turnone=false;}//if first turn between two pokemon , sets the first turn to false

				if(bat.attack(bat.getMon(bat.FOE).getMoves()[using],bat.FOE)){//uses the move
					bat.moveEffects(bat.getMon(bat.FOE).getMoves()[using],bat.FOE,0);//applies the moves effect
				}
			}


			if(bat.getStage()==bat.BATTLEOVER){//if foe used roar or whirlwind will end battle as per effect(but only in wild)
				battle=false;
			}

			bat.setStage(bat.TEXTDISPLAYFOE);



		}
		else if(bat.getStage()==bat.TEXTDISPLAYPLAYER){//displays text after players turn
			if (keys[KeyEvent.VK_C]||textdispframes>=192){//if you press the "A" button or the auto scroll time is reached
				battexttodisp+=1;//sets the text to display to the next in the list
				if (battexttodisp==bat.getText().size()){//if there is no more text to display
					bat.getText().clear();//resets text
					battexttodisp=0;
					bat.turn+=1;//moves turn forward

					if (bat.turn==3){//if both player and foe have acted

						bat.statusPoisonBurn();//applies end of turn damage
						bat.turn=0;//resets the turns for next turn

						if(bat.getText().size()>0){//if there is end of turn text to display from statusPoisonBurn()

							bat.setStage(bat.ENDTURNTEXT);
						}
						else{//other wise goes to next turn of battle
							bat.setStage(bat.ACTIONSELECT);
						}
					}

					else{//if only you have acted
						bat.setStage(bat.FOEATTACK);//lets the foe act


					}
					if (bat.deathCheck(bat.FOE)){//if the foe is fainted
						if(bat.battleOver()&&bat.teamDead()==false){//if the foes team is beaten but yours isnt(you won)
							bat.addText("YOU DEFEATED THE FOE");
							int moneygained=bat.getMon(bat.FOE).getLvl()*10;//calculates reward money

							bat.setStage(bat.ENDTURNTEXT);giveExp();bat.turn=1;//sets phase to display level ups and victory text
							if(bat.battype.equals("Trainer")){//if you beat a trainer
								moneygained*=2;//gives more money
								if(npcDraw.boss){//if it was a gym leader
									moneygained*=1.2;//even more money
									gymsCompleted+=1;
								}
								npcDraw.setBeaten(true);//sets the trainer to beaten
							}
							p.setMoney(p.getMoney()+moneygained);//gives you the money
							bat.addText("YOU GOT "+moneygained+" DOLLARS" );

						}
						else{//if the battle isnt over but you still fainted the foe (enemy has more pokemon left)
							bat.setStage(bat.ENDTURNTEXT);giveExp();bat.turn=1;//continues the battle
							bat.switchPokes(bat.FOE,bat.getFoesMon()+1);//foe switches to an alive pokemon
							turnone=true;


						}
					}
					if (bat.deathCheck(bat.PLAYER)&&bat.teamDead()==false&&bat.battleOver()==false){bat.setStage(bat.PARTYSELECT);bat.turn=1;}//if your pokemon fainted and the battle isnt over lets your switch pokemon
					else if (bat.deathCheck(bat.PLAYER)&&bat.battleOver()==false){bat.addText("YOU LOST");bat.setStage(bat.ENDTURNTEXT);}//if your pokemon died and you have none left(Lost) ends battle
					if(caught){//otherwise if you used a pokeball and caught the pokemon
						bat.setStage(bat.BATTLEOVER);//ends the battle
						caught=false;
						catching=false;
					}

				}
				textdispframes=0;
				keys[KeyEvent.VK_C]=false;

			}
		}
		else if(bat.getStage()==bat.TEXTDISPLAYFOE){//displays foes attack text
			if (keys[KeyEvent.VK_C]||textdispframes>=192){//goes through text
				battexttodisp+=1;

				if (battexttodisp==bat.getText().size()){//text completed
					bat.getText().clear();
					battexttodisp=0;
					bat.turn+=1;

					if (bat.deathCheck(bat.PLAYER)){bat.setStage(bat.PARTYSELECT);bat.turn=1;}//if the player fainted, lets the player switch pokemon
					else if (bat.turn==3){//other wise if player lived and the turn is over

						bat.statusPoisonBurn();//applies end turn damage
						bat.turn=0;
						/////////////////////////////Same as TEXTDISPLAYPLAYER////////////////////////////
						if(bat.getText().size()>0){

							bat.setStage(bat.ENDTURNTEXT);
						}
						else{
							bat.setStage(bat.ACTIONSELECT);
						}

					}
					else{//if turn isnt over lets the player act
						bat.setStage(bat.PLAYERATTACK);

					}
					/////////////////////////////Same as TEXTDISPLAYPLAYER////////////////////////////
					if (bat.deathCheck(bat.FOE)){
						if(bat.battleOver()&&bat.teamDead()==false){
							bat.addText("YOU DEFEATED THE FOE");
							int moneygained=bat.getMon(bat.FOE).getLvl()*10;

							bat.setStage(bat.ENDTURNTEXT);giveExp();bat.turn=1;
							if(bat.battype.equals("Trainer")){
								moneygained*=8;
								if(npcDraw.boss){
									moneygained*=1.2;
									gymsCompleted+=1;
								}
								npcDraw.setBeaten(true);
							}
							p.setMoney(p.getMoney()+moneygained);
							bat.addText("YOU GOT "+moneygained+" DOLLARS" );
							System.out.println(bat.getText().size()+",,,,,,,,,,,,,,,,,,,,,,,,,,");
						}
						else{
							bat.switchPokes(bat.FOE,bat.getFoesMon()+1);
							bat.setStage(bat.ENDTURNTEXT);giveExp();bat.turn=1;
							turnone=true;

						}
					}

					if (bat.deathCheck(bat.PLAYER)&&bat.teamDead()==false&&bat.battleOver()==false){bat.setStage(bat.PARTYSELECT);bat.turn=1;}
					else if (bat.deathCheck(bat.PLAYER)&&bat.battleOver()==false){bat.addText("YOU LOST");bat.setStage(bat.ENDTURNTEXT);}
				}

				//foe cant catch pokemon

				textdispframes=0;
				keys[KeyEvent.VK_C]=false;

			}
		}
		else if(bat.getStage()==bat.ENDTURNTEXT){//text for end of the turn

			if (keys[KeyEvent.VK_C]||textdispframes>=192){//displaying text
				battexttodisp+=1;

				if (battexttodisp==bat.getText().size()){//all text displayed
					bat.getText().clear();
					battexttodisp=0;
					if(pokelearning.size()==0){//if no pokemon are trying to learn moves
						if(bat.battleOver()||bat.teamDead()){//if you or the foe lost goes to end battle

							bat.setStage(bat.BATTLEOVER);
						}
						else if(bat.battleOver()==false){//if battle isnt over
							bat.setStage(bat.ACTIONSELECT);//continues battle

						}

					}
					else{//otherwise continues battle
						bat.setStage(bat.ACTIONSELECT);
					}
					if(bat.turn==1&&pokelearning.size()>0){//if some pokemon are trying to learn moves and you havn't seen this text below yet(indicated by bat.turn==1) just

						bat.setStage(bat.ENDTURNTEXT);//sets back to endturn to display this text
						bat.addText(pokelearning.get(learningm).getName()+" IS TRYING TO LEARN "+movelearning.get(learningm));//diplays what pokemon is trying to learn what move from list made by giveExp()
						bat.addText("FORGET WHAT MOVE?");
						bat.turn=0;
						battleselx=0;
						battlesely=0;
					}
					else if(bat.turn==0&&pokelearning.size()>0){//you just saw the text
						bat.setStage(bat.LEARNINGMOVE);//lets you choose what move to forget
					}
					bat.turn=0;//resets turn



				}
				textdispframes=0;
				keys[KeyEvent.VK_C]=false;
			}
		}
		else if(bat.getStage()==bat.LEARNINGMOVE){//you have to select a move to forget
			int[][] optionselect={{4},{0,1},{2,3}};//lets you choose what move to forget
			opt=options(optionselect);

			if (keys[KeyEvent.VK_C]){
				if (opt!=4&&pokelearning.get(learningm).getMoves()[opt]!=null){//if you are choosing an actuall move that isnt the move your trying to learn
					bat.addText(pokelearning.get(learningm).getName()+" FORGOT "+pokelearning.get(learningm).getMoves()[opt]);
					bat.addText("AND LEARNED "+movelearning.get(learningm));
					Moves[] tempmove=pokelearning.get(learningm).getMoves();//copies the pokemons moves
					tempmove[opt]=movelearning.get(learningm);//adds the new move
					pokelearning.get(learningm).setMoves(tempmove);//sets the pokemons updated moves
					learningm+=1;//continues through the list of pokemon learning new moves
					if(learningm>=pokelearning.size()){//if you reached the end of the list of pokemon learning new moves
						pokelearning.clear();//resets the lists
						movelearning.clear();
						learningm=0;
					}
					bat.turn=1;
					bat.setStage(bat.ENDTURNTEXT);//goes back to end turn to show text just added

					battleselx=0;
					battlesely=0;
				}
				else if(opt==4){//if you select the move your trying to learn
				//doesn't change your moves and continues through the pokemon learning
					bat.addText(pokelearning.get(learningm).getName()+" DIDN'T LEARN "+movelearning.get(learningm));
					learningm+=1;
					bat.turn=1;
					bat.setStage(bat.ENDTURNTEXT);
					if(learningm>=pokelearning.size()){
						pokelearning.clear();
						movelearning.clear();
						learningm=0;
					}
					battleselx=0;
					battlesely=0;
				}

				keys[KeyEvent.VK_C]=false;

			}
		}







	}

	/*makePoke:
	 *takes in a pokemons index and lvl and makes a new pokemon and returns it
	 */

    private Pokemon makePoke(int pokenum,int lvl){

    	Moves[] newpokemoves=new Moves[4];//list of moves the pokemon will have
    	int latestmove=0;//the most recent move the pokemon learned. used as this pokemon will delete its oldest move everytime it needs to as it learns new moves
    	int ivs[]=new int[6];//ivs for the pokemon
    	for (int k=0;k<6;k++){
    		ivs[k]=rand.nextInt(32);//gets random ivs for the pokemon
    	}

    	String[] birthmoves=lvlmoves[pokenum][3].split("-");//gets the moves the pokemon starts out with from text file

    	for (int j=0;j<birthmoves.length;j++){
    		System.out.println(birthmoves[j]);
    		newpokemoves[j%4]=(new Moves(allmoves.get(birthmoves[j])));//adds the birth moves and gets rid of the oldest move if it needs to learn a new move

    		latestmove=j%4;
    	}
    	for (int i=1;i<lvl;i++){//learns moves that a pokemon at the specifed lvl would learn to that point
    		if(lvlmoves[pokenum].length>3+i){
    			if (!lvlmoves[pokenum][3+i].equals(" ")){//if the move at lvl i is a move
    				System.out.println(lvlmoves[pokenum][3+i]);
    				boolean knowmove=false;
					for (int n=0;n<4;n++){//checks if the pokemon knows the move

						if(newpokemoves[n]!=null&&newpokemoves[n].equals(lvlmoves[pokenum][3+i])){
							knowmove=true;
							break;
						}
					}
					if(knowmove==false){//if it doesnt

						newpokemoves[(latestmove+1)%4]=(new Moves(allmoves.get(lvlmoves[pokenum][3+i])));//learns the new move and replaces the oldest
    					latestmove=(latestmove+1)%4;//sets the latest move
					}
					knowmove=false;
    				System.out.println(lvlmoves[pokenum][3+i]);

    			}
    		}
    	}

    	Pokemon foe=new Pokemon(allpokes[pokenum],lvl,newpokemoves,ivs,frontsprite[pokenum],backsprites[pokenum],minisprites[pokenum]);//makes the pokemon with the moves lvl and ivs obtained
    	return foe;
    }
    //options(int[][])
	//method used to select an option from a multi option menu
    private int options(int[][] returnopts){
		if(keys[KeyEvent.VK_UP]&&battlesely>0){
		    battlesely-=1;
		    keys[KeyEvent.VK_UP]=false;
	   	}
	    else if(keys[KeyEvent.VK_LEFT]&&battleselx>0){
			battleselx-=1;
			keys[KeyEvent.VK_LEFT]=false;
		}
		else if(keys[KeyEvent.VK_DOWN]&&battlesely<returnopts.length-1){
			battlesely+=1;
			keys[KeyEvent.VK_DOWN]=false;
		}
		else if(keys[KeyEvent.VK_RIGHT]&&battleselx<returnopts[battlesely].length-1){
			battleselx+=1;
			keys[KeyEvent.VK_RIGHT]=false;
		}

		return returnopts[battlesely][battleselx];
    }
    //options(Pokemon[][])
    //same as other options but returns a pokemon, used for pc
    private Pokemon options(Pokemon[][] returnopts){
		if(keys[KeyEvent.VK_UP]&&battlesely>0){
		    battlesely-=1;
		    keys[KeyEvent.VK_UP]=false;
	   	}
	    else if(keys[KeyEvent.VK_LEFT]&&battleselx>0){
			battleselx-=1;
			keys[KeyEvent.VK_LEFT]=false;
		}
		else if(keys[KeyEvent.VK_DOWN]&&battlesely<returnopts.length-1){
			battlesely+=1;
			keys[KeyEvent.VK_DOWN]=false;
		}
		else if(keys[KeyEvent.VK_RIGHT]&&battleselx<returnopts[battlesely].length-1){
			battleselx+=1;
			keys[KeyEvent.VK_RIGHT]=false;
		}

		return returnopts[battlesely][battleselx];
    }
    /*enemyMove:
     *uses algorithm to get the move the foe will use during a battle
     */
    private int enemyMove(boolean firstturn){
    	double chance=rand.nextDouble();//chance of using ai
    	ArrayList<Integer> bestchoices=new ArrayList<Integer>();//array of the best possible moves to use

    	for(int i=0;i<4;i++){
    			if(bat.getMon(bat.FOE).getMoves()[i]!=null){
    				System.out.println(bat.getMon(bat.FOE).getMoves()[i]);

    			}

    		}

    	if (chance<0.25){//wont use ai
    		System.out.println("random");
    		for(int i=0;i<4;i++){//adds all the moves to best choices as to get a random move from the move pool at the end
    			if(bat.getMon(bat.FOE).getMoves()[i]!=null&&bat.getMon(bat.FOE).getMoves()[i].getPP()>0){
    				bestchoices.add(i);

    			}

    		}
    	}
    	else{//use ai

    		if(firstturn){//if this is the first turn the 2 pokemon in the battle are fighting
    			//will try to use a status move
    			for(int j=0;j<4;j++){//adds all the status moves the pokemon knows to best choices
    				if(bat.getMon(bat.FOE).getMoves()[j]!=null&&bat.getMon(bat.FOE).getMoves()[j].getPP()>0){
    					System.out.println(bat.getMon(bat.FOE).getMoves()[j].getMoveType());
    					if(bat.getMon(bat.FOE).getMoves()[j].getMoveType().equals("Status")){
    						System.out.println(bat.getMon(bat.FOE).getMoves()[j]);
    						bestchoices.add(j);
    					}
    				}

    			}
    		}
   			if(bestchoices.size()==0||firstturn==false){//if not first turn
   				double bestmulti=0.0;//keeps track of which move has the best effectiveness
    			for(int k=0;k<4;k++){//goes through moves
    				if(bat.getMon(bat.FOE).getMoves()[k]!=null&&bat.getMon(bat.FOE).getMoves()[k].getPP()>0){// if the move is usable
    				//if the move has better effectivness than the best so far and it is a damage dealing move
    					if(bat.typeEffect(bat.getMon(bat.FOE).getMoves()[k],bat.getMon(bat.PLAYER),false)>bestmulti&&!bat.getMon(bat.FOE).getMoves()[k].getMoveType().equals("Status")){
    						bestchoices.clear();//clears the list and adds only this to bestchoices
    						bestchoices.add(k);

    						bestmulti=bat.typeEffect(bat.getMon(bat.FOE).getMoves()[k],bat.getMon(bat.PLAYER),false);//sets the best multiplier to this move

    					}
    					//if the move is tied for best effectivness
    					else if(bat.typeEffect(bat.getMon(bat.FOE).getMoves()[k],bat.getMon(bat.PLAYER),false)==bestmulti&&!bat.getMon(bat.FOE).getMoves()[k].getMoveType().equals("Status")){
    						System.out.println(bestmulti+" mmmmmmmmmmmmmmm");
    						bestchoices.add(k);//adds it with the others
    					}
    				}
    			}
   			}
   			if(bestchoices.size()==0){//if none of the moves were applicabe
   				for(int i=0;i<4;i++){//adds all moves and gets a random one
    				if(bat.getMon(bat.FOE).getMoves()[i]!=null&&bat.getMon(bat.FOE).getMoves()[i].getPP()>0){
    					bestchoices.add(i);

    				}

    			}

   			}
    	}
    	int moveuse=0;

    	moveuse=rand.nextInt(bestchoices.size());//gets a random move index from the best ones




    	System.out.println(bat.getMon(bat.FOE).getMoves()[moveuse]);

    	return bestchoices.get(moveuse);//returns the moves index

    }
    public void paintComponent(Graphics g){
    	Graphics2D g2 = (Graphics2D) g;
        g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,6));
    	if (battle){

    		g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,6));
    		g.drawImage(battleback,1,0,this);
    		g.drawImage(bat.getMon(bat.PLAYER).backsprite,30,58,this);
    		if(caught){
    			g.drawImage(pokeball.pic,170,55,this);
    		}
    		else{
    			g.drawImage(bat.getMon(bat.FOE).frontsprite,144,16,this);
    		}


    		g.drawImage(playerhpbar,135,70,this);
    		g.drawImage(foehpbar,10,10,this);
    		g.setColor(new Color(80,104,88));
			g2.setStroke(new BasicStroke(5));
            g.drawLine(185, 88, 229, 88);//hp blank
            g.drawLine(50, 28, 94, 28);
            g.setColor(new Color(112,248,168));
            g.drawLine(185, 88, 185+(int)(44*((double)bat.getMon(bat.PLAYER).getHP()/(double)bat.getMon(bat.PLAYER).getHPMax())), 88);//hp draw
            g.drawLine(50, 28, 50+(int)(44*((double)bat.getMon(bat.FOE).getHP()/(double)bat.getMon(bat.FOE).getHPMax())), 28);
            g.setColor(new Color(80,104,88));
			g2.setStroke(new BasicStroke(5));
            if(bat.getMon(bat.PLAYER).getHP()==0){g.drawLine(185, 88, 229, 88);}//thickness of line still shows if hp is zero this fixes it
    		if(bat.getMon(bat.FOE).getHP()==0){g.drawLine(50, 28, 94, 28);}
    		//status indicators
            if(bat.getMon(bat.PLAYER).getPara()){g.drawImage(paraicon,148,84,this);}
            else if(bat.getMon(bat.PLAYER).getPoison()){g.drawImage(poisonicon,148,84,this);}
            else if(bat.getMon(bat.PLAYER).getSleep()){g.drawImage(sleepicon,148,84,this);}
            else if(bat.getMon(bat.PLAYER).getFreeze()){g.drawImage(freezeicon,148,84,this);}
            else if(bat.getMon(bat.PLAYER).getBurn()){g.drawImage(burnicon,148,84,this);}
            if(bat.getMon(bat.FOE).getPara()){g.drawImage(paraicon,15,24,this);}
            else if(bat.getMon(bat.FOE).getPoison()){g.drawImage(poisonicon,15,24,this);}
            else if(bat.getMon(bat.FOE).getSleep()){g.drawImage(sleepicon,15,24,this);}
            else if(bat.getMon(bat.FOE).getFreeze()){g.drawImage(freezeicon,15,24,this);}
            else if(bat.getMon(bat.FOE).getBurn()){g.drawImage(burnicon,15,24,this);}
            g.setColor(Color.BLACK);
            //drawing pokemon info name lvl
            g.drawString(bat.getMon(bat.PLAYER).getName(),160,82);
            g.drawString(bat.getMon(bat.PLAYER).getLvl()+"",225,82);
            g.drawString(bat.getMon(bat.FOE).getName(),35,22);
            g.drawString(bat.getMon(bat.FOE).getLvl()+"",90,22);
            //drawing exp
            g.setColor(new Color(64,200,248));
			g2.setStroke(new BasicStroke(3));

			g.drawLine(168, 104, 168+(int)(63*((double)bat.getMon(bat.PLAYER).getExpEarned()/(double)bat.getMon(bat.PLAYER).toNextLvl())), 104);
			//.out.println(((double)bat.getMon(bat.PLAYER).getExpEarned()/(double)bat.getMon(bat.PLAYER).toNextLvl()));
            g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,5));//rest for text display
            g.setColor(Color.BLACK);
            if (bagopen&&bat.getStage()!=bat.PARTYSELECT){//drawing bag
            	g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,6));
            	g.drawImage(bagmenu,0,0,this);
            	g.drawImage(bag,20,40,this);
            	//drawing items
            	//drawing amount
            	g.drawString(pokeball.getName(),115,18);
            	g.drawString("X "+p.getBackpack()[0],180,18);
            	g.drawString(potion.getName(),115,35);
            	g.drawString("X "+p.getBackpack()[1],180,35);
            	g.drawString(superPotion.getName(),115,52);
            	g.drawString("X "+p.getBackpack()[2],180,52);
            	g.drawString(maxPotion.getName(),115,69);
            	g.drawString("X "+p.getBackpack()[3],180,69);
            	g.drawImage(selector,100,10+opt*17,this);//drawing selector
            	g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,5));
            }
    		else if (bat.getStage()==bat.ACTIONSELECT){//main fighting menu
    			g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,6));
    			g.drawImage(menu,0,110,this);
    			g.drawImage(fightmenu,122,110,this);
    			g.drawImage(selector,130+(opt%2)*55,122+(opt/2)*15,this);
    		}
    		else if(bat.getStage()==bat.MOVESELECT||bat.getStage()==bat.LEARNINGMOVE){//drawing moves
    			g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,5));
    			Pokemon focusmon=bat.getMon(bat.PLAYER);
    			if(bat.getStage()==bat.LEARNINGMOVE){//if your learning a move draws the extra window and attack
    				g.drawImage(learningmenu,0,80,this);
    				System.out.println("namashi");
    				g.drawString(movelearning.get(learningm).toString().toUpperCase(),20,100);
    				focusmon=pokelearning.get(learningm);
    			}
    			g.drawImage(attackmenu,0,110,this);



				for (int i=0;i<4;i++){//drawing regular moves
					if (focusmon.getMoves()[i]!=null){
						g.drawString(focusmon.getMoves()[i].toString().toUpperCase(),20+(i%2)*70,130+(i/2)*15);
					}
				}
				if(opt!=4&&focusmon.getMoves()[opt]!=null){//drawing regualr moves info (pp, type)
					g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,6));
					g.setColor(Color.BLACK);
					g.drawString(focusmon.getMoves()[opt].getPP()+"",200,130);
					g.drawString(focusmon.getMoves()[opt].getPPMax()+"",220,130);
					g.drawString(focusmon.getMoves()[opt].getType(),192,146);
				}
				if(opt!=4){//drawing selector if not on the move trubing to learn
					g.drawImage(selector,10+(opt%2)*70,122+(opt/2)*15,this);
				}
				else{//draws the move your trying to learn
					g.drawString(movelearning.get(learningm).getPP()+"",200,130);
					g.drawString(movelearning.get(learningm).getPPMax()+"",220,130);
					g.drawString(movelearning.get(learningm).getType(),192,146);
					g.drawImage(selector,10,92,this);
				}


    		}
    		else if(bat.getStage()==bat.TEXTDISPLAYPLAYER||bat.getStage()==bat.TEXTDISPLAYFOE||bat.getStage()==bat.ENDTURNTEXT){//displays battle text
    			g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,6));
    			g.drawImage(menu,0,110,this);
    			g.setColor(Color.WHITE);
    			g.drawString(bat.getText().get(battexttodisp),20,130);
    			textdispframes+=1;//auto scroll

    		}
    		else if(bat.getStage()==bat.PARTYSELECT){//displaying party

    			g.drawImage(partyback,0,0,this);
    			g.drawImage(firstparty,2,18,this);

    			for(int i=0;i<5;i++){
    				if(bat.getMon(bat.PLAYER,i+1)!=null){

    					g.drawImage(regparty,90,10+i*24,this);

    				}

    			}
    			if(opt==0){
    				g.drawImage(highfirstparty,2,18,this);//if the fisrt poke in party is being selected

    			}
    			else if(bat.getMon(bat.PLAYER,opt)!=null){//anyone else in party

    				g.drawImage(highregparty,90,10+(opt-1)*24,this);


    			}
    			g.setColor(new Color(112,248,168));
    			g.drawLine(35, 60, 35+(int)(44*((double)bat.getMon(bat.PLAYER,0).getHP()/(double)bat.getMon(bat.PLAYER,0).getHPMax())), 60);//drawing hp
    			g.setColor(Color.WHITE);
    			g.drawString(bat.getMon(bat.PLAYER,0).getName(),35,48);
    			for(int i=0;i<5;i++){
    				if(bat.getMon(bat.PLAYER,i+1)!=null){
    					g.setColor(Color.WHITE);
    					g.drawString(bat.getMon(bat.PLAYER,i+1).getName(),130,20+24*i);
    					g.setColor(new Color(112,248,168));
            			g.drawLine(187, 20+24*i, 187+(int)(44*((double)bat.getMon(bat.PLAYER,i+1).getHP()/(double)bat.getMon(bat.PLAYER,i+1).getHPMax())), 20+24*i);
    				}

    			}

    		}



    	}
    	else{//pokemon evolution animation
    		if(pokeevolve.size()>0){//if there are pokemon to evolve
    			g.setColor(Color.BLACK);
    			g.fillRect(0,0,300,300);
    			g.drawImage(menu,0,110,this);

    			actionframes+=1;
    			if(actionframes<=300){
    				g.drawImage(frontsprite[pokeevolve.get(evolvepoke).index-1],85,40,this);//draws current pokemon//draws evolved pokemon
    				g.setColor(Color.WHITE);
    				g.drawString(allpokes[pokeevolve.get(evolvepoke).index-1][2]+" IS EVOLVING",20,130);
    			}
    			else if(actionframes>300&&actionframes<800){// drawas growing white circle
    				g.drawImage(frontsprite[pokeevolve.get(evolvepoke).index-1],85,40,this);
    				g.setColor(Color.WHITE);
    				g.fillOval(120-(actionframes-500)/2,90-(actionframes-500)/2,actionframes-500,actionframes-500);
    			}
    			else if(actionframes<1300){
    				g.drawImage(pokeevolve.get(evolvepoke).frontsprite,85,40,this);
    				g.setColor(Color.WHITE);
    				g.drawString(allpokes[pokeevolve.get(evolvepoke).index-1][2]+" EVOLVED INTO "+pokeevolve.get(evolvepoke).getName(),20,130);
    			}
    			else{
    				if(evolvepoke==pokeevolve.size()-1){//if all pokemon evolving have bben shown
    					pokeevolve.clear();
    					evolvepoke=0;
    					actionframes=0;
    				}
    				else{//otherwoise move on to next evolving pokemon
    					evolvepoke+=1;
    					actionframes=0;
    				}
    			}


    		}
    		else if(changePokemon){//using pc
    			g.drawImage(pcback,0,0,this);


    			for(int i=0;i<9;i++){
    				for(int j=0;j<12;j++){
    					if(p.getPcPokes()[i][j]!=null){

    						g.drawImage(p.getPcPokes()[i][j].minisprite,j*20,i*20,this);//drawing pc pokemon
    					}
    				}
    			}
    			g.setColor(Color.BLUE);
    			g.drawRect(battleselx*20,battlesely*20,20,20);//draw selector
    			if(chosepcpoke){//if youve chosen a pc pokemon to swap
    				g.drawImage(pcparty,-5,0,this);//draws party pokemon
    				for(int k=0;k<6;k++){

    					if(p.getPokes()[k]!=null&&p.getPokes()[k].minisprite!=null){
    						if(k==0){

    							g.drawImage(p.getPokes()[k].minisprite,6,55,this);
    						}
    						else{
    							g.drawImage(p.getPokes()[k].minisprite,54,10+(k-1)*24,this);
    						}

    					}
    				}
    				g.setColor(Color.RED);//selector for party pokemon
    				if(opt==0){
    					g.drawRect(6,55,20,20);
    				}
    				else{
    					g.drawRect(54,10+(opt-1)*24,20,20);
    				}

    			}


    		}
    		else{

		    	if(moving){
					framecount+=1;
					if (framecount%4==0||(running&&framecount%2==0)){
					    maps[mapNum].move(movedir,4);//moving map
						g.drawImage(maps[mapNum].getMain(),maps[mapNum].getX(),maps[mapNum].getY(),this);//drawing map


						if(running){
							g.drawImage(p.move(framecount/2,movedir),7*16,5*16-2,this);//draw player
						}
						else{
							g.drawImage(p.move(framecount/4,movedir),7*16,5*16-2,this);
						}



					    if (framecount==16||(framecount==8&&running)){//if done moving
					    	framecount=0;
					    	moving=false;
					    	if(running){
					    		running=false;
					    	}
					    }
					}
		    	}
		    	if(moving==false){//pics for if the player isn't moving

		        	g.drawImage(maps[mapNum].getMain(),maps[mapNum].getX(),maps[mapNum].getY(),this);
		        	g.drawImage(p.stand(movedir),7*16,5*16-2,this);

		    	}
		    	if(mapNum==0){//drawing the houses and trees after so it doesn't look like they're walking on trees
    				g.drawImage(overMap,maps[0].getX(),maps[0].getY(),this);
    			}
		    	if (pokeHeal){//text for when the pokemon get healed
		    		g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,6));
		    		g.drawImage(speechRec,5,105,this);
			    	g.drawString("Your pokemon are now healed",20,130);
		    	}
		    	if(savegame){//text for saving the game
		    		g.setFont(new Font("PKMN RBYGSC",Font.PLAIN,6));
		    		g.drawImage(speechRec,5,105,this);
		    		if(mapNum!=0){
		    			g.drawString("CAN'T SAVE HERE",20,130);

		    		}
		    		else{
		    			g.drawString("YOUR GAME WAS SAVED",20,130);
		    		}

		    	}
		    	if (npcDraw!=null){//drawing the npc
		    		if (npcDraw.boss==false && mapNum==0){

		    			g.drawImage(npcDraw.getImages()[(int)trainerFrame/300],maps[mapNum].getX()+npcDraw.x+112,maps[mapNum].getY()+npcDraw.y+76,this);//the npc switches direction every few seconds

			    		if (textTalk && !battle){//what the npc says before the fight

			    			g.drawImage(speechRec,5,105,this);

			    			g.drawString(npcDraw.getText(),20,130);

			    		}

		    		}
		    		else if (npcDraw.boss){//gym fight
		    			g.drawImage(npcDraw.getImages()[0],maps[mapNum].getX()+npcDraw.x+112,maps[mapNum].getY()+npcDraw.y+76,this);
		    			if (textTalk && !battle){

		    				g.drawImage(speechRec,5,105,this);

			    			if ((mapNum-5)==gymsCompleted){//they are on the right gym
			    				g.drawString(npcDraw.getFirstText(),20,130);
			    			}
			    			else if ((mapNum-5)<gymsCompleted){//they've already defeated the gym
			    				g.drawString("Go on and defeat the rest of the gyms",20,130);
			    			}
			    			else{//they forgot a gym
			    				g.drawString("Complete the previous gyms first.",20,130);
			    			}
		    			}

		    		}

		    		npcDraw=null;
		    	}
		    	if (shopMenu){

		    		g.drawImage(shopBack,0,0,this);
		    		g.setColor(Color.white);
	    			g.drawImage(moneyBox,5,5,this);//the money they have in the top left corner
	    			g.drawString("$"+p.getStringMoney(),15,24);

	    			for (int i=0;i<allItems.length;i++){//draws all items  with pics and names and price
	    				g.drawImage(itemRec,75,5+35*i,this);
	    				if (i==0){
	    					g.drawImage(allItems[i].pic,85,15,this);
	    				}
	    				else{
	    					g.drawImage(allItems[i].pic,85,10+35*i,this);
	    				}

	    				g.drawString(allItems[i].name,115,24+35*i);
	    				g.drawString("$"+allItems[i].price,195,24+35*i);

	    			}
	    			if (buyNum){//a box displaying how many items you are buying

	    				g.setColor(Color.black);
	    				g.drawImage(whiteBox,90,50,this);
	    				g.drawString("x "+Integer.toString(purchaseNum),110,75);
	    			}
	    			else{//shows which item you are on
	    				g.drawImage(selector,65,14+35*opt,this);
	    			}

		    		g.setColor(Color.white);
		    		for (int i=0;i<allItems[itemNum].descript.length;i++){//draws the description of the item you are on

		    			g.drawString(allItems[itemNum].descript[i],5,70+12*i);
		    		}


		    	}

    		}

    	}
    }
    public static String[][] fileRead(String fname,int len1,String splitby){
    	String[][] finalarray=new String[len1][];
    	try{
    		Scanner inFile=new Scanner(new BufferedReader(new FileReader(fname)));//reads in the pokemon.txt file
	    	for (int i=0;i<len1;i++){
	    		finalarray[i]=(inFile.nextLine().split(splitby));// splits at commas to seperate between different stats and adds it as a String[] to "allpokemon"

	    	}

    	}
    	catch(IOException ex){
    		//////////System.out.println("Make sure "+fname+" is in the same folder as this file");
    	}
    	return finalarray;
    }
    public NPC[] getSmallArray(NPC[] arr,int start,int end){
		NPC[] goodArray=new NPC[end-start];
		for (int i=0;i<end-start;i++){
			goodArray[i]=arr[i+start];
		}
		return goodArray;
	}

    public Color getPixel(BufferedImage back,int x,int y){//returns the colour of the tile you are on from the mask
    	int c=back.getRGB(x,y);
    	Color col=new Color((c >> 16) & 0xFF,(c >> 8) & 0xFF,c & 0xFF);

    	return col;
	}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e){}

    public void mousePressed(MouseEvent e){
    	//////////System.out.println(e.getX()+","+e.getY());
		/*destx = e.getX();
		desty = e.getY();
		destx -= destx %5;
		desty -= desty %5;*/
	}


}
