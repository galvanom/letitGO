class Point{
	int i, j;
	Point(){

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