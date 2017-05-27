package Model;

public class Board {
	
	static final int AREA_I = 1;
	static final int AREA_II = 5;
	static final int AREA_III = 10;
	static final int SECTOR_I = 1;
	static final int SECTOR_II = 10;
	static final int SECTOR_III = 20;
	static final int SECTOR_IV = 40;
	static final int BEAT_POINTS = 30;
	static final int BACKUP_POINTS = 10;
	static final int COUNT_FACTOR = 5;
	
	// positions row/col from 0 to 7

	private Checker [][] board;
	
	private boolean if_double_move = false; // if currently move checker has to beat another one
	
	private boolean if_checker_beaten = false; //if checker was beaten in current move
	public int beaten_checker_row;
	public int beaten_checker_col;
	
	private int white_number = 12;
	private int black_number = 12;	
	
	public Board(){
		board = new Checker [8][8];
		
		for(int i = 0; i<3; i++){
			for(int j = i%2==0? 1 : 0, k = i%2==0? 0 : 1 ; j<8; j+=2, k+=2){
				 board[i][j] = new Checker(CheckerColor.BLACK);
				 board[i+5][k] = new Checker(CheckerColor.WHITE);
			}
		}
	}
	
	public Board(Board board){
		
		this.board = new Checker [8][8];
		
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				if(board.board[i][j] != null){
					Checker c  = board.board[i][j];
					Checker new_c = new Checker(c.color);
					this.board[i][j] = new_c;
				}
			}
		}
		
		
		this.white_number = board.white_number;
		this.black_number = board.black_number;
		
		this.if_double_move = board.if_double_move;
		
		this.if_checker_beaten = board.if_checker_beaten;
		this.beaten_checker_row = board.beaten_checker_row;
		this.beaten_checker_col = board.beaten_checker_col;
	}
	
	public void moveChecker(int old_row, int old_col, int new_row, int new_col){
		resetBeatenChecker();
		resetDoubleMove();
		Checker c = board[old_row][old_col];
		board[old_row][old_col] = null;
		board[new_row][new_col] = c;
		
		if(new_row - old_row == 2){
			if_checker_beaten = true;
			beaten_checker_row = old_row+1;
			beaten_checker_col = new_col - old_col == 2? old_col +1 : old_col-1;
			removeChecker(beaten_checker_row, beaten_checker_col);
		}
		else if(new_row - old_row == -2){
			if_checker_beaten = true;
			beaten_checker_row = old_row-1;
			beaten_checker_col = new_col - old_col == 2? old_col +1 : old_col-1;
			removeChecker(beaten_checker_row, beaten_checker_col);
		}
	}
	
	public void removeChecker(int row, int col){
		CheckerColor c = board[row][col].color;
		board[row][col] = null;
		if(c == CheckerColor.WHITE) white_number --;
		else black_number --;
	}
	
	public Checker getChecker(int row, int col){
		return board[row][col];
	}
	
	public boolean isOccupied(int row, int col){
		return board[row][col] != null;
	}
	
	public boolean isDoubleMove(){
		return if_double_move;
	}
	
	public boolean isCheckerBeaten(){
		return if_checker_beaten;
	}
	
	public void resetBeatenChecker(){
		if_checker_beaten = false;
		beaten_checker_col = -1;
		beaten_checker_row = -1;
	}
	
	public void resetDoubleMove(){
		if_double_move = false;
	}
	

	public boolean validateMove(int old_row, int old_col, int new_row, int new_col){
			
		if(new_row>7 || new_row<0 || new_col>7 || new_col<0) return false;
		CheckerColor color = getChecker(old_row, old_col).color;
		
		if(color == CheckerColor.WHITE){
			if(new_row - old_row == -1)	return (new_col == old_col+1 || new_col ==  old_col -1) && onePositionMoveAbility(new_row, new_col);	
			else if(new_row - old_row == -2){
				if(new_col == old_col+2) return twoPositionsMoveAbility(new_row, new_col, old_row-1, old_col+1, CheckerColor.BLACK);				
				else if(new_col ==  old_col -2) return twoPositionsMoveAbility(new_row, new_col, old_row-1, old_col-1, CheckerColor.BLACK);
			}
			else return false;
		}
		else{
			if(new_row - old_row == 1)	return (new_col == old_col+1 || new_col ==  old_col -1) && onePositionMoveAbility(new_row, new_col);	
			else if(new_row - old_row == 2){
				if(new_col == old_col+2) return twoPositionsMoveAbility(new_row, new_col, old_row+1, old_col+1, CheckerColor.WHITE);				
				else if(new_col ==  old_col -2) return twoPositionsMoveAbility(new_row, new_col, old_row+1, old_col-1, CheckerColor.WHITE);
			}
			else return false;
		}
		return false;
	}
	
	public boolean canMakeAnotherMove(int row, int col, CheckerColor color){ // determines if checker that beaten enemies checker can beat another one

		if(!isCheckerBeaten()) return false;
		
		resetBeatenChecker();
		if(!isCheckerAbleToMove(row,col,color)) return false;
		if(color == CheckerColor.WHITE){
			if(twoPositionsMoveAbility(row-2, col-2, row-1, col-1,  CheckerColor.BLACK) 
					|| twoPositionsMoveAbility(row-2, col+2, row-1, col+1,  CheckerColor.BLACK)){
				if_double_move = true;
				return true;
			}
			else return false;
		}
		else {
			if(twoPositionsMoveAbility(row+2, col-2, row+1, col-1,  CheckerColor.WHITE)
					|| twoPositionsMoveAbility(row+2, col+2, row+1, col+1,  CheckerColor.WHITE)){
				if_double_move = true;	
				return true;
			}
			else return false;		
		}
	}
	
	public GameState validateGameState(){
		
		if(white_number == 0) return GameState.BLACK_PLAYER_WON;
		else if(black_number == 0) return GameState.WHITE_PLAYER_WON;
		else {
			boolean isFinished_black = false;
			boolean isFinished_white = false;
			
			int min_black_row = 7;
			int min_white_row = 0;
			for(int i = 0; i<8; i++){
				for(int j = 0; j<8; j++){
					if(isOccupied(i, j) && getChecker(i, j).color==CheckerColor.WHITE) {
						if(i>min_white_row) min_white_row = i;
					}
					else if(i<min_black_row) min_black_row = i;			
				}
			}
			
			if(min_black_row >= min_white_row) {
				isFinished_black = true;
				isFinished_white = true;
			}
			else{
				boolean white_can_move = false;
				boolean black_can_move = false;
				for(int i = 0; i<8; i++){
					for(int j = 0; j<8; j++){
						if(!(white_can_move && black_can_move)  && isOccupied(i, j)) {
							if(getChecker(i, j).color==CheckerColor.WHITE && isCheckerAbleToMove(i, j, CheckerColor.WHITE)) white_can_move = true;
							else if(getChecker(i, j).color==CheckerColor.BLACK && isCheckerAbleToMove(i, j,CheckerColor.BLACK)) black_can_move = true;
						}			
					}		
				}
				if(!white_can_move) isFinished_white = true;
				if(!black_can_move) isFinished_black = true;
			}
					
			if(isFinished_black || isFinished_white){
				if(white_number == black_number) return GameState.TIE;
				else if(white_number > black_number) return GameState.WHITE_PLAYER_WON;
				else  return GameState.BLACK_PLAYER_WON;
			}
		}
		return GameState.IN_PLAY;
	}
	
	public boolean isCheckerAbleToMove(int row, int col, CheckerColor color){	
		if(color == CheckerColor.WHITE){
			if(row==0) return false;
			return onePositionMoveAbility(row-1, col-1) || onePositionMoveAbility(row-1, col+1)
					|| twoPositionsMoveAbility(row-2, col-2, row-1, col-1,  CheckerColor.BLACK)
					|| twoPositionsMoveAbility(row-2, col+2, row-1, col+1,  CheckerColor.BLACK);
		}
		else{
			if(row==7) return false;
			return onePositionMoveAbility(row+1, col-1) || onePositionMoveAbility(row+1, col+1)
					|| twoPositionsMoveAbility(row+2, col-2, row+1, col-1,  CheckerColor.WHITE)
					|| twoPositionsMoveAbility(row+2, col+2, row+1, col+1,  CheckerColor.WHITE);		
		}
	}
	
	public boolean onePositionMoveAbility(int new_row, int new_col){
		return new_col>=0 && new_col<=7 && new_row>=0 && new_row<=7 && !isOccupied(new_row, new_col);
	}
	
	public boolean twoPositionsMoveAbility(int new_row, int new_col, int mid_row, int mid_col, CheckerColor opponent_color){
		return new_col>=0 && new_col<=7 && new_row>=0 && new_row<=7 && isOccupied(mid_row, mid_col) && getChecker(mid_row, mid_col).color == opponent_color && !isOccupied(new_row, new_col);
		
	}
	
	public int getCheckersNumberScore(CheckerColor color){
		if(color == CheckerColor.WHITE) return isCheckerBeaten()? (100+white_number * COUNT_FACTOR) : white_number * COUNT_FACTOR; 
		else return isCheckerBeaten()? (100+black_number * COUNT_FACTOR) : black_number * COUNT_FACTOR; 
	}
	
	public int getCheckersBeatScore(CheckerColor color){
		
		int score = 0;
		
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				if(isOccupied(i, j) && board[i][j].color == color){
					if(color == CheckerColor.WHITE){
						if(twoPositionsMoveAbility(i-2, j-2, i-1, j-1, CheckerColor.BLACK)) {
							score += BEAT_POINTS;
							if(i+1<7 && j+1<7 && isOccupied(i+1, j+1)) score += BACKUP_POINTS;								
						}
						if(twoPositionsMoveAbility(i-2, j+2, i-1, j+1, CheckerColor.BLACK)){
							score += BEAT_POINTS;
							if(i+1<7 && j-1>0 && isOccupied(i+1, j-1)) score += BACKUP_POINTS;
						}		
					}
					else{
						if(twoPositionsMoveAbility(i+2, j-2, i+1, j-1, CheckerColor.WHITE)) {
							score += BEAT_POINTS;
							if(i-1>0 && j+1<7 && isOccupied(i-1, j+1)) score += BACKUP_POINTS;
						}
						if(twoPositionsMoveAbility(i+2, j+2, i+1, j+1, CheckerColor.WHITE)) {
							score += BEAT_POINTS;							
							if(i-1>0 && j-1>0 &&isOccupied(i-1, j-1)) score += BACKUP_POINTS;
						}					
					}
				}
			}
		}
		return score;
	}
	
	public int getAreasScore(CheckerColor color){
		int score = 0;
		
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				if(isOccupied(i, j) && board[i][j].color == color) score += countArea(i, j);
			}
		}
		return score;
	}
	
	private int countArea(int i, int j){
		
		int area = AREA_III;
		if(i>0 && i<7 && j>0 && j<7){
			area = AREA_II;
			if(i>1 && i<6 && j>1 && j<6) area = AREA_I;
		}
		return area;
	}
	
	public int getSectorScore(CheckerColor color){
		int score = 0;
		
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				if(isOccupied(i, j) && board[i][j].color == color) {
					if(color == CheckerColor.WHITE){
						if(i<2) score += SECTOR_IV;
						else if(i<4) score += SECTOR_III;
						else if(i<6) score += SECTOR_II;	
						else score += SECTOR_I;
					}
					else{					
						if(i>5) score += SECTOR_IV;
						else if(i>3) score += SECTOR_III;
						else if(i>1) score += SECTOR_II;	
						else score += SECTOR_I;
					}
				}
			}
		}
		return score;
	}
}
