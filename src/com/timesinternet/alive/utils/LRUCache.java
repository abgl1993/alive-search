package com.timesinternet.alive.utils;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class LRUCache {

	private final int maxSize;

	private ConcurrentMap<String, String > lruCache;

	private ConcurrentLinkedQueue<String> queue;

	public LRUCache(int maxSize) {
		this.maxSize = maxSize;
		lruCache = new ConcurrentHashMap<String, String >();
		queue = new ConcurrentLinkedQueue<String>();
	}

	public void put(String key, String  value) {

		if (queue.size() >= maxSize) {
			queue.remove(0);
			queue.add(key);
		} else {
			queue.add(key);
		}

		lruCache.put(key, value);
	}

	public String get(String key) {
 
		String value = null;
		if(queue.contains(key)){
			value=lruCache.get(key);
		}
		return value;
	}

	public void displayCache(){
		System.out.println("size: "+lruCache.size());
		Set<Entry<String,String>> entrySet= lruCache.entrySet();
		Iterator iterator=entrySet.iterator();
		while(iterator.hasNext()){
			Entry<String,String>entry=(Entry<String, String>) iterator.next();
			String key=entry.getKey();
			System.out.println("key: "+key);
		}
	    System.out.println(lruCache);
	}

	public void clearCache() {
		lruCache.clear();
		queue.clear();
	}

	public void size() {
		System.out.println("Cache Size: "+queue.size());
		
	}
}
