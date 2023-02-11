package ds.project1task2;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 10, 2023
 *
 * This model includes data from 6 sources.
 * Country: from file countries including 29 different countries.
 * Nickname of football teams: scraping from www.topendsports.com.
 * Capital: scraping from restcountries.com.
 * Top Scorer: scraping from www.espn.com.
 * Flag: scraping from www.cia.gov.
 * Emoji: API from cdn.jsdelivr.net.
 */

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.*;

public class WorldCupModel {
    /*
    Path of data sources.
     */
    private final String nickNameSource = "www.topendsports.com/sport/soccer/team-nicknames-women.htm";
    private final String capitalBaseSource = "restcountries.com";
    private final String topScorerSource = "www.espn.com/soccer/stats/_/league/FIFA.WWC/season/2019/view/scoring";
    private final String flagSource = "www.cia.gov/the-world-factbook/countries/";
    /*
    k - v -> Country Name - Nickname Mapping
     */
    private Map<String, String> countryNicknameMap;
    /*
    k - v -> Country Name - Capital Mapping
     */
    private Map<String, String> countryCapitalMap;
    /*
    List of country names
     */
    private ArrayList<String> countryList;
    /*
    List of emoji object.
     */
    private ArrayList<Emoji> countryEmojiList;
    /*
    k - v -> Country Name - Country Code Mapping
     */
    private Map<String, String> countryCodeMap;

    /**
     * Constructor: load data of country list, nickname map, capital map, and emoji list.
     * @throws IOException
     */
    WorldCupModel() throws IOException {
        setCountryList();
        setCountryNicknameMap();
        setCountryCapitalMap();
        setCountryEmojiList();
    }

    /**
     * Emoji class
     * with the country name, emoji, unicode, and emoji svg path.
     */
    private static class Emoji {
        private String name;
        private String emoji;
        private String unicode;
        private String image;

        /**
         * Non-arg constructor.
         */
        Emoji () {

        }

        /**
         * Constructor with arguments.
         * @param n country name
         * @param e emoji
         * @param u unicode
         * @param i image svg link
         */
        Emoji (String n, String e, String u, String i) {
            super();
            this.name = n;
            this.emoji = e;
            this.unicode = u;
            this.image = i;
        }

        /**
         * Get the name of the emoji object.
         * @return country name of the emoji
         */
        public String getName() {
            return name;
        }

        /**
         * Get the emoji of the emoji object.
         * @return emoji of the emoji object
         */
        public String getEmoji() {
            return emoji;
        }

        /**
         * Get the unicode of the emoji object.
         * @return unicode of the emoji object
         */
        public String getUnicode() {
            return unicode;
        }

        /**
         * Get the image of the emoji object.
         * @return image svg link of the emoji object
         */
        public String getImage() {
            return image;
        }
    }

    /**
     * Get the web scraping source of nicknames.
     * @return source url of nicknames
     */
    public String getNickNameSource() {
        return nickNameSource;
    }

    /**
     * Get the web scraping source of capitals.
     * @return source url of capitals
     */
    public String getCapitalBaseSource() {
        return capitalBaseSource;
    }

    /**
     * Get the country list for this app.
     * All other data will be limited to the countries in this list.
     * @return list of countries
     */
    public ArrayList<String> getCountryList() {
        return countryList;
    }

    /**
     * Get the web scraping source of top scorers.
     * @return source url of top scores
     */
    public String getTopScorerSource() { return topScorerSource;}

    /**
     * Get the web scraping source of flags.
     * @return source url of flags
     */
    public String getFlagSource() {return flagSource;}

