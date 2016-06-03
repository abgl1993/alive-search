package com.timesinternet.alive.search.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;
import com.timesinternet.alive.utils.CacheBuilder;
import com.timesinternet.alive.utils.LRUCache;

/**
 * @author Atul.Baghel
 *
 */
public class DataBaseServices {

	String userName = "aliveops";
	String password = "aliveops@20";
	String databaseName = "alive";
	ResultSet resultSet = null;
	String word = "";
	List<String> master_category1 = null;
	List<String> master_category2 = null;
	List<List<String>> categoryList = null;
	String value = null;

	DataBase database;
	CacheBuilder cacheBuilder;
	LRUCache cache;
	boolean foundInCache = false;

	PreparedStatement statement = null;

	public List<List<String>> getData(String[] keywords) {

		cacheBuilder = new CacheBuilder();
		master_category1 = new ArrayList<String>();
		master_category2 = new ArrayList<String>();

		cache = (LRUCache) cacheBuilder.getCache("lrucache");

		cache.size();
		for (int i = 0; i < keywords.length; i++) {

			value = cache.get(keywords[i].trim());
			if (value != null && value.equals("##") != true) {
				String categoryArray[] = value.split("##");
				foundInCache = true;
				System.out.println(categoryArray.length);
				if (categoryArray.length > 0) {
					if (master_category1.contains(categoryArray[0]) != true) {
						master_category1.add(categoryArray[0]);
					}
				}
				if (categoryArray.length > 1) {
					if (master_category2.contains(categoryArray[1]) != true) {
						master_category2.add(categoryArray[1]);
					}
				}

			}

		}
		if (!foundInCache) {
			categoryList = getDataFromDataBase(keywords);
		}

		if (categoryList == null) {
			System.out.println("Keyword found in cache");
			categoryList = new ArrayList<List<String>>();
			categoryList.add(master_category1);
			categoryList.add(master_category2);
		}
		return categoryList;
	}

	private List<List<String>> getDataFromDataBase(String[] keywords) {
		database = new DataBase();
		master_category1 = new ArrayList<String>();
		master_category2 = new ArrayList<String>();
		List<List<String>> categoryList = new ArrayList<List<String>>();

		Connection con = database.getConnection(userName, password);

		try {

			for (int i = 0; i < keywords.length; i++) {
				if (con != null) {
					statement = (PreparedStatement) con
							.prepareStatement("select keywords, master_cat1,master_cat2 from os_suggestive_search where keywords = ?");
				}
				if (statement != null) {
					statement.setString(1, keywords[i]);

					resultSet = statement.executeQuery();
				}
				while (resultSet != null && resultSet.next()) {

					cache.put(keywords[i], resultSet.getString(2) + "##" + resultSet.getString(3));
					if (master_category1.contains(resultSet.getString(2)) != true) {
						master_category1.add(resultSet.getString(2));
					}
					if (master_category2.contains(resultSet.getString(3)) != true) {
						master_category2.add(resultSet.getString(3));
					}

				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		try {
			if (con != null) {
				con.close();
			} else {
				System.out.println("No database connection to close");
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		categoryList.add(master_category1);
		categoryList.add(master_category2);
		return categoryList;

	}

	public void buildInitialCache(LRUCache lruCache) {
		database = new DataBase();
		cacheBuilder = new CacheBuilder();
		Connection con = database.getConnection(userName, password);
		cache = (LRUCache) cacheBuilder.getCache("lrucache");
		try {

			if (con != null) {
				statement = (PreparedStatement) con
						.prepareStatement("select keywords,master_cat1,master_cat2 from os_suggestive_search where id > ? Limit 51");
			}
			if (statement != null) {
				statement.setString(1, "200");

				resultSet = statement.executeQuery();
			}
			while (resultSet != null && resultSet.next()) {
				String keyword = resultSet.getString(1);
				String master_category1 = resultSet.getString(2);
				String master_category2 = resultSet.getString(3);
				cache.put(keyword, master_category1 + "##" + master_category2);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		try {
			if (con != null) {
				con.close();
			} else {
				System.out.println("No database connection to close ## Cache Can not be built");
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}
}
