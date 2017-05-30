package Controller;
import javax.swing.JFrame;
import Model.Board;
import Model.Board.FieldState;
import Model.CheckerColor;
import Model.GameState;
import Model.Heuristic;
import Model.Move;
import Model.Player;
import Model.Player.PlayerListener;
import Model.PlayerType;
import View.BoardView;
import View.BoardView.BoardViewListener;
import View.CheckerView;


public class Game {
	static BoardView boardView;
	static JFrame frame;
	static GameState gameState;
	static Board gameLogic;
	private static Player current_player;
	private static Player second_player;
	static boolean players_swap = true;
	static Move taken_move;
	
	public static void main(String[] args){
	     
		initializePlayers();
		initializePlayersListeners();
		initializeGameLogic();
		initializeBoard();
		initializeViewListener();  	 
	    runGame();
		
	}
	
	private static void initializePlayers() {

		//current_player = new Player(FieldState.WHITE, PlayerType.ALFA_BETA, Heuristic.BEAT_ABILITY, null);
		//second_player = new Player(FieldState.BLACK, PlayerType.MIN_MAX, Heuristic.BEAT_ABILITY, null);
		
		current_player = new Player(FieldState.WHITE, PlayerType.HUMAN, Heuristic.BEAT_ABILITY, null);
		second_player = new Player(FieldState.BLACK, PlayerType.ALFA_BETA, Heuristic.BEAT_AB_AREA, null);
		
	}
	
	private static void initializePlayersListeners(){
		
		if(current_player.getPlayerType() != PlayerType.HUMAN) current_player.setPlayerListener(new PlayerListener() {
			
			@Override
			public void makeMove(Move move) {
				makeTheMove(move);
			}
		});
		
		if(second_player.getPlayerType() != PlayerType.HUMAN) second_player.setPlayerListener(new PlayerListener() {
			
			@Override
			public void makeMove(Move move) {
				makeTheMove(move);
			}
		});
		
	}
	
	private static void makeTheMove(Move move){
		//System.out.println("AI MADE MOVE");
		gameLogic.moveChecker(move);
		
		
		boardView.moveChecker(move.old_row+1, move.old_col+1, move.new_row+1, move.new_col+1);
		synchronized(gameLogic){
			try {
				gameLogic.wait(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		taken_move = move;
		current_player.if_made_move = true;	
		processMove();
	}
	
	private static void initializeBoard() {
		
		frame = new JFrame("U CAN RUN, BUT U CAN'T HIDE BITCH");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		boardView = new BoardView();
		
		
		for(int i = 1; i<=3; i++){
			for(int j = i%2==0? 1 : 2, k = i%2==0? 2 : 1; j<=8; j+=2, k+=2){
				 boardView.add(new CheckerView(CheckerColor.BLACK), i, j);
				 boardView.add(new CheckerView(CheckerColor.WHITE), i+5, k);
			}
		}
			 
		 frame.setContentPane(boardView);
		 frame.pack();
		 frame.setResizable(false);
	     changePlayerInView(current_player.getPlayerColor());
	}
	
	private static void changePlayerInView(FieldState color){
		if(color == FieldState.BLACK) boardView.changePlayer(CheckerColor.BLACK);
		else boardView.changePlayer(CheckerColor.WHITE);
	}
	
	private static void initializeViewListener(){
		boardView.setBoardViewListener(new BoardViewListener() {
			
			@Override
			public boolean validateMove(int old_row, int old_col, int new_row, int new_col, CheckerColor color) {
				System.out.println("HUMAN: VALIDATE MOVE");
				Move move = new Move(old_row-1, old_col-1, new_row-1, new_col-1);
				if(!gameLogic.validateMove(move)) return false;
				gameLogic.moveChecker(move);
				taken_move = move;
				current_player.if_made_move = true;				
				return true;
			}

			@Override
			public void moveMade() {
				System.out.println("HUMAN MADE MOVE");
				processMove();				
			}
		});
	}
	

	
	private static void initializeGameLogic(){
		gameLogic = new Board();
	}
	
		
	private static void processMove(){
		
		if(gameState == GameState.IN_PLAY){
			if(current_player.if_made_move){
				if(gameLogic.isCheckerBeaten()){
					//System.out.println("CHECKER BEATEN");
					boardView.removeChecker(gameLogic.beaten_checker_row+1, gameLogic.beaten_checker_col+1);	
					boardView.repaintManually();
					synchronized(gameLogic){
						try {
							gameLogic.wait(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
									
					if(gameLogic.isDoubleMove()) {
						players_swap = false;
						//System.out.println("CAN MAKE ANOTHER MOVE");
						if(current_player.getPlayerType() == PlayerType.HUMAN) boardView.forceCheckerMove(taken_move.new_row+1, taken_move.new_col+1);
					}
					else boardView.resetForcedMove();
				}				
				current_player.if_made_move = false;
				
				//System.out.println("AREA: " + gameLogic.getAreasScore(current_player.getPlayerColor()));
				//System.out.println("BEAT: " + gameLogic.getCheckersBeatScore(current_player.getPlayerColor()));
				//System.out.println("NUMBER: " + gameLogic.getCheckersNumberScore(current_player.getPlayerColor()));
				//System.out.println("SECTOR: " + gameLogic.getSectorScore(current_player.getPlayerColor()));
				
				if(players_swap){
					//System.out.println("CHANGE PLAYER");
					Player c_p = current_player;
					current_player = second_player;
					second_player = c_p;	
					if(current_player.getPlayerType() == PlayerType.HUMAN) changePlayerInView(current_player.getPlayerColor());
					else changePlayerInView(null);
					
				}
			}
		}	
		gameState = gameLogic.validateGameState();
		if(gameState != GameState.IN_PLAY) {
			System.out.println("KONIEC GRY - " + gameState);
			System.out.println(current_player.getPlayerColor() +"  " + current_player.second  +"  " + current_player.time/(current_player.move * 1000000000.0));
			System.out.println(second_player.getPlayerColor() +"  " +  second_player.second  +"  " + second_player.time/(second_player.move * 1000000000.0));
			System.out.println(current_player.getPlayerColor() +"  " + current_player.type  +"  " + current_player.counter+"  " + current_player.counter*1.0/current_player.move);
			System.out.println(second_player.getPlayerColor() +"  " +  second_player.type  +"  " + second_player.counter +"  " +  second_player.counter*1.0/second_player.move);
		}
		else{
			players_swap = true;	
			current_player.yourTurn(gameLogic);
		}	
	}
	
	private static void runGame() {
		gameState = GameState.IN_PLAY;
		frame.setVisible(true);  
		if(current_player.getPlayerType() != PlayerType.HUMAN) current_player.yourTurn(gameLogic);
	}

}
