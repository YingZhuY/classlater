package exercise1;

public class list_num {
	
	static int N=4;
	static int M=2;
	static int[] a=new int[]{1,2,3,4};	//初始1~n数组
	static int[] b=new int[M];		//序号指针的数组
	public static void main(String[] args)
	{
		listNum(N,M);
	}
	
	static void listNum(int n,int m)
	{
		for(int i=m;i<=n;i++) {	//m~n
			b[m-1]=i-1;	//基准序号为i-1，只考虑基准序号之后的，即考虑序列为m-1到n-1
			if(m>1)	listNum(i-1,m-1);
			else
			{
				for(int j=0;j<M;j++)
					System.out.print(a[b[j]] + " ");
				System.out.println();
			}
				
		}
	}

}
