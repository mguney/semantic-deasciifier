package com.gun3y.nlp.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.gun3y.nlp.ui.MainScreen;

public class SwingLogger extends AppenderSkeleton {

    protected void append(LoggingEvent event) {
	MainScreen.LOG_MODEL.addElement(event.getMessage().toString());
    }

    public void close() {

    }

    public boolean requiresLayout() {
	return false;
    }
}
