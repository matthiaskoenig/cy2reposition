package reposition;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogCyPlugin {
	private String name;
	private static final String line = "---------------------------------------";
	
	private enum PrintLevels {
		CONFIG, INFO, WARNING, ERROR, TEST;
	}
	
	public LogCyPlugin(String name){
		this.name = name;
	}
	
	public String config(String text){
		return config(text, true);
	}
	public String config(String text, boolean output){
		return printLevel(System.out, PrintLevels.CONFIG, text, output);
	}
	
	public String info(String text){
		return info(text, true);
	}
	public String info(String text, boolean output){
		return printLevel(System.out, PrintLevels.INFO, text, output);
	}
	
	public String warning(String text){
		return warning(text, true);
	}
	public String warning(String text, boolean output){
		return printLevel(System.err, PrintLevels.WARNING, text, output);
	}
	
	public String error(String text){
		return error(text, true);
	}
	public String error(String text, boolean output){
		return printLevel(System.err, PrintLevels.ERROR, text, output);
	}
	
	public String test(String text){
		return test(text, true);
	}
	public String test(String text, boolean output){
		return printLevel(System.out, PrintLevels.TEST, text, output);
	}
	
	private String printLevel(PrintStream stream, PrintLevels level, String text, boolean output){
		String out = String.format("%s[%s]: %s", name, level.toString(), text);
		if (output){
			if (level == PrintLevels.TEST){
				stream.println(line);
				stream.println(out);
				stream.println(line);
			}else {
				stream.println(out);	
			}
			
		}
		return out;
	}
	
	/* Function to log StackTraces */
	public static String getStackTrace(Throwable t){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}	
	
}
