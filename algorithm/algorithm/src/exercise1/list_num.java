package exercise1;

public class list_num {
	
	static int N=4;
	static int M=2;
	static int[] a=new int[]{1,2,3,4};	//��ʼ1~n����
	static int[] b=new int[M];		//���ָ�������
	public static void main(String[] args)
	{
		listNum(N,M);
	}
	
	static void listNum(int n,int m)
	{
		for(int i=m;i<=n;i++) {	//m~n
			b[m-1]=i-1;	//��׼���Ϊi-1��ֻ���ǻ�׼���֮��ģ�����������Ϊm-1��n-1
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
