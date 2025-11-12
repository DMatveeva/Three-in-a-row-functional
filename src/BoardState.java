import java.util.List;

public class BoardState {
    public final Board board; //todo
    public final int score;

    public BoardState(Board board, int score) {
        this.board = board;
        this.score = score;
    }


    public BoardState fillEmptySpaces() {

        if (this.board.cells == null) {
            return new BoardState(this.board, this.score);
        }

        Element[][] newCells = (Element[][]) this.board.cells.clone();

        for (int row = 0; row < this.board.size; row++) {
            for (int col = 0; col < this.board.size; col++) {
                if (newCells[row][col].symbol == Element.EMPTY) {
                    char symbol = Game.symbols[Game.r.nextInt(Game.symbols.length)];
                    newCells[row][col] = new Element(symbol);
                }
            }
        }

        Board newBoard = new Board(this.board.size);
        newBoard.cells = newCells;
        return new BoardState(newBoard, this.score);
    }
}
