<%-- @author Candice Chiang
     Andrew id: wantienc
     Last Modified: Feb 10 2023 --%>
<%-- The initial page for users to request the text and the hash function to compute--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Compute Hash</title>
</head>
<body>
<h1><%= "Compute Hash" %>
</h1>
<form action="getHash" method="GET">
    <label for="letter">Type the word.</label>
    <input type="text" name="searchWord" value="" /><br>
    <label for="letter">Select a hash function:</label><br>
    <input type="radio" id="md5" name="hash_func" value="MD5" checked>
    <label for="md5">MD5</label><br>
    <input type="radio" id="sha256" name="hash_func" value="SHA-256">
    <label for="sha256">SHA-256</label><br>
    <input type="submit" value="Submit">
</form>
</body>
</html>