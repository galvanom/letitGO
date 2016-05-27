import java.util.*;

public class Main{
	public static void main(String[] args){
	
		Board board = new Board(9);

		board.printBoard();
		playOut(board, Board.FRIENDLY);
		
		board.setPoint(new Point(2,1),Board.EMPTY);
		board.setPoint(new Point(1,2),Board.EMPTY);
		board.printBoard();
		System.out.printf("Dame number: %d",getDameNumber(new Point(1,1), board));

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
		Point p;

		for (i = 0; i < board.getSize(); i++)
			for (j = 0; j < board.getSize(); j++){
				p = new Point(i,j);
				if (board.getPoint(p) == Board.EMPTY){
					points.add(p);
				}
			}
		return points;
	}
	static int getDameNumber(Point p, Board board){
		int dame_count = 0;
		if (board.getPoint(new Point(p.x+1, p.y)) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(new Point(p.x-1, p.y)) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(new Point(p.x, p.y+1)) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(new Point(p.x, p.y-1)) == Board.EMPTY)
			dame_count++;

		return dame_count;

	}

}

