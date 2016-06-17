import java.util.*;
import java.io.*;

public class Main{
	public static void main(String[] args){
	
		Board board = new Board(9);
		Point p;
		Playout playout = new Playout();
		playout.playRandomGame(board, Board.FRIENDLY);
		board.printBoard();


		/*board.loadFromFile("board9x9.dat");
		board.printBoard();
		if (checkRules(new Point(4,2), Board.ENEMY, board))
			System.out.println("\nLegal move");
		else
			System.out.println("\nIllegal move");
		removeDeadStones(board);
		board.printBoard();*/
	}
	
	
}

