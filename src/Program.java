public class Program {
    public static void main(String[] args)
    {
        BoardState bs = Game.initializeGame();
        while(true) {
            Game.draw(bs.board);
            bs = Game.readMove(bs);
        }
    }
}
