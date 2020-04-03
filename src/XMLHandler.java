import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class XMLHandler extends DefaultHandler {

    int limit = 5000000;
    int number = 0;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("voter") && number < limit) {
            String name = attributes.getValue("name");
            String birthDate = attributes.getValue("birthDay");
            try {
                DBConnection.insertVoter(name, birthDate);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            number++;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {}
}
