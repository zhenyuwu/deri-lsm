package deri.sensor.components.Map;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeProvider;

/**
 * @author ZK support forum
 */
public class CacheableThemeProvider implements ThemeProvider{
    private static String DEFAULT_WCS = "~./zul/css/zk.wcs";
 
    @Override
	public Collection getThemeURIs(Execution exec, List uris) {
        //font-size
        final String fsc = getFontSizeCookie(exec);
        if (fsc != null && fsc.length() > 0) {
            for (ListIterator it = uris.listIterator(); it.hasNext();) {
                final String uri = (String)it.next();
                if (uri.startsWith(DEFAULT_WCS)) {
                    it.set(Aide.injectURI(uri, fsc));
                    break;
                }
            }
        }
 
        //slivergray
        if ("silvergray".equals(getSkinCookie(exec))) {
            uris.add("~./silvergray/color.css.dsp");
            uris.add("~./silvergray/img.css.dsp");
        }
        return uris;
    }
 
    @Override
	public int getWCSCacheControl(Execution exec, String uri) {
        return 8760; //safe to cache
    }
    @Override
	public String beforeWCS(Execution exec, String uri) {
        final String[] dec = Aide.decodeURI(uri);
        if (dec != null) {
            if ("lg".equals(dec[1])) {
                exec.setAttribute("fontSizeM", "13px");
                exec.setAttribute("fontSizeMS", "11px");
                exec.setAttribute("fontSizeS", "11px");
                exec.setAttribute("fontSizeXS", "10px");
            } else if ("sm".equals(dec[1])) {
                exec.setAttribute("fontSizeM", "8px");
                exec.setAttribute("fontSizeMS", "7px");
                exec.setAttribute("fontSizeS", "7px");
                exec.setAttribute("fontSizeXS", "6px");
            }
            return dec[0];
        }
        return uri;
    }
 
    @Override
	public String beforeWidgetCSS(Execution exec, String uri) {
        return uri;
    }
 
    /** Returns the font size specified in cooke. */
    private static String getFontSizeCookie(Execution exec) {
        Cookie[] cookies = ((HttpServletRequest)exec.getNativeRequest()).getCookies();
        if (cookies!=null)
            for (int i=0; i<cookies.length; i++)
                if ("myfontsize".equals(cookies[i].getName()))
                    return cookies[i].getValue();
        return "";
    }
    /** Returns the skin specified in cookie. */
    private static String getSkinCookie(Execution exec) {
        Cookie[] cookies = ((HttpServletRequest)exec.getNativeRequest()).getCookies();
        if (cookies!=null)
            for (int i=0; i<cookies.length; i++)
                if ("myskin".equals(cookies[i].getName()))
                    return cookies[i].getValue();
        return "";
    }
}
