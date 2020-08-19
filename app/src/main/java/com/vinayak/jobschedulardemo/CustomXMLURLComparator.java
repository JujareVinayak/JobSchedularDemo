package com.vinayak.jobschedulardemo;


import android.util.Log;

import com.kodiak.platform.DroidApiManager;
import com.kodiak.ui.util.Logger;
import com.kodiak.ui.util.CustomFileXMLHandler;

import java.util.ArrayList;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.kodiak.ui.util.CustomXMLURLComparator.ComponentMatchResult.*;
import static com.vinayak.jobschedulardemo.CustomXMLURLComparator.ComponentMatchResult.ENUM_COMPONENT_MATCH_RESULT_FAILED;
import static com.vinayak.jobschedulardemo.CustomXMLURLComparator.ComponentMatchResult.ENUM_COMPONENT_MATCH_RESULT_MATCHED;
import static com.vinayak.jobschedulardemo.CustomXMLURLComparator.ComponentMatchResult.ENUM_COMPONENT_MATCH_RESULT_NOT_MATCHED;
import static com.vinayak.jobschedulardemo.CustomXMLURLComparator.ComponentMatchResult.ENUM_COMPONENT_MATCH_RESULT_NOT_SUPPORTED;
import static com.vinayak.jobschedulardemo.CustomXMLURLComparator.ComponentMatchResult.ENUM_COMPONENT_MATCH_RESULT_NOT_YET_MATCHED;

public class CustomXMLURLComparator {

    private static final String TAG = "CustomXMLURLComparator";

    /*Constant strings for XML parsing*/
    private static String IMS_CELL_URL_TAG = "ims-url";
    private static String INET_CELL_URL_TAG = "inet-url";
    private static String WIFI_URL_TAG = "server-url-wifi";
    private static String LCMS_URL_TAG = "lcm-server-url";
    private static String CC_URL_TAG = "cc-uri";
    private static String INET_OIDC_CELL_URL_TAG = "inet-oidc-discovery-url";
    private static String IMS_OIDC_CELL_URL_TAG = "ims-oidc-discovery-url";
    private static String WIFI_OIDC_CELL_URL_TAG = "wifi-oidc-discovery-url";
    private static String REDIRECT_URL_TAG = "redirect-uri";

    /*Parsed Results will be stored here*/
    private static String IMS_CELL_URL = null;
    private static String INET_CELL_URL = null;
    private static String WIFI_URL = null;
    private static String LCMS_URL = null;
    private static String CC_URL = null;
    private static String INET_OIDC_CELL_URL = null;
    private static String IMS_OIDC_CELL_URL = null;
    private static String WIFI_OIDC_CELL_URL = null;
    private static String REDIRECT_URL = null;

    private static String CONFIG_FILE_NAME = "kn_up_custom.xml";


    private static CustomFileXMLHandler xmlWriteHandler;

    /*What URL in Config File to look for*/
    public enum URLMatchType {
        ENUM_URL_MATCH_TYPE_INET_CELL,
        ENUM_URL_MATCH_TYPE_IMS_CELL,
        ENUM_URL_MATCH_TYPE_WIFI,
        ENUM_URL_MATCH_TYPE_LCMS,
        ENUM_URL_MATCH_TYPE_CC,
        ENUM_URL_MATCH_TYPE_INET_OIDC_CELL,
        ENUM_URL_MATCH_TYPE_IMS_OIDC_CELL,
        ENUM_URL_MATCH_TYPE_WIFI_OIDC,
        ENUM_URL_MATCH_TYPE_REDIRECT,
        ENUM_URL_MATCH_TYPE_ANY
    }

    public enum ComponentMatchType {
        ENUM_COMPONENT_MATCH_TYPE_URL,
        ENUM_COMPONENT_MATCH_TYPE_HOST,
        ENUM_COMPONENT_MATCH_TYPE_PATH,
    }

    public enum ComponentMatchResult {
        ENUM_COMPONENT_MATCH_RESULT_NOT_YET_MATCHED,
        ENUM_COMPONENT_MATCH_RESULT_FAILED,
        ENUM_COMPONENT_MATCH_RESULT_MATCHED,
        ENUM_COMPONENT_MATCH_RESULT_NOT_MATCHED,
        ENUM_COMPONENT_MATCH_RESULT_NOT_SUPPORTED
    }

    public enum ConfigUpdateResult {
        ENUM_CONFIG_UPDATE_RESULT_SUCCESS,
        ENUM_CONFIG_UPDATE_RESULT_FAILED,
        ENUM_CONFIG_UPDATE_RESULT_INVALID,
        ENUM_CONFIG_UPDATE_RESULT_UNABLE_TO_PERFORM
    }

