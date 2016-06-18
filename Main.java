import java.util.*;
import java.io.*;

public class Main{
	public static void main(String[] args){
	
		Board board = new Board(9);
		Point p;
		Playout playout = new Playout();
		//playout.playRandomGame(board, Board.FRIENDLY);
		
		board.loadFromFile("board9x9.dat");
		//playout.removeDeadStones(board);
		board.printBoard();
		board.saveBoardState();
		
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
		
		board.printBoard();
	}
	
	
}

