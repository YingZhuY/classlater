package exercise1;

import java.util.HashMap;

public class chars_count {
	public static void main(String[] args)
	{
		String J="aA";		//"z";
		String S="aAAbbbb";	//"ZZ";
		int len_J=J.length();
		int len_S=S.length();
		char base=J.charAt(0);	//使用J的第一个元素作为基准
		int total_count=0;		//最后的返回值
		
		int[] temp_J=new int[len_J];	//构造表示J，S的整型数组，数组元素为对应位置元素减基准元素
		int[] temp_S=new int[len_S];
		for(int i=0;i<len_J;i++)	//复杂度O(len_J)
		{
			if( ((J.charAt(i)>='a')&&(J.charAt(i)<='z')) || ((J.charAt(i)>='A')&&(J.charAt(i)<='Z')))
				temp_J[i]=J.charAt(i)-base;
			else
			{
				System.out.print("input J error!");
				return;
			}
		}
		for(int i=0;i<len_S;i++)	//复杂度O(len_S)
		{
			if( ((S.charAt(i)>='a')&&(S.charAt(i)<='z')) || ((S.charAt(i)>='A')&&(S.charAt(i)<='Z')))
				temp_S[i]=S.charAt(i)-base;
			else
			{
				System.out.print("input S error!");
				return;
			}
		}
		
		//构造S的计数hash表
		HashMap<Integer,Integer> count=new HashMap<>();
		for(int i=0;i<len_S;i++)	//复杂度O(len_S)
		{
			if(!count.containsKey(temp_S[i]))
				count.put(temp_S[i], 1);
			else
				count.put(temp_S[i],count.get(temp_S[i])+1);
		}
		
		//计算最后的返回值total_count
		for(int i=0;i<len_J;i++)	//复杂度O(len_J)
		{
			if(count.containsKey(temp_J[i]))
				total_count+=count.get(temp_J[i]);
		}
		System.out.print(total_count);
	}

}
