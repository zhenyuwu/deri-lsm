package deri.sensor.components.Map;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeProvider;
/**
 * @author ZK support forum
 */
public class MapThemeProvider implements ThemeProvider {
    @Override
	public Collection getThemeURIs(Execution exec, List uris) {
        if ("silvergray".equals(getSkinCookie(exec))) {
            uris.add("~./silvergray/color.css.dsp");
            uris.add("~./silvergray/img.css.dsp");
        }
        return uris;
    }
    @Override
	public int getWCSCacheControl(Execution exec, String uri) {
        return -1;
    }
    @Override
	public String beforeWCS(Execution exec, String uri) {
        final String fsc = getFontSizeCookie(exec);
        if ("lg".equals(fsc)) {
            exec.setAttribute("fontSizeM", "15px");
            exec.setAttribute("fontSizeMS", "13px");
            exec.setAttribute("fontSizeS", "13px");
            exec.setAttribute("fontSizeXS", "12px");
        } else if ("sm".equals(fsc)) {
            exec.setAttribute("fontSizeM", "10px");
            exec.setAttribute("fontSizeMS", "9px");
            exec.setAttribute("fontSizeS", "9px");
            exec.setAttribute("fontSizeXS", "8px");
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
