package com.yut.controller;

import com.yut.controller.model_interfaces.GameModelInterface;
import com.yut.controller.model_interfaces.GameTurnModelInterface;
import com.yut.controller.view_interfaces.GameScreenInterface;
import com.yut.model.Node;
import com.yut.model.Group;
import com.yut.model.Player;
import com.yut.model.YutResult;
import com.yut.ui.swing.EndingFrame;
import com.yut.ui.swing.GameScreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

public class GameController {

    private final GameModelInterface gameModel;
    private final GameScreen gameScreen;

    public GameController(GameModelInterface gameModel, GameScreen gameScreen) {
        this.gameModel = gameModel;
        this.gameScreen = gameScreen;

        registerEventCallback();

        gameScreen.getNodeMap().forEach((nodeId, node) ->
            gameScreen.addNodeClickListener(node, new NodeClickListener(nodeId))
        );

        gameScreen.addRandomThrowButtonListener(new RandomThrowButtonListener());

        for (int i = 0; i < YutResult.values().length; i++)
            gameScreen.addSelectedThrowButtonListener(i, new SelectThrowButtonListener(i));

        gameScreen.addMoveNewPieceButtonListener(new MoveNewPieceButtonListener());

        for (int i = 1; i <= gameModel.getPlayerCount(); i++)
            gameScreen.updatePlayerCanvas(i, gameModel.getNumOfTotalPieces());

        gameScreen.highlightCurrentPlayer(gameModel.getCurrentPlayer().getId());
        gameScreen.addGoalButtonListener(new GoalButtonListener());
    }

    private void registerEventCallback() {
        gameModel.getGameTurn().setEventCallback(event -> {
            String message = switch (event) {
                case EXTRA_TURN_GRANTED -> "한 번 더 던지세요!";
                case BACKDO_SKIPPED     -> "뒤로 갈 수 있는 말이 없습니다!";
            };
            JOptionPane.showMessageDialog(gameScreen, message);
        });
    }

    class NodeClickListener extends MouseAdapter {
        private final int nodeId;

        NodeClickListener(int nodeId) {
            this.nodeId = nodeId;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            GameTurnModelInterface gameTurnModel = gameModel.getGameTurn();

            if (gameTurnModel.getState() == GameTurnModelInterface.THROWABLE)
                return;

            Node node = gameModel.getBoard().getNodeById(nodeId);
            Player currentPlayer = gameModel.getCurrentPlayer();

            gameScreen.setGoalButtonVisible(false);

            if (gameScreen.getNodeState(nodeId) == 1) {
                Group targetGroup = currentPlayer.getMoveTarget();

                if (targetGroup.getCurrentLocation().getId() != 111)
                    gameScreen.deletePiece(targetGroup.getCurrentLocation().getId());

                gameScreen.deletePiece(nodeId);
                gameTurnModel.move(targetGroup);
                gameScreen.printDeckContents(gameTurnModel.getLeftYuts());
                gameScreen.updatePlayerCanvas(currentPlayer.getId(), currentPlayer.getNumOfCurrentPieces());
                gameScreen.drawPiece(nodeId, currentPlayer.getId(), node.getCurrentGroup().getNumOfPieces());
                gameScreen.deleteMovePreview();

                if (gameTurnModel.getLeftYuts().isEmpty() && gameTurnModel.getRollCount() == 0)
                    gameModel.switchTurn();

                gameScreen.highlightCurrentPlayer(gameModel.getCurrentPlayer().getId());

            } else if (gameScreen.getNodeState(nodeId) == 3) {
                if (node.getCurrentGroup().getOwner().getId() == currentPlayer.getId()) {
                    currentPlayer.chooseTarget(node.getCurrentGroup());
                    gameScreen.deleteMovePreview();
                    Node toNode = gameTurnModel.showNextMove(node.getCurrentGroup());

                    if (toNode.getId() != 0)
                        gameScreen.showMovePreview(toNode.getId(), currentPlayer.getId());
                    else
                        gameScreen.setGoalButtonVisible(true);
                }
            }
        }
    }

    class MoveNewPieceButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GameTurnModelInterface gameTurnModel = gameModel.getGameTurn();
            if (gameTurnModel.getState() == GameTurnModelInterface.THROWABLE)
                return;

            Player currentPlayer = gameModel.getCurrentPlayer();
            Group waitingGroup = currentPlayer.getNewGroup();

            if (waitingGroup != null) {
                gameScreen.deleteMovePreview();
                currentPlayer.chooseTarget(waitingGroup);
                Node toNode = gameTurnModel.showNextMove(waitingGroup);
                gameScreen.showMovePreview(toNode.getId(), currentPlayer.getId());
            }
        }
    }

    class RandomThrowButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GameTurnModelInterface gameTurnModel = gameModel.getGameTurn();
            if (gameTurnModel.getState() == GameTurnModelInterface.HASTOMOVE)
                return;

            gameTurnModel.roll(-2);

            if (!gameTurnModel.getLeftYuts().isEmpty())
                gameScreen.updateRandomResult(gameTurnModel.getLeftYuts().getLast());

            gameScreen.printDeckContents(gameTurnModel.getLeftYuts());
        }
    }

    class SelectThrowButtonListener implements ActionListener {
        private final int type;

        SelectThrowButtonListener(int type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GameTurnModelInterface gameTurnModel = gameModel.getGameTurn();
            if (gameTurnModel.getState() == GameTurnModelInterface.HASTOMOVE)
                return;

            gameTurnModel.roll(type);

            if (!gameTurnModel.getLeftYuts().isEmpty())
                gameScreen.updateRandomResult(gameTurnModel.getLeftYuts().getLast());

            gameScreen.printDeckContents(gameTurnModel.getLeftYuts());
        }
    }

    class GoalButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GameTurnModelInterface gameTurnModel = gameModel.getGameTurn();
            if (gameTurnModel.getState() == GameTurnModelInterface.THROWABLE)
                return;

            Player currentPlayer = gameModel.getCurrentPlayer();
            Group targetGroup = currentPlayer.getMoveTarget();

            gameScreen.deletePiece(targetGroup.getCurrentLocation().getId());
            gameTurnModel.move(targetGroup);
            gameScreen.printDeckContents(gameTurnModel.getLeftYuts());
            targetGroup.goal();

            if (gameTurnModel.getLeftYuts().isEmpty() && gameTurnModel.getRollCount() == 0) {
                gameModel.switchTurn();
                gameScreen.highlightCurrentPlayer(gameModel.getCurrentPlayer().getId());
            }

            gameScreen.updatePlayerCanvas(currentPlayer.getId(), currentPlayer.getNumOfCurrentPieces());
            gameScreen.setGoalButtonVisible(false);

            if (currentPlayer.getNumOfCurrentPieces() == 0) {
                EndingFrame endingFrame = new EndingFrame(currentPlayer.getId());
                endingFrame.setVisible(true);
                gameScreen.setVisible(false);
            }
        }
    }
}
