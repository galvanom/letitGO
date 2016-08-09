package com.letitgo.heuristics;
import java.util.*;
import com.letitgo.Board;
import com.letitgo.Playout;
import com.letitgo.Point;
import com.letitgo.Group;

public class Capture{
	//Fast version of the algorithm. Searches moves only in area of 3x3 around last move
	public Point getFirstMove(final Board board, int ownStoneType){
		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<Point> captureMoves;
		
		points = board.getLastPoint().getNeighbours();
		points.addAll(board.getLastPoint().getDiagonalNeighbours());
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
				points.add(new Point(board,i,j));
			}
		}
		return getCaptureMoves(board, points, ownStoneType, false);

	}
	private ArrayList<Point> getCaptureMoves(final Board board, ArrayList<Point> points, int ownStoneType, boolean isFast){
		//ArrayList<Point> points = null;
		Group group;
		ArrayList<Point> groupDame;
		ArrayList<Point> neighbours;
		Group enemyGroup, friendlyGroup; 
		ArrayList<Point> enemyGroupDame, friendlyGroupDame;
		ArrayList<Point> allCaptureMoves = new ArrayList<Point>();
		int boardSize = board.getSize();
		byte visitedNeighbours[][] = new byte[boardSize][boardSize];
		byte visitedGroups[][] = new byte[boardSize][boardSize];
		int pointType;
		Point atariGroupDame;
		Point smth = null;
		int i,j;

		if (points == null){
			return allCaptureMoves;
		}
		for (i = 0; i < boardSize; i++) //TODO: Try to merge it with visitedNeighbours
			for (j = 0; j < boardSize; j++)
				visitedGroups[i][j] = 0;

		for (Point point: points){
			Point xpoint = point;
			Board newBoard = new Board(board);
			pointType = board.getPoint(xpoint);

			if (pointType != Board.ENEMY && pointType != Board.FRIENDLY){
				continue;
			}
						

			group = board.getGroup(point);
			groupDame = group.getGroupDame();
			//if group in atari
			
			if (groupDame.size() == 1){
				atariGroupDame = groupDame.get(0);
				//if this is enemy group return move to capture it
				if (pointType == Board.getOppositeSide(ownStoneType) && board.checkRules(atariGroupDame, ownStoneType) == true){
					//System.out.printf("Dames: %d, Dame(0): %d,%d\n",groupDame.size(), groupDame.get(0).i, groupDame.get(0).j );
					 allCaptureMoves.add(atariGroupDame);
					 if (isFast){
					 	return allCaptureMoves;
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
						neighbours = stone.getNeighbours();
						for (Point neighbour: neighbours){
							if (board.getPoint(neighbour) != Board.getOppositeSide(ownStoneType)){
								continue;
							}
							if (visitedNeighbours[neighbour.i][neighbour.j] == 1){
								continue;
							}
							enemyGroup = board.getGroup(neighbour);
							enemyGroupDame = enemyGroup.getGroupDame();
							if (enemyGroupDame.size() == 1 && board.checkRules(enemyGroupDame.get(0), ownStoneType) == true){
								
								allCaptureMoves.add(enemyGroupDame.get(0));
								if (isFast){
									return allCaptureMoves;
								}
								
							}
							for (Point enemyStone: enemyGroup)
								visitedNeighbours[enemyStone.i][enemyStone.j] = 1;							
						}
					}
					
					//Try to put a stone to the free dame and avoid self atari and the ladder
					if (board.checkRules(atariGroupDame, ownStoneType) == true){
						board.tryMove(atariGroupDame, ownStoneType);
						
						friendlyGroup = board.getGroup(atariGroupDame);
						friendlyGroupDame = friendlyGroup.getGroupDame();
						board.undoMove();
						if (friendlyGroupDame.size() > 2){
							allCaptureMoves.add(atariGroupDame);
							if (isFast){
								return allCaptureMoves;
							}
						}

					
					}

				}
				
			}
			for (Point visitedStone: group)
				visitedGroups[visitedStone.i][visitedStone.j] = 1;	
		}
	
	return allCaptureMoves;
	}

}