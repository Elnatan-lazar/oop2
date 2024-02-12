import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;

public class GameLogic implements PlayableLogic{


    public static int Size=11;


    private boolean isTheKingDead=false;
    private ConcretePiece Board[][]=new ConcretePiece[Size][Size];
    private Position [][] positions=new Position[Size][Size];
    ArrayList<ConcretePiece> PiecesP1=new ArrayList<ConcretePiece>();
    ArrayList<ConcretePiece> allPices=new ArrayList<ConcretePiece>();
    ArrayList<ConcretePiece> PiecesP2=new ArrayList<ConcretePiece>();

    private  Position kingPosition=new Position(5,5);
    private Pawn tempPawn=new Pawn(null);

    private Stack<ConcretePiece[][]> copy=new Stack<ConcretePiece[][]>();

    private  final  ConcretePlayer attack=new ConcretePlayer(false);
    private  final ConcretePlayer defeend =new ConcretePlayer(true);

    private  King King=new King(defeend);
    private  boolean isGameFinished=false;
    private boolean HowsTurn=true;

    public GameLogic(){
        this.reset();
    }

    @Override
    public boolean move(Position a, Position b) {


        // if the selection or the destintion  is outside the map
        if(inMap(a)==false){return  false;}
        if((inMap(b)==false&& Board[a.getX()][a.getY()] instanceof Pawn)||inMapKing(b)==false){
            return false;
        }

        if(a.getX()==b.getX()&&a.getY()==b.getY()){return false;} // if it's the same position
        if(a.getX()!=b.getX()&&a.getY()!=b.getY()){return false;} // if it's a diagnal move
        if(checkWay(a,b)==false){return false;}     // check if the move skips on other pieces


        copyMap();
        if(getPieceAtPosition(b)!=null){return false;}
        if(getPieceAtPosition(a).getOwner().isPlayerOne()&&HowsTurn==true){return false;}
        if(!getPieceAtPosition(a).getOwner().isPlayerOne()&&HowsTurn==false){return false;}


        if(Board[a.getX()][a.getY()] instanceof King){
            Board[a.getX()][a.getY()]=null;
            Board[b.getX()][b.getY()]=King;
            kingPosition=new Position(b);

            if((b.getX()==0&&b.getY()==0)||(b.getX()==0&&b.getY()==10)||(b.getX()==10&&b.getY()==0)||(b.getX()==10&&b.getY()==10)){ // a corner
                isGameFinished=true;
                defeend.win();

            }
        }

        else {
            if((b.getX()==0&&b.getY()==0)||(b.getX()==0&&b.getY()==10)||(b.getX()==10&&b.getY()==0)||(b.getX()==10&&b.getY()==10)){return  false;}// a pawn cannot move to a corner
            tempPawn= (Pawn)  Board[a.getX()][a.getY()];
            Board[b.getX()][b.getY()]=tempPawn;
            Board[a.getX()][a.getY()]=null;

        }
        if(HowsTurn==true){ // if players 2 turn chek if the king dead
            scanKing();
        }
        //printBoard();
        positions[b.getX()][b.getY()].addPeice(getPieceAtPosition(b)); // if the move is allowed we add the piece to the squars collaction
        ConcretePiece temp=(ConcretePiece) getPieceAtPosition(b);
        temp.addStep(b); //add the step to the piece that just walked
        if(isGameFinished==false){ // else just play the game
            if(getPieceAtPosition(b).getType().equals("King")==false){

                scanMap(b);
            }


        }
        else {
            PrintSteps();
        }





        HowsTurn=!HowsTurn; //change the turn

        return true;
    }



