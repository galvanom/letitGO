import java.io.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

public class PlayoutTest{
	@Test
	public void initTest(){
		Playout pl = new Playout();
		assertEquals(0, pl.counter);
	}
	@Test
	public void removeDeadStonesTest1(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		int firstPoint =  board.getPoint(2,6);
		int secondPoint = board.getPoint(3,6);
		Playout pl = new Playout();
		
		pl.removeDeadStones(board, Board.ENEMY);
		assertFalse("removeDeadStonesTest1 failed", firstPoint == Board.EMPTY && secondPoint == Board.EMPTY);
	}
	@Test
	public void removeDeadStonesTest2(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		int point =  board.getPoint(0,0);
		Playout pl = new Playout();
		
		pl.removeDeadStones(board, Board.FRIENDLY);
		assertFalse("removeDeadStonesTest2 failed", point == Board.EMPTY);
	}
	@Test
	public void koTest(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		Playout pl = new Playout();

		board.setPoint(8,6, Board.ENEMY);
		pl.removeDeadStones(board, Board.FRIENDLY);
		
		System.out.printf("[%d %d]", board.koPoint.i, board.koPoint.j);

		assertFalse("KO test failed", pl.checkRules(new Point(8,7), Board.FRIENDLY, board) == true);
		//board.printBoard();
	}
	@Test
	public void checkRulesTest1(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		Playout pl = new Playout();
		assertFalse("checkRulesTest1 failed", pl.checkRules(new Point(5,2), Board.FRIENDLY, board) == true);
	}
	@Test
	public void checkRulesTest2(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		Playout pl = new Playout();
		assertFalse("checkRulesTest2 failed", pl.checkRules(new Point(0,8), Board.FRIENDLY, board) == true);
	}
	@Test
	public void isFriendlySingleEyePointTest(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		Playout pl = new Playout();
		assertFalse("isFriendlySingleEyePoint failed", pl.isFriendlySingleEyePoint(new Point(5,2), Board.ENEMY, board) == false);
	}

}
