package org.wyb.trade.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.wyb.trade.model.Tick;

public class DBTickDao implements TickDao{
	
	private Connection con;
	
	
	private final String url;
	private final String username;
	private final String password;
	private final String driver;
	public DBTickDao(String driver,String url, String username, String password){
		this.url = url;
		this.username = username;
		this.password = password;
		this.driver = driver;
	}
	
	//private final static String INSERT_QUERY = "INSERT INTO trade_tick (symbol,tick_time,tick_time_msc,bid,ask,last,volume,flag) VALUES (?,?,?,?,?,?,?,?)";
	private final static String UPDATE_QUERY = "UPDATE trade_last_tick SET tick_time=?, tick_time_msc=?, bid=?, ask=?, last=?, volume=?, flag=? where symbol=?";
	
	private final static String INIT_QUERY = "INSERT INTO trade_last_tick (symbol,tick_time,tick_time_msc,bid,ask,last,volume,flag) VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE tick_time=?, tick_time_msc=?, bid=?, ask=?, last=?, volume=?, flag=?";
	
	private final static String[] SYMBOL_LIST = {"AUDJPY`","AUDNZD`","AUDUSD`","CHFJPY`","EURAUD`","EURGBP`","EURJPY`","EURNZD`","EURUSD`","GBPCHF`","GBPJPY`","GBPNZD`","GBPUSD`","NZDJPY`","NZDUSD`","USDCAD`","USDCHF`","USDJPY`","USOUSD`","XAGUSD`","XAUUSD`","DOLLAR_INDX"};

	@Override
	public void connect() throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		con = DriverManager.getConnection(url, username, password);
		
		//init last tick table
		initLastTick();
		
	}
	
	private void initLastTick() throws SQLException{
		PreparedStatement insertStat = con.prepareStatement(INIT_QUERY);
		for(int i=0;i<SYMBOL_LIST.length;i++){
			
			long ts = System.currentTimeMillis();
			Date tickDate = new Date(ts);
			Timestamp tickTimestamp = new Timestamp(ts);
			
			insertStat.setString(1, SYMBOL_LIST[i]);
			insertStat.setDate(2, tickDate);
			insertStat.setTimestamp(3, tickTimestamp);

			insertStat.setBigDecimal(4, BigDecimal.ZERO);
			insertStat.setBigDecimal(5, BigDecimal.ZERO);
			insertStat.setBigDecimal(6,BigDecimal.ZERO);
			insertStat.setInt(7, 0);
			insertStat.setInt(8, 0);
			
			insertStat.setDate(9, tickDate);
			insertStat.setTimestamp(10, tickTimestamp);
			insertStat.setBigDecimal(11, BigDecimal.ZERO);
			insertStat.setBigDecimal(12, BigDecimal.ZERO);
			insertStat.setBigDecimal(13,BigDecimal.ZERO);
			insertStat.setInt(14, 0);
			insertStat.setInt(15, 0);
			
			insertStat.addBatch();
		}
		insertStat.executeBatch();
	}
	@Override
	public void disconnect(){
		if(con != null){
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public void save(List<Tick> ticks) throws Exception {
		PreparedStatement updateStat = con.prepareStatement(UPDATE_QUERY);
		//PreparedStatement insertStat = con.prepareStatement(INSERT_QUERY);
		for(Tick tick:ticks){
			
			Date tickDate = new Date(tick.getTickTimeMsc());
			Timestamp tickTimestamp = new Timestamp(tick.getTickTimeMsc());
			/*
			insertStat.setString(1, tick.getSymbol());
			insertStat.setDate(2, tickDate);
			insertStat.setTimestamp(3, tickTimestamp);
			insertStat.setBigDecimal(4, BigDecimal.valueOf(tick.getBid()).setScale(5,BigDecimal.ROUND_FLOOR));
			insertStat.setBigDecimal(5, BigDecimal.valueOf(tick.getAsk()).setScale(5,BigDecimal.ROUND_FLOOR));
			insertStat.setBigDecimal(6, BigDecimal.valueOf(tick.getLast()).setScale(5,BigDecimal.ROUND_FLOOR));
			insertStat.setInt(7, tick.getVolume());
			insertStat.setInt(8, tick.getFlag());
			insertStat.addBatch();
			*/
			int scale = 5;
			if(tick.getSymbol().endsWith("JPY`") || "XAGUSD`".equals(tick.getSymbol())|| "DOLLAR_INDX".equals(tick.getSymbol())){
				scale = 3;
			}else if("XAUUSD`".equals(tick.getSymbol())||"USOUSD`".equals(tick.getSymbol())){
				scale = 2;
			}
			updateStat.setDate(1, tickDate);
			updateStat.setTimestamp(2, tickTimestamp);
			updateStat.setBigDecimal(3, BigDecimal.valueOf(tick.getBid()).setScale(scale,BigDecimal.ROUND_FLOOR));
			updateStat.setBigDecimal(4, BigDecimal.valueOf(tick.getAsk()).setScale(scale,BigDecimal.ROUND_FLOOR));
			updateStat.setBigDecimal(5, BigDecimal.valueOf(tick.getLast()).setScale(scale,BigDecimal.ROUND_FLOOR));
			updateStat.setInt(6, tick.getVolume());
			updateStat.setInt(7, tick.getFlag());
			updateStat.setString(8, tick.getSymbol());
			updateStat.addBatch();
		}
		updateStat.executeBatch();
		updateStat.close();
		/*
		insertStat.executeBatch();
		insertStat.close();
		*/
		
	}

}
