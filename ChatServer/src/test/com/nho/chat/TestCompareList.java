package test.com.nho.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestCompareList {
	public static void main(String[] args) {
		List<String> num = new ArrayList<>();
		num.add("105748046554827");
		num.add("113795192415050");
		List<String> number = new ArrayList<>();
		number.add("113795192415050");
		number.add("105748046554827");
		Collections.sort(number);
		Collections.sort(num);
		if(num.equals(number)){
			System.out.println("2 list is equal");
		}else {
			System.out.println("2 list is difference");
		}
	}
}
