import java.util.*;
import java.io.*;

public class Playout{

	void playRandomGame(Board board, int first_stone){
		ArrayList<Point> free_points;
		Random random = new Random();
		int random_point;
		int stoneType = first_stone;
		int passTimes = 0;
		int maxMoves = 200;
		Point p;

		for (int movesCount = 0; movesCount < maxMoves && passTimes < 2; stoneType = Board.getOppositeSide(stoneType), movesCount++){
			free_points = getFreePoints(board, stoneType);
			
			if (free_points.size() == 0){
				passTimes++;
				continue;
			}
			passTimes = 0;

			random_point = random.nextInt(free_points.size());
			p = free_points.get(random_point);
			board.setPoint(p, stoneType);
			removeDeadStones(board);

		}
		
	}
	ArrayList<Point> getFreePoints(Board board, int stoneType){
		int i,j;
		ArrayList<Point> points = new ArrayList<Point>();
		Point p;

		for (i = 0; i < board.getSize(); i++)
			for (j = 0; j < board.getSize(); j++){
				p = new Point(i,j);
				if (board.getPoint(p) == Board.EMPTY){
					if (checkRules(p, stoneType, board))
						points.add(p);
				}
			}
		return points;
	}

	int getDameNumber(Point p, Board board){
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
	
	static boolean isFriendlySingleEyePoint(Point p, int stoneType, Board board){ 
		boolean is_friendly = true;
		if (board.getPoint(p.i+1, p.j) == Board.getOppositeSide(stoneType)) 
			return false;
		if (board.getPoint(p.i-1, p.j) == Board.getOppositeSide(stoneType))
			return false;
		if (board.getPoint(p.i, p.j+1) == Board.getOppositeSide(stoneType))
			return false;;
		if (board.getPoint(p.i, p.j-1) == Board.getOppositeSide(stoneType))
			return false;

		return true;
	}


	boolean checkRules(Point p, int stoneType, Board board){

		//Create new board copy to change existing board state without changes in the main board
		Board new_board = new Board(board);
		
		ArrayList<Point> points_to_delete = new ArrayList<Point>(), points;
		Point[] surroundedStones = {	new Point(p.i-1, p.j),	//up 
										new Point(p.i+1, p.j),	//down	
										new Point(p.i, p.j+1),	//right
										new Point(p.i, p.j-1)	//left 
									};

		new_board.setPoint(p.i, p.j, stoneType);
		//new_board.printBoard();

		if (getDameNumber(p, board) != 0) 
			return true;
		if (isFriendlySingleEyePoint(p, stoneType, board))
			return false;
		if (new_board.isKO())
			return false;

		
		 //posible suicide move
		if (isGroupDead(new_board, p) == null){
			return true;
		}

		//check could we kill neigbour enemy groups with this move
		for (Point next: surroundedStones){
			
			if (board.getPoint(next) == Board.getOppositeSide(stoneType)){ //TODO: Always enemy?
				//System.out.printf("\nNeigbour [%d:%d]\n", next.i, next.j);
				if (isGroupDead(new_board, next) != null)
					return true;
				
			}

		}

		return false;
	}
	void removeDeadStones(Board board){
		int i,j;
		Point point;
		ArrayList<Point> deleteThisStones;
		for(i = 0; i < board.getSize(); i++){
			for(j = 0; j < board.getSize(); j++){
				point = new Point(i,j);
				if (board.getPoint(point) == Board.FRIENDLY || board.getPoint(point) == Board.ENEMY){
					deleteThisStones = isGroupDead(board, point);
					if (deleteThisStones != null){
						System.out.println("Deleted stones: ");
						for (Point stone: deleteThisStones){
							board.setPoint(stone, Board.EMPTY);
							System.out.printf("[%d,%d] ",stone.i, stone.j);
						}
						System.out.println();
					}
				}
			
			}
		}
	}
	ArrayList<Point> isGroupDead (Board board, Point p){
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
	boolean isPointVisited(ArrayList<Point> visited, LinkedList<Point> queue, Point p){
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