package com.yut.model;

public enum YutResult {
    BACKDO(0, "빽도"),
    DO    (1, "도"),
    GE    (2, "개"),
    GUL   (3, "걸"),
    YUT   (4, "윷"),
    MO    (5, "모");

    public final int code;
    public final String korName;

    YutResult(int code, String korName) {
        this.code = code;
        this.korName = korName;
    }

    public boolean grantsExtraTurn() {
        return this == YUT || this == MO;
    }

    public boolean isBackdo() {
        return this == BACKDO;
    }

    /** 이동 칸 수. 빽도는 -1(한 칸 뒤), 나머지는 결과값 그대로. */
    public int getSteps() {
        return this == BACKDO ? -1 : code;
    }

    public static YutResult of(int code) {
        for (YutResult r : values()) {
            if (r.code == code) return r;
        }
        throw new IllegalArgumentException("Invalid yut code: " + code);
    }
}
