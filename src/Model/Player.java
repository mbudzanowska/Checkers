package Model;

public class Player {

	public CheckerColor color;
	public PlayerType type;
	public boolean if_made_move = false;
	private PlayerListener playerListener;
	private static Move move;
	
	public Player(CheckerColor color, PlayerType type){
		this.color = color;
		this.type = type;
	}
	
	public void setPlayerListener(PlayerListener playerListener){
		this.playerListener = playerListener;
	}
	
	public interface PlayerListener{
		
		public void makeMove(Move move);
	}
	
	public void yourTurn(Board board){
		
		System.out.println("ELO");
		//playerListener.makeMove(null);
		
		// kejsy srejsy dla algorytmów
		
		
	}
	
	private Move min_max(Board board, Node n, CheckerColor player_color){
		
		//for(int i = 0; i<)
		return null;
		
	}
	
}
