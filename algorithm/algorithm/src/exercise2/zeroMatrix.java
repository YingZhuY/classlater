package exercise2;

public class zeroMatrix {
	
	static int[][] temp=new int[][] {{0,1,2,0},{3,4,5,2},{1,3,1,5}} ;	//{{1,1,1},{1,0,1},{1,1,1}};
	
	public static void main(String[] args) {
		if((temp.length==0) || (temp[0].length)==0)	//����Ϊ��ʱ����
			return;
		boolean row=false;		//��Ӧ��0���Ƿ����Ϊ0
		boolean column=false;	//��Ӧ��0���Ƿ����Ϊ0
		int i=0,j=0;
		for(i=0;i<temp[0].length;i++) {	//�Ե�0�еĴ���
			if(temp[0][i]==0) {
				row=true;
				break;
			}
		}
		for(j=0;j<temp.length;j++) {	//�Ե�0�еĴ���
			if(temp[j][0]==0) {
				column=true;
				break;
			}
		}
		for(i=0;i<temp.length;i++) {
			for(j=0;j<temp[0].length;j++) {
				if(temp[i][j]==0) {
					temp[i][0]=temp[0][j]=0;	//�õ�0�С���0������ʶ���С������Ƿ����Ϊ0
				}
			}
		}
		for(i=1;i<temp.length;i++) {	//���е���0
			if(temp[i][0]==0) {
				for(j=1;j<temp[0].length;j++)
					temp[i][j]=0;
			}
		}
		for(i=1;i<temp[0].length;i++) {	//���е���0
			if(temp[0][i]==0) {
				for(j=1;j<temp.length;j++)
					temp[j][i]=0;
			}
		}
		if(row) {						//�ĵ�0��
			for(i=0;i<temp[0].length;i++)
				temp[0][i]=0;
		}
		if(column) {					//�ĵ�0��
			for(j=0;j<temp.length;j++)
				temp[j][0]=0;
		}
		
		for(i=0;i<temp.length;i++) {	//��ӡ�������
			for(j=0;j<temp[0].length;j++) {
				System.out.print(temp[i][j]+" ");
			}
			System.out.println();
		}
		
	}

}
