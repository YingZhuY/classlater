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
		File cat_file=new File("D:\\USTC-SSE\\java\\作业2_201910288153061.docx");
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
    FileInputStream in = null;	//定义文件流变量，初始化为空
    try {	//开始读取目标文件
      in = new FileInputStream("D:\\USTC-SSE\\java\\作业2_201910288153061.docx");	//("TestFileInputStream.java");
    } catch (FileNotFoundException e) {		//目标文件不存在时抛出异常
      System.out.println("找不到指定文件"); 
      System.exit(-1);
    }
    
    try {
      long num = 0;		//文件字节数计数
      while((b=in.read())!=-1){		//依次读取文件，内容不为空时计数
        System.out.print((char)b); 
        num++;
      }
      in.close();  		//读取结束后关闭文件
      System.out.println();
      System.out.println("共读取了 "+num+" 个字节");
    } catch (IOException e1) {
      System.out.println("文件读取错误"); System.exit(-1);	//捕获文件读取过程中的异常
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
      fr = new FileReader("D:\\USTC-SSE\\java\\作业2_201910288153061.docx");		//("TestFileInputStream.java");
      int ln = 0;
      while ((c = fr.read()) != -1) {
        //char ch = (char) fr.read();
        System.out.print((char)c);
        //if (++ln >= 100) { System.out.println(); ln = 0;}
      }
      fr.close();
    } catch (FileNotFoundException e) {
      System.out.println("找不到指定文件");
    } catch (IOException e) {
      System.out.println("文件读取错误");
    }
  }
}*/

import java.io.*;
public class newjava_e2 {//TestBufferStream1 {
  public static void main(String[] args) {
    try {
      FileInputStream fis = 
              new FileInputStream("D:\\USTC-SSE\\java\\作业2_201910288153061.docx");	//("d:\\share\\java\\HelloWorld.java");
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