    public  boolean checkWay(Position a1,Position b1) {
        Position a=new Position(a1);
        Position b=new Position(b1);
        if (Board[a.getX()][a.getY()] != null) {
            if (a.getX() != b.getX()) {  // if we go thee the sides
                if (a.getX() > b.getX()) {  // if we fgo left
                    while (a.getX() > b.getX()) {
                        if (getPieceAtPosition(b) != null) {
                            return false;
                        }
                        b.setX(b.getX()+1);
                    }
                    return true;
                } else {
                    while (a.getX()< b.getX()) {
                        if (getPieceAtPosition(b) != null) {
                            return false;
                        }
                        b.setX(b.getX()-1);
                    }
                    return true;

                }
            } else {
                if (a.getY() > b.getY()) {  // if we fgo left
                    while (a.getY() > b.getY()) {
                        if (getPieceAtPosition(b) != null) {
                            return false;
                        }
                        b.setY(b.getY()+1);
                    }
                    return true;
                } else {
                    while (a.getY() < b.getY()) {
                        if (getPieceAtPosition(b) != null) {
                            return false;
                        }
                        b.setY(b.getY()-1);
                    }
                    return true;

                }
            }
        }
        else {return false;}

    }

    public void scanKing(){
        boolean flagFriends=false;
        boolean flagMove=false;
        Position left=new Position(kingPosition.getX()-1,kingPosition.getY());
        Position right=new Position(kingPosition.getX()+1,kingPosition.getY());
        Position up=new Position(kingPosition.getX(),kingPosition.getY()+1);
        Position down=new Position(kingPosition.getX(),kingPosition.getY()-1);


        if(inMap(left)&&inMap(right)&&inMap(up)&&inMap(down)){ // the king serunded
            if(getPieceAtPosition(left)==null||getPieceAtPosition(right)==null||getPieceAtPosition(up)==null||getPieceAtPosition(down)==null) //if there is a place to move
            {
                return;
            }
            if(getPieceAtPosition(left).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(right).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(up).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(down).getOwner().isPlayerOne()==false)
            {
                isGameFinished=true;
                isTheKingDead=true;
                attack.win();
            }
        }

        if(inMap(left)&&inMap(right)&&inMap(up)&&!inMap(down)){ // the king serunded but down
            if(getPieceAtPosition(left)==null||getPieceAtPosition(right)==null||getPieceAtPosition(up)==null) //if there is a place to move
            {
                return;
            }
            if(getPieceAtPosition(left).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(right).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(up).getOwner().isPlayerOne()==false)
            {
                isGameFinished=true;
                isTheKingDead=true;
                attack.win();
            }
        }
        if(inMap(left)&&inMap(right)&&!inMap(up)&&inMap(down)){ // the king serunded but up
            if(getPieceAtPosition(left)==null||getPieceAtPosition(right)==null||getPieceAtPosition(down)==null) //if there is a place to move
            {
                return;
            }
            if(getPieceAtPosition(left).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(right).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(down).getOwner().isPlayerOne()==false)
            {
                isGameFinished=true;
                isTheKingDead=true;
                attack.win();
            }
        }

        if(inMap(left)&&!inMap(right)&&inMap(up)&&inMap(down)){ // the king serunded but right
            if(getPieceAtPosition(left)==null||getPieceAtPosition(right)==null||getPieceAtPosition(up)==null) //if there is a place to move
            {
                return;
            }
            if(getPieceAtPosition(left).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(up).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(down).getOwner().isPlayerOne()==false)
            {
                isGameFinished=true;
                isTheKingDead=true;
                attack.win();
            }
        }

        if(!inMap(left)&&inMap(right)&&inMap(up)&&inMap(down)){ // the king serunded but left
            if(getPieceAtPosition(right)==null||getPieceAtPosition(up)==null||getPieceAtPosition(down)==null) //if there is a place to move
            {
                return;
            }
            if(getPieceAtPosition(right).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(up).getOwner().isPlayerOne()==false&&
                    getPieceAtPosition(down).getOwner().isPlayerOne()==false)
            {
                isGameFinished=true;
                isTheKingDead=true;
                attack.win();
            }
        }

    }



