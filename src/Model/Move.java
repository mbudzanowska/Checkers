package Model;

public class Move {

	public int old_row;
	public int old_col;
	public int new_row;
	public int new_col;
	
	public Move(int o_r, int o_c, int n_r, int n_c){
		old_row = o_r;
		old_col = o_c;
		new_row = n_r;
		new_col = n_c;
	}
}
