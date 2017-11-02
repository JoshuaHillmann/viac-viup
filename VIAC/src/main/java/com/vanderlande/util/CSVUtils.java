package com.vanderlande.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * The Class CSVUtils.
 * 
 * @author dekscha
 * 
 *         This class provides methods to write comma separated value data to a Writer
 */
public class CSVUtils
{

    /** The Constant DEFAULT_SEPARATOR. */
    private static final char DEFAULT_SEPARATOR = ',';

    /**
     * Write line.
     *
     * @param w
     *            the writer
     * @param values
     *            the values
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeLine(Writer w, List<String> values)
        throws IOException
    {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    /**
     * Write line.
     *
     * @param w
     *            the writer
     * @param values
     *            the values
     * @param separators
     *            the separators
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeLine(Writer w, List<String> values, char separators)
        throws IOException
    {
        writeLine(w, values, separators, ' ');
    }

    /**
     * Follow CSV format. See https://tools.ietf.org/html/rfc4180
     *
     * @param value
     *            the value
     * @return the string
     */
    private static String followCSVformat(String value)
    {
        String result = value;
        if (result.contains("\""))
        {
            result = result.replace("\"", "\"\"");
        }
        return result;
    }

    /**
     * Write line.
     *
     * @param w
     *            the writer
     * @param values
     *            the values
     * @param separators
     *            the separators
     * @param customQuote
     *            the custom quote
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeLine(Writer w, List<String> values, char separators, char customQuote)
        throws IOException
    {
        boolean first = true;

        // default customQuote is empty

        if (separators == ' ')
        {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values)
        {
            if (!first)
            {
                sb.append(separators);
            }
            if (customQuote == ' ')
            {
                sb.append(followCSVformat(value));
            }
            else
            {
                sb.append(customQuote).append(followCSVformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }
}