/*******************************************************************************
 *        File: PersianNumberFormatter.java
 * Description: Formats a number into a Persian String object.
 *      Author: Arman Radmanesh <arman@ratnic.se>
 *  Created on: Dec 5, 2014
 *     Project: ratnic.quizzer
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package ext;

import play.templates.JavaExtensions;

/**
 * The Class PersianNumberFormatter.
 *
 * @author arman
 * @version 1
 */
public class PersianNumberFormatter extends JavaExtensions {
    private static final char[] PersianNumbers = { '\u06f0', '\u06f1', '\u06f2', '\u06f3', '\u06f4', '\u06f5',
            '\u06f6', '\u06f7', '\u06f8', '\u06f9' };

    /**
     * Formats a number into a Persian String object.
     *
     * @param number the number to be formatted
     * @return the output string
     */
    public static String format(Number number){
        return convertDigits(number.toString());
    }
    private static String convertDigits (String str) {
        if (str == null || str.length() == 0)
            return str;

        char[] s = new char[str.length()];
        for (int i = 0; i < s.length; i++)
            s[i] = toDigit(str.charAt(i));

        return new String(s);
    }
    private static char toDigit (char ch) {
        int n = Character.getNumericValue( (int)ch );
        return n >=0 && n < 10 ? PersianNumbers[n] : ch;
    }

    /**
     * Convert an int into Persian number string.
     *
     * @param num the num
     * @return the string
     */
    public static String toString (int num) {
        return convertDigits( Integer.toString( num ) );
    }
}
