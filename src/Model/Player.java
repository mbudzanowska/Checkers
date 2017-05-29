package Model;

import java.util.ArrayList;
import java.util.List;
import Model.Board.FieldState;


public class Player {

	private FieldState player_color;
	public PlayerType type;
	public boolean if_made_move = false;
	private PlayerListener playerListener;
	private final static int MAX_DEPTH = 6;
	private static List<Node> leaves;
	
	public Player(FieldState player_color,FieldState opponent_color, PlayerType type){
		this.player_color = player_color;
		this.type = type;	
		leaves = new ArrayList<Node>();
	}
	
	public void setPlayerListener(PlayerListener playerListener){
		this.playerListener = playerListener;
	}
	
	public interface PlayerListener{
		
		public void makeMove(Move move);
	}
	
	public void yourTurn(Board board){ 
		
		Board copy = new Board(board);
		Move move = null;
		switch (type) {
		case HUMAN:{
			break;
		}
		case MIN_MAX :{
			System.out.println("MIN MAX PROCESSING");
			move = useMinMax(copy, player_color);
			playerListener.makeMove(move);
			break;
			
		}
		case ALFA_BETA :{
			break;
		}
		}
		
	}
	
	private static Move useMinMax(Board  board, FieldState player_color){	
		leaves.clear();
		min_max(board, null, player_color, player_color, 0, null);
		int best_score = -10000;
		Node best_node = null;
		
		if(leaves.size() == 0) System.out.println("Krzycz trybson");
		
		for(int i = 0; i<leaves.size(); i++){
			if(leaves.get(i).getScore()>best_score) {
				best_score = leaves.get(i).getScore();
				best_node = leaves.get(i);
			}
		}
		
		System.out.println(best_score);
		System.out.println(best_node.getMove());
		
		while(best_node.parent != null && best_node.parent.parent != null) best_node = best_node.parent;
		return best_node.getMove();
		
	}
	
	 private static Node min_max(Board board, Move move, FieldState player_color, FieldState current_player_color, int depth, Node parent){
			 
		 Node node;
		 if(depth != 0){
			 board.moveChecker(move);
			 // tu ew dodaæ która heurystyka liczenia
			 int score = board.getAreasScore(current_player_color) + board.getCheckersBeatScore(current_player_color) 
				+ board.getCheckersNumberScore(current_player_color) + board.getSectorScore(current_player_color) 
				+ board.getGameStateScore(current_player_color); 
			 score = current_player_color == player_color? score + parent.getScore() : -score+parent.getScore();
			 node = new Node(move, score, parent);
		 }
		 else {
			 node = new Node(null, 0, null);
		 }
		 GameState game_state = board.getCurrentGameState();
		
		 if(game_state != GameState.IN_PLAY || depth == MAX_DEPTH) leaves.add(node);
		 else {
			 List <Move> possible_moves = null;
			 FieldState opponent;
			 if(current_player_color == FieldState.WHITE)  {
				 possible_moves = board.getAllWhiteAvailableMoves();
				 opponent = FieldState.BLACK;
			 }
			 else {
				 possible_moves = board.getAllBlackAvailableMoves();
				 opponent = FieldState.WHITE;
			 }			 
			 opponent = board.isDoubleMove() || depth == 0? current_player_color : opponent;
			 
			 for(int i = 0; i<possible_moves.size(); i++){
				 node.addChild(min_max(new Board(board), possible_moves.get(i), player_color,  opponent, depth+1, node));
			 }			
		 }		 
		//depth nieparzyste ->ruch gracza, parzyste -> ruch przeciwnika(NIE BO PODWOJNY RUCH PATRZYMY NA KOLOR GRACZA)
		return node;			
	}

	public FieldState getPlayerColor() {
		return player_color;
	} 
	
}
