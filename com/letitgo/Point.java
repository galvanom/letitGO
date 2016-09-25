package com.letitgo;
import java.util.*;

public class Point{
	public final int i, j;
	public Board board;

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
		neighbours.add(new Point(board, this.i-1, this.j+1));

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
	// Является ли глазом
	public boolean isSingleEyePoint(int stoneType){ 
		ArrayList<Point> neighbours = getNeighbours();

		for (Point neighbour : neighbours){
			if (neighbour.getValue() == Board.getOppositeSide(stoneType) ||
				neighbour.getValue() == Board.EMPTY){
				return false;
			}
		}

		return true;
	}
	// Настоящий ли глаз
	// Должна использоваться после определения точки как глаза
	public boolean isTrueEye(int stoneType){
		ArrayList<Point> diagNeighbours = getDiagonalNeighbours();
		int myStones = 0;
		int notMyStones = 0;
		boolean sideOrCorner = false;

		for (Point neighbour : diagNeighbours){
			if (neighbour.getValue() == stoneType){
				myStones++;
			}
			else if (neighbour.getValue() != Board.BORDER){
				notMyStones++;
			}
				else{
					sideOrCorner = true;
				}
			if (notMyStones > 1){
				return false;
			}
		}

		if (notMyStones == 0 && sideOrCorner){
			return true;
		}
		if (notMyStones <= 1 && !sideOrCorner){
			return true;
		}
		return false;
	}
	public void printPoint(){
		System.out.printf("[%d %d]\n", this.i, this.j);
	}
	public int getValue(){
		return this.board.getPoint(this);
	}

}