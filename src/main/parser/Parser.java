package main.parser;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

                        writer.append(campeonato.toJSONString());
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
        Element combo = topDiv.selectFirst("div.combo");
        Element factSheet = topDiv.selectFirst("div.factsheet");

        if (combo != null && factSheet != null)
            System.out.println("Found factsheet and combo box");

        /* Select correct edition of competition. */

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
                    //tableString += urlPieces[4] + " " + urlPieces[5] + "\n";

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

                        writer.append(campeonato.toJSONString());
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

    public static Set<String> getExternalLinks(String html, String baseUri) {
        Document doc = Jsoup.parse(html, baseUri);
        Set<String> links = new HashSet<String>();

        for (Element link : doc.select("a")) {
            links.add(link.absUrl("href"));
        }

        return (links);
    }
}
