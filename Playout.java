import java.util.*;
import java.io.*;

//TODO: Save Go specific functions to GoFun class
public class Playout{
	char[][] pat3src = {  // 3x3 playout patterns; X,O are colors, x,o are their inverses
       {'X','O','X',  // hane pattern - enclosing hane
        '.','.','.',
        '?','?','?'},
       {'X','O','.',  // hane pattern - non-cutting hane
        '.','.','.',
        '?','.','?'}
    };

    static char[][] permutations;
	Playout(){
		permutations = Pattern33.getPermutations3x3(pat3src);
		for (char[] perm: permutations){
			for(int i = 0; i < 9; i++){
				if (i % 3 == 0)
					System.out.println();
				System.out.printf("%c",perm[i]);

			}
		}
	
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
		Point p = null;
		//Point heurPoint = new Point();
		Board playBoard = new Board(board);
		/*
		//Tests for patterns
		if (Pattern33.isPattern3x3(playBoard, new Point(7,1)))
			System.out.printf("\n***Pattern found!\n");
		else
			System.out.printf("\n***No pattern found\n");
			
		*/

		for (int movesCount = 0; movesCount < MAX_MOVES && passTimes < 2; stoneType = Board.getOppositeSide(stoneType), movesCount++){

			//Before get random moves, engine has to try moves in the last move's neighbourhood.
			//This moves have to be either "capture/release capture" or the patterns
			p = getHeuristicMove(playBoard, stoneType);
			
			if (p != null ) {
				//playBoard.printBoard();
				makeMove(playBoard, p, stoneType);
				//System.out.printf("\n****Heuristic occured. The point is [%d,%d] Stone type is: %s ****",p.i , p.j, stoneType == Board.ENEMY ? "X" :"O");
				//playBoard.printBoard();
				continue;
			}

			//TODO: Improve getFreePoints for the perfomance goal. Bigger number of free points bigger CPU load.
			//Maybe this too many garbage to collect and GC runs more frequently then usually
			
			free_points = getFreePoints(playBoard, stoneType);

			if (free_points.size() == 0){
				passTimes++;
				continue;
			}
			passTimes = 0;


			//p = free_points.get(0);
			p = free_points.get(random.nextInt(free_points.size()));
			//p = getBestMove(playBoard, free_points);

			makeMove(playBoard, p, stoneType);
			//playBoard.printBoard();

			
		}
		//playBoard.printBoard();

		int[] score = getScore(playBoard);

		//System.out.printf("\nO: %d  X: %d\n", score[0], score[1]);  
		return score[0] > score[1] ? Board.FRIENDLY : Board.ENEMY; //TODO: komi is not used

	}
	public static class Pattern33{
		static boolean isPattern3x3(Board board, Point point){
			char[] square33 = new char[9];
			int i,j,k;
			int stoneType;
			HashMap<String,String> map = new HashMap<String,String>();
			String symbols;
			map.put("x","O.");
			map.put("X","X");
			map.put("O","O");
			map.put("o","X.");
			map.put("?","XO.");
			map.put(".",".");
			if (point.i == 0 || point.i == board.getSize()-1 || point.j == 0 || point.j == board.getSize()-1)
				return false;
			//Get 3x3 region around the point
			for (i = point.i-1, k = 0;i <= point.i + 1;i++){
				j = point.j - 1;
				for (;j <= point.j + 1; j++,k++){
					stoneType = board.getPoint(i,j);
					switch (stoneType){
						case Board.ENEMY:
							square33[k] = 'X';
							break;
						case Board.FRIENDLY:
							square33[k] = 'O';
							break;
						case Board.EMPTY:
							square33[k] = '.';
								
					}
					System.out.printf("%c ", square33[k]);
				}

			}
			//Compare to the all patterns and them permutations
			boolean flag;
			for (char[] perm: permutations){
				System.out.println(String.valueOf(perm));
				toNextPoint:
				for (i = 0; i < 9; i++){
					System.out.println(square33[i]);
					symbols = map.get(String.valueOf(perm[i]));
					for (j = 0; j < symbols.length(); j++){
						if (symbols.charAt(j) == square33[i])
							continue toNextPoint;
					}
					break;

				}
				if (i == 9){
					return true;
				}
			}
			return false;
		}

