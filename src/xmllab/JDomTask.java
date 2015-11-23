package xmllab;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class JDomTask {
	
	static double lat = 0, lon = 0;
	
	static String infile, outfile;
	
	static long countElements(Element e) {
		long count = 0;
		List<Element> children = e.getChildren();
		count += children.size();
		for (Element child : children) {
			count += countElements(child);
		}
		return count;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		parseArgs(args);
		Document document = buildDocument();

		feladat5(document);
	}
	
	static void parseArgs(String... args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i"))
				infile = args[++i];
			else if (args[i].equals("-o"))
				outfile = args[++i];
			else if (args[i].equals("-lat"))
				lat = Double.parseDouble(args[++i]);
			else if (args[i].equals("-lon"))
				lon = Double.parseDouble(args[++i]);
		}
	}
	
	static void feladat5(Document d) throws Exception {
		Element root = d.getRootElement();
		List<Element> children = new ArrayList<>(root.getChildren());
		for (Element child : children) {
			if (!child.getName().equals("node")) {
				child.detach();
				continue;
			}
			boolean busStopTagFound = false;
			List<Element> tags = child.getChildren();
			for (Element tag : tags) {
				if (tag.getName().equals("tag") && 
						tag.hasAttributes() && tag.getAttributeValue("v").equals("bus_stop")) {
					busStopTagFound = true;
					break;
				}
			}
			if (!busStopTagFound)
				child.detach();
			else {
				addDistance(child);
			}
		}
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(d, new FileWriter(outfile));
	}
	
	static void addDistance(Element node) {
		double nlat = Double.parseDouble(node.getAttributeValue("lat"));
		double nlon = Double.parseDouble(node.getAttributeValue("lon"));
		double dist = TagCounter.dist1(lat, lon, nlat, nlon);
		Element distTag = new Element("tag");
		distTag.setAttribute("k", "distance");
		distTag.setAttribute("v", Double.toString(dist));
		node.addContent(distTag);
	}

	static Document buildDocument() throws Exception {
		SAXBuilder b = new SAXBuilder();
		File f = new File(infile);
		
		return (Document) b.build(f);
	}
	
	static void feladat4(Document d) throws Exception {
		long count = 1;
		count += countElements(d.getRootElement());
		System.out.println(count);
	}

}
