<%--The result page shows all the information about the country team. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%= request.getAttribute("doctype") %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Women's World Cup 2023</title>
</head>
<body>
<h1><%= "Country: " %><%= request.getParameter("country") %></h1>
<h2>Nickname: <%= request.getAttribute("nickname")%></h2>
<h4><%= request.getAttribute("nicknameSource")%></h4>
<h2>Capital City: <%= request.getAttribute("capital")%></h2>
<h4><%= request.getAttribute("capitalSource")%></h4>
<h2>Top Scorer in 2019: <%= request.getAttribute("topScorer")%></h2>
<h4><%= request.getAttribute("topScorerSource")%></h4>
<h2>Flag: </h2>
<img width="10%" src="<%= request.getAttribute("flag") %>">
<h4><%= request.getAttribute("flagSource")%></h4>
<h2>Flag Emoji: </h2>
<img width="10%" src="<%= request.getAttribute("flagEmoji") %>">
<h4><%= request.getAttribute("flagEmoji")%></h4><br>
<form action="getWorldCupInfo" method="GET">
    <input type="submit" value="Continue">
</form>
</body>
</html>