    public  boolean inMapKing(Position b){
        if(b.getX()<0||b.getX()>10||b.getY()>10||b.getY()<0){
            return false;
        }
        return true;
    }
    public void scanMap(Position p) {
        Piece left = null;
        Piece right = null;
        Piece up = null;
        Piece down = null;

        Position tempPosition = new Position(p.getX(), p.getY());

        Position leftP = new Position(p.getX() - 1, p.getY());
        Position rightP = new Position(p.getX() + 1, p.getY());
        Position upP = new Position(p.getX(), p.getY() + 1);
        Position downP = new Position(p.getX(), p.getY() - 1);

        Position leftP2 = new Position(p.getX() - 2, p.getY());
        Position rightP2 = new Position(p.getX() + 2, p.getY());
        Position upP2 = new Position(p.getX(), p.getY() + 2);
        Position downP2 = new Position(p.getX(), p.getY() - 2);
        if (getPieceAtPosition(p) instanceof Pawn) {
            if (!HowsTurn) {//player 1 turn
                if (inMap(leftP)) {

                    if (getPieceAtPosition(leftP) != null) {
                        if (!Board[leftP.getX()][leftP.getY()].getOwner().isPlayerOne()) { // הכלי שמימינו שייך לשחקן השני
                            if (!inMap(leftP2)) {
                                Board[leftP.getX()][leftP.getY()] = null;
                                Pawn temp = (Pawn) getPieceAtPosition(p);
                                temp.kill();
                            } // if its the coener of the map  the pice dead
                            else {
                                if (getPieceAtPosition(leftP2) != null) {
                                    if (getPieceAtPosition(leftP2).getOwner().isPlayerOne() && (getPieceAtPosition(leftP2) instanceof Pawn)) { //the pice stuck between 2 pices
                                        Board[leftP.getX()][leftP.getY()] = null;
                                        Pawn temp = (Pawn) getPieceAtPosition(p);
                                        temp.kill();
                                    }
                                }
                            }


                        }
                    }
                }


                if (inMap(rightP)) {

                    if (getPieceAtPosition(rightP) != null) {
                        if (!Board[rightP.getX()][rightP.getY()].getOwner().isPlayerOne()) { // הכלי שמימינו שייך לשחקן השני
                            if (!inMap(rightP2)) {
                                Board[rightP.getX()][rightP.getY()] = null;
                                Pawn temp = (Pawn) getPieceAtPosition(p);
                                temp.kill();
                            } // if its the coener of the map  the pice dead
                            else {
                                if (getPieceAtPosition(rightP2) != null) {
                                    if (getPieceAtPosition(rightP2).getOwner().isPlayerOne() && (getPieceAtPosition(rightP2) instanceof Pawn)) { //the pice stuck between 2 pices
                                        Board[rightP.getX()][rightP.getY()] = null;
                                        Pawn temp = (Pawn) getPieceAtPosition(p);
                                        temp.kill();
                                    }
                                }
                            }

                        }
                    }
                }

                if (inMap(upP)) {

                    if (getPieceAtPosition(upP) != null) {
                        if (!Board[upP.getX()][upP.getY()].getOwner().isPlayerOne()) { // הכלי שמימינו שייך לשחקן השני
                            if (!inMap(upP2)) {
                                Board[upP.getX()][upP.getY()] = null;
                                Pawn temp = (Pawn) getPieceAtPosition(p);
                                temp.kill();
                            } // if its the coener of the map  the pice dead
                            else {
                                if (getPieceAtPosition(upP2) != null) {
                                    if (getPieceAtPosition(upP2).getOwner().isPlayerOne() && (getPieceAtPosition(upP2) instanceof Pawn)) { //the pice stuck between 2 pices
                                        Board[upP.getX()][upP.getY()] = null;
                                        Pawn temp = (Pawn) getPieceAtPosition(p);
                                        temp.kill();
                                    }
                                }
                            }

                        }
                    }
                }

                if (inMap(downP)) {

                    if (getPieceAtPosition(downP) != null) {
                        if (!Board[downP.getX()][downP.getY()].getOwner().isPlayerOne()) { // הכלי שמימינו שייך לשחקן השני
                            if (!inMap(downP2)) {
                                Board[downP.getX()][downP.getY()] = null;
                                Pawn temp = (Pawn) getPieceAtPosition(p);
                                temp.kill();
                            } // if its the coener of the map  the pice dead
                            else {
                                if (getPieceAtPosition(downP2) != null) {
                                    if (getPieceAtPosition(downP2).getOwner().isPlayerOne() && (getPieceAtPosition(downP2) instanceof Pawn)) { //the pice stuck between 2 pices
                                        Board[downP.getX()][downP.getY()] = null;
                                        Pawn temp = (Pawn) getPieceAtPosition(p);
                                        temp.kill();
                                    }
                                }
                            }

                        }
                    }
                }


            }


            if (HowsTurn) {//player 2 turn
                if (inMap(leftP)) {
                    if (getPieceAtPosition(leftP) != null && getPieceAtPosition(leftP) instanceof Pawn) {
                        if (Board[leftP.getX()][leftP.getY()].getOwner().isPlayerOne() && Board[leftP.getX()][leftP.getY()].getType().equals("King") == false) { // הכלי שמימינו שייך לשחקן השני
                            if (!inMap(leftP2)) {
                                Board[leftP.getX()][leftP.getY()] = null;
                                Pawn temp = (Pawn) getPieceAtPosition(p);
                                temp.kill();
                            } // if its the coener of the map  the pice dead
                            else {
                                if (getPieceAtPosition(leftP2) != null) {
                                    if (!getPieceAtPosition(leftP2).getOwner().isPlayerOne()) { //the pice stuck between 2 pices
                                        Board[leftP.getX()][leftP.getY()] = null;
                                        Pawn temp = (Pawn) getPieceAtPosition(p);
                                        temp.kill();
                                    }
                                }
                            }

                        }
                    }
                }

                if (inMap(rightP)) {
                    if (getPieceAtPosition(rightP) != null && getPieceAtPosition(rightP) instanceof Pawn) {

                        if (Board[rightP.getX()][rightP.getY()].getOwner().isPlayerOne() && Board[rightP.getX()][rightP.getY()].getType().equals("King") == false) { // הכלי שמימינו שייך לשחקן השני
                            if (!inMap(rightP2)) {
                                Board[rightP.getX()][rightP.getY()] = null;
                                Pawn temp = (Pawn) getPieceAtPosition(p);
                                temp.kill();
                            } // if its the coener of the map  the pice dead
                            else {
                                if (getPieceAtPosition(rightP2) != null) {
                                    if (!getPieceAtPosition(rightP2).getOwner().isPlayerOne()) { //the pice stuck between 2 pices
                                        Board[rightP.getX()][rightP.getY()] = null;
                                        Pawn temp = (Pawn) getPieceAtPosition(p);
                                        temp.kill();
                                    }
                                }
                            }

                        }
                    }

                }

                if (inMap(upP)) {

                    if (getPieceAtPosition(upP) != null && getPieceAtPosition(upP) instanceof Pawn) {
                        if (Board[upP.getX()][upP.getY()].getOwner().isPlayerOne() && Board[upP.getX()][upP.getY()].getType().equals("King") == false) { // הכלי שמימינו שייך לשחקן השני
                            if (!inMap(upP2)) {
                                Board[upP.getX()][upP.getY()] = null;
                                Pawn temp = (Pawn) getPieceAtPosition(p);
                                temp.kill();
                            } // if its the coener of the map  the pice dead
                            else {
                                if (getPieceAtPosition(upP2) != null) {
                                    if (!getPieceAtPosition(upP2).getOwner().isPlayerOne()) { //the pice stuck between 2 pices
                                        Board[upP.getX()][upP.getY()] = null;
                                        Pawn temp = (Pawn) getPieceAtPosition(p);
                                        temp.kill();
                                    }
                                }
                            }

                        }
                    }
                }

                if (inMap(downP)) {

                    if (getPieceAtPosition(downP) != null && getPieceAtPosition(downP) instanceof Pawn) {
                        if (Board[downP.getX()][downP.getY()].getOwner().isPlayerOne() && Board[downP.getX()][downP.getY()].getType().equals("King") == false) { // הכלי שמימינו שייך לשחקן השני
                            if (!inMap(downP2)) {
                                Board[downP.getX()][downP.getY()] = null;
                                Pawn temp = (Pawn) getPieceAtPosition(p);
                                temp.kill();
                            } // if its the coener of the map  the pice dead
                            else {
                                if (getPieceAtPosition(downP2) != null) {
                                    if (!getPieceAtPosition(downP2).getOwner().isPlayerOne()) { //the pice stuck between 2 pices
                                        Board[downP.getX()][downP.getY()] = null;
                                        Pawn temp = (Pawn) getPieceAtPosition(p);
                                        temp.kill();
                                    }
                                }
                            }

                        }
                    }
                }


            }


        }
    }
    public  boolean inMap(Position b){
        if(b.getX()<0||b.getX()>10||b.getY()>10||b.getY()<0||(b.getX()==0&&b.getY()==0)||(b.getX()==0&&b.getY()==10)||(b.getX()==10&&b.getY()==0)||(b.getX()==10&&b.getY()==10)){
            return false;
        }
        return true;
    }

