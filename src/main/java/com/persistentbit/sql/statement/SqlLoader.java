package com.persistentbit.sql.statement;


import com.persistentbit.core.Nothing;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.POrderedMap;
import com.persistentbit.core.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Loads SQL statement from a java resource.<br>
 * The SQL statements are grouped and named by using:<br>
 * {@code
 * -->><NameOfTheGroup>
 * SQL statements separated by ';'
 * -->>
 * }
 *
 * @author Peter Muys
 * @since 18/06/16
 */
public class SqlLoader{

	private final String resourcePath;
	private POrderedMap<String, PList<String>> snippets = POrderedMap.empty();

	/**
	 * Create a new SqlLoader for the given resource.
	 *
	 * @param resourcePath The resource name
	 */
	public SqlLoader(String resourcePath) {
		this.resourcePath = resourcePath;
		Log.function(resourcePath).code(log -> {
			InputStream in = SqlLoader.class.getResourceAsStream(resourcePath);
			if(in == null) {
				log.error("Can't find Sql resource '" + resourcePath + "'");
			}
			else {
				load(in);
			}
			return Nothing.inst;
		});

	}

	@Override
	public String toString() {
		return "SqlLoader[" + resourcePath + "]";
	}

	private void load(InputStream in) {
		try(BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
			String                    name         = null;
			Map<String, List<String>> fileSnippets = new LinkedHashMap<>();
			BiConsumer<String, String> toSnippets = (n, c) -> {
				if(n != null && c != null) {
					List<String> existing = fileSnippets.get(n);
					if(existing == null) {
						existing = new ArrayList<>();
						fileSnippets.put(n, existing);
					}
					existing.add(c);
				}
			};
			for(String line : r.lines().collect(Collectors.toList())) {
				if(line.trim().startsWith("-->>")) {
					toSnippets.accept(name, null);
					name = line.trim().substring(4).trim().toLowerCase();
					if(name.isEmpty()) {
						name = null;
					}
				}
				else {
					if(name != null) {
						toSnippets.accept(name, line);
						//current = current == null ? line : current + "\n" + line;
					}
				}
			}
			toSnippets.accept(name, null);

			for(Map.Entry<String, List<String>> entry : fileSnippets.entrySet()) {
				List<String> allCurrent = new ArrayList<>();
				String       delimiter  = ";";
				String       current    = "";
				for(String line : entry.getValue()) {
					line = line.trim();
					if(line.toUpperCase().startsWith("DELIMITER")) {
						delimiter = line.substring("delimiter".length()).trim();
						continue;
					}
					if(current.isEmpty() == false) {
						current += "\r\n";
					}
					current += line;
					if(current.trim().endsWith(delimiter)) {
						allCurrent.add(current.substring(0, current.length() - delimiter.length()));
						current = "";

					}
				}
				if(current.trim().isEmpty() == false) {
					allCurrent.add(current);
				}

				snippets = snippets.put(entry.getKey(), PList.from(allCurrent));

			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Get all the SQL statements for a given group name
	 *
	 * @param name The name of the statement group
	 *
	 * @return All statements for the group
	 *
	 * @throws IllegalArgumentException when there is no group with the given name
	 * @see #hasSnippet
	 */
	public PList<String> getAll(String name) {
		return snippets.getOpt(name.toLowerCase())
					   .orElseThrow(() -> new IllegalArgumentException("Can't find snippet '" + name + "' in  '" + resourcePath + "'"));
	}

	/**
	 * Check if a given group(snippet) name exists.<br>
	 *
	 * @param name The name of the group(snippet)
	 *
	 * @return true if group exists
	 */
	public boolean hasSnippet(String name) {
		return snippets.containsKey(name.toLowerCase());
	}

	/**
	 * Get all group(snippet) names.<br>
	 *
	 * @return The list of names
	 */
	public PList<String> getAllSnippetNames() {
		return snippets.keys().plist();
	}


}
