 package com.Bluetooth;
 
import java.util.Random;

import android.util.Log;

public class DataConverse{
	 private String valuetransfer(String Kw1,String Kw)
	    {
	    	Kw = Kw1;
	    	return Kw;
	    }
	 
	 
	public static String getKw1 (String Kws,String R1s)
	{
		int Kw = stringToInt(Kws);
		int R1 = stringToInt(R1s);
		int Kw1 = Kw ^ Rot(R1, Kw);
		return intToString(Kw1);		
	}
	
	public static String getKm1 (String Kms,String R2s)
	{
		int Km = stringToInt(Kms);
		int R2 = stringToInt(R2s);
		int Km1 = Km ^ Rot(R2, Km);
		return intToString(Km1);		
	}
	
	public static String RandomNumber(){
		Random r = new Random();
		int random = r.nextInt();
		String s = intToString(random);
		
		return s;
		
	}
	
	public static String RandomNumber2() {         

	      char[] chs = new char[15];       

	  Random ran = new Random();        

	 for(int i = 0; i < 15; i++) {          

	   chs[i] = (char)(ran.nextInt(10) + '0');     

	    }        

	 return new String(chs);     

	}
	
	/*change string to hexadecimal array*/
	public byte[] stringToBytes(String message){

		byte[] bytes = message.getBytes();
		
		return bytes;
	}
	
	/*change hexadecimal array to string*/
	public String bytesToString(byte[] bytes){

		String result = new String(bytes);
		
		return result;
	}

	public static String intToString(int Ori){
		String s = "";
		String tempS;
		int temp;
		for(int i = 28, j = 0;j < 32;j += 4){
			temp = Ori << j;
			temp = temp >>> i;
			tempS = Integer.toHexString(temp);
			
			s += tempS;
		}
		
		return s;
	}
	
	public static String intToString2(long Ori){
		String s = "";
		String tempS;
		long temp;
		for(int i = 56, j = 0;j < 60;j += 4){
			temp = Ori << j;
			temp = temp >>> i;
			tempS = Integer.toHexString((int)temp);
			
			s += tempS;
		}
		
		return s;
	}
	
	public static int stringToInt(String s){
		int Ori = 0;
		char ch;
		
		for(int i = 0; i < s.length(); i++){
			Ori *= 16;
			ch  = s.charAt(i);
			if(ch >= 'A'){
				if(ch >= 'a')
					Ori += (int)(ch - 87);
				else
					Ori += (int)(ch - 55);
			}
			else
				Ori += (int)(ch - 48);
			
		}
		return Ori;
	}
	
	public static long stringToInt2(String s){
		long Ori = 0;
		char ch;
		
		for(int i = 0; i < s.length(); i++){
			Ori *= 16;
			ch  = s.charAt(i);
			if(ch >= 'A'){
				if(ch >= 'a')
					Ori += (long)(ch - 87);
				else
					Ori += (long)(ch - 55);
			}
			else
				Ori += (long)(ch - 48);
			
		}
		return Ori;
	}
	
	public static String getA(String Kws, String Kms, String R1s){
		String A = "";
		int result = 0;
		int Kw = stringToInt(Kws);
		int Km = stringToInt(Kms);
		int R1 = stringToInt(R1s);
		
		result = Kw ^ Km ^ R1;
		A = intToString(result);
		
		return A;
	}
	
	public static int strToInt(String value, int defaultValue) { 
	    try { 
	        return Integer.valueOf(value); 
	    } catch (Exception e) { 
	        return defaultValue; 
	    } 
	} 
	
	public static String getB(String IDws, String IDms, String R2s){
		
		long result = 0;
		
		long IDw = Long.parseLong(IDws);
		long IDm = Long.parseLong(IDms);
		long R2 = stringToInt2(R2s);
		
		result = IDw ^ IDm ^ R2;
		String B = Long.toString(result);
		
		return B;
	}
	
	public static String getKmFromA(String As, String Kws, String R1s){
		String Km = "";
		int KmI = 0;
		int A = stringToInt(As);
		int Kw = stringToInt(Kws);
		int R1 = stringToInt(R1s);
		
		KmI = A ^ Kw ^ R1;
		Km = intToString(KmI);
		
		return Km;
	}
	
	public static String getIDmFromB(String Bs, String IDws, String R2s){
		
		long B = Long.parseLong(Bs);
		long IDw = Long.parseLong(IDws);
		long R2 = stringToInt2(R2s);
		
		long IDm = B ^ IDw ^ R2;
		String IDms = Long.toString(IDm);
		
		
		return IDms;
	}
	
	public static String getC(String Kws, String Kms, String R1s, String R2s){
		String C = "";
		int result = 0;
		int Kw = stringToInt(Kws);
		int Km = stringToInt(Kms);
		int R1 = stringToInt(R1s);
		int R2 = stringToInt(R2s);
		
		result = ((Kw ^ Rot(R1, Km)) + (Km ^ Rot(R2, Km))) % (int)Math.pow(2, 32);
		C = intToString(result);
		
		return C;
	}
	
	private static int Rot(int R1, int K){
		int K_ = 0;		
		int count = count1(K);
		
		K_ = R1^K << count;
		
		return K_;
	}
	
	private static int count1(int Ks){
		int count = 0;
		int K = Ks;
		int maxBit = 30;
		int temp = 0;
		
		for(int i = 0; i <= 30; i++){
			temp = K << i;
			temp = temp >> maxBit;
			
			if(temp == 1){
				count++;
			}
		}
		
		return count;
	}
	
}
