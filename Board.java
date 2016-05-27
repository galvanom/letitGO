
public class Board{
	int[][] board;
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
	void printBoard(){
		int i, j;
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
		System.out.println();
		}
	}
	boolean setPoint(Point p, int stone){
		board[p.x + 1][p.y + 1] = stone;
		return true;
	}
	int getPoint(Point p){
		return board[p.x + 1][p.y + 1];
	}

	int getSize(){
		return size - 2;
	}
}