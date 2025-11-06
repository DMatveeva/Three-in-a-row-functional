import java.io.Console;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    static Random r = new Random();
    static char[] symbols = {'A', 'B', 'C', 'D', 'E', 'F'};

    // i - строка
    // j - колонка
    public static void draw(Board board) {
        System.out.println("  0 1 2 3 4 5 6 7");
        for (int i = 0; i < 8; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(board.cells[i][j].symbol + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void draw4(Board board) {
        System.out.println("  0 1 2 3");
        for (int i = 0; i < 4; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 4; j++) {
                System.out.print(board.cells[i][j].symbol + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static Board cloneBoard(Board board) {
        Board b = new Board(board.size);
        for (int row = 0; row < board.size; row++) {
            for (int col = 0; col < board.size; col++) {
                b.cells[row][col] = new Element(board.cells[row][col]);
            }
        }
        return b;
    }

    public static BoardState readMove(BoardState bs) {
        System.out.println(">");
        Console console = System.console();
        String input = console.readLine();

        if (input.equals("q")) {
            System.exit(0);
        }

        Board board = cloneBoard(bs.board);
        String[] coords = input.split(" ");
        int x = Integer.getInteger(coords[1]);
        int y = Integer.getInteger(coords[0]);
        int x1 = Integer.getInteger(coords[3]);
        int y1 = Integer.getInteger(coords[2]);
        Element e = board.cells[x][y];
        board.cells[x][y] = new Element(board.cells[x][y1]); //?
        board.cells[x1][y1] = new Element(e);
        BoardState bb = new BoardState(board, bs.score);
        return bb;
    }

    public static BoardState initializeGame() {
        return null;
    }

    public static List<Match> findMatches(Board board) {
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
                    addMatchIfValid(matches, row, startCol, col - startCol, MatchDirection.HORIZONTAL);
                    startCol = col + 1;
                    continue;
                }

                // Проверяем совпадение символов для непустых ячеек
                if (board.cells[row][col].symbol != board.cells[row][startCol].symbol) {
                    addMatchIfValid(matches, row, startCol, col - startCol, MatchDirection.HORIZONTAL);
                    startCol = col;
                } else if (col == board.size - 1) {
                    addMatchIfValid(matches, row, startCol, col - startCol + 1, MatchDirection.HORIZONTAL);
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
                    addMatchIfValid(matches, startRow, col, row - startRow, MatchDirection.VERTICAL);
                    startRow = row + 1;
                    continue;
                }

                // Проверяем совпадение символов для непустых ячеек
                if (board.cells[row][col].symbol != board.cells[startRow][col].symbol) {
                    addMatchIfValid(matches, startRow, col, row - startRow, MatchDirection.VERTICAL);
                    startRow = row;
                } else if (row == board.size - 1) {
                    addMatchIfValid(matches, startRow, col, row - startRow + 1, MatchDirection.VERTICAL);
                }
            }
        }
        return matches;
    }

    private static void addMatchIfValid(List<Match> matches, int row, int col, int length, MatchDirection direction) {
        // Учитываем только комбинации из 3 и более элементов (ТЗ)
        if (length >= 3) {
            matches.add(new Match(direction, row, col, length));
        }
    }

    public static BoardState removeMatches(BoardState currentState, List<Match> matches) {
        if (matches == null || matches.isEmpty())
            return currentState;

        // Шаг 1: Помечаем ячейки для удаления
        Element[][] markedCells = markCellsForRemoval(currentState.board, matches);

        // Шаг 2: Применяем гравитацию
        Element[][] gravityAppliedCells = applyGravity(markedCells, currentState.board.size); //в кыадрате?

        // Шаг 3: Подсчитываем очки
        int removedCount = matches.stream().mapToInt(m -> m.length).sum();
        int newScore = currentState.score + calculateScore(removedCount);

        // Возвращаем НОВОЕ состояние
        Board newBoard = new Board(currentState.board.size);
        newBoard.cells = gravityAppliedCells;
        return new BoardState(newBoard, newScore);
    }

    private static Element[][] markCellsForRemoval(Board board, List<Match> matches) {
        Element[][] newCells = (Element[][]) board.cells.clone();

        for (Match match : matches) {
            for (int i = 0; i < match.length; i++) {
                int row = match.direction == MatchDirection.HORIZONTAL ? match.row : match.row + i;
                int col = match.direction == MatchDirection.HORIZONTAL ? match.col + i : match.col;

                newCells[row][col] = new Element(Element.EMPTY);
            }
        }

        return newCells;
    }

    private static Element[][] applyGravity(Element[][] cells, int size) {
        Element[][] newCells = new Element[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                newCells[row][col] = new Element(Element.EMPTY);
            }
        }

        for (int col = 0; col < size; col++) {
            int newRow = size - 1;
            for (int row = size - 1; row >= 0; row--) {
                if (cells[row][col].symbol != Element.EMPTY) {
                    newCells[newRow][col] = cells[row][col];
                    newRow--;
                }
            }
        }

        return newCells;
    }

    private static int calculateScore(int removedCount) {
        // Базовая система подсчета очков: 10 за каждый элемент
        return removedCount * 10;
    }

    public static BoardState fillEmptySpaces(BoardState currentState) {
        if (currentState.board.cells == null)
            return currentState;

        Element[][] newCells = (Element[][]) currentState.board.cells.clone();

        for (int row = 0; row < currentState.board.size; row++) {
            for (int col = 0; col < currentState.board.size; col++) {
                if (newCells[row][col].symbol == Element.EMPTY) {
                    char symbol = Game.symbols[Game.r.nextInt(Game.symbols.length)];
                    newCells[row][col] = new Element(symbol);
                }
            }
        }

        Board newBoard = new Board(currentState.board.size);
        newBoard.cells = newCells;
        return new BoardState(newBoard, currentState.score);
    }

    public static BoardState processCascade(BoardState boardState) {
        Board board = boardState.board;
        List<Match> matches = findMatches(board);
        if (matches.isEmpty()) {
            return boardState;
        }
        BoardState boardStateAfterRemoval = removeMatches(boardState, matches);
        BoardState newBoardState = fillEmptySpaces(boardStateAfterRemoval);

        return processCascade(newBoardState);
    }
}