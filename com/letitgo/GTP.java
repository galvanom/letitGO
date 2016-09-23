package com.letitgo;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class GTP extends ConsoleInterface{
	private int boardsize;
	private double komi;
	private static final int PLAYOUTS_NUM = 5000;
	private final String[] commands = {
								"boardsize",
								"list_commands",
								"name",
								"play",
								"clear_board",
								"komi",
								"quit",
								"genmove"};
	// private final String match = "abcdefghjklmnopqrstuvwxyz";
	// private Board board;
	private Montecarlo mc;

	public GTP(){
		boardsize = 9;
		komi = 6.5;
		board = null;
	}
    @Override
	public void start(){
		String input;
        BufferedReader stdIn;
        Point p;
        int stoneType =  Board.ENEMY;
        long startTime;
        Montecarlo.Move aiMove = null;
        String humanMove;
        boolean aiPasses = false;

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
                    continue;
                    
                }
                if (input.equals(" name")){
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
                    startTime = System.currentTimeMillis();
                    for (int i = 0; i < PLAYOUTS_NUM; i++){
						mc.playOneSequence();
					}
					// System.out.println(System.currentTimeMillis() - startTime);
					aiMove = mc.getWinner();
					if (aiMove == null || aiPasses == true){
						System.out.printf("= PASS\n\n");
						continue;
					}

					board.makeMove(aiMove,stoneType);
					// System.out.printf("Confidence level: %f\n\n", aiMove.getMoveScore());
					if (aiMove.getMoveScore() < 0.25){
						System.out.printf("= resign\n\n");
						continue;
					}
					System.out.printf("= %s\n\n", pointToStr(aiMove.i, aiMove.j,board.getSize()));
					continue;
                }
                 if (input.startsWith("play")){
                 	humanMove = input.split(" ")[2];
                 	if (humanMove.equals("pass")){
               			if (aiMove != null && aiMove.getMoveScore() > 0.7){
               				aiPasses = true;
                    	}
               			System.out.println("=\n");
                 		continue;
                 	}


                 	p = strToPoint(humanMove,board.getSize());

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
                	this.showBoard();
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

}