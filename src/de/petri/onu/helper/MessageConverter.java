package de.petri.onu.helper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageConverter {

    public String[] getBetweenTag(String message, String tagName) {
        //create the open and close tag
        String tagOpen = "<" + tagName + ">";
        String tagClose = "</" + tagName + ">";

        //creates the RegEx
        String regex = tagOpen + "(.+?)" + tagClose;

        //creates the patten and initializes the matcher
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(message);

        //creates result list
        ArrayList<String> results = new ArrayList<String>();

        //loops through all finds and adds them to the list
        while(m.find()) {
            results.add(m.group(1));
        }

        //returns the results as a String[]
        String[] result = new String[results.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = results.get(i);
        }
        return result;
    }

    public String tagged(String message, String tagName) {
        String tagOpen = "<" + tagName + ">";
        String tagClose = "</" + tagName + ">";

        return tagOpen + message + tagClose;
    }

    public boolean hasTag(String message, String tagName) {
        //create the open and close tag
        String tagOpen = "<" + tagName + ">";
        String tagClose = "</" + tagName + ">";

        if(message.contains(tagOpen) && message.contains(tagClose)) {
            return true;
        } else {
            return false;
        }
    }

}
