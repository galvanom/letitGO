import java.util.*;
import java.io.*;

public class Board{
	int[][] board, boardState;
	int size;
	public static final int EMPTY = 0;
	public static final int BORDER = 1;
	public static final int FRIENDLY= 2;
	public static final int ENEMY = 3;

	Board(){};

	Board(int size){
		int i, j;
		this.size = size+2;
		board = new int[this.size][this.size];
		for (i = 0; i < this.size; i++)
			for (j = 0; j < this.size; j++)
				board[i][j] = EMPTY;
		for (i = 0; i < this.size; i++){
			board[0][i] = BORDER;
			board[i][0] = BORDER;
			board[this.size-1][i] = BORDER;
			board[i][this.size-1] = BORDER;

		}
	}
	Board(Board otherBoard){
		int i,j;
		this.size = otherBoard.getSize()+2;
		this.board = new int[this.size][this.size];
		
		for (i = 0; i < this.size; i++){
			board[0][i] = BORDER;
			board[i][0] = BORDER;
			board[this.size-1][i] = BORDER;
			board[i][this.size-1] = BORDER;

		}
		for (i = 0; i < this.size-2; i++)
			for (j = 0; j < this.size-2; j++)
				this.board[i+1][j+1] = otherBoard.getPoint(i,j);

	}
	void printBoard(){
		int i, j, k;
		System.out.print("  ");
		for (k = 0; k < size-2; k++){
			System.out.printf("%d ", k);
		}
		System.out.println();
		for (i = 0; i < size; i++){

			for (j = 0; j < size; j++){
				switch (board[i][j]) {
					case EMPTY:
						System.out.print(".");
						break;
					case BORDER:
						System.out.print("#");
						break;
					case FRIENDLY:
						System.out.print("O");
						break;
					case ENEMY:
						System.out.print("X");
						break;
				}
				System.out.print(" ");
			}
		if ( i != 0 && i != size-1){
			System.out.print(i-1);
			}	
		System.out.println();
		}
		System.out.println(board.hashCode());
	}
	void loadFromFile(String filename){
		

		int i,j;
		try {
			File file = new File(filename);
			Scanner sc = new Scanner(file);
			for (i = 0; sc.hasNextLine(); i++){
				for (j = 0; j < this.size-2; j++)
					board[i+1][j+1] = sc.nextInt();
			}
		}
		catch (FileNotFoundException e) {
        	e.printStackTrace();
        }

    

	}
	boolean setPoint(int i, int j, int stone){
		saveBoardState();				//for KO
		board[i + 1][j + 1] = stone;
		return true;
	}
	boolean setPoint(Point p, int stone){
		saveBoardState();				//for KO
		board[p.i + 1][p.j + 1] = stone;
		return true;
	}
	int getPoint(int i, int j){
		return board[i + 1][j + 1];
	}
	int getPoint(Point p){
		return board[p.i + 1][p.j + 1];
	}

	int getSize(){
		return size - 2;
	}
	void saveBoardState(){
		boardState = new int[this.size][this.size];
		for (i = 0; i < this.size; i++)
			for (j = 0; j < this.size; j++)
				boardState[i][j] = board[i][j];
	}
	boolean matchBoardState(){
		if (boardState == null)
			return false;
		for (i = 0; i < this.size; i++)
			for (j = 0; j < this.size; j++)
				if (boardState[i][j] != board[i][j])
					return false;
		return true;
	}
	boolean isKO(){
		return matchBoardState();
	}

}