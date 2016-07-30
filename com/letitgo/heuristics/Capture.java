package com.letitgo.heuristics;
import java.util.*;
import com.letitgo.Board;
import com.letitgo.Playout;
import com.letitgo.Point;

public class Capture{
	//Fast version of the algorithm. Searches moves only in area of 3x3 around last move
	public Point getFirstMove(final Board board, int ownStoneType){
		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<Point> captureMoves;
		
		points = Playout.getNeighbours(board, board.getLastPoint());
		points.addAll(Playout.getDiagonalNeighbours(board, board.getLastPoint()));
		captureMoves = getCaptureMoves(board, points, ownStoneType, true);
		
		if (captureMoves.size() > 0){
			return captureMoves.get(0);
		}
		else{
			return null;
		}
	}
	//Full version. Returns all available capture moves at the board
	public ArrayList<Point> getAllMoves(final Board board, int ownStoneType){
		int boardSize = board.getSize();
		ArrayList<Point> points = new ArrayList<Point>();

		for (int i = 0; i < boardSize; i++){
			for (int j = 0; j < boardSize; j++){
				points.add(new Point(i,j));
			}
		}
		return getCaptureMoves(board, points, ownStoneType, false);

	}
	private ArrayList<Point> getCaptureMoves(final Board board, ArrayList<Point> points, int ownStoneType, boolean isFast){
		//ArrayList<Point> points = null;
		ArrayList<Point> group;
		ArrayList<Point> groupDame;
		ArrayList<Point> neighbours;
		ArrayList<Point> enemyGroup, enemyGroupDame, friendlyGroup, friendlyGroupDame;
		ArrayList<Point> allHeuristicMoves = new ArrayList<Point>();
		int boardSize = board.getSize();
		byte visitedNeighbours[][] = new byte[boardSize][boardSize];
		byte visitedGroups[][] = new byte[boardSize][boardSize];
		int pointType;
		Board checkBoard;
		Point atariGroupDame;
		Point smth = null;
		int i,j;

		if (points == null){
			return allHeuristicMoves;
		}
		for (i = 0; i < boardSize; i++) //TODO: Try to merge it with visitedNeighbours
			for (j = 0; j < boardSize; j++)
				visitedGroups[i][j] = 0;

		for (Point point: points){
			pointType = board.getPoint(point);
			if (pointType != Board.ENEMY && pointType != Board.FRIENDLY){
				continue;
			}

			if (!Playout.checkRules(point, ownStoneType, board)){
				continue;
			}
			
			/*//Patterns matching

			*/

			group = Playout.getGroup(board, point);
			groupDame = Playout.getGroupDame(board, group);
			//if group in atari
			
			if (groupDame.size() == 1){
				atariGroupDame = groupDame.get(0);
				//if this is enemy group return move to capture it
				if (pointType == Board.getOppositeSide(ownStoneType) && Playout.checkRules(atariGroupDame, ownStoneType, board) == true){
					//System.out.printf("Dames: %d, Dame(0): %d,%d\n",groupDame.size(), groupDame.get(0).i, groupDame.get(0).j );
					 allHeuristicMoves.add(atariGroupDame);
					 if (isFast){
					 	return allHeuristicMoves;
					 }
					//return null;
				}
				//if this is friendly group, try to capture neighbour enemy group or
				//try to escape but dont't make selfatari as well as the ladder
				else if (pointType == ownStoneType){
					
					//trying to kill enemy
					for (i = 0; i < boardSize; i++)
						for (j = 0; j < boardSize; j++)
							visitedNeighbours[i][j] = 0;

					for (Point stone: group){
						neighbours = Playout.getNeighbours(board, stone);
						for (Point neighbour: neighbours){
							if (board.getPoint(neighbour) != Board.getOppositeSide(ownStoneType)){
								continue;
							}
							if (visitedNeighbours[neighbour.i][neighbour.j] == 1){
								continue;
							}
							enemyGroup = Playout.getGroup(board, neighbour);
							enemyGroupDame = Playout.getGroupDame(board, enemyGroup);
							if (enemyGroupDame.size() == 1 && Playout.checkRules(enemyGroupDame.get(0), ownStoneType, board) == true){
								
								allHeuristicMoves.add(enemyGroupDame.get(0));
								if (isFast){
									return allHeuristicMoves;
								}
								
							}
							for (Point enemyStone: enemyGroup)
								visitedNeighbours[enemyStone.i][enemyStone.j] = 1;							
						}
					}
					
					//Try to put a stone to the free dame and avoid self atari and the ladder
					if (Playout.checkRules(atariGroupDame, ownStoneType, board) == true){
						checkBoard = new Board(board);
						checkBoard.setPoint(atariGroupDame, ownStoneType);
						friendlyGroup = Playout.getGroup(checkBoard, atariGroupDame);
						friendlyGroupDame = Playout.getGroupDame(checkBoard, friendlyGroup);
						if (friendlyGroupDame.size() > 2){
							allHeuristicMoves.add(atariGroupDame);
							if (isFast){
								return allHeuristicMoves;
							}
						}
					}

					

				}
				
			}
			for (Point visitedStone: group)
				visitedGroups[visitedStone.i][visitedStone.j] = 1;	
		}
	
	return allHeuristicMoves;
	}
}