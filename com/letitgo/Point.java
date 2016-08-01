package com.letitgo;
public class Point{
	public int i, j;
	private final Board board;
	public Point(){
		
	}
	public Point(Board board, int i, int j){
		this.board = board;
		this.i = i;
		this.j = j;
	}
	public boolean isEqualsTo(Point other){
		if (this.i == other.i && this.j == other.j)
			return true;
		return false;
	}
	public static ArrayList<Point> getNeighbours(){
		ArrayList<Point> neighbours = new ArrayList<Point>();
		neighbours.add(new Point(board, p.i+1, p.j));
		neighbours.add(new Point(board, p.i-1, p.j));
		neighbours.add(new Point(board, p.i, p.j+1));
		neighbours.add(new Point(board, p.i, p.j-1));

		return neighbours;
	}
	public static ArrayList<Point> getDiagonalNeighbours(){
		ArrayList<Point> neighbours = new ArrayList<Point>();
		neighbours.add(new Point(board, p.i+1, p.j+1));
		neighbours.add(new Point(board, p.i+1, p.j-1));
		neighbours.add(new Point(board, p.i-1, p.j-1));
		neighbours.add(new Point(board, p.i-1, p.j+1)); //WTF? This line increases execution for 5 times

		return neighbours;
	}
	// TODO: Maybe change to boolean hasDame()
	public static int getDameNumber(){
		int dame_count = 0;
		if (board.getPoint(p.i+1, p.j) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(p.i-1, p.j) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(p.i, p.j+1) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(p.i, p.j-1) == Board.EMPTY)
			dame_count++;

		return dame_count;
	}
	public static boolean isFriendlySingleEyePoint(int stoneType){ 
		boolean is_friendly = true;
		if (board.getPoint(p.i+1, p.j) == Board.getOppositeSide(stoneType)) 
			return false;
		if (board.getPoint(p.i-1, p.j) == Board.getOppositeSide(stoneType))
			return false;
		if (board.getPoint(p.i, p.j+1) == Board.getOppositeSide(stoneType))
			return false;;
		if (board.getPoint(p.i, p.j-1) == Board.getOppositeSide(stoneType))
			return false;

		return true;
	}

}