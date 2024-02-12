public class Pawn extends ConcretePiece{

    private int kills=0;
    public  Pawn(Player onwer){
        this.Owner=onwer;


    }
    public void kill(){
        kills++;
    }
    public void delteKill(){
        kills--;
    }
    public int getKills(){
        return  this.kills;
    }
    public String printKills(){
        return  this.kills+ " kills";
    }
    @Override
    public String getType() {
        if(this.Owner.isPlayerOne()){
            return  "♙";
        }
        else return "♟";
    }

    @Override
    public String toString() {
        if(this.Owner.isPlayerOne()){
            return  "D"+this.getNumber()+":";
        }
        else {
            return "A"+this.getNumber()+":";
        }
    }

}
