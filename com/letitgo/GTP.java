package com.letitgo;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class GTP{
	private int boardsize;
	private double komi;
	private static final int PLAYOUTS_NUM = 5000;
	private String[] commands = {
								"boardsize",
								"list_commands",
								"name",
								"play",
								"clear_board",
								"komi",
								"quit",
								"genmove"};
	private String match = "abcdefghjklmnopqrstuvwxyz";
	private Board board;
	private Montecarlo mc;

	public GTP(){
		boardsize = 9;
		komi = 6.5;
		board = null;
	}

	public void start(){
		String input;
        BufferedReader stdIn;
        Point p;
        int stoneType =  Board.ENEMY;;

        try{
            stdIn = new BufferedReader(new InputStreamReader(System.in));

            while (true){
            	// try{
            	// 	TimeUnit.SECONDS.sleep(1);
            	// }
            	// catch (InterruptedException e){

            	// }
                input = stdIn.readLine();
                if (input.equals("list_commands")){
                    System.out.print("= ");
                    for (String command : commands){
                    	System.out.println(command);
                    }
                    System.out.println();
                    continue;
                    
                }
                if (input.equals("name")){
                    System.out.print("= ");
                    System.out.println("LetItGO");
                    continue;
                }
                if (input.equals("quit")){
                    break;
                }
                if (input.startsWith("boardsize")){
                	
                    System.out.println("=\n");
                    if (input.split(" ")[1].equals("9")){
                    	boardsize = 9;
                    }
                    if (input.split(" ")[1].equals("13")){
                    	boardsize = 13;
                    }
                    if (input.split(" ")[1].equals("19")){
                    	boardsize = 19;
                    }
                    // System.out.printf("Helllo1\n");
                    continue;
                    
                }
                if (input.startsWith("komi")){

                    System.out.println("=\n");
                    continue;
                }
                if (input.startsWith("clear_board")){
                	
                	board = new Board(boardsize);
                	
                	System.out.println("=\n");
                	continue;
                }
                if (input.startsWith("genmove")){

                	if (input.split(" ")[1].equals("b")){
                    	mc = new Montecarlo(board, Board.FRIENDLY);
                    	stoneType = Board.ENEMY;
                    }
                    if (input.split(" ")[1].equals("w")){
                    	mc = new Montecarlo(board, Board.ENEMY);
                    	stoneType = Board.FRIENDLY;
                    }
                    for (int i = 0; i < PLAYOUTS_NUM; i++){
						mc.playOneSequence();
					}
					p = mc.getWinner();
					board.makeMove(p,stoneType);
					System.out.printf("= %s\n\n", pointToStr(p.i, p.j));
					continue;
                }
                 if (input.startsWith("play")){
                 	p = strToPoint(input.split(" ")[2]);

                	if (input.split(" ")[1].equals("b")){
                		board.makeMove(p, Board.ENEMY);
               	    }
                    if (input.split(" ")[1].equals("w")){
                    	board.makeMove(p, Board.FRIENDLY);
                    }
                    
                    System.out.println("=\n");
                    continue;
                    				
                }
                if (input.startsWith("showboard")){
                	board.printBoard();
                	continue;
                }

                System.out.println("?\n");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        // System.out.printf("%s\n", pointToStr(8,8));
        // strToPoint("J4").printPoint();
	}
	private String pointToStr(int i, int j){
		String charCoord, digitCoord;
		digitCoord = Integer.toString(boardsize-i);
		charCoord = String.valueOf(match.charAt(j));

		return charCoord+digitCoord;
	}
	private Point strToPoint(String coord){
		int charCoord = -1;
		int digitCoord;
		String lowcoord = coord.toLowerCase();
		for (int i = 0; i < match.length(); i++){
			if (coord.toLowerCase().charAt(0) == match.charAt(i)){
				charCoord = i;
				break;
			}
		}
		digitCoord = (boardsize -1) - (Integer.parseInt(lowcoord.substring(1)) - 1);

		return new Point(board, digitCoord, charCoord);
	}
}