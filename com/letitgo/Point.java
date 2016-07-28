class Point{
	int i, j;
	Point(){
		i = -1;
		j = -1;
	}
	Point(int i, int j){
		this.i = i;
		this.j = j;
	}
	boolean isEqualsTo(Point other){
		if (this.i == other.i && this.j == other.j)
			return true;
		return false;
	}
	

}