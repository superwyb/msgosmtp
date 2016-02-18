package org.wyb.trade.model;

import java.util.Date;

public class Tick {
	
	private String symbol;
	private Date tickTime;
	private float bid;
	private float ask;
	private float last;
	private int volume;
	private int flag;
	private long tickTimeMsc;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Date getTickTime() {
		return tickTime;
	}
	public void setTickTime(Date tickTime) {
		this.tickTime = tickTime;
	}
	public float getBid() {
		return bid;
	}
	public void setBid(float bid) {
		this.bid = bid;
	}
	public float getAsk() {
		return ask;
	}
	public void setAsk(float ask) {
		this.ask = ask;
	}
	public float getLast() {
		return last;
	}
	public void setLast(float last) {
		this.last = last;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public long getTickTimeMsc() {
		return tickTimeMsc;
	}
	public void setTickTimeMsc(long tickTimeMsc) {
		this.tickTimeMsc = tickTimeMsc;
	}
	
	

}
