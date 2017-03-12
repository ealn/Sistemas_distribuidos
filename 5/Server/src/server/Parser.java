package server;

import java.util.ArrayList;

public class Parser implements Constants
{
    public static ArrayList <String> getListOfParameters(String inputStr, String parameter)
    {
        ArrayList <String>list = null;
        String  subString = null;
        String  str = null;
        int     index  = 0;
        
        subString = inputStr;
        
        while (subString.indexOf(parameter) != -1)
        {
            str = parseString(subString, parameter);
            
            if (str != null)
            {
                if (list == null)
                {
                    list = new ArrayList<>();
                }
                
                list.add(str);
                
                //get the index where the string was found
                index = subString.indexOf(parameter + str);
                
                //get a substring after the string found
                subString = subString.substring(index + str.length());
            }
            else
            {
                //No more matches
                break;
            }
        }
        
        return list;
    }
    
    public static String parseString(String str, String parameter)
    {
        String retStr = null;
        int index = 0;
        String subStr = null;
        
        if (str != null)
        {
            index = str.indexOf(parameter);
            
            if (index != -1)
            {
                //get a substring from index1
                subStr = str.substring(index + parameter.length());
                
                //search the next parameter separator
                index = subStr.indexOf(SEPARATOR);
                
                if (index != -1)
                {
                    //found
                    retStr = subStr.substring(0, index);
                }
            }
        }
        
        return retStr;
    }
}
