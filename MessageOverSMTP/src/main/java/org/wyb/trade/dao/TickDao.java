package org.wyb.trade.dao;

import java.util.List;

import org.wyb.trade.model.Tick;

public interface TickDao {
	
	public void connect() throws Exception;
	public void disconnect();
	public void save(List<Tick> ticks) throws Exception;

}
