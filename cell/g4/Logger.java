package cell.g4;

/*
 * In courtesy of G5 
 */
public abstract class Logger {
	private static final boolean DEBUG = true;	
	
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