    public static class CustomXMLURLMatchEvent {

        private ComponentMatchResult matchResult = ENUM_COMPONENT_MATCH_RESULT_NOT_YET_MATCHED;
        private String configXMLURL = null;

        public URLMatchType ConfigURLType;
        public ComponentMatchType matchType;
        public String sourceString = null;
        public URL resultURL = null;

        public ComponentMatchResult getMatchResult() {
            return matchResult;
        }

        private void setMatchResult(ComponentMatchResult result) {
            matchResult = result;
        }

        public String getConfigURL() {
            return configXMLURL;
        }

        public boolean isMatched() {

            if(matchResult == ENUM_COMPONENT_MATCH_RESULT_MATCHED) {
                return true;
            } else {
                return false;
            }
        }

        public ConfigUpdateResult writeToConfig() {

            if(resultURL == null) {
                Logger.d(TAG,"ConfigUpdateResult No resultURL found to write. ignoring write operation");
                return ConfigUpdateResult.ENUM_CONFIG_UPDATE_RESULT_FAILED;
            }

            if(matchResult == ENUM_COMPONENT_MATCH_RESULT_MATCHED) {
                Logger.d(TAG,"ConfigUpdateResult Results are already match, no need to update the config file resultUrl: " + resultURL);
                return ConfigUpdateResult.ENUM_CONFIG_UPDATE_RESULT_INVALID;
            } else if(matchResult == ENUM_COMPONENT_MATCH_RESULT_NOT_MATCHED){
                //incase we are in good state(we know its not matched) then only write, else don't perform write, it might corrupt the file.
                String tag = getXMLNodeForURLType(ConfigURLType);
                Logger.d(TAG,"ConfigUpdateResult Components mismatched updating config tag: " + tag + "Value: " + resultURL);
                xmlWriteHandler.writeToXmlFile(CONFIG_FILE_NAME,tag,resultURL.toString());
                return ConfigUpdateResult.ENUM_CONFIG_UPDATE_RESULT_SUCCESS;
            } else {
                Logger.d(TAG,"ConfigUpdateResult  Write failed, unknown state");
                return ConfigUpdateResult.ENUM_CONFIG_UPDATE_RESULT_FAILED;
            }
        }
    }

    public CustomXMLURLComparator() {
        xmlWriteHandler = new CustomFileXMLHandler();
        loadConfigVaues();
    }

    private void registerDataForNode(String nodeName, String nodeValue) {

        if(nodeName == null || nodeValue == null) {
            return;
        }


        switch (nodeName) {
            case "ims-url":
                IMS_CELL_URL = nodeValue;
                break;
            case "inet-url":
                INET_CELL_URL = nodeValue;
                break;
            case "server-url-wifi":
                WIFI_URL = nodeValue;
                break;
            case "lcm-server-url":
                LCMS_URL = nodeValue;
                break;
            case "cc-uri":
                CC_URL = nodeValue;
                break;
            case "inet-oidc-discovery-url":
                INET_OIDC_CELL_URL = nodeValue;
                break;
            case "ims-oidc-discovery-url":
                IMS_OIDC_CELL_URL = nodeValue;
                break;
            case "wifi-oidc-discovery-url":
                WIFI_OIDC_CELL_URL = nodeValue;
                break;
            case "redirect-uri":
                REDIRECT_URL = nodeValue;
                break;
        }
    }

    private void dissectChildNodes(NodeList childNodes) {

        if(childNodes == null) {
            return;
        }

        for(int looper = 0; looper < childNodes.getLength(); ++looper) {

            //get the Node
            Node singleNode = childNodes.item(looper);

            if(singleNode != null && singleNode.getNodeType() == Node.ELEMENT_NODE) {
                registerDataForNode(singleNode.getNodeName(),singleNode.getTextContent());
            }

            if(singleNode.hasChildNodes()) {
                dissectChildNodes(singleNode.getChildNodes());
            }
        }
    }

