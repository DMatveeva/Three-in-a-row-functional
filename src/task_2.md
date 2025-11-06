```java
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
    
```