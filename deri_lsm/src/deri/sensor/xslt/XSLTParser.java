package deri.sensor.xslt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import deri.sensor.wrapper.WebServiceURLRetriever;

public class XSLTParser {
    /**
     * Simple transformation method.
     * @param sourcePath - Absolute path to source xml file.
     * @param xsltPath - Absolute path to xslt file.
     * @param resultDir - Directory where you want to put resulting files.
     */
	
    public static String simpleTransform(String sourceURL, String xsltPath,
                                       String resultDir) {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        String txt="";
        try {
            Transformer transformer =
                tFactory.newTransformer(new StreamSource(new File(xsltPath)));
            transformer.setParameter("sensorId", "354635a4df5adf");
//            String xml = WebServiceURLRetriever.RetrieveFromURL(sourceURL);
//            InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            
            Writer outWriter = new StringWriter();  
            StreamResult result = new StreamResult( outWriter );
            
            transformer.transform(new StreamSource(new File(sourceURL)),result);
            
//            transformer.transform(new StreamSource(inputStream),result);
            txt = outWriter.toString();
            //outWriter.write(txt);
//            System.out.println(txt);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txt;
    }

    public static void main(String[] args) {
        //Set saxon as transformer.
        System.setProperty("javax.xml.transform.TransformerFactory",
                           "net.sf.saxon.TransformerFactoryImpl");

//        simpleTransform("http://api.wunderground.com/weatherstation/WXCurrentObXML.asp?ID=I07986B2",
//                        "xslt/WUnderground.xsl","xslt/result.txt");
        simpleTransform("xslt/wunderground.xml",
                "xslt/WUnderground.xsl","xslt/result.txt");

    }

}
