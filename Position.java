import java.util.ArrayList;

public class Position {
   private int x;
     private  int y;
     private ArrayList<Piece> piecesSteped=new ArrayList<Piece>();


    Position(int x, int y){
        this.x=x;
        this.y=y;
    }
    Position(Position p){
        this.x=p.x;
        this.y=p.y;
    }


    @Override
    public String toString(){
        return "("+x+", "+y+")";
    }
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public  void addPeice(Piece p){    // add only difrrent players;

        if(piecesSteped.contains(p)==false){
            piecesSteped.add(p);
        }

    }
    public  int getPiecesNumber(){
        return piecesSteped.size();
    }
    public String printNumPieces(){
        return this.getPiecesNumber()+ " pieces";
    }
    public ArrayList<Piece> getPiecesSteped(){
        return piecesSteped;
    }
    public void deletePiece(Piece piece) {
        this.piecesSteped.remove(piece);
    }
}
