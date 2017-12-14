package com.output.whats;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import com.extend.OperateFile;
import com.extend.Struct;
import com.extend.SPARQLParse;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 获得所有生物过程的原料和产物，将生物过程、产物写入的txt文本中，以便出题使用
 * @Noticed  1.子过程的原料和产物同时也是父过程的原料和产物
 *           eg.水是水的光解的原料，水也是光反应的原料，同时水也是光合作用的原料
 *           2.但如果生物过程的两个子过程中，一个子过程产物作为另一个子过程的原料，那么则不是生物过程的原料和产物
 *           eg.还原氢是水的光解的产物，还原氢也即是光反应的产物
 *              还原氢是三碳化合物的还原的原料，还原氢也即是暗反应的原料
 *              对于光合作用来说，还原氢不作为光合作用的原料和产物
 * @JDKversion JDK1.8
 * @CreatorDate 2017-9
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-22
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class GetOutputFromBioprocess extends SPARQLParse{

	private OntModel model;
	
	private static final String strFilePath = "./src/data/";
	private static final String strFileName = "光合作用.owl";
	
	private static final String strSubFilePath = "./src/data/output/";
	private static final String strOutputFileName = "output.txt";
	
	//前缀
	private static final String strPrefix="http://www.co-ode.org/ontologies/ont.owl#";
	
	protected GetOutputFromBioprocess() {}
	
	protected GetOutputFromBioprocess(String strFile) {
		super(strFile);
		InputStream in = FileManager.get().open(strFile);
		if(in == null)
		{
			throw new IllegalArgumentException("file:"+strFileName+"not found");
		}
		model=ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		//read the RDF/XML File
		model.read(in,"");
		//Write it to standard out
		model.write(System.out);
	}
	
	/***
 	 * @Comments 查找生物过程的子过程
 	 *           将子过程存放在动态数组返回
 	 * @param str
 	 * @return ArrayList<String>
 	 */
 	protected ArrayList<String> getSubprocessFromBioprocess(String strBioprocess){
 		OperateFile opfl = new OperateFile();
 		String strfile=strSubFilePath+"result_sub.xml";
 		String strPredicate="<"+strPrefix+"子过程>";
 		String strBio = "<"+strPrefix+strBioprocess+">";
 		//含有前缀的子过程数组
 		ArrayList <String>arr_sub = new ArrayList<String>();
 		//不含前缀的子过程数组
 		ArrayList<String> arrlistSubprocessNoPrefix = new ArrayList<String>();
 		
		ResultSet result = super.findBioprocessFromParentbio(strBio, strPredicate);
		String result_sub = ResultSetFormatter.asXMLString(result);
		opfl.writeToFile(strfile,result_sub);
		arr_sub=opfl.readXMLFileAboutSubprocess(strfile);
		
		//销毁对象
		opfl=null;
		System.gc();
		//去掉子过程的前缀
		while(arr_sub.size()>0) {
			String strEachBioprocess = arr_sub.get(0).replaceAll(strPrefix, "");
			arr_sub.remove(0);
			arrlistSubprocessNoPrefix.add(strEachBioprocess);
		}
		arr_sub=null;
		return arrlistSubprocessNoPrefix;
 	}
 	
 	/**
 	 * @Comments 查找生物过程的原料并进行打印
 	 * @Flow First:创建一个处理文件的对象
 	 *       Second:查找生物过程的原料
 	 *       Third:得到的原料写入XML文件，对其进行解析
 	 *       Forth:将解析后的原料放入数组
 	 *       Final:打印产物并销毁对象
 	 * @param strBioprocess
 	 * @param arrayListRaw
 	 */
 	protected void printRaw(String strBioprocess,ArrayList <String>arrayListRaw){
 		String strBio="<"+strPrefix+strBioprocess+">";
 		String strRaw="<"+strPrefix+"原料>";
 		String strfile=strSubFilePath+"result_raw.xml";
 		OperateFile opfl = new OperateFile();
 		ResultSet subprocess = super.findRawFromBioprocess(strBio, strRaw);
		String query_raw= ResultSetFormatter.asXMLString(subprocess);
		opfl.writeToFile(strfile,query_raw);
		 //读取XML文件
		ArrayList<String> arrlist=opfl.readXMLFileAboutSubprocess(strfile);
//		System.out.print("原料:");
		for(int i=0;i<arrlist.size();i++){
			String list_raw=arrlist.get(i);
			String list_raw_replace=list_raw.replaceAll(strPrefix, "");  //去掉前缀
//			System.out.print(list_raw_replace+" ");
			arrayListRaw.add(list_raw_replace);
		}
//		System.out.print("\n");
		//销毁对象
		opfl=null;
 	}
 	
	/***
 	 * @Comments 查找并打印生物过程所产生的产物
 	 *           将查找的产物存放在数组里面、
 	 * @Flow First:创建一个处理文件的对象
 	 *       Second:查找生物过程的产物
 	 *       Third:得到的产物写入XML文件，对其进行解析
 	 *       Forth:将解析后的产物放入数组
 	 *       Final:打印产物并销毁创建的对象
 	 * @param strBioprocess
 	 * @param arr_output
 	 */
 	protected void printOutput(String strBioprocess,ArrayList <String>arr_output){
 		OperateFile opfl = new OperateFile();
 		String strBio="<"+strPrefix+strBioprocess+">";
 		String strOutput="<"+strPrefix+"产物>";
 		String strfile=strSubFilePath+"result_output.xml";
 		ResultSet subprocess = super.findOutputFromBioprocess(strBio, strOutput);
		String query_output= ResultSetFormatter.asXMLString(subprocess);
		opfl.writeToFile(strfile,query_output);
		ArrayList <String>arrlist=opfl.readXMLFileAboutSubprocess(strfile); //读取文件里的值
//		System.out.print("产物:");
		for(int i=0;i<arrlist.size();i++){
			String list_output=arrlist.get(i);
			String list_output_replace=list_output.replaceAll(strPrefix,"");  //去掉前缀
//			System.out.print(list_output_replace+" ");
			arr_output.add(list_output_replace);
		}
//		System.out.println();
		//销毁对象
		opfl=null;
 	}
 	
 	/***
 	 * @Comments 删除字符串的最后一个逗号
 	 * @param str
 	 * @return
 	 */
 	protected String deleteComma(String str){
 		str = str.substring(0,str.length()-1);
 		return str;
 	}
 	
 	/****
	 * @Comments 查找原料和产物相同的生物物质
	 *           并将相同的物质去除掉
	 * @param arr_str1
	 * @param arr_str2
	 */
 	protected void checkCommon(ArrayList<String>arr_str1,ArrayList <String>arr_str2)
	{
 		for(int i=0;i<arr_str1.size();i++)
 		{
 			String str1 = arr_str1.get(i);
 			for(int j=0;j<arr_str2.size();j++){
 				String str2 = arr_str2.get(j);
 				if(str1.equals(str2)){
 					arr_str1.remove(str1);
 					arr_str2.remove(str2);
 					i--;
 					j--;
 				}
 			}
 		}
	}
 	
 	/***
	 * @Comments 递归查找生物过程及其原料和产物
	 * @Flow First:建立一个Strut对象，OperateFile对象
	 *       Second:判断生物过程是否为空
	 *       Thirt:若为空，退出：若不为空，查找其子过程
	 *       Fourth:求子过程的原料和产物
	 *       Fifth：将子过程的原料和产物存放的数组返回结构体里面作为父过程的原料和产物
	 *       Sixth:去除作为原料的产物
	 * @param strBioprocess
	 * @return
	 */
	protected Struct recursive(String strBioprocess)
	{
		Struct s = new Struct();
		OperateFile opfl = new OperateFile();
		
		//strFile为./src/data/raw/raw.txt文件
		String strFile=strSubFilePath+strOutputFileName;
		ArrayList <String>arr_raw = new ArrayList<String>();
		ArrayList <String>arr_output = new ArrayList<String>();
		
		if(strBioprocess==null)
			return null;
		ArrayList <String>arr_sub = getSubprocessFromBioprocess(strBioprocess);
		for(int i=0;i<arr_sub.size();i++){
			s.add(recursive((String)getSubprocessFromBioprocess(strBioprocess).get(i)));
		}
		
		opfl.writeToTXT(strBioprocess+"\n",strFile);
		//查找原料和产物并打印，不打印也可以
		printRaw(strBioprocess,arr_raw);
		printOutput(strBioprocess,arr_output);
		
		s.addRawToStruct(arr_raw);
		s.addOutputToStruct(arr_output);
		
		checkCommon(s.struct_arr_raw,s.struct_arr_output);
		
		/****写<产物>进入文件*/
//		opfl.writeToTXT("产物:",strFile);
		
		//将原料整理成字符串
		String s_arr_output="";
		for(int m=0;m<s.struct_arr_output.size();m++){
//			System.out.print((String)s.struct_arr_output.get(m)+" ");
			s_arr_output=s_arr_output+(String)s.struct_arr_output.get(m)+",";
//			writeToTXT((String)s.struct_arr_output.get(m)+",");
		}
		s_arr_output = deleteComma(s_arr_output);
		
		/****写产物进入文件****/
		opfl.writeToTXT(s_arr_output,strFile);
		opfl.writeToTXT("\n", strFile);
	
		arr_output.clear();
		arr_output.clear();
		return s;
	}
	
	public static void main(String args[]){
		
		OperateFile opfl = new OperateFile();
		GetOutputFromBioprocess t1 = new GetOutputFromBioprocess(strFilePath+strFileName);
		
		String photosynthesis ="光合作用";
		String strFile=strSubFilePath+strOutputFileName;
		
		opfl.clearFile(strFile);
		t1.recursive(photosynthesis);
		System.out.println();
		opfl.readToTXT(strSubFilePath+strOutputFileName);
		
		opfl=null;
		t1=null;
	}
}
