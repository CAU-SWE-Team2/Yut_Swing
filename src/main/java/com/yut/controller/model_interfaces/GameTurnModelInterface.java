package com.yut.controller.model_interfaces;

import java.util.Deque;
import java.util.function.Consumer;

import com.yut.model.Group;
import com.yut.model.Node;
import com.yut.model.YutResult;

public interface GameTurnModelInterface {
    int THROWABLE = 1;
    int HASTOMOVE = 2;

    enum Event { EXTRA_TURN_GRANTED, BACKDO_SKIPPED }

    // type == -2면 랜덤, 아니면 type을 YutResult.of(type)으로 변환
    void roll(int type);
    Node showNextMove(Group group);
    void move(Group group);
    int getState();

    Deque<YutResult> getLeftYuts();
    int getRollCount();

    void setEventCallback(Consumer<Event> callback);
}
