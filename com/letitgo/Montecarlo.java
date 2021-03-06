package com.letitgo;
import java.util.*;
import java.io.*;
import com.letitgo.heuristics.*;

public class Montecarlo{
	// Параметры для эвристики. Чем больше значения тем больше влияние
	// конкретной эвристики на алгоритм UCT
	final int CAPTURE = 100;
	final int PATTERN33 = 30;
	final int THIRD_LINE = 10;
	final int FIRST_SECOND_LINE = 10;
	final int LAST_POINT_AREA = 10;
	final int LAST_DAME = 30;
	final int SELF_ATARI = 100000;
	// Минимальное количество плейаутов перед раскрытием узла дерева
	private static final int MIN_GAMES_FOR_EXPLORATION = 5;
	
	private class Node{
		Node parent;
		ArrayList<Node> children = null;
		Board board; 
		int stoneType;
		Point point;
		Playout playout;
		int wins;
		int games;

		// Создаем новый узел в дереве ходов
		// Каждому узлу соответсвует уникальное состояние доски
		Node(Node parent, final Board board, final Point point, int stoneType){

			this.stoneType = stoneType;
			this.parent = parent;
			this.board = new Board(board);
			if (point != null){ // root node
				this.point = new Point(this.board,point.i,point.j);
			}
			wins = 0;
			games = 0;
			if (parent != null && point != null){ 		//root node
				this.board.setPoint(this.point, stoneType);
				boolean wasRemovedStones = this.board.removeDeadStones(this.point);
				// this.board.makeMove(this.point, stoneType);

				Group group = this.board.getGroup(this.point);

				if (group.isGroupInAtari() && !wasRemovedStones){
					this.wins = 0;
					this.games = SELF_ATARI; //TODO: Переделать, удалять еще до создания ноды

				}
			}


		}
		Node addChild(final Point p){
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
				playout = new Playout(komi);
			}
			return playout.playRandomGame(board, Board.getOppositeSide(stoneType)); 
		}
	}
	// Класс Move расширяет Point для использования доп. параметров,
	// например хранения вероятности победы для хода
	public static class Move extends Point{
		private double moveScore;
		public Move(Board board, int i, int j, double moveScore){
			super(board,i,j);
			this.moveScore = moveScore;
		}
		public double getMoveScore(){
			return moveScore;
		}
	}

	private Node root;
	private Heuristics hr;
	private final Board sourceBoard;
	private double komi = 6.5;
	
	Montecarlo(final Board board, int whoseTurn){
		// ×èòàåì ôàéë ñ øàáëîíàìè
		Pattern33.init();

		sourceBoard = board;
		root = new Node(null, sourceBoard, null, Board.getOppositeSide(whoseTurn));
		hr = new Heuristics();
	}
	Montecarlo(final Board board, int whoseTurn, double komi){
		this(board, whoseTurn);
		this.komi = komi;
	}
	// Сделать один проход UCT
	public void playOneSequence(){
		Node node = selectNode(root);

	//Раскрываем узел только если в не отыграно больше
	//случайных партий чем MIN_GAMES_FOR_EXPLORATION

		if (node == root || node.getGames() > MIN_GAMES_FOR_EXPLORATION){		
			node = expand(node);

			if (node == null){ // Если ошибка
				return;
			}
		}
		int winner = simulation(node);
		backPropagation(node, winner);

	}
	// Выбор узла
	// Параметр c используется для баланса между глубиной и шириной поиска
	// Чем меньше тем глубже исследуются интересные узлы
	private Node selectNode(Node node){
		int i, t; 
		double value, bestValue, c = 3.44,n,w;
		Node bestNode = null;
		ArrayList<Node> children;
		Node currentNode = node;

		for (children = currentNode.getChildren(); children != null; currentNode = bestNode){
			
			bestNode = null;
			bestValue = -1;
			t = 1;

			for (Node child: children){
				t += child.getGames();
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
			children = bestNode.getChildren(); 
			
		}
		return currentNode;
		
	}
	// Когда в узле проигранно некоторое количество партий,
	// мы открываем его. Т.е. создаем узлы с новыми ходами
	// сделанными после текущего (papa)
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
					papa.addChild(move);
		
			}
			// Назначаем приоритет новым ходам
			// Закомментировать if, чтобы не использовать эвристику
			if (papa.getChildren() != null){
				rateChildren(papa);		
			}
			return selectNode(papa);
		}
		
		return null;
	}
	// Функция назначения приоритетов ходам
	private void rateChildren(Node papa){
		final int boardSize = papa.getBoard().getSize();

		ArrayList<Node> children = papa.getChildren();
		
		int[][] markBoard = new int[boardSize][boardSize];

		int i,j;
		int value;
		Point childPoint;
		int child_i, child_j;


		ArrayList<Point> allCaptureMoves = hr.capture.getAllMoves(papa.getBoard(), Board.getOppositeSide(papa.getStoneType()));
		ArrayList<Point> allPattern33Moves = hr.pattern33.getAllMoves(papa.getBoard());
		ArrayList<Point> enemyGroupsLastDame = hr.capture.getAtariPoints(papa.getBoard(),papa.getStoneType());


		Point lastPoint = papa.getBoard().getLastPoint();
		ArrayList<Point> lastPointArea = null;

		for (i = 0; i < boardSize; i++){
			Arrays.fill(markBoard[i],0);
		}

		// Помечаем ходы в окрестностях последнего хода как хорошие
		if (lastPoint != null){
			lastPointArea = lastPoint.getAllNeighbours();

			
			for (Point point : lastPointArea){
				if (point.i <= 0 || point.i >=boardSize-1 || point.j <= 0 || point.j >= boardSize-1){
					continue;
				}

				markBoard[point.i][point.j] += LAST_POINT_AREA;

				
			}
		}
		for (Point move : allCaptureMoves){
			markBoard[move.i][move.j] += CAPTURE; 
		}
		for (Point move: allPattern33Moves){
			markBoard[move.i][move.j] += PATTERN33;
		}
		for (Point move: enemyGroupsLastDame){
			markBoard[move.i][move.j] += LAST_DAME;
		}

		for (Node child: children){
			childPoint = child.getPoint();
			value = markBoard[childPoint.i][childPoint.j];
			child.addGames(value);
			child.addWins(value);

			// Положительное значение приоритета ходам на 3-ю линию, при условии, что
			// на расстоянии в 3 клетки от него нет камней
			// Отрицательный приоритет 1-й и 2-й линиям
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
	// Обновляем значения узлов дерева
	private void backPropagation(Node startNode, int winner){
		Node currentNode = startNode;
		do{
			if (currentNode.getStoneType() == winner)
				currentNode.incWins();
			currentNode.incGames();
			currentNode = currentNode.getParent();
		} while(currentNode != null);
		
	}
	// Обновляем значения узлов дерева
	public Move getWinner(){
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
				// System.out.printf("[%d %d] Child %d wins: %d, games: %d (%f)\n",child.getPoint().i,child.getPoint().j, child.stoneType, child.getWins(), child.getGames(), childValue);

				if (max < child.getGames() && child.getGames() < SELF_ATARI){
					max = child.getGames();
					bestChild = child;
				}

			}
		}

		if (bestChild != null){

			// System.out.printf("Best child wins: %d, games: %d\n", bestChild.getWins(), bestChild.getGames());
			// if ((double)bestChild.getWins()/bestChild.getGames() < 0.25){
			// 	System.out.printf("Resign!\n");
			// }
			// printTree();
			return new Move(sourceBoard,bestChild.getPoint().i,bestChild.getPoint().j, (double)bestChild.getWins()/bestChild.getGames());

		}
		else {
			return null;
		}
				
	}

	// Вывести в консоль дерево (очень много:)
	public void printTree(){
		Node node;
		ArrayList<Node> nodeChildren;
		Stack<Node> stack = new Stack<Node>();
		Stack<String> tabs_stack = new Stack<String>();	
		String tab = "+";
		
		stack.push(root);
		tabs_stack.add(tab);
		// System.out.printf("\nMonteCarlo search tree:\n");
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
				if (node.getGames() != 0){
					// System.out.printf("\n%s %s [%d,%d] Games:%d Wins:%d\n", tab,
					// 	node.getStoneType() == Board.FRIENDLY ? "FRIENDLY" : "ENEMY",
					//   	node.getPoint().i, node.getPoint().j,
					//   	node.getGames(), node.getWins());
				}
			}

		}

	}

}