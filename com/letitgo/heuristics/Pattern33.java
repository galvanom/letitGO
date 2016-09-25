package com.letitgo.heuristics;
import java.util.*;
import java.io.*;
import com.letitgo.Board;
import com.letitgo.Playout;
import com.letitgo.Point;

public  class Pattern33{
	private final int PATTERN_SIZE = 9;
	private static int[] patternsBase;
	private static final String [] patterns = {  // 3x3 playout patterns; X,O are colors, x,o are their inverses
						       "XOX...???",
						       "XO....?.?",
						       "XO?X..x.?",
						       ".O.X.....",
						       "XO?O.o?o?",
						       "XO?O.X???",
						       "?X?O.Oooo",
						       "OX?o.O???"
						    	};
	public Pattern33(){
	}
	public static void init(){
		// При инициализации считываем все шаблоны из файла
		patternsBase = readPatternsFile("com/letitgo/patterns33.dat");
	}
	public static int[] readPatternsFile(String fileName){
		InputStream in = null;
		DataInputStream din = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final int patternsNumber;
		int[] patterns = null;
		try{
			
			in = classLoader.getResourceAsStream(fileName);
			//TODO: Проверить in на null
			din = new DataInputStream(in);
			patternsNumber = din.readInt();
			patterns = new int[patternsNumber];
			
			for (int i = 0; i < patternsNumber; i++){
				patterns[i] = din.readInt();
			}
			in.close();
		}
		catch (IOException e){
			System.out.printf("File %s IO error: %s",fileName, e);
		}

		return patterns;		
	}
	public void createPatternsFile(String fileName){
		HashSet<String> permutations;
		HashMap<String,String> wildchars = new HashMap<String,String>();

		// Инициализация шаблонов состояний
		wildchars.put("x","O.");
		wildchars.put("X","X");
		wildchars.put("O","O");
		wildchars.put("o","X.");
		wildchars.put("?","XO.");
		wildchars.put(".",".");
		// Получаем все вариации каждого шаблона 
		permutations = getPermutations3x3(wildchars);
		
		LinkedList<Integer> digitalPermutations = new LinkedList<Integer>();
		int digitalPermutation;
		int i;
		for (String permutation: permutations){
			digitalPermutation = stringToNumber(permutation);
			
			if (digitalPermutations.size() == 0){
				digitalPermutations.add(digitalPermutation);
				continue;
			}
			i = 0;
			for (int element : digitalPermutations){
				if (digitalPermutation < element){
					digitalPermutations.add(i, digitalPermutation);
					break;
				}
				i++;
			}
			if (i >= digitalPermutations.size()){
				digitalPermutations.addLast(digitalPermutation);
			}
		}
		FileOutputStream out = null;
		DataOutputStream dout = null;
		try{
			out = new FileOutputStream(fileName);
			dout = new DataOutputStream(out);
			
			dout.writeInt(digitalPermutations.size());

			if (dout != null){
				for (int element : digitalPermutations){
					// System.out.println(element);
					dout.writeInt(element);
				}

				out.close();
			}
		}
		catch(IOException e){
			System.out.printf("File %s IO error: %s",fileName, e);
		}

	}
	// Быстрая версия алгоритма ищет шаблоны только в округе 3 на 3
	public Point getFirstMove(Board board){
		ArrayList<Point> points = new ArrayList<Point>();
		points = board.getLastPoint().getNeighbours();
		points.addAll(board.getLastPoint().getDiagonalNeighbours());
				long seed = System.nanoTime();
		Collections.shuffle(points, new Random(seed));
		
		return getPatternMove(board, points);
	}
	// Ищет шаблоны по всей доске
	public ArrayList<Point> getAllMoves(Board board){
		int boardSize = board.getSize();
		int i,j;
		Point currentPoint;
		ArrayList<Point> allPattern33Moves = new ArrayList<Point>();
		for (i = 0; i < boardSize; i++){
			for (j = 0; j < boardSize; j++){
				currentPoint = new Point(board, i,j);
				if (board.getPoint(currentPoint) == Board.EMPTY){
					if (isPattern3x3(board, currentPoint)){
						allPattern33Moves.add(currentPoint);
					}
				}
			}
		}
		return allPattern33Moves;
	}

