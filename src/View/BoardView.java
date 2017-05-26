package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import Controller.Game;
import Model.CheckerColor;

public class BoardView extends JComponent {
	
	// positions row/col from 1 to 8
	
	private final static int SQUARE_SIZE = (int) (CheckerView.getDimension() * 1.25);
	private final int BOARD_SIZE =  8*SQUARE_SIZE;
	private Dimension BOARD_DIMENSION;
	
	private boolean inDrag = false;
	private int deltaX, deltaY;  // displacement between drag start coordinates and checker center coordinates
	private CheckerPosition checkerPosition; // reference to position of checker at start of drag
	private int oldCX, oldCY; // center location of checker at start position;
	private List<CheckerPosition> checkers;
	
	private static CheckerColor player_color;
	
	private BoardViewListener boardViewListener;
	private boolean listener_called = false; // to make sure that the listener method is only called once
	
	private CheckerPosition checker_to_remove; // to make sure that checker is not removed before the end of list iteration
	private boolean forced_checker_move;
	private CheckerPosition forced_checker;
	
	// constructor
	public BoardView(){
		
		checkers = new ArrayList<>();
		BOARD_DIMENSION = new Dimension(BOARD_SIZE, BOARD_SIZE);
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent mouse){
				
				 int x = mouse.getX();
                 int y = mouse.getY();
                 
                 if(forced_checker_move){
                	 //poprawiæ tutaj jeœli wymuszony drugi ruch a ruszy przeciwnik	 
	                	 if (CheckerView.contains(x, y, forced_checker.cx, forced_checker.cy))
	                      {
	                		 
	                         BoardView.this.checkerPosition = forced_checker;
	                         oldCX = checkerPosition.cx;
	                         oldCY = checkerPosition.cy;
	                         deltaX = x - checkerPosition.cx;
	                         deltaY = y - checkerPosition.cy;
	                         inDrag = true;
	                         return;
	                      }          	 
                 }
                 else {
                	 for (CheckerPosition checkerPosition: checkers)
                         if (checkerPosition.checker.color == player_color 
                         		&& CheckerView.contains(x, y, checkerPosition.cx, checkerPosition.cy))
                         {
                            BoardView.this.checkerPosition = checkerPosition;
                            oldCX = checkerPosition.cx;
                            oldCY = checkerPosition.cy;
                            deltaX = x - checkerPosition.cx;
                            deltaY = y - checkerPosition.cy;
                            inDrag = true;
                            return;
                         }
                 }        
			}
			
			@Override
			public void mouseReleased(MouseEvent mouse){
				
				if (inDrag)
                    inDrag = false;
                 else
                    return;
				
				boolean moved = false;
				
				 int x = mouse.getX();
                 int y = mouse.getY();
                 checkerPosition.cx = (x - deltaX) / SQUARE_SIZE * SQUARE_SIZE + SQUARE_SIZE / 2;
                 checkerPosition.cy = (y - deltaY) / SQUARE_SIZE * SQUARE_SIZE + SQUARE_SIZE / 2;

                 int i = (checkerPosition.cx - SQUARE_SIZE / 2)/SQUARE_SIZE+1;
             	 int j = (checkerPosition.cy - SQUARE_SIZE / 2)/SQUARE_SIZE+1;
             	 
             	 int old_i = (oldCX - SQUARE_SIZE / 2)/SQUARE_SIZE+1;
             	 int old_j = (oldCY - SQUARE_SIZE / 2)/SQUARE_SIZE+1;
             	 
             	 if(i%2==0 && j%2==0 || j%2!=0 && i%2!=0) {
             		BoardView.this.checkerPosition.cx = oldCX;
                    BoardView.this.checkerPosition.cy = oldCY;
             	 }
             	 else {
             		 for (CheckerPosition checkerPosition: checkers)
                         if (checkerPosition != BoardView.this.checkerPosition && 
                         		checkerPosition.cx == BoardView.this.checkerPosition.cx &&
                         				checkerPosition.cy == BoardView.this.checkerPosition.cy)
                         {                      	
                        	 BoardView.this.checkerPosition.cx = oldCX;
                             BoardView.this.checkerPosition.cy = oldCY;
                             break;
                         }
                        else if(!listener_called){
                        	listener_called = true;
                        	moved = boardViewListener.validateMove(old_j, old_i, j, i, player_color);
                        	if(!moved){
                        		 // to make sure that the listener method is only called once
                            	BoardView.this.checkerPosition.cx = oldCX;
                                BoardView.this.checkerPosition.cy = oldCY;   
                        	}
                        	break;
                        }                        

             		 
             	 }
                
                 checkerPosition = null;
                 repaint();
                 listener_called = false;
                 
                 //forced_checker_move = false;
                 if(moved) {
                	 boardViewListener.moveMade();               	
                 }
                 
                 if(checker_to_remove != null){
                	 checkers.remove(checker_to_remove);
                	 checker_to_remove = null;
                	 repaint();
                 }
			}
			 
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			

