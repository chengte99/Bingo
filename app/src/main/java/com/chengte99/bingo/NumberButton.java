package com.chengte99.bingo;

import android.content.Context;
import android.util.AttributeSet;

public class NumberButton extends androidx.appcompat.widget.AppCompatButton {
    int number;
    boolean isPicked;
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

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        this.isPicked = picked;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
