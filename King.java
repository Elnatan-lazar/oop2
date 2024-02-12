public class King extends ConcretePiece{
    public King(Player owner){
        this.Owner=owner;
        this.Number=7;

    }


    @Override
    public String getType() {
        return "♔";
    }

    @Override
    public String toString() {
        return "K"+this.getNumber()+":";
    }
}
