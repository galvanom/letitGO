package com.letitgo;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class Main{
	public static void main(String[] args){
		/*
		Board board = new Board(9);
		Point p = null;
		Playout playout = new Playout();
		int stoneType = Board.ENEMY;
		
		//board.loadFromFile("patterns_test1.dat");	
		board.printBoard();



		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1; i++){
			if (i%1000 == 0){
				System.gc();
			}
			playout.playRandomGame(board, Board.FRIENDLY);


		}
		System.out.println(System.currentTimeMillis() - startTime);
		
	*/
		Board board = new Board(9);
		board.loadFromFile("board9x9.dat");
		Group group = board.getGroup(new Point(board, 1, 1));


	}
	
	
}

