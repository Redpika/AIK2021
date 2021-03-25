import java.util.ArrayList;
import java.util.List;

public class AIGroup3 extends AI {
    public double[][] map;

    public AIGroup3(int id, int team, GameModel gameModel){
        super(id, team, gameModel);
        uselessCellIdx = new ArrayList<>();
        map = new double[10][10];
        resetMap();
    }

    //used to clear the map to the beginning of the game
    public void resetMap (){
        for (int x = 0; x<10; x++) {
            for (int y = 0; y < 10; y++) {
                if (x==0 || y == 0 || x == y) {
                    map[x][y] = 10;
                }
                else map[x][y] = 1;
            }
        }
        map[0][0] = map[0][9] = map[9][0] = map[9][9] = 0;
    }

    @Override
    public int discardCard() {
        //always discard dead cards
        List<Card> deadCards = getDeadCards(getGameModel().getSequenceBoard());
        if(deadCards == null)
            return 0;

        int ttlDiscardedCards = deadCards.size();
        for(Card eachCard : deadCards){
            getGameModel().setSelectedCard(eachCard);
            getGameModel().discardSelectedCard();
        }
        return ttlDiscardedCards;
    }

    @Override
    public Card evaluateHand() {
        double maxValue;
        int maxIdx;
        Card selectedCard = null;
        setTwinCardEnabled(false);
        int bestIdxCellOneDim = 0;

        //check to remove dead card (might be first in priority to get new card)

        for (int n = 0; n < 6; n++) {
            List<Integer> idx = getGameModel().getSequenceBoard().getAvailableCellsIdx(getCards().get(n));
            //basic outline: check all the cards. check the value and get max. if same pick first card with the value.
            //problem: need to check the how the idx works
        }

        //always play twin cards
        List<Card> twinCards = getTwinCards();
        if(twinCards != null){
            //basic outline: check the total result from the twin card
            //problem: same as the normal
            if(getGameModel().getSequenceBoard().getAvailableCellsIdx(twinCards.get(0)).size() == 2 ) {
                selectedCard = twinCards.get(0);
                setTwinCardEnabled(true);
                return selectedCard;
            }
        }
        //always select the first card
        /*selectedCard = getCards().get(0);
        List<Integer> idx = getGameModel().getSequenceBoard().getAvailableCellsIdx(selectedCard);
        if(idx.size() > 0)
            setSelectedIdxOneDim(idx.get(0));*/


        return selectedCard;
    }

    public int[] findHeuristic(int x, int y) {
        //prepare the variable
        int[] maxMarkDir = new int[4];
        int h, temp;
        boolean isSequence = false;
        Cell[][] cells = getGameModel().getSequenceBoard().getCells();

        //check column (LINE_EAST)
        for(int incCol=-4; incCol<=0; incCol++){
            isSequence = false;
            h = temp = 0;
            int idxCol = x+incCol;

            //if out of bound (leftmost column), then next iteration
            if(idxCol < 0)
                continue;
            //if out of bound (rightmost column), then break (do not need to check next column)
            if(idxCol + 4 >= SequenceBoard.BOARD_WIDTH)
                break;

            //check the next 5 cells to the east
            for(int n=0; n<=4; n++){
                //if there is a wild cell
                if(cells[idxCol + n][y].isWildCell())
                    temp = this.getTeam();

                //if there is a sequence AND this is not the first encounter (more than 1 cell are already a sequence)
                if(cells[idxCol + n][y].isSequence()){
                    if(isSequence)
                        break;
                    isSequence = true;
                }

                //sum the mark (value) of each cell
                if(cells[idxCol + n][y].getMark() == this.getTeam())
                    h += cells[idxCol + n][y].getMark();
                if(idxCol + n == x)
                    h += this.getTeam();
            }
            h += temp;

            //after this code

            //check the total sum (h)
            if(h == 5*this.getTeam()){
                //create a sequence
            }
        }
        return maxMarkDir;
    }

