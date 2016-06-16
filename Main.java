import java.util.*;
import java.io.*;
/*
TODO
Change FRIENDLY and ENEMY constants to variable
Name stone and color as stone_type for readability
Start random playout
*/

public class Main{
	public static void main(String[] args){
	
		Board board = new Board(9);
		Point p;

		board.loadFromFile("board9x9.dat");
		board.printBoard();
		if (checkRules(new Point(4,2), board))
			System.out.println("\nLegal move");
		else
			System.out.println("\nIllegal move");
		removeDeadStones(board);
		board.printBoard();



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
			
			
			stone = board.getOppositeSide(stone)
			
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
		if (board.getPoint(p.i+1, p.j) == Board.ENEMY) //TODO: Not always enemy!
			return false;
		if (board.getPoint(p.i-1, p.j) == Board.ENEMY)
			return false;
		if (board.getPoint(p.i, p.j+1) == Board.ENEMY)
			return false;;
		if (board.getPoint(p.i, p.j-1) == Board.ENEMY)
			return false;

		return true;
	}


	static boolean checkRules(Point p, Board board){

		//Create new board copy to change existing board state without changes in main board
		Board new_board = new Board(board);
		
		ArrayList<Point> points_to_delete = new ArrayList<Point>(), points;
		Point[] surroundedStones = {	new Point(p.i-1, p.j),	//up 
										new Point(p.i+1, p.j),	//down	
										new Point(p.i, p.j+1),	//right
										new Point(p.i, p.j-1)	//left 
									};

		new_board.setPoint(p.i, p.j, Board.FRIENDLY); //TODO: Not always friendly!!! 
		new_board.printBoard();

		if (getDameNumber(p, board) != 0) 
			return true;
		if (isFriendlySingleEyePoint(p, board))
			return false;
		if (new_board.isKO())
			return false;

		
		 //posible suicide move
		if (isGroupDead(new_board, p) == null){
			return true;
		}

		//check could we kill neigbour enemy groups with this move
		for (Point next: surroundedStones){
			
			if (board.getPoint(next) == Board.ENEMY){ //TODO: Always enemy?
				System.out.printf("\nNeigbour [%d:%d]\n", next.i, next.j);
				if (isGroupDead(new_board, next) != null)
					return true;
				
			}

		}

		return false;
	}
	static void removeDeadStones(Board board){
		int i,j;
		Point point;
		ArrayList<Point> deleteThisStones;
		for(i = 0; i < board.getSize(); i++){
			for(j = 0; j < board.getSize(); j++){
				point = new Point(i,j);
				if (board.getPoint(point) == Board.FRIENDLY || board.getPoint(point) == Board.ENEMY){
					deleteThisStones = isGroupDead(board, point);
					if (deleteThisStones != null){
						for (Point stone: deleteThisStones){
							board.setPoint(stone, Board.EMPTY);
						}
					}
				}
			
			}
		}
	}
	static  ArrayList<Point> isGroupDead (Board board, Point p){
		int i,j;
		int dame_number;
		Point point,up, down, left, right;
		LinkedList<Point> queue = new LinkedList<Point>();
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
				//System.out.printf("%d %d\n",up.i, up.j);
			}
			right = new Point(point.i, point.j+1);
			if ((board.getPoint(point) == board.getPoint(right)) && !isPointVisited(visited, queue, right)){
				queue.add(right);
				//System.out.printf("%d %d\n",right.i, right.j);
			}

			down = new Point(point.i+1, point.j);
			if ((board.getPoint(point) == board.getPoint(down)) && !isPointVisited(visited, queue, down)){
				queue.add(down);
				//System.out.printf("%d %d\n",down.i, down.j);
			}
			left = new Point(point.i, point.j-1);
			if ((board.getPoint(point) == board.getPoint(left)) && !isPointVisited(visited, queue, left)){
				queue.add(left);
				//System.out.printf("%d %d\n",left.i, left.j);
			}

		}
		//System.out.printf("\nDames:%d", dame_number);

		if (dame_number == 0){
			//System.out.println("Group is dead");
			return visited;
		}
		
		return null;
		

	}
	static boolean isPointVisited(ArrayList<Point> visited, LinkedList<Point> queue, Point p){
		Iterator<Point> it_v = visited.iterator();
		Iterator<Point> it_q = queue.iterator();
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

