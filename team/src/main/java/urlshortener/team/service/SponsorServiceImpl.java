package urlshortener.team.service;

import urlshortener.team.domain.ShortURL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SponsorServiceImpl implements SponsorService {
    private static String htmlTemplate = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Redirecting...</title>\n" +
            "    <script src=\"https://code.jquery.com/jquery-3.3.1.min.js\" type=\"text/javascript\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js\"></script>\n" +
            "    <script src=\"js/sponsor.js\" type=\"text/javascript\"></script>\n" +
            "    <script src=\"js/stomp.js\"></script>\n" +
            "    <link href=\"webjars/bootstrap/3.3.5/css/bootstrap.min.css\" rel=\"stylesheet\"\n" +
            "          type=\"text/css\"/>\n" +
            "    <style>\n" +
            "        #sponsor-buttons a:hover{cursor:pointer;}\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<li id=\"sponsor-buttons\" style=\"display:none\">\n" +
            "    <a id=\"sponsor-skip-button\">Skip ad</a>\n" +
            "    <a id=\"sponsor-go-button\" style=\"display:none;\">Continue</a>\n" +
            "    <a id=\"sponsor-countdown\" style=\"display:none;\">Wait 5 seconds...</a>\n" +
            "    <div id=\"token\" style=\"display:none;\">${token}</div>\n" +
            "</li>\n" +
            "<div id =\"barraNavegacion\">\n" +
            "    <script>\n" +
            "        $(\"#barraNavegacion\").load(\"index.html #barraNavegacion\",function(){\n" +
            "            $(\"#barraNavegacion .navbar-nav\").empty();\n" +
            "            var sponsorButtons = $(\"#sponsor-buttons\").clone()[0];\n" +
            "            $(\"#sponsor-buttons\").remove();\n" +
            "            $(\"#barraNavegacion .navbar-nav\").append(sponsorButtons);\n" +
            "            $(\"#sponsor-skip-button\").click(function () {\n" +
            "                $(\"#sponsor-skip-button\").hide();\n" +
            "                $(\"#sponsor-countdown\").show();\n" +
            "                connect();\n" +
            "            });\n" +
            "            $(\"#sponsor-buttons\").show();\n" +
            "        });\n" +
            "    </script>\n" +
            "</div>\n" +
            "<div id=\"sponsor-body\" style=\"position:absolute;top:50px;\">${sponsorhtml}</div>\n" +
            "</body>\n" +
            "</html>";

    private static int id = 0;

    private static String responseToString(HttpURLConnection con) throws IOException {
        BufferedReader bfr = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String line = null;
        StringBuffer content = new StringBuffer();
        while ((line = bfr.readLine()) != null) {
            content.append(line);
        }
        bfr.close();
        return new String(content);
    }

    public String generateHtml(ShortURL l) {
        try {
            // https://www.baeldung.com/java-http-request
            URL urlSponsor = new URL(l.getSponsor());
            HttpURLConnection con = (HttpURLConnection) urlSponsor.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(6000);
            con.setConnectTimeout(6000);

            if (con.getResponseCode() != 200) {
                con.disconnect();
                return htmlTemplate.replace("${sponsorhtml}", "default");
            } else {
                String sponsorHtml = responseToString(con);
                con.disconnect();
                return htmlTemplate.replace("${sponsorhtml}", sponsorHtml)
                        .replace("${token}", Integer.toString(id++));
            }
        } catch (MalformedURLException e) {
            return htmlTemplate.replace("${sponsorhtml}", "default")
                    .replace("${token}", Integer.toString(id++));
        } catch (IOException e) {
            return htmlTemplate.replace("${sponsorhtml}", "default")
                    .replace("${token}", Integer.toString(id++));
        }
    }
}
