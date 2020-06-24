package com.chengte99.bingo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class NumberButton extends androidx.appcompat.widget.AppCompatButton {
    int number;
    boolean is_picked;
    int pos;

    public NumberButton(Context context) {
        super(context);
    }

    public NumberButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isIs_picked() {
        return is_picked;
    }

    public void setIs_picked(boolean is_picked) {
        this.is_picked = is_picked;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
