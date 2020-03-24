import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class XMLHandler extends DefaultHandler {
    Voter voter;
    private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");

    public static HashMap<Voter, Integer> getVoterCounts() {
        return voterCounts;
    }

    private static HashMap<Voter, Integer> voterCounts;

    public XMLHandler() {
        voterCounts = new HashMap<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            if (qName.equals("voter") && voter == null) {
                Date birthDay = birthDayFormat.parse(attributes.getValue("birthDay"));
                voter = new Voter(attributes.getValue("name"), birthDay);
            } else if (qName.equals("visit") && voter != null) {
                int count = voterCounts.getOrDefault(voter, 0);
                voterCounts.put(voter, count + 1);
            }
            if (voterCounts.keySet().size() == 40000) {
                String pattern = "yyyy-MM-dd";
                DateFormat df = new SimpleDateFormat(pattern);
                for (Voter voter : voterCounts.keySet()) {
                    int count = voterCounts.get(voter);
                    DBConnection.executeMultiInsert(voter.getName(), df.format(voter.getBirthDay()), count);
                }
                voterCounts.clear();
            }
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!voterCounts.keySet().isEmpty()) {
            String pattern = "yyyy-MM-dd";
            DateFormat df = new SimpleDateFormat(pattern);
            for (Voter voter : voterCounts.keySet()) {
                int count = voterCounts.get(voter);
                try {
                    DBConnection.executeMultiInsert(voter.getName(), df.format(voter.getBirthDay()), count);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            voterCounts.clear();
        }
        if (qName.equals("voter")) {
            voter = null;
        }
    }

    public void insert() throws SQLException {
        String pattern = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(pattern);
        voterCounts.keySet().forEach(e -> {
            String name = e.getName();
            String birthDate = df.format(e.getBirthDay());

        });
        DBConnection.executeMultiInsert(voter.getName(), df.format(voter.getBirthDay()), 1);

//        for(Voter voter : voterCounts.keySet()){
//            int count = voterCounts.get(voter);
//            if(count > 1){
//                System.out.println(voter.toString() + " - " + count);
//            }
//        }
    }
}
