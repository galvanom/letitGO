import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class Main{
	public static void main(String[] args){
	
		Board board = new Board(9);
		Point p = null;
		Playout playout = new Playout();
		int stoneType = Board.ENEMY;
		
		//board.loadFromFile("patterns_test1.dat");	
		board.printBoard();
		/*

		Point lastDame = hrs.getLastDame(board, Board.ENEMY);
		if (lastDame != null){
			System.out.printf("Last dame: [%d,%d]",lastDame.i, lastDame.j);
		}
		*/
		
		//playout.playRandomGame(board, Board.ENEMY);

		//Montecarlo mc = new Montecarlo(board, Board.FRIENDLY);

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++){
			if (i%1000 == 0){
				System.gc();
			}
			playout.playRandomGame(board, Board.FRIENDLY);
			//board.lastPoint = new Point(5,2);
			/*p = playout.getHeuristicMove(board,Board.FRIENDLY);
			if (p != null){
				System.out.printf("\nPoint [%d,%d]\n", p.i, p.j);
			}
			else {
				System.out.printf("\nNo point\n");
			}
		*/
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
		
		*/

	}
	
	
}

