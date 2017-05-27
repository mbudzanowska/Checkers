package Controller;

import java.awt.EventQueue;

import javax.swing.JFrame;

import Model.Board;
import Model.CheckerColor;
import Model.GameState;
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
	static Player current_player;
	static Player second_player;
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

		current_player = new Player(CheckerColor.WHITE, PlayerType.HUMAN);
		second_player = new Player(CheckerColor.BLACK, PlayerType.MIN_MAX);
		
	}
	
	private static void initializePlayersListeners(){
		
		if(current_player.type != PlayerType.HUMAN) current_player.setPlayerListener(new PlayerListener() {
			
			@Override
			public void makeMove(Move move) {
				System.out.println("GOT HERE LOL");
				gameLogic.moveChecker(move.old_row, move.old_col, move.new_row, move.new_col);
				boardView.moveChecker(move.old_row+1, move.old_col+1, move.new_row+1, move.new_col+1);
				taken_move = move;
				current_player.if_made_move = true;	
				processMove();
			}
		});
		
		if(second_player.type != PlayerType.HUMAN) second_player.setPlayerListener(new PlayerListener() {
			
			@Override
			public void makeMove(Move move) {
				System.out.println("GOT HERE LOL");
				gameLogic.moveChecker(move.old_row, move.old_col, move.new_row, move.new_col);
				boardView.moveChecker(move.old_row+1, move.old_col+1, move.new_row+1, move.new_col+1);
				taken_move = move;
				current_player.if_made_move = true;	
				processMove();
			}
		});
		
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
	     boardView.changePlayer(current_player.color);
	}
	
	private static void initializeViewListener(){
		boardView.setBoardViewListener(new BoardViewListener() {
			
			@Override
			public boolean validateMove(int old_row, int old_col, int new_row, int new_col, CheckerColor color) {
				System.out.println("VALIDATE MOVE CALLED");
				old_row--;
				old_col--;
				new_row--;
				new_col--;
				
				if(!gameLogic.validateMove(old_row, old_col, new_row, new_col)) return false;
				gameLogic.moveChecker(old_row, old_col, new_row, new_col);
				taken_move = new Move(old_row, old_col, new_row, new_col);
				current_player.if_made_move = true;				
				return true;
			}

			@Override
			public void moveMade() {
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
					System.out.println("CHECKER BEATEN");
					boardView.removeChecker(gameLogic.beaten_checker_row+1, gameLogic.beaten_checker_col+1);
					
					if(gameLogic.canMakeAnotherMove(taken_move.new_row, taken_move.new_col, current_player.color)) {
						players_swap = false;
						System.out.println("CAN MAKE ANOTHER MOVE");
						if(current_player.type == PlayerType.HUMAN) boardView.forceCheckerMove(taken_move.new_row+1, taken_move.new_col+1);
					}
				}
				
				current_player.if_made_move = false;
				
				System.out.println("AREA: " + gameLogic.getAreasScore(current_player.color));
				System.out.println("BEAT: " + gameLogic.getCheckersBeatScore(current_player.color));
				System.out.println("NUMBER: " + gameLogic.getCheckersNumberScore(current_player.color));
				System.out.println("SECTOR: " + gameLogic.getSectorScore(current_player.color));
				
				if(players_swap){
					System.out.println("CHANGE PLAYER");
					Player c_p = current_player;
					current_player = second_player;
					second_player = c_p;	
					if(current_player.type == PlayerType.HUMAN) boardView.changePlayer(current_player.color);
					else boardView.changePlayer(null);
					
				}
			}
		}	
		gameState = gameLogic.validateGameState();
		if(gameState != GameState.IN_PLAY) System.out.println("KONIEC GRYYYYYYYYYY");
		players_swap = true;
		
		current_player.yourTurn(new Board(gameLogic));
	}
	
	private static void runGame() {
		gameState = GameState.IN_PLAY;
		frame.setVisible(true);  
	}

}
