
# PlagarismDetector - Java

COMP 6651 Project Readme File – Plagiarism Detection

## Algorithms used in project

- Jaccard Similarity
	The Jaccard similarity measures the similarity between two sets of data  to see which members are shared and distinct. 
	J(A,B) = (Common words) / (Union of words) 


- N-gram Matching
N-gram is a contiguous sequence of n items from a given sample of text. In string 
similarity while comparing strings n-gram  of length k are computed and for string 1 
and matched against string 2 and some similarity metric is used to calculate the percentage 
of similarity. The value of k derives the efficiency of matching and can affect results. 
In this project n-gram similarity is calculated based in dice similarity:
Similarity: (2*number of match words)/(length of string 1 + length of string 2)
Time cmplexity: O(k*m)) – k patterns to be matched in string of length m

- Longest Common Subsequence
In this project LCS is implemented using the space optimized version using 1D array, dynamic programming 
approach where we store the previous value in a cell and overwrite it with new values in the subsequent iteration. 
LCS is computed on bag of words of two files where longest common subsequence of words is calculated and similarity is computed as:
Similarity: LCS / min(s1.length,s2.length)
This gives us the percentage of similarity of smaller file with respect to larger file.
Time complexity: O(n*m) – where n is length of string 1 and m is length of string 2. 

## Plagiarism detection approach used in the project is as follows: 

1. Identifying type of file
The type of file is checked by matching patterns for CPP and JAVA code. 
Patterns for CPP:

-  File containing #include 
Patterns Java:
-  File containing public class *
- File containing public static void main * 
- File containing import statement.

2. Preprocessing of input text file
In the preprocessing step both the files are read into the program memory and URLs, punctuations 
are removed and line is converted to lowercase. Only characters and numbers are kept. The string is 
tokenized into words by space and hyphen separator and stored in list (bag of words). 
There is a list of 10 stop words which are removed from the list of bags of words. All the preprocessed 
lines are concatenated to make a long string for each file. 

3. Preprocessing of input code file
In case the file type is code then the files are processed by removing line and block comments from the 
file and removing spaces and concatenating lines to make long string for both the files.

3. Algorithm for text files
	````
	If the file length is greater than 100kb:
		Rvalue = Run N-gram matching with length 5
		Jaccard = Run Jaccard index 
		Take average of Rvalue and Jaccard
	Else:
		Rvalue = Run N-gram matching with length 5 and get similarity
		Jaccard = Run Jaccard index 
		LCS = Run LCS on bag of words to find longest common subsequence of words and get similarity.
		Take average of Rvalue, Jaccard and LCS

If the average similarity is greater than 0.5 then output 1 else output 0.

4. Algorithm for code file
N-gram matching is applied for code file where the first string is split into n-gram of length 5 and  each n-gram is searched in the second string and the match length is increased when each time a match  is found and then similarity is calculated based on it.
Similarity = 2* Match Length / (length of string 1 + length of string 2)
If the similarity is greater than 0.5 then output 1 else output 0.

5. Criteria for selection of plagiarism and output
The plagiarism threshold is set at 0.5 for both code and text files. If the value is greater than 0.5 then the output is 1 which means it is plagiarized else 0 which means it is not plagiarized.

