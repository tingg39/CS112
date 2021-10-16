package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence 
{
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) 
	{
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() 
	{
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine 
{
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() 
	{
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException 
	{
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) 
		{
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) 
		{
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException 
	{
		HashMap<String, Occurrence> hm = new HashMap<String, Occurrence>();
		int freq = 1;
		
		Scanner sc = new Scanner(new File(docFile));
		while(sc.hasNext())
		{
			String word = sc.next();
			word = getKeyWord(word);

			if(word != null)
			{
				if(hm.get(word) != null)
					hm.get(word).frequency++;
				else
				{
					Occurrence o = new Occurrence(docFile, freq);
					hm.put(word, o);
				}
			}	
		}
		return hm;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) 
	{
		for(String s : kws.keySet())
		{
			
			if(keywordsIndex.get(s) == null)
				keywordsIndex.put(s, new ArrayList<Occurrence>());
			
			keywordsIndex.get(s).add(kws.get(s));
			insertLastOccurrence(keywordsIndex.get(s));
			
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) 
	{
		if(word == null)
			return null;
		
		String w = "";
		String result = "";
		
		word = word.toLowerCase();
		
		for(int j = 0; j < word.length(); j++)
		{
			if(word.charAt(j) != ' ')
				w = w + word.charAt(j);
		}
		
		for(int i = 0; i < w.length(); i++)
		{
			
			String c = w.substring(i, i+1);
			if(Character.isAlphabetic(w.charAt(i)))
				result += c;
			else
			{
				while(i != w.length() && !Character.isAlphabetic(w.charAt(i)))
					i++;
				if(i != w.length())
					return null;
			}
		}
		
		if(noiseWords.get(result) == null && !result.equals(""))
			return result;
		return null;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) 
	{
		if(occs.size() <= 1)
			return null;
		
		int min = 0;
		int max = occs.size()-1;
		Occurrence occ = occs.get(occs.size()-1);
		int middle = 0;
		ArrayList<Integer> l = new ArrayList<Integer>();
		/*if(occs.size() == 2)
		{
			if(occs.get(0).frequency < occs.get(1).frequency)
				occs.add(0, occs.remove(1));
			return l;
			
		}*/
		while(min <= max)
		{
			middle = (min+max)/2;
			l.add(middle);
			if(occs.get(middle).frequency > occ.frequency)
				min = middle+1;
			else if(occs.get(middle).frequency < occ.frequency)
				max = middle-1;
			else
			{
				occs.add(middle, occs.remove(occs.size()-1));
				return l;
			}
		}
		Occurrence o = occs.remove(occs.size()-1);
		if(o.frequency < occs.get(middle).frequency)
			occs.add(middle + 1, o);
		else
			occs.add(middle, o);
		return l;
	}

	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) 
	{
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		ArrayList<String> res = new ArrayList<String>();
		ArrayList<Occurrence> w1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> w2 = new ArrayList<Occurrence>();
		w1 = keywordsIndex.get(kw1);
		w2 = keywordsIndex.get(kw2);
		
		if(w1 == null && w2 == null)
			return null;
		else if(w1 != null && w2 ==null)
		{
			for(int k = 0; k < w1.size(); k++)
			{
				res.add(w1.get(k).document);
			}
		}else if(w1 == null && w2 !=null)
		{
			for(int k = 0; k < w2.size(); k++)
			{
				res.add(w2.get(k).document);
			}
		}
		if(w1 != null && w2 !=null)
			res = searchHelp(res,w1,w2);
		for(int i = 0; i < res.size(); i++)
		{
			String s = res.get(i);
			if(i + 1 != res.size())
			{
				for(int j = i+1; j < res.size(); j++)
				{
					String s1 = res.get(j);
					if(s1 == s)
						res.remove(j);
				}
			}
		}
		return res;
	}
	
	private ArrayList<String> searchHelp(ArrayList<String> res, ArrayList<Occurrence> w1, ArrayList<Occurrence> w2)
	{
		ArrayList<String> res5 = new ArrayList<String>(5);
		int i = 0;
		int j = 0;
		int num = 0;
		int s1 = w1.size();
		int s2 = w2.size();
		while(i != s1 && j != s2)
		{
			int f1 = w1.get(i).frequency;
			int f2 = w2.get(j).frequency;
			if(f1 == f2)
			{
				String a = w1.get(i).document;
				String b = w2.get(j).document;
				if(a == b)
				{
					res.add(a);
					i++;
					j++;
				}else
				{
					res.add(a);
					res.add(b);
					i++;
					j++;
				}
			}else if(f1 > f2)
			{
				String a = w1.get(i).document;
				String b = w2.get(j).document;
				if(a == b)
				{
					res.add(a);
					i++;
					j++;
				}else
				{
					res.add(a);
					i++;
				}
			}else if(f1 < f2)
			{
				String a = w1.get(i).document;
				String b = w2.get(j).document;
				if(a == b)
				{
					res.add(b);
					i++;
					j++;
				}else
				{
					res.add(b);
					j++;
				}
			}
			
		}
		
		if(i == s1 && j != s2)
		{
			for(int k = j; k < s2; k++)
			{
				String b = w2.get(k).document;
				if(res.contains(k) == false)
				{
					res.add(b);
				}
			}
		}else if(i != s1 && j == s2)
		{
			for(int k = i; k < s1; k++)
			{
				String a = w1.get(k).document;
				if(res.contains(k) == false)
				{
					res.add(a);
				}
			}
		}
		
		while(num < 5 && num != res.size())
		{
			res5.add(res.get(num));
			num++;
		}
		return res5;
	}
}
