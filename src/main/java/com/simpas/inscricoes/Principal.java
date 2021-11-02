
package com.simpas.inscricoes;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Principal {
    
    public static void main(String[] args) throws IOException{
        
       Table tabela = new Table("/home/fernando/Desktop/simbolosGpas/emaisTotal.csv",",");    
        
       Inscricoes inscricoes =  new Inscricoes(tabela,"Palestra","Simposio","Retornados");
       
       //Table.readFile("/home/fernando/Desktop/simbolosGpas/emaisTotal.csv",",");
       
       //tabela.printTable(tabela);
       
      Map<String,List<String>> otimo = tabela.getTabela();
      
      /*
       for(Map.Entry e:otimo.entrySet()){          
               
           System.out.println(e.getKey()+":"+e.getValue());
           
       }*/
       
       inscricoes.resume(otimo, "Palestra", "Simposio");
       
       //inscricoes.getTable().forEach(x->System.out.println(Arrays.toString(x)));
       
       //inscricoes.salvarCSV("/home/fernando/Desktop/simbolosGpas/resultado21-10.csv");
       
       //inscricoes.getTable().stream().forEach(x->System.out.println(Arrays.toString(x)));
       
       //inscricoes.duplicados("Simposio").forEach(System.out::println);
       List<String[]> tabelaformat = Table.formatToSave(Inscricoes.getOtimoTable());
       
       for(String[] i:tabelaformat){
           
           System.out.println(Arrays.toString(i));
           
       }
       
       //Table.salvarCSV(tabelaformat,"/home/fernando/Desktop/simbolosGpas/NOVATABELA.csv");
       
       //inscricoes.soPalestra().forEach(System.out::println);
       
       //inscricoes.duplicados("Simposio").forEach(System.out::println); 
       
        
    }
    
}
