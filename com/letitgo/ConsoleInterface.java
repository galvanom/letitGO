package com.letitgo;
import java.io.*;

public class ConsoleInterface{
	protected static final String match = "abcdefghjklmnopqrstuvwxyz";
	protected Board board = new Board(9);

	public boolean showBoard(){
		if (board == null){
			return false;
		}

		int boardSize = board.getSize();
		int i, j;
		int starsShift = 4;
		System.out.println("o - white stones");
		System.out.println("x - black stones");
		for (i = 0; i < boardSize; i++){
			System.out.printf("%c ", match.charAt(i));
		}
		System.out.println();

		if (boardSize == 9){
			starsShift = 3;
		}
		for (i = 0; i < boardSize; i++){
			for (j = 0; j < boardSize; j++){
				switch (board.getPoint(i,j)) {
					case Board.EMPTY:
						if ((boardSize - i == starsShift || i == starsShift-1) &&
						 (boardSize - j == starsShift || j == starsShift-1)){
							System.out.print("+ ");
						}
						else if (i == boardSize/2 && j == boardSize/2){
							System.out.print("+ ");
						}
						else{
							System.out.print(". ");	
						}
						
						break;
					case Board.FRIENDLY:
						System.out.print("o ");
						break;
					case Board.ENEMY:
						System.out.print("x ");
						break;

				}
			}
			System.out.print(boardSize - i);
			System.out.println();
		}
		

		return true;
	}

	public String pointToStr(int i, int j, int boardSize){
		String charCoord, digitCoord;
		digitCoord = Integer.toString(boardSize-i);
		charCoord = String.valueOf(match.charAt(j));

		return charCoord+digitCoord;
	}
	public Point strToPoint(String coord, int boardSize){
		int charCoord =-1;
		int digitCoord;
		String lowcoord = coord.toLowerCase();
		for (int i = 0; i < match.length(); i++){
			if (coord.toLowerCase().charAt(0) == match.charAt(i)){
				charCoord = i;
				break;
			}
		}
		digitCoord = (boardSize-1) - (Integer.parseInt(lowcoord.substring(1)) - 1);

		return new Point(board, digitCoord, charCoord);
	}


}