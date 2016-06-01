import java.util.*;

public class Main{
	public static void main(String[] args){
	
		Board board = new Board(9);
		Point p;

		board.printBoard();
		playOut(board, Board.FRIENDLY);

		//<isFriendlySingleEyePoint() test>
		/*
		board.setPoint(0,1, Board.FRIENDLY);
		board.setPoint(2,1, Board.FRIENDLY);
		board.setPoint(1,0, Board.FRIENDLY);
		board.setPoint(1,2, Board.FRIENDLY);
		p = new Point(1,1);
		if (getDameNumber(p, board) == 0){
			boolean is_friendly = isFriendlySingleEyePoint(p, board);
			if (is_friendly)
				System.out.printf("The single eye point is FRIENDLY\n");
			else
				System.out.printf("The single eye point is ENEMY\n");
		}
		*/
		//</isFriendlySingleEyePoint() test>

	}
	
	static void playOut(Board board, int first_stone){
		ArrayList<Point> free_points;
		Random random = new Random();
		int random_point;
		int stone = first_stone;
		Point p;

		free_points = getFreePoints(board);
		while (!free_points.isEmpty()){
			random_point = random.nextInt(free_points.size());
			p = free_points.get(random_point);
			board.setPoint(p.x, p.y, stone);
			
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
				if (board.getPoint(i,j) == Board.EMPTY){
					points.add(p);
				}
			}
		return points;
	}
	static int getDameNumber(Point p, Board board){
		int dame_count = 0;
		if (board.getPoint(p.x+1, p.y) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(p.x-1, p.y) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(p.x, p.y+1) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(p.x, p.y-1) == Board.EMPTY)
			dame_count++;

		return dame_count;
	}
	static boolean isFriendlySingleEyePoint(Point p, Board board){
		boolean is_friendly = true;
		if (board.getPoint(p.x+1, p.y) == Board.ENEMY)
			is_friendly = false;
		if (board.getPoint(p.x-1, p.y) == Board.ENEMY)
			is_friendly = false;
		if (board.getPoint(p.x, p.y+1) == Board.ENEMY)
			is_friendly = false;;
		if (board.getPoint(p.x, p.y-1) == Board.ENEMY)
			is_friendly = false;

		return is_friendly;
	}


	static boolean couldMoveGetDame(Point p, Board board){
		Board new_board = new Board(board);

		new_board.setPoint(p.x, p.y, Board.FRIENDLY); //TODO: Always friendly?
		removeDeadStones(new_board);
		if (getDameNumber(p, new_board) == 0)
			return false;
		/*if (board.isKO(new_board))
			return false;*/

		return true;
	}
	static void removeDeadStones(Board board){
		int i,j;

	}
}

