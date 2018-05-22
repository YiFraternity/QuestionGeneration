package com.raw_output.relation;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import com.extend.*;
/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 获得需要的原料、产物和生物过程
 *           倘若A过程的产物作为了B过程的原料，则返回
 *           原料（产物）、A过程以及B过程，
 *           同时返回使用原料的生物过程及他的父过程及生成原料的生物过程及父过程
 * @ReturnFormat [原料  A过程  B过程]
 *               [生物过程   父过程  ...]
 *               [生物过程  父过程   ...]
 *               [....]
 * @Noticed  
 * @JDKversion JDK1.8
 * @CreatorDate 2017-10
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-23
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class GetRawOutputForRelationNeed extends SPARQLParse{
	
	private OntModel model;
	
	private static final String strFilePath = "./src/data/";
	private static final String strFileName = "光合作用.owl";
	private static final String strSubFilePath="./src/data/raw_output/relation/";
	private static final String strRawOutputFileName = "raw_output.txt";
	private static final String strPrefix = "http://www.co-ode.org/ontologies/ont.owl#";
	
	protected GetRawOutputForRelationNeed() {}
	/***
	 * @Comments 含参构造函数
	 * @param strFile
	 */
	protected GetRawOutputForRelationNeed(String strFile) {
			super(strFile);
			InputStream in = FileManager.get().open(strFile);
			if(in == null)
			{
				throw new IllegalArgumentException("file:"+strFileName+"not found");
			}
			model=ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
			
			model.read(in,"");      //read the RDF/XML File
			model.write(System.out);//Write it to standard out
	}
	
	/***
 	 * @Comments 查找生物过程的子过程
 	 *           将子过程存放在动态数组返回
 	 * @param str
 	 * @return ArrayList<String>
 	 */
 	protected ArrayList<String> getSubprocessFromBioprocess(String strBioprocess){
 		OperateFile opfl = new OperateFile();
 		String strFile=strSubFilePath+"result_sub.xml";
 		String strPredicate="<"+strPrefix+"子过程>";
 		String strBio = "<"+strPrefix+strBioprocess+">";
 		
 		ArrayList<String> arrlistSubprocessWithPrefix = new ArrayList<String>();//含有前缀的子过程数组
 		ArrayList<String> arrlistSubprocessNoPrefix = new ArrayList<String>();  //不含前缀的子过程数组
 		
		ResultSet result = super.findBioprocessFromParentbio(strBio, strPredicate);
		String result_sub = ResultSetFormatter.asXMLString(result);
		opfl.writeToFile(strFile,result_sub);
		arrlistSubprocessWithPrefix=opfl.readXMLFileAboutSubprocess(strFile);
		
		opfl=null;//销毁对象
		//去掉子过程的前缀
		while(arrlistSubprocessWithPrefix.size()>0) {
			String strEachBioprocess = arrlistSubprocessWithPrefix.get(0).replaceAll(strPrefix, "");
			arrlistSubprocessWithPrefix.remove(0);
			arrlistSubprocessNoPrefix.add(strEachBioprocess);
		}
		arrlistSubprocessWithPrefix=null;
		return arrlistSubprocessNoPrefix;
 	}
 	
 	/***
 	 * @Comments 求生物过程的父过程
 	 *           将父过程写入到数组里面、
 	 * @Flow First:调用SPARQLParse类中的求父过程的函数
 	 *       Second:将得到的父过程ResultSet格式
 	 *       Thrid:将ResultSet格式写成XML格式
 	 *       Forth：从XML格式中读取写入数组，此时得到含有前缀的父过程
 	 *       Fivth：去掉前缀，写入数组然后传回
 	 * @param strBioprocess
 	 * @return
 	 */
 	protected ArrayList<String> getParentproFromBioprocess(String strBioprocess){
 		OperateFile opfl = new OperateFile();
 		String strFile=strSubFilePath+"result_sub.xml";
 		String strPredicate="<"+strPrefix+"子过程>";
 		String strBio = "<"+strPrefix+strBioprocess+">";
 		
 		ArrayList<String> arrListParentproWithPrefix = new ArrayList<String>();   //含有前缀父过程的数组
 		ArrayList<String> arrListParentproWithoutPrefix = new ArrayList<String>();//不含前缀父过程的数组
 		
 		ResultSet result = super.findParentbioFromBioprocess(strBio, strPredicate);
 		String strResult = ResultSetFormatter.asXMLString(result);
 		opfl.writeToFile(strFile, strResult);
 		arrListParentproWithPrefix = opfl.readXMLFileAboutSubprocess(strFile);
 		
 		while(arrListParentproWithPrefix.size()>0) {
 			String strEachParentpro = arrListParentproWithPrefix.get(0);
 			strEachParentpro = strEachParentpro.replaceAll(strPrefix, "");
 			arrListParentproWithoutPrefix.add(strEachParentpro);
 			arrListParentproWithPrefix.remove(0);
 		}
 		opfl=null;
 		arrListParentproWithPrefix = null;
 		return arrListParentproWithoutPrefix;
 	}
 	
 	/**
 	 * @Comments 查找生物过程的原料并进行打印(不打印也可)
 	 *           将原料存放在数组arrListRaw
 	 * @Flow First:创建一个处理文件的对象
 	 *       Second:查找生物过程的原料
 	 *       Third:得到的原料写入XML文件，对其进行解析
 	 *       Forth:将解析后的原料放入数组
 	 *       Final:打印产物并销毁对象
 	 * @param strBioprocess
 	 * @param arrListRaw
 	 */
 	protected void printRaw(String strBioprocess,ArrayList<String>arrListRaw){
 		String strBio="<"+strPrefix+strBioprocess+">";
 		String strPredicateForRaw="<"+strPrefix+"原料>";
 		String strFile=strSubFilePath+"result_raw.xml";
 		OperateFile opfl = new OperateFile();
 		ResultSet subprocess = super.findRawFromBioprocess(strBio, strPredicateForRaw);
		String strRawForFound= ResultSetFormatter.asXMLString(subprocess);
		opfl.writeToFile(strFile,strRawForFound);
		 
		ArrayList<String> arrlist=opfl.readXMLFileAboutSubprocess(strFile);      //读取XML文件
		for(int i=0;i<arrlist.size();i++){
			String strRawForList=arrlist.get(i);
			String strRawForListReplace=strRawForList.replaceAll(strPrefix, "");  //去掉前缀
			arrListRaw.add(strRawForListReplace);
		}
		opfl=null;
 	}
 	
 	/**
 	 * @Comments 查找生物过程的产物并进行打印(不打印也可)
 	 *           将产物存放在数组arrListOutput
 	 * @Flow First:创建一个处理文件的对象
 	 *       Second:查找生物过程的产物
 	 *       Third:得到的产物写入XML文件，对其进行解析
 	 *       Forth:将解析后的产物放入数组
 	 *       Final:打印产物并销毁对象
 	 * @param strBioprocess
 	 * @param arrListOutput
 	 */
 	protected void printOutput(String strBioprocess,ArrayList<String>arrListOutput){
 		String strBio="<"+strPrefix+strBioprocess+">";
 		String strPredicateForOutput="<"+strPrefix+"产物>";
 		String strFile=strSubFilePath+"result_Output.xml";
 		OperateFile opfl = new OperateFile();
 		ResultSet subprocess = super.findOutputFromBioprocess(strBio, strPredicateForOutput);
		String strOutputForFound= ResultSetFormatter.asXMLString(subprocess);
		opfl.writeToFile(strFile,strOutputForFound);
		ArrayList<String> arrlist=opfl.readXMLFileAboutSubprocess(strFile);            //读取XML文件
		
		for(int i=0;i<arrlist.size();i++){
			String strOutputForList=arrlist.get(i);
			String strOutputForListReplace=strOutputForList.replaceAll(strPrefix, "");  //去掉前缀
			arrListOutput.add(strOutputForListReplace);
		}
		opfl=null;
 	}
 	
 	/****
 	 * @Comments 根据原料查找生物过程
 	 *           将查找的生物过程写入数组
 	 * @Flow First:调用SPARQLParse类中的求生物过程的函数
 	 *       Second:将得到的生物过程ResultSet格式
 	 *       Thrid:将ResultSet格式写成XML格式
 	 *       Forth：从XML格式中读取写入数组，此时得到含有前缀的生物过程
 	 *       Fivth：去掉前缀，写入数组然后传回
 	 * @param strRaw
 	 * @return
 	 */
 	protected ArrayList<String> getBioprocessFromRaw(String strRaw){
 		String strRawWithPrefix = "<"+strPrefix+strRaw+">";
 		String strPredicate = "<"+strPrefix+"原料>";
 		OperateFile opfl = new OperateFile();
 		
 		String strFile = strSubFilePath + "bioprocess.xml";
 		
 		ArrayList<String> arrListBioprocessWithPrefix = new ArrayList<String>();   //含有前缀的生物过程的数组
 		ArrayList<String> arrListBioprocessWithoutPrefix = new ArrayList<String>();//不含前缀的生物过程的数组
 		
 		ResultSet resultSet = super.findBioprocessFromRaw(strRawWithPrefix, strPredicate);
 		String strResult = ResultSetFormatter.asXMLString(resultSet);
 		
 		opfl.writeToFile(strFile, strResult);
 		arrListBioprocessWithPrefix = opfl.readXMLFileAboutSubprocess(strFile);
 		
 		while(arrListBioprocessWithPrefix.size()>0) {
 			String strEachBioprocess = arrListBioprocessWithPrefix.get(0);
 			strEachBioprocess = strEachBioprocess.replaceAll(strPrefix, "");
 			arrListBioprocessWithoutPrefix.add(strEachBioprocess);
 			arrListBioprocessWithPrefix.remove(0);
 		}
 		
 		return arrListBioprocessWithoutPrefix;
 	}
 	
 	/****
 	 * @Comments 根据产物查找生物过程
 	 *           将查找的生物过程写入数组
 	 * @Flow First:调用SPARQLParse类中的求生物过程的函数
 	 *       Second:将得到的生物过程ResultSet格式
 	 *       Thrid:将ResultSet格式写成XML格式
 	 *       Forth：从XML格式中读取写入数组，此时得到含有前缀的生物过程
 	 *       Fivth：去掉前缀，写入数组然后传回
 	 * @param strOuput
 	 * @return
 	 */
 	protected ArrayList<String> getBioprocessFromOutput(String strOutput){
 		String strOutputWithPrefix = "<"+strPrefix+strOutput+">";
 		String strPredicate = "<"+strPrefix+"产物>";
 		OperateFile opfl = new OperateFile();
 		
 		String strFile = strSubFilePath + "bioprocess.xml";
 		
 		ArrayList<String> arrListBioprocessWithPrefix = new ArrayList<String>();   //含有前缀的生物过程的数组
 		ArrayList<String> arrListBioprocessWithoutPrefix = new ArrayList<String>();//不含前缀的生物过程的数组
 		
 		ResultSet resultSet = super.findBioprocessFromOutput(strOutputWithPrefix, strPredicate);
 		String strResult = ResultSetFormatter.asXMLString(resultSet);
 		
 		opfl.writeToFile(strFile, strResult);
 		arrListBioprocessWithPrefix = opfl.readXMLFileAboutSubprocess(strFile);
 		
 		while(arrListBioprocessWithPrefix.size()>0) {
 			String strEachBioprocess = arrListBioprocessWithPrefix.get(0);
 			strEachBioprocess = strEachBioprocess.replaceAll(strPrefix, "");
 			arrListBioprocessWithoutPrefix.add(strEachBioprocess);
 			arrListBioprocessWithPrefix.remove(0);
 		}
 		
 		return arrListBioprocessWithoutPrefix;
 	}
 	/****
	 * @Comments 查找原料和产物相同的生物物质
	 *           并将相同的物质去除掉,并将相同的生物物质添加到动态数组里面
	 *           返回动态数组
	 * @Noticed 去除相同物质后，数组大小减一
	 * @param arrListForRaw
	 * @param arrListForOutput
	 * @return ArrayList<String>
	 */
 	protected ArrayList<String> checkCommon(ArrayList<String>arrListForRaw,ArrayList <String>arrListForOutput){
 		ArrayList<String> arrListCommonBiomaterial = new ArrayList<String>();//共同的生物物质
 		for(int i=0;i<arrListForRaw.size();i++)
 		{
 			String strRaw =arrListForRaw.get(i);
 			for(int j=0;j<arrListForOutput.size();j++){
 				String strOutput =arrListForOutput.get(j);
 				if(strRaw.equals(strOutput)){
 					arrListCommonBiomaterial.add(strRaw);
 					arrListForRaw.remove(strRaw);
 					arrListForOutput.remove(strOutput);
 					i--;
 					j--;
 				}
 			}
 		}
 		return arrListCommonBiomaterial;
	}

 	/**
 	 * @Comments 递归进行查找生物过程,将其进行打印
 	 * @param arrayListBioprocess
 	 * @param strMaterial
 	 */
 	public String recursiveFindParprocess(ArrayList<String> arrayListBioprocess,String strBioprocess) {
 		ArrayList<String> arrayListParentpro = new ArrayList<String>();//存放父过程的数组
 		if(strBioprocess == null) {
 			return null;
 		}
 		arrayListParentpro=getParentproFromBioprocess(strBioprocess);
 		String strParentpro = null;
 		if(arrayListParentpro.size()>0) {
 			strParentpro=arrayListParentpro.get(0);
 		}
 		recursiveFindParprocess(arrayListBioprocess,strParentpro);
 		arrayListBioprocess.add(strBioprocess);
 		return strBioprocess;
 	}
 	
 	/***
 	 * @Comments 求生物过程A与生物过程B如何建立联系
 	 *           生物过程B生成的产物作为生物过程A的原料，也就A与B建立了联系
 	 *           eg.(光反应和暗反应如何产生联系)
 	 *           光反应中水的光解的产物还原氢作为了暗反应中三碳化合物的原料
 	 * @flow  First:查找原料和产物，将共同的原料和产物写入到数组
 	 *        Second:对共同的原料和产物进行查找其对应的生物过程
 	 *        Third:对生物过程查找其所有父过程
 	 *        Forth:将查找的东西所有写入到文件
 	 * @print [[原料(产物 )]
 	 *         [生物过程  子过程  ... ]   //使用该原料的生物过程
 	 *         [生物过程 子过程   ... ]   //生成该产物的生物过程
 	 *         [...]]
 	 * @param strBioprocess
 	 * @return
 	 */
 	public Struct recursive(String strBioprocess) {
 		Struct st = new Struct();
 		
 		ArrayList<String> arrListRaw = new ArrayList<String>();           //存放原料的数组
 		ArrayList<String> arrListOutput = new ArrayList<String>();        //存放产物的数组
 		ArrayList<String> arrListSubprocess = new ArrayList<String>();    //存放子过程的数组
 		ArrayList<String> arrListCommonMaterial = new ArrayList<String>();//存放共同部分的数组
 			
 		if(strBioprocess==null)
			return null;
 		
 		arrListSubprocess = getSubprocessFromBioprocess(strBioprocess);
 		
 		for(int i=0;i<arrListSubprocess.size();i++) {
 			String strEachSubprocess = arrListSubprocess.get(i);
 			st.add(recursive(strEachSubprocess));
 		}
 		
 		printRaw(strBioprocess,arrListRaw);
 		printOutput(strBioprocess,arrListOutput);
 		
 		st.addRawToStruct(arrListRaw);
 		st.addOutputToStruct(arrListOutput);
 		
 		arrListCommonMaterial = checkCommon(st.struct_arr_raw,st.struct_arr_output);
 		
 		for(int i=0;i<arrListCommonMaterial.size();i++) {                         //求共同生物物质的生物工程
 			OperateFile opfl = new OperateFile();
 	 		ArrayList<String> arrListBioprocessForRaw = new ArrayList<String>();   //存放有关使用原料的生物过程
 	 		ArrayList<String> arrListBioprocessForOutput = new ArrayList<String>();//存放有关生成产物的生物过程
 	 		
 			String strCommonMaterial = arrListCommonMaterial.get(i);
 			System.out.println(strCommonMaterial);
 			opfl.writeToTXT(strCommonMaterial+"\n", strSubFilePath+strRawOutputFileName);
 			
 			ArrayList<String> arrListGetBioprocessForRaw = getBioprocessFromRaw(strCommonMaterial);
 			String strBioproForRaw = arrListGetBioprocessForRaw.get(0);
 			recursiveFindParprocess(arrListBioprocessForRaw,strBioproForRaw);
 			
 			ArrayList<String> arrListGetBioprocessForOutput = getBioprocessFromOutput(strCommonMaterial);
 			String strBioproForOutput = arrListGetBioprocessForOutput.get(0);
 			recursiveFindParprocess(arrListBioprocessForOutput,strBioproForOutput);
 			
 			for(int j=0;j<arrListBioprocessForRaw.size();j++) {
 	 			String strEachBioprocessForRaw = arrListBioprocessForRaw.get(j);
 				System.out.print(strEachBioprocessForRaw+" ");
 				opfl.writeToTXT(strEachBioprocessForRaw+" ", strSubFilePath+strRawOutputFileName);
 	 		}
 	 		System.out.println();
 	 		opfl.writeToTXT("\n", strSubFilePath+strRawOutputFileName);
 	 		for(int j=0;j<arrListBioprocessForOutput.size();j++) {
 	 			String strEachBioprocessForOutput = arrListBioprocessForOutput.get(j);
 				System.out.print(strEachBioprocessForOutput+" ");
 				opfl.writeToTXT(strEachBioprocessForOutput+" ", strSubFilePath+strRawOutputFileName);
 	 		}
 	 		System.out.println();
 	 		opfl.writeToTXT("\n", strSubFilePath+strRawOutputFileName);
 	 		arrListBioprocessForRaw = null;
 	 		arrListBioprocessForOutput = null;
 	 		
 			arrListGetBioprocessForRaw=null;
 			arrListGetBioprocessForOutput=null;
 			
 			opfl=null;
 		}
 		return st;
 	}
 	
 	public static void main(String[] args) {
 		GetRawOutputForRelationNeed gro = new GetRawOutputForRelationNeed(strFilePath+strFileName);
 		String strBioprocess = "光合作用";
 		OperateFile opfl = new OperateFile();
 		opfl.clearFile(strSubFilePath+strRawOutputFileName);
 		gro.recursive(strBioprocess);
 	}
}