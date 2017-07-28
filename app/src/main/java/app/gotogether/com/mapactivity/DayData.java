package app.gotogether.com.mapactivity;

public class DayData {
	private int itYear;
	private int itMonth;
	private int itDay;
	private String content;
	
	private int hour;
	private int min;
	private String txt =null;
	
	public DayData(){
		init();
	}
	
	public DayData(int Y, int M, int D, String C){
		itYear = Y;
		itMonth = M;
		itDay = D;
		content = C;
	}

	
	public void init(){
		hour = 0;
		min = 0;
		txt = "";
	}
	
	public void setYear(int Y){
		itYear = Y;
	}
	
	public void setMonth(int M){
		itMonth = M;
	}
	
	public void setDay(int D){
		itDay = D;
	}
	
	public int getYear(){
		return itYear;
	}
	
	public int getMonth(){
		return itMonth;
	}
	
	public int getDay(){
		return itDay;
	}

	public void setContent(String C){
		content = C;
	}
	public String getContent(){
		return content;
	}
	public void setString(String T){
		txt = T;
	}
	
	public void setTime(int H, int M){
		hour = H;
		min = M;
	}
	
	public String getString(){
		txt = Integer.valueOf(getYear()).toString() + Integer.valueOf(getMonth()).toString() + Integer.valueOf(getDay()).toString();
		return txt;
	}
	
	public String getTime(){
		return ""+hour+" : "+min;
				
	}
	
	public String getScadule(){
		return content;
	}
}
