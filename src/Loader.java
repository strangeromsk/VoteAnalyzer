import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;


public class Loader {

    public static void main(String[] args) throws Exception {
        final long beforeParsing = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        final long start = System.currentTimeMillis();

        String fileName = "res/data-1572M.xml";

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLHandler handler = new XMLHandler();
        parser.parse(new File(fileName), handler);

        final long end = System.currentTimeMillis() - start;
        final long afterParsing = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        final long diff = (afterParsing - beforeParsing) / 1048576;
        System.err.println("Memory consumption = " + diff + " MBs\tTime duration = " + end + " sec");

        //DBConnection.closeConnection();
    }
}