    /**
     * Set up country-nickname mapping by web scraping.
     * @throws IOException
     */
    private void setCountryNicknameMap() throws IOException {
        countryNicknameMap = new HashMap<>();
        Document nickNameList = Jsoup.connect("https://" + nickNameSource).validateTLSCertificates(false).get();
        // Get the table of country-nickname data.
        Element table = nickNameList.select("table").get(0);
        // Get rows in the table.
        Elements rows = table.select("tr");
        // Extract country names and nicknames and put in the map.
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            if (countryList.contains(cols.get(0).text().trim())) {
                countryNicknameMap.put(cols.get(0).text().trim(), cols.get(1).text().trim());
            }
        }
    }

    /**
     * Set up country list from file countries.
     * @throws IOException
     */
    private void setCountryList() throws IOException {
        String country_file_path = "/countries";
        InputStream is = WorldCupModel.class.getResourceAsStream(country_file_path);
        BufferedReader reader = null;
        if (is != null) {
            reader = new BufferedReader(new InputStreamReader(is));
        }
        String line;
        countryList = new ArrayList<>();
        if (reader != null) {
            while ((line = reader.readLine()) != null) {
                countryList.add(line);
            }
        }

    }

    /**
     * Return nickname of the football team, not found if it's not in the source.
     * @param searchKey country name passed by the browser
     * @return nickname of searchKey
     */
    public String searchNickname(String searchKey) {
        return countryNicknameMap.getOrDefault(searchKey, "Not Found");
    }

    /**
     * Set up country-capital mapping by web scraping.
     * @throws IOException
     */
    private void setCountryCapitalMap() throws IOException {
        String capitalSource = capitalBaseSource + "/v3.1/all";
        String jsonStr= Jsoup.connect("https://" + capitalSource).ignoreContentType(true).execute().body();
        // Regex pattern to capture groups (country name) (country code) (capital)
        // Extract country code for further emoji mapping.
        String patternStr = "\\{\"name\":\\{\"common\":\"(.*?)\".+?(?<=\"cca2\":\")(\\w+)\".+?(?<=\"capital\":\\[)(.*?)]";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(jsonStr);
        countryCapitalMap = new HashMap<>();
        countryCodeMap = new HashMap<>();
        while (matcher.find()) {
            String countryName = matcher.group(1);
            if (countryList.contains(countryName)) {
                String capital = matcher.group(3).replace("\"", "");
                countryCapitalMap.put(countryName, capital);
                countryCodeMap.put(countryName, matcher.group(2));
            } else if (countryName.equals("United Kingdom")) { // handle England
                String capital = matcher.group(3).replace("\"", "");
                countryCapitalMap.put(countryName, capital);
                countryCapitalMap.put("England", capital);
                countryCodeMap.put(countryName, matcher.group(2));
                countryCodeMap.put("England", matcher.group(2));
            }
        }
    }

    /**
     * Get capital of country.
     * @param searchKey country name
     * @return capital
     */
    public String getCapital(String searchKey) {
        return countryCapitalMap.getOrDefault(searchKey, "Not Found");
    }

    /**
     * Get top scorers by web scraping.
     * @param searchKey country name
     * @return name of the top scorer, total goals
     * @throws IOException
     */
    public String getTopScorer(String searchKey) throws IOException {
        Document topScorerList = Jsoup.connect("https://" + topScorerSource).validateTLSCertificates(false).get();
        String result = "N/A";
        // Get the table.
        Element table = topScorerList.select("table").get(0);
        // Get rows.
        Elements rows = table.select("tr");
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            if (cols.get(2).select("span > a.AnchorLink").text().trim().equals(searchKey)) {
                // Get name of the scorer.
                String scorer = cols.get(1).select("span > a.AnchorLink").text().trim();
                // Get total goals.
                String score = cols.get(4).select("span.tar").text().trim();
                result = scorer + ", " + score + " goals";
                break;
            }
        }
        return result;
    }

    /**
     * Get the flag image link by web scraping.
     * @param searchKey country name
     * @return image link of the flag
     * @throws IOException
     */
    public String getFlag(String searchKey) throws IOException {
        // Handle exceptions.
        if (searchKey.equals("England")) {
            searchKey = "United Kingdom";
        } else if (searchKey.equals("South Korea")) {
            searchKey = "Korea South";
        }
        searchKey = searchKey.replace(" ", "-").toLowerCase();
        Document flag = Jsoup.connect("https://" + flagSource + searchKey).validateTLSCertificates(false).get();
        Element infoBox = flag.select("div.col-md-6.mb30").get(0);
        Elements imge = infoBox.select("div.wfb-card-wrapper > div.row.no-gutters > div.col-md-3.align-self-center > div.wfb-card__image-container > div.gatsby-image-wrapper.gatsby-image-wrapper-constrained.wfb-card__image");
        Element image = imge.select("img").get(1);
        String result = image.attr("data-src");
        result = "https://www.cia.gov" + result;
        return result;
    }

    /**
     * Set up emoji list by calling API.
     * @throws IOException
     */
    private void setCountryEmojiList() throws IOException {
        String emojiSource = "https://cdn.jsdelivr.net/npm/country-flag-emoji-json@2.0.0/dist/by-code.json";
        String jsonStr= Jsoup.connect(emojiSource).ignoreContentType(true).validateTLSCertificates(false).execute().body();
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray names = jsonObject.names();
        countryEmojiList = new ArrayList<>();
        Gson gson = new Gson();
        Emoji emoji;
        for (int i = 0; i < names.length(); i++) {
            if (countryCodeMap.containsValue(names.getString(i))) {
                emoji = gson.fromJson(jsonObject.get(names.getString(i)).toString(), Emoji.class);
                if (names.getString(i).equals("GB")) {
                    Emoji eng = new Emoji("England", emoji.getEmoji(), emoji.getUnicode(), emoji.getImage());
                    countryEmojiList.add(eng);
                }
                countryEmojiList.add(emoji);
            }
        }
    }

    /**
     * Get emoji image link.
     * @param searchKey country name
     * @return emoji image link
     */
    public String getEmoji(String searchKey) {
        String result = "Not Found";
        for (Emoji emoji : countryEmojiList) {
            if (emoji.getName().equals(searchKey)) {
                result = emoji.getImage();
                break;
            }
        }
        return result;
    }

}
