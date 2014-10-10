import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

public class Tetris extends JPanel { 
	
	final Color empty = Color.black;
	public static int DEFAULT_R = 20;
	public static int DEFAULT_C = 10;
	public int rows;
	public int cols;
	public Color[][] board; 
	
	/*CURRENT PIECE*/
	boolean[][] p = newFallingPiece();
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
		board[5][5] = Color.red;
		board[10][9] = Color.blue;
		board[10][1] = Color.blue;
		board[rows-1][0] = Color.yellow;
		board[rows-1][cols-1] = Color.green;
		
		
		this.setFocusable(true);
		KeyListener l = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
    			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
    				moveFallingPiece(0, -1);
    				repaint();
    			}
    			else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
    				moveFallingPiece(0, 1);
    				repaint();
    			}
    			else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
    				moveFallingPiece(1, 0);
    				repaint();
    			}
    			else if (e.getKeyCode() == KeyEvent.VK_UP) {
    				rotateFallingPiece();
    				repaint();
    			}
            }
        };
        addKeyListener(l);
	}
	
	public Tetris() {
		this(DEFAULT_R, DEFAULT_C);
	}
	
	//SEVEN STANDARD PIECES (TETROMINOES):
	static boolean T = true; //T for tetrominoes; readability
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
	
	public boolean[][] newFallingPiece() {
		Random r = new Random();
		int i = r.nextInt(7);
		System.out.println("Index: " + i);
		boolean[][] fallingPiece = PIECES[i];
		this.pieceColor = PIECE_COLORS[i];
		
		this.pieceRow = 0;
		int offset = (fallingPiece[0].length+1)/2;
		//this.pieceCol = this.cols/2 - offset;
		this.pieceCol = 5 - offset;
		
		return fallingPiece;	
	}
	
	public void moveFallingPiece(int drow, int dcol) {
		pieceRow += drow;
		pieceCol += dcol;
		if (!isLegal()) {
			pieceRow -= drow;
			pieceCol -= dcol;
			System.out.println("COLLISION");
		}
	}
	
	public void rotateFallingPiece() {
		System.out.println("ROTATE-PIECE CALLED.");
		boolean[][] original = p;
		int row = pieceRow;
		int col = pieceCol;
		int width = p.length-1;
		int height = p[0].length - 1;
		
		boolean[][] newPiece = new boolean[height+1][width+1];
		
		for (int i = 0; i <= height; i++) {
			for (int j = 0; j <= width; j++) {
				//System.out.print("R/C: " + i + "," + j);
				//System.out.println("  From: " + j + "," + (height-i));
				newPiece[i][j] = original[j][height-i];
			}
		}
		p = newPiece;
		int rowShift = (original.length-1-height)/2;
		int colShift = (original[0].length-1-width)/2;
		pieceRow += rowShift;
		pieceCol += colShift;
		
		//DEBUGGING
		/*System.out.println("Original r/c: " + row + "," + col);
		System.out.println(original.length-1);
		System.out.println(height);
		System.out.println(original.length-1-height);
		System.out.println("Difference in r/c: " + (original.length-1-height)/2 + "," + (original[0].length-1-width)/2);
		System.out.println("Final r/c: " + pieceRow + "," + pieceCol);
		System.out.println();*/
		
		if (!isLegal()) {
			p = original;
			pieceRow -= rowShift;
			pieceCol -= colShift;
		}
	}
	
	public boolean isLegal() {
		//off the grid?
		int maxRow = pieceRow+p.length-1;
		if (maxRow >= 20 || pieceCol < 0 || pieceCol+p[0].length > 10) {
			System.out.println("Out of bounds");
			return false;
		}
		for (int i = 0; i < p.length; i++) {
			for (int j = 0; j < p[0].length; j++) {
				System.out.print(p[i][j] + ", ");
				for (boolean cell: p[i]) {
					if (cell == true && board[pieceRow+i][pieceCol+j] != empty) {
						System.out.println();
						System.out.println("R/C:" + (pieceRow+i) + "," + (pieceCol+j));
						System.out.println();
						System.out.println("COLLIDER: " + cell + ": " + i + "," + j);
						System.out.println("NON-EMPTY CELL: " + (pieceRow+i) + "," + (pieceCol+j));
						return false;
					}
				}
			}
		}
		System.out.println();
		return true;
		
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
	
	public void drawCell(Graphics g, Color color, int x, int y) {
		int xPos = x*30;
		int yPos = y*30;
		
		g.setColor(Color.white);
		g.drawRect(xPos, yPos, 30, 30);
		
		g.setColor(color);
		g.fillRect(xPos, yPos, 29, 29);
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
