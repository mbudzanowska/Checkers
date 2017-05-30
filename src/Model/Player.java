package Model;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import Model.Board.FieldState;


public class Player {

	private FieldState player_color;
	private Heuristic board_heuristic;
	public Heuristic second;
	public PlayerType type;
	public boolean if_made_move = false;
	private PlayerListener playerListener;
	private final static int MAX_DEPTH = 5;
	private Move chosen_move;
	public int move = 0;
	public long time;
	
	public long counter = 0;
	
	public Player(FieldState player_color, PlayerType type, Heuristic board_heuristic, Heuristic second){
		this.player_color = player_color;
		this.type = type;	
		this.board_heuristic = board_heuristic;
		this.second = second;
	}
	
	public void setPlayerListener(PlayerListener playerListener){
		this.playerListener = playerListener;
	}
	
	public FieldState getPlayerColor(){
		return player_color;
	}
	
	private FieldState getOpponent(FieldState player_color){
		return player_color == FieldState.WHITE? FieldState.BLACK : FieldState.WHITE;
	}
	
	public PlayerType getPlayerType(){
		return type;
	}
	
	public interface PlayerListener{	
		public void makeMove(Move move);
	}
	
	public void yourTurn(Board board){ 	
		Board copy = new Board(board);
		chosen_move = null;
		
		switch (type) {
		case HUMAN:{
			break;
		}
		case MIN_MAX :{
			//System.out.println("MIN MAX PROCESSING");
			move ++;
			//long startTime=System.nanoTime();
			min_max(copy, null, player_color, MAX_DEPTH, 0);
			//long elapsed=System.nanoTime() - startTime;
			//System.out.println("Czas wykonania:"+elapsed/1000000000.0+"  "+move +" MAX");
			//System.out.println(chosen_move +"       " + val + "    " + player_color);
			//time +=elapsed;
			playerListener.makeMove(chosen_move);
			
			break;
			
		}
		case ALFA_BETA :{
			move ++;
			long startTime=System.nanoTime();
			System.out.println("ALPHA BETA PROCESSING");
			alpha_beta(copy, null, player_color, MAX_DEPTH, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			long elapsed=System.nanoTime() - startTime;
			time +=elapsed;
			System.out.println("Czas wykonania:"+elapsed/1000000000.0+"  "+move +" ALFA");
			playerListener.makeMove(chosen_move);
			break;
		}
		}
		
	}
	
	 private double min_max(Board board, Move move, FieldState current_player_color, int depth, int score_sum){
		 
		 counter ++;
		 
		 int score = depth == MAX_DEPTH? score_sum : score_sum + getNodeScore(board, move, current_player_color); 
		 if(board.getCurrentGameState() != GameState.IN_PLAY || depth == 0) {
			 return score;
		 }
		 
		 double bestValue;
		 List <Move> possible_moves = getMoves(current_player_color, board);
		 FieldState opponent = board.isDoubleMove()? current_player_color : getOpponent(current_player_color);
		 
		 if(current_player_color == getPlayerColor()) { // MAX
			 bestValue = Double.NEGATIVE_INFINITY;
			 for(Move child: possible_moves){
				 double value = min_max(new Board(board), child, opponent, depth-1, score);
				 if(value > bestValue){			
					 bestValue = value;
					 if(depth == MAX_DEPTH)  chosen_move = child;
						// System.out.println("MAX  "+current_player_color+ " best: " + child + " val: "+val +" depth:" + depth);					 
				 }
			 }	
			 return bestValue;
		 }
		 else{ //MIN
			 bestValue = Double.POSITIVE_INFINITY;
			 for(Move child: possible_moves){
				 double value = min_max(new Board(board), child, opponent, depth-1, score);
				 if(value < bestValue){
					 bestValue = value;				 
					 if(depth == MAX_DEPTH)  chosen_move = child;	
						 //System.out.println("MIN  "+current_player_color+ " best: " + child + " val: "+val +" depth:" + depth); 
				 }
			 }
			 return bestValue;
		 }
	 }
	 
 private double alpha_beta(Board board, Move move, FieldState current_player_color, int depth, int score_sum, double alpha, double beta){
	 
	 	counter ++;
		 
		 int score = depth == MAX_DEPTH? score_sum : score_sum + getNodeScore(board, move, current_player_color); 
		 if(board.getCurrentGameState() != GameState.IN_PLAY || depth == 0) {
			 return score;
		 }
		 
		 double bestValue;
		 List <Move> possible_moves = getMoves(current_player_color, board);
		 if(second != null) Collections.sort(possible_moves, new MoveComparator());
		 
		 FieldState opponent = board.isDoubleMove()? current_player_color : getOpponent(current_player_color);
		 
		 if(current_player_color == getPlayerColor()) { // MAX
			 bestValue = Double.NEGATIVE_INFINITY;
			 for(Move child: possible_moves){
				 double value = alpha_beta(new Board(board), child, opponent, depth-1, score, alpha, beta);
				 if(value > bestValue){			
					 bestValue = value;
					 if(depth == MAX_DEPTH)  chosen_move = child;
						// System.out.println("alfa  "+current_player_color+ " best: " + child + " val: "+val +" depth:" + depth);					 
				 }
				 alpha = Double.max(alpha, bestValue);
				 if(beta <= alpha) break;
			 }	
			 return bestValue;
		 }
		 else{ //MIN
			 bestValue = Double.POSITIVE_INFINITY;
			 for(Move child: possible_moves){
				 double value = alpha_beta(new Board(board), child, opponent, depth-1, score, alpha, beta);
				 if(value < bestValue){
					 bestValue = value;				 
					 if(depth == MAX_DEPTH)  chosen_move = child;	
						 //System.out.println("alfa  "+current_player_color+ " best: " + child + " val: "+val +" depth:" + depth); 
				 }
				 beta = Double.min(beta, bestValue);
				 if(beta <= alpha) break;
			 }
			 return bestValue;
		 }
	 }
	
	private int getScore(Board board, FieldState c){		
		
		FieldState color = c == FieldState.WHITE? FieldState.BLACK : FieldState.WHITE;		
		int score = 0;	
		switch(board_heuristic){
		case AREA: {
			score = board.getAreasScore(color);
			break;
		}
		case SECTOR: {
			score = board.getSectorScore(color);
			break;
		}
		case BEAT_ABILITY: {
			score = board.getCheckersBeatScore(color);
			break;
		}
		case CHECKERS_NUMBER: {
			score = board.getCheckersNumberScore(color);
			break;
		}
		case BEAT_AB_AREA:{
			score = board.getCheckersBeatScore(color) + board.getAreasScore(color);
			break;
		}			
		case CH_NUMBER_AREA: {
			score = board.getCheckersNumberScore(color) + board.getAreasScore(color);
			break;
		}		
		case CH_NUMBER_BEAT_AB: {
			score = board.getCheckersNumberScore(color) +  board.getCheckersBeatScore(color);
			break;
		}
		}
		return score + board.getGameStateScore(color); 		
	}
	
	private int getNodeScore(Board board, Move move, FieldState current_player){
		board.moveChecker(move);
		return current_player == player_color? -getScore(board,current_player) : getScore(board,current_player);
	}
	
	private List<Move> getMoves(FieldState current_player, Board board){
		if(current_player == FieldState.WHITE) return board.getAllWhiteAvailableMoves();
		else return board.getAllBlackAvailableMoves();
	}
	
}
