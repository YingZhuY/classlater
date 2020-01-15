package exercise2;

public class zeroMatrix {
	
	static int[][] temp=new int[][] {{0,1,2,0},{3,4,5,2},{1,3,1,5}} ;	//{{1,1,1},{1,0,1},{1,1,1}};
	
	public static void main(String[] args) {
		if((temp.length==0) || (temp[0].length)==0)	//数组为空时返回
			return;
		boolean row=false;		//对应第0行是否该设为0
		boolean column=false;	//对应第0列是否该设为0
		int i=0,j=0;
		for(i=0;i<temp[0].length;i++) {	//对第0行的处理
			if(temp[0][i]==0) {
				row=true;
				break;
			}
		}
		for(j=0;j<temp.length;j++) {	//对第0列的处理
			if(temp[j][0]==0) {
				column=true;
				break;
			}
		}
		for(i=0;i<temp.length;i++) {
			for(j=0;j<temp[0].length;j++) {
				if(temp[i][j]==0) {
					temp[i][0]=temp[0][j]=0;	//用第0行、第0列来标识该行、该列是否该置为0
				}
			}
		}
		for(i=1;i<temp.length;i++) {	//对行的置0
			if(temp[i][0]==0) {
				for(j=1;j<temp[0].length;j++)
					temp[i][j]=0;
			}
		}
		for(i=1;i<temp[0].length;i++) {	//对列的置0
			if(temp[0][i]==0) {
				for(j=1;j<temp.length;j++)
					temp[j][i]=0;
			}
		}
		if(row) {						//改第0行
			for(i=0;i<temp[0].length;i++)
				temp[0][i]=0;
		}
		if(column) {					//改第0列
			for(j=0;j<temp.length;j++)
				temp[j][0]=0;
		}
		
		for(i=0;i<temp.length;i++) {	//打印结果数组
			for(j=0;j<temp[0].length;j++) {
				System.out.print(temp[i][j]+" ");
			}
			System.out.println();
		}
		
	}

}
