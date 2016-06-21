import java.util.*;
import java.io.*;


public class Montecarlo{
	private class Node{
		Node parent;
		ArrayList<Node> children = null;
		Board board;
		int stoneType;
		Point point;
		Playout playout;
		int wins;
		int games;

		Node(Node parent, Board board, Point point, int stoneType){
			this.point = point;
			this.stoneType = stoneType;
			this.parent = parent;
			this.board = new Board(board);
			playout = new Playout();
			wins = 0;
			games = 0;

			this.playout.makeMove(board, point, stoneType);
		}
		void addChild(Point p){
			if (children == null)
				new ArrayList<Node>();

			children.add(new Node(this, board, p, Board.getOppositeSide(stoneType)));
		}
		Node getChildren(){
			if (children != null)
				return children;
			return null;
		}

		void incWins(){
			wins++;
		}
		void incGames(){
			games++;
		}
	}
	Node root;
	Montecarlo(Board board, int whoseTurn){
		root = new Node(null, board, null, whoseTurn);
	}
	void playOneSquence(){

	}
	Node SelectNode(Node node){
		int i, n, w, t; 
		double value, bestValue = -1, c = 0.44;
		Node bestNode = null;
		int maxNodes = 5;
		
		if (node.children_num < node.board.getFreePositionsNumber() && node.children_num < maxNodes)
			return null;

		for (i = 0, t = 0; node.getChildNode(i) != null; i++){
			t += node.getChildNode(i).getGames();
		}
		

		for (i = 0; node.getChildNode(i) != null; i++){
			n = node.getChildNode(i).getGames();
			w = node.getChildNode(i).getWins();
			//if (n==0)
			//	n = 1;
			value = w/n + c * Math.sqrt(Math.log(t)/n);
			if (value > best_value){
				best_value = value;
				best_node = node.getChildNode(i);
			}
		}
		return best_node;
	}
}