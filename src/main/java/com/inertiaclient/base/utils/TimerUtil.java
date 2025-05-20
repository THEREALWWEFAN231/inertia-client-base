package com.inertiaclient.base.utils;

public class TimerUtil {

    private long currentMS = 0L;
    private long lastMS = -1L;

    public TimerUtil() {

    }

    public TimerUtil(boolean reset) {
        if (reset) {
            this.reset();
        }
    }

    public boolean hasDelayRun(double time) {
        return (this.currentMS - this.lastMS) >= time;
    }

    public long getTime() {
        return (this.currentMS - this.lastMS);
    }

    public void update() {
        this.currentMS = TimerUtil.currentTimeMillis();
    }

    public void reset() {
        this.lastMS = TimerUtil.currentTimeMillis();
    }

    //so we use nanotime instead of currentTimeMillis, nanotime isn't changed by the user changing there computer time, diving it by 1000000(to milliseconds) isn't *necessarily* needed(it is for milliseconds, but not for the WWETimerUtils), but most other clients(and NetHandlerPlayServer.currentTimeMillis) do it so i assume its better? i also should note nanoTime is supposingly alot slower then currentTimeMillis. And nanoTime is a little more accurate then currentTimeMillis(honestly we probably don't need it but its there....)
    public static long currentTimeMillis() {
        return System.nanoTime() / 1000000L;
    }
}
