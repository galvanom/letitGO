package com.letitgo;
import java.io.*;

public class ConsoleInterface{
	protected static final String match = "abcdefghjklmnopqrstuvwxyz";
	protected Board board;
	protected int playoutsNum;
	protected int aiStone;
	protected double komi;


	public void setParam(int boardSize, int aiStone, double komi, int playoutsNum){
		this.board = new Board(boardSize);
		this.aiStone = aiStone;
		this.komi = komi;
		this.playoutsNum = playoutsNum;
	}

	public void start(){
		String input;
        BufferedReader stdIn;
        Point p;
        Montecarlo mc;
        int currentMove = Board.ENEMY;
        if (board == null){
        	System.out.printf("Board wasn't initialized\n");
        	return;
        }
        System.out.printf("\n*LetItGo engine*\n\n");
        System.out.printf("Board size: %d\n", board.getSize());
        System.out.printf("Komi %f\n", this.komi);
        System.out.printf("Playouts per move %d\n", this.playoutsNum);
        if (aiStone == Board.ENEMY){
        	System.out.printf("AI plays for black\n");
        }
        if (aiStone == Board.FRIENDLY){
        	System.out.printf("AI plays for white\n");
        }

        System.out.printf("\nFor move type coordinate. For example b6\nFor pass type pass\nFor resign type quit\n\n");
        showBoard();

        try{
            stdIn = new BufferedReader(new InputStreamReader(System.in));

            while (true){
            	if (currentMove == aiStone){
            		System.out.printf("AI is searching for a move\nIt can take some time\n");
            		mc = new Montecarlo(board, Board.getOppositeSide(aiStone));
            		for (int i = 0; i < playoutsNum; i++){
						mc.playOneSequence();
					}
					p = mc.getWinner();
					board.makeMove(p, aiStone);
            	}
            	else{
            		System.out.print("Next move:\n>");
                	input = stdIn.readLine();
                	p = strToPoint(input, board.getSize());
                	board.makeMove(p, Board.getOppositeSide(aiStone));
            	}
            	showBoard();
            	currentMove = Board.getOppositeSide(currentMove);
            }
        }

        catch (IOException e){
            e.printStackTrace();
        }
	}

	public boolean showBoard(){
		if (board == null){
			return false;
		}

		int boardSize = board.getSize();
		int i, j;
		int starsShift = 4;
		System.out.println("O - white stones");
		System.out.println("X - black stones\n");
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
						System.out.print("O ");
						break;
					case Board.ENEMY:
						System.out.print("X ");
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