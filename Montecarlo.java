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
		ArrayList<Point> allPossibleMoves;

		Node(Node parent, Board board, Point point, int stoneType){
			this.point = point;
			this.stoneType = stoneType;
			this.parent = parent;
			this.board = new Board(board);
			playout = new Playout();
			wins = 0;
			games = 0;
			if (parent != null && point != null) 		//root node
				this.playout.makeMove(this.board, point, stoneType);

			allPossibleMoves = this.playout.getFreePoints(board, Board.getOppositeSide(stoneType));
		}
		Node addChild(Point p){
			Node child;
			if (children == null)
				children = new ArrayList<Node>();
			child = new Node(this, board, p, Board.getOppositeSide(stoneType));
			children.add(child);
			return child;
		}
		ArrayList<Node> getChildren(){
			return children;
		}
		void incWins(){
			wins++;
		}
		void incGames(){
			games++;
		}
		int getGames(){
			return games;
		}
		int getWins(){
			return wins;
		}
		Point getPoint(){
			return point;
		}
		int getStoneType(){  //TODO: Don't need this function
			return stoneType;
		}
		Board getBoard(){ //TODO: Don't need this function
			return board;
		}
		Node getParent(){
			return parent;
		}
		int StartPlayout(){
			if (playout == null){
				playout = new Playout();
			}
			return playout.playRandomGame(board, Board.getOppositeSide(stoneType)); 
		}
	}
	Node root;
	Montecarlo(Board board, int whoseTurn){
		root = new Node(null, board, null, whoseTurn);
	}
	void playOneSequence(){
		root.addChild(new Point(1,1));
		root.addChild(new Point(2,2));
		root.getChildren().get(0).addChild(new Point(3,3));
		Node node = SelectNode(root);
		//if (node != null)
		//	System.out.print(node.getGames());
		node = Expand(node);
		int winner = Simulation(node);
		BackPropagation(node, winner);

	}
	Node SelectNode(Node node){
		int i, n, w, t; 
		double value, bestValue = -1, c = 0.44;
		Node bestNode = null;
		int MAX_NODES = 5;
		ArrayList<Node> children;
		Node currentNode = node;

		for (children = currentNode.getChildren(); children != null; currentNode = bestNode){
			
			bestNode = null;
			bestValue = -1;
			t = 0;
			if (children.size() < currentNode.allPossibleMoves.size() && children.size() < MAX_NODES)
				break; ///!!!!
			/*if (children == null)
				System.out.printf("children == null");
			else
				System.out.printf("children != null");
				*/
			
			for (Node child: children){
				t += child.getGames()      +1;
			}
			

			for (Node child: children){
				n = child.getGames()      +1;
				w = child.getWins()      +1;
				//if (n==0)
				//	n = 1;
				value = w/n + c * Math.sqrt(Math.log(t)/n);
				
				if (value > bestValue){
					bestValue = value;
					bestNode = child;
				}
			}
			children = currentNode.getChildren();
			
		}
		return currentNode;
		
	}
	
	Node Expand(Node papa){
		ArrayList<Node> papasChildren = papa.getChildren();

		for (Point move: papa.allPossibleMoves){
			for (Node child: papasChildren){
				if (papasChildren == null && (move.i != child.getPoint().i || move.j != child.getPoint().j)){
					return papa.addChild(move);
				}
			}	
		}
		return null;
	}
	int Simulation(Node node){
		return node.StartPlayout();
	}
	void BackPropagation(Node startNode, int winner){
		Node currentNode = startNode;
		do{
			if (currentNode.getStoneType() == winner)
				currentNode.incWins();
			currentNode.incGames();
			currentNode = currentNode.getParent();
		} while(currentNode != null);
		
	}

}