import java.util.ArrayList;
import java.util.List;

public class BoardState {
    public final Board board; //todo
    public final int score;

    public BoardState(Board board, int score) {
        this.board = board;
        this.score = score;
    }

    public BoardState fillEmptySpaces() {
        BoardState currentState = this;
        int boardSize = currentState.board.size;

        if (currentState.board.cells == null) {
            return currentState;
        }

        Element[][] newCells = currentState.board.cells.clone(); // make shallow copy of original cells

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (newCells[row][col].symbol == Element.EMPTY) {
                    char symbol = Game.symbols[Game.r.nextInt(Game.symbols.length)];
                    newCells[row][col] = new Element(symbol); // put new Element to copied cells
                }
            }
        }

        Board newBoard = new Board(boardSize);
        newBoard.cells = newCells;
        return new BoardState(newBoard, currentState.score);
    }

    public BoardState processCascade() {
        Board board = this.board;
        List<Match> matches = findMatches(board);
        if (matches.isEmpty()) {
            return this;
        }

        return this.removeMatches(matches)
                .fillEmptySpaces()
                .processCascade();
    }

    public List<Match> findMatches(Board board) {
        ArrayList<Match> matches = new ArrayList<>();

        // Горизонтальные комбинации
        for (int row = 0; row < board.size; row++) {
            int startCol = 0;
            for (int col = 1; col < board.size; col++) {
                // Пропускаем пустые ячейки в начале строки
                if (board.cells[row][startCol].symbol == Element.EMPTY) {
                    startCol = col;
                    continue;
                }

                // Если текущая ячейка пустая, обрываем текущую последовательность
                if (board.cells[row][col].symbol == Element.EMPTY) {
                    Game.addMatchIfValid(matches, row, startCol, col - startCol, MatchDirection.HORIZONTAL);
                    startCol = col + 1;
                    continue;
                }

                // Проверяем совпадение символов для непустых ячеек
                if (board.cells[row][col].symbol != board.cells[row][startCol].symbol) {
                    Game.addMatchIfValid(matches, row, startCol, col - startCol, MatchDirection.HORIZONTAL);
                    startCol = col;
                } else if (col == board.size - 1) {
                    Game.addMatchIfValid(matches, row, startCol, col - startCol + 1, MatchDirection.HORIZONTAL);
                }
            }
        }

        // Вертикальные комбинации
        for (int col = 0; col < board.size; col++) {
            int startRow = 0;
            for (int row = 1; row < board.size; row++) {
                // Пропускаем пустые ячейки в начале столбца
                if (board.cells[startRow][col].symbol == Element.EMPTY) {
                    startRow = row;
                    continue;
                }

                // Если текущая ячейка пустая, обрываем текущую последовательность
                if (board.cells[row][col].symbol == Element.EMPTY) {
                    Game.addMatchIfValid(matches, startRow, col, row - startRow, MatchDirection.VERTICAL);
                    startRow = row + 1;
                    continue;
                }

                // Проверяем совпадение символов для непустых ячеек
                if (board.cells[row][col].symbol != board.cells[startRow][col].symbol) {
                    Game.addMatchIfValid(matches, startRow, col, row - startRow, MatchDirection.VERTICAL);
                    startRow = row;
                } else if (row == board.size - 1) {
                    Game.addMatchIfValid(matches, startRow, col, row - startRow + 1, MatchDirection.VERTICAL);
                }
            }
        }
        return matches;
    }

    public BoardState removeMatches(List<Match> matches) {
        BoardState currentState = this;
        if (matches == null || matches.isEmpty())
            return currentState;

        // Шаг 1: Помечаем ячейки для удаления
        Element[][] markedCells = Game.markCellsForRemoval(currentState.board, matches);

        // Шаг 2: Применяем гравитацию
        Element[][] gravityAppliedCells = Game.applyGravity(markedCells, currentState.board.size);

        // Шаг 3: Подсчитываем очки
        int removedCount = matches.stream().mapToInt(m -> m.length).sum();
        int newScore = currentState.score + Game.calculateScore(removedCount);

        // Возвращаем НОВОЕ состояние
        Board newBoard = new Board(currentState.board.size);
        newBoard.cells = gravityAppliedCells;
        return new BoardState(newBoard, newScore);
    }




}
