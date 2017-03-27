package unidue.ub.bibliometrics;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
//import java.util.Properties;

//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.jdom2.Document;
import org.jdom2.Element;
import org.mycore.common.MCRMailer;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.userauth.User;
import unidue.ub.userauth.UserDAO;
import unidue.ub.userauth.UserRole;

@WebServlet("/analysis/exportReport")
public class BibliometricReportExportServlet extends MCRServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final static String resultsDir;

    private static final Logger LOGGER = Logger.getLogger(BibliometricReportExportServlet.class);

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        resultsDir = config.getString("ub.bibliometrics.resultsDir");
    }

    
    public void doGetPost(MCRServletJob job) throws Exception {
            LOGGER.info("preparing pdf report");
            Document reportXML = (Document) MCRSessionMgr.getCurrentSession().get("bibliometricReport");

            String authorName = (String) MCRSessionMgr.getCurrentSession().get("authorName");
            String hash = getHashedName(authorName);
            File reportFolder = new File(resultsDir + "/" + hash.substring(0, 6));
            if (!reportFolder.exists())
                reportFolder.mkdirs();
            File reportfile = new File(reportFolder, "report.xml");
            if (!reportfile.exists())
                reportfile.createNewFile();
            Element reportXMLToDisk = reportXML.clone().detachRootElement().clone();
            reportXMLToDisk.removeChild("navbar");
            new MCRJDOMContent(reportXMLToDisk.clone()).sendTo(reportfile);
            File latexFile = new File(reportFolder, "report.tex");
            File pubsPerTypeFile = new File(reportFolder, "pubsPerType.json");
            File pubsPerYearFile = new File(reportFolder, "pubsPerYear.json");
            
            generateFile(reportXMLToDisk.clone(),"xsl/bibliometricReport2latex.xsl",latexFile);
            generateFile(reportXMLToDisk.clone(),"xsl/pubsPerYear2JSON.xsl",pubsPerYearFile);
            generateFile(reportXMLToDisk.clone(),"xsl/pubsPerType2JSON.xsl",pubsPerTypeFile);
            
            // build the pdf by pdflatex
            ProcessBuilder pbPubsPerYear = new ProcessBuilder("cmd","/C","C:\\Users\\Eike\\AppData\\Roaming\\npm\\highcharts-export-server","-infile","pubsPerYear.json","-outfile","pubsPerYear.png");
            pbPubsPerYear.directory(reportFolder);
            pbPubsPerYear.start().waitFor();
            
            // build the pdf by pdflatex
            ProcessBuilder pbPubsPerType = new ProcessBuilder("cmd","/C","C:\\Users\\Eike\\AppData\\Roaming\\npm\\highcharts-export-server","-infile","pubsPerType.json","-outfile","pubsPerType.png");
            pbPubsPerType.directory(reportFolder);
            pbPubsPerType.start().waitFor();

            // build the pdf by pdflatex
            ProcessBuilder pbLatex = new ProcessBuilder("pdflatex","-interaction=nonstopmode","report.tex");
            pbLatex.directory(reportFolder);
            pbLatex.redirectOutput(new File(reportFolder, "shell.log"));
            pbLatex.start().waitFor();
            pbLatex.start().waitFor();
            LOGGER.info("running latex");

            String completePath = job.getRequest().getRequestURL().toString().replace("analysis/exportReport", "download/") + hash;
            String username = hash.substring(0, 6);
            String password = hash.substring(6);
            User user = UserDAO.getUser(username);
            if (user == null) {
                user = new User();
                user.setUsername(username);
                registrate(user, password, "client");
            }

            //String uriStr = String.format("mailto:%s?subject=%s&body=%s", "Eike.Spielberg@uni-due.de", "bibliometric report", completePath + "\n" + completePath.replace("download", "display"));
            //job.getResponse().sendRedirect(uriStr);
            sendEmail(completePath);
            job.getResponse().sendRedirect("start");
    }

    private String getHashedName(String authorName) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(authorName.getBytes());
        byte[] aMessageDigest = md.digest();
        String outEncoded = Base64.getEncoder().encodeToString(aMessageDigest).substring(0, 12);
        return (outEncoded);
    }

    private void registrate(User user, String plainTextPassword, String role) {
        generatePassword(user, plainTextPassword);
        UserRole userRole = new UserRole();
        userRole.setUsername(user.getUsername());
        userRole.setRoleName(role);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("userData");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(user);
        em.persist(userRole);
        tx.commit();
        em.close();
        LOGGER.info("created user " + user.getUsername() + " with role " + userRole.getRoleName());
    }

    private void generatePassword(User user, String plainTextPassword) {
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        Object salt = rng.nextBytes();
        String hashedPasswordBase64 = new Sha256Hash(plainTextPassword, salt, 1024).toBase64();
        user.setPassword(hashedPasswordBase64);
        user.setSalt(salt.toString());
    }

    private void sendEmail(String link) {
        Element mail = new Element("email");

        Element sender = new Element("from");
        sender.addContent("eike.spielberg@uni-due.de");
        mail.addContent(sender);

        Element recipients = new Element("to");
        String to = "Eike.Spielberg@uni-due.de";
        recipients.addContent(to);
        mail.addContent(recipients);
        //Element blindCarbonCopy = new Element("bcc");
        //blindCarbonCopy.addContent();
        //mail.addContent(blindCarbonCopy);

        Element subject = new Element("subject");
        subject.addContent("Bibliometrischer Report bereit");
        mail.addContent(subject);

        Element body = new Element("body");

        String mailContents = "Der Report kann unter folgender Adresse heruntergeladen werden: " + link + " \n";
        mailContents = mailContents + "und hier angesehen werden: " + link.replace("download", "display") +  " \n";

        String greeting = "\n" + "Vielen herzlichen Dank.\n" + "Ihre AG Forschung & Innovation";

        mailContents = mailContents + greeting;
        body.addContent(mailContents);
        mail.addContent(body);
        MCRMailer.send(mail);
    }
    
    private void generateFile(Element source, String pathToXSL, File outputFile) throws IOException, TransformerException{
    	StreamSource stylesource = new StreamSource(getClass().getClassLoader().getResourceAsStream(pathToXSL));
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(stylesource);
        if (!outputFile.exists())
            outputFile.createNewFile();
        Source in = (new MCRJDOMContent(source)).getSource();
        StreamResult out = new StreamResult(outputFile);
        transformer.transform(in, out);
    }
}