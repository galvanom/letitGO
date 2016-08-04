package com.letitgo;
import java.util.*;
import java.io.*;

public class Board{
	private int[][] board, boardState;
	private final int size;

	private Point koPoint = null;
	private int koPointLifeTime = 0;
	private int koStoneType = EMPTY;
	private Point lastPoint = null;
	public Point tryPoint = null;

	//Point types
	public static final int EMPTY = 0;
	public static final int BORDER = 1;
	public static final int FRIENDLY= 2;
	public static final int ENEMY = 3;

	//public Board(){}

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
	// TODO: Copy class fields too
	public Board(Board otherBoard){
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

	}
	public void tryMove(final Point p, int pointType){
		this.tryPoint = p;
		board[p.i + 1][p.j + 1] = pointType;
	}
	public void undoMove(){
		if (this.tryPoint != null){
			board[this.tryPoint.i + 1][this.tryPoint.j + 1] = Board.EMPTY;
			this.tryPoint = null;
		}

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
	public void loadFromFile(String filename){
		int i,j;
		char point;
		int c;
		try{
			InputStream in = new FileInputStream(new File(filename));
			for (i = 0, j = 0, c = in.read(); c != -1 && i < this.size -2; c = in.read()){ 
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
	public int getPoint(int i, int j){
		return board[i + 1][j + 1];
	}
	public int getPoint(Point p){
		return board[p.i + 1][p.j + 1];
	}

	public int getSize(){
		return size - 2;
	}

	private void setKO(Point point, int stoneType){
		koPoint = point;
		koStoneType = stoneType;
		koPointLifeTime = 0;

	}
	private boolean isKO(Point point, int stoneType){
		if (koPointLifeTime == 0 && koPoint != null){
			//System.out.printf("koPointLifeTime: %d KoPoint:[%d,%d] Point: [%d,%d] koStoneType: %d stoneType: %d\n", koPointLifeTime, koPoint.i, koPoint.j, point.i, point.j,koStoneType,stoneType );

			if (koPoint.i == point.i && koPoint.j == point.j && koStoneType == stoneType){
				return true;
			}
		}
		return false;
	}
	public Point getLastPoint(){
		return lastPoint;
	}
	public void makeMove(Point p, int stoneType){
		setPoint(p, stoneType);
		removeDeadStones(getOppositeSide(stoneType));
	}

	public  boolean checkRules(final Point p, int stoneType){

		ArrayList<Point> points_to_delete = new ArrayList<Point>();
		ArrayList<Point> points;
		ArrayList<Point> neighbours = p.getNeighbours();
		Group group;
		boolean rulesOK = false;


		// If point has dame return true
		if (p.getDameNumber() != 0){
			return true;
		}
		// If point doesnt have dame but there is a friendly single eye return false
		if (p.isFriendlySingleEyePoint(stoneType)){
			return false;
		}
		// If there is a Ko return false
		if (isKO(p, stoneType)){
			return false;
		}
		

	    // Check. Could we kill neigbour enemy groups with this move.
	    // So we put a stone to the point position and look for dead enemy groups around
		tryMove(p, stoneType);
		
		group = getGroup(p);

		if (group.isGroupDead() == true){

		
			for (Point neighbour: neighbours){
				
				if (getPoint(neighbour) == getOppositeSide(stoneType)){
					
					//System.out.printf("\nNeigbour [%d:%d]\n", next.i, next.j);
					group = getGroup(neighbour);
					
					if (group.isGroupDead() == true){
						rulesOK = true;
						break;
					}
					
				}
				
			}
			
		}
		
		undoMove();
		return rulesOK;
	}
	public void removeDeadStones(int stoneType){
		int i,j, deletedStonesNumber = 0;
		Point point, lastPoint = null; //change lastPoint name
		Group group;
		boolean[][] visited = new boolean[getSize()][getSize()];
		
		for(i = 0; i < getSize(); i++)
			for(j = 0; j < getSize(); j++)
				visited[i][j] = false;

		for(i = 0; i < getSize(); i++){
			for(j = 0; j < getSize(); j++){
				point = new Point(this, i, j);
				if (visited[point.i][point.j] == true){
					continue;
				}
				
				if (getPoint(point) == stoneType){

					group = getGroup(point);
					
					if (group.isGroupDead() == true){
						deletedStonesNumber += group.getSize();
						lastPoint = point; //possible ko point
							    

						for (Point stone: group){
							setPoint(stone, Board.EMPTY);
						}
					}
					else{
						for (Point stone: group){
							visited[stone.i][stone.j] = true;
						}
					}
				}
			
			}
		}
		if (deletedStonesNumber == 1 && lastPoint != null){
			setKO(lastPoint, stoneType);
		}
	}
	// TODO: Return an empty group except null
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
					if ((getPoint(currentPoint) == getPoint(neighbour)) && !isPointVisited(visited, queue, neighbour)){
						queue.add(neighbour);
					}
				}
								
			}
			group = new Group(this, visited);
		}


		
		return group;
	}
	private static boolean isPointVisited(ArrayList<Point> visited, LinkedList<Point> queue, Point p){
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
	public static int getOppositeSide(int stone){
		if (stone == FRIENDLY)
			return ENEMY;
			
		return FRIENDLY;
	}

}