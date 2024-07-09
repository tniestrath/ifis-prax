package com.analysetool.services;

import com.analysetool.api.EventsController;
import com.analysetool.api.PostController;
import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.Constants;
import com.analysetool.util.DashConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private StatMailsRepository statMailsRepo;
    @Autowired
    private WPUserRepository userRepo;
    @Autowired
    private PostController postController;
    @Autowired
    private UserStatsRepository userStatsRepository;
    @Autowired
    private WPUserMetaRepository wpUserMetaRepository;
    @Autowired
    private UserViewsByHourDLCRepository userViewsRepo;
    @Autowired
    private UserRedirectsHourlyRepository userRedirectsRepo;
    @Autowired
    private RankingGroupContentRepository rankingGroupContentRepo;
    @Autowired
    private RankingGroupProfileRepository rankingGroupProfileRepo;
    @Autowired
    private RankingTotalContentRepository rankingTotalContentRepo;
    @Autowired
    private RankingTotalProfileRepository rankingTotalProfileRepo;
    @Autowired
    private PostClicksByHourDLCRepository postClicksRepo;
    @Autowired
    private NewsletterRepository newsletterRepo;
    @Autowired
    private universalStatsRepository uniRepo;
    @Autowired
    private PostStatsRepository statRepository;
    @Autowired
    private PostRepository postRepo;
    @Autowired
    private EventsRepository eventsRepo;
    @Autowired
    private EventsController eventsController;

    private final DashConfig config;

    private final String imageProfile = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAA3QAAAN0BcFOiBwAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAIASURBVFiFvdY/a5RBEMfxz5MLRIwRFRtRAioIgn8CsbKwM2KnFhEbCytfhJ2IlWCjIIgWCmqrBHwBFiKEWFgoGBSxSKGFaKLBxLG4S1juLnf73D2XgYFnH57Z33efnZndIiKUsaIoTuIiJnAc3zCHN7gbEYulJoyILMcW3MIqYgOfx6ncOSMiDwAjmO0gnPo/XK4a4Gam+Jr/wHglADiBlZIAgZc5AEMZaXIOtcyUSm2qKIqxbh/lAEz0IA6FepX0DXC0R4Cs2ByAhT4AusbmAMz1AdA1NgfgdY/iCxHxuetXGWVYU2+zZctwuspGdBi/S4g/q7QTNiCm8DVD/DG2Vw7QgNiBh1hqIzyP82XmiwhFY+K2VhTFfkziIGYi4l3jfQ2HJMdxRHxP4iZxGh8wGxFfSiUhduKe+smWrvIBdnf4Q3vwtClmBbcxlrUF6uf+2w57vIT7mMYRHMMlPMJyh7hXGM4BuNMlyfrxGx0BsA9/BwiwiF2djuOrGG6bLNXYVlxJXzQDnBmgeHuN5PeP6u3mU9Z/YajdFhzQ282nrI1i79ogBRjfBPEWrRRg2yYCrGulADl3g6psqOUB7zcR4OP6U1IFI/hj8FXwE7WWKoiIZVwf0IpTuxYRq+ujplY8jBc9rCrXn0h6wIYXElzADD5pPZLL+Kr6ReU5zrbT+g/lp72yFoh52gAAAABJRU5ErkJggg==";
    private final String imageContent = "iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAACXBIWXMAABFNAAARTQHAOWBjAAAB/0lEQVRIiWL4//8/xZiBgcGAgYHhAQMDA4gDwgcYGBgEwGb//88AAAAA//+ilgUfkCyA4Qtgi/7/ZwAAAAD//6KVBQiLGBgEAAAAAP//otSSCXgsgGAGhgMAAAAA//+ixAcToOwFeC1hYPgPAAAA//+iNIgWELSIgeE/AAAA//+iRhzgt4iB4QAAAAD//6JWJOOy6AMDA4MBAAAA//8i1wIQOwCaenBZBLbg////DAAAAAD//yLXApAYvqADpTqwBf///2cAAAAA//+ipgUwDE51cPz/PwMAAAD//6K2BfAgguP//xkAAAAA//+iuQX///9nAAAAAP//orkF////ZwAAAAD//6K5Bf///2cAAAAA//8ilA8aKLXg////DAAAAAD//0K2BFQHECrsSLbg////DAAAAAD//4JZkIBWPCNXQBRZ8P//fwYAAAAA//+CWYJcHCgQ8BVJFvz//58BAAAA//+CWQIL8wtI8QOyCN1HJFvw//9/BgAAAAD//wIZ6IAtt0J9hFw2kWXB////GQAAAAD//2KBFnQw8IGRkRFU7oDE5JHEP4Ic8///f5ClpAEGBgYAAAAA//8CYWTXYq8PGBgUyPEBGP//zwAAAAD//wJZgityQYkB5HqyDQfj//8ZAAAAAP//Qs4fIB+BMx81DIbj//8ZAAAAAP//AwARtZXpaOI3DgAAAABJRU5ErkJggg==";
    private final String imageDaily = "iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAJYSURBVEhLvZbNS1ZBFIevhRYq2BcE0sYKJALXFS2Lighatq+V0K7+CAP/CME/oIWgEAVtCzLcRRD0qdEHRZuorJ7nOkeO876YmvWDh/vemXPOnTlzZubtadbXEFwojMEh2A8f4BUswCzMwSfYlHbBTXgBvzaAdtrrtyEdh4cQAZ7ABJyB66XNp++22x+2+um/rk7De9DhDVyF3RA6Ad/LM2S/dtrrp79xusoRxAfug/mvtROOlGct7fWLD3XMyFxGijQcgK1Iv/iQ8daskYtmh1PuNoPNSP9InXFb7YGXYKO5rTUI9cxyunaUZ5ZxjGfVuQ2aK6XBKsmLrPM0OCqNb8MBOAiPwMrS/i5ol/ec7VF1xm+myotOWX2QS1km4WT5/RhcXH+7Oc1IlvHsm3K07mR1pzxD3+AcHIbI7VGI9Nj/FX6AZV0r4o3pEAt9DKz/nO+PMAyn2reVgI5OOXI3pDGiTelvHOOpNv5P0EgckfsgNA7RJy6oa/IltYnrFunS3zjRtwzNu/LiUZFnYo0/Bftc2IsQugT3wAOy/kjMJI4g4zfz5cWpZ+2Ft+BMz8MI5OpTo2AKl6BeeOMZd958Ohp1tjyzHJWl6XH+DKyuWtr0rvxco4i34EcMoC5DHqn3wwyYiuewCH4o6zWYNmN8tqHIOMZTbXx3ZNwb3Xa8u13627dORUlnXQPjre54dQNsdNTbeXYZd1X/5RRW//w+CdU3o7nNxWD9/9XNGHIED0AH8TS9BZbkttzxIXPpoRhV9ye0075jDdRW/nftAw/O/L+r3idJTfMbUfvNNr3flUgAAAAASUVORK5CYII=";
    private final String imageRedirect = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAALPSURBVFhH1ZfL6w1RHMCvZwhRFBbkLWEjr9hIoSjPlNfSwubHgqWNWPkDSMpCLBTlsfDIjrxKIiulEAsboizx+cycuc0998zv3hkkn/p0z8w553vOPXMeM63/nQPBxgwNv02ZEWzMsPBbh5m4AbfhJpyDU3AafsfP2Dd1RmA73sWbuA9H47fgKNyLN9Aylv1jzMd7+BRteAIWHA8WmGeZJ2gd6/4Wm/EdHsUh3og4EoyxrHWsa4xGWPE9rs2u0owMVmFdY9TuhENn7wdrXHp1QIxhrFqPw+fnEFYxCc/i86DpyViFsYzZF85gJ1zqmctYfIxncEHQtPfMS2EsY7p0e+IyciZXcQiv5ckOvHc4TybZj8buIN4H3GTcUFzrVSxH13uM95blySTmG9s22sQdMPgb/JJdpfmEs/NkB7PQvCqMaWzbaBN3YB6+yJOVnMMtuCK7yjG9Fc0bjJc4N0/mODnsxB6ciLvQnt5B9/RL+APLOIxXcCXe9gZ4Nrj7OYE/eKNEOf56dLe8jFl8M9XnshAt5HIy7b14hHbgA7Sx3XghaPoR3sedWKYc39h2oCp+6xiezJNdLMK3uDq7SmOeZRZnV90Y2zbaxD14jUvyZBcuv/PoCFRhnmUsm8LYtlGJM/kVlk+8Aod3TZ4cFEfBsjHGNLZttIlHwGXyEePDo5isvnD0wjKWjXdSYxrbNtp0TQI4jQMYBxiO03Fq+E1Z5I3AMsbysRi7g7iRAg+OW3gqu8q5iEvRf1hV7yeOwWfo0ivwMNqI67KrPkgdx/6r8ZHjgvH98gg0Oo6lnxeSXli30QtJgRXtvUOYGvaq7wLLWse6jRsvcOiKl1KP1PISPREsMM+j3LJ9vZRWTaYU7vMH0bPApeShtQrlIbrJuMZdas72q9iTOh0ocA/3SPVU81SU6+gO5xnRsc7/NvF3QW1SG1Edvgb/GX6SaUNarV8yHYTxxkCURwAAAABJRU5ErkJggg==";

    public UserService(DashConfig config) {
        this.config = config;
    }


    private final String tableBase = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html charset=UTF-8\" />\n" +
            "    <title>Analyse ihrer Marktplatz Präsens</title>\n" +
            "    <style type=\"text/css\">\n" +
            "        .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td {line-height: 100%;}\n" +
            "        .ExternalClass {width: 100%;}\n" +
            "    </style>\n" +
            "</head>\n" +
            "    <body style=\"width: 100%; margin: 5px 0; background: #cccccc;\">\n" +
            "        <table style=\"width: 100%\">\n" +
            "            <tr style=\"width: 100%\">\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                <td id=\"quarterStats\" class=\"chapter\" style=\"background: white;\">\n" +
            "                    <p class=\"chapter-title\" style=\"text-align: center;font-size: x-large;width: 100%;margin:0;\">Ihr Zuwachs im letzten Quartal:</p><br>\n" +
            "                    <table id=\"quarterStatsList\" >\n" +
            "                        <tr id=\"qs-1\">\n" +
            "                            <td>Profilaufrufe: {{PROFILEVIEWSQUARTER}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAANwAAADcAaIUQOMAAAGaSURBVEhLrda/K0VhHMfx68cgigih/JgMEkosshqU8icwGm0Wm1gYDP4AdoMio0FRioFBlJKFQX4TIt6fe3447v2e0znn3k+9ep7cc5/vved8n+fKxMwI1nCOe+xjAe0oOBWYwzN+DKcYRUGZhrV40DW6kSpduIO1cK5NpEqcb+F5QyPMlLqjlV53jJMy9DnT/EQVKXHHONG1oddHFTlxxzj5xrEzTZYexH3w20idGViLBt2gH6lTjiW8wypwiXFERl0RlkpUYQOHqIZa9QEX2MIkDlAPFf1CXqyOqMUEhtz5DlagM0uF9LcraNEmTGEYt9jFKp4QmhroEwZviRxBz0f3vgWDmIU6KvfadegOhGYZuW8KesWjO1qve+ZhphP6ytabktKh2YZsgptRXVLnTAtOA8ac6f8iut9JjpKoqGsHnOlfET2o0FM0ZZqR3SJeEXVVZEekiNbTun4R7WwpZvw1vSLqKv2OFzMv0AHrF9FxoQ1XzOgoyjtmWqEXrL5Pag86csx0YBFn+IS1QJgP6F8k7XZ9YDeZzC9m57j6lJJIgQAAAABJRU5ErkJggg==\"/></td>\n" +
            "                        </tr>\n" +
            "                        <tr id=\"qs-2\">\n" +
            "                            <td style=\"padding-left: 4em;\">Weiterleitungen zur eigenen Homepage: {{REDIRECTSQUARTER}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAALPSURBVFhH1ZfL6w1RHMCvZwhRFBbkLWEjr9hIoSjPlNfSwubHgqWNWPkDSMpCLBTlsfDIjrxKIiulEAsboizx+cycuc0998zv3hkkn/p0z8w553vOPXMeM63/nQPBxgwNv02ZEWzMsPBbh5m4AbfhJpyDU3AafsfP2Dd1RmA73sWbuA9H47fgKNyLN9Aylv1jzMd7+BRteAIWHA8WmGeZJ2gd6/4Wm/EdHsUh3og4EoyxrHWsa4xGWPE9rs2u0owMVmFdY9TuhENn7wdrXHp1QIxhrFqPw+fnEFYxCc/i86DpyViFsYzZF85gJ1zqmctYfIxncEHQtPfMS2EsY7p0e+IyciZXcQiv5ckOvHc4TybZj8buIN4H3GTcUFzrVSxH13uM95blySTmG9s22sQdMPgb/JJdpfmEs/NkB7PQvCqMaWzbaBN3YB6+yJOVnMMtuCK7yjG9Fc0bjJc4N0/mODnsxB6ciLvQnt5B9/RL+APLOIxXcCXe9gZ4Nrj7OYE/eKNEOf56dLe8jFl8M9XnshAt5HIy7b14hHbgA7Sx3XghaPoR3sedWKYc39h2oCp+6xiezJNdLMK3uDq7SmOeZRZnV90Y2zbaxD14jUvyZBcuv/PoCFRhnmUsm8LYtlGJM/kVlk+8Aod3TZ4cFEfBsjHGNLZttIlHwGXyEePDo5isvnD0wjKWjXdSYxrbNtp0TQI4jQMYBxiO03Fq+E1Z5I3AMsbysRi7g7iRAg+OW3gqu8q5iEvRf1hV7yeOwWfo0ivwMNqI67KrPkgdx/6r8ZHjgvH98gg0Oo6lnxeSXli30QtJgRXtvUOYGvaq7wLLWse6jRsvcOiKl1KP1PISPREsMM+j3LJ9vZRWTaYU7vMH0bPApeShtQrlIbrJuMZdas72q9iTOh0ocA/3SPVU81SU6+gO5xnRsc7/NvF3QW1SG1Edvgb/GX6SaUNarV8yHYTxxkCURwAAAABJRU5ErkJggg==\"/></td>\n" +
            "                        </tr>\n" +
            "                        <tr id=\"qs-3\">\n" +
            "                            <td>Inhaltsaufrufe: {{CONTENTVIEWSQUARTER}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAACXBIWXMAABFNAAARTQHAOWBjAAAB/0lEQVRIiWL4//8/xZiBgcGAgYHhAQMDA4gDwgcYGBgEwGb//88AAAAA//+ilgUfkCyA4Qtgi/7/ZwAAAAD//6KVBQiLGBgEAAAAAP//otSSCXgsgGAGhgMAAAAA//+ixAcToOwFeC1hYPgPAAAA//+iNIgWELSIgeE/AAAA//+iRhzgt4iB4QAAAAD//6JWJOOy6AMDA4MBAAAA//8i1wIQOwCaenBZBLbg////DAAAAAD//yLXApAYvqADpTqwBf///2cAAAAA//+ipgUwDE51cPz/PwMAAAD//6K2BfAgguP//xkAAAAA//+iuQX///9nAAAAAP//orkF////ZwAAAAD//6K5Bf///2cAAAAA//8ilA8aKLXg////DAAAAAD//0K2BFQHECrsSLbg////DAAAAAD//4JZkIBWPCNXQBRZ8P//fwYAAAAA//+CWYJcHCgQ8BVJFvz//58BAAAA//+CWQIL8wtI8QOyCN1HJFvw//9/BgAAAAD//wIZ6IAtt0J9hFw2kWXB////GQAAAAD//2KBFnQw8IGRkRFU7oDE5JHEP4Ic8///f5ClpAEGBgYAAAAA//8CYWTXYq8PGBgUyPEBGP//zwAAAAD//wJZgityQYkB5HqyDQfj//8ZAAAAAP//Qs4fIB+BMx81DIbj//8ZAAAAAP//AwARtZXpaOI3DgAAAABJRU5ErkJggg==\"/></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                </td>\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"width: 100%\">\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                <td id=\"baseStats\" class=\"chapter\" style=\"background: white;\">\n" +
            "                    <p class=\"chapter-title\" style=\"text-align: center;font-size: x-large;width: 100%;margin:0;\">Ihre Gesamtübersicht:</p><br>\n" +
            "                    <table id=\"baseStatsList\">\n" +
            "                        <tr id=\"bs-1\">\n" +
            "                            <td>Profilaufrufe: {{PROFILEVIEWS}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAANwAAADcAaIUQOMAAAGaSURBVEhLrda/K0VhHMfx68cgigih/JgMEkosshqU8icwGm0Wm1gYDP4AdoMio0FRioFBlJKFQX4TIt6fe3447v2e0znn3k+9ep7cc5/vved8n+fKxMwI1nCOe+xjAe0oOBWYwzN+DKcYRUGZhrV40DW6kSpduIO1cK5NpEqcb+F5QyPMlLqjlV53jJMy9DnT/EQVKXHHONG1oddHFTlxxzj5xrEzTZYexH3w20idGViLBt2gH6lTjiW8wypwiXFERl0RlkpUYQOHqIZa9QEX2MIkDlAPFf1CXqyOqMUEhtz5DlagM0uF9LcraNEmTGEYt9jFKp4QmhroEwZviRxBz0f3vgWDmIU6KvfadegOhGYZuW8KesWjO1qve+ZhphP6ytabktKh2YZsgptRXVLnTAtOA8ac6f8iut9JjpKoqGsHnOlfET2o0FM0ZZqR3SJeEXVVZEekiNbTun4R7WwpZvw1vSLqKv2OFzMv0AHrF9FxoQ1XzOgoyjtmWqEXrL5Pag86csx0YBFn+IS1QJgP6F8k7XZ9YDeZzC9m57j6lJJIgQAAAABJRU5ErkJggg==\"/></td>\n" +
            "                        </tr>\n" +
            "                        <tr>\n" +
            "                            <td style=\"padding-left: 4em;\" id=\"bs-1-1\">Tägliche Aufrufe: {{DAILYVIEWS}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAJYSURBVEhLvZbNS1ZBFIevhRYq2BcE0sYKJALXFS2Lighatq+V0K7+CAP/CME/oIWgEAVtCzLcRRD0qdEHRZuorJ7nOkeO876YmvWDh/vemXPOnTlzZubtadbXEFwojMEh2A8f4BUswCzMwSfYlHbBTXgBvzaAdtrrtyEdh4cQAZ7ABJyB66XNp++22x+2+um/rk7De9DhDVyF3RA6Ad/LM2S/dtrrp79xusoRxAfug/mvtROOlGct7fWLD3XMyFxGijQcgK1Iv/iQ8daskYtmh1PuNoPNSP9InXFb7YGXYKO5rTUI9cxyunaUZ5ZxjGfVuQ2aK6XBKsmLrPM0OCqNb8MBOAiPwMrS/i5ol/ec7VF1xm+myotOWX2QS1km4WT5/RhcXH+7Oc1IlvHsm3K07mR1pzxD3+AcHIbI7VGI9Nj/FX6AZV0r4o3pEAt9DKz/nO+PMAyn2reVgI5OOXI3pDGiTelvHOOpNv5P0EgckfsgNA7RJy6oa/IltYnrFunS3zjRtwzNu/LiUZFnYo0/Bftc2IsQugT3wAOy/kjMJI4g4zfz5cWpZ+2Ft+BMz8MI5OpTo2AKl6BeeOMZd958Ohp1tjyzHJWl6XH+DKyuWtr0rvxco4i34EcMoC5DHqn3wwyYiuewCH4o6zWYNmN8tqHIOMZTbXx3ZNwb3Xa8u13627dORUlnXQPjre54dQNsdNTbeXYZd1X/5RRW//w+CdU3o7nNxWD9/9XNGHIED0AH8TS9BZbkttzxIXPpoRhV9ye0075jDdRW/nftAw/O/L+r3idJTfMbUfvNNr3flUgAAAAASUVORK5CYII=\"/></td>\n" +
            "                        </tr>\n" +
            "                        <tr>\n" +
            "                            <td style=\"padding-left: 4em;\" id=\"bs-1-2\">Weiterleitungen zur eigenen Homepage: {{REDIRECTS}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAALPSURBVFhH1ZfL6w1RHMCvZwhRFBbkLWEjr9hIoSjPlNfSwubHgqWNWPkDSMpCLBTlsfDIjrxKIiulEAsboizx+cycuc0998zv3hkkn/p0z8w553vOPXMeM63/nQPBxgwNv02ZEWzMsPBbh5m4AbfhJpyDU3AafsfP2Dd1RmA73sWbuA9H47fgKNyLN9Aylv1jzMd7+BRteAIWHA8WmGeZJ2gd6/4Wm/EdHsUh3og4EoyxrHWsa4xGWPE9rs2u0owMVmFdY9TuhENn7wdrXHp1QIxhrFqPw+fnEFYxCc/i86DpyViFsYzZF85gJ1zqmctYfIxncEHQtPfMS2EsY7p0e+IyciZXcQiv5ckOvHc4TybZj8buIN4H3GTcUFzrVSxH13uM95blySTmG9s22sQdMPgb/JJdpfmEs/NkB7PQvCqMaWzbaBN3YB6+yJOVnMMtuCK7yjG9Fc0bjJc4N0/mODnsxB6ciLvQnt5B9/RL+APLOIxXcCXe9gZ4Nrj7OYE/eKNEOf56dLe8jFl8M9XnshAt5HIy7b14hHbgA7Sx3XghaPoR3sedWKYc39h2oCp+6xiezJNdLMK3uDq7SmOeZRZnV90Y2zbaxD14jUvyZBcuv/PoCFRhnmUsm8LYtlGJM/kVlk+8Aod3TZ4cFEfBsjHGNLZttIlHwGXyEePDo5isvnD0wjKWjXdSYxrbNtp0TQI4jQMYBxiO03Fq+E1Z5I3AMsbysRi7g7iRAg+OW3gqu8q5iEvRf1hV7yeOwWfo0ivwMNqI67KrPkgdx/6r8ZHjgvH98gg0Oo6lnxeSXli30QtJgRXtvUOYGvaq7wLLWse6jRsvcOiKl1KP1PISPREsMM+j3LJ9vZRWTaYU7vMH0bPApeShtQrlIbrJuMZdas72q9iTOh0ocA/3SPVU81SU6+gO5xnRsc7/NvF3QW1SG1Edvgb/GX6SaUNarV8yHYTxxkCURwAAAABJRU5ErkJggg==\"/></td>\n" +
            "                        </tr>\n" +
            "\n" +
            "                        <tr id=\"bs-2\">\n" +
            "                            <td>Inhaltsaufrufe: {{CONTENTVIEWS}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAACXBIWXMAABFNAAARTQHAOWBjAAAB/0lEQVRIiWL4//8/xZiBgcGAgYHhAQMDA4gDwgcYGBgEwGb//88AAAAA//+ilgUfkCyA4Qtgi/7/ZwAAAAD//6KVBQiLGBgEAAAAAP//otSSCXgsgGAGhgMAAAAA//+ixAcToOwFeC1hYPgPAAAA//+iNIgWELSIgeE/AAAA//+iRhzgt4iB4QAAAAD//6JWJOOy6AMDA4MBAAAA//8i1wIQOwCaenBZBLbg////DAAAAAD//yLXApAYvqADpTqwBf///2cAAAAA//+ipgUwDE51cPz/PwMAAAD//6K2BfAgguP//xkAAAAA//+iuQX///9nAAAAAP//orkF////ZwAAAAD//6K5Bf///2cAAAAA//8ilA8aKLXg////DAAAAAD//0K2BFQHECrsSLbg////DAAAAAD//4JZkIBWPCNXQBRZ8P//fwYAAAAA//+CWYJcHCgQ8BVJFvz//58BAAAA//+CWQIL8wtI8QOyCN1HJFvw//9/BgAAAAD//wIZ6IAtt0J9hFw2kWXB////GQAAAAD//2KBFnQw8IGRkRFU7oDE5JHEP4Ic8///f5ClpAEGBgYAAAAA//8CYWTXYq8PGBgUyPEBGP//zwAAAAD//wJZgityQYkB5HqyDQfj//8ZAAAAAP//Qs4fIB+BMx81DIbj//8ZAAAAAP//AwARtZXpaOI3DgAAAABJRU5ErkJggg==\"/></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                </td>\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"width: 100%\">\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                <td id=\"rankings\" class=\"chapter\" style=\"background: white;\">\n" +
            "                    <table id=\"rankingsList\">\n" +
            "                        <tr id=\"rs-1\">\n" +
            "                            <td>Ihre Platzierung nach Profilaufrufen: #{{PROFILERANK}}</td>\n" +
            "                        </tr>\n" +
            "                        <tr id=\"rs-2\">\n" +
            "                            <td>Innerhalb des gleichen Packets: #{{GROUPPROFILERANK}}</td>\n" +
            "                        </tr>\n" +
            "                        <tr><td style=\"height: 1em\"></td></tr>\n" +
            "                        <tr id=\"rs-3\">\n" +
            "                            <td>Platzierung nach Inhaltsaufrufen: #{{CONTENTRANK}}</td>\n" +
            "                        </tr>\n" +
            "                        <tr id=\"rs-4\">\n" +
            "                            <td>Innerhalb des gleichen Packets: #{{GROUPCONTENTRANK}}</td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                    <table style=\"width: 100%\">\n" +
            "                        <tr>\n" +
            "                            <td class=\"spacer\" style=\"width: 5%\"></td>\n" +
            "                            <td>\n" +
            "                                <table id=\"rankingsTable\" style=\"border-collapse: collapse;width: 100%\">\n" +
            "                                    <thead>\n" +
            "                                    <tr>\n" +
            "                                        <th>Gewählte Themen</th>\n" +
            "                                        <th>Ihre Platzierung</th>\n" +
            "                                        <th>Globale Nutzungshäufigkeit</th>\n" +
            "                                    </tr>\n" +
            "                                    </thead>\n" +
            "                                    <tbody>\n" +
            "                                    <!--<tr><td class=\"rankings-1\">-</td><td class=\"rankings-2\" style=\"text-align: center;\">-</td><td class=\"rankings-3\" style=\"text-align: center;\">-</td></tr> -->\n" +
            "                                    {{TABLEROW}}\n" +
            "                                    </tbody>\n" +
            "                                </table>\n" +
            "                            </td>\n" +
            "                            <td class=\"spacer\" style=\"width: 5%\"></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                </td>\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "    </body>\n" +
            "</html>";

    private final String tablerowBase = "<tr><td class='rankings-1'>REPLACE1</td><td style=\"text-align:center;\" class='rankings-2'>REPLACE2</td><td style=\"text-align:center;\" class='rankings-3'>REPLACE3</td></tr>";

    private final String tablerowCSS = "border-bottom: 1px solid #ddd;";


    @Scheduled(cron = "0 0 0 1 */3 ?")
    public void generateMails() throws JSONException {
        generateMailsPlus();
        generateMailsPremium();
    }

    private void generateMailsPremium() {
        for(WPUser user : userRepo.getByAboType("premium")) {

        }
    }

    private void generateMailsPlus() throws JSONException {
        for(WPUser user : userRepo.getByAboType("plus")) {
            generateMailSingle(Math.toIntExact(user.getId()));
        }
    }

    public void generateMailSingle(int userId) throws JSONException {
        WPUser user = userRepo.findById((long) userId).get();
        JSONObject obj = new JSONObject(getAllSingleUser(user.getId()));
        StatMails statMail;
        if(statMailsRepo.findByUserId(user.getId()) == null) {
            statMail = new StatMails();
            statMail.setUserId(Math.toIntExact(user.getId()));
        } else {
            statMail = statMailsRepo.findByUserId(user.getId());
        }

        String profileViewsQuarter;
        if(userViewsRepo.getSumForUserThisQuarter(userId) != 0 && userViewsRepo.getSumForUserPreviousQuarter(userId) != 0) {
            profileViewsQuarter = (userViewsRepo.getSumForUserThisQuarter(userId) - userViewsRepo.getSumForUserPreviousQuarter(userId) > 0 ? "+" : "") + (userViewsRepo.getSumForUserThisQuarter(userId) - userViewsRepo.getSumForUserPreviousQuarter(userId)) + "(" + userViewsRepo.getSumForUserThisQuarter(userId) + " - " + userViewsRepo.getSumForUserPreviousQuarter(userId) + ")";
        } else {
            profileViewsQuarter = "0";
        }

        String redirectsQuarter;
        if(userRedirectsRepo.getSumForUserThisQuarter(userId) != 0 && userRedirectsRepo.getSumForUserPreviousQuarter(userId) != 0) {
            redirectsQuarter = (userRedirectsRepo.getSumForUserThisQuarter(userId) - userRedirectsRepo.getSumForUserPreviousQuarter(userId) > 0 ? "+" : "") + (userRedirectsRepo.getSumForUserThisQuarter(userId) - userRedirectsRepo.getSumForUserPreviousQuarter(userId)) + "(" + userRedirectsRepo.getSumForUserThisQuarter(userId) + " - " + userRedirectsRepo.getSumForUserPreviousQuarter(userId) + ")";
        } else {
            redirectsQuarter = "0";
        }

        String contentViewsQuarter;
        if(postClicksRepo.getSumForUserThisQuarter(userId) != 0 && postClicksRepo.getSumForUserPreviousQuarter(userId) != 0) {
            contentViewsQuarter = (postClicksRepo.getSumForUserThisQuarter(userId) - postClicksRepo.getSumForUserPreviousQuarter(userId) > 0 ? "+" : "") + (postClicksRepo.getSumForUserThisQuarter(userId) - postClicksRepo.getSumForUserPreviousQuarter(userId)) + "(" + postClicksRepo.getSumForUserThisQuarter(userId) + " - " + postClicksRepo.getSumForUserPreviousQuarter(userId) + ")";
        } else {
            contentViewsQuarter = "0";
        }

        //Add Ranking Table Data
        String content = tableBase.replace("{{TABLEROW}}",  makeRankingTable("plus", Math.toIntExact(user.getId())))
                .replace("{{PROFILEVIEWS}}", obj.getString("profileViews"))
                .replace("{{DAILYVIEWS}}", obj.getString("viewsPerDay"))
                .replace("{{REDIRECTS}}", obj.getString("redirects"))
                .replace("{{CONTENTVIEWS}}", obj.getString("postViews"))
                .replace("{{PROFILERANK}}", obj.getString("rankingProfile"))
                .replace("{{GROUPPROFILERANK}}", obj.getString("rankingProfileByGroup"))
                .replace("{{CONTENTRANK}}", obj.getString("rankingContent"))
                .replace("{{GROUPCONTENTRANK}}", obj.getString("rankingContentByGroup"))
                .replace("{{PROFILEVIEWSQUARTER}}", profileViewsQuarter)
                .replace("{{REDIRECTSQUARTER}}", redirectsQuarter)
                .replace("{{CONTENTVIEWSQUARTER}}", contentViewsQuarter)
                .replace("{{IMAGEDAILY}}", imageDaily)
                .replace("{{IMAGEPROFILE}}", imageProfile)
                .replace("{{IMAGEREDIRECTS}}", imageRedirect)
                .replace("{{IMAGECONTENT}}", imageContent)
                .replace("<tr>", "<tr style=\"" + tablerowCSS + "\">")
                ;



        statMail.setContent(content);
        statMail.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        statMail.setSent(0);

        statMailsRepo.save(statMail);
    }


    private String makeRankingTable(String accType, int userId) throws JSONException {
        StringBuilder content = new StringBuilder();

        JSONArray array = new JSONArray(getSingleUserTagsData(userId, "profile"));

        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String row = tablerowBase;
            row = row.replace("REPLACE1", obj.getString("name")).replace("REPLACE2", obj.getString("ranking")).replace("REPLACE3", obj.getString("count"));
            content.append(row);
        }

        return content.toString();
    }

    public String getSingleUserTagsData(long id, String sorter) throws JSONException {
        JSONObject ranking = new JSONObject(getRankingsInTagsForUserBySorter(id, sorter));
        JSONObject percentage = getPercentageForTagsByUserId(id);
        var jsonKeys = ranking.keys();
        JSONArray array = new JSONArray();

        while (jsonKeys.hasNext()) {
            String tag = jsonKeys.next().toString();
            JSONObject tempJson;
            int companyCount = new ArrayList<>(Arrays.stream(getCompetitionByTags(id).get(tag).split(",")).toList()).size();
            try {
                tempJson = new JSONObject().put("percentage", percentage.getDouble(tag));
            } catch (Exception e) {
                e.printStackTrace();
                tempJson = new JSONObject().put("percentage", -1);
            }
            try {
                tempJson.put("ranking", ranking.getInt(tag)).put("name", tag);
            } catch (Exception e) {
                e.printStackTrace();
                tempJson.put("ranking", -1);
            }
            tempJson.put("count", companyCount);

            array.put(tempJson);
        }
        return array.toString();

    }

    public String getRankingsInTagsForUserBySorter(long id, String sorter) throws JSONException {
        Map<String, String> competition = getCompetitionByTags(id);
        String thisCompanyName = userRepo.getDisplayNameById(id);

        JSONObject json = new JSONObject();

        for(String key : competition.keySet()) {
            List<String> companyNames = new ArrayList<>(Arrays.stream(competition.get(key).split(",")).toList());

            for(String name : companyNames) {
                if(name.startsWith(" ")) {
                    companyNames.set(companyNames.indexOf(name), name.replaceFirst(" ", ""));
                }
            }

            if(sorter.equalsIgnoreCase("content")) {
                json.put(key, getRankingInListByContentView(thisCompanyName, companyNames));
            } else if(sorter.equalsIgnoreCase("profile")){
                json.put(key, getRankingInListByProfileView(thisCompanyName, companyNames));
            } else {
                json.put(key, getRankingInListByProfileView(thisCompanyName, companyNames));
            }

        }
        return json.toString();

    }

    public JSONObject getPercentageForTagsByUserId(Long userId) throws JSONException {
        JSONObject tagPercentages = new JSONObject();
        Optional<String> tagData = getTags(userId, getTypeProfileTags(Math.toIntExact(userId)));

        if (tagData.isPresent()) {
            List<String> rawTags = Arrays.asList(tagData.get().split(";"));
            List<List<String>> decryptedTags = decryptTagsStringInList(rawTags);
            for(List<String> tags : decryptedTags) {
                for (String tag : tags) {
                    double percentage = getUserCountAsPercentageForSingleTag(tag);
                    tagPercentages.put(tag, percentage);
                }
            }
        }
        return tagPercentages;
    }

    public Map<String,String> getCompetitionByTags(Long userId){
        Map<String, String> tagsWithCompetingUsers = new HashMap<>();
        Optional<String> tagData = getTags(userId, getTypeProfileTags(Math.toIntExact(userId)));

        if (tagData.isPresent()) {
            List<String> rawTags = Arrays.asList(tagData.get().split(";"));
            List<List<String>> decryptedTags = decryptTagsStringInList(rawTags);

            for(List<String> tags : decryptedTags) {
                for (String tag : tags) {
                    List<Long> competingUserIdsWithTag = getUserIdsByTag(tag);
                    List<String> competingUsersWithTag = userRepo.findAllDisplayNameByIdIn(competingUserIdsWithTag);
                    tagsWithCompetingUsers.put(tag, competingUsersWithTag.toString());
                }
            }
        }

        return tagsWithCompetingUsers;
    }


    private int getRankingInListByContentView(String companyName, List<String> otherCompanies) {
        List<String> allCompaniesList = new ArrayList<>(otherCompanies);
        if(!allCompaniesList.contains(companyName)) {
            allCompaniesList.add(companyName);
        }
        try {
            allCompaniesList.sort((o1, o2) -> Math.toIntExact((int)(userRepo.findByDisplayName(o2).isPresent() ?
                    postController.getPostViewsOfUserById(userRepo.findByDisplayName(o2).get().getId()) : 0)
                    - (userRepo.findByDisplayName(o1).isPresent() ?
                    postController.getPostViewsOfUserById(userRepo.findByDisplayName(o1).get().getId()) : 0)));

            return allCompaniesList.indexOf(companyName) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int getRankingInListByProfileView(String companyName, List<String> otherCompanies) {
        List<String> allCompaniesList = new ArrayList<>(otherCompanies);
        if(!allCompaniesList.contains(companyName)) {
            allCompaniesList.add(companyName);
        }
        try {
            allCompaniesList.sort((o1, o2) -> {
                int value1 = 0;
                int value2 = 0;
                if(userRepo.findByDisplayName(o2).isPresent()) {
                    value2 = userStatsRepository.findByUserId(userRepo.findByDisplayName(o2).get().getId()) != null ? Math.toIntExact(userStatsRepository.findByUserId(userRepo.findByDisplayName(o2).get().getId()).getProfileView()) : 0;
                }
                if(userRepo.findByDisplayName(o1).isPresent()) {
                    value1 = userStatsRepository.findByUserId(userRepo.findByDisplayName(o1).get().getId()) != null ? Math.toIntExact(userStatsRepository.findByUserId(userRepo.findByDisplayName(o1).get().getId()).getProfileView()) : 0;
                }
                return value2 - value1;
            });
            return allCompaniesList.indexOf(companyName) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    public Optional<String> getTags(long userId, String type) {
        switch (type) {
            case "basis" -> {
                return wpUserMetaRepository.getTagsBasis(userId);
            }
            case "basis_plus" -> {
                return wpUserMetaRepository.getTagsBasisPlus(userId);
            }
            case "plus" -> {
                return wpUserMetaRepository.getTagsPlus(userId);
            }
            case "premium" -> {
                return wpUserMetaRepository.getTagsPremium(userId);
            }
        }
        return Optional.empty();
    }

    public String getTypeProfileTags(int id) {
        if (wpUserMetaRepository.existsByUserId((long) id)){
            String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId((long) id);
            if (wpUserMeta.contains("customer")) return "none";
            if (wpUserMeta.contains("administrator")) return "admin";
            if (wpUserMeta.contains(Constants.getInstance().getPlusAnbieter())) return "plus";
            if (wpUserMeta.contains(Constants.getInstance().getBasisPlusAnbieter())) return "basis_plus";
            if (wpUserMeta.contains(Constants.getInstance().getPremiumAnbieter())) return "premium";
            if(wpUserMeta.contains(Constants.getInstance().getBasisAnbieter())) return "basis";
            if (wpUserMeta.contains("anbieter")) return "none";
        }


        return "none";
    }

    public List<List<String>> decryptTagsStringInList(List<String> cryptedTags) {
        List<List<String>> list = new ArrayList<>();
        for(String tags : cryptedTags) {
            list.add(decryptTags(tags));
        }
        return list;
    }

    public List<String> decryptTags(String cryptedTag) {
        Pattern cleaner = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = cleaner.matcher(cryptedTag);
        List<String> tags = new ArrayList<>();
        while(matcher.find()) {
            tags.add(matcher.group(1));
        }
        return tags;
    }

    public double getUserCountAsPercentageForSingleTag(String tag) {
        int totalUsersWithTag = getTotalCountOfUsersWithTags();
        int countForTag = countUsersByTag(tag);

        if (totalUsersWithTag == 0) {
            return 0; // Vermeidung der Division durch null
        }

        return (double) countForTag / totalUsersWithTag * 100;
    }

    public List<Long> getUserIdsByTag(String tag) {
        List<Long> list = new ArrayList<>();
        list.addAll(wpUserMetaRepository.getUserIdsByTagBasis("\"" + tag + "\""));
        list.addAll(wpUserMetaRepository.getUserIdsByTagBasisPlus("\"" + tag + "\""));
        list.addAll(wpUserMetaRepository.getUserIdsByTagPlus("\"" + tag + "\""));
        list.addAll(wpUserMetaRepository.getUserIdsByTagPremium("\"" + tag + "\""));
        return list;
    }

    public Integer getTotalCountOfUsersWithTags() {
        return wpUserMetaRepository.getTotalCountOfUsersWithTagBasis() + wpUserMetaRepository.getTotalCountOfUsersWithTagBasisPlus() + wpUserMetaRepository.getTotalCountOfUsersWithTagPlus() + wpUserMetaRepository.getTotalCountOfUsersWithTagPremium();
    }

    public Integer countUsersByTag(String tag) {
        return wpUserMetaRepository.countUsersByTagBasis("\"" + tag + "\"") + wpUserMetaRepository.countUsersByTagBasisPlus("\"" + tag + "\"") + wpUserMetaRepository.countUsersByTagPlus("\"" + tag + "\"") + wpUserMetaRepository.countUsersByTagPremium("\"" + tag + "\"");
    }


    public String getAllSingleUser(long id) throws JSONException {
        JSONObject obj = new JSONObject();
        WPUser user = userRepo.findById(id).isPresent() ? userRepo.findById(id).get() : null;
        if(user != null) {
            obj.put("id", user.getId());
            obj.put("email", user.getEmail());
            obj.put("displayName", user.getDisplayName());
            obj.put("niceName", user.getNicename());
            obj.put("creationDate", user.getRegistered().toLocalDate().toString());
            if (userStatsRepository.existsByUserId(user.getId())) {
                UserStats statsUser = userStatsRepository.findByUserId(user.getId());
                obj.put("profileViews", statsUser.getProfileView());
                obj.put("postViews", postController.getPostViewsOfUserById(user.getId()));
                obj.put("postCount", postController.getPostCountOfUserById(user.getId()));
            } else {
                obj.put("profileViews", 0);
                obj.put("postViews", 0);
                obj.put("postCount", 0);
                obj.put("performance", 0);
            }
            if (userViewsRepo.existsByUserId(user.getId())) {
                obj.put("viewsPerDay", getUserClicksPerDay(user.getId()));
                if (tendencyUp(user.getId()) != null) {
                    obj.put("tendency", tendencyUp(user.getId()));
                }
            } else {
                obj.put("viewsPerDay", 0);
                obj.put("tendency", 0);
            }

            //Does User have a made in EU badge
            if (wpUserMetaRepository.getTeleEU(user.getId()).isEmpty()) {
                obj.put("TeleEU", false);
            } else {
                obj.put("TeleEU", wpUserMetaRepository.getTeleEU(user.getId()).get().contains("a:1:{"));
            }
            if (wpUserMetaRepository.getTeleDE(user.getId()).isEmpty()) {
                obj.put("TeleDE", false);
            } else {
                obj.put("TeleDE", wpUserMetaRepository.getTeleDE(user.getId()).get().contains("a:1:{"));
            }

            //Does User have a made in DE badge
            if (wpUserMetaRepository.getCompanyCategory(user.getId()).isEmpty()) {
                obj.put("category", "none");
            } else {
                obj.put("category", getCompanyCategoryFromString(wpUserMetaRepository.getCompanyCategory(user.getId()).get()));
            }

            //checks how many employees a company has.
            if (wpUserMetaRepository.getCompanyEmployees(user.getId()).isEmpty()) {
                obj.put("employees", "");
            } else {
                obj.put("employees", wpUserMetaRepository.getCompanyEmployees(user.getId()).get());
            }

            //Checks how many times website has redirected to a user's homepage
            if(userRedirectsRepo.existsByUserId(user.getId())) {
                obj.put("redirects", userRedirectsRepo.getAllRedirectsOfUserIdSummed(user.getId()));
            } else {
                obj.put("redirects", 0);
            }

            Pattern pattern = Pattern.compile("\"([^\"]+)\"");

            if (wpUserMetaRepository.getService(user.getId()).isEmpty()) {
                obj.put("service", "none");
            } else {
                JSONArray json = new JSONArray();
                Matcher matcher = pattern.matcher(wpUserMetaRepository.getService(user.getId()).get());
                if (matcher.find()) {
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        json.put(matcher.group(i));
                    }
                }
                obj.put("service", json);
            }


            if (getTags(user.getId(), getTypeProfileTags(Math.toIntExact(user.getId()))).isEmpty()) {
                obj.put("tags", "none");
            } else {
                JSONArray json = new JSONArray();
                Matcher matcher = pattern.matcher(getTags(user.getId(), getTypeProfileTags(Math.toIntExact(user.getId()))).get());
                if (matcher.find()) {
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        json.put(matcher.group(i));
                    }
                }
                obj.put("tags", json);
            }
            obj.put("potential", 0);
            try {
                obj.put("potential", getPotentialPercent(Math.toIntExact(user.getId())));
            } catch (Exception ignored) {
            }

            if(wpUserMetaRepository.getTelIntern(user.getId()).isPresent()) {
                obj.put("tel", wpUserMetaRepository.getTelIntern(user.getId()).get());
            } else if(wpUserMetaRepository.getTelExtern(user.getId()).isPresent()) {
                obj.put("tel", wpUserMetaRepository.getTelExtern(user.getId()).get());
            }

            if(wpUserMetaRepository.getSlogan(user.getId()).isPresent()) {
                obj.put("slogan", wpUserMetaRepository.getSlogan(user.getId()).get());
            } else {
                obj.put("slogan", " - ");
            }

            obj.put("accountType", getType(Math.toIntExact(user.getId())));

            String path = String.valueOf(Paths.get(config.getProfilephotos() + "/" + user.getId() + "/profile_photo.png"));
            String path2 = String.valueOf(Paths.get(config.getProfilephotos() + "/" + user.getId() + "/profile_photo.jpg"));

            String srcUrl = Constants.getInstance().getProfilePhotoStart() + user.getId() + "/profile_photo";

            if (new File(path).exists()) {
                obj.put("img", srcUrl + ".png");
            } else if (new File(path2).exists()) {
                obj.put("img", srcUrl + ".jpg");
            }

            putRankings(id, obj);

            return obj.toString();
        } else {
            return "User not found";
        }
    }

    public double getUserClicksPerDay(long userId) {
        int countDays = getDaysSinceTracking(userId);
        long totalClicks = 0;
        int lastUniId = 0;
        for(UserViewsByHourDLC u : userViewsRepo.findByUserId(userId)) {
            if(lastUniId != u.getUniId()) {
                lastUniId = u.getUniId();
            }
            totalClicks+= u.getViews();
        }
        if(countDays > 0) {
            return (double) totalClicks / countDays;
        } else {
            return 0;
        }
    }

    public Boolean tendencyUp(long userId) {
        int count = 7;
        int clicks = 0;
        if(getDaysSinceTracking(userId) > 7) {
            for(Integer uni : userViewsRepo.getLast7Uni()) {
                for(UserViewsByHourDLC u : userViewsRepo.findByUserIdAndUniId(userId, uni)) {
                    clicks += u.getViews();
                }
            }
        } else {
            return null;
        }
        Double avg = ((double) clicks / count);
        if(avg > getUserClicksPerDay(userId)) return true;
        if(avg.equals(getUserClicksPerDay(userId))) return null;
        return false;
    }

    private String getCompanyCategoryFromString(String categoryString) {
        if(categoryString.contains("Startup")) return "startup";
        if(categoryString.contains("Hochschule")) return "hochschule";
        if(categoryString.contains("Mittelstand")) return "mittelstand";
        if(categoryString.contains("Verband")) return "verband";
        if(categoryString.contains("Keine Angabe")) return "keine angabe";
        if(categoryString.contains("Dienstleister")) return "dienstleister";
        if(categoryString.contains("Großkonzern")) return "großkonzern";
        return "none";
    }


    public double getPotentialPercent(int userId) throws JSONException {
        JSONObject json = new JSONObject(getPotentialByID(userId));

        int countFulfilled = 0; int countPossible = 0;
        countPossible+= 1 + 1 + 1 + 1 + json.getInt("tagsMax") + 1 + json.getInt("contactPublicMax") + json.getInt("contactInternMax") + json.getInt("companyDetailsMax") + json.getInt("solutionsMax");
        countFulfilled += json.getInt("profilePicture")
                + json.getInt("titlePicture") + json.getInt("bio") + json.getInt("slogan")
                + json.getInt("tagsCount") + json.getInt("contactPublic")
                + json.getInt("contactIntern") + json.getInt("companyDetails") + json.getInt("solutions");

        return (double) countFulfilled / countPossible;
    }

    public String getType(int id) {
        if (wpUserMetaRepository.existsByUserId((long) id)){
            String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId((long) id);
            if (wpUserMeta.contains("customer")) return "none";
            if (wpUserMeta.contains("administrator")) return "admin";
            if (wpUserMeta.contains(Constants.getInstance().getPlusAnbieter())) return "plus";
            if (wpUserMeta.contains(Constants.getInstance().getBasisPlusAnbieter())) return "basis-plus";
            if (wpUserMeta.contains(Constants.getInstance().getPremiumAnbieter())) return "premium";
            if(wpUserMeta.contains(Constants.getInstance().getBasisAnbieter())) return "basis";
            if (wpUserMeta.contains("anbieter")) return "none";
        }


        return "none";
    }

    private void putRankings(long id, JSONObject obj) throws JSONException {
        obj.put("rankingContent", getRankingTotalContentViews(id));
        obj.put("rankingContentByGroup", getRankingInTypeContentViews(id));
        obj.put("rankingProfile", getRankingTotalProfileViews(id));
        obj.put("rankingProfileByGroup", getRankingInTypeProfileViews(id));
    }

    private int getDaysSinceTracking(long userId) {
        if(userViewsRepo.existsByUserId(userId)) {
            return (int) (userViewsRepo.getLastUniId() - userViewsRepo.getFirstUniIdByUserid(userId));
        } else {
            return 0;
        }
    }

    public String getPotentialByID(int userId) throws JSONException {

        String type = this.getType(userId);
        //Check whether these profile parts have been filled out.
        boolean hasProfilePic = wpUserMetaRepository.getProfilePath(((long) userId)).isPresent() && !wpUserMetaRepository.getProfilePath(((long) userId)).get().equals("https://it-sicherheit.de/wp-content/uploads/2023/06/it-sicherheit-logo_icon_190x190.png");
        boolean hasCover = wpUserMetaRepository.getCoverPath((long) userId).isPresent();
        boolean hasDescription = wpUserMetaRepository.getDescription((long) userId).isPresent();
        boolean hasSlogan = !type.equals("basis") && wpUserMetaRepository.getSlogan((long) userId).isPresent();

        //Check how many internal contacts have been filled.
        int countAnsprechpartnerIntern = 0;
        int maxAnsprechpartnerIntern = 3;
        if(wpUserMetaRepository.getPersonIntern((long) userId).isPresent() && !wpUserMetaRepository.getPersonIntern((long) userId).get().isEmpty()) countAnsprechpartnerIntern++;
        if(wpUserMetaRepository.getMailIntern((long) userId).isPresent() && !wpUserMetaRepository.getMailIntern((long) userId).get().isEmpty()) countAnsprechpartnerIntern++;
        if(wpUserMetaRepository.getTelIntern((long) userId).isPresent() && !wpUserMetaRepository.getTelIntern((long) userId).get().isEmpty()) countAnsprechpartnerIntern++;

        //Check how many external contacts have been filled.
        int countKontaktExtern = 0;
        int maxKontaktExtern = 7;
        if(wpUserMetaRepository.getNameExtern((long) userId).isPresent()  && !wpUserMetaRepository.getNameExtern((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getSecondaryMail((long) userId).isPresent() && !wpUserMetaRepository.getSecondaryMail((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getTelExtern((long) userId).isPresent() && !wpUserMetaRepository.getTelExtern((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getAdresseStreet((long) userId).isPresent() && !wpUserMetaRepository.getAdresseStreet((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getAdressePLZ((long) userId).isPresent() && !wpUserMetaRepository.getAdressePLZ((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getAdresseOrt((long) userId).isPresent() && !wpUserMetaRepository.getAdresseOrt((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getURLExtern((long) userId).isPresent() && !wpUserMetaRepository.getURLExtern((long) userId).get().isEmpty()) countKontaktExtern++;

        //Check how many tags are allowed, and how many are set.
        int allowedTags = 0;
        int allowedLosungen = 0;
        switch (type) {
            case "basis" -> {
                allowedTags = 1;
                maxKontaktExtern = 6;
            }
            case "basis-plus" -> allowedTags = 3;
            case "plus" -> {
                allowedTags = 8;
                allowedLosungen = 5;
            }
            case "premium" -> {
                allowedTags = 12;
                allowedLosungen = 12;
            }
            case "admin" -> {
                allowedTags = 100;
                allowedLosungen = 100;
            }
        }


        int countTags = new JSONArray(getSingleUserTagsData(userId, "profile")).length();

        //Check how many solutions are allowed, and how many are set.
        int solutions = 0;
        for(int i = 0; i < allowedLosungen; i++) {
            switch(i) {
                case(0) -> {
                    if(wpUserMetaRepository.getSolutionHead1((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead1((long) userId).get().isBlank()) solutions ++;
                }
                case(1) -> {
                    if(wpUserMetaRepository.getSolutionHead2((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead2((long) userId).get().isBlank()) solutions ++;
                }
                case(2) -> {
                    if(wpUserMetaRepository.getSolutionHead3((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead3((long) userId).get().isBlank()) solutions ++;
                }
                case(3) -> {
                    if(wpUserMetaRepository.getSolutionHead4((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead4((long) userId).get().isBlank()) solutions ++;
                }
                case(4) -> {
                    if(wpUserMetaRepository.getSolutionHead5((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead5((long) userId).get().isBlank()) solutions ++;
                }
                case(5) -> {
                    if(wpUserMetaRepository.getSolutionHead6((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead6((long) userId).get().isBlank()) solutions ++;
                }
                case(6) -> {
                    if(wpUserMetaRepository.getSolutionHead7((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead7((long) userId).get().isBlank()) solutions ++;
                }
                case(7) -> {
                    if(wpUserMetaRepository.getSolutionHead8((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead8((long) userId).get().isBlank()) solutions ++;
                }
                case(8) -> {
                    if(wpUserMetaRepository.getSolutionHead9((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead9((long) userId).get().isBlank()) solutions ++;
                }
                case(9) -> {
                    if(wpUserMetaRepository.getSolutionHead10((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead10((long) userId).get().isBlank()) solutions ++;
                }
                case(10) -> {
                    if(wpUserMetaRepository.getSolutionHead11((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead11((long) userId).get().isBlank()) solutions ++;
                }
                case(11) -> {
                    if(wpUserMetaRepository.getSolutionHead12((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead12((long) userId).get().isBlank()) solutions ++;
                }
            }
        }

        //Check how many company datafields have been filled.
        int companyDetails = 0;
        int companyDetailsMax = 4;
        if(wpUserMetaRepository.getCompanyCategory((long) userId).isPresent() && !wpUserMetaRepository.getCompanyCategory((long) userId).get().isEmpty()) companyDetails++;
        if(wpUserMetaRepository.getManager((long) userId).isPresent() && !wpUserMetaRepository.getManager((long) userId).get().isEmpty()) companyDetails++;
        if(wpUserMetaRepository.getCompanyEmployees((long) userId).isPresent() && !wpUserMetaRepository.getCompanyEmployees((long) userId).get().isEmpty()) companyDetails++;
        if(wpUserMetaRepository.getService((long) userId).isPresent() && !wpUserMetaRepository.getService((long) userId).get().isEmpty()) companyDetails++;


        JSONObject json = new JSONObject();
        json.put("profilePicture", hasProfilePic ? 1 : 0);
        json.put("titlePicture", hasCover ? 1 : 0);
        json.put("bio", hasDescription ? 1 : 0);
        json.put("slogan", hasSlogan ? 1 : 0);
        json.put("tagsCount", countTags);
        json.put("tagsMax", allowedTags);
        json.put("contactPublic", countKontaktExtern);
        json.put("contactPublicMax", maxKontaktExtern);
        json.put("contactIntern", countAnsprechpartnerIntern);
        json.put("contactInternMax", maxAnsprechpartnerIntern);
        json.put("companyDetails", companyDetails);
        json.put("companyDetailsMax", companyDetailsMax);
        json.put("solutions", solutions);
        json.put("solutionsMax", allowedLosungen);

        return json.toString();
    }

    public int getRankingInTypeProfileViews(long id) {
        return rankingGroupProfileRepo.getRankById(id).isPresent() ? rankingGroupProfileRepo.getRankById(id).get() : -1;
    }

    public int getRankingInTypeContentViews(long id) {
        return rankingGroupContentRepo.getRankById(id).isPresent() ? rankingGroupContentRepo.getRankById(id).get() : -1;
    }

    public int getRankingTotalProfileViews(long id) {
        return rankingTotalProfileRepo.getRankById(id).isPresent() ? rankingTotalProfileRepo.getRankById(id).get() : -1;
    }

    public int getRankingTotalContentViews(long id)  {
        return rankingTotalContentRepo.getRankById(id).isPresent() ? rankingTotalContentRepo.getRankById(id).get() : -1;
    }

    /**
     *
     * @param page which page of results in the given size you want to fetch.
     * @param size the number of results you want per page.
     * @param search the search-term you want results for, give empty string for none.
     * @param filterAbo "basis" "basis-plus" "plus" "premium" "sponsor" "none" "admin"
     * @param sorter "profileView" "contentView" "viewsByTime", any other String searches by user id.
     * @return a JSON String containing information about all users in the specified page, and the number of users loaded.
     * @throws JSONException .
     */
    public String getAll(Integer page, Integer size, String search, String filterAbo, String filterTyp, String sorter) throws JSONException {
        List<WPUser> list;


        if(sorter != null) {
            //Both filters unused, sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAll(search, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAll(search, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAll(search, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAll(search, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAbo(search, filterAbo, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAbo(search, filterAbo, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAbo(search, filterAbo, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAbo(search, filterAbo, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Type Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsCompany(search, filterTyp, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsCompany(search, filterTyp, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeCompany(search, filterTyp, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingCompany(search, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else {
                //Abo, Company type and sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            }
        } else {
            //Neither filters nor sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                list = userRepo.getAllByNicenameContainingAll(search, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used.
                list = userRepo.getAllByNicenameContainingAbo(search, filterAbo, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Filter used.
                list = userRepo.getAllByNicenameContainingCompany(search, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
            } else {
                //Both filters used, no sorter used.
                list = userRepo.getAllByNicenameContainingAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
            }
        }

        JSONArray response = new JSONArray();

        for(WPUser user : list) {
            JSONObject obj = new JSONObject(getAllSingleUser(user.getId()));
            response.put(obj);
        }
        return new JSONObject().put("users", response).put("count", list.size()).toString();
    }


    public String getAllWithTagsTest(Integer page, Integer size, String search, String filterAbo, String filterTyp, String tag, String sorter) throws JSONException {
        List<WPUser> list;


        if(sorter != null) {
            //Both filters unused, sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAllWithTags(search, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAllWithTags(search, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAllWithTags(search, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAllWithTags(search, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAboWithTags(search, filterAbo, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAboWithTags(search, filterAbo, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAboWithTags(search, filterAbo, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAboWithTags(search, filterAbo, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Type Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else {
                //Abo, Company type and sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            }
        } else {
            //Neither filters nor sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                list = userRepo.getAllByNicenameContainingAllWithTags(search, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used.
                list = userRepo.getAllByNicenameContainingAboWithTags(search, filterAbo, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Filter used.
                list = userRepo.getAllByNicenameContainingCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            } else {
                //Both filters used, no sorter used.
                list = userRepo.getAllByNicenameContainingAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            }
        }

        JSONArray response = new JSONArray();

        for(WPUser user : list) {
            JSONObject obj = new JSONObject(getAllSingleUser(user.getId()));
            response.put(obj);
        }
        return new JSONObject().put("users", response).put("count", list.size()).toString();
    }

    public String getUserById(int id) throws JSONException {
        JSONObject obj = new JSONObject();
        WPUser user = userRepo.findById((long) id).orElseThrow();

        obj.put("id", user.getId());
        obj.put("displayName", user.getDisplayName());
        obj.put("accountType", getType(Math.toIntExact(user.getId())));
        obj.put("accessLevel", getAccessLevel(user.getId()));

        return obj.toString();
    }

    public String getAccessLevel(long userId) {
        if(getType((int) userId).equals("admin")) {
            return "admin";
        } else if(isModerator(userId)) {
            return "mod";
        } else if(getType((int) userId).equals("premium")) {
            return "user";
        } else {
            return "none";
        }
    }

    public String getUserByLogin(@RequestParam String u) throws JSONException {
        JSONObject obj = new JSONObject();
        var user = userRepo.findByLogin(u);
        if (user.isPresent()){
            obj.put("id", user.get().getId());
            obj.put("displayName",user.get().getDisplayName());
            obj.put("accountType", getType(Math.toIntExact(user.get().getId())));
            obj.put("accessLevel", getAccessLevel(user.get().getId()));
        }
        return obj.toString();
    }

    public String getAllSingleUserNewsletter(long id) {
        JSONObject obj = new JSONObject();
        try {
            id = newsletterRepo.getWpUserIdById(id);
            WPUser user = userRepo.findById(id).isPresent() ? userRepo.findById(id).get() : null;
            if (user != null) {
                obj.put("id", user.getId());
                obj.put("email", user.getEmail());
                obj.put("displayName", user.getDisplayName());
                obj.put("niceName", user.getNicename());
                obj.put("creationDate", user.getRegistered().toLocalDate().toString());
            }
        } catch (Exception e) {
            return "Error, user kaputt";
        }
        return obj.toString();
    }

    public ResponseEntity<byte[]> getProfilePic(@RequestParam long id) {

        try {
            String path = String.valueOf(Paths.get(config.getProfilephotos() + "/" + id + "/profile_photo.png"));
            String path2 = String.valueOf(Paths.get(config.getProfilephotos() + "/" + id + "/profile_photo.jpg"));

            File cutePic = new File(path);
            if (!cutePic.exists())
            {
                cutePic = new File(path2);
            }
            byte[] imageBytes = Files.readAllBytes(cutePic.toPath());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }


    public String getUserClicksChartData(long id, String start, String end) throws JSONException {
        java.sql.Date startDate = java.sql.Date.valueOf(start);
        java.sql.Date endDate  = java.sql.Date.valueOf(end);

        //If the beginning is after the end (good manhwa), swap them.
        if(startDate.after(endDate)) {
            java.sql.Date puffer = startDate;
            startDate = endDate;
            endDate = puffer;
        }

        JSONArray json = new JSONArray();
        //For all dates selected, add data
        for(LocalDate date : startDate.toLocalDate().datesUntil(endDate.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;
            //Check if we have stats for the day
            if (uniRepo.findByDatum(java.sql.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            if(uniId != 0 && uniRepo.findById(uniId).isPresent()) {
                //Since we have data, add date and profileViews
                JSONObject day = new JSONObject();
                JSONArray dailyPosts = new JSONArray();
                JSONObject biggestPost = new JSONObject();

                day.put("date", uniRepo.findById(uniId).get().getDatum());
                if(userViewsRepo.existsByUserId(id)) {
                    day.put("profileViews", userViewsRepo.getSumByUniIdAndUserId(uniId, id) != null ? userViewsRepo.getSumByUniIdAndUserId(uniId, id) : 0);
                } else {
                    day.put("profileViews", 0);
                }

                Post biggestPostbuffer = null;
                for(Post post : postRepo.getPostsByAuthorAndDate(id, date)) {
                    //Add data for all posts
                    JSONObject postToday = new JSONObject();
                    if(biggestPostbuffer == null) {
                        biggestPostbuffer = post;
                    } else {
                        if((statRepository.getSumClicks(post.getId())) != null) {
                            if(statRepository.getSumClicks(post.getId()) > statRepository.getSumClicks(biggestPostbuffer.getId())) {
                                biggestPostbuffer = post;
                            }
                        }
                    }
                    postToday.put("id", post.getId());
                    postToday.put("title", post.getTitle());
                    postToday.put("type", postController.getType(Math.toIntExact(post.getId())));
                    postToday.put("clicks", statRepository.getSumClicks(post.getId()) != null ? statRepository.getSumClicks(post.getId()) : 0);
                    dailyPosts.put(postToday);
                }
                day.put("posts", dailyPosts);
                if(biggestPostbuffer != null) {
                    biggestPost.put("id", biggestPostbuffer.getId());
                    biggestPost.put("title", biggestPostbuffer.getTitle());
                    biggestPost.put("type", postController.getType(Math.toIntExact(biggestPostbuffer.getId())));
                    biggestPost.put("clicks", statRepository.getSumClicks(biggestPostbuffer.getId()) != null ? statRepository.getSumClicks(biggestPostbuffer.getId()) : 0);
                } else {
                    biggestPost.put("id", 0);
                    biggestPost.put("title", 0);
                    biggestPost.put("type", 0);
                    biggestPost.put("clicks", 0);
                }
                day.put("biggestPost", biggestPost);

                json.put(day);
            }

        }
        return  json.toString();
    }

    /**
     *
     * @return a JSON-String containing a list of Events (newEvents) starting with u| for upcoming, c| for current and their type,
     * the count of events in the past for this user (countOldEvents)
     * and a count of all events by this user that are active (countTotal).
     */
    public String getCountEvents(long id) throws JSONException {

        JSONObject json = new JSONObject();

        List<String> events = new ArrayList<>();
        List<Events> allEvents = eventsRepo.getAllByOwnerID(id);
        int countOld = 0;

        for (Events e : allEvents) {
            if(eventsController.isActive(e)) {
                if (eventsController.isCurrent(e)) {
                    events.add("c|" + eventsController.getEventType(e));
                } else if (eventsController.isUpcoming(e)) {
                    events.add("u|" + eventsController.getEventType(e));
                } else {
                    countOld++;
                }
            }

        }
        json.put("newEvents", new JSONArray(events));
        json.put("countOldEvents", countOld);
        json.put("countTotal", countOld + events.size());

        return json.toString();
    }

    public String getPostCountByType(long id) throws JSONException {
        List<Post> posts = postRepo.findByAuthorPageable(id, "", PageRequest.of(0, postController.getCountTotalPosts()));

        int countArtikel = 0;
        int countBlogs = 0;
        int countNews = 0;
        int countWhitepaper = 0;
        int countPodcasts = 0;

        for(Post post : posts) {
            switch(postController.getType(post.getId())) {
                case "artikel" -> {
                    countArtikel++;
                }
                case "blog" -> {
                    countBlogs++;
                }
                case "news" -> {
                    countNews++;
                }
                case "podcast" -> {
                    countPodcasts++;
                }
                case "whitepaper" -> {
                    countWhitepaper++;
                }
            }
        }

        JSONObject json = new JSONObject();
        json.put("Whitepaper", countWhitepaper);
        json.put("Blogs", countBlogs);
        json.put("News", countNews);
        json.put("Podcasts", countPodcasts);
        json.put("Artikel", countArtikel);

        return json.toString();
    }

    public List<String> getAmountOfEventsCreatedYesterday(long id) {
        List<String> events = new ArrayList<>();
        List<Events> allEvents = eventsRepo.getAllByOwnerID(id);
        LocalDate today = LocalDate.now();

        for (Events e : allEvents) {
            LocalDate createdDate = e.getEventDateCreated().toLocalDate();

            if (createdDate.isBefore(today) && eventsController.isActive(e)) {
                if(eventsController.isCurrent(e)) {
                    events.add("c|" + eventsController.getEventType(e));
                } else if(eventsController.isUpcoming(e)) {
                    events.add("u|" + eventsController.getEventType(e));
                }

            }
        }
        return events;
    }

    public String getEventsWithStats(Integer page, Integer size,  String filter, String search, long id) throws JSONException, ParseException {
        List<Post> list;

        if(filter.isBlank()) {
            list = postRepo.getAllEventsWithSearchAndAuthor(search, id, PageRequest.of(page, size));
        } else {
            list = postRepo.getAllEventsWithTypeAndSearchAndAuthor(eventsController.getTermIdFromFrontendType(filter), search, id, PageRequest.of(page, size));
        }

        List<JSONObject> stats = new ArrayList<>();

        for(Post post : list) {
            stats.add(new JSONObject(postController.PostStatsByIdForFrontend(post.getId())));
        }

        return new JSONArray(stats).toString();
    }

    public UserStats getUserStats(@PathVariable("userId") Long userId) {
        return userStatsRepository.findByUserId(userId);
    }

    public String getUserStat(@RequestParam Long id) throws JSONException {
        JSONObject obj = new JSONObject();
        UserStats user = userStatsRepository.findByUserId(id);
        obj.put("Profilaufrufe",user.getProfileView());
        return obj.toString();
    }

    public String getViewsBrokenDown(@RequestParam Long id) throws JSONException {
        long viewsBlog = 0;
        long viewsArtikel = 0;
        long viewsProfile = 0;
        long viewsNews = 0;
        long viewsWP = 0;
        long viewsPodcast = 0;
        long viewsVideo = 0;
        try {
            viewsProfile = userStatsRepository.findByUserId(id).getProfileView();
        } catch (NullPointerException ignored) {
        }

        List<Post> posts = postRepo.findByAuthor(id.intValue());

        for (Post post : posts) {
            if (statRepository.existsByArtId(post.getId())) {
                int stat = statRepository.getSumClicks(post.getId()) == null ? 0 : statRepository.getSumClicks(post.getId());
                switch(postController.getType(post.getId())) {
                    case "blog" -> viewsBlog += stat;
                    case "artikel" -> viewsArtikel += stat;
                    case "news" -> viewsNews += stat;
                    case "whitepaper" -> viewsWP += stat;
                    case "podcast" -> viewsPodcast += stat;
                    case "videos" -> viewsVideo += stat;
                }
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("viewsBlog", viewsBlog);
        obj.put("viewsArtikel", viewsArtikel);
        obj.put("viewsNews", viewsNews);
        obj.put("viewsWhitepaper", viewsWP);
        obj.put("viewsPodcast", viewsPodcast);
        obj.put("viewsVideo", viewsVideo);
        obj.put("viewsProfile", viewsProfile);
        return obj.toString();

    }

    public String getUserProfileViewsAveragesByTypeAndPosts() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject(getUserAveragesWithoutPosts()));
        array.put(new JSONObject(getUserAveragesByType()));
        array.put(new JSONObject(getUserAveragesWithPostsWithoutPostClicks()));
        return array.toString();
    }

    public String getUserProfileAndPostViewsAveragesByType() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject(getUserAveragesWithPostsWithoutPostClicks()));
        array.put(new JSONObject(getUserAveragesWithPostsOnlyPostClicks()));
        return array.toString();
    }

    public String getUserAveragesWithoutPosts() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);

        for(WPUser u : userRepo.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            if(!hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, stats);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }

    public String getUserAveragesByType() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);

        for(WPUser u : userRepo.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            addCountAndProfileViewsByType(counts, clicks, u, stats);
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);

        return averages.toString();
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    private String getUserAveragesWithPostsWithoutPostClicks() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);
        for(WPUser u : userRepo.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            if(hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, stats);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their post's views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    private String getUserAveragesWithPostsOnlyPostClicks() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);
        for(WPUser u : userRepo.findAll()) {
            if(hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, false, true);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }

    public boolean hasPost(@RequestParam int id) {
        return !postRepo.findByAuthor(id).isEmpty();
    }

    public boolean isModerator(long userId) {
        if(wpUserMetaRepository.existsByUserId(userId)) {
            return wpUserMetaRepository.getWPUserMetaValueByUserId(userId).contains("editor");
        }
        return false;
    }

    public void addCountAndProfileViewsByType(JSONObject counts, JSONObject clicks, WPUser u, boolean profileViews) throws JSONException {
        switch(getType(Math.toIntExact((u.getId())))) {
            case "basis" -> {
                if(profileViews) {
                    clicks.put("basis", clicks.getInt("basis") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("basis", counts.getInt("basis") + 1);
            }
            case "basis-plus" -> {
                if(profileViews) {
                    clicks.put("basis-plus", clicks.getInt("basis-plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("basis-plus", counts.getInt("basis-plus") + 1);
            }
            case "plus" -> {
                if(profileViews) {
                    clicks.put("plus", clicks.getInt("plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("plus", counts.getInt("plus") + 1);
            }
            case "premium" -> {
                if(profileViews) {
                    clicks.put("premium", clicks.getInt("premium") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("premium", counts.getInt("premium") + 1);
            }
            case "sponsor" -> {
                if(profileViews) {
                    clicks.put("sponsor", clicks.getInt("sponsor") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("sponsor", counts.getInt("sponsor") + 1);
            }
        }
    }

    public void addCountAndProfileViewsByType(JSONObject counts, JSONObject clicks, WPUser u, boolean profileViews, boolean postViews) throws JSONException {
        switch(getType(Math.toIntExact((u.getId())))) {
            case "basis" -> {
                if(profileViews) {
                    clicks.put("basis", clicks.getInt("basis") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("basis", clicks.getInt("basis") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("basis", counts.getInt("basis") + 1);
            }
            case "basis-plus" -> {
                if(profileViews) {
                    clicks.put("basis-plus", clicks.getInt("basis-plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("basis-plus", clicks.getInt("basis-plus") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("basis-plus", counts.getInt("basis-plus") + 1);
            }
            case "plus" -> {
                if(profileViews) {
                    clicks.put("plus", clicks.getInt("plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("plus", clicks.getInt("plus") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("plus", counts.getInt("plus") + 1);
            }
            case "premium" -> {
                if(profileViews) {
                    clicks.put("premium", clicks.getInt("premium") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("premium", clicks.getInt("premium") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("premium", counts.getInt("premium") + 1);
            }
            case "sponsor" -> {
                if(profileViews) {
                    clicks.put("sponsor", clicks.getInt("sponsor") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("sponsor", clicks.getInt("sponsor") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("sponsor", counts.getInt("sponsor") + 1);
            }
        }
    }

    public void buildAveragesFromCountsAndClicks(JSONObject counts, JSONObject clicks, JSONObject averages) throws JSONException {
        if(counts.getInt("basis") != 0) {
            averages.put("basis", clicks.getInt("basis") / counts.getInt("basis"));
        } else {
            averages.put("basis", 0);
        }
        if(counts.getInt("basis-plus") != 0) {
            averages.put("basis-plus", clicks.getInt("basis-plus") / counts.getInt("basis-plus"));
        } else {
            averages.put("basis-plus", 0);
        }
        if(counts.getInt("plus") != 0) {
            averages.put("plus", clicks.getInt("plus") / counts.getInt("plus"));
        } else {
            averages.put("plus", 0);
        }
        if(counts.getInt("premium") != 0) {
            averages.put("premium", clicks.getInt("premium") / counts.getInt("premium"));
        } else {
            averages.put("premium", 0);
        }
    }

    public int getClickTotalOnPostsOfUser (int uid){
        List<Post> posts= postRepo.findByAuthor(uid);
        int clicks = 0;
        for(Post post : posts) {
            if(post != null) {
                clicks += statRepository.getSumClicks(post.getId()) != null ? statRepository.getSumClicks(post.getId()) : 0;
            }
        }

        return clicks;
    }
}

