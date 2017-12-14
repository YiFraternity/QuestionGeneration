package com.extend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 对文件进行操作
 *           清空文件，写入文件，读取文件...
 * @JDKversion JDK1.8
 * @CreatorDate 2017-9
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-21
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class OperateFile {

    //无参构造函数
	public OperateFile() {}
	
	/**
	 * @Comment 读取存放子过程的XML文件
	 *          将XML文件中的内容解析到数组里面，返回数组
	 * @param strFile
	 * @return ArrayList<String>
	 */
	public ArrayList<String> readXMLFileAboutSubprocess(String strFile){
		ArrayList<String> strArray = new ArrayList<String>();
		try{
			File f = new File(strFile);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			
			NodeList nodelist = doc.getElementsByTagName("binding");
			for(int i =0;i<nodelist.getLength();i++){
				strArray.add(doc.getElementsByTagName("uri").item(i).getFirstChild().getNodeValue());
//					String strArrayEach=(String)strArray.get(i);
//					System.out.println(strArrayEach);
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		return strArray;
	}
	
	/***
	 * @Comments 读取存放原料和产物的XML文件
	 *           将XML文件中的内容解析到数组里面，返回数组
	 * @param strFile
	 * @return ArrayList<String>
	 */
	public ArrayList<String> readXMLFileAboutRawOuput(String strFile){
		ArrayList <String>strArray = new ArrayList<String>();
		try{
			File f = new File(strFile);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			
			NodeList nodelist = doc.getElementsByTagName("binding");
			for(int i =0;i<nodelist.getLength();i++){
				strArray.add(doc.getElementsByTagName("literal").item(i).getFirstChild().getNodeValue());
//					String strArrayEach=(String)strArray.get(i);
//					System.out.println(strArrayEach);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return strArray;
	}
	
	/***
	 * @Comments 将字符串a写入文件strFile
	 *           替换掉原有部分
	 *           (使用BufferedWriter创建对象时，注意第二参数为空或者为false)
	 * @param strFile
	 * @param a
	 */
	public void writeToFile(String strFile,String a){
		try{
			File file = new File(strFile);
			if(!file.exists()){
				file.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(a);
			out.flush();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/***
	 * @Comment 从Txt文本逐行读取
	 *          将每一行读取到的字符串写入数组
	 * @param strFile
	 * @return ArrayList<String>
	 */
	public ArrayList<String> readToTXT(String strFile){
		File file = new File(strFile);
		ArrayList<String> arrlist = new ArrayList<String>();
		try{
			if(!file.exists()){
				System.out.println("找不到指定文件");
				return null;
			}
			InputStreamReader read = new InputStreamReader(new FileInputStream(file));
			BufferedReader bufferReader = new BufferedReader(read);
			String lineText = null;
			while((lineText = bufferReader.readLine()) != null){
				arrlist.add(lineText);
				System.out.println(lineText);
			}
			read.close();
		}
		catch(Exception e){
			System.out.println("读取文件错误");
			e.printStackTrace();
		}
		return arrlist;
	}
	
	/***
	 * @Comments 将字符串str写入文件strFile
	 * @Noticed 并不将文件进行清空，而是将字符串追加到文件末尾
	 * @param str
	 */
	public void writeToTXT(String str,String strFile){
		FileOutputStream o = null;
		byte[] buff = new byte[]{};
		try{
			File file = new File(strFile);
			if(!file.exists()){
				file.createNewFile();
			}
			buff=str.getBytes();
			o=new FileOutputStream(file,true);
			o.write(buff);
			o.flush();
			o.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/***
	 * @Comments 清空文件
	 * @Notice 并不删除文件，只是将文件清空
	 * @param strFile
	 */
	public void clearFile(String strFile){
		File file = new File(strFile);
		try{
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write("");
			fileWriter.flush();
			fileWriter.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
