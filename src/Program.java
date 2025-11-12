public class Program {
    public static void main(String[] args)
    {
        int boardSize = 8;
        BoardState bs = Game.initializeGame(boardSize);
        while(true) {
            Game.draw(bs.board);
            bs = Game.readMove(bs);
        }
    }
}
