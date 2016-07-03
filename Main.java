import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class Main{
	public static void main(String[] args){
	
		Board board = new Board(9);
		Point p;
		Playout playout = new Playout();
		int stoneType = Board.ENEMY;
		
		//board.loadFromFile("board9x9_1.dat");	
		board.printBoard();
		/*

		Heuristics hrs = new Heuristics();
		Point lastDame = hrs.getLastDame(board, Board.ENEMY);
		if (lastDame != null){
			System.out.printf("Last dame: [%d,%d]",lastDame.i, lastDame.j);
		}
		*/
		
		//playout.playRandomGame(board, Board.ENEMY);

		//Montecarlo mc = new Montecarlo(board, Board.FRIENDLY);

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1; i++){

			playout.playRandomGame(board, Board.FRIENDLY);

		//	mc.playOneSequence();
			//System.gc();

		}
		System.out.println(System.currentTimeMillis() - startTime);
		//mc.printTree();
		
		/*p = mc.getWinner();
		if (p != null){
			System.out.printf("\nBest move is [%d, %d]\n", p.i, p.j);
			board.printBoard();
		}
		else {
			System.out.printf("\nCan't get best move, function returned null.\n");
		}
		
		/*
		for (int i = 0; i < 100; i++){
			board = new Board(9);
			System.out.printf("\n%d\n",i);
			playout.playRandomGame(board, Board.getOppositeSide(stoneType));
			if ( i % 10 == 0)
				System.gc();

		}*/
		//board.loadFromFile("board9x9_1.dat");
		//playout.removeDeadStones(board);
		//board.printBoard();
		/*board.saveBoardState();
		
		if (playout.checkRules(new Point(4,3), Board.FRIENDLY, board))
			System.out.println("\nLegal move");
		else
			System.out.println("\nIllegal move");
		
		board.setPoint(new Point(4,3), Board.FRIENDLY);
		playout.removeDeadStones(board, Board.ENEMY);
		board.printBoard();

		if (playout.checkRules(new Point(4,2), Board.ENEMY, board))
			System.out.println("\nLegal move");
		else
			System.out.println("\nIllegal move");
		board.setPoint(new Point(4,2), Board.ENEMY);
		*/
		//board.printBoard();
	}
	
	
}

