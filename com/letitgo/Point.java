package com.letitgo;
import java.util.*;
// TODO: try factory method to get instances of the class
public class Point{
	public final int i, j;
	private Board board;

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
	public ArrayList<Point> getNeighbours(){
		ArrayList<Point> neighbours = new ArrayList<Point>();
		neighbours.add(new Point(board, this.i+1, this.j));
		neighbours.add(new Point(board, this.i-1, this.j));
		neighbours.add(new Point(board, this.i, this.j+1));
		neighbours.add(new Point(board, this.i, this.j-1));

		return neighbours;
	}
	public ArrayList<Point> getDiagonalNeighbours(){
		ArrayList<Point> neighbours = new ArrayList<Point>();
		neighbours.add(new Point(board, this.i+1, this.j+1));
		neighbours.add(new Point(board, this.i+1, this.j-1));
		neighbours.add(new Point(board, this.i-1, this.j-1));
		neighbours.add(new Point(board, this.i-1, this.j+1)); //WTF? This line increases execution for 5 times

		return neighbours;
	}
	public ArrayList<Point> getAllNeighbours(){
		ArrayList<Point> allNeighbours = getNeighbours();
		allNeighbours.addAll(getDiagonalNeighbours());

		return allNeighbours;
	}
	// TODO: Maybe change to boolean hasDame()
	public int getDameNumber(){
		int dame_count = 0;
		if (board.getPoint(this.i+1, this.j) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(this.i-1, this.j) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(this.i, this.j+1) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(this.i, this.j-1) == Board.EMPTY)
			dame_count++;

		return dame_count;
	}
	public boolean isSingleEyePoint(int stoneType){ 
		// boolean is_friendly = true;
		if (board.getPoint(this.i+1, this.j) == Board.getOppositeSide(stoneType)) 
			return false;
		if (board.getPoint(this.i-1, this.j) == Board.getOppositeSide(stoneType))
			return false;
		if (board.getPoint(this.i, this.j+1) == Board.getOppositeSide(stoneType))
			return false;
		if (board.getPoint(this.i, this.j-1) == Board.getOppositeSide(stoneType))
			return false;

		return true;
	}
	public void printPoint(){
		System.out.printf("[%d %d]\n", this.i, this.j);
	}
	public int getValue(){
		return this.board.getPoint(this);
	}
	// Проверяем не находится ли текущая позиция под атари
	// ownStoneType это цвет камня который находится под атари
	public boolean isAtari(int ownStoneType){
		int emptyPoints = 0;
		int notFriendlyStones = 0;
		ArrayList<Point> neighbours = getNeighbours();
		for (Point neighbour: neighbours){
			// Т.к. эта функция не проверяет группы под атари, встретив свой камень выходим
			if (board.getPoint(neighbour) == ownStoneType){
				return false;
			}
			else if (board.getPoint(neighbour) == Board.EMPTY){
				emptyPoints++;
			}
		}
		// Если только одно дыхание возвращаем true
		if (emptyPoints == 1){
			return true;
		}

		return false;
	}
}