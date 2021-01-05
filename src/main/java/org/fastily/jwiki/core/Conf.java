package org.fastily.jwiki.core;

import okhttp3.HttpUrl;

/**
 * Per-Wiki configurable settings.
 * 
 * @author Fastily
 *
 */
public class Conf
{
	/**
	 * The {@code User-Agent} header to use for HTTP requests.
	 */
	protected String userAgent = String.format("jwiki on %s %s with JVM %s", System.getProperty("os.name"),
			System.getProperty("os.version"), System.getProperty("java.version"));

	/**
	 * The url pointing to the base MediaWiki API endpoint.
	 */
	protected HttpUrl baseURL;

	/**
	 * Flag indicating whether the logged in user is a bot.
	 */
	protected boolean isBot = false;

	/**
	 * The hostname of the Wiki to target. Example: {@code en.wikipedia.org}
	 */
	protected String hostname;

	/**
	 * The low maximum limit for maximum number of list items returned for queries that return lists. Use this if a max
	 * value is needed but where the client does not know the max.
	 */
	protected int maxResultLimit = 500;

	/**
	 * User name (without namespace prefix), only set if user is logged in.
	 */
	protected String uname = null;

	/**
	 * Flag indicating whether logs should be prefixed with the current wiki instance.
	 */
	protected boolean prefixLogs = true;

	/**
	 * CSRF token. Used for actions that change Wiki content.
	 */
	protected String token = "+\\";

	/**
	 * Constructor, creates a new Conf pointing to en.wikipedia.org.
	 */
	protected Conf()
	{
		retarget(HttpUrl.parse("https://en.wikipedia.org/w/api.php"));
	}

	/**
	 * Points this Conf to another endpoint.
	 * 
	 * @param baseURL The new API endpoint to use.
	 */
	protected void retarget(HttpUrl baseURL)
	{
		this.baseURL = baseURL;
		hostname = baseURL.host();
	}

    public String getUserAgent() {
        return userAgent;
    }

    public HttpUrl getBaseURL() {
        return baseURL;
    }

    public boolean isBot() {
        return isBot;
    }

    public String getHostname() {
        return hostname;
    }

    public int getMaxResultLimit() {
        return maxResultLimit;
    }

    public String getUname() {
        return uname;
    }

    public boolean isPrefixLogs() {
	    return prefixLogs;
    }

    public String getToken() {
        return token;
    }
}