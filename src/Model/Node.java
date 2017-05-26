package Model;

import java.util.ArrayList;

public class Node {

	private Move move;
	private int depth;
	private int value;
	private ArrayList<Node> children;
	
	public Node(Move move, int depth, int value){
		this.move = move;
		this.depth = depth;
		children = new ArrayList<Node>();
	}
	
}
