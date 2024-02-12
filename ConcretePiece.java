import java.util.ArrayList;
import java.util.List;

public abstract class ConcretePiece implements  Piece{
    protected Player Owner;
    private final ArrayList<Position> allPosition=new ArrayList<Position>();
    protected int distance=0;
    protected int Number=-1;
    @Override
    public Player getOwner() {
        return Owner;
    }

    @Override
    public abstract String getType() ;


    public abstract String toString();
    public int getNumber(){
        return Number;
    }
    public void setNumber(int num){
         Number=num;
    }
    public void  addStep(Position p){
        if(!allPosition.isEmpty()){ // if it not the first position
            Position temp=allPosition.get(allPosition.size()-1);        //get the last position and calculet the distance;
            if(temp.getX()!=p.getX()){
                distance=distance+Math.abs(temp.getX()- p.getX());
            }
            else {
                distance=distance+Math.abs(temp.getY()- p.getY());
            }
        }
        allPosition.add(p);
    }


    public  void removeLastStep(){
        allPosition.remove(allPosition.size()-1);
    }

    public int getNumberOfMoves() {
       return allPosition.size();

    }

    public ArrayList<Position> getMoves() {
        return  allPosition;
    }
}
