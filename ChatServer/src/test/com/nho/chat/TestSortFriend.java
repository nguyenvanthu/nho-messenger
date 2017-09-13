package test.com.nho.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TestSortFriend {
	public static void main(String[] args) {
		TestSortFriend test = new TestSortFriend();
		Map<String, Long> friends = new HashMap<>();
		friends.put("thunv", 10L);
		friends.put("hiepnd", 5L);
		friends.put("phuonglm", 100L);
		friends.put("thuonglt", 10000L);
		friends.put("vanbt", 300L);
		friends.put("thuanvd", 100L);
		
		List<String> userNames = new ArrayList<>();
		friends = test.sortByTime(friends);
		for (Entry<String, Long> entry : friends.entrySet()) {
			System.out.println(entry.getKey() + " - " + entry.getValue());
			userNames.add(entry.getKey());
		}
		Collections.sort(userNames);
		for(String userName : userNames){
			System.out.println(userName);
		}
	}

	private Map<String, Long> sortByTime(Map<String, Long> priortyOfFriends) {
		List<Map.Entry<String, Long>> list = new LinkedList<>(priortyOfFriends.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {

			@Override
			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return (o1.getValue().compareTo(o2.getValue()));
			}
		});
		Map<String, Long> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<String, Long> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
