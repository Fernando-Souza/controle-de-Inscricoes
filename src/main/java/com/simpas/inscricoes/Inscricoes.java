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
    
    public static Map<String, List<String>> removerdaTabela(Table tabela, String toRemover) {

        Map<String, List<String>> auxTable = new HashMap<>();

        tabela.getTabela().keySet().forEach(s -> {
            List<String> aux = tabela.getTabela().get(s).stream()
                    .map(x -> x.trim().toLowerCase())                    
                    .filter(x -> tabela.getTabela().get(toRemover)
                    .stream()
                    .map(y -> y.trim().toLowerCase())                    
                    .noneMatch(z -> x.equals(z)))
                    .collect(Collectors.toList());

            auxTable.put(s, aux);
        });

        return auxTable;

    }

    public static Map<String, List<String>> getOtimoTable() {
        return otimoTable;
    }    
    
    public List<String> ambos(Map<String,List<String>> tabela,String coluna1, String coluna2) {

        List<String> aux1 = tabela.get(coluna1);
        List<String> aux2 = tabela.get(coluna2);

        return aux1.stream()
                .filter(x -> aux2.stream()
                .anyMatch(y -> x.equals(y)))
                .collect(Collectors.toList());

    }

    public List<String> soPalestra(Map<String,List<String>> tabela,String palestra, String simposio) {

        List<String> aux1 = tabela.get(palestra);
        List<String> aux2 = tabela.get(simposio);

        return aux1.stream()
                .filter(x -> !ambos(tabela,palestra,simposio).stream().
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

    public void resume(Map<String,List<String>> tabela,String palestra, String simposio) {

        int atingidos = soPalestra(tabela,palestra,simposio).size() + otimoTable.get("Simposio").size();

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
        System.out.print("Inscritos no Simpósio e Palestra: " + ambos(tabela,palestra,simposio).size() + "\n");
        System.out.print("Inscritos somente na palestra: " + soPalestra(tabela,palestra,simposio).size() + "\n");
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