    public void copyMap(){
        ConcretePiece copyB[][]=new ConcretePiece[11][11];
        for (int i = 0; i <11 ; i++) {
            for (int j = 0; j < 11; j++) {
                copyB[i][j]=Board[i][j];

            }

        }
        copy.add(copyB);
    }


















    @Override
    public Piece getPieceAtPosition(Position position) {
        return Board[position.getX()][position.getY()];
    }

    @Override
    public Player getFirstPlayer() {
        return attack;
    }

    @Override
    public Player getSecondPlayer() {
        return defeend;
    }

    @Override
    public boolean isGameFinished() {
        return isGameFinished;
    }

    @Override
    public boolean isSecondPlayerTurn() {
        return HowsTurn;
    }

    @Override
    public void reset() {



        copy.clear();
        kingPosition=new Position(5,5);
        for (int i = 0; i <11 ; i++) {
            for (int j = 0; j <11 ; j++) {
                positions[i][j]=new Position(i,j);
                Board[i][j]=null;
            }
        }


        if(!isGameFinished){
           attack.clear();
           defeend.clear();



        }
        else {
            isGameFinished=!isGameFinished;

        }


        isTheKingDead=false;


        Pawn d1=new Pawn(attack);

        Pawn d2=new Pawn(attack);
        Pawn d3=new Pawn(attack);
        Pawn d4=new Pawn(attack);
        Pawn d5=new Pawn(attack);
        Pawn d6=new Pawn(attack);
        Pawn d7=new Pawn(attack);
        Pawn d8=new Pawn(attack);
        Pawn d9=new Pawn(attack);
        Pawn d10=new Pawn(attack);
        Pawn d11=new Pawn(attack);
        Pawn d12=new Pawn(attack);
        Pawn d13=new Pawn(attack);
        Pawn d14=new Pawn(attack);
        Pawn d15=new Pawn(attack);
        Pawn d16=new Pawn(attack);
        Pawn d17=new Pawn(attack);
        Pawn d18=new Pawn(attack);
        Pawn d19=new Pawn(attack);
        Pawn d20=new Pawn(attack);
        Pawn d21=new Pawn(attack);
        Pawn d22=new Pawn(attack);
        Pawn d23=new Pawn(attack);
        Pawn d24=new Pawn(attack);


        Board[0][3]=d1;
        d1.setNumber(7);
        Board[0][4]=d2;
        d2.setNumber(9);
        Board[0][5]=d3;
        d3.setNumber(11);
        Board[0][6]=d4;
        d4.setNumber(15);
        Board[0][7]=d5;
        d5.setNumber(17);
        Board[1][5]=d6;
        d6.setNumber(12);

        Board[3][0]=d7;
        d7.setNumber(7);
        Board[4][0]=d8;
        d8.setNumber(2);
        Board[5][0]=d9;
        d9.setNumber(3);
        Board[6][0]=d10;
        d10.setNumber(4);
        Board[7][0]=d11;
        d11.setNumber(5);
        Board[5][1]=d12;
        d12.setNumber(6);

        Board[10][3]=d13;
        d13.setNumber(7);
        Board[10][4]=d14;
        d14.setNumber(10);
        Board[10][5]=d15;
        d15.setNumber(14);
        Board[10][6]=d16;
        d16.setNumber(16);
        Board[10][7]=d17;
        d17.setNumber(18);
        Board[9][5]=d18;
        d18.setNumber(13);

        Board[3][10]=d19;
        d19.setNumber(20);
        Board[4][10]=d20;
        d20.setNumber(21);
        Board[5][10]=d21;
        d21.setNumber(22);
        Board[6][10]=d22;
        d22.setNumber(23);
        Board[7][10]=d23;
        d23.setNumber(24);
        Board[5][9]=d24;
        d24.setNumber(19);


        Pawn a1=new Pawn(defeend);
        Pawn a2=new Pawn(defeend);
        Pawn a3=new Pawn(defeend);
        Pawn a4=new Pawn(defeend);
        Pawn a5=new Pawn(defeend);
        Pawn a6=new Pawn(defeend);
        Pawn a7=new Pawn(defeend);
        Pawn a8=new Pawn(defeend);
        Pawn a9=new Pawn(defeend);
        Pawn a10=new Pawn(defeend);
        Pawn a11=new Pawn(defeend);
        Pawn a12=new Pawn(defeend);
        Pawn a13=new Pawn(defeend);


        Board[5][3]=a1;
        a1.setNumber(1);

        Board[4][4]=a2;
        a2.setNumber(2);
        Board[5][4]=a3;
        a3.setNumber(3);
        Board[6][4]=a4;
        a4.setNumber(4);

        Board[3][5]=a5;
        a5.setNumber(5);
        Board[4][5]=a7;
        a7.setNumber(6);


        Board[5][5]=King;



        Board[6][5]=a8;
        a8.setNumber(8);
        Board[7][5]=a9;
        a9.setNumber(9);

        Board[4][6]=a10;
        a10.setNumber(10);
        Board[5][6]=a11;
        a11.setNumber(11);
        Board[6][6]=a12;
        a12.setNumber(12);

        Board[5][7]=a13;
        a13.setNumber(13);
        for (int i = 0; i <11 ; i++) {       // הוספנו את כל הכלים לתןך אוסף
            for (int j = 0; j <11 ; j++) {
                if(getPieceAtPosition(new Position(i,j))!=null){
                    if(getPieceAtPosition(new Position(i,j)).getOwner().isPlayerOne()){          // add al the pieces to their player collection and the to all collation
                        PiecesP1.add((ConcretePiece) getPieceAtPosition(new Position(i,j)));
                    }
                    else
                    {
                        PiecesP2.add((ConcretePiece) getPieceAtPosition(new Position(i,j)));
                    }


                    allPices.add((ConcretePiece) getPieceAtPosition(new Position(i,j)));
                    ConcretePiece temp=(ConcretePiece) Board[i][j]; // update all the pices starting positon
                    temp.addStep(new Position(i,j));
                           // update all the squars that they been steped
                    positions[i][j].addPeice(temp);

                }
            }
        }
        HowsTurn=true;







    }

