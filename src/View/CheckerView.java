package View;
import java.awt.Color;
import java.awt.Graphics;
import Model.CheckerColor;

public class CheckerView {

	private final static int DIMENSION = 50;
	CheckerColor color;
	
	public CheckerView(CheckerColor color){
	      this.color = color;
	}
	
	public void draw(Graphics g, int cx, int cy){
		
	    int x = cx - DIMENSION / 2;
	    int y = cy - DIMENSION / 2;
	    
	    g.setColor(Color.GRAY);
	    g.fillOval(x, y, DIMENSION, DIMENSION);
	    g.setColor(color == CheckerColor.BLACK ? Color.BLACK : Color.WHITE);
	    g.fillOval(x+3, y+3, DIMENSION-10, DIMENSION-10);
	     
	}
	
	public static boolean contains(int x, int y, int cx, int cy){
	      return (cx - x) * (cx - x) + (cy - y) * (cy - y) < DIMENSION / 2 * DIMENSION / 2;
	}
	
	public static int getDimension(){
	      return DIMENSION;
	}
}
