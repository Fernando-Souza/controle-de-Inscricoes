package com.simpas.inscricoes;

import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Inscricoes {

    private static Map<String, List<String>> otimoTable = new HashMap<>();
    private static Table tabela;
    private String coluna1;
    private String coluna2;
    private String retornados;

    public Inscricoes(Table tabela, String colname1, String colname2, String retornados) {

        this.tabela = tabela;
        this.coluna1 = colname1;
        this.coluna2 = colname2;
        this.retornados = retornados;
        this.otimoTable = otimizaTable(tabela, retornados);

    }

    public static Map<String, List<String>> otimizaTable(Table tabela, String remover) {

        Map<String, List<String>> auxTable = new HashMap<>();

        tabela.getTabela().keySet().forEach(s -> {
            List<String> aux = tabela.getTabela().get(s).stream()
                    .map(x -> x.trim().toLowerCase())
                    .distinct()
                    .filter(x -> tabela.getTabela().get(remover)
                    .stream()
                    .map(y -> y.trim().toLowerCase())
                    .distinct()
                    .noneMatch(z -> x.equals(z)))
                    .collect(Collectors.toList());

            auxTable.put(s, aux);
        });

        return auxTable;

    }

    public static Map<String, List<String>> removeDuplicados(Table tabela) {

        Map<String, List<String>> auxTable = new HashMap<>();

        tabela.getTabela().keySet().forEach(s -> {
            List<String> aux = tabela.getTabela().get(s).stream()
                    .map(x -> x.trim().toLowerCase())
                    .distinct()
                    .collect(Collectors.toList());

            auxTable.put(s, aux);
        });

        return auxTable;
    }

    public static Map<String, List<String>> getOtimoTable() {
        return otimoTable;
    }

    private List<String[]> ToSave(Map<String, List<String>> tabela) {

        List<String> palestra = tabela.get(coluna1);
        List<String> simposio = tabela.get(coluna2);
        String[] head = tabela.keySet().stream().filter(x -> !x.equals("Retornados")).toArray(String[]::new);

        Collections.sort(palestra);
        Collections.sort(simposio);

        List<String[]> finalList = IntStream.range(0, Math.max(palestra.size(), simposio.size())).
                mapToObj(i -> {
                    if (i >= Math.min(palestra.size(), simposio.size())) {

                        if (palestra.size() > simposio.size()) {

                            return new String[]{palestra.get(i), null};

                        } else {

                            return new String[]{null, simposio.get(i)};
                        }

                    } else {

                        return new String[]{palestra.get(i), simposio.get(i)};
                    }

                }).collect(Collectors.toList());

        finalList.add(0, head);

        return finalList;
    }

    public List<String[]> formatToSave(Map<String, List<String>> tabela) {

        int maxnrows = tabela.entrySet().stream().mapToInt(r -> r.getValue().size()).max().getAsInt();
        int maxncols = tabela.keySet().stream().mapToInt(x -> 1).sum();
        String[] colnames = tabela.keySet().toArray(new String[maxncols]);
        
        
        List<String[]> newtab = new ArrayList<>();
        newtab.add(colnames);

        int linha = 0;

        ctrlLinha:
        while (linha < maxnrows) {

            Iterator<String> colIterator = tabela.keySet().iterator();
            String[] newrow = new String[maxncols];
            int coluna = 0;

            colname:
            while (colIterator.hasNext()) {                
                
                String email = colIterator.next();

                xcoluna:
                while (coluna < maxncols) {

                    if (tabela.get(email).isEmpty()) {
                        newrow[coluna] = null;
                        continue colname;
                    } else {

                        if (linha >= tabela.get(email).size()) {

                            newrow[coluna] = null;

                        } else {

                            newrow[coluna] = tabela.get(email).get(linha);

                        }

                        if (colIterator.hasNext()) {                            
                            coluna++;
                            continue colname;
                        } else {

                            newtab.add(newrow);
                            newrow = null;
                            linha++;
                            continue ctrlLinha;

                        }

                    }

                }
            }
        }

        return newtab;
    }

    public List<String> ambos() {

        List<String> aux1 = otimoTable.get(coluna1);
        List<String> aux2 = otimoTable.get(coluna2);

        return aux1.stream()
                .filter(x -> aux2.stream()
                .anyMatch(y -> x.equals(y)))
                .collect(Collectors.toList());

    }

    public List<String> soPalestra() {

        List<String> aux1 = otimoTable.get(coluna1);
        List<String> aux2 = otimoTable.get(coluna2);

        return aux1.stream()
                .filter(x -> !ambos().stream().
                anyMatch(y -> y.equals(x)))
                .collect(Collectors.toList());

    }

    public Set<String> duplicados(String coluna) {

        Map<String, List<String>> tb = tabela.getTabela();

        Set<String> duplicados = tb.get(coluna).stream()
                .map(x -> x.trim().toLowerCase())
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting())).
                entrySet().stream()
                .filter(x -> x.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());;

        return duplicados;

    }

    private Map<String, List<String>> retornados(Map<String, List<String>> tabela) {

        Map<String, List<String>> tb = tabela;

        List<String> palestra = tb.get(coluna1);
        List<String> simposio = tb.get(coluna2);

        List<String> emailsretornados = new ArrayList<>();

        List<String> retornadosPal = palestra.stream()
                .map(x -> x.trim().toLowerCase())
                .distinct()
                .filter(x -> tb.get(retornados)
                .stream()
                .map(y -> y.toLowerCase())
                .distinct()
                .anyMatch(z -> x.equals(z)))
                .collect(Collectors.toList());

        List<String> retornadosSimp = simposio.stream()
                .map(x -> x.trim().toLowerCase())
                .distinct()
                .filter(x -> tb.get(retornados)
                .stream()
                .map(y -> y.toLowerCase())
                .distinct()
                .anyMatch(z -> x.equals(z)))
                .collect(Collectors.toList());

        Map<String, List<String>> retornados = new HashMap<>();

        retornados.put("Palestra", retornadosPal);
        retornados.put("Simposio", retornadosSimp);

        return retornados;

    }

    public void resume() {

        int atingidos = soPalestra().size() + otimoTable.get("Simposio").size();

        double conversao = (otimoTable.get("Simposio").size() / (double) atingidos) * 100;

        System.out.println("=======================");
        System.out.print("Resumo dos dados\n");
        System.out.println("=======================");
        otimoTable.keySet().forEach(x -> {
            System.out.printf("%s : %d \n", x, otimoTable.get(x).size());
        });
        System.out.println("--------------------------------------");
        System.out.print("Total de emails duplicados(simposio): " + duplicados("Simposio").size() + "\n");
        System.out.print("Total de emails duplicados(palestra): " + duplicados("Palestra").size() + "\n");
        System.out.println("******************************");
        System.out.print("Inscritos no Simpósio e Palestra: " + ambos().size() + "\n");
        System.out.print("Inscritos somente na palestra: " + soPalestra().size() + "\n");
        System.out.print("Total de emails retornados (Palestra): " + (retornados(otimoTable).get("Palestra").size()) + "\n");
        System.out.print("Total de emails retornados (Simposio): " + (retornados(otimoTable).get("Simposio").size()) + "\n");
        System.out.println("******************************");
        System.out.print("Total de pessoas atingidas: " + atingidos + "\n");
        System.out.printf("Taxa de conversao para o simpósio: %.2f %%\n", conversao);
        System.out.println("=======================");
    }

    public void printRetornados() {

        retornados(otimoTable).entrySet().stream().forEach(x -> System.out.println("[" + x.getKey() + "=" + x.getValue() + "]"));

    }

}
