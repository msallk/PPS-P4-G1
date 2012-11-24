package cell.g1;

public interface Logger {
	void log(String message);
	
	public static final Logger DEFAULT_LOGGER = new Logger(){
		public void log(String message){
			System.out.println(message);
		}
	};
}
