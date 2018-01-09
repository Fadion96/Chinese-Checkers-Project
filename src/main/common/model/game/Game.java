package common.model.game;

import javafx.util.Pair;

/**
 * Class responsible for creating and playing the game. It contains game board
 */
public class Game {
    /**
     * Game board representation.
     */
    private Marble[][] board;

    /**
     * Determines whose player should move.
     */
    private Color turn = Color.NONE;

    /**
     * Number of players.
     */
    private int nrPlayers;

    /**
     * Size of the board.
     */
    private int size;

    /**
     * Widths of each row.
     */
    private final static int[] WIDTHS =
            {1, 2, 3, 4, 13, 12, 11, 10, 9, 10, 11, 12, 13, 4, 3, 2, 1};

    /**
     * Starting column in each row.
     */
    private final static int[] OFFSETS =
            {4, 4, 4, 4, 0, 1, 2, 3, 4, 4, 4, 4, 4, 9, 10, 11, 12};

    /**
     * Extreme points of the board.
     */
    private final static int[][] EXTREMEPOINTS =
            {{0, 4}, {4, 0}, {4, 12}, {12, 4}, {12, 16}, {16, 12}};

    /**
     * @param numberPlayers Number of players in game
     * @param boardSize Size of board
     */
    public Game(final int numberPlayers, final int boardSize) {
        this.board = new Marble[boardSize][boardSize];
        this.nrPlayers = numberPlayers;
        this.size = boardSize;

        for (int i = 0; i < boardSize; i++) {
            for (int j = i; j < boardSize; j++) {
                if (j < OFFSETS[i] || j >= OFFSETS[i] + WIDTHS[i]) {
                    board[i][j] = null;
                    board[j][i] = null;
                } else {
                    board[i][j] = new Marble(i, j, Color.NONE, Color.NONE);
                    board[j][i] = new Marble(j, i, Color.NONE, Color.NONE);
                }
            }
        }
        setNeighbours();
        setJumps();
        setTerritories();
        setMarbles();
    }

