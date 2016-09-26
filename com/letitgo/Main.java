package com.letitgo;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import com.letitgo.heuristics.*;

public class Main{
	public static void main(String[] args){
		
		ConsoleInterface ci = new ConsoleInterface();
		ci.setParam(9, Board.FRIENDLY, 6.5, 5000);
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

	
}
