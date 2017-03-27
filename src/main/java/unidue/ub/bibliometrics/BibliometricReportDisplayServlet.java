package unidue.ub.bibliometrics;

import java.io.File;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

@WebServlet("/display/*")
public class BibliometricReportDisplayServlet extends MCRServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final static String resultsDir;

    private static final Logger LOGGER = Logger.getLogger(BibliometricReportDisplayServlet.class);

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        resultsDir = config.getString("ub.bibliometrics.resultsDir");
    }

    public void doGetPost(MCRServletJob job) throws Exception {
        HttpServletRequest req = job.getRequest();
        String path = job.getRequest().getPathInfo().substring(1);
        LOGGER.info(path);
        boolean b = tryLogin(path.substring(0, 6), path.substring(6), false);
        if (b) {
            org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
            if (currentUser.hasRole("client")) {
                String hash = req.getUserPrincipal().getName();
                File reportFile = new File(resultsDir + "/" + hash.substring(0, 6), "report.xml");
                SAXBuilder builder = new SAXBuilder();
                Document document = builder.build(reportFile);
                getLayoutService().doLayout(job.getRequest(), job.getResponse(), new MCRJDOMContent(document));
                }
        }
    }

    public boolean tryLogin(String email, String password, Boolean rememberMe) {
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(email, password);
        token.setRememberMe(rememberMe);

        try {
            currentUser.login(token);
            System.out.println("User [" + currentUser.getPrincipal().toString() + "] logged in successfully.");
            // save username in the session
            currentUser.getSession().setAttribute("username", email);
            return true;
        } catch (UnknownAccountException uae) {
            System.out.println("There is no user with username of " + token.getPrincipal());
        } catch (IncorrectCredentialsException ice) {
            System.out.println("Password for account " + token.getPrincipal() + " was incorrect!");
        } catch (LockedAccountException lae) {
            System.out.println("The account for username " + token.getPrincipal() + " is locked.  " + "Please contact your administrator to unlock it.");
        }

        return false;
    }
}
