package com.donglu.carpark.util;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.Layout;

public class MyAppender extends AppenderBase<LoggingEvent> {
	protected PatternLayoutEncoder encoder = new PatternLayoutEncoder();
	public interface LogCallback{
		public void log(String s);
	}
	private Layout<ILoggingEvent> layout;
	public static List<LogCallback> list=new ArrayList<>();
    @Override  
    protected void append(LoggingEvent eventObject) {
    	for (LogCallback log : list) {
    		log.log(layout.doLayout(eventObject));
		}
    }  
    
    @Override
    public Context getContext() {
    	return super.getContext();
    }
	public PatternLayoutEncoder getEncoder() {
		return encoder;
	}
	public void setEncoder(PatternLayoutEncoder encoder) {
		System.out.println("setEncoder");
		this.encoder = encoder;
		layout = encoder.getLayout();
	}
    
} 