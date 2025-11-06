import java.util.ArrayList;
import java.util.List;

public class Match { //TODO record

    public final MatchDirection direction;
    public final int row;
    public final int col;
    public final int length;

    public Match(MatchDirection direction, int row, int col, int length) {
        this.direction = direction;
        this.row = row;
        this.col = col;
        this.length = length;
    }
}