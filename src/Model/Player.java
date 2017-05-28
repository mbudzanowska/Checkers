package Model;

import java.util.ArrayList;
import java.util.List;
import Model.Board.FieldState;


public class Player {

	private FieldState player_color;
	public PlayerType type;
	public boolean if_made_move = false;
	private PlayerListener playerListener;
	//private static Move move;
	private final static int MAX_DEPTH = 4;
	private static List<Node> leaves;
	
	public Player(FieldState color, PlayerType type){
		player_color = color;
		this.type = type;
	}
	
	public void setPlayerListener(PlayerListener playerListener){
		this.playerListener = playerListener;
	}
	
	public interface PlayerListener{
		
		public void makeMove(Move move);
	}
	
	public FieldState getPlayerColor(){
		return player_color;
	}
	
	public void yourTurn(Board board){
		
		System.out.println("ELO");
		leaves = new ArrayList<Node>();
		
		switch (type) {
		case HUMAN:{
			break;
		}
		case MIN_MAX :{
			//min_max(board,null,player_color,-1,null);
			int best_score = - 10000;
			Node best_node = null;
			for(int i = 0; i<leaves.size(); i++){
				if(leaves.get(i).getScore()>best_score) {
					best_score = leaves.get(i).getScore();
					best_node = leaves.get(i);
				}
			}
			playerListener.makeMove(best_node.getMove());
			break;
		}
		case ALFA_BETA :{
			break;
		}
		}
		//playerListener.makeMove(null);
		
		// kejsy srejsy dla algorytmów
		
		
	}
	
	/* private static Node min_max(Board board, Move move, CheckerColor player_color, int depth, Node parent){
		
			depth += 1;
			Node node = null;
			GameState state = null;
			if(depth == 0){
				node = new Node(null, 0, 0, null);
			}
			else{
				board.moveChecker(move.old_row, move.old_col, move.new_row, move.new_col);
				int score = board.getAreasScore(player_color) + board.getCheckersBeatScore(player_color) 
					+ board.getCheckersNumberScore(player_color) + board.getSectorScore(player_color) ;
				score = p_color == player_color ? score+parent.getScore() : -score + parent.getScore();	
				state = board.validateGameState();
				if(state != GameState.IN_PLAY){
					switch (state) {
					case BLACK_PLAYER_WON:{
						if(player_color == CheckerColor.BLACK) score += 1000;
						else score -= 1000;
						break;
					}
					case WHITE_PLAYER_WON:{
						if(player_color == CheckerColor.WHITE) score += 1000;
						else score -= 1000;
						break;
					}
					case TIE:{
						score += 500;
						break;
					}
					case IN_PLAY:
						break;
					default:
						break;
					}
				}
				node = new Node(move, depth, score, parent);
				if(state != GameState.IN_PLAY) leaves.add(node);
			}
			
			if(depth <= MAX_DEPTH && state != GameState.IN_PLAY){
				System.out.println(depth);
				if(player_color == CheckerColor.WHITE){ 
					if(board.isDoubleMove()){ // jeszcze raz ruch tego samego gracza
						if(board.twoPositionsMoveAbility(move.new_row-2, move.new_col+2, move.new_row-1, move.new_col+1, CheckerColor.BLACK))
							node.addChild(min_max(new Board(board), new Move(move.new_row, move.new_col, move.new_row-2, move.new_col+2),CheckerColor.WHITE, depth ++, node));
						if(board.twoPositionsMoveAbility(move.new_row-2, move.new_col-2, move.new_row-1, move.new_col-1, CheckerColor.BLACK))
							node.addChild(min_max(new Board(board), new Move(move.new_row, move.new_col, move.new_row-2, move.new_col-2),CheckerColor.WHITE, depth ++, node));
					}
					else{ // ruch przeciwnika
						for(int i = 0; i<7; i++){
							for(int j = 0; j<7; j++){
								if(board.isOccupied(i, j) && board.getChecker(i, j).color == CheckerColor.BLACK){
									if(board.onePositionMoveAbility(i+1, j+1)) 
										node.addChild(min_max(new Board(board), new Move(i,j,i+1,j+1), CheckerColor.BLACK, depth, node));
									if(board.onePositionMoveAbility(i+1, j-1))
										node.addChild(min_max(new Board(board), new Move(i,j,i+1,j-1), CheckerColor.BLACK, depth, node));
									if(board.twoPositionsMoveAbility(i+2, j+2, i+1, j+1, CheckerColor.WHITE))
										node.addChild(min_max(new Board(board), new Move(i, j, i+2, j+2),CheckerColor.BLACK, depth, node));
									if(board.twoPositionsMoveAbility(i+2, j-2, i+1, j-1, CheckerColor.WHITE))
										node.addChild(min_max(new Board(board), new Move(i, j, i+2, j-2),CheckerColor.BLACK, depth, node));
								}
							}
						}
					}
				}
				else{
					if(board.isDoubleMove()){
						if(board.twoPositionsMoveAbility(move.new_row+2, move.new_col+2, move.new_row+1, move.new_col+1, CheckerColor.WHITE))
							node.addChild(min_max(new Board(board), new Move(move.new_row, move.new_col, move.new_row+2, move.new_col+2),CheckerColor.BLACK, depth ++, node));
						if(board.twoPositionsMoveAbility(move.new_row+2, move.new_col-2, move.new_row+1, move.new_col-1, CheckerColor.WHITE))
							node.addChild(min_max(new Board(board), new Move(move.new_row, move.new_col, move.new_row+2, move.new_col-2),CheckerColor.BLACK, depth ++, node));
					}
					else{
						for(int i = 0; i<7; i++){
							for(int j = 0; j<7; j++){
								if(board.isOccupied(i, j) && board.getChecker(i, j).color == CheckerColor.WHITE){
									if(board.onePositionMoveAbility(i-1, j+1)) 
										node.addChild(min_max(new Board(board), new Move(i,j,i-1,j+1), CheckerColor.WHITE, depth ++, node));
									if(board.onePositionMoveAbility(i-1, j-1))
										node.addChild(min_max(new Board(board), new Move(i,j,i-1,j-1), CheckerColor.WHITE, depth ++, node));
									if(board.twoPositionsMoveAbility(i-2, j+2, i-1, j+1, CheckerColor.BLACK))
										node.addChild(min_max(new Board(board), new Move(i, j, i-2, j+2),CheckerColor.WHITE, depth ++, node));
									if(board.twoPositionsMoveAbility(i-2, j-2, i-1, j-1, CheckerColor.BLACK))
										node.addChild(min_max(new Board(board), new Move(i, j, i-2, j-2),CheckerColor.WHITE, depth ++, node));
								}
							}
						}
					}
				}
			}
		if(depth == MAX_DEPTH) leaves.add(node);
		return node;			
	} */
	
}
