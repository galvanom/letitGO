import java.util.*;
import java.io.*;

public class Main{
	public static void main(String[] args){
	
		Board board = new Board(9);
		Point p;

		board.loadFromFile("board9x9.dat");
		board.printBoard();
		couldMoveGetDame(new Point(1,1), board);
		//playOut(board, Board.FRIENDLY);

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
			board.setPoint(p.i, p.j, stone);
			
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
		if (board.getPoint(p.i+1, p.j) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(p.i-1, p.j) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(p.i, p.j+1) == Board.EMPTY)
			dame_count++;
		if (board.getPoint(p.i, p.j-1) == Board.EMPTY)
			dame_count++;

		return dame_count;
	}
	static boolean isFriendlySingleEyePoint(Point p, Board board){
		boolean is_friendly = true;
		if (board.getPoint(p.i+1, p.j) == Board.ENEMY)
			is_friendly = false;
		if (board.getPoint(p.i-1, p.j) == Board.ENEMY)
			is_friendly = false;
		if (board.getPoint(p.i, p.j+1) == Board.ENEMY)
			is_friendly = false;;
		if (board.getPoint(p.i, p.j-1) == Board.ENEMY)
			is_friendly = false;

		return is_friendly;
	}


	static boolean couldMoveGetDame(Point p, Board board){
		Board new_board = new Board(board);

		new_board.setPoint(p.i, p.j, Board.FRIENDLY); //TODO: Always friendly?
		removeDeadStones(new_board);
		if (getDameNumber(p, new_board) == 0)
			return false;
		/*if (board.isKO(new_board))
			return false;*/

		return true;
	}
	static void removeDeadStones(Board board){
		int i,j;
		int dame_number;
		Point point, up, down, left, right;
		Point p = new Point(2,3);
		PriorityQueue<Point> queue = new PriorityQueue<Point>();
		ArrayList<Point> visited = new ArrayList<Point>();
		queue.add(p);
		dame_number = 0;

		while (queue.size() > 0){
			point = queue.poll();
			visited.add(point);
			dame_number += getDameNumber(point,board);
			
			up = new Point(point.i-1, point.j);
			if ((board.getPoint(point) == board.getPoint(up)) && !isPointVisited(visited, queue, up)){
				queue.add(up);
			}

		}

	}
	static boolean isPointVisited(ArrayList visited, PriorityQueue queue, Point p){
		Iterator it_v = visited.iterator();
		Iterator it_q = queue.iterator();

		//System.out.println(it_v.next().getClass().toString());
		while(it_v.hasNext()){
			if (p.isEqualsTo(it_v.next()))
				return true;
		}
		while(it_q.hasNext()){
			if (p.isEqualsTo(it_q.next()))
				return true;
		}

		return false;
	}
}

