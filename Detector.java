import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Detector {
	
	public static double PLAG_THRESHOLD_TEXT = 0.50;
	public static double PLAG_THRESHOLD_CODE = 0.50;
	
	public static ArrayList<String> tokens1 = new ArrayList<String>();
	public static ArrayList<String> tokens2 = new ArrayList<String>();
	public static String sf1 = "";
	public static String sf2 = "";
	
	public static HashSet<String> stopwords;
	
	public static boolean isCodeFile(File f) throws FileNotFoundException {
		boolean cpp_result = false;
		boolean java_result = false;
		
		ArrayList<Pattern> IDENTIFY_CPP_KEYWORDS = new ArrayList<Pattern>();
		IDENTIFY_CPP_KEYWORDS.add(Pattern.compile("#include <\\w+>"));
		IDENTIFY_CPP_KEYWORDS.add(Pattern.compile("(int|void|char|long|byte|bool) \\w*\\W*"));
		
		ArrayList<Pattern> IDENTIFY_JAVA_KEYWORDS = new ArrayList<Pattern>();
		IDENTIFY_JAVA_KEYWORDS.add(Pattern.compile("public class \\w+\\W+(\\{)*"));
		IDENTIFY_JAVA_KEYWORDS.add(Pattern.compile("class \\w+\\W+(\\{)*"));
		IDENTIFY_JAVA_KEYWORDS.add(Pattern.compile("public static void main\\w+\\W+"));
		IDENTIFY_JAVA_KEYWORDS.add(Pattern.compile("import \\w+\\\\W+"));
		
		Scanner sc = new Scanner(f);
		
		while(sc.hasNextLine()) {
			String line = sc.nextLine();
			//check cpp code
			for(Pattern p : IDENTIFY_CPP_KEYWORDS) {
				if(!p.matcher(line).matches()) {
					cpp_result = false;
				}else {
					cpp_result = true;
					break;
				}
			}
			
			//check java code
			for(Pattern p : IDENTIFY_JAVA_KEYWORDS) {
				if(!p.matcher(line).matches()) {
					java_result = false;
				}else {
					java_result = true;
					break;
				}
			}
			
			if(cpp_result || java_result) {
				break;
			}
			
		}
		
		sc.close();
		
		if(!cpp_result && !java_result)
			return false;
		else
			return true;
	}
	
	public static double JaccardIndex(ArrayList<String> f1Tokens,ArrayList<String> f2Tokens) {
		
		if(f1Tokens.isEmpty() && f2Tokens.isEmpty()) {
			return 0;
		}
		
		double rvalue = 0;
		
		float match = 0;
		for(String s:f1Tokens) {
			if(f2Tokens.contains(s)) {
				match++;
			}
		}
		
		int length1 = f1Tokens.size();
		int length2 = f2Tokens.size();

		rvalue = (match)/((length1+length2)-match);
		return rvalue;
		
	}
	
	public static double LCSubsequence(ArrayList<String> s1,ArrayList<String> s2) {
		
		if(s1.isEmpty()||s2.isEmpty()) {
			return 0;
		}
		
		int m = s1.size();
		int n = s2.size();
		
		int[] dp = new int[s2.size()+1];
        for(int i = 0; i< s1.size(); i++){
            int prev = dp[0];
            for(int j = 1; j < dp.length; j++){
                int temp = dp[j];
                if(!s1.get(i).equals(s2.get(j-1))){
                    dp[j] = Math.max(dp[j-1], dp[j]);
                }else{
                    dp[j] = prev +1;
                }
                prev = temp;
            }
        }
        return (double)dp[dp.length-1]/Math.min(m,n);
	}
	
	
    public static double rabinKarpNgram(String s1,String s2,int n){
    	
    	if(s1.isEmpty() || s2.isEmpty()) {
    		return 0;
    	}
    	
    	int len = s1.length();
    	int start = 0;
    	int end=n;
    	
    	if(s1.length()<n) {
    		end = s1.length();
    	}
    	
    	int match=0;
    	while(len>=0) {
    		
    		String search = s1.substring(start,end);
    		
    		if(s2.contains(search)) {
    			match+=n;
    		}
    		if(n+end>s1.length()) {
    			start = end;
    			end = s1.length();
    		}else {
    			start = end;
    			end=n+end;
    		}
    		len-=n;
    	}
    	
    	double dice = (double)(2*match)/(s1.length()+s2.length());
    	
    	return dice;
    }
			   
		
	public static void tokenizeText(File f1,File f2) throws IOException {
		
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(f1))); 
		String line="";
		while((line = br1.readLine())!=null) {
			if(line.isEmpty())
				continue;
			
			line = line.replaceAll("[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)", "");
			line = line.replaceAll("[^\\sA-za-z0-9-]","").toLowerCase();
			tokens1.addAll(Arrays.asList(line.split("[\\s-]")));
			line = line.replaceAll("[\\s-]", "");
			sf1+=line;
		}
		br1.close();
		
		BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(f2))); 
		
		while((line = br2.readLine())!=null) {
			if(line.isEmpty())
				continue;
			
			line = line.replaceAll("[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)", "");
			line = line.replaceAll("[^\\sA-za-z0-9-]","").toLowerCase();
			tokens2.addAll(Arrays.asList(line.split("[\\s-]")));
			line = line.replaceAll("[\\s-]", "");
			sf2+=line;
		}
		br2.close();
		
		tokens1.removeAll(stopwords);
		tokens2.removeAll(stopwords);
	}
	
	public static String removeComments(File f) throws FileNotFoundException {
		String res = "";
		String temp = "";
		Scanner sc = new Scanner(f);
		Stack s = new Stack<String>();
		while(sc.hasNextLine()) {
	
			String line = sc.nextLine();
			
			if(line.contains("import") || line.contains("#include")) {
				continue;
			}
			
			if((line.contains("/**") || line.contains("/*")) && line.contains("*/")) {
				continue;
			}
			
			if(line.contains("/**") || line.contains("/*")){
				s.push("/**");
			}
			
			if(line.contains("*/")) {
				s.pop();
				continue;
			}

			if(s.isEmpty()) {
				if((line.indexOf("//") > line.lastIndexOf(";") && s.isEmpty())
						|| (line.contains("//") && !line.contains(";") && s.isEmpty())	
							) {
						temp+=line.substring(0,line.indexOf("//")).toLowerCase();
				}else {
					temp+=line.toLowerCase();
				}
			}
			
		}
		
		
		for(int i=0;i<temp.length();i++) {
			if(temp.charAt(i) != ' ' && temp.charAt(i)!=';' && temp.charAt(i)!='\t' && temp.charAt(i)!='\"' && temp.charAt(i)!='\'') {
				res+=temp.charAt(i);
			}
		}
		
		sc.close();
		return res;
	}

	public static void processTextDocument(File f1,File f2) throws IOException {
		
		tokenizeText(f1,f2);
	
		double rabinNgram = rabinKarpNgram(sf1, sf2,5);
		 
		double jaccard = JaccardIndex(tokens1, tokens2);
	
		double avg = (rabinNgram+jaccard)/2;
	
		if(f1.length()/1024 < 100 || f2.length()/1024 < 100) {
			double lcs_list = LCSubsequence(tokens1, tokens2);
			avg = (rabinNgram+jaccard+lcs_list)/3;
		}

		if(avg > PLAG_THRESHOLD_TEXT) {
			System.out.println(1);
		}else {
			System.out.println(0);
		}
		
	}
	
	public static void processCodeFiles(File f1,File f2) throws FileNotFoundException {
		sf1 = removeComments(f1);
		sf2 = removeComments(f2);
		
		double rabin = rabinKarpNgram(sf1, sf2, 5);
		
		if(rabin > PLAG_THRESHOLD_CODE) {
			System.out.println(1);
		}else {
			System.out.println(0);
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		stopwords = new HashSet<String>();
		stopwords.add("a");
		stopwords.add("an");
		stopwords.add("the");
		stopwords.add("am");
		stopwords.add("that");
		stopwords.add("to");
		stopwords.add("which");
		stopwords.add("who");
		stopwords.add("of");
		stopwords.add("is");
		
		File f1 = new File(args[0]);
		File f2 = new File(args[1]);
		
		if(!isCodeFile(f1) && !isCodeFile(f2)) {
			processTextDocument(f1, f2);
		}else{
			processCodeFiles(f1,f2);
		}
		
	}

}
