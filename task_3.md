Возможно, чтобы перенести конвейерный стиль, нужно перенести методы fillEmptySpaces и processCascade в класс BoardState.

```java
public static BoardState initializeGame(int boardSize) {

    Board board = new Board(boardSize);
    return new BoardState(board, 0)
            .fillEmptySpaces()
            .processCascade();
}

```