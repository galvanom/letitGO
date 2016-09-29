package com.letitgo;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import com.letitgo.heuristics.*;

public class Main{
	public static void main(String[] args){
		int aiStone = Board.FRIENDLY;
	
		for (String s: args){
			if (s.equals("-gtp")){
				GTP gtp = new GTP();
				gtp.start();
				return;
			}
			if (s.equals("-w")){
				aiStone = Board.ENEMY;
				break;
			}
			if (s.equals("-h")){
			    System.out.printf("\n--gtp\tuse GTP");
        		System.out.printf("\n-w\tplay white in CLI");
        		System.out.printf("\n-h\thelp\n");
        		return;
        	}
		}

		ConsoleInterface ci = new ConsoleInterface();
		ci.setParam(9, aiStone, 6.5, 5000);
		ci.start();


		
		// Board board = new Board(9);
		// Point p = null;
		

		// startTime = System.currentTimeMillis();
		// mc = new Montecarlo(board, aiStone);	
		// for (int i = 0; i < 1; i++){
		// 	// if (i%10000 == 0){
		// 	// 	System.gc();
		// 	// }
		// 	mc.playOneSequence();
		// 	//playout.playRandomGame(board, Board.FRIENDLY);
		// }


		// System.out.println(System.currentTimeMillis() - startTime);

		// p = mc.getWinner();
		// p.printPoint();
		// board.printBoard();

	}

	
}