		//TODO: get all permutations of each pattern, not only four
		static char[][] getPermutations3x3(char[][] patterns){
			int length = patterns.length*24;
			char[][] permutations = new char[length][];
			char[] currentPattern;
			int next = 0;
			for (char[] pattern : patterns){
				currentPattern = pattern;
				for (int i = 0; i < 2; i++){
					for (int j = 0; j < 4; j++){
						permutations[next] = currentPattern;
						next++;
						permutations[next] = getHorizPermutation(currentPattern);
						next++;
						permutations[next] = getVertPermutation(currentPattern);
						next++;
						currentPattern = get90degPermutation(currentPattern);
					}
					currentPattern = changeColors(currentPattern);
				}
				/*
				//original
				permutations[next] = pattern;
				next++;
				//horizontal flip
				permutations[next] = getHorizPermutation(pattern);
				next++;
				//vertical flip
				permutations[next] = getVertPermutation(pattern);
				next++;
				//90 degree
				permutations[next] = get90degPermutation(pattern);
				next++;
				
				permutations[next] = changeColors(pattern);
				next++;
				*/
			}

			return permutations;
		}
		static char[] get90degPermutation(char[] pattern){
			char[] newPattern = new char[9];
			newPattern[0] = pattern[6];
			newPattern[1] = pattern[3];
			newPattern[2] = pattern[0];
			newPattern[3] = pattern[7];
			newPattern[4] = pattern[4];
			newPattern[5] = pattern[1];
			newPattern[6] = pattern[8];
			newPattern[7] = pattern[5];
			newPattern[8] = pattern[2];
			return newPattern;
		}
		static char[] getHorizPermutation(char[] pattern){
			char[] newPattern = new char[9];
			newPattern[0] = pattern[6];
			newPattern[1] = pattern[7];
			newPattern[2] = pattern[8];
			newPattern[3] = pattern[3];
			newPattern[4] = pattern[4];
			newPattern[5] = pattern[5];
			newPattern[6] = pattern[0];
			newPattern[7] = pattern[1];
			newPattern[8] = pattern[2];
			return newPattern;
		}
		static char[] getVertPermutation(char[] pattern){
			char[] newPattern = new char[9];
			newPattern[0] = pattern[2];
			newPattern[1] = pattern[1];
			newPattern[2] = pattern[0];
			newPattern[3] = pattern[5];
			newPattern[4] = pattern[4];
			newPattern[5] = pattern[3];
			newPattern[6] = pattern[8];
			newPattern[7] = pattern[7];
			newPattern[8] = pattern[6];
			return newPattern;
		}
		static char[] changeColors(char[] pattern){
			char[] newPattern = new char[9];
			char currentPoint;
			byte next = 0;
			for (char point: pattern){
				switch (point){
					case 'X':
						currentPoint = 'O';
						break;
					case 'O':
						currentPoint = 'X';
						break;
					case 'x':
						currentPoint = 'o';
						break;
					case 'o':
						currentPoint = 'x';
						break;
					default:
						currentPoint = point;
				}

				
				newPattern[next++] = currentPoint;
				
			}
			return newPattern;
		}

	}
	//TODO: Make test for it.
	Point getHeuristicMove(final Board board, int stoneType){
		ArrayList<Point> points = null;
		ArrayList<Point> group;
		ArrayList<Point> groupDame;
		ArrayList<Point> neighbours;
		ArrayList<Point> enemyGroup, enemyGroupDame, friendlyGroup, friendlyGroupDame;
		int boardSize = board.getSize();
		byte visitedNeighbours[][] = new byte[boardSize][boardSize];
		int pointType;
		Board checkBoard;
		Point atariGroupDame;
		Point smth = null;

		if (board.getLastPoint() != null){
			points = getNeighbours(board, board.getLastPoint());
			points.addAll(getDiagonalNeighbours(board, board.getLastPoint()));
		}
		else{
			
			return null;
		}
		if (points == null){
			return null;
		}
		
		for (Point point: points){
			pointType = board.getPoint(point);
			if (pointType != Board.ENEMY && pointType != Board.FRIENDLY){
				continue;
			}

			if (!checkRules(point, stoneType, board)){
				continue;
			}
			/*Patterns matching
			if (Pattern33.isPattern3x3(board, point))
				return point;
			*/
			group = getGroup(board, point);
			groupDame = getGroupDame(board, group);
			//if group in atari
			
			if (groupDame.size() == 1){
				atariGroupDame = groupDame.get(0);
				//if this is enemy group return move to capture it
				if (pointType == Board.getOppositeSide(stoneType) && checkRules(atariGroupDame, stoneType, board) == true){
					//System.out.printf("Dames: %d, Dame(0): %d,%d\n",groupDame.size(), groupDame.get(0).i, groupDame.get(0).j );
					return atariGroupDame;
					//return null;
				}
				//if this is friendly group, try to capture neighbour enemy group or
				//try to escape but dont't make selfatari as well as the ladder
				else if (pointType == stoneType){
					
					//trying to kill enemy
					for (int i = 0; i < boardSize; i++)
						for (int j = 0; j < boardSize; j++)
							visitedNeighbours[i][j] = 0;

					for (Point stone: group){
						neighbours = getNeighbours(board, stone);
						for (Point neighbour: neighbours){
							if (board.getPoint(neighbour) == stoneType){
								continue;
							}
							if (board.getPoint(neighbour) == Board.BORDER){
								continue;
							}

							if (visitedNeighbours[neighbour.i][neighbour.j] == 1){
								continue;
							}
							enemyGroup = getGroup(board, neighbour);
							enemyGroupDame = getGroupDame(board, enemyGroup);
							if (enemyGroupDame.size() == 1 && checkRules(enemyGroupDame.get(0), stoneType, board) == true){

								return enemyGroupDame.get(0);
							}
							for (Point enemyStone: enemyGroup)
								visitedNeighbours[enemyStone.i][enemyStone.j] = 1;							
						}
					}
					
					//Try to put a stone to the free dame and avoid self atari and the ladder
					if (checkRules(atariGroupDame, stoneType, board) == true){
						checkBoard = new Board(board);
						checkBoard.setPoint(atariGroupDame, stoneType);
						friendlyGroup = getGroup(checkBoard, atariGroupDame);
						friendlyGroupDame = getGroupDame(checkBoard, friendlyGroup);
						if (friendlyGroupDame.size() > 2){
							return atariGroupDame;
						}
					}

					

				}
				
			}
			
		}
	
	return null;
	}

