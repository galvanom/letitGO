import java.io.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
		int firstPoint;
		int secondPoint;
		Playout pl = new Playout();
		
		pl.removeDeadStones(board, Board.ENEMY);
		firstPoint =  board.getPoint(2,6);
		secondPoint = board.getPoint(3,6);
		assertTrue("removeDeadStonesTest1 failed", firstPoint == Board.EMPTY && secondPoint == Board.EMPTY);

	}
	@Test
	public void removeDeadStonesTest2(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		int point;
		Playout pl = new Playout();
		
		pl.removeDeadStones(board, Board.FRIENDLY);
		point = board.getPoint(0,0);
		assertTrue("removeDeadStonesTest2 failed", point == Board.EMPTY);
	}
	@Test
	public void koTest(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		Playout pl = new Playout();
		pl.removeDeadStones(board, Board.FRIENDLY);
		pl.removeDeadStones(board, Board.ENEMY);

		board.setPoint(8,6, Board.ENEMY);
		pl.removeDeadStones(board, Board.FRIENDLY);
		
		//board.setKO(new Point(8,7));
		//System.out.printf("[%d %d]", board.koPoint.i, board.koPoint.j);

		assertTrue("KO test failed", pl.checkRules(new Point(8,7), Board.FRIENDLY, board) == false);
		//board.printBoard();
	}
	@Test
	public void checkRulesTest1(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		Playout pl = new Playout();
		assertTrue("checkRulesTest1 failed", pl.checkRules(new Point(5,2), Board.FRIENDLY, board) == false);
	}
	@Test
	public void checkRulesTest2(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		Playout pl = new Playout();
		assertTrue("checkRulesTest2 failed", pl.checkRules(new Point(0,8), Board.FRIENDLY, board) == false);
	}
	@Test
	public void isFriendlySingleEyePointTest(){
		Board board = new Board(9);
		board.loadFromFile("board9x9_1.dat");
		Playout pl = new Playout();
		assertTrue("isFriendlySingleEyePoint failed", pl.isFriendlySingleEyePoint(new Point(5,2), Board.ENEMY, board) == true);
	}

}
