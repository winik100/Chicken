package de.hhu.propra.chicken.aggregates;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class LsfValidierung {

    boolean gueltigeLsfId(Long lsfId, Document... document) throws IOException {
        String lsfIdString = lsfId.toString();
        Document doc;
        if (document.length != 0) {
            doc = document[0];
        } else {
            doc = Jsoup.connect("https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid="
                    + lsfIdString
                    + "&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung").get();
        }
        String htmlDoc = doc.wholeText();
        return htmlDoc.contains(lsfIdString);
    }
}