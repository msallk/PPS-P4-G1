package cell.g5;

import java.util.Map;

public abstract class Logger {
	private static final boolean DEBUG;
	
	static {
		Map<String, String> env = System.getenv();
		if(env.containsKey("debug_logging")) {
			DEBUG = true;
		} else {
			DEBUG = false;
		}
	}
	
	public static Logger getLogger(Class<?> clazz) {
		return new ClassLogger(clazz);
	}
	
	public abstract void log(String msg);
	
	public static class ClassLogger extends Logger {
		private String clazz;
		
		public ClassLogger(Class<?> clazz) {
			this.clazz = clazz.getSimpleName();
		}
		
		@Override
		public void log(String msg) {
			if(DEBUG) {
				System.out.println(clazz + ": " + msg);
			}
		}
	}
}
