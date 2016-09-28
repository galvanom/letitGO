package com.letitgo;
import java.util.*;
import java.io.*;

public class Board{
	private int[][] board, boardState;
	private final int size;

	public Point koPoint = null;
	public int koPointLifeTime = 0;
	public int koStoneType = EMPTY;
	private Point lastPoint = null;
	public Point tryPoint = null;

	// Состояния пересечения доски 
	public static final int EMPTY = 0;
	public static final int BORDER = 1;
	public static final int FRIENDLY= 2;
	public static final int ENEMY = 3;


	public Board(int size){
		int i, j;
		this.size = size+2;
		board = new int[this.size][this.size];
		for (i = 0; i < this.size; i++)
			for (j = 0; j < this.size; j++)
				board[i][j] = EMPTY;
		for (i = 0; i < this.size; i++){
			board[0][i] = BORDER;
			board[i][0] = BORDER;
			board[this.size-1][i] = BORDER;
			board[i][this.size-1] = BORDER;

		}

	}
	// Копируем доску
	public Board(final Board otherBoard){
		int i,j;
		this.size = otherBoard.getSize()+2;
		this.board = new int[this.size][this.size];
		
		for (i = 0; i < this.size; i++){
			board[0][i] = BORDER;
			board[i][0] = BORDER;
			board[this.size-1][i] = BORDER;
			board[i][this.size-1] = BORDER;
		}
		for (i = 0; i < this.size-2; i++)
			for (j = 0; j < this.size-2; j++)
				this.board[i+1][j+1] = otherBoard.getPoint(i,j);

		// Копируем не только доску, но и состояние ее полей
		if (otherBoard.koPoint != null){
			this.koPoint = new Point(this, otherBoard.koPoint.i,
				otherBoard.koPoint.j) ;
			this.koPointLifeTime = otherBoard.koPointLifeTime;
			this.koStoneType = otherBoard.koStoneType;
		}
		if (otherBoard.getLastPoint() != null){
			this.lastPoint = new Point(this, otherBoard.getLastPoint().i, 
				otherBoard.getLastPoint().j);
		}
		

	}
	// Запоминает состояние доски перед тем как поставить камень
	public void tryMove(final Point p, int pointType){
		this.tryPoint = p;
		board[p.i + 1][p.j + 1] = pointType;
		// board[(p.i + 1)*size + p.j + 1] 1D array
	}
	// Восстанавливает состояние доски после 
	public void undoMove(){
		if (this.tryPoint != null){
			board[this.tryPoint.i + 1][this.tryPoint.j + 1] = Board.EMPTY;
			this.tryPoint = null;
		}

	}
	public void setLastPoint(Point p){
		this.lastPoint = p;
	}
	public void printBoard(){
		int i, j, k;
		System.out.print("\n  ");
		for (k = 0; k < size-2; k++){
			System.out.printf("%d ", k);
		}
		System.out.println();
		for (i = 0; i < size; i++){

			for (j = 0; j < size; j++){
				switch (board[i][j]) {
					case EMPTY:
						System.out.print(".");
						break;
					case BORDER:
						System.out.print("#");
						break;
					case FRIENDLY:
						System.out.print("O");
						break;
					case ENEMY:
						System.out.print("X");
						break;
				}
				System.out.print(" ");
			}
		if ( i != 0 && i != size-1){
			System.out.print(i-1);
			}	
		System.out.println();
		}
		
	}
	// Читаем доску из файла
	public void loadFromFile(String fileName){
		int i,j;
		char point;
		int c;
		try{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream in = classLoader.getResourceAsStream(fileName);
			for (i = 0, j = 0, c = in.read(); c != -1 &&
			 i < this.size -2; c = in.read()){ 
				if ((char)c == ' ')
					continue;

				switch ((char)c){
					case '.':
						board[i+1][j+1] = Board.EMPTY;
						j++;
						break;
					case 'X':
						board[i+1][j+1] = Board.ENEMY;
						j++;
						break;
					case 'O':
						board[i+1][j+1] = Board.FRIENDLY;
						j++;
						break;
					default:
					break;
				}
				if (j == this.size - 2){
					i++; j = 0;
				}
			}
		}

		catch(IOException e){
			System.out.println("File read error");
		}
	}
	// Установить пересечение в нужное состояние
	public void setPoint(int i, int j, int pointType){
		if (pointType == Board.FRIENDLY || pointType == Board.ENEMY){
			koPointLifeTime++; //for KO
			lastPoint = new Point(this, i,j);
		}

		board[i + 1][j + 1] = pointType;
	}
	public void setPoint(Point p, int pointType){
		setPoint(p.i, p.j, pointType);
	}
	// Вернуть состояние пересечения (точки)
	public int getPoint(int i, int j){
		return board[i + 1][j + 1];
	}
	public int getPoint(Point p){
		return board[p.i + 1][p.j + 1];
	}
	// Вернуть формальный размер доски (9,13,19 итд)
	public int getSize(){
		return size - 2;
	}
	// Установить КО
	private void setKO(Point point, int stoneType){
		koPoint = point;
		koStoneType = stoneType;
		koPointLifeTime = 0;

	}
	// Проверка на КО
	public boolean isKO(Point point, int stoneType){
		if (koPointLifeTime == 0 && koPoint != null){

			if (koPoint.i == point.i && koPoint.j == point.j &&
			 koStoneType == stoneType){
				return true;
			}
		}
		return false;
	}
	// Вернуть последний ход
	public Point getLastPoint(){
		return lastPoint;
	}
	// Сделать ход
	// Установить точку в состояние stoneType и удалить мертвые камни
	public void makeMove(Point p, int stoneType){
		setPoint(p, stoneType);
		removeDeadStones(p);
	}
	// Проверить можно ли поставить камень в эту точку
	public  boolean checkRules(final Point p, int stoneType){

		ArrayList<Point> points_to_delete = new ArrayList<Point>();
		ArrayList<Point> points;
		ArrayList<Point> neighbours = p.getNeighbours();
		Group group;

		if (p.getValue() != Board.EMPTY){
			return false;
		}
		// Если у камня есть дамэ, тогда все по правилам
		if (p.getDameNumber() != 0){
			return true;
		}
		// Если это КО, тогда нарушение правил
		if (isKO(p, stoneType)){
			return false;
		}
		
		// Проверяем можем ли можем ли мы убить соседние группы

		tryMove(p, stoneType);
		
		if (!isDead(p).isEmpty()){
			for (Point neighbour: neighbours){
				
				if (getPoint(neighbour) == getOppositeSide(stoneType)){
										
					if (!isDead(neighbour).isEmpty()){

						undoMove();
						return true;
					}
					
				}
								
			}

		}
		else {
			undoMove();
			return true;
		}

		undoMove();
		return false;
	}
	// Мертва ли группа в которую входит камень
	private ArrayList<Point> isDead(final Point initialPoint){
		int i,j;
		int dame_number;
		ArrayList<Point> neighbours;
		LinkedList<Point> queue = new LinkedList<Point>();
		ArrayList<Point> visited = new ArrayList<Point>();

		int boardSize = getSize();
		Group group = null;
		Point currentPoint;

		if (getPoint(initialPoint) == Board.FRIENDLY || getPoint(initialPoint) == Board.ENEMY){

			queue.add(initialPoint);
			while (queue.size() > 0){
				currentPoint = queue.poll();
				if (currentPoint.getDameNumber() > 0){
					return new ArrayList<Point>();
				}
				neighbours = currentPoint.getNeighbours();
				visited.add(currentPoint);
			
				for (Point neighbour: neighbours){
					if ( (getPoint(currentPoint) == getPoint(neighbour)) && 
						!isPointVisited(visited, queue, neighbour) ){
						queue.add(neighbour);
					}
				}
								
			}
		}
		return visited;

	}
	/*
	* Убираем мертвые камни с доски
	* Параметр lastMove содержит последний ход, вокруг которого
	* мы будем искать мертвые камни
	*/
	public boolean removeDeadStones(Point lastMove){
		int stoneType = getOppositeSide(getPoint(lastMove));
		int i,j, allDeleted = 0, deleted;
		Point point, lastPoint = null; //change lastPoint name
		Group group;
		boolean[][] visited = new boolean[getSize()][getSize()];
		ArrayList<Point> lastMoveNeighbours = lastMove.getNeighbours();
		boolean isSingleEye = lastMove.isSingleEyePoint(stoneType);

		for (Point neighbour: lastMoveNeighbours){
				
			if (getPoint(neighbour) == stoneType){

				deleted = deleteGroupIfItsDead(neighbour);
				allDeleted += deleted; 
				if (deleted == 1){
					lastPoint = neighbour;
				}
			}
			
		}
		// Если удалили 1 камень и ход сделан в глаз противника,
		// то это Ко
		if (allDeleted == 1 && lastPoint != null && isSingleEye){

			setKO(lastPoint, stoneType);
		}
		if (allDeleted > 0){ // удалили с доски камни или нет
			return true;
		}

		return false;
	}