    @Override
    public void undoLastMove() {
        if(copy.isEmpty()==false){
            Board=copy.pop();
        }
        HowsTurn=!HowsTurn;

    }

    @Override
    public int getBoardSize() {
        return Size;
    }


    public void PrintSteps() {
        Comparator comp = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                ConcretePiece temp1 = (ConcretePiece) o1;
                ConcretePiece temp2 = (ConcretePiece) o2;

                if (temp1.getNumberOfMoves() > temp2.getNumberOfMoves()) {
                    return 1;
                } else if (temp1.getNumberOfMoves() < temp2.getNumberOfMoves()) {
                    return -1;
                } else {
                    if (temp1.getOwner().isPlayerOne() != temp2.getOwner().isPlayerOne()) {
                        if (!isTheKingDead) {              // if king dead
                            if (temp1.getOwner().isPlayerOne()) {      // and its the D team retur -1
                                return 1;
                            } else {
                                return -1;
                            }
                        } else {
                            if (!temp1.getOwner().isPlayerOne()) {
                                return 1;
                            } else {
                                return -1;
                            }

                        }
                    }
                    else {


                        int temp1_int=temp1.Number;
                        int temp2_int=temp2.Number;


                        if(temp1_int>=temp2_int){
                            return 1;
                        }
                        else  {return  -1;}
                    }
                }

            }
        };



        allPices.sort(comp);
        PiecesP2.sort(comp);
        PiecesP1.sort(comp);

        comp.compare(King,PiecesP1.get(11));
        if(isTheKingDead){
            for (int i = 0; i < PiecesP2.size(); i++) {
                if (PiecesP2.get(i).getMoves().size() > 1) {
                    System.out.print(PiecesP2.get(i) + " [");

                    for (int j = 0; j <PiecesP2.get(i).getNumberOfMoves()-1 ; j++) {

                        System.out.print(PiecesP2.get(i).getMoves().get(j) + ", ");
                    }

                    System.out.print(PiecesP2.get(i).getMoves().get(PiecesP2.get(i).getNumberOfMoves()-1) + "]");
                    System.out.println("");
                }

            }
            for (int i = 0; i < PiecesP1.size(); i++) {
                if (PiecesP1.get(i).getMoves().size() > 1) {
                    System.out.print(PiecesP1.get(i) + " [");

                    for (int j = 0; j <PiecesP1.get(i).getNumberOfMoves()-1 ; j++) {

                        System.out.print(PiecesP1.get(i).getMoves().get(j) + ", ");
                    }

                    System.out.print(PiecesP1.get(i).getMoves().get(PiecesP1.get(i).getNumberOfMoves()-1) + "]");
                    System.out.println("");
                }

            }
        }else {
            for (int i = 0; i < PiecesP1.size(); i++) {
                if (PiecesP1.get(i).getMoves().size() > 1) {
                    System.out.print(PiecesP1.get(i) + " [");

                    for (int j = 0; j <PiecesP1.get(i).getNumberOfMoves()-1 ; j++) {

                        System.out.print(PiecesP1.get(i).getMoves().get(j) + ", ");
                    }

                    System.out.print(PiecesP1.get(i).getMoves().get(PiecesP1.get(i).getNumberOfMoves()-1) + "]");
                    System.out.println("");
                }

            }
            for (int i = 0; i < PiecesP2.size(); i++) {
                if (PiecesP2.get(i).getMoves().size() > 1) {
                    System.out.print(PiecesP2.get(i) + " [");

                    for (int j = 0; j <PiecesP2.get(i).getNumberOfMoves()-1 ; j++) {

                        System.out.print(PiecesP2.get(i).getMoves().get(j) + ", ");
                    }

                    System.out.print(PiecesP2.get(i).getMoves().get(PiecesP2.get(i).getNumberOfMoves()-1) + "]");
                    System.out.println("");
                }

            }

        }


        System.out.println("***************************************************************************");

        Comparator comp2 = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                if (o1 instanceof King) {
                    return -1;
                } else if (o2 instanceof King) {
                    return 1;
                } else {
                    Pawn temp1 = (Pawn) o1;
                    Pawn temp2 = (Pawn) o2;

                    if (temp1.getKills() > temp2.getKills()) {
                        return -1;
                    } else if (temp1.getKills() < temp2.getKills()) {
                        return 1;
                    } else {

                            int temp1_int=temp1.Number;
                            int temp2_int=temp2.Number;


                            if(temp1_int>temp2_int){
                                return 1;
                            }
                            else if(temp1_int<temp2_int)  {return  -1;}
                            else{

                                if (!isTheKingDead) {              // if king dead
                                    if (temp1.getOwner().isPlayerOne()) {      // and its the D team retur -1
                                        return 1;
                                    } else {
                                        return -1;
                                    }
                                } else {
                                    if (!temp1.getOwner().isPlayerOne()) {
                                        return 1;
                                    } else {
                                        return -1;
                                    }

                                }

                            }
                        }
                    }
                }
            };




        allPices.sort(comp2);
        for (int i = 0; i < allPices.size(); i++) {
            if((allPices.get(i) instanceof Pawn)){
                if ( ((Pawn) allPices.get(i)).getKills() > 0) {
                    System.out.println(allPices.get(i) + " "+((Pawn) allPices.get(i)).getKills()+" kills");

                }

            }
        }
        System.out.println("***************************************************************************");
        Comparator comp3=new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                ConcretePiece temp1 = (ConcretePiece) o1;
                ConcretePiece temp2 = (ConcretePiece) o2;

                if (temp1.distance > temp2.distance) {
                    return -1;
                } else if (temp1.distance < temp2.distance) {
                    return 1;
                }  else {

                    int temp1_int=temp1.Number;
                    int temp2_int=temp2.Number;


                    if(temp1_int>temp2_int){
                        return 1;
                    }
                    else if(temp1_int<temp2_int)  {return  -1;}
                    else{

                        if (!isTheKingDead) {              // if king dead
                            if (temp1.getOwner().isPlayerOne()) {      // and its the D team retur -1
                                return 1;
                            } else {
                                return -1;
                            }
                        } else {
                            if (!temp1.getOwner().isPlayerOne()) {
                                return 1;
                            } else {
                                return -1;
                            }

                        }

                    }
                }
                }


            };


        allPices.sort(comp3);

        for (int i = 0; i < allPices.size(); i++) {


            if (allPices.get(i).distance > 0) {
                System.out.println(allPices.get(i) + " " + allPices.get(i).distance + " squares");

            }
        }
        System.out.println("***************************************************************************");

        ArrayList<Position> tempPos=new ArrayList<Position>();
        for (int i = 0; i <11 ; i++) {
            for (int j = 0; j <11 ; j++) {
                tempPos.add(positions[i][j]);
            }

        }

        Comparator comp4=new Comparator() {
            @Override
            public int compare(Object o11, Object o22) {
                Position o1= (Position) o11;
                Position o2= (Position) o22;
                if(o1.getPiecesNumber()> o2.getPiecesNumber()){
                    return -1;
                }
                if(o1.getPiecesNumber()< o2.getPiecesNumber()){
                    return 1;
                }
                else {
                    if(o1.getX()!=o2.getX()){
                        if(o1.getX()>o2.getX()){return  1;}
                        else {return -1;}
                    }
                    else {
                        if(o1.getY()>o2.getY()){return  1;}
                        else {return -1;}
                    }
                }
            }
        };
        tempPos.sort(comp4);

        for (int i = 0; i < tempPos.size(); i++) {
            if(tempPos.get(i).getPiecesNumber()>1){
                System.out.println(tempPos.get(i)+""+tempPos.get(i).printNumPieces());
            }
        }
        System.out.println("***************************************************************************");


    }




}
