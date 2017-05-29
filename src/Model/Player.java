package Model;
import java.util.List;
import Model.Board.FieldState;


public class Player {

	private FieldState player_color;
	private Heuristic heuristic;
	private PlayerType type;
	public boolean if_made_move = false;
	private PlayerListener playerListener;
	private final static int MAX_DEPTH = 6;
	private Move chosen_move;
	private int val;
	
	public Player(FieldState player_color, PlayerType type, Heuristic heuristic){
		this.player_color = player_color;
		this.type = type;	
		this.heuristic = heuristic;
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
			System.out.println("MIN MAX PROCESSING");
			min_max(copy, null, player_color, MAX_DEPTH, 0);
			System.out.println(chosen_move +"       " + val + "    " + player_color);
			playerListener.makeMove(chosen_move);
			break;
			
		}
		case ALFA_BETA :{
			break;
		}
		}
		
	}
	
	 private double min_max(Board board, Move move, FieldState current_player_color, int depth, int score_sum){
		 
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
					 if(depth == MAX_DEPTH) {
						 chosen_move = child;
						 val = (int) bestValue;
						 System.out.println("MAX  "+current_player_color+ " best: " + child + " val: "+val +" depth:" + depth);
					 }
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
					 
					 if(depth == MAX_DEPTH) {
						 chosen_move = child;
						 val = (int) bestValue;		
						 System.out.println("MIN  "+current_player_color+ " best: " + child + " val: "+val +" depth:" + depth);
					 }
				 }
			 }
			 return bestValue;
		 }
	 }
	 
 private double alpha_beta(Board board, Move move, FieldState current_player_color, int depth, int score_sum){
		 
		 int score = depth == MAX_DEPTH? score_sum : score_sum + getNodeScore(board, move, current_player_color); 
		 if(board.getCurrentGameState() != GameState.IN_PLAY || depth == 0) {
			 System.out.println(score);
			 return score;
		 }
		 
		 double bestValue;
		 List <Move> possible_moves = getMoves(current_player_color, board);
		 FieldState opponent = board.isDoubleMove()? current_player_color : getOpponent(current_player_color);
		 
		 if(current_player_color != getPlayerColor()) { // MAX
			 bestValue = Double.NEGATIVE_INFINITY;
			 for(Move child: possible_moves){
				 double value = min_max(new Board(board), child, opponent, depth-1, score);
				 if(value > bestValue){
					 bestValue = value;
					 if(depth == MAX_DEPTH) {
						 chosen_move = child;
						 val = (int) bestValue;
						 System.out.println(current_player_color+ "   best: " + child);
					 }
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
					 if(depth == MAX_DEPTH) {
						 chosen_move = child;
						 val = (int) bestValue;
						 System.out.println(current_player_color+ "   best: " + child);
					 }
				 }
			 }
			 return bestValue;
		 }
	 }
	
	private int getScore(Board board, FieldState color){		
		int score = 0;	
		switch(heuristic){
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
