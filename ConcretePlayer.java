public class ConcretePlayer implements Player{
    private final boolean isPlayerOne;
    private int wins=0;

    public ConcretePlayer(boolean isPlayerOne) {
        this.isPlayerOne=isPlayerOne;

    }

    @Override
    public boolean isPlayerOne() {
        return isPlayerOne;
    }

    @Override
    public int getWins() {
        return wins;
    }
    public void win(){
        wins++;
    }
    public void clear(){
        wins=0;
    }
}
