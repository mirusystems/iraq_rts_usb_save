package com.mirusystems.usbsave;


import com.mirusystems.usbsave.data.UsbListEntity;

public class UsbState {
    public static final int STATE_UNKNOWN = -1;
    public static final int STATE_READY = 0;
    public static final int STATE_WRITING_CARD = 1;
    public static final int STATE_WRITE_SUCCESS = 2;
    public static final int STATE_WRITE_FAILURE = 3;
    public static final int STATE_SELECTED = 4;
    public static final int STATE_DESELECTED = 5;

    private int position;
    private int state;
    private UsbListEntity ps;

    public UsbState(int position, int state) {
        this.position = position;
        this.state = state;
        this.ps = null;
    }

    public UsbState(int position, int state, UsbListEntity ps) {
        this.position = position;
        this.state = state;
        this.ps = ps;
    }

    public int getPosition() {
        return position;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public UsbListEntity getPs() {
        return ps;
    }

    @Override
    public String toString() {
        return "PsState{" +
                "position=" + position +
                ", state=" + state +
                ", ps=" + ps +
                '}';
    }
}
