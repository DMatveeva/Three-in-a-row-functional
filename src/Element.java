public class Element {

    public static final char EMPTY = '0';
    public char symbol;

    public Element(char c) {
        symbol = c;
    }

    public Element(Element e) {
        symbol = e.symbol;
    }
    //TODO make this copy by value
}
