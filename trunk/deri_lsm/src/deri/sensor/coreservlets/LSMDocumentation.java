package deri.sensor.coreservlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class LSMDocumentation extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    //get the 'file' parameter
    String fileName = (String) request.getParameter("file");
    if (fileName == null || fileName.equals(""))
      throw new ServletException(
          "Invalid or non-existent file parameter in SendPdf servlet.");

    // add the .pdf suffix if it doesn't already exist
    if (fileName.indexOf(".pdf") == -1)
      fileName = fileName + ".pdf";

    String pdfDir = getServletContext().getInitParameter("pdf-dir");
    if (pdfDir == null || pdfDir.equals(""))
      throw new ServletException(
          "Invalid or non-existent 'pdf-Dir' context-param.");

    ServletOutputStream stream = null;
    BufferedInputStream buf = null;
    try {
      stream = response.getOutputStream();
      File pdf = new File(pdfDir + "/" + fileName);
      response.setContentType("application/pdf");

      response.addHeader("Content-Disposition", "attachment; filename="
          + fileName);
      response.setContentLength((int) pdf.length());
      FileInputStream input = new FileInputStream(pdf);
      buf = new BufferedInputStream(input);
      int readBytes = 0;

      while ((readBytes = buf.read()) != -1)
        stream.write(readBytes);
    } catch (IOException ioe) {
      throw new ServletException(ioe.getMessage());
    } finally {
      if (stream != null)
        stream.close();
      if (buf != null)
        buf.close();
    }
  }
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }
}