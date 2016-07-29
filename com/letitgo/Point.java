package com.letitgo;
public class Point{
	public int i, j;
	public Point(){
		i = -1;
		j = -1;
	}
	public Point(int i, int j){
		this.i = i;
		this.j = j;
	}
	public boolean isEqualsTo(Point other){
		if (this.i == other.i && this.j == other.j)
			return true;
		return false;
	}
	

}