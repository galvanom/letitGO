package com.letitgo;
public class Group implements Iterable<Point>{
	private ArrayList<Points> group;
	private ArrayList<Points> groupDame;
	private final Board board;

	Group(final Board board, ArrayList<Point> group){
		this.board = board;
		this.group = group;
	}
	@Override
	public Iterator<Point> iterator(){
		return this.group.iterator();
	}
	public getSize(){
		return this.group.size();
	}
	// TODO: Don't know is this function needed or not
	public void addPoint(Point point){
		this.group.add(point);
	}
	public int getGroupDameNumber(){
		if (this.groupDame == null){
			this.groupDame = getGroupDame();
		}
		return this.groupDame.size();
	}
	public ArrayList<Point> getGroupDame(){
		int boardSize = board.getSize();
		byte visited[][] = new byte[boardSize][boardSize];
		int i,j;
		ArrayList<Point> dame = new ArrayList<Point>(); //What is initialization value?
		ArrayList<Point> neigbours;

		for (i = 0; i < boardSize; i++)
			for (j = 0; j < boardSize; j++)
				visited[i][j] = 0;
		for (Point stone: this.group){
			
			neigbours = stone.getNeighbours();
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
	public boolean isGroupDead(){
		for (Point stone: this.group){
			if (stone.getDameNumber() != 0){
				return false;
			}
		}

		return true;
	}

}