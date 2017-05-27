package Model;

import java.util.ArrayList;

public class Node {
	
	private Node parent;
	private Move move;
	private int depth;
	private int value;
	private ArrayList<Node> children;
	
	public Node(Move move, int depth, int value, Node parent){
		this.move = move;
		this.depth = depth;
		this.parent = parent;
		this.value = value;
		children = new ArrayList<Node>();
	}
	
	public void addChild(Node node){
		children.add(node);
	}
	
	public int getScore(){
		return value;
	}
	
	public Move getMove(){
		return move;
	}
	
}
