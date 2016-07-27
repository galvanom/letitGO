import java.io.*;
import java.util.*;

public class Heuristics{

    

    public  class Pattern33{
    	static HashSet<String> permutations;
    	String [] pat3src = {  // 3x3 playout patterns; X,O are colors, x,o are their inverses
					       "XOX...???",
					       "XO....?.?",
					       "XO?X..x.?",
					       ".O.X.....",
					       "XO?O.o?o?",
					       "XO?O.X???",
					       "?X?O.Oooo",
					       "OX?o.O???"
					    	};
					    		//Test it
		Point getPatternMove(Board board, ArrayList<Point> points){
			if (points != null){
				for (Point point: points){
					//System.out.printf("\n[%d,%d]\n", point.i, point.j);
					if (board.getPoint(point) == Board.EMPTY){
						if (Pattern33.isPattern3x3(board, point))
							return point;
					}
				}
			}
			return null;
				
		}
		
		boolean isPattern3x3(Board board, Point point){
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
			if (point.i <= 0 || point.i >= board.getSize()-1 || point.j <= 0 || point.j >= board.getSize()-1)
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
					//System.out.printf("%c ", square33[k]);
				}

			}
			//Compare to the all patterns and them permutations
			boolean flag;
			for (String perm: permutations){
				//System.out.println(String.valueOf(perm));
				toNextPoint:
				for (i = 0; i < 9; i++){
					//System.out.println(square33[i]);
					symbols = map.get(String.valueOf(perm.charAt(i)));
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

		HashSet<String> getPermutations3x3(String[] patterns){
			/*int length = patterns.length*24;
			char[][] permutations = new char[length][];*/
			String currentPattern;
			//int next = 0;
			HashSet<String> st = new HashSet<String>();
			for (String pattern : patterns){
				currentPattern = pattern;
				for (int i = 0; i < 2; i++){
					for (int j = 0; j < 4; j++){
						st.add(currentPattern);
						st.add(getHorizPermutation(currentPattern));
						st.add(getVertPermutation(currentPattern));
						currentPattern = get90degPermutation(currentPattern);
					}
					currentPattern = changeColors(currentPattern);
				}
			}

			return st;
		}
		String get90degPermutation(String pattern){
			char[] newPattern = new char[9];
			newPattern[0] = pattern.charAt(6);
			newPattern[1] = pattern.charAt(3);
			newPattern[2] = pattern.charAt(0);
			newPattern[3] = pattern.charAt(7);
			newPattern[4] = pattern.charAt(4);
			newPattern[5] = pattern.charAt(1);
			newPattern[6] = pattern.charAt(8);
			newPattern[7] = pattern.charAt(5);
			newPattern[8] = pattern.charAt(2);
			return String.valueOf(newPattern);
		}
		String getHorizPermutation(String pattern){
			char[] newPattern = new char[9];
			newPattern[0] = pattern.charAt(6);
			newPattern[1] = pattern.charAt(7);
			newPattern[2] = pattern.charAt(8);
			newPattern[3] = pattern.charAt(3);
			newPattern[4] = pattern.charAt(4);
			newPattern[5] = pattern.charAt(5);
			newPattern[6] = pattern.charAt(0);
			newPattern[7] = pattern.charAt(1);
			newPattern[8] = pattern.charAt(2);
			return String.valueOf(newPattern);
		}
		String getVertPermutation(String pattern){
			char[] newPattern = new char[9];
			newPattern[0] = pattern.charAt(2);
			newPattern[1] = pattern.charAt(1);
			newPattern[2] = pattern.charAt(0);
			newPattern[3] = pattern.charAt(5);
			newPattern[4] = pattern.charAt(4);
			newPattern[5] = pattern.charAt(3);
			newPattern[6] = pattern.charAt(8);
			newPattern[7] = pattern.charAt(7);
			newPattern[8] = pattern.charAt(6);
			return String.valueOf(newPattern);
		}
		String changeColors(String pattern){
			char[] newPattern = new char[9];
			char currentPoint;
			char point;
			for (int i = 0; i < 9; i++){
				point = pattern.charAt(i) ;
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

				
				newPattern[i] = currentPoint;
				
			}
			return String.valueOf(newPattern);
		}
	}

}