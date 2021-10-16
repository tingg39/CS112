package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols()
    {
    	scalars = new ArrayList<ScalarSymbol>();
    	arrays = new ArrayList<ArraySymbol>();
    	for(int i = 0; i < expr.length(); i++)
    	{
    		char c = expr.charAt(i);
    		String name = "";
    		if((c >= 'a' && c <= 'z') || (c >= 'A'&& c <= 'Z')) 
    		{	
    			int j = 0;
    			for(j = i+1; j < expr.length(); j++)
    			{
    				char ch = expr.charAt(j);
    				if(!((ch >= 'a' && ch <= 'z') || (ch >= 'A'&& ch <= 'Z')))
    				{
    					break;
    				}
    			}
    			name = expr.substring(i, j);
				i = j-1;
    			if(i != expr.length()-1 && expr.charAt(i+1) == '[')
    			{	
    				ArraySymbol as = new ArraySymbol(name);
    				arrays.add(as);
    			}
    			else
    			{
    				ScalarSymbol ss = new ScalarSymbol(name);
    				scalars.add(ss);
    			}
    		}	
    	}
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    		/** COMPLETE THIS METHOD **/
    		// following line just a placeholder for compilation
    	/*StringTokenizer st = new StringTokenizer(expr, delims);
    	String token;
    	Stack<Float> oper = new Stack<Float>();
    	Stack<Character> operator = new Stack<Character>();
    	Stack<Character> operator1 = new Stack<Character>();
    	for(int i = 0; i < expr.length(); i++){
    		char ch = expr.charAt(i);
    		if(ch == ' '){
    			continue;
    		}
    		if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')){
    			token = st.nextToken();
    			i += (token.length() - 1);
    			if(expr.charAt(i+1) == '('){
    				
    			}
    		}
    	}*/
    	
    	//a-(b+A[B[2]])*d+3
    	// a -     (b+A[B[2]]) *d
    	// b +  A[B[2]]
    	expr = expr.replace(" ","");
    	return operation(reevaluate(expr));
    }
    
    private float getSValue(String s){
    	int a = s.length()-1;
    	char b = s.charAt(a);
    	if(Character.isLetter(b)){
    		ScalarSymbol ss = new ScalarSymbol(s);
    		float val = (float)(scalars.get(scalars.indexOf(ss)).value);
    		return val;
    	}else{
    		return Float.parseFloat(s);
    	}
    }
    	
    private int getAValue(String s, int i){
    	s = s.substring(0,s.indexOf('['));
    	ArraySymbol as = new ArraySymbol(s);
    	return arrays.get(arrays.indexOf(as)).values[i];
    }
    
    private float prod(String s)
    {
    	float sum = 1;
    	StringTokenizer st = new StringTokenizer(s, "/");
    	String temp = st.nextToken();
    	StringTokenizer st2 = new StringTokenizer(temp, "*");
    	while(st2.hasMoreTokens())
    	{
    		String s1 = st2.nextToken();
    		sum = sum * getSValue(s1);
    	}
    	float val = sum;
    	float div = 1;
    	while(st.hasMoreTokens())
    	{
    		String t = st.nextToken();
    		StringTokenizer st3 = new StringTokenizer(t, "*");
    		while(st3.hasMoreTokens() == true)
    		{
    			String s2 = st3.nextToken();
    			div = div*getSValue(s2);
    		}
    		val = val / div;
    	}
    	return val;
    }
    
    //1+2*4+3/4
    //1 2*4 3/4
    //temp = 2*4
    private float operation(String s){
    	float whole = 0;
    	StringTokenizer st = new StringTokenizer(s, "+");
    	while(st.hasMoreTokens() == true){
    		int i = 0;
        	float sum = 0;
    		String temp = st.nextToken();
    		for(int j = 0; j <= temp.length() - 1; j++)
    		{
    			if(j!=0&&(temp.charAt(j)=='-'&&(temp.charAt(j-1)!='*'&&temp.charAt(j-1)!='/')))
    			{
    				if(i == 0)
    				{
    					sum = sum + prod(temp.substring(i,j));
    					i = j + 1;
    				}
    				else
    				{
    					sum = sum - prod(temp.substring(i,j));
    					i = j + 1;
    				}
    			}
    			if(j == temp.length()-1)
    			{
    				if(i == 0)
    				{
    					sum = sum + prod(temp.substring(i,j+1));
    					i = j+1;
    				}
    				else
    				{
    					sum = sum - prod(temp.substring(i,j+1));
    					i = j+1;
    				}
    			}
    		}
    		whole = whole + sum;
    	}
    	return whole;
    }
    
    private String reevaluate(String s)
    {
    	for(int i = 0; i < s.length(); i++)
    	{
    		char ch = s.charAt(i);
    		if(ch == '[' || ch == '(')
    		{
    			int j = i;
    			Stack<Integer> st = new Stack<Integer>();
    			st.push(i);
    			do{
    				j++;
    				char ch1 = s.charAt(j);
    				if(ch1 == '(' || ch1 == '[')
    				{
    					st.push(j);
    				}
    				if(ch1 == ')' || ch1 == ']')
    				{
    					st.pop();
    				}//A[B[]] A
    				//(()) ... (())
    			}while(st.isEmpty() == false);
    			if(ch == '(')
    			{
    				if(j < s.length()-1)
    				{
    					return s.substring(0,i) + Float.toString(operation(reevaluate(s.substring(i+1,j)))) + reevaluate(s.substring(j+1));
    				}
    				else
    				{
    					return s.substring(0,i) + Float.toString(operation(reevaluate(s.substring(i+1,j))));
    				}
    			}
    			//varx[]
    			else if(ch == '[')
    			{
    				int pos = i;
    				for(int a = i; a >= 0; a--)
    				{
    					if(a == 0)
    					{
    						pos = a;
    					}
    					char c = s.charAt(a);
    					if(c == '+' || c == '-' || c == '*' || c == '/')
    					{
    						pos = a + 1;
    						break;
    					}
    				}
    				if(j < s.length() - 1)
    				{
    					return s.substring(0,pos) + Integer.toString(getAValue(s.substring(pos,i+1),(int)operation(reevaluate(s.substring(i+1,j))))) + reevaluate(s.substring(j+1));
    				}else
    				{
    					return s.substring(0,pos) + Integer.toString(getAValue(s.substring(pos, i+1), (int)operation(reevaluate(s.substring(i+1,j)))));
    				}
    			}
    		}
    	}
    	char ch0 = s.charAt(0);
    	if(ch0 == '+' || ch0 == '-' || ch0 == '*' || ch0 == '/'){
    		return s;
    	}
    	return Float.toString(operation(s));
    }
    
    
    //recursive method
    //expr -> "varx"
   /* private float evaluate(String expr, int s, int e)
    {
    	/*StringTokenizer st = new StringTokenizer(expr.substring(s, e), delims);
    	
    	if(st.countTokens() == 1)
    	{
    		if(!Character.isLetter(expr.charAt(0)))
    			return Integer.valueOf(st.nextToken());
    			
    		for(int i = 0; i < scalars.size(); i++)
    			if(scalars.get(i).name.equals(expr))
    				return scalars.get(i).value;	
    	}
    	
    	if(st.countTokens() == 2)
    	{
    		String fValue = st.nextToken();
    		String sValue = st.nextToken();
    		
    		for(int i = 0; i < expr.length(); i++)
    		{
    			if(expr.charAt(i) == '+')
    				return evaluate(fValue, 0, fValue.length())+evaluate(sValue, 0, sValue.length());
    			if(expr.charAt(i) == '-')
    				return evaluate(fValue, 0, fValue.length())-evaluate(sValue, 0, sValue.length());
    			if(expr.charAt(i) == '*')
    				return evaluate(fValue, 0, fValue.length())*evaluate(sValue, 0, sValue.length());
    			if(expr.charAt(i) == '/')
    				return evaluate(fValue, 0, fValue.length())/evaluate(sValue, 0, sValue.length());
    		}
    	}
    	if(st.countTokens()>2)
    	{
    		//1+2+t
    		String lValue = "";
    		while(st.hasMoreTokens())
    			lValue = st.nextToken();
    		
    		for(int i = expr.length()-1; i >= 0; i--)
    		{
    			if(expr.charAt(i) == '+')
    				return evaluate(lValue, 0, lValue.length())+evaluate(expr.substring(0, i), 0, i);
    			if(expr.charAt(i) == '-')
    				return evaluate(lValue, 0, lValue.length())-evaluate(expr.substring(0, i), 0, i);
    			if(expr.charAt(i) == '*')
    				return evaluate(lValue, 0, lValue.length())*evaluate(expr.substring(0, i), 0, i);
    			if(expr.charAt(i) == '/')
    				return evaluate(lValue, 0, lValue.length())/evaluate(expr.substring(0, i), 0, i);
    		}
    	}
		return 0; */
    	
    	//parentheses
    	/*
    	s = expr.indexOf("(");
    	e = -1;
    	for(int i = expr.length()-1; i >= 0; i--)
    		if(expr.substring(i, i+1).equals(")"))
    		{
    			e = i;
    			break;
    		}
    	if(s != -1 && e != -1)
    		return evaluate(s, e, expr);
    	
    	
    	//a+b
    	//vara*b
    	
    	
    	return 0;
    	*/
    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
