package Model;

import java.util.ArrayList;
import java.util.List;

public class Board {
	
	public enum FieldState {
		BLACK,
		WHITE,
		EMPTY,
	}
	
	static final int AREA_I = 1;
	static final int AREA_II = 5;
	static final int AREA_III = 10;
	static final int SECTOR_I = 1;
	static final int SECTOR_II = 5;
	static final int SECTOR_III = 10;
	static final int SECTOR_IV = 20;
	static final int BEAT_POINTS = 50;
	static final int BACKUP_POINTS = 50;
	static final int COUNT_FACTOR = 5;
	
	private FieldState [][] board;
	private boolean if_double_move = false; // if currently move checker has to beat another one
	private int double_checker_row;
	private int double_checker_col;
	
	private boolean if_checker_beaten = false; //if checker was beaten in current move
	public int beaten_checker_row;
	public int beaten_checker_col;
	
	private int white_number = 12;
	private int black_number = 12;	
	
	private List<Move> availableBlackMoves;
	private List<Move> availableWhiteMoves;
	
	private GameState current_game_state;
	
	public Board(){
		
		board = new FieldState [8][8];
		
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				board[i][j] = FieldState.EMPTY;
			}
		}
		
		for(int i = 0; i<3; i++){
			for(int j = i%2==0? 1 : 0, k = i%2==0? 0 : 1 ; j<8; j+=2, k+=2){
				 board[i][j] = FieldState.BLACK;
				 board[i+5][k] = FieldState.WHITE;
			}
		}	
	}
	
	public Board(Board board){
		this.board = new FieldState [8][8];
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				this.setChecker(i, j, board.getChecker(i, j));
			}
		}	
		this.white_number = board.white_number;
		this.black_number = board.black_number;
		this.if_double_move = board.if_double_move;
		this.double_checker_col = board.double_checker_col;
		this.double_checker_row = board.double_checker_row;
		
	}
	
	private FieldState getChecker(int row, int col){
		if ((row > 7) || (row < 0) || (col > 7) || (col < 0)) return null;
		return board[row][col];
	}
	
	private void setChecker(int row, int col, FieldState checker){
		if ((row  <= 7) && (row >= 0) && (col <= 7) && (col >= 0))
		board [row][col] = checker;
	}
	
	public void moveChecker(Move move){
		resetState();
		setChecker(move.new_row, move.new_col, getChecker(move.old_row, move.old_col));
		setChecker(move.old_row, move.old_col, FieldState.EMPTY);
		
		if(Math.abs(move.new_row - move.old_row) == 2){
			if_checker_beaten = true;
			beaten_checker_row = (move.new_row + move.old_row)/2;
			beaten_checker_col = (move.new_col + move.old_col)/2;
			removeChecker(beaten_checker_row, beaten_checker_col);
			canMakeAnotherMove(move.new_row, move.new_col);
		}
	}
	
	public void removeChecker(int row, int col){
		if(getChecker(row, col) == FieldState.BLACK) black_number --;
		else white_number --;
		setChecker(row, col, FieldState.EMPTY);
	}
	
	private void resetState(){
		if_checker_beaten = false;
		if_double_move = false;
		double_checker_col = -1;
		double_checker_col = -1;
		beaten_checker_col = -1;
		beaten_checker_row = -1;	
		current_game_state = null;
		resetAvailableMoves();
	}
	
	public boolean isDoubleMove(){
		return if_double_move;
	}
	
	public boolean isCheckerBeaten(){
		return if_checker_beaten;
	}
	
	private boolean isFieldFree(int row, int col){
		return getChecker(row, col) == FieldState.EMPTY;
	}
	
	public GameState getCurrentGameState(){
		if(current_game_state == null) current_game_state = validateGameState();
		return current_game_state;
	}
	
	private boolean canMakeAnotherMove(int row, int col){
		
		boolean if_can = false;
		if(getChecker(row, col) == FieldState.WHITE) 
			if_can = canCheckerBeat(row, col, row-2, col-2, FieldState.BLACK) 
				|| canCheckerBeat(row, col, row-2, col+2, FieldState.BLACK);
		else if_can = canCheckerBeat(row, col, row+2, col-2, FieldState.WHITE) 
				|| canCheckerBeat(row, col, row+2, col+2, FieldState.WHITE);

		if(if_can) {
			if_double_move = true;
			double_checker_row = row;
			double_checker_col = col;
		}
		return if_can;
	}
	
	public boolean canCheckerBeat(int old_row, int old_col, int new_row, int new_col, FieldState opponent_checker){
		return (new_row  <= 7) && (new_row >= 0) && (new_col <= 7) && (new_col >= 0)
				&& getChecker((new_row + old_row) / 2, (new_col + old_col) / 2) == opponent_checker
				&& isFieldFree(new_row, new_col);		
	}
	
	private boolean canCheckerBeat(Move move, FieldState opponent_checker){
		return (move.new_row  <= 7) && (move.new_row >= 0) && (move.new_col <= 7) && (move.new_col >= 0)
				&& getChecker((move.new_row + move.old_row) / 2, (move.new_col + move.old_col) / 2) == opponent_checker
				&& isFieldFree(move.new_row, move.new_col);	
	}
	
	public boolean validateMove(Move move){
		if ((move.new_row > 7) || (move.new_row < 0) || (move.new_col > 7) || (move.new_col < 0)) return false;
		
		if (Math.abs(move.new_row - move.old_row) == 1 && Math.abs(move.new_col - move.old_col) == 1)
			return isFieldFree(move.new_row, move.new_col);
		else if(Math.abs(move.new_row - move.old_row) == 2 && Math.abs(move.new_col - move.old_col) == 2){
			FieldState opponent = getChecker(move.old_row, move.old_col) == FieldState.WHITE? FieldState.BLACK: FieldState.WHITE;
			return canCheckerBeat(move, opponent);
		}
		else return false;	
	}
	
	public GameState validateGameState(){
		
		resetAvailableMoves();
		boolean game_finished = false;
		if(white_number == 0) return GameState.BLACK_PLAYER_WON;
		else if(black_number == 0) return GameState.WHITE_PLAYER_WON;
		else{
			int [] white_black_last_row = getLastCheckerRows();
			if(white_black_last_row[1] >= white_black_last_row[0]){
				game_finished = true;
			}
			else{
				int white_moves_number = getAllWhiteAvailableMoves().size();
				int black_moves_number = getAllBlackAvailableMoves().size();
				if(white_moves_number == 0 || black_moves_number == 0) game_finished = true;
			}
		}
		
		if(game_finished){
			if(white_number == black_number) return GameState.TIE;
			else if(white_number > black_number) return GameState.WHITE_PLAYER_WON;
			else  return GameState.BLACK_PLAYER_WON;
		}
		return GameState.IN_PLAY;
	}
	
	private void resetAvailableMoves(){
		availableBlackMoves = null;
		availableWhiteMoves = null;
	}
	
	public List<Move> getAvailableWhiteMoves(){
		if(availableWhiteMoves != null && availableWhiteMoves.size() > 0) return availableWhiteMoves;
		else return getAllWhiteAvailableMoves();
	}
	
	public List<Move> getAvailableBlackMoves(){
		if(availableBlackMoves != null && availableBlackMoves.size() > 0) return availableBlackMoves;
		else return getAllBlackAvailableMoves();
	}
	
	private int []  getLastCheckerRows(){	
		int min_black_row = 7;
		int min_white_row = 0;	
		int [] rows = new int [2]; //white,black
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				if(getChecker(i, j) == FieldState.WHITE) if(i>min_white_row) min_white_row = i;		
				if(getChecker(i, j) == FieldState.BLACK) if(i<min_black_row) min_black_row = i;			
			}
		}
		rows[0] = min_white_row;
		rows[1] = min_black_row;
		return rows;
	}
	
	public List<Move> getAllBlackAvailableMoves(){
		List<Move> list = new ArrayList<Move>();
		if(isDoubleMove() && getChecker(double_checker_row, double_checker_col) == FieldState.BLACK){
			if(canCheckerBeat(double_checker_row, double_checker_col, double_checker_row+2, double_checker_col+2, FieldState.WHITE))
				list.add(new Move(double_checker_row, double_checker_col, double_checker_row+2, double_checker_col+2));
			if(canCheckerBeat(double_checker_row, double_checker_col, double_checker_row+2, double_checker_col-2, FieldState.WHITE))
				list.add(new Move(double_checker_row, double_checker_col, double_checker_row+2, double_checker_col-2));
		}
		else {
			for(int i = 0; i<8; i++){
				for(int j = 0; j<8; j++){
					if(getChecker(i, j) == FieldState.BLACK) list.addAll(getAvailableMovesForBlackOne(i, j));
				}
			}
		}		
		availableBlackMoves = list;
		return list;
	}
	
	public List<Move> getAllWhiteAvailableMoves(){
		List<Move> list = new ArrayList<Move>();
		if(isDoubleMove() && getChecker(double_checker_row, double_checker_col) == FieldState.WHITE){
			if(canCheckerBeat(double_checker_row, double_checker_col, double_checker_row-2, double_checker_col+2, FieldState.BLACK))
				list.add(new Move(double_checker_row, double_checker_col, double_checker_row-2, double_checker_col+2));
			if(canCheckerBeat(double_checker_row, double_checker_col, double_checker_row-2, double_checker_col-2, FieldState.BLACK))
				list.add(new Move(double_checker_row, double_checker_col, double_checker_row-2, double_checker_col-2));
		}
		else {
			for(int i = 0; i<8; i++){
				for(int j = 0; j<8; j++){
					if(getChecker(i, j) == FieldState.WHITE) list.addAll(getAvailableMovesForWhiteOne(i, j));
				}
			}
		}
		
		availableWhiteMoves = list;
		return list;
	}
	
	public List<Move> getAvailableMovesForWhiteOne(int row, int col){
		List<Move> list = new ArrayList<Move>();
		if((row-1 >= 0) && (col-1 >= 0) && isFieldFree(row-1, col-1)) list.add(new Move(row, col, row-1, col-1));
		if((row-1 >= 0) && (col+1 <= 7) && isFieldFree(row-1, col+1)) list.add(new Move(row, col, row-1, col+1));
		if(canCheckerBeat(row, col, row-2, col+2, FieldState.BLACK)) list.add(new Move(row, col, row-2, col+2));
		if(canCheckerBeat(row, col, row-2, col-2, FieldState.BLACK)) list.add(new Move(row, col, row-2, col-2));
		return list;
	}
	
	public List<Move> getAvailableMovesForBlackOne(int row, int col){
		List<Move> list = new ArrayList<Move>();
		if((row+1 <= 7) && (col-1 >= 0) && isFieldFree(row+1, col-1)) list.add(new Move(row, col, row+1, col-1));
		if((row+1 <= 7) && (col+1 <= 7) && isFieldFree(row+1, col+1)) list.add(new Move(row, col, row+1, col+1));
		if(canCheckerBeat(row, col, row+2, col-2, FieldState.WHITE)) list.add(new Move(row, col, row+2, col-2));
		if(canCheckerBeat(row, col, row+2, col+2, FieldState.WHITE)) list.add(new Move(row, col, row+2, col+2));
		return list;
	}
	
	public int getCheckersNumberScore(FieldState color){
		if(color == FieldState.WHITE) return white_number * COUNT_FACTOR; 
		else return black_number * COUNT_FACTOR; 
	}
	
	public int getGameStateScore(FieldState color){
		GameState state = validateGameState();
		if(color == FieldState.BLACK && state == GameState.BLACK_PLAYER_WON 
				|| color == FieldState.WHITE && state == GameState.WHITE_PLAYER_WON) return 10000;
		else if(state == GameState.TIE) return 5000;
		else return 0;
	}
	
	public int getCheckersBeatScore(FieldState color){
		
		int score = 0;
		if(if_checker_beaten) score += 300;
		
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				if(color == FieldState.WHITE && getChecker(i, j) == color){
					if(canCheckerBeat(i-2, j-2, i-1, j-1, FieldState.BLACK)){
						score += BEAT_POINTS;
						if((i+1 < 7) && (j+1 < 7) && !isFieldFree(i+1, j+1)) score += BACKUP_POINTS;
						else score -= BEAT_POINTS*1.5;
					}
					if(canCheckerBeat(i-2, j+2, i-1, j+1, FieldState.BLACK)){
						score += BEAT_POINTS;
						if((i+1 < 7) && (j-1 > 0) && !isFieldFree(i+1, j-1)) score += BACKUP_POINTS;
						else score -= BEAT_POINTS*1.5;
					}		
					
				else if(color == FieldState.BLACK && getChecker(i, j) == color)
					if(canCheckerBeat(i+2, j-2, i+1, j-1, FieldState.WHITE)){
						score += BEAT_POINTS;
						if((i-1 > 0) && (j+1 < 7) && !isFieldFree(i-1, j+1)) score += BACKUP_POINTS;	
						else score -= BEAT_POINTS*1.5;
					}
					if(canCheckerBeat(i+2, j+2, i+1, j+1, FieldState.WHITE)){
						score += BEAT_POINTS;
						if((i-1 > 0) && (j-1 > 0) && !isFieldFree(i-1, j-1)) score += BACKUP_POINTS;
						else score -= BEAT_POINTS*1.5;
					}		
				}
			}
		}
		return score;
	}
	
	public int getAreasScore(FieldState color){
		int score = 0;
		
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				if(getChecker(i, j) == color) score += countArea(i, j);
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
	
	public int getSectorScore(FieldState color){
		int score = 0;
		
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				if(getChecker(i, j) == color) {
					if(color == FieldState.WHITE){
						if(i<2) score += SECTOR_IV;
						else if(i<4) score += SECTOR_III;
						else if(i<6) score += SECTOR_II;	
						else score += SECTOR_I;
					}
					else if (color == FieldState.BLACK){					
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
