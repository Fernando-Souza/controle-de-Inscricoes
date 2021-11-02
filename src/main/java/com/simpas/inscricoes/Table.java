/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpas.inscricoes;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Table {
    
    
    private static ArrayList<List<String>> table;
    
    String path;
    String sep;
    
    public Table(String path,String sep) throws IOException{
        
        this.table = readFile(path,sep);
        this.path=path;
        this.sep=sep;
        
    }
    
    
    public static ArrayList<List<String>> readFile(String path,String sep){     
         
        
        try(Stream<String> lines =  Files.lines(Path.of(path),Charset.defaultCharset())){       
           
                
              ArrayList<List<String>> auxTable = lines.map(line -> line.split(sep,-1))
                      .map(x-> Arrays.asList(x))
                      .collect(Collectors.toCollection(ArrayList::new));                
            
            return auxTable;
            
        }catch(IOException io){
            
            System.out.println(io.getMessage());
            
            return null;
            
        }    
        
        
    }
    
        
    private LinkedHashMap<String,List<String>> makeTable(List<List<String>> tabela){
        
        LinkedHashMap<String,List<String>> aux = new LinkedHashMap<>();
        
        List<String> columnName = tabela.get(0);
             
        
        tabela.subList(1,tabela.size()).stream().forEach(linha -> {
            
                IntStream.range(0,linha.size()).forEach(i->{                   
                    
                    
                    if(aux.containsKey(columnName.get(i))){
                        
                        aux.get(columnName.get(i)).add(linha.get(i));
                        
                    }else{
                        
                        aux.put(columnName.get(i),new ArrayList<String>());
                        aux.get(columnName.get(i)).add(linha.get(i));
                    }
                    
                    });        
            
        });
        
        
       return aux;
        
            
        }
    
    public LinkedHashMap<String,List<String>> getTabela(){
        
        return makeTable(table);
    }
 
    public static  List<String[]> formatToSave(Map<String, List<String>> tabela) {

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
      
    
    public static void salvarCSV(List<String[]> tabela,String path){
        
        try(CSVWriter writer = new CSVWriter(new FileWriter(path))){
            
            writer.writeAll(tabela);
            
            
        } catch (IOException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public void printTable(Table tab){
        
        
        for(Map.Entry s:tab.getTabela().entrySet()){
            
        
        System.out.println(s.getKey()+"::"+s.getValue());
        System.out.println("*--*--*--*--*--*--*--*--*--*");           
        
        
        }
     
    }    
    
}