    private void loadConfigVaues() {

        try {
            String path_to_file = DroidApiManager.getInstance().getStoragePath();
            String fullFilePath = path_to_file+CONFIG_FILE_NAME;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(fullFilePath));
            doc.getDocumentElement().normalize();

           if(doc.hasChildNodes()) {
               dissectChildNodes(doc.getChildNodes());
           }

        } catch (Exception e) {
           // Logger.d(TAG, e);
            Logger.d(TAG,"loadConfigVaues Exception Loading Config Values");
        }

    }

    private static String getXMLNodeForURLType(URLMatchType type) {

        String result = null;
        switch (type) {
            case ENUM_URL_MATCH_TYPE_IMS_CELL:
                result =  IMS_CELL_URL_TAG;
                break;
            case ENUM_URL_MATCH_TYPE_INET_CELL:
                result =  INET_CELL_URL_TAG;
                break;
            case ENUM_URL_MATCH_TYPE_WIFI:
                result =  WIFI_URL_TAG;
                break;
            case ENUM_URL_MATCH_TYPE_LCMS:
                result =  LCMS_URL_TAG;
                break;
            case ENUM_URL_MATCH_TYPE_CC:
                result =  CC_URL_TAG;
                break;
            case ENUM_URL_MATCH_TYPE_INET_OIDC_CELL:
                result =  INET_OIDC_CELL_URL_TAG;
                break;
            case ENUM_URL_MATCH_TYPE_IMS_OIDC_CELL:
                result =  IMS_OIDC_CELL_URL_TAG;
                break;
            case ENUM_URL_MATCH_TYPE_WIFI_OIDC:
                result =  WIFI_OIDC_CELL_URL_TAG;
                break;
            case ENUM_URL_MATCH_TYPE_REDIRECT:
                result =  REDIRECT_URL_TAG;
                break;
        }

        return result;
    }

    private String getConfigURLForType(URLMatchType type) {

        String result = null;
        switch (type) {
            case ENUM_URL_MATCH_TYPE_IMS_CELL:
                result =  IMS_CELL_URL;
                break;
            case ENUM_URL_MATCH_TYPE_INET_CELL:
                result =  INET_CELL_URL;
                break;
            case ENUM_URL_MATCH_TYPE_WIFI:
                result =  WIFI_URL;
                break;
            case ENUM_URL_MATCH_TYPE_LCMS:
                result =  LCMS_URL;
                break;
            case ENUM_URL_MATCH_TYPE_CC:
                result =  CC_URL;
                break;
            case ENUM_URL_MATCH_TYPE_INET_OIDC_CELL:
                result =  INET_OIDC_CELL_URL;
                break;
            case ENUM_URL_MATCH_TYPE_IMS_OIDC_CELL:
                result =  IMS_OIDC_CELL_URL;
                break;
            case ENUM_URL_MATCH_TYPE_WIFI_OIDC:
                result =  WIFI_OIDC_CELL_URL;
                break;
            case ENUM_URL_MATCH_TYPE_REDIRECT:
                result =  REDIRECT_URL;
                break;
        }

        return result;
    }

    /* Returns true if url is valid */
    private boolean isValidURL(String url)
    {
        try {
            //URL to have standard protocol and domain then it will be valid.
            new URL(url).toURI();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /* Returns true if url is valid */
    private URL makeURLFromHost(URL url, String host)
    {
        if(!(url != null && isValidURL(url.toString()) && host != null)) {
            return null;
        }

        try {
            String parsedHost = host;
            int portNum = -1;
            //host name should be plane one without port, in case if we have port we need to separate them
            if(host.indexOf(":") != -1) {
                URL hostURL = new URL("http://" + host);
                parsedHost = hostURL.getHost();
                portNum = hostURL.getPort();
            }

            URL newURL = new URL(url.getProtocol(), parsedHost, portNum == -1 ? url.getPort() : portNum , url.getFile());

            return newURL;
        }
        catch (Exception e) {
            Logger.d(TAG,"makeURLFromHost Exception making URL for Host: " + host + "to actual URL: " + url);
            return null;
        }
    }

    /* Returns true if url is valid */
    private URL getURLFromString(String urlString)
    {
        try {
            //URL to have standard protocol and domain then it will be valid.
            URL newurl = new URL(urlString);
            return newurl;
        }

        catch (Exception e) {
            Logger.d(TAG,"getURLFromString Exception getting URL from String :" + urlString);
            return null;
        }
    }

    private ComponentMatchResult matchForType(URLMatchType urlType, ComponentMatchType matchType, String sourcestring, CustomXMLURLMatchEvent event) {


        String XMLString = getConfigURLForType(urlType);
        URL inputURL = null, XMLURL = null;
        ComponentMatchResult result = ENUM_COMPONENT_MATCH_RESULT_FAILED;
        boolean matchresult = false;
        Logger.d(TAG,"matchForType Received XML String is " + XMLString == null ? "null" : XMLString);
        if(XMLString == null || sourcestring == null) {
            Logger.d(TAG,"matchForType either XML string or src string is null");
            return result;
        }

        if(isValidURL(sourcestring)) {
            Logger.d(TAG,"matchForType source string is valid URL");
            inputURL = getURLFromString(sourcestring);
        }
        if(isValidURL(XMLString)) {
            Logger.d(TAG,"matchForType XML string is valid URL");
            XMLURL = getURLFromString(XMLString);
        }

        if(event != null) {
            event.ConfigURLType = urlType;
            event.matchType = matchType;
            event.sourceString = sourcestring;
        }

        switch (matchType) {
            case ENUM_COMPONENT_MATCH_TYPE_URL:
                Logger.d(TAG,"matchForType matching Complete URl");
                if (inputURL == null || XMLURL == null) {
                    Logger.d(TAG,"matchForType URL match failed either of the inputURl or XMLUrl is null");
                    result = ENUM_COMPONENT_MATCH_RESULT_FAILED;
                }
                if (inputURL.equals(XMLURL)) {
                    matchresult = true;
                    result = ENUM_COMPONENT_MATCH_RESULT_MATCHED;
                } else {
                    matchresult = false;
                    result = ENUM_COMPONENT_MATCH_RESULT_NOT_MATCHED;
                }
                Logger.d(TAG,"matchForType(URL) input url " + inputURL + " XML URL " + XMLURL + "Result: " + matchresult);
                event.resultURL = inputURL; //update complete url
                break;
            case ENUM_COMPONENT_MATCH_TYPE_HOST:
                Logger.d(TAG,"matchForType matching only Host");
                if (XMLURL == null) {
                    Logger.d(TAG,"matchForType XMLUrl is null can't process");
                    result = ENUM_COMPONENT_MATCH_RESULT_FAILED;
                } else {
                    String inputHost = inputURL == null ? sourcestring : inputURL.getHost();
                    if(inputHost.equals(XMLURL.getHost())) {
                        matchresult = true;
                        result = ENUM_COMPONENT_MATCH_RESULT_MATCHED;
                    } else {
                        matchresult = false;
                        result = ENUM_COMPONENT_MATCH_RESULT_NOT_MATCHED;
                    }

                    Logger.d(TAG,"matchForType(HOST) input Host " + inputHost + " XML URL " + XMLURL + "Result: " + matchresult);

                    if(inputURL != null) {
                        //passed us the URL for host comparision. so we will assume that input url is the final result.
                        event.resultURL = inputURL;
                    } else {
                        //we got only host, we need to inject that into xml url.
                        //if null then we were unable to parse the create URL.
                        event.resultURL = makeURLFromHost(XMLURL,sourcestring);
                        Logger.d(TAG,"matchForType(HOST) After Injecting Host to XML URL: " + event.resultURL);
                    }
                }
                break;
            case ENUM_COMPONENT_MATCH_TYPE_PATH:
                Logger.d(TAG,"matchForType UnSupported Match Type");
                result = ENUM_COMPONENT_MATCH_RESULT_NOT_SUPPORTED;
                break;
        }

        return result;
    }


    public void match(ArrayList<CustomXMLURLMatchEvent> events) {

        try {
            if (events == null || events.isEmpty()) {
                return;
            }

            for (int looper = 0; looper < events.size(); ++looper) {
                CustomXMLURLMatchEvent event = events.get(looper);
                Logger.d(TAG,"matching for ConfigURLType:" + event.ConfigURLType + " match Type:" + event.matchType + "srcString:" + event.sourceString);
                event.matchResult = matchForType(event.ConfigURLType, event.matchType, event.sourceString, event);
            }
        } catch (Exception e) {
            return;
        }
    }


    public void test() {

        ArrayList<CustomXMLURLMatchEvent> events = new ArrayList<>(); //list of input values

        String[] hostnames = {"dummy.com:2030","dummy.sms.com","dummy.sms.com","dummy.lcms.com"};
        URLMatchType[] URLTypes = {URLMatchType.ENUM_URL_MATCH_TYPE_IMS_CELL,URLMatchType.ENUM_URL_MATCH_TYPE_INET_CELL,URLMatchType.ENUM_URL_MATCH_TYPE_IMS_OIDC_CELL,URLMatchType.ENUM_URL_MATCH_TYPE_LCMS};

        for(int i = 0; i < hostnames.length ; i++) {
            CustomXMLURLMatchEvent event = new CustomXMLURLMatchEvent();
            event.ConfigURLType = URLTypes[i];
            event.matchType = ComponentMatchType.ENUM_COMPONENT_MATCH_TYPE_HOST;
            event.sourceString = hostnames[i];
            events.add(event);
        }

        match(events);

        for(int i = 0; i < events.size(); ++i) {
            CustomXMLURLMatchEvent event = events.get(i);
            if(event.getMatchResult() == ENUM_COMPONENT_MATCH_RESULT_MATCHED) {
                System.out.println(event.sourceString + " Matched");
                event.writeToConfig();
            } else {
                System.out.println(event.sourceString + " Not Matched");
            }
        }
    }
}