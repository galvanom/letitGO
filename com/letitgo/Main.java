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
		int humanStone = 0;
		Heuristics hr = new Heuristics();
		Montecarlo mc;
		long startTime;

		// playout.playRandomGame(board, Board.FRIENDLY);
		// ArrayList<Point> freePoints = Playout.getFreePoints(board, Board.FRIENDLY);
		// for (Point freePoint : freePoints){
		// 	freePoint.printPoint();
		// }
		//board.loadFromFile("board9x9.dat");

		System.out.println("Please choose your side. Type X for black or O for white:");
		char side = 'X';
		try{
			side = (char) System.in.read();
		}
		catch(IOException e){

		}

		if (side != 'X' && side != 'O'){
			System.out.println("Wrong choice. Type X for black or O for white:");
			return;
		}
		if (side == 'X'){
			humanStone = Board.ENEMY;
			aiStone = Board.FRIENDLY;
			humanMove(board, humanStone);
		}
		if (side == 'O'){
			humanStone = Board.FRIENDLY;
			aiStone = Board.ENEMY;
		}

		while (true){
		
			startTime = System.currentTimeMillis();
			mc = new Montecarlo(board, Board.FRIENDLY);	
			for (int i = 0; i < 5000; i++){
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

			
			humanMove(board, humanStone);

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
