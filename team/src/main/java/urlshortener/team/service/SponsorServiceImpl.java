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
            "</head>\n" +
            "<body>\n" +
            "<div style=\"width:100%;height:50px;position:fixed;top:0px;\n" +
            "        background-color:#ff3399;z-index:99999;\">\n" +
            "    <span id=\"sponsor-skip-button\">Skip</span>\n" +
            "    <span id=\"sponsor-go-button\" style=\"display:none;\">Go</span>\n" +
            "    <span id=\"sponsor-countdown\" style=\"display:none;\">Wait 5 seconds...</span>\n" +
            "    <div id=\"token\" style=\"display:none;\">${token}</div>\n" +
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
