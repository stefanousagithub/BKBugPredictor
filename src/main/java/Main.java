package main.java;

import java.util.HashMap;
import java.util.Map;

import main.java.model.ClassInstance;

public class Main {
	public static void main(String[] args) throws CloneNotSupportedException {
		Map<String,  Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		map.put("c", 3);
		Integer a = map.get("a");
		if(a != null) System.out.println(a);
	}

}
