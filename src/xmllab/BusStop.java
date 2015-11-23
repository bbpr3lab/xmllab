package xmllab;

public class BusStop {

	String name, oldName, wheelchair;
	boolean valid;
	double distance;
	
	public String toString() {
		return "Megallo:\n\tNev: " + name + " (" + oldName + ")\n\t"
				+ "Kerekesszek: " + wheelchair;
	}
	
	
}
