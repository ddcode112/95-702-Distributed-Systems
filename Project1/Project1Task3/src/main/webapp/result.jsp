<%--The result page shows the answer history. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Distributed Systems Class Clicker</title>
</head>
<body>
<h1><%= "Distributed Systems Class Clicker" %></h1>
    <%-- Check if no answers. --%>
    <% if (request.getAttribute("sum").equals(0)) { %>
        <p>There are currently no results</p>
    <% } else { %>
        <p>The results from the survey are as follows</p><br>
        <p>A: <%= request.getAttribute("totalA") %></p>
        <p>B: <%= request.getAttribute("totalB") %></p>
        <p>C: <%= request.getAttribute("totalC") %></p>
        <p>D: <%= request.getAttribute("totalD") %></p>
    <% } %>
</body>
</html>
