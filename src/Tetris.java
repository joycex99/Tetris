import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

public class Tetris extends JPanel implements ActionListener { 

	final Color empty = Color.black;
	public static int DEFAULT_R = 20;
	public static int DEFAULT_C = 10;
	public int rows;
	public int cols;
	public Color[][] board; 

	public Timer timer;
	public int delay;
	
	public int counter;

	//DIRECTIONS
	private static final int[] DOWN = {1, 0};
	private static final int[] LEFT = {0, -1};
	private static final int[] RIGHT = {0, 1};

	/*CURRENT PIECE*/
	boolean[][] p;
	int pieceRow;
	int pieceCol;
	Color pieceColor;

	
	public Tetris(int r, int c) {
		this.rows = r;
		this.cols = c;
		board = new Color[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				board[i][j] = empty;
			}
		}
		
		delay = 500;
		timer = new Timer(delay, this);
		timer.start();
		
		counter = 0;

		this.setFocusable(true);
		KeyListener l = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					moveFallingPiece(LEFT);
					repaint();
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					moveFallingPiece(RIGHT);
					repaint();
				}
				else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					dropDown();
					repaint();
				}
				else if (e.getKeyCode() == KeyEvent.VK_UP) {
					rotateFallingPiece();
					repaint();
				}
			}
		};
		addKeyListener(l);
		p = newFallingPiece();
	}

	public Tetris() {
		this(DEFAULT_R, DEFAULT_C);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!moveFallingPiece(1, 0)) {
			placeFallingPiece();
			if (!gameIsOver()) {
				p = newFallingPiece();
			}
		}
		removeLines();
		setTimer();
		repaint();
	}

	//SEVEN STANDARD PIECES (TETROMINOES):
	static boolean T = true; //T for tetrominoes
	private static final boolean[][] I_PIECE = {
		{T, T, T, T},
	};
	private static final boolean[][] J_PIECE = {
		{T, false, false},
		{T, T, T}
	};
	private static final boolean[][] L_PIECE = {
		{false, false, T},
		{T, T, T}

	};
	private static final boolean[][] O_PIECE = {
		{T, T},
		{T, T}
	};
	private static final boolean[][] T_PIECE = {
		{false, T, false},
		{T, T, T}

	};
	private static final boolean[][] S_PIECE = {
		{false, T, T},
		{T, T, false}

	};
	private static final boolean[][] Z_PIECE = {
		{T, T, false},
		{false, T, T}
	};

	private static boolean[][][] PIECES = {
		I_PIECE, J_PIECE, L_PIECE, O_PIECE, T_PIECE, S_PIECE, Z_PIECE
	};

	private static Color[] PIECE_COLORS = {
		Color.red, Color.yellow, Color.magenta, Color.pink, Color.green, Color.cyan, Color.orange
	};

	public boolean gameIsOver() {
		int height = p.length;
		if (pieceRow - height <= 0) {
			return true;
		}
		return false;
	}

	public boolean[][] newFallingPiece() {
		Random r = new Random();
		int i = r.nextInt(7);

		boolean[][] fallingPiece = PIECES[i];
		this.pieceColor = PIECE_COLORS[i];

		this.pieceRow = 0;
		int offset = (fallingPiece[0].length+1)/2;
		this.pieceCol = 5 - offset;

		return fallingPiece;	
	}

	public boolean isLegal() {
		//off the grid?
		int maxRow = pieceRow+p.length-1;
		if (maxRow >= 20 || pieceCol < 0 || pieceCol+p[0].length > 10) {
			return false;
		}
		for (int i = 0; i < p.length; i++) { //rows of array
			for (int j = 0; j < p[0].length; j++) { //cols of array
				try {
					if (p[i][j] == true && board[pieceRow+i][pieceCol+j] != empty) {
						return false;
					}
				} catch (NullPointerException ex) {
					throw ex;
				} catch (ArrayIndexOutOfBoundsException e) {
					throw e;
				}
			}
		}
		return true;
	}
	
	public boolean isOnBoard(int r, int c) {
		if (r >= 0 && r <= rows-1 && cols >= 0 && cols <= cols-1) {
			return true;
		}
		return false;
	}

	public void moveFallingPiece(int[] direction) {
		int dr = direction[0];
		int dc = direction[1];
		moveFallingPiece(dr, dc);
	}

	public boolean moveFallingPiece(int drow, int dcol) {
		pieceRow += drow;
		pieceCol += dcol;
		if (!isLegal()) {
			pieceRow -= drow;
			pieceCol -= dcol;
			return false;
		}
		return true;
	}

	public void rotateFallingPiece() {
		boolean[][] original = p;
		int row = pieceRow;
		int col = pieceCol;
		int width = p.length-1;
		int height = p[0].length - 1;

		boolean[][] newPiece = new boolean[height+1][width+1];

		for (int i = 0; i <= height; i++) {
			for (int j = 0; j <= width; j++) {
				newPiece[i][j] = original[j][height-i];
			}
		}
		p = newPiece;
		int rowShift = (original.length-1-height)/2;
		int colShift = (original[0].length-1-width)/2;
		pieceRow += rowShift;
		pieceCol += colShift;

		if (!isLegal()) {
			p = original;
			pieceRow -= rowShift;
			pieceCol -= colShift;
		}
	}
	
	public void dropDown() {
		boolean notDropped = true;
		while (notDropped) {
			pieceRow++;
			if (!isLegal()) {
				pieceRow--;
				notDropped = false;
			}
		}
	}
	
	public void removeLines() {
		for (int i = rows-1; i > 0; i--) {
			boolean full = rowFull(i);
			if (full) {
				counter++;
				//System.out.println(counter);
			}
			int rowsCleared = 0;
			while (full) {
				for (int k = i; k > 0; k--) {
					for (int j = 0; j < cols; j++) {
						board[k][j] = board[k-1][j];
						rowsCleared++;
					}
				}
				full = rowFull(i);
			}
			//System.out.println(rowsCleared);
		}
	}

	public boolean rowFull(int row) {
		for (Color c : board[row]) {
			if (c == Color.black) {
				return false;
			}
		} 
		return true;
	}

	public void setTimer() {
		if (counter == 10) {
			counter = 0;
			delay -= 25;
			timer.setDelay(delay);
		}
	}
	
	//GUI
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {	
				drawCell(g, board[i][j], j, i);
			}
		}
		paintFallingPiece(g);
	}


	public void paintFallingPiece(Graphics g) {
		for (int i = 0; i < p.length; i++) {
			for (int j = 0; j < p[0].length; j++) {
				//System.out.println(i + " " + j);
				if (p[i][j] == true) {
					drawCell(g, pieceColor, j+pieceCol, i+pieceRow);
				}
			}
		}
	}

	public void placeFallingPiece() {
		for (int i = 0; i < p.length; i++) {
			for (int j = 0; j < p[0].length; j++) {
				if (p[i][j] == true) {
					board[pieceRow+i][pieceCol+j] = pieceColor;
				}
			}
		}
	}

	public void drawCell(Graphics g, Color color, int x, int y) {
		int xPos = x*30;
		int yPos = y*30;

		if (color != Color.black) {
			g.setColor(Color.white);
			g.drawRect(xPos, yPos, 30, 30);
		}

		g.setColor(color);
		g.fillRect(xPos+1, yPos+1, 29, 29);
	}


	//MAIN
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setTitle("Tetris Simple");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(300, 620);

		Tetris tetris = new Tetris();
		f.add(tetris);
		f.setVisible(true);
	}
} 
