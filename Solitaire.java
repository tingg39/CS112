package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	//complete
	void jokerA() {
		
		//find 27
		CardNode prev = deckRear;
		CardNode temp = deckRear.next;
		
		while(temp.cardValue != 27)
		{
			prev = temp;
			temp = temp.next;
		}
		
		//when 27 is the last value
		if(deckRear.cardValue == 27)
			deckRear = temp.next;
		//when 27 is the second to last value
		else if(temp.next == deckRear)
			deckRear = temp;
		
		//reconnect values
		prev.next = temp.next; 
		temp.next = temp.next.next; 
		prev.next.next = temp;
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	//complete
	void jokerB() 
	{
		
		CardNode prev = deckRear;
		CardNode temp = deckRear.next;
		
		while(temp.cardValue != 28){
			
			prev = temp;
			temp = temp.next;
			
		}
		
		if(deckRear.cardValue == 28)
			deckRear = deckRear.next;
		else if(temp.next.next == deckRear)
			deckRear = temp;
		
		prev.next = temp.next; //25->3
		temp.next = temp.next.next.next;
		prev.next.next.next = temp;
		//22 25 28 3 6 9 12
		//25->3 28->9 6->28
		//22 25 3 6 28 9 12
		
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		
		//find the first joker(27 or 28)
		CardNode joker1 = deckRear.next;
		CardNode joker2 = null;
		CardNode prev = deckRear;
		while(joker1.cardValue != 27 && joker1.cardValue != 28)
		{
			prev = joker1;
			joker1 = joker1.next;
		}
		joker2 = joker1.next;
		while(joker2.cardValue != 27 && joker2.cardValue != 28)
		{
			joker2 = joker2.next;
		}
		
		if(deckRear.next == joker1)
			deckRear = joker2;
		else if(deckRear == joker2)
			deckRear = prev;
		else
		{
			CardNode temp = deckRear; //temp is at 26
			deckRear = prev; //deckRear is at 25
			deckRear.next = joker2.next; //deckRear.next is 2
			joker2.next = temp.next; //27->1
			temp.next = joker1; //26->28
		}
		//1 4 7 10 13 16 19 22 25 28 3 6 9 12 15 18 21 24 27 2 5 8 11 14 17 20 23 26
		//(1 4 7 10 13 16 19 22 25) 28 3 6 9 12 15 18 21 24 27 (2 5 8 11 14 17 20 23 26)
		//(2 5 8 11 14 17 20 23 26) 28 3 6 9 12 15 18 21 24 27 (1 4 7 10 13 16 19 22 25)
		
		//26->28 27->1 25->2
		
		//1 is deckRear.next
		//26 is deckRear
		//2 is joker2.next
		//25 is previous
		//28 is joker1
		//27 is joker2
		//deckRear = 25

	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() 
	{	
		int n = 0;
		if(deckRear.cardValue == 28)
			n = 27;
		else
			n = deckRear.cardValue;
		
		CardNode firstCard = deckRear.next;
		CardNode lastCard = deckRear;
		CardNode prev = deckRear;
		while(prev.next.cardValue != deckRear.cardValue)
			prev = prev.next;
		
		for(int i = 1; i <= n; i++){
			lastCard = lastCard.next;
		}
		prev.next = firstCard; //23->1
		deckRear.next = lastCard.next;
		lastCard.next = deckRear;
		//5,8,11,14,17,20,23,26,28,9,12,15,18,21,24,2,27,1,4,7,10,13,16,19,22,25,3,6
		//(5,8,11,14,17,20,)23,26,28,9,12,15,18,21,24,2,27,1,4,7,10,13,16,19,22,25,3,6
		//23,26,28,9,12,15,18,21,24,2,27,1,4,7,10,13,16,19,22,25,3,(5,8,11,14,17,20),6
		//firstCard = 5
		//lastCard = 20
		//(previous card of deckRear).next = firstCard
		//lastCard.next = deckRear
		//deckRe
		
		
		
		//(1 4 7 10 13 16 19 22 25 28 3 6 9 12 15 18 21 24 27 2 5 8 11 14 17 20) 23 26
		//23 (1 4 7 10 13 16 19 22 25 28 3 6 9 12 15 18 21 24 27 2 5 8 11 14 17 20) 26
		//value = deckRear.cardValue
		//lastCard = 20
		//firstCard = 1
		//lastCard.next.next = firstCard 23-> 1
		//lastCard.next = deckRear 20->26
		
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		int a = 0;
		CardNode temp = null;
		
		do
		{
			jokerA();
			jokerB();
			tripleCut();
			countCut();
			temp = deckRear;
			a = deckRear.next.cardValue;
			if(a == 28)
				a = 27;
			for(int i = 0; i < a; i++)
				temp = temp.next;
		}while(temp.next.cardValue == 27 || temp.next.cardValue == 28);
		
		return temp.next.cardValue;
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		// COMPLETE THIS METHOD
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		String result = "";
		message = message.toUpperCase();
		for(int i = 0; i < message.length(); i++)
		{
			char key = 0;

			key = message.charAt(i);
			if(key < 'A' || key > 'Z')
				continue;
			
			key = (char) (key + getKey());
			if(key > (26+64))
				key = (char)(key - 26);
			result += key;
		}
		
	    return result;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) 
	{	
		String result = "";
		for(int i = 0; i < message.length(); i++)
		{
			char key = 0;
			key = message.charAt(i);
			key = (char) (key - getKey());
			if(key < 64)
				key = (char)(key + 26);
			result += key;
		}
		
	    return result;
	}
}
