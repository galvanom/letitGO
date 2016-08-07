package com.letitgo.heuristics;
import java.io.*;
import java.util.*;
import com.letitgo.Point;
import com.letitgo.Board;

public class Heuristics{
	public Pattern33 pattern33;
	public Capture capture;

	public Heuristics(){
		this.pattern33 = new Pattern33();
		this.capture = new Capture();
	}
	// TODO: Completly not tested
	public boolean isEmptyArea(Board board, Point p, int dist){
		if (p.i < 0 || p.j < 0 || p.i >= board.getSize() || p.j >= board.getSize()){
			return true;
		}
		if (dist >= 0){
			return true;
		}
			ArrayList<Point> neighbours = p.getNeighbours();
			for (Point neighbour: neighbours){
				if (board.getPoint(neighbour) != Board.EMPTY){
					return false;
				}
				if (isEmptyArea(board,neighbour,dist-1) == false){
					return false;
				}
			}
		return true;

	}
}