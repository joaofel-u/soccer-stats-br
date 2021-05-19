package main.parser;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.json.simple.parser.JSONParser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Parser {
    public static void parseSrGoool(String html, String url) {
        Document doc = Jsoup.parse(html);
        String urlPieces[] = url.split("/");

        /* Recupera a tabela de classificacao. */
        for (Element divClassificacao : doc.select("div#classificacao")) {
            for (Element tabelaClassificacao : divClassificacao.select("table#table_classificacao")) {
                JSONObject campeonato = new JSONObject();
                Boolean empty = true;
                String tableString = "";

                campeonato.put("url", url);

                try {
                    JSONArray classificacao = new JSONArray();
                    FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/temp/out.json", true);
                    tableString += urlPieces[4] + " " + urlPieces[5] + "\n";

                    /* Percorre a tabela. */
                    for (Element line : tabelaClassificacao.select("tr.linha-class")) {
                        JSONObject equipe = new JSONObject();
                        String participante = "";
                        int pg = Integer.MIN_VALUE, j = Integer.MIN_VALUE;
                        int v = Integer.MIN_VALUE, e = Integer.MIN_VALUE;
                        int d = Integer.MIN_VALUE, gp = Integer.MIN_VALUE;
                        int gc = Integer.MIN_VALUE, sg;

                        /* Recupera o nome da equipe. */
                        for (Element column : line.select("td.coluna-participante"))
                            participante = column.text();

                        /* Checks if table is empty. */
                        if (empty) {
                            if (participante.equals(""))
                                break;

                            empty = false;
                        }

                        equipe.put("nome", participante);

                        /* Recupera o número de pontos ganhos. */
                        for (Element column : line.select("td.coluna-pg"))
                            pg = Integer.parseInt(column.text());

                        equipe.put("PG", pg);

                        /* Recupera o número de jogos disputados. */
                        for (Element column : line.select("td.coluna-j"))
                            j = Integer.parseInt(column.text());

                        equipe.put("J", j);

                        /* Recupera o número de vitorias. */
                        for (Element column : line.select("td.coluna-v"))
                            v = Integer.parseInt(column.text());

                        equipe.put("V", v);

                        /* Recupera o número de empates. */
                        for (Element column : line.select("td.coluna-e"))
                            e = Integer.parseInt(column.text());

                        equipe.put("E", e);

                        /* Recupera o número de derrotas. */
                        for (Element column : line.select("td.coluna-d"))
                            d = Integer.parseInt(column.text());

                        equipe.put("D", d);

                        /* Recupera o número de gols marcados. */
                        for (Element column : line.select("td.coluna-gp"))
                            gp = Integer.parseInt(column.text());

                        equipe.put("GP", gp);

                        /* Recupera o número de gols sofridos. */
                        for (Element column : line.select("td.coluna-gc"))
                            gc = Integer.parseInt(column.text());

                        equipe.put("GC", gc);

                        /* Faz um assert nos valores recuperados. */
                        // Test.assertDifferentString(participante, "");
                        // Test.assertDifferentInt(pg, Integer.MIN_VALUE);
                        // Test.assertDifferentInt(j, Integer.MIN_VALUE);
                        // Test.assertDifferentInt(v, Integer.MIN_VALUE);
                        // Test.assertDifferentInt(e, Integer.MIN_VALUE);
                        // Test.assertDifferentInt(d, Integer.MIN_VALUE);
                        // Test.assertDifferentInt(gp, Integer.MIN_VALUE);
                        // Test.assertDifferentInt(gc, Integer.MIN_VALUE);

                        /* Calcula o saldo de gols. */
                        sg = (gp - gc);

                        equipe.put("SG", sg);

                        classificacao.add(equipe);

                        tableString += (participante + " " + pg + " " + j +
                            " " + v + " " + e + " " + d + " " + gp + " " + gc + " " + sg + "\n");
                    }

                    if (empty)
                    {
                        System.out.println("Empty table...");
                        campeonato.put("empty", true);
                    }
                    else
                    {
                        campeonato.put("empty", false);
                        campeonato.put("classificacao", classificacao);

                        writer.write(campeonato.toJSONString());
                        writer.flush();
                    }

                    // writer.append(tableString + "\n");

                    writer.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }  
            }
        }
    }

    public static void parseOGol(String html, String url) {
        Document doc = Jsoup.parse(html);
        
        /* Extract important elements. */
        Element topDiv = doc.selectFirst("div.top");
        Element factSheet = topDiv.selectFirst("div.factsheet");
        Element divContent = doc.selectFirst("div#page_content");

        /* Select correct edition of competition. */
        String title = factSheet.selectFirst("span.name").text();
        HashMap<String, Team> teams = new HashMap<>();

        try {
            JSONParser parser = new JSONParser();

            /* JSON Objects. */
            JSONObject campeonato = new JSONObject();
            JSONArray classificacao = new JSONArray();

            JSONObject obj = (JSONObject) parser.parse(new FileReader(System.getProperty("user.dir") + "/temp/out.json"));
            JSONArray campeonatos = (JSONArray) obj.get("campeonatos");

            FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/temp/out.json", false);

            campeonato.put("name", title);
            campeonato.put("url", url);
            campeonato.put("empty", false);

            /* Recupera as tabelas de classificacao. */
            for (Element box : divContent.select("div.box")) {
                Element table = box.selectFirst("table.zztable");

                /* Skip unwanted containers. */
                if (table == null)
                    continue;

                /* Main table or auxiliary ones? */
                if (table.hasAttr("id") && table.id().equals("datatable")) {
                    for (Element line : table.select("tr")) {
                        Elements cells = line.select("td");
                        Team team;

                        /* Avoids unwanted lines. */
                        if (cells.size() == 0)
                            continue;

                        String name = cells.get(2).text();
                        int p  = Integer.parseInt(cells.get(3).text());
                        int j  = Integer.parseInt(cells.get(4).text());
                        int v  = Integer.parseInt(cells.get(5).text());
                        int e  = Integer.parseInt(cells.get(6).text());
                        int d  = Integer.parseInt(cells.get(7).text());
                        int gp = Integer.parseInt(cells.get(8).text());
                        int gc = Integer.parseInt(cells.get(9).text());

                        /* Already registered? */
                        if ((team = teams.get(name)) == null) {
                            team = new Team(name);
                            teams.put(name, team);
                        }

                        team.updatePoints(p);
                        team.updateGames(j);
                        team.updateVictories(v);
                        team.updateDraws(e);
                        team.updateLosses(d);
                        team.updateGoalsPro(gp);
                        team.updateGoalsAgainst(gc);
                    }
                } else {
                    for (Element line : table.select("tr")) {
                        Elements cells = line.select("td");
                        Team team1, team2;
                        int goals1, goals2;

                        /* Skip unwanted tags. */
                        if (cells.size() == 0)
                            continue;

                        // String name1 = line.selectFirst("td.text.home").text();
                        // String name2 = line.selectFirst("td.text.away").text();
                        String name1 = cells.get(0).text();
                        String name2 = cells.get(2).text();

                        if ((team1 = teams.get(name1)) == null) {
                            team1 = new Team(name1);
                            teams.put(name1, team1);
                        }

                        if ((team2 = teams.get(name2)) == null) {
                            team2 = new Team(name2);
                            teams.put(name2, team2);
                        }

                        /* Checks the results of all played matches. */
                        for (Element cell : line.select("td.result")) {
                            /* Removes unnecessary spaces and splits the result string. */
                            String[] temp = cell.text().split(" ");
                            String[] goals = temp[0].split("-");

                            goals1 = Integer.parseInt(goals[0]);
                            goals2 = Integer.parseInt(goals[1]);

                            /* Update goals statistics. */
                            team1.updateGoalsPro(goals1);
                            team1.updateGoalsAgainst(goals2);
                            team2.updateGoalsPro(goals2);
                            team2.updateGoalsAgainst(goals1);

                            /* Victory of home team? */
                            if (goals1 > goals2) {
                                team1.incVictories();
                                team2.incLosses();
                            } else if (goals2 > goals1) {  // Victory of away team
                                team1.incLosses();
                                team2.incVictories();
                            } else {  // Draw.
                                team1.incDraws();
                                team2.incDraws();
                            }
                        }
                    }
                }
            }

            /* Adds all teams to the final classification. */
            for (Team team : teams.values()) {
                classificacao.add(team.toJsonObject());
                System.out.println(team.toString());
            }

            campeonato.put("classificacao", classificacao);

            campeonatos.add(campeonato);

            /* Appends the competition to the output file. */
            writer.write(obj.toJSONString());
            writer.flush();

            /* Closes the output writer. */
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Set<String> getExternalLinks(String html, String baseUri) {
        Document doc = Jsoup.parse(html, baseUri);
        Set<String> links = new HashSet<String>();

        for (Element link : doc.select("a")) {
            links.add(link.absUrl("href"));
        }

        return (links);
    }
}
