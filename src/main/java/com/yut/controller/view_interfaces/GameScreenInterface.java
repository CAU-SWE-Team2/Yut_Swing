package com.yut.controller.view_interfaces;

import com.yut.model.YutResult;
import com.yut.ui.swing.ClickableNode;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Deque;
import java.util.Map;

public interface GameScreenInterface {
    void showMovePreview(int nodeID, int playerID);
    void deleteMovePreview();
    void drawPiece(int nodeID, int playerID, int pieceNumber);
    void deletePiece(int nodeID);
    void updatePlayerCanvas(int playerID, int pieceCount);
    void highlightCurrentPlayer(int playerId);
    void select(int nodeID);
    void updateRandomResult(YutResult result);
    void printDeckContents(Deque<YutResult> deck);
    int getNodeState(int nodeID);
    void addRandomThrowButtonListener(ActionListener listener);
    void addSelectedThrowButtonListener(int index, ActionListener listener);
    void addBackButtonListener(ActionListener listener);
    void addNodeClickListener(ClickableNode node, MouseListener listener);
    void addMoveNewPieceButtonListener(ActionListener listener);
    void addGoalButtonListener(ActionListener listener);
    void setGoalButtonVisible(boolean visible);
    Map<Integer, ClickableNode> getNodeMap();
}
