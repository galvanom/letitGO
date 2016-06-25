import java.util.*;
import java.io.*;

public class Playout{
	//TODO: return winner
	int playRandomGame(final Board board, int first_stone){
		ArrayList<Point> free_points;
		Random random = new Random();
		int random_point;
		int stoneType = first_stone;
		int passTimes = 0;
		int MAX_MOVES = 200;
		Point p;
		Board playBoard = new Board(board);

		for (int movesCount = 0; movesCount < MAX_MOVES && passTimes < 2; stoneType = Board.getOppositeSide(stoneType), movesCount++){
			free_points = getFreePoints(playBoard, stoneType);
			
			if (free_points.size() == 0){
				passTimes++;
				continue;
			}
			passTimes = 0;

			random_point = random.nextInt(free_points.size());
			p = free_points.get(random_point);
			//playBoard.saveBoardState(); //for KO
			makeMove(playBoard, p, stoneType);

		}

		int[] score = getScore(playBoard);
		return score[0] > score[1] ? Board.FRIENDLY : Board.ENEMY; //TODO: komi is not used
		
	}
	void makeMove(Board board, Point p, int stoneType){
		board.setPoint(p, stoneType);
		removeDeadStones(board, Board.getOppositeSide(stoneType));
	}
	ArrayList<Point> getFreePoints(Board board, int stoneType){
		int i,j;
		ArrayList<Point> points = new ArrayList<Point>();
		Point p;

		for (i = 0; i < board.getSize(); i++)
			for (j = 0; j < board.getSize(); j++){
				p = new Point(i,j);
				if (board.getPoint(p) == Board.EMPTY){
					//System.out.printf ("b");
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

		
		//new_board.printBoard();

		if (getDameNumber(p, board) != 0) 
			return true;
		if (isFriendlySingleEyePoint(p, stoneType, board))
			return false;
		if (board.isKO(p)){
			//System.out.printf("\nKO [%d,%d]\n", p.i,p.j);
			return false;
		}

		/*
		//KO check
		new_board.saveBoardState();
		new_board.setPoint(p, stoneType);
		//System.out.printf("[%d,%d]", p.i,p.j);
		//new_board.printBoard();
		removeDeadStones(new_board, Board.getOppositeSide(stoneType));
		if (board.matchBoardState(new_board)){ //KO
			//System.out.println("KO");
			return false;
		}
		else{
			//System.out.println("NewBoard:");
			//new_board.printBoard();
			new_board.loadBoardState();
		}
		*/
		new_board.setPoint(p, stoneType);

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

	void removeDeadStones(Board board, int stoneType){
		int i,j, delStonesNumber = 0;
		Point point, lastPoint = null;
		ArrayList<Point> deleteThisStones;
		for(i = 0; i < board.getSize(); i++){
			for(j = 0; j < board.getSize(); j++){
				point = new Point(i,j);
				if (board.getPoint(point) == stoneType){
					deleteThisStones = isGroupDead(board, point);
					if (deleteThisStones != null){
						//System.out.println("\nDeleted stones: ");
						delStonesNumber += deleteThisStones.size();
						lastPoint = point; //possible ko point
							    

						for (Point stone: deleteThisStones){
							board.setPoint(stone, Board.EMPTY);
							//System.out.printf("[%d,%d] ",stone.i, stone.j);
						}
						//System.out.println();
					}
				}
			
			}
		}
		if (delStonesNumber == 1 && lastPoint != null){
			board.setKO(lastPoint);
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
	int[] getScore(Board board){
		int i,j, friendScore = 0, enemyScore = 0;
		int surroundedStones[] = new int[4];
		Point p;
		boolean isFriendly;


		for (i = 0; i < board.getSize(); i++){
			for (j = 0; j < board.getSize(); j++){
				p = new Point(i,j);
				if (board.getPoint(p) == Board.EMPTY){
					surroundedStones[0] = board.getPoint(i-1,j);
					surroundedStones[1] = board.getPoint(i+1,j);
					surroundedStones[2] = board.getPoint(i,j-1);
					surroundedStones[3] = board.getPoint(i,j+1);

					isFriendly = true;
					for (int pointType: surroundedStones){
						if (pointType != Board.FRIENDLY && pointType != Board.BORDER){
							isFriendly = false;
							break;
						}
					}

					if (isFriendly)
						friendScore++;
					else
						enemyScore++;


				}
			}
		}
		System.out.printf("\nFriend score: %d Enemy score: %d", friendScore, enemyScore);

		int[] score = {friendScore, enemyScore};
		return score;
		
	}
}