	ArrayList<Point> getGroupDame(final Board board, ArrayList<Point> group){
		int boardSize = board.getSize();
		byte visited[][] = new byte[boardSize][boardSize];
		int i,j;
		ArrayList<Point> dame = new ArrayList<Point>(); //What is initialization value?
		ArrayList<Point> neigbours;

		for (i = 0; i < boardSize; i++)
			for (j = 0; j < boardSize; j++)
				visited[i][j] = 0;
		for (Point stone: group){
			
			neigbours = getNeighbours(board, stone);
			for (Point neighbour: neigbours){
				//System.out.printf("\nstone: [%d,%d], neighbour: [%d,%d]\n", stone.i, stone.j, neighbour.i, neighbour.j);
				if (board.getPoint(neighbour) == Board.EMPTY && visited[neighbour.i][neighbour.j] == 0){
					dame.add(neighbour);
					visited[neighbour.i][neighbour.j] = 1;
				}
			}
	
		}
		return dame;
	}
	ArrayList<Point> getNeighbours(final Board board, Point p){
		ArrayList<Point> neighbours = new ArrayList<Point>();
		neighbours.add(new Point(p.i+1, p.j));
		neighbours.add(new Point(p.i-1, p.j));
		neighbours.add(new Point(p.i, p.j+1));
		neighbours.add(new Point(p.i, p.j-1));

		return neighbours;
	}
	ArrayList<Point> getDiagonalNeighbours(final Board board, Point p){
		ArrayList<Point> neighbours = new ArrayList<Point>();
		neighbours.add(new Point(p.i+1, p.j+1));
		neighbours.add(new Point(p.i+1, p.j-1));
		neighbours.add(new Point(p.i-1, p.j-1));
		neighbours.add(new Point(p.i-1, p.j+1)); //WTF? This line increases execution for 5 times

		return neighbours;
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
		
		if (isGroupDead(new_board, getGroup(new_board, p)) == false){
			//board.setPoint(p, pValue);	
			//new_board.loadState();
			return true;

		}
		
		//check could we kill neigbour enemy groups with this move
		for (Point next: surroundedStones){
			

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
		int boardSize = board.getSize();
		queue.add(p);
		while (queue.size() > 0){
			point = queue.poll();
			visited.add(point);
			
			//if (point.i-1 >= 0){
				up = new Point(point.i-1, point.j);
				if ((board.getPoint(point) == board.getPoint(up)) && !isPointVisited(visited, queue, up)){
					queue.add(up);

				}
			//}
			//if (point.j+1 < boardSize){
				right = new Point(point.i, point.j+1);
				if ((board.getPoint(point) == board.getPoint(right)) && !isPointVisited(visited, queue, right)){
					queue.add(right);

				}
			//}
			//if (point.i+1 < boardSize){
				down = new Point(point.i+1, point.j);
				if ((board.getPoint(point) == board.getPoint(down)) && !isPointVisited(visited, queue, down)){
					queue.add(down);

				}
			//}
			//if (point.j-1 >= 0){
				left = new Point(point.i, point.j-1);
				if ((board.getPoint(point) == board.getPoint(left)) && !isPointVisited(visited, queue, left)){
					queue.add(left);

				}
			//}
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