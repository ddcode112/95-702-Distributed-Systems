package ds.project1task2;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 10, 2023
 *
 * The servlet is acting as the controller.
 * There are two views - index.jsp and result.jsp.
 * It decides between the two by determining if there is a search parameter or not.
 * If there is no parameter, then it uses the index.jsp view, as a starting place.
 * If there is a search parameter, then it searches for the info of the country
 * and uses the result.jsp view.
 * The model is provided by WorldCupModel.
 */


import ds.project1task2.WorldCupModel;
import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "WorldCupServlet", urlPatterns = {"/getWorldCupInfo"})
public class WorldCupServlet extends HttpServlet {
    /*
     * The business model for this app.
     */
    WorldCupModel wcm = null;

    /**
     * Initiate this servlet by instantiating the model that it will use.
     */
    public void init() {
        try {
            wcm = new WorldCupModel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reply to HTTP GET requests via this doGet method.
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Pass the country list from model to view for the drop-down list
        ArrayList<String> countryList = wcm.getCountryList();
        request.setAttribute("countryList", countryList);
        // Get the search parameter if it exists
        String searchKey = request.getParameter("country");
        // Determine what type of device our user is.
        String ua = request.getHeader("User-Agent");

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
        String nextView;
        /*
         * Check if the search parameter is present.
         * If not, direct the user to the search page.
         * If there is a search parameter, then do the search and return the result.
         */
        if (searchKey != null) {
            // Pass the required attributes to the view.
            request.setAttribute("nickname", wcm.searchNickname(searchKey));
            request.setAttribute("nicknameSource", wcm.getNickNameSource());
            request.setAttribute("capital", wcm.getCapital(searchKey));
            request.setAttribute("capitalSource", wcm.getCapitalBaseSource());
            request.setAttribute("topScorer", wcm.getTopScorer(searchKey));
            request.setAttribute("topScorerSource", wcm.getTopScorerSource());
            request.setAttribute("flag", wcm.getFlag(searchKey));
            request.setAttribute("flagSource", wcm.getFlagSource());
            request.setAttribute("flagEmoji", wcm.getEmoji(searchKey));
            // Having search param, go to result.jsp.
            nextView = "result.jsp";
        } else {
            // Having no search param, go to index.jsp.
            nextView = "index.jsp";
        }
        // Transfer control over the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);

    }

    public void destroy() {
    }
}