    /**
     * Method for setting neighbours of each marble.
     */
    private void setNeighbours() {
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                if (j == i) {
                    if (board[i][j] != null) {

                        if (i - 1 >= 0) {
                            if (j - 1 >= 0 && board[i - 1][j - 1] != null) {
                                board[i][j].addNeighbour(
                                        i - 1,
                                        j - 1,
                                        Direction.NORTH_WEST
                                );
                            }
                            if (board[i - 1][j] != null) {
                                board[i][j].addNeighbour(
                                        i - 1,
                                        j,
                                        Direction.NORTH
                                );
                            }
                        }
                        if (j - 1 >= 0 && board[i][j - 1] != null) {
                            board[i][j].addNeighbour(
                                    i,
                                    j - 1,
                                    Direction.WEST
                            );
                        }
                        if (i + 1 < size) {
                            if (j + 1 < size && board[i + 1][j + 1] != null) {
                                board[i][j].addNeighbour(
                                        i + 1,
                                        j + 1,
                                        Direction.SOUTH_EAST
                                );
                            }
                            if (board[i + 1][j] != null) {
                                board[i][j].addNeighbour(
                                        i + 1,
                                        j,
                                        Direction.SOUTH
                                );
                            }
                        }
                        if (j + 1 < size && board[i][j + 1] != null) {
                            board[i][j].addNeighbour(
                                    i,
                                    j + 1,
                                    Direction.EAST
                            );
                        }
                    }
                } else {
                    if (board[i][j] != null) {

                        if (i - 1 >= 0) {
                            if (j - 1 >= 0 && board[i - 1][j - 1] != null) {
                                board[i][j].addNeighbour(
                                        i - 1,
                                        j - 1,
                                        Direction.NORTH_WEST
                                );
                                board[j][i].addNeighbour(
                                        j - 1,
                                        i - 1,
                                        Direction.NORTH_WEST
                                );
                            }
                            if (board[i - 1][j] != null) {
                                board[i][j].addNeighbour(
                                        i - 1,
                                        j,
                                        Direction.NORTH
                                );
                                board[j][i].addNeighbour(
                                        j,
                                        i - 1,
                                        Direction.WEST
                                );
                            }
                        }
                        if (j - 1 >= 0 && board[i][j - 1] != null) {
                            board[i][j].addNeighbour(
                                    i,
                                    j - 1,
                                    Direction.WEST
                            );
                            board[j][i].addNeighbour(
                                    j - 1,
                                    i,
                                    Direction.NORTH
                            );
                        }
                        if (i + 1 < size) {
                            if (j + 1 < size && board[i + 1][j + 1] != null) {
                                board[i][j].addNeighbour(
                                        i + 1,
                                        j + 1,
                                        Direction.SOUTH_EAST
                                );
                                board[j][i].addNeighbour(
                                        j + 1,
                                        i + 1,
                                        Direction.SOUTH_EAST
                                );
                            }
                            if (board[i + 1][j] != null) {
                                board[i][j].addNeighbour(
                                        i + 1,
                                        j,
                                        Direction.SOUTH
                                );
                                board[j][i].addNeighbour(
                                        j,
                                        i + 1,
                                        Direction.EAST
                                );
                            }
                        }
                        if (j + 1 < size && board[i][j + 1] != null) {
                            board[i][j].addNeighbour(i, j + 1, Direction.EAST);
                            board[j][i].addNeighbour(j + 1, i, Direction.SOUTH);
                        }
                    }
                }
            }
        }
    }

    /**
     * Method for setting all possible jump moves of each marble.
     */
    private void setJumps() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != null) {
                    for (Pair<Pair<Integer, Integer>, Direction> neighbour
                            : board[i][j].getNeighbours()) {
                        Pair<Integer, Integer> coords = neighbour.getKey();
                        switch (neighbour.getValue()) {
                            case NORTH_WEST:
                                if (coords.getKey() > 0
                                        && coords.getValue() > 0) {
                                    if (board[coords.getKey() - 1]
                                            [coords.getValue() - 1] != null) {
                                        board[i][j].addJump(
                                                coords.getKey() - 1,
                                                coords.getValue() - 1,
                                                neighbour.getValue()
                                        );
                                    }
                                }
                                break;
                            case NORTH:
                                if (coords.getKey() > 0) {
                                    if (board[coords.getKey() - 1]
                                            [coords.getValue()] != null) {
                                        board[i][j].addJump(
                                                coords.getKey() - 1,
                                                coords.getValue(),
                                                neighbour.getValue()
                                        );
                                    }
                                }
                                break;
                            case WEST:
                                if (coords.getValue() > 0) {
                                    if (board[coords.getKey()]
                                            [coords.getValue() - 1] != null) {
                                        board[i][j].addJump(
                                                coords.getKey(),
                                                coords.getValue() - 1,
                                                neighbour.getValue()
                                        );
                                    }
                                }
                                break;
                            case SOUTH_EAST:
                                if (coords.getKey() < size - 1
                                        && coords.getValue() < size - 1) {
                                    if (board[coords.getKey() + 1]
                                            [coords.getValue() + 1] != null) {
                                        board[i][j].addJump(
                                                coords.getKey() + 1,
                                                coords.getValue() + 1,
                                                neighbour.getValue()
                                        );
                                    }
                                }
                                break;
                            case EAST:
                                if (coords.getValue() < size - 1) {
                                    if (board[coords.getKey()]
                                            [coords.getValue() + 1] != null) {
                                        board[i][j].addJump(
                                                coords.getKey(),
                                                coords.getValue() + 1,
                                                neighbour.getValue()
                                        );
                                    }
                                }
                                break;
                            case SOUTH:
                                if (coords.getKey() < size - 1) {
                                    if (board[coords.getKey() + 1]
                                            [coords.getValue()] != null) {
                                        board[i][j].addJump(
                                                coords.getKey() + 1,
                                                coords.getValue(),
                                                neighbour.getValue()
                                        );
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Method for setting territories on the board.
     */
    private void setTerritories() {
        for (int i = 0; i < 4; i++) {
            for (int j = 4; j < i + 5; j++) {
                board[i][j].setTerritory(Color.AZURE);
            }
        }
        for (int i = 4; i < 8; i++) {
            for (int j = i - 4; j < 4; j++) {
                board[i][j].setTerritory(Color.GREEN);
                board[i][j + 9].setTerritory(Color.RED);
            }
        }
        for (int i = 9; i < 13; i++) {
            for (int j = 4; j < i - 4; j++) {
                board[i][j].setTerritory(Color.PINK);
                board[i][j + 9].setTerritory(Color.YELLOW);
            }
        }
        for (int i = 13; i < size; i++) {
            for (int j = i - 4; j < 13; j++) {
                board[i][j].setTerritory(Color.BLUE);
            }
        }
    }

    /**
     * Method for setting playable marbles on the board.
     */
    private void setMarbles() {
        switch (this.nrPlayers) {
            case 2:
                for (int i = 0; i < 4; i++) {
                    for (int j = 4; j < i + 5; j++) {
                        board[i][j].setColor(Color.AZURE);
                    }
                }
                for (int i = 13; i < size; i++) {
                    for (int j = i - 4; j < 13; j++) {
                        board[i][j].setColor(Color.BLUE);
                    }
                }
                break;
            case 3:
                for (int i = 0; i < 4; i++) {
                    for (int j = 4; j < i + 5; j++) {
                        board[i][j].setColor(Color.AZURE);
                    }
                }
                for (int i = 9; i < 13; i++) {
                    for (int j = 4; j < i - 4; j++) {
                        board[i][j].setColor(Color.PINK);
                        board[i][j + 9].setColor(Color.YELLOW);
                    }
                }
                break;
            case 4:
                for (int i = 4; i < 8; i++) {
                    for (int j = i - 4; j < 4; j++) {
                        board[i][j].setColor(Color.GREEN);
                        board[i][j + 9].setColor(Color.RED);
                    }
                }
                for (int i = 9; i < 13; i++) {
                    for (int j = 4; j < i - 4; j++) {
                        board[i][j].setColor(Color.PINK);
                        board[i][j + 9].setColor(Color.YELLOW);
                    }
                }
                break;
            case 6:
                for (int i = 0; i < 4; i++) {
                    for (int j = 4; j < i + 5; j++) {
                        board[i][j].setColor(Color.AZURE);
                    }
                }
                for (int i = 4; i < 8; i++) {
                    for (int j = i - 4; j < 4; j++) {
                        board[i][j].setColor(Color.GREEN);
                        board[i][j + 9].setColor(Color.RED);
                    }
                }
                for (int i = 9; i < 13; i++) {
                    for (int j = 4; j < i - 4; j++) {
                        board[i][j].setColor(Color.PINK);
                        board[i][j + 9].setColor(Color.YELLOW);
                    }
                }
                for (int i = 13; i < size; i++) {
                    for (int j = i - 4; j < 13; j++) {
                        board[i][j].setColor(Color.BLUE);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Method for checking if move is legal.
     * @param prevX Previous x coordinate of marble
     * @param prevY Previous y coordinate of marble
     * @param nextX Next x coordinate of marble
     * @param nextY Next y coordinate of marble
     * @param color Color of moved marble
     * @return Boolean if this move is legal
     */
    public boolean canMove(
            final int prevX,
            final int prevY,
            final int nextX,
            final int nextY,
            final Color color
    ) {
        if (validateCords(nextX, nextY)) {
            if (colorMatches(prevX, prevY, color)) {
                if (isInTerritory(prevX, prevY, color)) {
                    if (isInTerritory(nextX, nextY, color)) {
                        if (canMakeMove(prevX, prevY, nextX, nextY)) {
                            return true;
                        }
                    }
                } else {
                    if (canMakeMove(prevX, prevY, nextX, nextY)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    /**
     * Method for checking if jump is legal.
     * @param prevX Previous x coordinate of marble
     * @param prevY Previous y coordinate of marble
     * @param nextX Next x coordinate of marble
     * @param nextY Next y coordinate of marble
     * @param color Color of moved marble
     * @return Boolean if this jump is legal
     */
    public boolean canJump(
            final int prevX,
            final int prevY,
            final int nextX,
            final int nextY,
            final Color color
    ) {
        if (validateCords(nextX, nextY)) {
            if (colorMatches(prevX, prevY, color)) {
                if (isInTerritory(prevX, prevY, color)) {
                    if (isInTerritory(nextX, nextY, color)) {
                        if (canMakeJump(prevX, prevY, nextX, nextY)) {
                            return true;
                        }
                    }
                } else {
                    if (canMakeJump(prevX, prevY, nextX, nextY)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    /**
     * Method that checks if coords are on board.
     * @param x x coordinate of marble
     * @param y y coordinate of marble
     * @return boolean if cords are validate
     */
    private boolean validateCords(final int x, final int y) {
        return board[x][y] != null;
    }

    /**
     * Method that checks if color of player is the same as color of marble.
     * @param x x coordinate of marble
     * @param y y coordinate of marble
     * @param color color of moving player
     * @return boolean if colors are the same
     */
    private boolean colorMatches(final int x, final int y, final Color color) {
        return board[x][y].getColor() == color;
    }

    /**
     * Method for checking if marble is in opposite territory.
     * @param x x coordinate of marble
     * @param y y coordinate of marble
     * @param color color of marble
     * @return boolean if marble is in opposite territory
     */
    private boolean isInTerritory(final int x, final int y, final Color color) {
        return board[x][y].getTerritory() == color.getOpposite();
    }

    /**
     * Method for checking if next coordinates are in neighbourhood of previous.
     * @param prevX Previous x coordinate of marble
     * @param prevY Previous y coordinate of marble
     * @param nextX Next x coordinate of marble
     * @param nextY Next y coordinate of marble
     * @return Boolean
     */
    private boolean canMakeMove(
            final int prevX,
            final int prevY,
            final int nextX,
            final int nextY
    ) {
        if (board[nextX][nextY].getColor() == Color.NONE) {
            Pair<Integer, Integer> coords = new Pair<>(nextX, nextY);
            for (Pair<Pair<Integer, Integer>, Direction> neighbour
                    : board[prevX][prevY].getNeighbours()) {
                if (coords.equals(neighbour.getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method that checks if next coords are in jumps possibilities of previous.
     * @param prevX Previous x coordinate of marble
     * @param prevY Previous y coordinate of marble
     * @param nextX Next x coordinate of marble
     * @param nextY Next y coordinate of marble
     * @return Boolean
     */
    private boolean canMakeJump(
            final int prevX,
            final int prevY,
            final int nextX,
            final int nextY
    ) {
        if (board[nextX][nextY].getColor() == Color.NONE) {
            Pair<Integer, Integer> coords = new Pair<>(nextX, nextY);
            for (Pair<Pair<Integer, Integer>, Direction> jump
                    : board[prevX][prevY].getJumps()) {
                if (coords.equals(jump.getKey())) {
                   switch (jump.getValue()) {
                       case NORTH_WEST:
                           return board[nextX + 1][nextY + 1].getColor() != Color.NONE;
                       case NORTH:
                           return board[nextX + 1][nextY].getColor() != Color.NONE;
                       case WEST:
                           return board[nextX][nextY + 1].getColor() != Color.NONE;
                       case SOUTH_EAST:
                           return board[nextX - 1][nextY - 1].getColor() != Color.NONE;
                       case SOUTH:
                           return board[nextX - 1][nextY].getColor() != Color.NONE;
                       case EAST:
                           return board[nextX - 1][nextY + 1].getColor() != Color.NONE;
                       default:
                           break;
                   }
                }
            }
        }
        return false;
    }

    /**
     * Function that handles moving marbles.
     * @param prevX X position of marble which we want to move.
     * @param prevY Y position of marble which we want to move.
     * @param nextX X position where we want to move marble.
     * @param nextY Y position where we want to move marble.
     * @param color color of player what makes move.
     */
    public void makeMove(
            final int prevX,
            final int prevY,
            final int nextX,
            final int nextY,
            final Color color
    ) {
        board[prevX][prevY].setColor(Color.NONE);
        board[nextX][nextY].setColor(color);
    }



    /**
     * Getter for board.
     * @return Board
     */
    public Marble[][] getBoard() {
        return board;
    }


    /**
     * Deletes marbles of given color from the board.
     * @param color color that we want to remove from board
     */
    public void deleteMarbles(final Color color) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].getColor() == color) {
                        board[i][j].setColor(Color.NONE);
                    }
                }
            }
        }

    }

    /**
     * Method to determine if given color is winner of the game.
     * @param color color that we want check for win
     * @return boolean
     */
    public boolean isWinner(final Color color) {
        return true;
    }

    /**
     * Getter of turn.
     * @return current turn
     */
    public Color getTurn() {
        return turn;
    }

    /**
     * Setter of turn.
     * @param currentTurn current turn of the game
    */
    public void setTurn(final Color currentTurn) {
        this.turn = currentTurn;
    }

    /**
     * Getter of extreme point for given color.
     * @param color given color we want to check extreme point
     * @return marble with extreme point
     */
    public Marble getExtremePoint(final Color color) {
        Marble marble = null;
        for (int[] e : EXTREMEPOINTS) {
            if (board[e[0]][e[1]].getColor() == color) {
                marble = board[e[0]][e[1]];
            }
        }
        return marble;
    }
}
