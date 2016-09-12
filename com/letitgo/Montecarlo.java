package com.letitgo;
import java.util.*;
import java.io.*;
import com.letitgo.heuristics.*;

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
		//ArrayList<Point> allPossibleMoves;

		Node(Node parent, Board board, Point point, int stoneType){
			this.point = point;
			this.stoneType = stoneType;
			this.parent = parent;
			this.board = new Board(board);
			//playout = new Playout();
			wins = 0;
			games = 0;
			if (parent != null && point != null){ 		//root node
				this.board.makeMove(point, stoneType);
			}
			/*
			allPossibleMoves = this.playout.getFreePoints(this.board, Board.getOppositeSide(stoneType));
			if (allPossibleMoves.size() == 0){
				//board.printBoard();
				System.out.printf("\nallPossibleMoves == 0 %d,%d\n", point.i, point.j);
			}
			*/

		}
		Node addChild(Point p){
			Node child;
			if (children == null)
				children = new ArrayList<Node>();
			child = new Node(this, this.board, p, Board.getOppositeSide(stoneType));
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
		void addWins(int value){
			wins += value;
		}
		void addGames(int value){
			games += value;
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
		int getStoneType(){  
			return stoneType;
		}
		Board getBoard(){
			return board;
		}
		Node getParent(){
			return parent;
		}
		int startPlayout(){
			if (playout == null){
				playout = new Playout();
			}
			return playout.playRandomGame(board, Board.getOppositeSide(stoneType)); 
		}
	}
	Node root;
	Heuristics hr;
	Montecarlo(Board board, int whoseTurn){
		Pattern33.init();
		root = new Node(null, board, null, whoseTurn);
		hr = new Heuristics();
	}
	public void playOneSequence(){
		Node node = selectNode(root);

		node = expand(node);
		if (node == null){
			return;
		}
		int winner = simulation(node);
		backPropagation(node, winner);

	}
	private Node selectNode(Node node){
		int i, n, w, t; 
		double value, bestValue = -1, c = 0.44;
		Node bestNode = null;
		int MAX_NODES = 10;
		ArrayList<Node> children;
		Node currentNode = node;

		for (children = currentNode.getChildren(); children != null; currentNode = bestNode){
			
			bestNode = null;
			bestValue = -1;
			t = 0;

			// if (children.size() < currentNode.allPossibleMoves.size() && children.size() < MAX_NODES)
			// 	break; ///!!!!
			/*if (children == null)
				System.out.printf("children == null");
			else
				System.out.printf("children != null");
				*/
			
			for (Node child: children){
				t += child.getGames()+1;
			}
			

			for (Node child: children){
				n = child.getGames()+1;
				w = child.getWins();

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
	
	private Node expand(Node papa){
		int boardSize = root.getBoard().getSize();
		int[][] childrenBoard = new int[boardSize][boardSize];
		ArrayList<Point> notExpanded = new ArrayList<Point>(); 
		Random random = new Random();
		int randomPoint;
		ArrayList<Point> allPossibleMoves;
		
		allPossibleMoves = Playout.getFreePoints(papa.getBoard(), Board.getOppositeSide(papa.getStoneType()));
		if (allPossibleMoves.size() > 0){
			for (Point move : allPossibleMoves){
				// Проверяем не находится ли ход под атари
				if (!move.isAtari(Board.getOppositeSide(papa.getStoneType()))){
					papa.addChild(move);
				}
				
			}
			if (papa.getChildren() != null){
				rateChildren(papa);		
			}
			return selectNode(papa);
		}
		
		return null;
	}
	private void rateChildren(Node papa){
		// Constants
		final int boardSize = papa.getBoard().getSize();
		final int CAPTURE = 5;
		final int PATTERN33 = 5;
		final int THIRD_LINE = 5;
		final int FIRST_SECOND_LINE = 5;
		final int LAST_POINT_AREA = 10;

		ArrayList<Node> children = papa.getChildren();
		
		int[][] markBoard = new int[boardSize][boardSize];

		int i,j;
		int value;
		Point childPoint;
		int child_i, child_j;

		// At first try to mark all capture moves with positive values
		ArrayList<Point> allCaptureMoves = hr.capture.getAllMoves(papa.getBoard(), Board.getOppositeSide(papa.getStoneType()));
		ArrayList<Point> allPattern33Moves = hr.pattern33.getAllMoves(papa.getBoard());
		Point lastPoint = papa.getBoard().getLastPoint();
		ArrayList<Point> lastPointArea = null;

		// Create the board representation
		for (i = 0; i < boardSize; i++){
			Arrays.fill(markBoard[i],0);
		}

		// Помечаем ходы в окрестностях последнего хода как хорошие
		if (lastPoint != null){
			lastPointArea = lastPoint.getAllNeighbours();
			if (papa.getParent() == null){
				lastPoint.printPoint();
			}
			// Mark last move neighbourhood
			for (Point point : lastPointArea){
				if (point.i <= 0 || point.i >=boardSize-1 || point.j <= 0 || point.j >= boardSize-1){
					continue;
				}

				markBoard[point.i][point.j] += LAST_POINT_AREA;

				
			}
		}
		// Mark good moves with constant values
		for (Point move : allCaptureMoves){
			markBoard[move.i][move.j] += CAPTURE; 
		}
		for (Point move: allPattern33Moves){
			markBoard[move.i][move.j] += PATTERN33;
		}
		// Add constant values to children
		for (Node child: children){
			childPoint = child.getPoint();
			value = markBoard[childPoint.i][childPoint.j];
			child.addGames(value);
			child.addWins(value);

			// Set third line priority
			if (hr.isEmptyArea(papa.getBoard(), child.getPoint(), 3)){
				child_i = child.getPoint().i;
				child_j = child.getPoint().j;
				
				if (child_i == 2 || child_i == boardSize-3){
					if (child_j >= 2 && child_j < boardSize - 2){
						child.addGames(THIRD_LINE);
						child.addWins(THIRD_LINE);
					}
				}
				else if (child_i == 0 || child_i == 1 || child_i == boardSize-2 || child_i == boardSize-3){
					child.addGames(THIRD_LINE);
				}

				if (child_j == 2 || child_j == boardSize-3){
					if (child_i >= 2 && child_i < boardSize - 2){
						child.addGames(THIRD_LINE);
						child.addWins(THIRD_LINE);
					}
				}
				else if (child_j == 0 || child_j == 1 || child_j == boardSize-2 || child_j == boardSize-3){
					child.addGames(THIRD_LINE);
				}

			}
		}

	}

	private int simulation(Node node){
		return node.startPlayout();
	}
	private void backPropagation(Node startNode, int winner){
		Node currentNode = startNode;
		do{
			if (currentNode.getStoneType() == winner)
				currentNode.incWins();
			currentNode.incGames();
			currentNode = currentNode.getParent();
		} while(currentNode != null);
		
	}
	public Point getWinner(){
		ArrayList<Node> children = root.getChildren();
		Node bestChild = null;
		int maxGames = -1, maxWins = -1;
		double max = -1.0;
		double childValue;
		int games, wins;
		if (children != null){
			for (Node child : children){
				games = child.getGames();
				wins = child.getWins();
				try{
					childValue = ((double)wins)/games;
				}
				catch(ArithmeticException e){
					childValue = 0;
				}
				System.out.printf("[%d %d] Child %d wins: %d, games: %d (%f)\n",child.getPoint().i,child.getPoint().j, child.stoneType, child.getWins(), child.getGames(), childValue);


				if (max < childValue ){
					max = childValue;
					bestChild = child;
				}

			}
		}

		if (bestChild != null){

			System.out.printf("Best child wins: %d, games: %d\n", bestChild.getWins(), bestChild.getGames());
			return bestChild.getPoint();

		}
		else {
			return null;
		}
		
		
	}
	//non recursive DFS
	public void printTree(){
		Node node;
		ArrayList<Node> nodeChildren;
		Stack<Node> stack = new Stack<Node>();
		Stack<String> tabs_stack = new Stack<String>();		//TODO: fix this variable
		String tab = "+";
		
		stack.push(root);
		tabs_stack.add(tab);
		System.out.printf("\nMonteCarlo search tree:\n");
		while(!stack.empty()){
			node = stack.pop();
			//node.getBoard().printBoard();
			tab = tabs_stack.pop();
			nodeChildren = node.getChildren();
			
			if (nodeChildren != null){
				for (Node child: nodeChildren){
					tabs_stack.add(tab+"+");
					stack.add(child);
				}
			}
			if (node.getParent() != null){
				System.out.printf("\n%s %s [%d,%d] Games:%d Wins:%d\n", tab,
					node.getStoneType() == Board.FRIENDLY ? "FRIENDLY" : "ENEMY",
				  	node.getPoint().i, node.getPoint().j,
				  	node.getGames(), node.getWins());
			}

		}

	}

}