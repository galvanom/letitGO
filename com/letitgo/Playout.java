package com.letitgo;
import java.util.*;
import java.io.*;
import com.letitgo.heuristics.*;

public class Playout{
	private final Heuristics heuristics;
	private double komi;
    public Playout(){
    	komi = 6.5;
		heuristics = new Heuristics();
	}

	public int playRandomGame(final Board board, int first_stone){
		//Вероятность срабатывания эвристик
		final int CAP_RAND_THRESS = 10;
		final int PAT_RAND_THRESS = 10;
		final int EYE_RAND_THRESS = 10;

		ArrayList<Point> free_points;
		int boardSize = board.getSize();
		//Point[] free_points = new Point[boardSize*boardSize];
		Random random;
		int freePointsSize;
		int random_point,i,j;
		int stoneType = first_stone;
		int passTimes = 0;
		final int MAX_MOVES = boardSize*boardSize*2;
		Point captureMove = null;
		Point move, patternMove = null;
		//Point heurPoint = new Point();
		Board playBoard = new Board(board);
		ArrayList<Point> points = null;
		
		//Tests for patterns
		/*if (Pattern33.isPattern3x3(playBoard, new Point(1,7)))
			System.out.printf("\n***Pattern found!\n");
		else
			System.out.printf("\n***No pattern found\n");
		*/	
		

		for (int movesCount = 0; movesCount < MAX_MOVES && passTimes < 2; stoneType = Board.getOppositeSide(stoneType), movesCount++){

			//Before to get random moves, engine has to try moves in the last move's neighbourhood.
			//This moves have to be either "capture/release capture" or the patterns
			random = new Random(System.nanoTime());

			if (playBoard.getLastPoint() != null){

				if (random.nextInt(CAP_RAND_THRESS) != 0){
					captureMove =  heuristics.capture.getFirstMove(playBoard, stoneType);
					if (captureMove != null) {
						// playBoard.printBoard();
						playBoard.makeMove(captureMove, stoneType);
						// System.out.printf("\n****Capture occured. The point is [%d,%d] Stone type is: %s ****",captureMove.i, captureMove.j, stoneType == Board.ENEMY ? "X" :"O");
						// playBoard.printBoard();
						continue;
					}
				}

				if (random.nextInt(PAT_RAND_THRESS) != 0){
					patternMove = heuristics.pattern33.getFirstMove(playBoard);

					if (patternMove != null){
						// playBoard.printBoard();
						playBoard.makeMove(patternMove, stoneType);
						// System.out.printf("\n****Pattern occured. The point is [%d,%d] Stone type is: %s ****",patternMove.i , patternMove.j, stoneType == Board.ENEMY ? "X" :"O");
						// playBoard.printBoard();
						continue;
					}
				}
			
			}
			
			//TODO: Improve getFreePoints for the perfomance goal. The bigger number of free points is the bigger CPU load.
			//Maybe this too many garbage to collect and GC runs more frequently then usually
			
			free_points = getFreePoints(playBoard, stoneType);

			if (free_points.size() == 0){
				passTimes++;
				continue;
			}
			passTimes = 0;

			
			do {
				move = free_points.get(random.nextInt(free_points.size()));
			} while(move.isSingleEyePoint(stoneType) && random.nextInt(EYE_RAND_THRESS) != 0);

			//p = getBestMove(playBoard, free_points);

			playBoard.makeMove(move, stoneType);
			// playBoard.printBoard();

			
		}
		// playBoard.printBoard();

		int[] score = getScore(playBoard);

		// System.out.printf("\nO: %d  X: %d\n", score[0], score[1]);  
		return (double)score[0] + komi > score[1] ? Board.FRIENDLY : Board.ENEMY; //TODO: komi is not used
	}
	// TODO: Make a test for this
	private int[] getScore(final Board board){
		int i,j, friendScore = 0, enemyScore = 0;
		// int surroundedStones[] = new int[4];
		ArrayList<Point> surroundedStones;
		Point p;
		boolean isFriendly, isEnemy;
		int pointType;


		for (i = 0; i < board.getSize(); i++){
			for (j = 0; j < board.getSize(); j++){
				p = new Point(board, i, j);
				if (board.getPoint(p) == Board.EMPTY){
					surroundedStones = p.getNeighbours();
					isFriendly = true; isEnemy = true;
					// TODO: Change for stone.isFriendlySinglePoint()
					for (Point stone: surroundedStones){
						pointType = board.getPoint(stone);
						if (pointType != Board.FRIENDLY && pointType != Board.BORDER){
							isFriendly = false;
							break;
						}
					}
					for (Point stone: surroundedStones){
						pointType = board.getPoint(stone);
						if (pointType != Board.ENEMY && pointType != Board.BORDER){
							isEnemy = false;
							break;
						}
					}

					if (isFriendly)
						friendScore++;
					if (isEnemy)
						enemyScore++;
				}
				else {
					if (board.getPoint(p) == Board.FRIENDLY){
						friendScore++;
					}
					if (board.getPoint(p) == Board.ENEMY){
						enemyScore++;
					}

				}
			}
		}

		int[] score = {friendScore, enemyScore};
		return score;
	}
	// TODO: Make test for this
	public static ArrayList<Point> getFreePoints(final Board board, int stoneType){
		int i,j;
		int boardSize = board.getSize();
		ArrayList<Point> freePoints = new ArrayList<Point>();

		Point p;
		for (i = 0; i < boardSize; i++)
			for (j = 0; j < boardSize; j++){
				p = new Point(board, i, j); //TODO: Поменять с нижней строчкой местами
				if (board.getPoint(p) == Board.EMPTY){
					if (board.checkRules(p, stoneType)){
						// TODO: Dont like it. Try to move getDame check
						// If point doesnt have dame but there is a friendly single eye
						if (p.isSingleEyePoint(stoneType) && p.isTrueEye(stoneType)){
							continue;
						}
						freePoints.add(p);
						

					}
				}

			}
		return freePoints;		
	}

	public void setKomi(double value){
		this.komi = value;
	}

}