package com.yut.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import com.yut.controller.model_interfaces.GameTurnModelInterface;

public class GameTurn implements GameTurnModelInterface {

    private Deque<YutResult> leftYuts;
    private final Player currentPlayer;
    private int rollCount;
    private final Yut yut;
    private int state;
    private Consumer<Event> eventCallback;

    public GameTurn(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        this.rollCount = 1;
        this.yut = Yut.getYut();
        this.leftYuts = new ArrayDeque<>();
        this.state = GameTurnModelInterface.THROWABLE;
    }

    @Override
    public void setEventCallback(Consumer<Event> callback) {
        this.eventCallback = callback;
    }

    private void fireEvent(Event event) {
        if (eventCallback != null) eventCallback.accept(event);
    }

    // type == -2면 랜덤, 아니면 YutResult.of(type)으로 변환
    @Override
    public void roll(int type) {
        if (type == -2)
            yut.rollYutRandomly();
        else
            yut.rollYutSelected(YutResult.of(type));

        YutResult result = yut.getCurrent();
        rollCount--;

        if (result.grantsExtraTurn()) {
            rollCount++;
            fireEvent(Event.EXTRA_TURN_GRANTED);
            leftYuts.addLast(result);
        } else if (result.isBackdo() && currentPlayer.getNumOfWaitingPieces() == currentPlayer.getTotalPieces()) {
            fireEvent(Event.BACKDO_SKIPPED);
            rollCount++;
        } else {
            leftYuts.addLast(result);
        }

        if (rollCount == 0)
            state = GameTurnModelInterface.HASTOMOVE;
    }

    @Override
    public Node showNextMove(Group group) {
        YutResult nextYut = leftYuts.getFirst();
        return group.getNextNode(nextYut.getSteps());
    }

    @Override
    public void move(Group group) {
        YutResult nextYut = leftYuts.getFirst();
        int result = group.move(group.getNextNode(nextYut.getSteps()));

        if (result == 1 && currentPlayer.getNumOfCurrentPieces() != 0) {
            fireEvent(Event.EXTRA_TURN_GRANTED);
            rollCount++;
            state = GameTurnModelInterface.THROWABLE;
        }
        leftYuts.removeFirst();
    }

    @Override
    public int getState() { return state; }

    @Override
    public Deque<YutResult> getLeftYuts() { return leftYuts; }

    @Override
    public int getRollCount() { return rollCount; }
}
