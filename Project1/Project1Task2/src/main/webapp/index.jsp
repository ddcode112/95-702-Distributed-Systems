<%--The initial page for users to indicate the country they want to search for. --%>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%= request.getAttribute("doctype") %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Women's World Cup 2023</title>
</head>
<body>
<h1><%= "Women's World Cup 2023" %></h1>
<h4><%= "Created by Candice Chiang" %></h4>
<h2><%= "Participating Countries" %></h2>
<form action="getWorldCupInfo" method="GET">
    <label for="country">Choose a country:</label><br>
    <select name="country" id="country">
        <% ArrayList<String> countryList= (ArrayList<String>) request.getAttribute("countryList"); %>
        <% for (int i = 0; i < countryList.size(); i++) { %>
            <option value="<%= countryList.get(i) %>"><%= countryList.get(i) %></option>
        <% } %>
    </select>
    <br><br>
    <input type="submit" value="Submit">
</form>
</body>
</html>