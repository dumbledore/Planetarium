import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Canvas;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import java.awt.Cursor;
import java.awt.Point;
import java.util.Iterator;
import java.awt.Robot;
import java.awt.Rectangle;
import java.io.File;

import javax.imageio.ImageIO;
//import sun.java2d.opengl.*;

public class spaceField extends Canvas /*implements ImageObserver, MouseListener*/ {
	//public int[][] skyMatrix = null;
	//public ArrayList stars, planets, satellites;
	public HashMap sprites, stars, planets, satellites;
	public BufferStrategy strategy;
	public static final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	public boolean paused = false;
	public byte cursorIsDoing = 0;
	public Cursor appCursor;
	JFrame mainForm;
	
	public int MouseX = 0;
	public int MouseY = 0;
	public int Mode = 0;
	public boolean showNames = false;
	//public boolean updating = false;
	private boolean updateQuery = false;
	private boolean updateQueryRecieved = false;
	private boolean updateFinished = false;
	public boolean debugModeOn = false;
	public VolatileImage BG;
	public HashMap gfxST;
	public boolean safeMode;
	//public BufferedImage screenShot = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);;
	
	//public Graphics2D gfx;
	public spaceField(boolean sMode) {
		safeMode = sMode;
		sprites  = new HashMap();
		stars  = new HashMap();
		planets  = new HashMap();
		satellites  = new HashMap();
		gfxST = new HashMap();
		initializeGFXSettings();
		
		mainForm = new JFrame("Brisco's Planetarium");
		JPanel panel = (JPanel)mainForm.getContentPane();
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("arrow.gif"), new Point(0,0), "mainCursor"));
		addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					if (Mode == 0)
					{
						pauseIt(true);
						if (JOptionPane.showConfirmDialog(getParent(), "Do you want to quit?", "Exit Planetarium", JOptionPane.OK_CANCEL_OPTION) == 0)
						{
							System.exit(0);
						} else {
							pauseIt(false);
						}
					} else {
						pauseIt(false);
						Mode = 0;
						setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("arrow.gif"), new Point(0,0), "mainCursor"));
					}
				}
				if (event.getKeyCode() == KeyEvent.VK_PAUSE)
				{
					pauseIt(!paused);
				}
				if (event.getKeyCode() == KeyEvent.VK_F1)
				{
					showHelp();
				}
				if (event.getKeyCode() == KeyEvent.VK_F2)
				{
					pauseIt(true);
					Mode = 1;
					setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("sunCursor.gif"), new Point(0,0), "sunCursor"));
				}
				if (event.getKeyCode() == KeyEvent.VK_F3)
				{
					pauseIt(true);
					if(stars.size()==0)
					{
						JOptionPane.showMessageDialog(getParent(), "You cannot add planets.\nPlease, add a star first.", "Error!", JOptionPane.ERROR_MESSAGE);
						pauseIt(false);
						Mode = 0;
						setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("arrow.gif"), new Point(0,0), "mainCursor"));
					} else {
						pauseIt(true);
						Mode = 2;
						setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("planetCursor.gif"), new Point(0,0), "sunCursor"));
					}
				}
				if (event.getKeyCode() == KeyEvent.VK_F4)
				{
					pauseIt(true);
					if(stars.size()==0)
					{
						JOptionPane.showMessageDialog(getParent(), "You cannot add satellites.\nPlease, add at a planet first.", "Error!", JOptionPane.ERROR_MESSAGE);
						pauseIt(false);
						Mode = 0;
						setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("arrow.gif"), new Point(0,0), "mainCursor"));
					} else {
						pauseIt(true);
						Mode = 3;
						setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("satelliteCursor.gif"), new Point(0,0), "sunCursor"));
					}
				}
				if (event.getKeyCode() == KeyEvent.VK_D)
				{
					debugModeOn = !debugModeOn;
				}
			}
			public void keyReleased(KeyEvent event) {}
			public void keyTyped(KeyEvent event) {}
		}
		);
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e) {
				MouseX = e.getX();
				MouseY = e.getY();
				
				if (e.getButton() == 1)
				{
					if (Mode == 1)
					{
						addSun();
					}
					if (Mode == 2)
					{
						addPlanetOrSatellite(true);
					}	
					if (Mode == 3)
					{
						addPlanetOrSatellite(false);
					}
				} else {
					pauseIt(false);
					Mode = 0;
					setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("arrow.gif"), new Point(0,0), "mainCursor"));
				}
			}
		}
		);
		setBounds(0,0,WIDTH,HEIGHT);
		panel.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		panel.setLayout(null);
		panel.add(this);
		mainForm.setBounds(0,0,WIDTH,HEIGHT);
		mainForm.setUndecorated(true);
		mainForm.setResizable(false);
		mainForm.setVisible(true);
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		this.requestFocus();
		//(Graphics2D)strategy.getDrawGraphics();
		//showHelp();
		
		
		/*
		JDialog welcomeDialog = new JDialog();
		welcomeDialog.setBounds((WIDTH-400)/2, (HEIGHT-400)/2, 400, 400);
		welcomeDialog.setTitle("Welcome!");
		welcomeDialog.setModal(true);
		welcomeDialog.setLayout(null);
		welcomeDialog.setVisible(true);
		JLabel instructions = new JLabel();
		instructions.setText("Welcome to Brisco's Planetarium/nHow");
		welcomeDialog.add(instructions);
		*/
		//instructions.setBounds(0, 0, 400, 10);
		//requestFocus();
		updateWorld();
	}
	public void showHelp()
	{
		pauseIt(true);
		JOptionPane.showMessageDialog(this, "" +
				"Welcome to Brisco's Planetarium!\n\n" +
				"INSTRUCTIONS\n\n" +
				"F1 - opens this window\n" +
				"F2 - adds a star object\n" +
				"F3 - adds a planetary object\n" +
				"F4 - adds a satellite object\n" +
				"F8 - removes an object\n" +
				"F10 - shows / hides names and paths of all available objects\n\n" +
				"ESC - quit" +
				"\nFor suggestions and opinions write to brisco@data.bg"
				, "Welcome!", JOptionPane.PLAIN_MESSAGE);
		pauseIt(false);
	}
	
	public void initializeGFXSettings()
	{
		gfxST.put("sun1",new gfxATT("sun1.png",92,92,"sun1.gif",92,92));
		gfxST.put("sun_count", 1);
		gfxST.put("planet1", new gfxATT("pl1.png",30,30,"pl1.gif",30,30));
		gfxST.put("planet_count", 1);
		gfxST.put("satellite1", new gfxATT("sat1.png",30,30,"sat1.gif",30,30));
		gfxST.put("satellite_count", 1);
	}
	
	public void addSun()
	{
		//MouseEvent e = new MouseEvent();
		//int mX = MouseEvent.
		//this.mainForm.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("sunCursor.gif"), new Point(0,0), "sunCursor"));
		//myCursor = Toolkit.getDefaultToolkit().createCustomCursor(getSprite("sunCursor.gif"), new Point(0,0), "sunCursor");
				//JOptionPane myPane = new JOptionPane("add a star", JOptionPane.QUESTION_MESSAGE);
		//myPane.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("arrow.gif"), new Point(0,0), "mainCursor"));
		//myPane.setCursor(this.getCursor());
		String name = JOptionPane.showInputDialog(this, "Enter name (optional)", "Adding a Star to the Galaxy", JOptionPane.QUESTION_MESSAGE); 
		if (name != null)
		{
			flyingObject mySun = new flyingObject(name,"sun.png",MouseX,MouseY, 92,92, true);
			HashMapProtection(stars,mySun.uid,mySun);
			/*
			updateQuery = true;
			while(!updateQueryRecieved)
			{
				try
				{
					Thread.currentThread().wait(1);
				} catch (Exception e) {}
				
			}
			updateQueryRecieved = false;
			stars.put(mySun.uid,mySun);
			updateFinished = true;
			*/
			mySun.orbitTo = null;
		}
		pauseIt(false);
		Mode = 0;
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("arrow.gif"), new Point(0,0), "mainCursor"));
	}
	
	public void addPlanetOrSatellite(boolean isPlanet)
	{
		String name = JOptionPane.showInputDialog(this, "Enter name (optional)", "Adding a "+(isPlanet?"Planet":"Satellite")+" to the Galaxy", JOptionPane.QUESTION_MESSAGE); 
		if (name != null)
		{
			String velocity = "0";
			int velocityNum = 0;
			while (velocityNum == 0)
			{
				velocity = JOptionPane.showInputDialog(this, "Select velocity (1-500)", "Adding a "+(isPlanet?"Planet":"Satellite")+" to the Galaxy", JOptionPane.QUESTION_MESSAGE);
				try
				{
					velocityNum = Integer.parseInt(velocity);
					if (velocityNum < 1 || velocityNum > (isPlanet?500:100))
					{
					JOptionPane.showMessageDialog(this, "Please, add a number of the range 1-"+(isPlanet?"500":"100"), "Error", JOptionPane.ERROR_MESSAGE);
					velocityNum = 0;
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Please, add a number of the range 1-"+(isPlanet?"500":"100"), "Error", JOptionPane.ERROR_MESSAGE);
					velocityNum = 0;
				}
			}
			
			
			flyingObject myPlanetary = new flyingObject(name,"moon.png",MouseX,MouseY,30,30,false);
			myPlanetary.orbitTo = connectTo(myPlanetary, isPlanet);
			myPlanetary.radius = Math.sqrt(Math.abs(myPlanetary.orbitTo.x - myPlanetary.x)*Math.abs(myPlanetary.orbitTo.x - myPlanetary.x)+Math.abs(myPlanetary.orbitTo.y - myPlanetary.y)*Math.abs(myPlanetary.orbitTo.y - myPlanetary.y));
			if (myPlanetary.radius < 5)
			{
				myPlanetary.orbitTo.remove(myPlanetary.uid);
				myPlanetary = null;
				JOptionPane.showMessageDialog(this, "You are placing it too close to the star./nPlease, put it a bit further.", "Error!", JOptionPane.ERROR_MESSAGE);
			} else {
				myPlanetary.degree = Math.toDegrees(Math.asin(Math.abs((myPlanetary.y-myPlanetary.orbitTo.y))/myPlanetary.radius));
				if (myPlanetary.x < myPlanetary.orbitTo.x)
				{
					if (myPlanetary.y < myPlanetary.orbitTo.y)
					{
						myPlanetary.degree -= 180;
					} else {
						myPlanetary.degree = 180 - myPlanetary.degree;
					}
				} else {
					if (myPlanetary.y < myPlanetary.orbitTo.y)
					{
						myPlanetary.degree *= -1; //(90-myPlanet.degree);
					}
				}
				myPlanetary.velocity = ((double)velocityNum)/(myPlanetary.radius);
				myPlanetary.orbitTo.add(myPlanetary);
				if (debugModeOn)
				{
					System.out.println("new planet/satellite has been added");
					System.out.println(myPlanetary.name+" (uid: "+myPlanetary.uid+") with (x;y) = ("+myPlanetary.x+";"+myPlanetary.y+"), radius = "+myPlanetary.radius+", degree = "+myPlanetary.degree+", velocity = "+myPlanetary.velocity+" orbits "+myPlanetary.orbitTo.name);
				}
				if (isPlanet)
				{
					HashMapProtection(planets,myPlanetary.uid,myPlanetary);
				} else {
					HashMapProtection(satellites,myPlanetary.uid,myPlanetary);
				}
			}
		}
		pauseIt(false);
		Mode = 0;
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getSprite("arrow.gif"), new Point(0,0), "mainCursor"));
	}
	
	public void HashMapProtection(HashMap affectedObject, Object key, Object whatToPut)
	{
		updateQuery = true;
		while(!updateQueryRecieved) {}
		updateQueryRecieved = false;
		affectedObject.put(key,whatToPut);
		updateFinished = true;
	}
	
	public void HashMapProtection()
	{
		if (updateQuery)
		{
			updateQuery = false;
			updateQueryRecieved = true;
			while(!updateFinished) {}
			updateFinished = false;
		}
	}
	public flyingObject connectTo (flyingObject myObj, boolean isPlanet)
	{
		Iterator it;
		flyingObject master = null;
		HashMap masterSet;
		if (isPlanet)
		{
			masterSet = stars;
		} else {
			masterSet = planets;
		}
		double distance = 10000;
		it = masterSet.keySet().iterator();
		Object key;
		while (it.hasNext())
		{
			key = it.next();
			if (Math.sqrt(Math.abs(((flyingObject)masterSet.get(key)).x - myObj.x)*Math.abs(((flyingObject)masterSet.get(key)).x - myObj.x)+Math.abs(((flyingObject)masterSet.get(key)).y - myObj.y)*Math.abs(((flyingObject)masterSet.get(key)).y - myObj.y)) < distance)
			{
				master = (flyingObject)masterSet.get(key);
				distance = Math.sqrt(Math.abs(master.x - myObj.x)*Math.abs(master.x - myObj.x)+Math.abs(master.y - myObj.y)*Math.abs(master.y - myObj.y));
			}
		}
		return master;
	}
	
	public void DrawingEngine()
	{
		HashMapProtection();
		Graphics2D gfx = (Graphics2D)strategy.getDrawGraphics();
		/*
		if (updateQuery)
		{
			updateQuery = false;
			updateQueryRecieved = true;
			while(!updateFinished)
			{
				
				try
				{
					Thread.currentThread().wait(1);
				} catch (Exception e) {}
			}
			updateFinished = false;
		}

		 */
		/*
		if (!updating)
		{
			gfx = (Graphics2D)strategy.getDrawGraphics();
			//System.out.println("?");
		} else {
			gfx = screenShot.createGraphics();
			System.out.println("once");
			updateStarted = true;
		}
		*/
		
		/*
		if (paused)
		{
			if (screenShot == null)
			{
				BG = drawVolatileImage(gfx,BG,0,0,getSprite("sky.gif"));
				//System.out.println("null");
			} else {
				//BG = drawVolatileImage(gfx,BG,10,10,screenShot);
				//gfx.drawImage(screenShot, 0, 0, this);
				//System.out.println("hah");
			}
		} else {
			BG = drawVolatileImage(gfx,BG,0,0,getSprite("sky.gif"));
		}
		*/
		BG = drawVolatileImage(gfx,BG,0,0,getSprite("sky.gif"));
		/*if (!updating)
		{
			gfx.setColor(Color.white);
			gfx.drawString((paused == true)?"paused":"running",0,12);
		}*/
		
		gfx.setColor(Color.white);
		gfx.drawString((paused == true)?"paused":"running",0,12);
		/*if (!paused||updating)
		{*/
			//gfx.setColor(Color.black);
			//gfx.fillRect(0,0,WIDTH,HEIGHT);
			//gfx.drawImage(getSprite("moon.png"), x, 0, this);
			//System.out.println(stars.size());
		
			Iterator it;
			it = stars.values().iterator();
			while (it.hasNext())
			{
				try
				{
					flyingObject mySun = (flyingObject)it.next();
					mySun.paintObject(gfx, getSprite(mySun.usedImage), this,false,true,true,debugModeOn);
				} catch (Exception e) {}
			}
			it = planets.values().iterator();
			while (it.hasNext())
			{
				try
				{
					flyingObject myPlanet = (flyingObject)it.next();
					//System.out.println(myPlanet.orbitTo.name);
					if (/*!updating*/!paused)
					{
						myPlanet.orbit(1);
						//System.out.println(myPlanet.name+" orbits at "+myPlanet.degree+" degrees");
					}
					myPlanet.paintObject(gfx, getSprite(myPlanet.usedImage), this,true,true,true,debugModeOn);
				} catch (Exception e) {}
			}
			it = satellites.values().iterator();
			while (it.hasNext())
			{
				try
				{
					flyingObject mySatellite = (flyingObject)it.next();
					//System.out.println(myPlanet.orbitTo.name);
					if (/*!updating*/!paused)
					{
						mySatellite.orbit(1);
						//System.out.println(myPlanet.name+" orbits at "+myPlanet.degree+" degrees");
					}
					mySatellite.paintObject(gfx, getSprite(mySatellite.usedImage), this,true,true,true,debugModeOn);
				} catch (Exception e) {}
			}
			/*
			for (int s=0; s<stars.size(); s++)
			{
				flyingObject mySun = (flyingObject)stars.get(s);
				mySun.paintObject(gfx, getSprite(mySun.usedImage), this,false,true,true,false);
				//gfx.drawImage(getSprite(mySun.usedImage),mySun.x-(int)Math.round(getSprite(mySun.usedImage).getData().getWidth()/2),mySun.y-(int)Math.round(getSprite(mySun.usedImage).getData().getHeight()/2),this);
			}
			
			for (int p=0; p<planets.size(); p++)
			{
				flyingObject myPlanet = (flyingObject)planets.get(p);
				//System.out.println(myPlanet.orbitTo.name);
				myPlanet.orbit(1);
				myPlanet.paintObject(gfx, getSprite(myPlanet.usedImage), this,true,true,true,false);
			}
			*/
		//this.crea
		//}
		//if (!updating)
			strategy.show();
		/*
		if (updating&&updateStarted)
		{
			updating = false;
			paused = true;
		}*/
	}
	
	public void paintBG()
	{
		//double radius = Math.sqrt((myx-xx)*(myx-xx) + (myy - yy)*(myy-yy));
		/*
		MyY += 1;
		if (MyY >= Ycenter + MyRad)
		{
			subs = true;
			//MyY
		}*/
		
		//MyX = (int) (Xcenter + Math.round(Math.sqrt(MyRad*MyRad - (MyY - Ycenter)*(MyY - Ycenter)) + Xcenter));
		//MyX = (int) (Xcenter + Math.round(MyRad*Math.cos(Math.acos((MyX-Xcenter)/MyRad)-Math.toRadians(1))));
		//MyY = (int) (Ycenter + Math.round(MyRad*Math.sin(Math.asin((MyY-Ycenter)/MyRad)-Math.toRadians(1))));
		//System.out.println(Math.toDegrees(Math.asin((MyY-Ycenter)/MyRad)));
		//myx = ratio/myy;
		
		/*
		MyD+=10;
		while (MyD>360)
		{
			MyD = MyD % 360;
		}
		MyX = (int) (Xcenter + Math.round(MyRad*Math.cos(Math.toRadians(MyD))));
		MyY = (int) (Ycenter + Math.round(MyRad*Math.sin(Math.toRadians(MyD))));
		Graphics2D gfx = (Graphics2D)strategy.getDrawGraphics();
		gfx.setColor(Color.RED);
		gfx.drawRect(Xcenter-2, Ycenter-2, 4, 4);
		gfx.drawRect((int)Math.round(Xcenter-MyRad), (int)Math.round(Ycenter-MyRad), (int)(Math.round(2*MyRad)), (int)(Math.round(2*MyRad)));
		gfx.drawArc((int)Math.round(Xcenter-MyRad), (int)Math.round(Ycenter-MyRad), (int)(Math.round(2*MyRad)), (int)(Math.round(2*MyRad)),0,360);
		*/
		
		//gfx.drawArc((int)(Xcenter-Math.round(MyRad/Math.sqrt(2))),(int)(Ycenter-Math.round(MyRad/Math.sqrt(2))),(int)Math.round(2*MyRad),(int)Math.round(2*MyRad),0,360);
		//System.out.println(""+this.MyRad+","+Math.round(this.MyRad));
		//gfx.drawArc((int)Math.round(xx-this.MyRad),(int)Math.round(yy-this.MyRad),(int)Math.round(2*this.MyRad),(int)Math.round(2*this.MyRad), 0, 360);
		
		/*
		gfx.setColor(Color.YELLOW);
		gfx.drawRect(MyX-2, MyY-2, 4, 4);
		*/
	}
	
	public void updateWorld()
	{
		while (isVisible()) {
			//calculating positions.
			//for (int i = 0; i < flyingObjects.size(); i++) {
			//flyingObject m = (flyingObject)flyingObjects.get(0);
			//m.x = m.x +1;
			//m.y = 10*i;
			//}
			
			//drawing gfx
//			System.exit(0);
				
				
			//ImageObserver img = gfx
			
			//gfx.drawImage(getSprite("sky.gif"),0,0,this);
			//if ()
			DrawingEngine();
			if (debugModeOn)
			try {
			 	Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
	}
	
	public void pauseIt(boolean stop)
	{
		paused = stop;
		/*
		if (stop)
		{
			
			try {
				//Graphics2D gfx = (Graphics2D)this.strategy.getDrawGraphics();
				Robot screenCapturer = new Robot();
				//screenShot = (BufferedImage)this.;
				screenShot = screenCapturer.createScreenCapture(new Rectangle(0,0,WIDTH,HEIGHT));
			} catch (Exception e){}
			
			//DrawingEngine((Graphics2D)screenShot.getGraphics(),false);
			paused = true;
		} else {
			//screenShot = null;
			paused = false;
			try{
				ImageIO.write(screenShot, "png", new File("newimage.png"));
		        } catch (Exception e) {}
		}*/
	}
	
	public VolatileImage drawVolatileImage(Graphics2D g, VolatileImage img, int x, int y, BufferedImage orig) {
		final int MAX_TRIES = 100;
		for (int i=0; i<MAX_TRIES; i++)
		{
			if (img != null)
			{
				// Draw the volatile image
				g.drawImage(img, x, y, null);
				// Check if it is still valid
				if (!img.contentsLost())
				{
					return img;
				}
			} else {
				// Create the volatile image
				img = g.getDeviceConfiguration().createCompatibleVolatileImage(orig.getWidth(null), orig.getHeight(null));
			}
			// Determine how to fix the volatile image
			switch (img.validate(g.getDeviceConfiguration()))
			{
				case VolatileImage.IMAGE_OK:
					// This should not happen
					break;
				case VolatileImage.IMAGE_INCOMPATIBLE:
					// Create a new volatile image object;
					// this could happen if the component was moved to another device
					img.flush();
					img = g.getDeviceConfiguration().createCompatibleVolatileImage(orig.getWidth(null), orig.getHeight(null));
				case VolatileImage.IMAGE_RESTORED:
					// Copy the original image to accelerated image memory
					Graphics2D gc = (Graphics2D)img.createGraphics();
					gc.drawImage(orig, 0, 0, null);
					gc.dispose();
					break;
			}
		}
		// The image failed to be drawn after MAX_TRIES;
		// draw with the non-accelerated image
		g.drawImage(orig, x, y, null);
		return img;
	}
	
	public BufferedImage loadImage(String path) {
		URL url=null;
		try {
			url = getClass().getClassLoader().getResource(path);
			return ImageIO.read(url);
		} catch (Exception e) {
			/*
			System.out.println("The Image " + path +" was not found at the url:"+url);
			System.out.println("The error was: "+e.getClass().getName()+" "+e.getMessage());
			System.exit(0);
			*/
			return null;
		}
	}
	
	public BufferedImage getSprite(String uid) {
		BufferedImage img = (BufferedImage)	sprites.get(uid);
		if (img == null) {
			img = loadImage("res/"+uid);
			sprites.put(uid,img);
		}
		return img;
	}
	
	public static void main(String[] args) {
		boolean safeMode = false;
		if (args.length > 0) {
			if (args[0].equals("-safemode"))
			{
				safeMode = true;
			}
		}
		spaceField FreeSpace = new spaceField(safeMode);
		//flyingObject Galaxy = new flyingObject("galaxy",null,true);
		//FreeSpace.updateWorld();
		//Galax
	}
	
	class gfxATT
	{
		public int realWidthInNormal, realHeightInNormal, realWidthInSafe, realHeightInSafe;
		public String normal, safemode;
		
		gfxATT (String normalUrl, int rwidthNormal, int rheightNormal, String safeUrl, int rwidthSafe, int rheightSafe)
		{
			normal = normalUrl;
			safemode = safeUrl;
			realWidthInNormal = rwidthNormal;
			realHeightInNormal = rheightNormal;
			realWidthInSafe = rwidthSafe;
			realHeightInSafe = rheightSafe;
		}
	}

}
