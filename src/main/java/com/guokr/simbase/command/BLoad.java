package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class BLoad extends SimCommand {

    @Override
    public String signature() {
        return "s";
    }

    @Override
    public void invoke(SimEngine engine, String bkey, SimCallback callback) {
        engine.bload(callback, bkey);
    }

}
