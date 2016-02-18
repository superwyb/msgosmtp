package org.wyb.trade;

import java.util.ArrayList;
import java.util.Date;

import org.wyb.smtp.mosmtp.Message;
import org.wyb.smtp.mosmtp.MessageHandler;
import org.wyb.trade.dao.TickDao;
import org.wyb.trade.model.Tick;

public class TickMessageHandler implements MessageHandler {
	
	
	private final TickDao dao;
	
	public TickMessageHandler(TickDao dao){
		this.dao = dao;
	}

	@Override
	public void handle(Message message) {
		if(!"ticks".equals(message.getSubject()))return;
		if(message.getContent()==null || message.getContent().isEmpty())return;		
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		String[] symbols = message.getContent().split("\\|");
		for(int i=0;i<symbols.length;i++){
			Tick tick = convert(symbols[i]);
			if(tick!=null){
				ticks.add(tick);
			}
		}
		if(ticks.size()>0){
			try {
				dao.save(ticks);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//symbol;time_msc;time;bid;ask;last;volume;flag
	private Tick convert(String paramStr){
		if(paramStr == null || paramStr.isEmpty())return null;
		String[] params = paramStr.split(";");
		if(params.length != 8)return null;
		Tick tick = new Tick();
		try{
			tick.setSymbol(params[0]);
			long ts = Long.parseLong(params[1]);
			tick.setTickTimeMsc(ts);
			tick.setTickTime(new Date(ts));
			tick.setBid(Float.parseFloat(params[3]));
			tick.setAsk(Float.parseFloat(params[4]));
			tick.setLast(Float.parseFloat(params[5]));
			tick.setVolume(Integer.parseInt(params[6]));
			tick.setFlag(Integer.parseInt(params[7]));
		}catch(Exception e){
			return null;
		}
		return tick;
	}

}
