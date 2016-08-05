package com.letitgo.tests;
import com.letitgo.Board;
import com.letitgo.Point;
import com.letitgo.heuristics.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class HeuristicsTest{

	//Test killing the enemy group
	@Test
	public void getHeuristicMoveTest1(){
		Board board = new Board(9);
		Heuristics hr = new Heuristics();
		Point p;
		board.loadFromFile("HeuristicsTest_a.dat");
		board.setLastPoint(new Point(board,2,0));
		
		p = hr.capture.getFirstMove(board, Board.FRIENDLY);

		//System.out.printf("Point %d,%d\n", p.i, p.j);
		assertTrue("getHeuristicMoveTest1 for FRIENDLY failed. Point is Null", p != null );
		assertTrue("getHeuristicMoveTest1 for FRIENDLY failed. Not expected behavior", p.i == 2 && p.j == 0);

		p = hr.capture.getFirstMove(board, Board.ENEMY);
		//System.out.printf("Point %d,%d\n", p.i, p.j);
		assertTrue("getHeuristicMoveTest1 for ENEMY failed. Point is Null", p != null );
		assertTrue("getHeuristicMoveTest1 for ENEMY. Not expected behavior", p.i == 0 && p.j == 1);
	}
	
	@Test
	public void getHeuristicMoveTest2(){
		Board board = new Board(9);
		Heuristics hr = new Heuristics();
		Point p;
		board.loadFromFile("HeuristicsTest_a.dat");
		board.setLastPoint(new Point(board,6,7));

		p = hr.capture.getFirstMove(board, Board.FRIENDLY);
		
		//System.out.printf("Point %d,%d\n", p.i, p.j);
		assertTrue("getHeuristicMoveTest2 for FRIENDLY failed. Point is Null", p != null );
		assertTrue("getHeuristicMoveTest2 for FRIENDLY failed. Not expected behavior", p.i == 6 && p.j == 8);

		p = hr.capture.getFirstMove(board, Board.ENEMY);
		//System.out.printf("Point %d,%d\n", p.i, p.j);
		assertTrue("getHeuristicMoveTest2 for ENEMY failed. Point is Null", p != null );
		assertTrue("getHeuristicMoveTest2 for ENEMY. Not expected behavior", p.i == 6 && p.j == 7);
	}
	
	@Test
	public void getHeuristicMoveTest3(){
		Board board = new Board(9);
		Heuristics hr = new Heuristics();
		Point p;

		board.loadFromFile("HeuristicsTest_b.dat");
		board.setLastPoint(new Point(board,4,2));
		p = hr.capture.getFirstMove(board, Board.FRIENDLY);
		
		//System.out.printf("Point %d,%d\n", p.i, p.j);
		assertTrue("getHeuristicMoveTest3 for FRIENDLY failed. Point is Null", p != null );
		assertTrue("getHeuristicMoveTest3 for FRIENDLY failed. Not expected behavior", p.i == 2 && p.j == 1);

		board.setLastPoint(new Point(board,1,2));
		p = hr.capture.getFirstMove(board, Board.ENEMY);

		//System.out.printf("Point %d,%d\n", p.i, p.j);
		assertTrue("getHeuristicMoveTest3 for ENEMY failed. Point is Null", p != null );
		assertTrue("getHeuristicMoveTest3 for ENEMY. Not expected behavior", p.i == 3 && p.j == 1);
	}

	@Test
	public void getHeuristicMoveTest4(){
		Board board = new Board(9);
		Heuristics hr = new Heuristics();
		Point p;

		board.loadFromFile("HeuristicsTest_c.dat");
		board.setLastPoint(new Point(board,1,3));
		p = hr.capture.getFirstMove(board, Board.FRIENDLY);
		
		//System.out.printf("Point %d,%d\n", p.i, p.j);
		assertTrue("getHeuristicMoveTest4 for FRIENDLY failed. Point is Null", p != null );
		assertTrue("getHeuristicMoveTest4 for FRIENDLY failed. Not expected behavior", p.i == 2 && p.j == 4);

	}
	//ladder test
	@Test
	public void getHeuristicMoveTest5(){
		Board board = new Board(9);
		Heuristics hr = new Heuristics();
		Point p;

		board.loadFromFile("HeuristicsTest_d.dat");
		board.setLastPoint(new Point(board,3,4));
		p = hr.capture.getFirstMove(board, Board.ENEMY);
		
		//System.out.printf("Point %d,%d\n", p.i, p.j);
		assertTrue("getHeuristicMoveTest5 for ENEMY failed. Point is not Null", p == null );
	}
	//self-atari
	@Test
	public void getHeuristicMoveTest6(){
		Board board = new Board(9);
		Heuristics hr = new Heuristics();
		Point p;

		board.loadFromFile("HeuristicsTest_e.dat");
		board.setLastPoint(new Point(board,8,8));

		p = hr.capture.getFirstMove(board, Board.ENEMY);
		
		//System.out.printf("Point %d,%d\n", p.i, p.j);
		assertTrue("getHeuristicMoveTest6 for ENEMY failed. Point is not Null", p == null );
	}
	//3x3 patterns
	@Test
	public void Patterns33Test(){
		Board board = new Board(9);
		Heuristics hr = new Heuristics();
		Point p;

		board.loadFromFile("HeuristicsTest_f.dat");
				
		assertTrue("Patterns test failed\n", hr.pattern33.isPattern3x3(board, new Point(board,1,1)));
		assertTrue("Patterns test failed\n", hr.pattern33.isPattern3x3(board, new Point(board,1,4)));
		assertTrue("Patterns test failed\n", hr.pattern33.isPattern3x3(board, new Point(board,1,7)));
		assertTrue("Patterns test failed\n", hr.pattern33.isPattern3x3(board, new Point(board,4,1)));
		assertTrue("Patterns test failed\n", hr.pattern33.isPattern3x3(board, new Point(board,4,4)));
		assertTrue("Patterns test failed\n", hr.pattern33.isPattern3x3(board, new Point(board,4,7)));
		assertTrue("Patterns test failed\n", !hr.pattern33.isPattern3x3(board, new Point(board,7,1)));

	}



}