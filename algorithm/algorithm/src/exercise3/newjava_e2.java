package exercise3;

/*
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

//modify the following cat method so that it will compile

public class newjava_e2 {
	public static void cat(File file) throws IOException {
	    RandomAccessFile input = null;
	    String line = null;

	    try {
	        input = new RandomAccessFile(file, "r");
	        while ((line = input.readLine()) != null) {
	            System.out.println(line);
	        }
	        return;
	    } finally {
	        if (input != null) {
	            input.close();
	        }
	    }
	}
	
	public static void main(String[] args) {
		File cat_file=new File("D:\\USTC-SSE\\java\\��ҵ2_201910288153061.docx");
		try {
			cat(cat_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
*/
/*
import java.io.*;
public class newjava_e2 {	//TestFileInputStream {
  public static void main(String[] args) {
    int b = 0;
    FileInputStream in = null;	//�����ļ�����������ʼ��Ϊ��
    try {	//��ʼ��ȡĿ���ļ�
      in = new FileInputStream("D:\\USTC-SSE\\java\\��ҵ2_201910288153061.docx");	//("TestFileInputStream.java");
    } catch (FileNotFoundException e) {		//Ŀ���ļ�������ʱ�׳��쳣
      System.out.println("�Ҳ���ָ���ļ�"); 
      System.exit(-1);
    }
    
    try {
      long num = 0;		//�ļ��ֽ�������
      while((b=in.read())!=-1){		//���ζ�ȡ�ļ������ݲ�Ϊ��ʱ����
        System.out.print((char)b); 
        num++;
      }
      in.close();  		//��ȡ������ر��ļ�
      System.out.println();
      System.out.println("����ȡ�� "+num+" ���ֽ�");
    } catch (IOException e1) {
      System.out.println("�ļ���ȡ����"); System.exit(-1);	//�����ļ���ȡ�����е��쳣
    }
  }
}*/
/*
import java.io.*;
public class newjava_e2 {//TestFileReader {
  public static void main(String[] args) {
    FileReader fr = null; 
    int c = 0;
    try {
      fr = new FileReader("D:\\USTC-SSE\\java\\��ҵ2_201910288153061.docx");		//("TestFileInputStream.java");
      int ln = 0;
      while ((c = fr.read()) != -1) {
        //char ch = (char) fr.read();
        System.out.print((char)c);
        //if (++ln >= 100) { System.out.println(); ln = 0;}
      }
      fr.close();
    } catch (FileNotFoundException e) {
      System.out.println("�Ҳ���ָ���ļ�");
    } catch (IOException e) {
      System.out.println("�ļ���ȡ����");
    }
  }
}*/

import java.io.*;
public class newjava_e2 {//TestBufferStream1 {
  public static void main(String[] args) {
    try {
      FileInputStream fis = 
              new FileInputStream("D:\\USTC-SSE\\java\\��ҵ2_201910288153061.docx");	//("d:\\share\\java\\HelloWorld.java");
      BufferedInputStream bis = 
              new BufferedInputStream(fis);
      int c = 0;
      System.out.println(bis.read());
      System.out.println(bis.read());
      bis.mark(100);
      for(int i=0;i<=10 && (c=bis.read())!=-1;i++){
        System.out.print(c+" ");//((char)c+" ");
      }
      System.out.println(); 
      bis.reset();
      for(int i=0;i<=10 && (c=bis.read())!=-1;i++){
        System.out.print(c+" ");//((char)c+" ");
      }
      bis.close();
    } catch (IOException e) {e.printStackTrace();}
  }
}



