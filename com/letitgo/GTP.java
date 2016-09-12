package com.letitgo;
import java.io.*;

public class GTP{
	private int boardsize;
	private double komi;
	private static final int PLAYOUTS_NUM = 100;
	private String[] commands = {
								"boardsize",
								"list_commands",
								"name",
								"play",
								"clear_board",
								"komi",
								"quit",
								"genmove"};
	private String match = "abcdefghijklmnopqrstuvwxyz";
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
        int stoneType;

        try{
            stdIn = new BufferedReader(new InputStreamReader(System.in));

            while (true){
                input = stdIn.readLine();
                if (input.equals("list_commands")){
                    System.out.print("= ");
                    for (String command : commands){
                    	System.out.println(command);
                    }
                    System.out.println();
                    
                }
                if (input.equals("name")){
                    System.out.print("= ");
                    System.out.println("LetItGO");
                }
                if (input.equals("quit")){
                    break;
                }
                if (input.startsWith("boardsize")){
                    System.out.print("= ");
                    if (input.split(" ")[1].equals("9")){
                    	boardsize = 9;
                    }
                    
                }
                if (input.startsWith("komi")){

                    System.out.println("= ");
                }
                if (input.startsWith("clear_board")){
                	board = new Board(boardsize);
                	System.out.println("= ");
                }
                if (input.startsWith("genmove")){
                	if (input.split(" ")[1].equals("b")){
                    	mc = new Montecarlo(board, Board.FRIENDLY);
                    }
                    if (input.split(" ")[1].equals("w")){
                    	mc = new Montecarlo(board, Board.ENEMY);
                    }
                    for (int i = 0; i < PLAYOUTS_NUM; i++){
						mc.playOneSequence();
					}
					p = mc.getWinner();
					System.out.printf("= %s\n", pointToStr(p.i, p.j));
					
                }
                 if (input.startsWith("play")){
                 	p = strToPoint(input.split(" ")[2]);
                	if (input.split(" ")[1].equals("b")){
                		board.makeMove(p, Board.ENEMY);
               	    }
                    if (input.split(" ")[1].equals("w")){
                    	board.makeMove(p, Board.FRIENDLY);
                    }
                    
                    System.out.println("=");
                    
					
                }

                
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
	}
	private String pointToStr(int i, int j){
		String charCoord, digitCoord;
		digitCoord = Integer.toString(i);
		charCoord = String.valueOf(match.charAt(j));

		return charCoord+digitCoord;
	}
	private Point strToPoint(String coord){
		int charCoord;
		int digitCoord;
		String lowcoord = coord.toLowerCase();
		charCoord = Character.getNumericValue(lowcoord.charAt(0)) - 49 - 1;
		digitCoord = Integer.parseInt(lowcoord.substring(1)) - 1;

		return new Point(null, digitCoord, charCoord);
	}
}