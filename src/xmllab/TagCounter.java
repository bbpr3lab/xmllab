package xmllab;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TagCounter extends DefaultHandler {
	
		private double lat;

		private double lon;
		

		public TagCounter(double lat, double lon) {
		// TODO Auto-generated constructor stub
			this.lat = lat;
			this.lon = lon;
		}


		static double dist1(double lat1, double lon1, double lat2, double lon2) {
			double R = 6371000;
			double phi1 = Math.toRadians(lat1);
			double phi2 = Math.toRadians(lat2);
			double dphi = phi2-phi1;
			double dl = Math.toRadians(lon2-lon1);
			
			double a = Math.sin(dphi/2) * Math.sin(dphi/2) +
					Math.cos(phi1) * Math.cos(phi2) *
					Math.sin(dl/2) * Math.sin(dl/2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			double d = R * c;
			return d;
		}
	
		Map<String, Integer> elementCount = new HashMap<>(0);
		
		BusStop busStop;
		
		List<BusStop> busStops = new ArrayList<>();
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// TODO Auto-generated method stub
			
			if (qName.equals("node")) {
				busStop = new BusStop();
				double nlat, nlon;
				nlat = Double.parseDouble(attributes.getValue("lat"));
				nlon = Double.parseDouble(attributes.getValue("lon"));
				busStop.distance = dist1(lat, lon, nlat, nlon);
			} else if (qName.equals("tag")) {
				String k = attributes.getValue("k");
				String v = attributes.getValue("v");
				switch (k) {
				case "highway":
					if (v.equals("bus_stop"))
						busStop.valid = true;
					break;
				case "name":
					busStop.name = v;
					break;
				case "old_name":
					busStop.oldName = v;
					break;
				case "wheelchair":
					busStop.wheelchair = v;
					break;
				}
			}

			int intValue = elementCount.containsKey(qName) ? elementCount.get(qName).intValue() : 0;
			elementCount.put(qName, ++intValue);
		}
		
		
		
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			if (qName.equals("node") && busStop.valid) {
//				System.out.println(busStop);
				busStops.add(busStop);
			}
		}



		public void printStats() {
			busStops.stream().sorted((b1, b2) -> Double.compare(b1.distance, b2.distance))
								.forEach(System.out::println);;
			Set<Entry<String, Integer>> entries = elementCount.entrySet();
			long total = 0;
			for (Entry<String, Integer> entry : entries) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
				total += entry.getValue();
			}
			System.out.println("total: " + total);
			System.out.println("bus stops: " + busStops.size());
		}
		

	public static void main(String... args) throws Exception {
		
		String filename = null;
		
		double lat = 0, lon = 0;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i"))
				filename = args[++i];
			else if (args[i].equals("-lat"))
				lat = Double.parseDouble(args[++i]);
			else if (args[i].equals("-lon"))
				lon = Double.parseDouble(args[++i]);
		}
		
		TagCounter h= new TagCounter(lat, lon);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser p = factory.newSAXParser();
		p.parse(new File(filename), h);
		h.printStats();
	}
}
