package com.letitgo;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import com.letitgo.heuristics.*;

public class Main{
	public static void main(String[] args){
		
		ConsoleInterface ci = new ConsoleInterface();
		ci.setParam(9, Board.ENEMY, 6.5, 5000);
		ci.start();
		// GTP gtp = new GTP();
		// gtp.start();

		
		Board board = new Board(9);
		Point p = null;
		Playout playout = new Playout();

		int aiStone = Board.ENEMY;
		int humanStone = Board.FRIENDLY;
		// // // int aiStone = Board.FRIENDLY;
		// // // int humanStone = Board.ENEMY;
		
		Heuristics hr = new Heuristics();
		Montecarlo mc;
		long startTime;

		board.loadFromFile("board9x9.dat");
		board.printBoard();


		// ArrayList<Point> capmov = hr.capture.getAllMoves(board, Board.ENEMY);
		// for (Point move : capmov){
		// 	move.printPoint();
		// }

		// while (true){
		
			startTime = System.currentTimeMillis();
			mc = new Montecarlo(board, humanStone);	
			for (int i = 0; i < 3000; i++){
				// if (i%10000 == 0){
				// 	System.gc();
				// }
				mc.playOneSequence();
				//playout.playRandomGame(board, Board.FRIENDLY);
			}


			System.out.println(System.currentTimeMillis() - startTime);

			p = mc.getWinner();
			p.printPoint();
			board.printBoard();

			
	
		// }		
	


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
				System.out.println("Set next Move: ");
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