	public int deleteGroupIfItsDead(final Point p){
		ArrayList<Point> group = isDead(p);
		for (Point point: group){
			setPoint(point, Board.EMPTY);
		}
		return group.size();
	}

	// Находим все камни в группе, где находится камень p 
	public Group getGroup(final Point p){
		int i,j;
		int dame_number;
		ArrayList<Point> neighbours;
		LinkedList<Point> queue = new LinkedList<Point>();
		ArrayList<Point> visited = new ArrayList<Point>();
		int boardSize = getSize();
		Group group = null;
		Point currentPoint;

		if (getPoint(p) == Board.FRIENDLY || getPoint(p) == Board.ENEMY){

			queue.add(p);
			while (queue.size() > 0){
				currentPoint = queue.poll();
				neighbours = currentPoint.getNeighbours();
				visited.add(currentPoint);
			
				for (Point neighbour: neighbours){
					if ((getPoint(currentPoint) == getPoint(neighbour)) && 
						!isPointVisited(visited, queue, neighbour)){
						queue.add(neighbour);
					}
				}
								
			}
			group = new Group(this, visited);
		}
		
		return group;
	}
	private boolean isPointVisited(	ArrayList<Point> visited, 
									LinkedList<Point> queue, 
									Point p){
		Iterator<Point> it_v = visited.iterator();
		Iterator<Point> it_q = queue.iterator();
		while(it_v.hasNext()){
			if (p.isEqualsTo(it_v.next()))
				return true;
		}
		while(it_q.hasNext()){
			if (p.isEqualsTo(it_q.next()))
				return true;
		}

		return false;
	}
	// Возвращает камень обратный данному
	public static int getOppositeSide(int stone){
		if (stone == FRIENDLY)
			return ENEMY;
			
		return FRIENDLY;
	}

}