	private Point getPatternMove(Board board, ArrayList<Point> points){
		if (points != null){
			for (Point point: points){
				//System.out.printf("\n[%d,%d]\n", point.i, point.j);
				if (board.getPoint(point) == Board.EMPTY){
					if (isPattern3x3(board, point))
						return point;
				}
			}
		}
		return null;
			
	}

	// Возвращает область 3 на 3 для заданной точки
	private char[] get3x3Region(Board board, Point point){
		int i,j,k;
		int stoneType;
		char[] square33 = new char[PATTERN_SIZE];

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
		return square33;
	}
	// Определяет подходит ли поле под шаблон
	public  boolean isPattern3x3(Board board, Point point){
		int i,j;
		int boardPoints; 

		if (point.i <= 0 || point.i >= board.getSize()-1 || point.j <= 0 || point.j >= board.getSize()-1)
			return false;
		
		boardPoints = stringToNumber(String.valueOf(get3x3Region(board, point)));

		for (int pattern : patternsBase){
			if (boardPoints == pattern){
				return true;
			}
		}

		return false;
	}
	// Получаем все перевороты и изменения цвета для каждого шаблона
	private HashSet<String> getPermutations3x3(HashMap<String,String> wildchars){
		String currentVariation;
		ArrayList<String> allVariations;
		HashSet<String> permutations = new HashSet<String>();
		//int next = 0;
		for (String pattern : patterns){
			allVariations = new ArrayList<String>();
			
			if(!getAllVariations(pattern.toCharArray(),0,allVariations, wildchars)){
				allVariations.add(pattern);
			}

			for (String variation : allVariations){
				currentVariation = variation;
				for (int i = 0; i < 2; i++){ // 2 different colors 
					for (int j = 0; j < 4; j++){ // 4 90 degree permutations 
						permutations.add(currentVariation);
						permutations.add(getHorizPermutation(currentVariation));
						permutations.add(getVertPermutation(currentVariation));
						currentVariation= get90degPermutation(currentVariation);
					}
					currentVariation = changeColors(currentVariation);
				}
			}
		}

		return permutations;
		
	}
	private String get90degPermutation(String pattern){
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
	private String getHorizPermutation(String pattern){
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
	private String getVertPermutation(String pattern){
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
	private String changeColors(String pattern){
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
	public boolean getAllVariations(char[] pattern, int pos, ArrayList<String> allVariations, HashMap<String,String> wildchars){
		int i, j;
		for (i = pos; i < PATTERN_SIZE;i++){
			switch(pattern[i]){
				case '?':
				case 'o':
				case 'x':
					String symbols = wildchars.get(String.valueOf(pattern[i]));
					char[] newPattern = Arrays.copyOf(pattern, PATTERN_SIZE);

					for (j = 0; j < symbols.length(); j++){
						newPattern[i] = symbols.charAt(j);
							
						if (!getAllVariations(newPattern, i+1, allVariations, wildchars)){
							// System.out.println(String.valueOf(newPattern));
							allVariations.add(String.valueOf(newPattern));
						}
						
					}
					return true;
			}
		}
		return false;
		
	}
	// Переводим шаблон в число типа int
	private int stringToNumber(String str){
		char point;
		int number = 0;
		for (int i = 0; i < str.length(); i++){
			point = str.charAt(i);
			number = number << 2;
			switch(point){
				case 'X':
					number |= 0b10;
					break;
				case 'O':
					number |= 0b01;
					break;
				case '.':
					//number |= 0b00;
					break;
				case '#':
					number |= 0b11;
					break;
			}
		}
		// System.out.println(Integer.toBinaryString(number));
		return number;
	}
}