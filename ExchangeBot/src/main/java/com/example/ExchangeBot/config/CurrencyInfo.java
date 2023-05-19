package com.example.ExchangeBot.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

@Data
@AllArgsConstructor
public class CurrencyInfo {
    //
    private String title;
    private Double value;

    //Method to read Currencies from Api
    public static ArrayList<CurrencyInfo> readCurrencies() throws MalformedURLException {
        //Api to the Current Rate of the different Currencies
        URL url = new URL("https://nationalbank.kz/rss/rates_all.xml");
        ArrayList<CurrencyInfo> currencyInfos = new ArrayList<>();

        try (InputStream input = url.openStream()) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();
            //Getting element by tagName (Tag item contains all currencies)
            NodeList list = doc.getElementsByTagName("item");
            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    //Reading title(Name of the currency)
                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    //Reading description(Rate of the currency)
                    String value = element.getElementsByTagName("description").item(0).getTextContent();
                    currencyInfos.add(new CurrencyInfo(title, Double.parseDouble(value)));
                }

            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
        return currencyInfos;
    }

    //Method to get Currency
    public static Double chooseCurrency(String currency) {
        ArrayList<CurrencyInfo> currencyInfos = new ArrayList<>();
        try {
            //Variable saving all List of Currencies with their values
            currencyInfos = CurrencyInfo.readCurrencies();
            for (CurrencyInfo currencyInfo : currencyInfos) {
                if (Objects.equals(currencyInfo.getTitle(), currency))
                    //Returning actual currency
                    return currencyInfo.getValue();
            }
        } catch (MalformedURLException ex) {
            System.out.println(ex.getMessage());
        }
        return (double) 0;
    }


}


