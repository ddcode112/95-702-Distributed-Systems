package ds.project1task3;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 10, 2023
 *
 * The servlet is acting as the controller.
 * There are two views - index.jsp and result.jsp.
 * It decides between the two by determining the path.
 * If the path is /getResults, it uses result.jsp.
 * If the path is others, it uses index.jsp view, which is also the starter view.
 * The model is provided by DSClickerModel.
 */

import java.io.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "DSClickerServlet", urlPatterns = {"/submit", "/getResults"})
public class DSClickerServlet extends HttpServlet {
    /*
     * The business model for this app.
     */
    DSClickerModel cm;
    /**
     * Initiate this servlet by instantiating the model that it will use.
     */
    public void init() {
        cm = new DSClickerModel();
    }

    /**
     * Reply to HTTP GET requests via this doGet method.
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException
     * @throws ServletException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Get the answer param if it exists.
        String answer = request.getParameter("answer");
        // Determine what type of device our user is.
        String ua = request.getHeader("User-Agent");
        // Get the path.
        String path = request.getServletPath();
        boolean mobile;
        // prepare the appropriate DOCTYPE for the view pages
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            mobile = true;
            /*
             * This is the latest XHTML Mobile doctype. To see the difference it
             * makes, comment it out so that a default desktop doctype is used
             * and view on an Android or iPhone.
             */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            mobile = false;
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }
        /*
         * Check if the path is /getResults.
         * If not, direct the user to the submit page.
         * If the path is /getResults, show the submission summary.
         */
        String nextView;
        if(path.equals("/getResults")) {
            // Get the recorded answers.
            int totalA = cm.getTotal("A");
            int totalB = cm.getTotal("B");
            int totalC = cm.getTotal("C");
            int totalD = cm.getTotal("D");
            int sum = totalA + totalB + totalC + totalD;
            // Pass the answer attributes to the view.
            request.setAttribute("totalA", totalA);
            request.setAttribute("totalB", totalB);
            request.setAttribute("totalC", totalC);
            request.setAttribute("totalD", totalD);
            request.setAttribute("sum", sum);
            // Go th resut.jsp.
            nextView = "result.jsp";
            // Reset the recorded answers.
            cm = new DSClickerModel();
        } else {
            // Record the answer.
            cm.addResult(answer);
            // Go to index.jsp.
            nextView = "index.jsp";
        }

        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }

    /**
     * Reply to HTTP POST requests via this doGet method.
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException
     * @throws ServletException
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
// Get the answer param if it exists.
        String answer = request.getParameter("answer");
        // Determine what type of device our user is.
        String ua = request.getHeader("User-Agent");
        // Get the path.
        String path = request.getServletPath();
        boolean mobile;
        // prepare the appropriate DOCTYPE for the view pages
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            mobile = true;
            /*
             * This is the latest XHTML Mobile doctype. To see the difference it
             * makes, comment it out so that a default desktop doctype is used
             * and view on an Android or iPhone.
             */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            mobile = false;
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }
        /*
         * Check if the path is /getResults.
         * If not, direct the user to the submit page.
         * If the path is /getResults, show the submission summary.
         */
        String nextView;
        if(path.equals("/getResults")) {
            // Get the recorded answers.
            int totalA = cm.getTotal("A");
            int totalB = cm.getTotal("B");
            int totalC = cm.getTotal("C");
            int totalD = cm.getTotal("D");
            int sum = totalA + totalB + totalC + totalD;
            // Pass the answer attributes to the view.
            request.setAttribute("totalA", totalA);
            request.setAttribute("totalB", totalB);
            request.setAttribute("totalC", totalC);
            request.setAttribute("totalD", totalD);
            request.setAttribute("sum", sum);
            // Go th resut.jsp.
            nextView = "result.jsp";
            // Reset the recorded answers.
            cm = new DSClickerModel();
        } else {
            // Record the answer.
            cm.addResult(answer);
            // Go to index.jsp.
            nextView = "index.jsp";
        }

        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }

    public void destroy() {
    }
}