			@Override
			public void mouseDragged(MouseEvent mouse) {
				if (inDrag)
                {
                   // Update location of checker center.
                   checkerPosition.cx = mouse.getX() - deltaX;
                   checkerPosition.cy = mouse.getY() - deltaY;
                   repaint();
                }
			}
		});
	}
	
	public void setBoardViewListener(BoardViewListener listener){
		this.boardViewListener = listener;
	}
	
	
	 public void add(CheckerView checker, int row, int col)
	   {
	      if (row < 1 || row > 8)
	         throw new IllegalArgumentException("row out of range: " + row);
	      if (col < 1 || col > 8)
	         throw new IllegalArgumentException("col out of range: " + col);
	      CheckerPosition checkerPosition = new CheckerPosition();
	      checkerPosition.checker = checker;
	      checkerPosition.cx = (col - 1) * SQUARE_SIZE + SQUARE_SIZE / 2;
	      checkerPosition.cy = (row - 1) * SQUARE_SIZE + SQUARE_SIZE / 2;
	      for (CheckerPosition _checkerPosition: checkers)
	         if (checkerPosition.cx == _checkerPosition.cx && checkerPosition.cy == _checkerPosition.cy)
	        	 throw new AlreadyOccupiedException("square at (" + row + "," +
                         col + ") is occupied");
	      checkers.add(checkerPosition);
	   }
	 
	 public void forceCheckerMove(int row, int col){
		 forced_checker_move = true;
		 for (CheckerPosition checkerPosition: checkers){
			   if(checkerPosition.cx == (col - 1) * SQUARE_SIZE + SQUARE_SIZE / 2
				   && checkerPosition.cy == (row - 1) * SQUARE_SIZE + SQUARE_SIZE / 2) forced_checker =  checkerPosition;
		   }
	 }
	 
	 
	 @Override
	   protected void paintComponent(Graphics g)
	   {
	      paintCheckerBoard(g);
	      for (CheckerPosition checkerPosition: checkers)
	         if (checkerPosition != BoardView.this.checkerPosition)
	        	 checkerPosition.checker.draw(g, checkerPosition.cx, checkerPosition.cy);

	      // Draw dragged checker last so that it appears over any underlying 
	      // checker.

	      if (checkerPosition != null)
	    	  checkerPosition.checker.draw(g, checkerPosition.cx, checkerPosition.cy);
	   }

	   private void paintCheckerBoard(Graphics g)
	   {
	      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                                        RenderingHints.VALUE_ANTIALIAS_ON);

	      // Paint checkerboard.

	      for (int row = 0; row < 8; row++)
	      {
	         g.setColor(((row & 1) != 0) ? Color.BLACK : Color.WHITE);
	         for (int col = 0; col < 8; col++)
	         {
	            g.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
	            g.setColor((g.getColor() == Color.BLACK) ? Color.WHITE : Color.BLACK);
	         }
	      }
	   }
	 
	 @Override
	   public Dimension getPreferredSize()
	   {
	      return BOARD_DIMENSION;
	   }
	 
	 public void changePlayer(CheckerColor color){
		 player_color = color;
	 }
	 
	 public void moveChecker(int oldY, int oldX, int newY, int newX){
		 int oldCX = countC(oldX);
		 int oldCY = countC(oldY);
		 int newCX = countC(newX);
		 int newCY = countC(newY);
		   for (CheckerPosition checkerPosition: checkers){
			   if(checkerPosition.cx == oldCX && checkerPosition.cy == oldCY) {
				   checkerPosition.cx = newCX;
				   checkerPosition.cy = newCY;
				   repaint();
			   }
		   }
	 }
	 
	 public void removeChecker(int oldY, int oldX){
		 CheckerPosition chosen = null;
		 int oldCX = countC(oldX);
		 int oldCY = countC(oldY);
		 for (CheckerPosition checkerPosition: checkers){
			   if(checkerPosition.cx == oldCX && checkerPosition.cy == oldCY) {
				   chosen = checkerPosition;
			   }
		 }
		 checker_to_remove = chosen;
	 }
	 
	 public int countC(int i){
		 return (i - 1) * SQUARE_SIZE + SQUARE_SIZE / 2;     
	 }
	 
	
	 // positioned checker helper class

	   private class CheckerPosition
	   {
	      public CheckerView checker;
	      public int cx;
	      public int cy;
	   }
	   
	   public interface BoardViewListener {
		   
		   public boolean validateMove(int old_row, int old_col, int new_row, int new_col, CheckerColor color);
		   public void moveMade();
		   
	   }

	public void resetForcedMove() {
		forced_checker = null;
		forced_checker_move = false;		
	}
	   
}
