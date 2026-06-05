package com.yut.controller;

import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.yut.ui.swing.MainFrame;
import com.yut.ui.swing.StartScreen;
import com.yut.controller.model_interfaces.GameModelInterface;
import com.yut.model.Game;

public class TitleController{

    private final MainFrame mainFrame;
    private StartScreen startScreen;
    private GameModelInterface gameModel;
    private GameController gameController;

    public TitleController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void start(){
        SwingUtilities.invokeLater(() -> {

            mainFrame.setVisible(true);
            mainFrame.showStart();

            startScreen = mainFrame.getStart();

            startScreen.addStartButtonListener(new StartButtonListener());
        });
    }

    class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int boardType = startScreen.getBoardType();
            int playerCount = startScreen.getPlayers();
            int pieceCount = startScreen.getPieces();

            gameModel = new Game(boardType, playerCount, pieceCount);

            mainFrame.showGame(boardType, playerCount, pieceCount);
            gameController = new GameController(gameModel, mainFrame.getGameScreen());
        }
    }
}
