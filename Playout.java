import java.util.*;
import java.io.*;

//TODO: Save Go specific functions to GoFun class
public class Playout{
	Playout(){
		counter = 0;
		counterRules = 0;
		counterDS = 0;
	}

	int playRandomGame(final Board board, int first_stone){
		ArrayList<Point> free_points;
		int boardSize = board.getSize();
		//Point[] free_points = new Point[boardSize*boardSize];
		int freePointsSize;
		Random random = new Random();
		int random_point,i,j;
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


			

			p = getBestMove(playBoard, free_points);

			makeMove(playBoard, p, stoneType);
			playBoard.printBoard();

			free_points.clear();

		}
		//playBoard.printBoard();

		int[] score = getScore(playBoard);

		return score[0] > score[1] ? Board.FRIENDLY : Board.ENEMY; //TODO: komi is not used

	}
	Point getBestMove(Board board, ArrayList<Point> freePoints){
		float rating, bestRating = -100;
		Point bestPoint = null;
		for (Point point: freePoints){
			rating = rateMove(board, point);
			if (rating > bestRating){
				bestRating = rating;
				bestPoint = point;
			}
		}
		return bestPoint;
	}
	float rateMove(Board board, Point p){

		float rating = 0;
		//First line check
		if (p.i == 0 || p.i == (board.getSize()-1) || p.j == 0 || p.j == (board.getSize()-1)){
			rating -= 0.25;
		}
		//Distance between moves check
		Point lastPoint = board.getLastPoint();
		if (lastPoint != null){
			float distance = (float)Math.sqrt(Math.pow((lastPoint.i - p.i), 2) + Math.pow((lastPoint.j - p.j), 2));
			if (distance < 3.0)
				rating += 0.25/distance;
		}


		return rating;

	}
	void makeMove(Board board, Point p, int stoneType){
		board.setPoint(p, stoneType);
		removeDeadStones(board, Board.getOppositeSide(stoneType));
	}
	ArrayList<Point> getFreePoints(Board board, int stoneType){
		int i,j;
		int boardSize = board.getSize();
		ArrayList<Point> freePoints = new ArrayList<Point>();

		Point p;
		for (i = 0; i < boardSize; i++)
			for (j = 0; j < boardSize; j++){
				p = new Point(i,j);
				if (board.getPoint(p) == Board.EMPTY){
					if (checkRules(p, stoneType, board)){
						freePoints.add(p);

					}
				}

			}
		return freePoints;		
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

		ArrayList<Point> points_to_delete = new ArrayList<Point>(), points;
		Point[] surroundedStones = {	new Point(p.i-1, p.j),	//up 
										new Point(p.i+1, p.j),	//down	
										new Point(p.i, p.j+1),	//right
										new Point(p.i, p.j-1)	//left 
									};
		ArrayList<Point> group;
		int pValue = board.getPoint(p);
		

		if (getDameNumber(p, board) != 0){
			return true;
		}
		if (isFriendlySingleEyePoint(p, stoneType, board)){
			return false;
		}
		if (board.isKO(p, stoneType)){
			return false;
		}

		//Create new board copy to change existing board state without changes in the main board
		Board new_board = new Board(board);
		//new_board.saveState();
		new_board.setPoint(p, stoneType);
		
		 //posible suicide move
		counterRules++;
		if (isGroupDead(new_board, getGroup(new_board, p)) == false){
			//board.setPoint(p, pValue);	
			//new_board.loadState();
			return true;

		}
		
		//check could we kill neigbour enemy groups with this move
		for (Point next: surroundedStones){
			counterRules++;

			if (board.getPoint(next) == Board.getOppositeSide(stoneType)){
				//System.out.printf("\nNeigbour [%d:%d]\n", next.i, next.j);
				group = getGroup(new_board, next);
				if (isGroupDead(new_board, group) == true){
					//board.setPoint(p, pValue);	
					//new_board.loadState();
					return true;
				}
			}
			

		}
		
		//board.setPoint(p, pValue);
		//new_board.loadState();
		return false;
	}

	void removeDeadStones(Board board, int stoneType){
		int i,j, deletedStonesNumber = 0;
		Point point, lastPoint = null;
		ArrayList<Point> group;
		boolean[][] visited = new boolean[board.getSize()][board.getSize()];
		
		for(i = 0; i < board.getSize(); i++)
			for(j = 0; j < board.getSize(); j++)
				visited[i][j] = false;

		for(i = 0; i < board.getSize(); i++){
			for(j = 0; j < board.getSize(); j++){
				point = new Point(i,j);
				if (visited[point.i][point.j] == true){
					continue;
				}
				
				if (board.getPoint(point) == stoneType){

					group = getGroup(board, point);
					counterDS++;
					if (isGroupDead(board, group) == true){
						deletedStonesNumber += group.size();
						lastPoint = point; //possible ko point
							    

						for (Point stone: group){
							board.setPoint(stone, Board.EMPTY);
						}
					}
					else{
						for (Point stone: group){
							visited[stone.i][stone.j] = true;
						}
					}
				}
			
			}
		}
		if (deletedStonesNumber == 1 && lastPoint != null){
			board.setKO(lastPoint, stoneType);
		}
	}
	boolean isGroupDead(Board board, ArrayList<Point> group){
		for (Point stone: group){
			if (getDameNumber(stone, board) != 0){
				return false;
			}
		}

		return true;
	}
	ArrayList<Point> getGroup (Board board, Point p){
		int i,j;
		int dame_number;
		Point point,up, down, left, right;
		LinkedList<Point> queue = new LinkedList<Point>();
		ArrayList<Point> visited = new ArrayList<Point>();
		counter++;
		queue.add(p);
		while (queue.size() > 0){
			point = queue.poll();
			visited.add(point);
			
			up = new Point(point.i-1, point.j);
			if ((board.getPoint(point) == board.getPoint(up)) && !isPointVisited(visited, queue, up)){
				queue.add(up);

			}
			right = new Point(point.i, point.j+1);
			if ((board.getPoint(point) == board.getPoint(right)) && !isPointVisited(visited, queue, right)){
				queue.add(right);

			}

			down = new Point(point.i+1, point.j);
			if ((board.getPoint(point) == board.getPoint(down)) && !isPointVisited(visited, queue, down)){
				queue.add(down);

			}
			left = new Point(point.i, point.j-1);
			if ((board.getPoint(point) == board.getPoint(left)) && !isPointVisited(visited, queue, left)){
				queue.add(left);

			}

		}
		
		return visited;
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
				else {
					if (board.getPoint(p) == Board.FRIENDLY){
						friendScore++;
					}
					if (board.getPoint(p) == Board.ENEMY){
						enemyScore++;
					}

				}
			}
		}

		int[] score = {friendScore, enemyScore};
		return score;
		
	}
}