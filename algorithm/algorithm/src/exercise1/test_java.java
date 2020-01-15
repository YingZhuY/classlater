package exercise1;


//Implement the QuickSort(¿ìËÙÅÅÐò) algorithm in Java to sort one dimensional array.

public class test_java {
	
	public static void QuickSort(int[] temp,int low,int high) {
		while(low>=high)	return;
		int middle=Partition(temp,low,high);
		QuickSort(temp,low,middle-1);
		QuickSort(temp,middle+1,high);
	}
	
	public static int Partition(int[] temp,int low,int high) {
		int base=temp[low];
		while(low<high)
		{
			while(temp[high]>base && low<high)
				high--;
			temp[low]=temp[high];
			while(temp[low]<=base && low<high)
				low++;
			temp[high]=temp[low];
		}
		temp[low]=base;
		return low;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] temp=new int[] {10,38,46,28,57,12,73,23,46,35};
		int len=temp.length;
		QuickSort(temp,0,len-1);
		for(int i=0;i<len;i++)
		{
			System.out.print(temp[i]+" ");
		}
	}

}


/*
//Given the following class,called NumberHolder,write some code that creates an instance of the class,initializes its two member variables,and then displays the value of each member variable.
public class test_java{
	public class NumberHolder{
		public int anInt;
		public float aFloat;
		public NumberHolder(int i, float d) {
			this.anInt=i;
			this.aFloat=d;
		}
		public void showInt() {
			System.out.println(anInt);
		}
		public void showFloat() {
			System.out.println(aFloat);
		}
	}
	
	public static void main(String[] args)
	{
		int i=7;
		float j=9;
		NumberHolder showall=new test_java().new NumberHolder(i,j);
		showall.showInt();
		showall.showFloat();
	}
}
*/

/*
//Write a class that implements the CharSequence interface found in the java.lang package.Your implementation should return the string backwards.
//Select one sentence to use as the data.Write a small main method to test your class;make sure to call all four methods.
public class test_java implements CharSequence{
	private String s;
	public test_java(String s) {
		this.s=s;
	}
	private int fromEnd(int i) {
		return s.length()-i-1;
	}

	public char charAt(int i) {
		if((i<0) || (i>=s.length())) {
			throw new StringIndexOutOfBoundsException(i);
		}
		return s.charAt(fromEnd(i));
	}
	
	public int length() {
		return s.length();
	}
	
	public CharSequence subSequence(int start,int end) {
		if(start<0) {
			throw new StringIndexOutOfBoundsException(start);
		}
		if(end>s.length()) {
			throw new StringIndexOutOfBoundsException(end);
		}
		if(start>end) {
			throw new StringIndexOutOfBoundsException(start-end);
		}
		StringBuilder sub=new StringBuilder(s.subSequence(fromEnd(end), fromEnd(start)));
		return sub.reverse();
	}
	
	public String toString() {
		StringBuilder s=new StringBuilder(this.s);
		return s.reverse().toString();
	}
	
	private static int random(int max) {
		return (int)Math.round(Math.random()*(max+1));
	}
	
	public static void main(String[] args) {
		test_java s=new test_java("CharSequence");
		for(int i=0;i<s.length();i++) {
			System.out.print(s.charAt(i));
		}
		System.out.println();
		int start=random(s.length()-1);
		int end=random(s.length()-1-start)+start;
		System.out.println(s.subSequence(start, end));
		System.out.println(s);
	}
}
*/

//learn how to use jar command to pack files into a compressed files.

//see the document
