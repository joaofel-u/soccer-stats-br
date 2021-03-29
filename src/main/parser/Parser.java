package main.parser;

import main.test.Test;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Parser {
    public static void printTable(String html, String url) {
        Document doc = Jsoup.parse(html);
        String urlPieces[] = url.split("/");

        /* Recupera a tabela de classificacao. */
        for (Element divClassificacao : doc.select("div#classificacao")) {
            for (Element tabelaClassificacao : divClassificacao.select("table#table_classificacao")) {
                Boolean empty = true;
                String tableString = "";

                try {
                    FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/temp/out.txt", true);
                    tableString += urlPieces[4] + " " + urlPieces[5] + "\n";

                    /* Percorre a tabela. */
                    for (Element line : tabelaClassificacao.select("tr.linha-class")) {
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

                        /* Recupera o número de pontos ganhos. */
                        for (Element column : line.select("td.coluna-pg"))
                            pg = Integer.parseInt(column.text());

                        /* Recupera o número de jogos disputados. */
                        for (Element column : line.select("td.coluna-j"))
                            j = Integer.parseInt(column.text());

                        /* Recupera o número de vitorias. */
                        for (Element column : line.select("td.coluna-v"))
                            v = Integer.parseInt(column.text());

                        /* Recupera o número de empates. */
                        for (Element column : line.select("td.coluna-e"))
                            e = Integer.parseInt(column.text());

                        /* Recupera o número de derrotas. */
                        for (Element column : line.select("td.coluna-d"))
                            d = Integer.parseInt(column.text());

                        /* Recupera o número de gols marcados. */
                        for (Element column : line.select("td.coluna-gp"))
                            gp = Integer.parseInt(column.text());

                        /* Recupera o número de gols sofridos. */
                        for (Element column : line.select("td.coluna-gc"))
                            gc = Integer.parseInt(column.text());

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

                        tableString += (participante + " " + pg + " " + j +
                            " " + v + " " + e + " " + d + " " + gp + " " + gc + " " + sg + "\n");
                    }

                    if (empty)
                        System.out.println("Empty table...");

                    writer.append(tableString + "\n");

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
            if (links.add(link.absUrl("href")))
                System.out.println("Added absUrl : " + link.absUrl("href"));
        }

        return (links);
    }
}
