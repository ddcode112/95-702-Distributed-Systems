<%--The starter page shows options to answer and the last recorded answer. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Distributed Systems Class Clicker</title>
</head>
<body>
<h1>Distributed Systems Class Clicker</h1>
<%-- Check if no answers, no previous recorded answer will be shown. --%>
<% if (request.getParameter("answer") != null) { %>
    <p>Your "<%= request.getParameter("answer") %>" has been registered</p>
<% } %>
<form action="submit" method="POST">
    <label for="letter">Submit your answer to the current question: </label><br>
    <input type="radio" id="optionA" name="answer" value="A">
    <label for="optionA">A</label><br>
    <input type="radio" id="optionB" name="answer" value="B">
    <label for="optionB">B</label><br>
    <input type="radio" id="optionC" name="answer" value="C">
    <label for="optionC">C</label><br>
    <input type="radio" id="optionD" name="answer" value="D">
    <label for="optionD">D</label><br><br>
    <input type="submit" value="Submit" />
</form>
</body>
</html>