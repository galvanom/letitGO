package com.letitgo;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import com.letitgo.heuristics.*;

public class Main{
	public static void main(String[] args){
		
		
		Board board = new Board(9);
		Point p = null;
		Playout playout = new Playout();
		int aiStone;
		int humanStone;
		Heuristics hr = new Heuristics();
		Montecarlo mc;
		long startTime;

		// playout.playRandomGame(board, Board.FRIENDLY);
		// ArrayList<Point> freePoints = Playout.getFreePoints(board, Board.FRIENDLY);
		// for (Point freePoint : freePoints){
		// 	freePoint.printPoint();
		// }
		//board.loadFromFile("board9x9.dat");


		while (true){
		
			startTime = System.currentTimeMillis();
			mc = new Montecarlo(board, Board.FRIENDLY);	
			for (int i = 0; i < 100; i++){
				// if (i%10000 == 0){
				// 	System.gc();
				// }
				mc.playOneSequence();
				//playout.playRandomGame(board, Board.FRIENDLY);
			}

			System.out.println(System.currentTimeMillis() - startTime);

		}

	}
	public static Point readNextMove(Board board){
		int i,j;
		Scanner sc = new Scanner(System.in);
		i = sc.nextInt(); j = sc.nextInt();
		
		return new Point(board, i,j);
	}
	public static void humanMove(Board board, int humanStoneType){
		Point move;
		while (true){
				System.out.println("Set next Move for O:");
				move = readNextMove(board);
				if (!board.checkRules(move, Board.FRIENDLY)){
					System.out.println("Your move is illegal!");

				}
				else{
					break;
				}
			}

			board.makeMove(move,Board.FRIENDLY);
			board.printBoard();
	}
	
	
}

