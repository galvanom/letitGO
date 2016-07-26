import java.io.*;
import java.util.*;

public class Heuristics{

	//Возвращает точку дыхания группы типа stoneType, находящейся в атари.
	//Если такой группы нет, возвращает null
	Point getLastDame(Board board, int stoneType){
		Playout pl = new Playout();
		int i,j, boardSize = board.getSize();
		boolean[][] visited = new boolean[boardSize][boardSize];
		ArrayList<Point> group;
		Point[] neighbours = new Point[4];
		Point lastEmptyPoint;
		boolean isAtariGroup;

		for(i = 0; i < boardSize; i++)
			for(j = 0; j < boardSize; j++)
				visited[i][j] = false;

		for (i = 0; i < boardSize; i++){
			for (j = 0; j < boardSize; j++){
				if (board.getPoint(i,j) == stoneType && visited[i][j] == false){
					group = pl.getGroup(board, new Point(i,j));
					isAtariGroup = true;
					lastEmptyPoint = null;
					for (Point stone: group){
						
						neighbours[0] = new Point(stone.i-1, stone.j);
						neighbours[1] = new Point(stone.i+1, stone.j);
						neighbours[2] = new Point(stone.i, stone.j-1);
						neighbours[3] = new Point(stone.i, stone.j+1);
						for (Point neigbour: neighbours){
							if ( neigbour.i < 0 || neigbour.i >= boardSize || neigbour.j < 0 || neigbour.j >= boardSize){
								continue;
							}

							visited[neigbour.i][neigbour.j] = true;
							if (board.getPoint(neigbour) == Board.EMPTY){
								if (lastEmptyPoint == null){
									lastEmptyPoint = neigbour;
									continue;
								}
								else{
									if (lastEmptyPoint.i != neigbour.i || lastEmptyPoint.j != neigbour.j){
										isAtariGroup = false;
									}

								}
							}
						}

					}
					//Если есть свободное даме и оно единственное для этой группы
					if (lastEmptyPoint != null && isAtariGroup == true){
						return lastEmptyPoint;
					}
				}
			}
		}
		return null;
	}
}