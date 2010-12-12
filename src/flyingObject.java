import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
//import java.util.ArrayList;
import java.awt.image.ImageObserver;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
//import java.awt.font.fon

public class flyingObject {
	
	public String uid;
	public String name;
	public int x = 0;
	public int y = 0;
	public int realWidth = 0;
	public int realHeight = 0;
	public double velocity;
	public double radius = 0;
	public double degree = 0;
	public String usedImage;
	public boolean moveToTheRight;
	public BufferStrategy strategy;
	public boolean fixed;
	public boolean debugged = false;
	public HashMap orbitingObjects = new HashMap();
	public flyingObject orbitTo;
	public final Font font = new Font("Serif", Font.PLAIN, 12);
	
	public flyingObject(String name, String usedImage, int x, int y, int width, int height, boolean fixed) {
		uid = Long.toString(System.currentTimeMillis()) + "$" + Long.toString(Math.round(Math.random()*1256000));
		this.name = name;
		this.usedImage = usedImage;
		this.fixed = fixed;
		this.x = x;
		this.y = y;
		this.realWidth = width;
		this.realHeight = height;
	}
	
	public flyingObject(String name, String usedImage, boolean fixed, int x, int y, int width, int height, double rad, double deg, double velocity, boolean moveToTheRight) {
		uid = Long.toString(System.currentTimeMillis()) + "$" + Long.toString(Math.round(Math.random()*1256000));
		this.name = name;
		this.usedImage = usedImage;
		this.fixed = fixed;
		this.x = x;
		this.y = y;
		this.realWidth = width;
		this.realHeight = height;
		this.radius = rad;
		this.degree = deg;
		this.velocity = velocity;
		this.moveToTheRight = moveToTheRight;
	}
	
	public void paintObject(Graphics2D gfx, BufferedImage sprite, ImageObserver img, boolean drawOrbit, boolean drawName, boolean drawRadius, boolean debugging)
	{
		if ((drawOrbit || debugging || debugged)&&!fixed)
		{
			gfx.setColor(Color.RED);
			gfx.drawArc((int) (orbitTo.x - Math.round(radius)), (int) (orbitTo.y - Math.round(radius)), (int) (Math.round(radius*2)), (int) (Math.round(radius*2)), 0, 360);
		}
		if ((drawRadius || debugging || debugged)&&!fixed)
		{
			gfx.setColor(Color.GREEN);
			gfx.drawLine(orbitTo.x, orbitTo.y, x, y);
		}
		if (debugging || debugged)
		{
			gfx.setColor(Color.GREEN);
			gfx.drawLine(x-100, y, x+100, y);
			gfx.drawLine(x, y-100, x, y+100);
			gfx.setColor(Color.YELLOW);
			gfx.drawRect(x-4, y-4, 8, 8);
		} else {
			gfx.drawImage(sprite, x-Math.round(sprite.getWidth()/2), y-Math.round(sprite.getHeight()/2), img);
		}
		if (drawName || debugging || debugged)
		{
			FontMetrics fsize = gfx.getFontMetrics();
			
			gfx.setColor(Color.YELLOW);
			gfx.drawString(name, x-Math.round(fsize.stringWidth(name)/2), y+Math.round(realHeight/2)+fsize.getHeight());
		}
	}
	
	public void orbit(long time)
	{
		if (fixed ==  false)
		{
			for (long i=0; i<time; i++)
			
			{
				degree += velocity;
				while (degree>360)
				{
					degree = degree % 360;
				}
			}
			x = (int) (orbitTo.x + Math.round(radius*Math.cos(Math.toRadians(degree))));
			y = (int) (orbitTo.y + Math.round(radius*Math.sin(Math.toRadians(degree))));
		}
	}
	
	public void add(flyingObject planetary)
	{
		orbitingObjects.put(planetary.uid,planetary);
	}
	
	public void remove(Object key)
	{
		orbitingObjects.remove(key);
	}
}
