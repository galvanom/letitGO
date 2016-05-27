import java.util.*;

public class Main{
	public static void main(String[] args){
	
		Board board = new Board(9);

		board.printBoard();
		playOut(board, Board.FRIENDLY);
		board.printBoard();
	}
	
	static void playOut(Board board, int first_stone){
		ArrayList<Point> free_points;
		Random random = new Random();
		int random_point;
		int stone = first_stone;
		
		free_points = getFreePoints(board);
		while (!free_points.isEmpty()){
			random_point = random.nextInt(free_points.size());
			board.setPoint(free_points.get(random_point), stone);
			
			if (stone == Board.FRIENDLY)
				stone = Board.ENEMY;
			else
				if (stone == Board.ENEMY)
					stone = Board.FRIENDLY;

			free_points = getFreePoints(board);

		}
		
	}
	static ArrayList<Point> getFreePoints(Board board){
		int i,j;
		ArrayList<Point> points = new ArrayList<Point>();
		for (i = 0; i < board.getSize(); i++)
			for (j = 0; j < board.getSize(); j++)
				if (board.getPoint(new Point(i,j)) == Board.EMPTY)
					points.add(new Point(i,j));
		return points;
	}
}

