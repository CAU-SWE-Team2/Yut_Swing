package com.yut.model;
import com.yut.controller.model_interfaces.GameModelInterface;

public class Game implements GameModelInterface {

    private Players players;
    private Board board;
    private int boardType;

    private GameTurn gameTurn;
    private int currentPlayerId;
    private int playerCount;
    private int numOfTotalPieces;


    public Game(int boardType, int playerCount, int numOfTotalPieces) {
        this.boardType = boardType;
        this.playerCount = playerCount;
        this.numOfTotalPieces = numOfTotalPieces;
        players = new Players(playerCount, numOfTotalPieces);
        
        if(this.boardType == 4)
            board = new SquareBoard();
        else if(this.boardType == 5)
            board = new PentagonBoard();
        else
            board = new HexagonBoard();
        
        currentPlayerId = players.getFirstPlayer().id;
        gameTurn = new GameTurn(players.getPlayer(1));

        for(int i = 1; i <= players.size(); i++){
            Player player = players.getPlayer(i);
            for(int j = 1; j <= numOfTotalPieces; j++ ){
                Group group = new Group(j, player);
                group.addToGame(board);
                  
            }
        
        }
    }

    public void switchTurn(){
        currentPlayerId++;
        currentPlayerId = (currentPlayerId - 1) % playerCount + 1;
        Player player = players.getPlayer(currentPlayerId);

        gameTurn = new GameTurn(player);
    }

    public boolean ifGameEnded(){
        for (int i = 1; i <= players.size(); i++){
            Player player = players.getPlayer(i);
            if(player.getNumOfCurrentPieces() == 0)
                return true;
        }
        return false;
    }

    public GameTurn getGameTurn(){
        return gameTurn;
    }

    public Board getBoard(){
        return board;
    }

    public Player getCurrentPlayer(){
        return players.getPlayer(currentPlayerId);
    }

    public int getPlayerCount(){
        return playerCount;
    }
    public int getNumOfTotalPieces(){
        return numOfTotalPieces;
    }

    public int getBoardType(){
        return boardType;
    }
}

