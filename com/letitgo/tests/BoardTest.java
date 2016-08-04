package com.letitgo.tests;
import com.letitgo.Board;
import com.letitgo.Point;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BoardTest{
	@Test
	public void removeDeadStonesTest1(){
		Board board = new Board(9);
		int firstPoint;
		int secondPoint;
		
		board.loadFromFile("BoardTest_a.dat");
		board.removeDeadStones(Board.ENEMY);

		firstPoint =  board.getPoint(2, 6);
		secondPoint = board.getPoint(3, 6);

		assertTrue("removeDeadStonesTest1 failed", firstPoint == Board.EMPTY && secondPoint == Board.EMPTY);
	}
	@Test
	public void removeDeadStonesTest2(){
		Board board = new Board(9);
		board.loadFromFile("BoardTest_a.dat");
		int point;
		
		board.removeDeadStones(Board.FRIENDLY);
		point = board.getPoint(0,0);
		assertTrue("removeDeadStonesTest2 failed", point == Board.EMPTY);
	}
	@Test
	public void koTest(){
		Board board = new Board(9);
		board.loadFromFile("BoardTest_a.dat");
		board.removeDeadStones(Board.FRIENDLY);
		board.removeDeadStones(Board.ENEMY);

		board.setPoint(8,6, Board.ENEMY);
		board.removeDeadStones(Board.FRIENDLY);
		
		//board.setKO(new Point(8,7));
		//System.out.printf("[%d %d]", board.koPoint.i, board.koPoint.j);

		assertTrue("KO test failed", board.checkRules(new Point(board, 8, 7), Board.FRIENDLY) == false);
		//board.printBoard();
	}
	@Test
	public void checkRulesTest1(){
		Board board = new Board(9);
		
		board.loadFromFile("BoardTest_a.dat");
		assertTrue("checkRulesTest1 failed", board.checkRules(new Point(board, 5, 2), Board.FRIENDLY) == false);
	}
	@Test
	public void checkRulesTest2(){
		Board board = new Board(9);
		
		board.loadFromFile("BoardTest_a.dat");
		assertTrue("checkRulesTest2 failed", board.checkRules(new Point(board, 0, 8), Board.FRIENDLY) == false);
	}
}