package unidue.ub.bibliometrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

@WebServlet("/analysis/csvUpload")
@MultipartConfig
public class CSVUploadServlet extends MCRServlet {
    
    private final static String inputDir;

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        inputDir = config.getString("ub.statistics.inputDir");
    }
    
    private static final Logger LOGGER = Logger.getLogger(CSVUploadServlet.class);

    private static final long serialVersionUID = 1;

    public void doPost(MCRServletJob job) throws Exception {
        HttpServletRequest req = job.getRequest();
        Collection<Part> parts = req.getParts();
        File uploadDir = new File(inputDir + "/upload"); 
        if (!uploadDir.exists()) uploadDir.mkdirs();
        byte[] buffer = new byte[8 * 1024];
        for (Part part : parts) {
        File uploadedFile = new File(uploadDir,getFileName(part));
        
        InputStream input = part.getInputStream();
        try {
          OutputStream output = new FileOutputStream(uploadedFile);
          try {
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
              output.write(buffer, 0, bytesRead);
            }
          } finally {
            output.close();
          }
        } finally {
          input.close();
        }
        LOGGER.info("saved csv-file " + uploadedFile);
        job.getResponse().sendRedirect("getScopusAuthorData?listName="+ uploadedFile);
        }
        
    }
    
    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
