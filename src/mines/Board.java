package mines;

import java.awt.Graphics;
import java.awt.Image;
import java.security.SecureRandom;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Board extends JPanel {
	private static final long serialVersionUID = 6195235521361212179L;
	
	protected static final int NUM_IMAGES = 13;
	protected static final int CELL_SIZE = 15;

	protected static final int COVER_FOR_CELL = 10;
	protected static final int MARK_FOR_CELL = 10;
	protected static final int EMPTY_CELL = 0;
	protected static final int MINE_CELL = 9;
	protected static final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
	protected static final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

	protected static final int DRAW_MINE = 9;
	protected static final int DRAW_COVER = 10;
	protected static final int DRAW_MARK = 11;
	protected static final int DRAW_WRONG_MARK = 12;
	private SecureRandom random = new SecureRandom();
	protected int[] field;
	protected boolean inGame;
	protected int minesLeft;
	protected transient Image[] img;
	protected int mines = 40;
	protected int rows = 16;
	protected int cols = 16;
	protected int allCells;
	protected JLabel statusbar;

    public Board(JLabel statusbar) {

        this.statusbar = statusbar;

        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {
			img[i] =
                    (new ImageIcon(getClass().getClassLoader().getResource((i)
            			    + ".gif"))).getImage();
        }

        setDoubleBuffered(true);

        addMouseListener(new MinesAdapter(this));
        newGame();
    }

    public void newGame() {

        int currentCol;
        
        int i = 0;
        int position = 0;
        int cell = 0;

        inGame = true;
        minesLeft = mines;

        allCells = rows * cols;
        field = new int[allCells];
        
        for (i = 0; i < allCells; i++)
            field[i] = COVER_FOR_CELL;

        statusbar.setText(Integer.toString(minesLeft));

        i = 0;
        while (i < mines) {
        	position = random.nextInt(0,256);

            if ((position < allCells) &&
                (field[position] != COVERED_MINE_CELL)) {

                currentCol = position % cols;
                field[position] = COVERED_MINE_CELL;
                i++;

                if (currentCol > 0) { 
                    cell = position - 1 - cols;
                    if ((cell >= 0) && (field[cell] != COVERED_MINE_CELL))
                            field[cell] += 1;
                    cell = position - 1;
                    if ((cell >= 0) && (field[cell] != COVERED_MINE_CELL))
                            field[cell] += 1;

                    cell = position + cols - 1;
                    if ((cell < allCells) && (field[cell] != COVERED_MINE_CELL))
                            field[cell] += 1;
                }

                cell = position - cols;
                if ((cell >= 0) && (field[cell] != COVERED_MINE_CELL))
                        field[cell] += 1;
                cell = position + cols;
                if ((cell < allCells) && (field[cell] != COVERED_MINE_CELL))
                        field[cell] += 1;

                if (currentCol < (cols - 1)) {
                    cell = position - cols + 1;
                    if ((cell >= 0) && (field[cell] != COVERED_MINE_CELL))
                            field[cell] += 1;
                    cell = position + cols + 1;
                    if ((cell < allCells) && (field[cell] != COVERED_MINE_CELL))
                            field[cell] += 1;
                    cell = position + 1;
                    if ((cell < allCells) && (field[cell] != COVERED_MINE_CELL))
                            field[cell] += 1;
                }
            }
        }
    }


    public void findEmptyCells(int j) {

        int currentCol = j % cols;
        int cell;

        if (currentCol > 0) { 
            cell = j - cols - 1;
            if ((cell >= 0) && (field[cell] > MINE_CELL)) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        findEmptyCells(cell);
                }

            cell = j - 1;
            if ((cell >= 0) && (field[cell] > MINE_CELL)) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        findEmptyCells(cell);
                }

            cell = j + cols - 1;
            if ((cell < allCells) && (field[cell] > MINE_CELL)) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        findEmptyCells(cell);
                }
        }

        cell = j - cols;
        if ((cell >= 0) && (field[cell] > MINE_CELL)) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL)
                    findEmptyCells(cell);
            }

        cell = j + cols;
        if ((cell < allCells) && (field[cell] > MINE_CELL)) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL)
                    findEmptyCells(cell);
            }

        if (currentCol < (cols - 1)) {
            cell = j - cols + 1;
            if ((cell >= 0) && (field[cell] > MINE_CELL)) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        findEmptyCells(cell);
                }

            cell = j + cols + 1;
            if ((cell < allCells) && (field[cell] > MINE_CELL)) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        findEmptyCells(cell);
                }

            cell = j + 1;
            if ((cell < allCells) && (field[cell] > MINE_CELL)) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        findEmptyCells(cell);
                }
        }

    }

    @Override
    public void paint(Graphics g) {

        int cell = 0;
        int uncover = 0;


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                cell = field[(i * cols) + j];

                if (inGame && cell == MINE_CELL)
                    inGame = false;

                if (!inGame) {
                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }


                } else {
                    if (cell > COVERED_MINE_CELL)
                        cell = DRAW_MARK;
                    else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE),
                    (i * CELL_SIZE), this);
            }
        }


        if (uncover == 0 && inGame) {
            inGame = false;
            statusbar.setText("Game won");
        } else if (!inGame)
            statusbar.setText("Game lost");
    }


   
}