package com.persistentbit.sql.statement;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * User: petermuys
 * Date: 18/06/16
 * Time: 18:54
 */
public class SqlLoader {
    static private final Logger log = Logger.getLogger(SqlLoader.class.getName());
    private final Map<String,List<String>> snippets    =   new LinkedHashMap<>();
    private final String resourcePath;

    public SqlLoader(String resourcePath){
        if(resourcePath.startsWith("/") == false){
            resourcePath = "/db/" + resourcePath;
        }
        this.resourcePath = resourcePath;
        InputStream in = SqlLoader.class.getResourceAsStream(resourcePath);
        if(in == null){
            log.warning("Can't find Sql resource '" + resourcePath + "'");
        } else {
            load(in);
        }
    }

    public String getOne(String name){
        List<String> result = getAll(name);
        if(result.size() != 1){
            throw new IllegalArgumentException("Found "+ result.size() +  " statements, expected just 1 for  '" + name + "' in  '" + resourcePath  + "'");
        }
        return result.get(0);
    }

    public List<String> getAll(String name){
        List<String> result = snippets.get(name);
        if(result == null){
            throw new IllegalArgumentException("Can't find snippet '" + name + "' in  '" + resourcePath  + "'");
        }
        return result;
    }

    public Collection<String> getAllSnippetNames() {
        return snippets.keySet();
    }


    public void load(InputStream in){
        try(BufferedReader r = new BufferedReader(new InputStreamReader(in))){
            String name = null;
            String current = null;
            Map<String,List<String>> fileSnippets = new HashMap<>();
            BiConsumer<String,String> toSnippets = (n, c) -> {
                if(n != null && c != null){
                    List<String> existing = fileSnippets.get(n);
                    if(existing == null){
                        existing = new ArrayList<>();
                        fileSnippets.put(n,existing);
                    }
                    existing.add(c);
                }
            };
            for(String line : r.lines().collect(Collectors.toList())){
                if(line.trim().startsWith("-->>")){
                    toSnippets.accept(name,current);
                    current = null;
                    name = line.trim().substring(4).trim().toLowerCase();
                    if(name.isEmpty()){
                        name = null;
                    } else {
                        log.fine("-->>" + name);
                    }
                } else {
                    if (name != null) {
                        toSnippets.accept(name,line);
                        //current = current == null ? line : current + "\n" + line;
                    }
                }
            }
            toSnippets.accept(name,current);

            for(Map.Entry<String,List<String>> entry : fileSnippets.entrySet()){
                List<String> allCurrent = new ArrayList<>();
                String delimiter = ";";
                current= "";
                for(String line : entry.getValue()){
                    line = line.trim();
                    if(line.toUpperCase().startsWith("DELIMITER")){
                        delimiter = line.substring("delimiter".length()).trim();
                        continue;
                    }
                    if(current.isEmpty() == false){
                        current += "\r\n";
                    }
                    current += line;
                    if(current.trim().endsWith(delimiter)){
                        allCurrent.add(current);
                        current = "";

                    }
                }
                if(current.trim().isEmpty() == false){
                    allCurrent.add(current);
                }

                snippets.put(entry.getKey(),allCurrent);

            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }

    }

    static public void main(String...args){
        SqlLoader l = new SqlLoader("/dbupdates/create_nextid.sql");

    }
}
