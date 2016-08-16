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
		int stoneType = Board.ENEMY;
		Heuristics hr = new Heuristics();
		Montecarlo mc;
		long startTime;

		//board.loadFromFile("board9x9.dat");
		while (true){
			
			
			startTime = System.currentTimeMillis();
			mc = new Montecarlo(board, Board.FRIENDLY);	
			for (int i = 0; i < 10000; i++){
				// if (i%10000 == 0){
				// 	System.gc();
				// }
				mc.playOneSequence();
				//playout.playRandomGame(board, Board.FRIENDLY);
			}
			System.out.println(System.currentTimeMillis() - startTime);

			p = mc.getWinner();
			
			if (p == null){
				System.out.println("Error. Next move is Null\n");
			}
			else{
				p.printPoint();
				board.makeMove(p, Board.ENEMY);
				board.printBoard();
				//board.printBoard();
				//mc.printTree();
			}
			System.out.println("Set next Move for O:");
			board.makeMove(readNextMove(board),Board.FRIENDLY);
			board.printBoard();

		}		
	
		// Pattern33 pat = new Pattern33("patterns33.dat");
		// pat.createPatternsFile("patterns33.dat");
		// int[] patterns = pat.readPatternsFile("patterns33.dat");

		// if (patterns != null){
		// 	for (int pattern : patterns){
		// 		System.out.printf("%d ",pattern);
		// 	}
		// }

		// Pattern33.stringToNumber("XO.#");
		// char[] pattern = {'X','O','?','X','.','.','x','.','?'};
		// char[] pattern_ = {'.','O','.','X','.','.','.','.','.'};
		// ArrayList<String> allVariations = new ArrayList<String>();
		// if(!pat.getAllVariations(pattern_,0,allVariations)){
		// 	allVariations.add(String.valueOf(pattern_));
		// }
		// for (String variation : allVariations){
		// 	System.out.println(variation);
		// }

	}
	public static Point readNextMove(Board board){
		int i,j;
		Scanner sc = new Scanner(System.in);
		i = sc.nextInt(); j = sc.nextInt();
		
		return new Point(board, i,j);
	}
	
	
}

