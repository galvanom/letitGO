package com.letitgo;
import java.util.*;
import java.io.*;
import com.letitgo.heuristics.*;

public class Playout{
	private final Heuristics heuristics;
	private double komi;
    public Playout(){
    	this.komi = 6.5;
		heuristics = new Heuristics();
	}
	public Playout(double komi){
    	this();
    	this.komi = komi;
	}


	public int playRandomGame(final Board board, int first_stone){
		int boardSize = board.getSize();
		// Вероятность срабатывания эвристик
		// Например, 10 означает 1 раз из 10
		final int CAP_RAND_THRESS = 10;
		final int PAT_RAND_THRESS = 100;
		final int EYE_RAND_THRESS = 10;
		// Игра длится не более чем это значение ходов
		final int MAX_MOVES = boardSize*boardSize*2;

		ArrayList<Point> free_points;
		
		Random random;
		int freePointsSize;
		int random_point,i,j;
		int stoneType = first_stone;
		int passTimes = 0;
		Point captureMove = null;
		Point move, patternMove = null;
		Board playBoard = new Board(board);
		ArrayList<Point> points = null;
		

		for (int movesCount = 0; movesCount < MAX_MOVES && passTimes < 2; stoneType = Board.getOppositeSide(stoneType), movesCount++){


			
			// Перед использованием случайных ходов, пытаемся найти в окрестностях последнего хода
			// интересные с точки зрения эвристики точки.
			// Если они найдены ходим туда.			random = new Random(System.nanoTime());

			if (playBoard.getLastPoint() != null){

				if (random.nextInt(CAP_RAND_THRESS) != 0){
					captureMove =  heuristics.capture.getFirstMove(playBoard, stoneType);
					// Если ходы связанные с захватом групп найдены, делаем ход
					if (captureMove != null) {
						// playBoard.printBoard();
						playBoard.makeMove(captureMove, stoneType);
						// System.out.printf("\n****Capture move found. The point is [%d,%d] Stone type is: %s ****",captureMove.i, captureMove.j, stoneType == Board.ENEMY ? "X" :"O");
						// playBoard.printBoard();
						continue;
					}
				}
				// Если ходы попавшие под шаблоны найдены делаем ход
				if (random.nextInt(PAT_RAND_THRESS) != 0){
					patternMove = heuristics.pattern33.getFirstMove(playBoard, stoneType);

					if (patternMove != null ){
						// playBoard.printBoard();
						
							playBoard.makeMove(patternMove, stoneType);
							// System.out.printf("\n****Pattern found. The point is [%d,%d] Stone type is: %s ****",patternMove.i , patternMove.j, stoneType == Board.ENEMY ? "X" :"O");
							// playBoard.printBoard();
							continue;
						
					}
				}
			
			}
			
			// Эвристики не сработали? Выбираем слчайный ход
			free_points = getFreePoints(playBoard, stoneType);

			if (free_points.size() == 0){
				passTimes++;
				continue;
			}
			passTimes = 0;

			// При этом ход не должен быть внутрь своего глаза с некоторой вероятностью
			do {
				move = free_points.get(random.nextInt(free_points.size()));
			} while(move.isSingleEyePoint(stoneType) && random.nextInt(EYE_RAND_THRESS) != 0);

			//p = getBestMove(playBoard, free_points);

			playBoard.makeMove(move, stoneType);
			// playBoard.printBoard();

			
		}
		// playBoard.printBoard();
		// Îòûãðàëè. Ïîëó÷àåì ñ÷åò.
		int[] score = getScore(playBoard);

		// System.out.printf("\nO: %d  X: %d\n", score[0], score[1]);  
		return (double)score[0] + komi > score[1] ? Board.FRIENDLY : Board.ENEMY; //TODO: komi is not used
	}
	// TODO: Сделать тесты
	// Ищем точки окруженные каждым игроком
	// И вычисляем счет по китайским правилам
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
					// TODO: Поменять на stone.isFriendlySinglePoint()
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
// TODO: Сделать тесты
	// Получаем список всех валидных точек на доске, куда можно сходить
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
						if (p.isSingleEyePoint(stoneType) && p.isTrueEye(stoneType)){
							continue;
						}
						freePoints.add(p);
						

					}
				}

			}
		return freePoints;		
	}

}