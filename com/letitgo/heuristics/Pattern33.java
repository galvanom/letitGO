package com.letitgo.heuristics;
import java.util.*;
import com.letitgo.Board;
import com.letitgo.Playout;
import com.letitgo.Point;

public  class Pattern33{
	private final int PATTERN_SIZE = 9;
	private HashMap<String,String> wildchars = new HashMap<String,String>();
	private HashSet<String> permutations;
	private static final String [] patterns = {  // 3x3 playout patterns; X,O are colors, x,o are their inverses
						       "XOX...???",
						       "XO....?.?",
						       "XO?X..x.?",
						       ".O.X.....",
						       "XO?O.o?o?",
						       "XO?O.X???",
						       "?X?O.Oooo",
						       "OX?o.O???"
						    	};
	public Pattern33(){
		permutations = new HashSet<String>();
		// Initializing patterns...
		getPermutations3x3();
		// Wildchars initialization
		wildchars.put("x","O.");
		wildchars.put("X","X");
		wildchars.put("O","O");
		wildchars.put("o","X.");
		wildchars.put("?","XO.");
		wildchars.put(".",".");

	}
	//Fast version of the algorithm. Searches moves only in area of 3x3 around last move
	public Point getFirstMove(Board board){
		ArrayList<Point> points = new ArrayList<Point>();
		points = board.getLastPoint().getNeighbours();
		points.addAll(board.getLastPoint().getDiagonalNeighbours());
		
		return getPatternMove(board, points);
	}
	public ArrayList<Point> getAllMoves(Board board){
		int boardSize = board.getSize();
		int i,j;
		Point currentPoint;
		ArrayList<Point> allPattern33Moves = new ArrayList<Point>();
		for (i = 0; i < boardSize; i++){
			for (j = 0; j < boardSize; j++){
				currentPoint = new Point(board, i,j);
				if (board.getPoint(currentPoint) == Board.EMPTY){
					if (isPattern3x3(board, currentPoint)){
						allPattern33Moves.add(currentPoint);
					}
				}
			}
		}
		return allPattern33Moves;
	}

	private Point getPatternMove(Board board, ArrayList<Point> points){
		if (points != null){
			for (Point point: points){
				//System.out.printf("\n[%d,%d]\n", point.i, point.j);
				if (board.getPoint(point) == Board.EMPTY){
					if (isPattern3x3(board, point))
						return point;
				}
			}
		}
		return null;
			
	}
	/**
	* Get 3x3 region around the point
	* Returns chars wich represent region
	*/
	private char[] get3x3Region(Board board, Point point){
		int i,j,k;
		int stoneType;
		char[] square33 = new char[PATTERN_SIZE];

		for (i = point.i-1, k = 0;i <= point.i + 1;i++){
			j = point.j - 1;
			for (;j <= point.j + 1; j++,k++){
				stoneType = board.getPoint(i,j);
				switch (stoneType){
					case Board.ENEMY:
						square33[k] = 'X';
						break;
					case Board.FRIENDLY:
						square33[k] = 'O';
						break;
					case Board.EMPTY:
						square33[k] = '.';
							
				}
				//System.out.printf("%c ", square33[k]);
			}

		}
		return square33;
	}
	private  boolean isPattern3x3(Board board, Point point){
		int i,j;
		char[] pointsToCheck; 
		String symbols;


		if (point.i <= 0 || point.i >= board.getSize()-1 || point.j <= 0 || point.j >= board.getSize()-1)
			return false;
		
		pointsToCheck = get3x3Region(board, point);

		//Compare to the all patterns and they permutations
		for (String perm: permutations){
			//System.out.println(String.valueOf(perm));
			toNextPoint:
			for (i = 0; i < PATTERN_SIZE; i++){
				symbols = wildchars.get(String.valueOf(perm.charAt(i)));
				for (j = 0; j < symbols.length(); j++){
					if (symbols.charAt(j) == pointsToCheck[i])
						continue toNextPoint;
				}
				break;

			}
			if (i == PATTERN_SIZE){
				return true;
			}
		}
		return false;
	}

	private void getPermutations3x3(){
		/*int length = patterns.length*24;
		char[][] permutations = new char[length][];*/
		String currentPattern;
		//int next = 0;
		for (String pattern : patterns){
			currentPattern = pattern;
			for (int i = 0; i < 2; i++){ // 2 different colors 
				for (int j = 0; j < 4; j++){ // 4 90 degree permutations 
					permutations.add(currentPattern);
					permutations.add(getHorizPermutation(currentPattern));
					permutations.add(getVertPermutation(currentPattern));
					currentPattern = get90degPermutation(currentPattern);
				}
				currentPattern = changeColors(currentPattern);
			}
		}

		
	}
	private String get90degPermutation(String pattern){
		char[] newPattern = new char[9];
		newPattern[0] = pattern.charAt(6);
		newPattern[1] = pattern.charAt(3);
		newPattern[2] = pattern.charAt(0);
		newPattern[3] = pattern.charAt(7);
		newPattern[4] = pattern.charAt(4);
		newPattern[5] = pattern.charAt(1);
		newPattern[6] = pattern.charAt(8);
		newPattern[7] = pattern.charAt(5);
		newPattern[8] = pattern.charAt(2);
		return String.valueOf(newPattern);
	}
	private String getHorizPermutation(String pattern){
		char[] newPattern = new char[9];
		newPattern[0] = pattern.charAt(6);
		newPattern[1] = pattern.charAt(7);
		newPattern[2] = pattern.charAt(8);
		newPattern[3] = pattern.charAt(3);
		newPattern[4] = pattern.charAt(4);
		newPattern[5] = pattern.charAt(5);
		newPattern[6] = pattern.charAt(0);
		newPattern[7] = pattern.charAt(1);
		newPattern[8] = pattern.charAt(2);
		return String.valueOf(newPattern);
	}
	private String getVertPermutation(String pattern){
		char[] newPattern = new char[9];
		newPattern[0] = pattern.charAt(2);
		newPattern[1] = pattern.charAt(1);
		newPattern[2] = pattern.charAt(0);
		newPattern[3] = pattern.charAt(5);
		newPattern[4] = pattern.charAt(4);
		newPattern[5] = pattern.charAt(3);
		newPattern[6] = pattern.charAt(8);
		newPattern[7] = pattern.charAt(7);
		newPattern[8] = pattern.charAt(6);
		return String.valueOf(newPattern);
	}
	private String changeColors(String pattern){
		char[] newPattern = new char[9];
		char currentPoint;
		char point;
		for (int i = 0; i < 9; i++){
			point = pattern.charAt(i) ;
			switch (point){
				case 'X':
					currentPoint = 'O';
					break;
				case 'O':
					currentPoint = 'X';
					break;
				case 'x':
					currentPoint = 'o';
					break;
				case 'o':
					currentPoint = 'x';
					break;
				default:
					currentPoint = point;
			}

			
			newPattern[i] = currentPoint;
			
		}
		return String.valueOf(newPattern);
	}
}