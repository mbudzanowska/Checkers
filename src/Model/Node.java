package Model;

import java.util.ArrayList;

public class Node {
	
	public Node parent;
	private Move move;
	private int value;
	private ArrayList<Node> children;
	
	public Node(Move move, int value, Node parent){
		this.move = move;
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
	
	public String toString(){
		return ""+value+" , " + children.size();
	}
	
}
