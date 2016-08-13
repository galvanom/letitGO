package com.letitgo;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import com.letitgo.heuristics.*;

public class Main{
	public static void main(String[] args){
		/*
		Board board = new Board(9);
		Point p = null;
		Playout playout = new Playout();
		int stoneType = Board.ENEMY;
		Heuristics hr = new Heuristics();
		Montecarlo mc;
		long startTime;

		board.loadFromFile("board9x9.dat");
		board.printBoard();

		startTime = System.currentTimeMillis();
		mc = new Montecarlo(board, Board.FRIENDLY);	
		for (int i = 0; i < 1000; i++){
			if (i%100 == 0){
				System.gc();
			}
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
			//board.printBoard();
			//mc.printTree();
		}		
	*/
		Pattern33 pat = new Pattern33();
		Pattern33.stringToNumber("XO.#");
		char[] pattern = {'X','O','?','X','.','.','x','.','?'};
		char[] pattern_ = {'.','O','.','X','.','.','.','.','.'};
		pat.getAllVariations(pattern,0);

	}
	
	
}

