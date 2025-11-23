В предыдущем задании я сделала конвейер используя класс Java java.util.Optional.
Но в этот раз попробовала сделать так же как и в примере C#. Определила статическую функцию pipe в классе BoardState.
Получилось так:
```java
public static BoardState processCascade(BoardState currentState) {
        boolean debugMode = true;
        return findMatches(currentState.board).isEmpty() ?
                currentState :
                BoardState
                        .pipe(currentState, bs -> removeMatches(bs, findMatches(bs.board)))
                        .draw(currentState, debugMode)
                        .pipe(currentState, Game::fillEmptySpaces)
                        .draw(currentState, debugMode)
                        .pipe(currentState, Game::processCascade);
    }
```
Т.к. в Java нет способа записать pipe(this BoardState...), чтобы вызывать статическую функцию как метод экземпляра, то приходится в каждой строчке указывать экземпляр класса (currentState), к которому применяем статическую функцию. Но тут также возникла проблема, что появились предупреждения вида: Static member 'BoardState.draw(BoardState, boolean)' accessed via instance reference. 

Поэтому вернулась к тому, чтобы сделать через Optional. Вынесла конвейер в отдельную функцию:
```java

public static BoardState processCascade(BoardState currentState) {
    boolean debugMode = true;
    return findMatches(currentState.board).isEmpty() ?
            currentState :
            processCascadeRec(currentState, debugMode);
}

private static BoardState processCascadeRec(BoardState currentState, boolean debugMode) {
    return Optional.of(currentState)
            .map(bs -> removeMatches(bs, findMatches(bs.board)))
            .map(bs -> draw(bs, debugMode))
            .map(Game::fillEmptySpaces)
            .map(bs -> draw(bs, debugMode))
            .map(Game::processCascade)
            .get();
}
```
