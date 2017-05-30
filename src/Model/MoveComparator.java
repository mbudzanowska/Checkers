package Model;

import java.util.Comparator;

public class MoveComparator implements Comparator<Move>{

	@Override
	public int compare(Move m1, Move m2) {
		
		return Math.abs(m1.new_row - m1.old_row) > Math.abs(m2.new_row - m2.old_row) ? 1 
				: Math.abs(m1.new_row - m1.old_row) == Math.abs(m2.new_row - m2.old_row)? 0 : -1;
	}	
}