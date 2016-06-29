import java.util.*;
import java.io.*;

public class Board{
	int[][] board, boardState;
	int size;
	Point koPoint = null;
	int koPointLifeTime = 0;
	Point lastPoint = null;
	//Point types
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
		System.out.print("\n  ");
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
		
	}
	//TODO: Gavnokod!!!
	void loadFromFile(String filename){
		int i,j;
		char point;
		int c;
		try{
			InputStream in = new FileInputStream(new File(filename));
			for (i = 0, j = 0, c = in.read(); c != -1 && i < this.size -2; c = in.read()){ 
				if ((char)c == ' ')
					continue;

				switch ((char)c){
					case '.':
						board[i+1][j+1] = Board.EMPTY;
						j++;
						break;
					case 'X':
						board[i+1][j+1] = Board.ENEMY;
						j++;
						break;
					case 'O':
						board[i+1][j+1] = Board.FRIENDLY;
						j++;
						break;
					default:
					break;
				}
				if (j == this.size - 2){
					i++; j = 0;
				}
			}
		}

		catch(IOException e){
			System.out.println("File read error");
		}

	}
	boolean setPoint(int i, int j, int pointType){
		if (pointType == Board.FRIENDLY || pointType == Board.ENEMY){
			koPointLifeTime++; //for KO
			lastPoint = new Point(i,j);
		}

		board[i + 1][j + 1] = pointType;
		return true;
	}
	boolean setPoint(Point p, int pointType){
		if (pointType == Board.FRIENDLY || pointType == Board.ENEMY){ 
			koPointLifeTime++;//for KO
			lastPoint = p;
		}

		board[p.i + 1][p.j + 1] = pointType;
		return true; //TODO: Boolean ?
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
		int i,j;
		boardState = new int[this.size][this.size];
		for (i = 0; i < this.size; i++)
			for (j = 0; j < this.size; j++)
				boardState[i][j] = board[i][j];
	}
	void loadBoardState(){
		int i,j;
		if (boardState == null)
			return;
		for (i = 0; i < this.size; i++)
			for (j = 0; j < this.size; j++)
				board[i][j] = boardState[i][j];

	}
	boolean matchBoardState(Board otherBoard){
		int i,j;
		if (boardState == null)
			return false;
		for (i = 0; i < otherBoard.getSize(); i++)
			for (j = 0; j < otherBoard.getSize(); j++)
				if (boardState[i+1][j+1] != otherBoard.getPoint(i,j))
					return false;
		return true;
	}
	static int getOppositeSide(int stone){
		if (stone == FRIENDLY)
			return ENEMY;
			
		return FRIENDLY;
	}
	void setKO(Point point){
		koPoint = point;
		koPointLifeTime = 0;

	}
	boolean isKO(Point point){
		if (koPointLifeTime == 0 && koPoint != null){
			if (koPoint.i == point.i && koPoint.j == point.j){
				return true;
			}
		}
		return false;
	}
	Point getLastPoint(){
		return lastPoint;
	}

}