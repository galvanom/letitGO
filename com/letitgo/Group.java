public class Group{
	private ArrayList<Points> group;
	private ArrayList<Points> groupDame;

	Group(){
		group = new ArrayList<Point>;
	}
	public void addPoint(Point point){
		group.add(point);
	}
	public int getGroupDameNumber(final Board board){
		if (groupDame == null){
			groupDame = getGroupDame(board);
		}
		return groupDame.size();
	}
	public ArrayList<Point> getGroupDame(final Board board){
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
	public boolean isGroupDead(Board board){
		for (Point stone: group){
			if (getDameNumber(stone, board) != 0){
				return false;
			}
		}

		return true;
	}

}