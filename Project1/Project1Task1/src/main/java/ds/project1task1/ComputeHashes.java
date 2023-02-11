package ds.project1task1;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 10, 2023
 * This program computes the requested cryptographic hash value
 * from the text transmitted by the browser.
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "ComputeHashes", value =  "/getHash")
public class ComputeHashes extends HttpServlet {
    /*
     * Starter message for the result.
     */
    private String message;

    /**
     * Initialize the servlet.
     */
    public void init() {
        message = "The hash value of ";
    }

    /**
     * Reply to HTTP GET method.
     * Receive request of text and the hash function from the browser,
     * and response with the computed hash values in hexadecimal and base 64 forms.
     * @param request http request
     * @param response http response
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        MessageDigest md = null;
        // Get the search and hash function parameters.
        String search = request.getParameter("searchWord");
        String hashFunc = request.getParameter("hash_func");
        try {
            // Compute hash values using the requested hash function.
            md = MessageDigest.getInstance(hashFunc);
            byte[] searchHash = md.digest(search.getBytes(StandardCharsets.UTF_8));
            String searchBase64 = jakarta.xml.bind.DatatypeConverter.printBase64Binary(searchHash);
            String searchHex = jakarta.xml.bind.DatatypeConverter.printHexBinary(searchHash);

            // Reply with the hash values.
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>" + message + search + " is (" + hashFunc + ")</h1>");
            out.println("<p> Hexadecimal: " + searchHex + "</p><br>");
            out.println("<p> Base64: " + searchBase64 + "</p><br>");
            out.println("<a href=\"index.jsp\">Continue</a>");
            out.println("</body></html>");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No " + hashFunc + " available " + e);
        }
    }

    public void destroy() {
    }
}