    public double calculateHeuristic(int x, int y) {
        //x1 ~ x6: variables
        //calc: counting for calc
        int x1 = 0, x2 = 0, x3 = 0, x4 = 1, x5 = 0, x6 = 1;
        int horCalcX1 = 0, verCalcX1 = 0, diagTopLeftCalcX1 = 0, diagBottomLeftCalcX1 = 0, horCalcX2 = 0, verCalcX2 = 0, diagTopLeftCalcX2 = 0, diagBottomLeftCalcX2 = 0;
        int horStatX1 = 0, verStatX1 = 0, diagTopLeftStatX1 = 0, diagBottomLeftStatX1 = 0, horStatX2 = 0, verStatX2 = 0, diagTopLeftStatX2 = 0, diagBottomLeftStatX2 = 0;
        Cell[][] cells = getGameModel().getSequenceBoard().getCells();
        for (int n = -4; n<=4; n++) {
            if (x+n>=0&&x+n<=9) {
                if (cells[x+n][y].isWildCell()) {
                    x1++;
                    x2++;
                    horCalcX1++;
                    horCalcX2++;
                }
                else if (cells[x+n][y].getMark() == Cell.MARK_EMPTY) {
                    if (horCalcX1 != 0 && horCalcX1 < 4) {
                        if (horStatX1 == 0) {
                            horStatX1 = 1;
                        }
                        else {
                            horCalcX1 = 0;
                            horStatX1 = 0;
                        }
                    }
                    else if (horCalcX2 != 0 && horCalcX2 < 4) {
                        if (horStatX2 == 0) {
                            horStatX2 = 1;
                        }
                        else {
                            horCalcX2 = 0;
                            horStatX2 = 0;
                        }
                    }
                }
                else if (cells[x+n][y].getMark() == this.getTeam()) {
                    x1++;
                    horCalcX1++;
                    horCalcX2 = 0;
                }
                else if (cells[x+n][y].getMark() != this.getTeam()) {
                    x2++;
                    horCalcX1 = 0;
                    horCalcX2++;
                }
            }
            if (y+n>=0&&y+n<=9) {
                if (cells[x][y+n].isWildCell()) {
                    x1++;
                    x2++;
                    verCalcX1++;
                    verCalcX2++;
                }
                else if (cells[x][y+n].getMark() == Cell.MARK_EMPTY) {
                    if (verCalcX1 != 0 && verCalcX1 < 4) {
                        if (verStatX1 == 0) {
                            verStatX1 = 1;
                        }
                        else {
                            verCalcX1 = 0;
                            verStatX1 = 0;
                        }
                    }
                    else if (verCalcX2 != 0 && verCalcX2 < 4) {
                        if (verStatX2 == 0) {
                            verStatX2 = 1;
                        }
                        else {
                            verCalcX2 = 0;
                            verStatX2 = 0;
                        }
                    }
                }
                else if (cells[x][y+n].getMark() == this.getTeam()) {
                    x1++;
                    verCalcX1++;
                    verCalcX2 = 0;
                }
                else if (cells[x][y+n].getMark() != this.getTeam()) {
                    x2++;
                    verCalcX1 = 0;
                    verCalcX2++;
                }
            }
            if (x+n>=0&&x+n<=9&&y+n>=0&&y+n<=9) {
                if (cells[x+n][y+n].isWildCell()) {
                    x1++;
                    x2++;
                    diagBottomLeftCalcX1++;
                    diagBottomLeftCalcX2++;
                }
                else if (cells[x+n][y+n].getMark() == Cell.MARK_EMPTY) {
                    if (diagBottomLeftCalcX1 != 0 && diagBottomLeftCalcX1 < 4) {
                        if (diagBottomLeftStatX1 == 0) {
                            diagBottomLeftStatX1 = 1;
                        }
                        else {
                            diagBottomLeftCalcX1 = 0;
                            diagBottomLeftStatX1 = 0;
                        }
                    }
                    else if (diagBottomLeftCalcX2 != 0 && diagBottomLeftCalcX2 < 4) {
                        if (diagBottomLeftStatX2 == 0) {
                            diagBottomLeftStatX2 = 1;
                        }
                        else {
                            diagBottomLeftCalcX2 = 0;
                            diagBottomLeftStatX2 = 0;
                        }
                    }
                }
                else if (cells[x+n][y+n].getMark() == this.getTeam()) {
                    x1++;
                    diagBottomLeftCalcX1++;
                    diagBottomLeftCalcX2 = 0;
                }
                else if (cells[x+n][y+n].getMark() != this.getTeam()) {
                    x2++;
                    diagBottomLeftCalcX1 = 0;
                    diagBottomLeftCalcX2++;
                }
            }
            if (x+n>=0&&x+n<=9&&y-n>=0&&y-n<=9) {
                if (cells[x+n][y-n].isWildCell()) {
                    x1++;
                    x2++;
                    diagTopLeftCalcX1++;
                    diagTopLeftCalcX2++;
                }
                else if (cells[x+n][y-n].getMark() == Cell.MARK_EMPTY) {
                    if (diagTopLeftCalcX1 != 0 && diagTopLeftCalcX1 < 4) {
                        if (diagTopLeftStatX1 == 0) {
                            diagTopLeftStatX1 = 1;
                        }
                        else {
                            diagTopLeftCalcX1 = 0;
                            diagTopLeftStatX1 = 0;
                        }
                    }
                    else if (diagTopLeftCalcX2 != 0 && diagTopLeftCalcX2 < 4) {
                        if (diagTopLeftStatX2 == 0) {
                            diagTopLeftStatX2 = 1;
                        }
                        else {
                            diagTopLeftCalcX2 = 0;
                            diagTopLeftStatX2 = 0;
                        }
                    }
                }
                else if (cells[x+n][y-n].getMark() == this.getTeam()) {
                    x1++;
                    diagTopLeftCalcX1++;
                    diagTopLeftCalcX2 = 0;
                }
                else if (cells[x+n][y-n].getMark() != this.getTeam()) {
                    x2++;
                    diagTopLeftCalcX1 = 0;
                    diagTopLeftCalcX2++;
                }
            }
        }
        if (horCalcX1 >= 4 || verCalcX1 >= 4 || diagBottomLeftCalcX1 >= 4 || diagTopLeftCalcX1 >= 4) {
            x3 = 1;
        }

        if (horCalcX2 >= 4 || verCalcX2 >= 4 || diagBottomLeftCalcX2 >= 4 || diagTopLeftCalcX2 >= 4) {
            x5 = 1;
        }
        //todo: find how to check complete sequence
        return (x1*10+x2*10+(x3*1500)*x4+(x5*1000)*x6);
    }
}
