```java
import java.util.Optional;

public static BoardState processCascade(BoardState boardState) {
    Board board = boardState.board;

    List<Match> matches = findMatches(board);
    if (matches.isEmpty()) {
        return boardState;
    }

    return Optional.of(boardState)
            .map(bs -> removeMatches(bs, matches))
            .map(Game::fillEmptySpaces)
            .map(Game::processCascade)
            .get();
}
```
