package com.extend;

import java.util.ArrayList;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 产生各种物质存放在数组
 *           使用原料和产物查找生物过程
 *           根据生物过程查找父过程
 *           根据父过程查找子过程
 *           ...
 * @JDKversion JDK1.8
 * @CreatorDate 2017-10
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-27
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class GetMaterial {
	
	private static final String strPrefix = "http://www.co-ode.org/ontologies/ont.owl#";
	private static final String strFilePath = "./src/data/";
	private static final String strFileName = "光合作用.owl";
	
	public GetMaterial() {};
	
	/***
 	 * @Comments 查找生物过程的子过程
 	 *           将子过程存放在动态数组返回
 	 * @param strBioprocess
 	 * @param strFile
 	 * @return ArrayList<String>
 	 */
 	public ArrayList<String> getSubprocessFromBioprocess(String strBioprocess,String strFile){
 		SPARQLParse sparql = new SPARQLParse(strFilePath+strFileName);
 		OperateFile opfl = new OperateFile();
 		
 		String strPredicate="<"+strPrefix+"子过程>";
 		String strBio = "<"+strPrefix+strBioprocess+">";
 		ArrayList <String>arr_sub = new ArrayList<String>();                 //含有前缀的子过程数组
 		ArrayList<String> arrlistSubprocessNoPrefix = new ArrayList<String>();//不含前缀的子过程数组
 		
		ResultSet result = sparql.findBioprocessFromParentbio(strBio, strPredicate);
		String result_sub = ResultSetFormatter.asXMLString(result);
		opfl.writeToFile(strFile,result_sub);
		arr_sub=opfl.readXMLFileAboutSubprocess(strFile);
		
		opfl=null;
		sparql = null;
		while(arr_sub.size()>0) {
			String strEachBioprocess = arr_sub.get(0).replaceAll(strPrefix, ""); //去掉子过程的前缀
			arr_sub.remove(0);
			arrlistSubprocessNoPrefix.add(strEachBioprocess);
		}
		arr_sub=null;
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
 	 * @param strFile
 	 * @return
 	 */
 	public ArrayList<String> getParentproFromBioprocess(String strBioprocess,String strFile){
 		OperateFile opfl = new OperateFile();
 		SPARQLParse sparql = new SPARQLParse(strFilePath+strFileName);
 		String strPredicate="<"+strPrefix+"子过程>";
 		String strBio = "<"+strPrefix+strBioprocess+">";
 		
 		ArrayList<String> arrListParentproWithPrefix = new ArrayList<String>();    //含有前缀父过程的数组
 		ArrayList<String> arrListParentproWithoutPrefix = new ArrayList<String>(); //不含前缀父过程的数组
 		
 		ResultSet result = sparql.findParentbioFromBioprocess(strBio, strPredicate);
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
		sparql = null;
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
 	 * @param strFile
 	 */
 	public void printRaw(String strBioprocess,ArrayList<String>arrListRaw,String strFile){
 		SPARQLParse sparql = new SPARQLParse(strFilePath+strFileName);
 		String strBio="<"+strPrefix+strBioprocess+">";
 		String strPredicateForRaw="<"+strPrefix+"原料>";

 		OperateFile opfl = new OperateFile();
 		ResultSet subprocess = sparql.findRawFromBioprocess(strBio, strPredicateForRaw);
		String strRawForFound= ResultSetFormatter.asXMLString(subprocess);
		opfl.writeToFile(strFile,strRawForFound);
		ArrayList<String> arrlist=opfl.readXMLFileAboutSubprocess(strFile);
		for(int i=0;i<arrlist.size();i++){
			String strRawForList=arrlist.get(i);
			String strRawForListReplace=strRawForList.replaceAll(strPrefix, "");  //去掉前缀
			arrListRaw.add(strRawForListReplace);
		}
		opfl=null;
		sparql = null;
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
 	 * @param strFile
 	 */
 	public void printOutput(String strBioprocess,ArrayList<String>arrListOutput,String strFile){
 		SPARQLParse sparql = new SPARQLParse(strFilePath+strFileName);
 		String strBio="<"+strPrefix+strBioprocess+">";
 		String strPredicateForOutput="<"+strPrefix+"产物>";
 		
 		OperateFile opfl = new OperateFile();
 		ResultSet subprocess = sparql.findOutputFromBioprocess(strBio, strPredicateForOutput);
		String strOutputForFound= ResultSetFormatter.asXMLString(subprocess);
		opfl.writeToFile(strFile,strOutputForFound);
		 //读取XML文件
		ArrayList<String> arrlist=opfl.readXMLFileAboutSubprocess(strFile);
//		System.out.print("产物:");
		for(int i=0;i<arrlist.size();i++){
			String strOutputForList=arrlist.get(i);
			String strOutputForListReplace=strOutputForList.replaceAll(strPrefix, "");  //去掉前缀
			arrListOutput.add(strOutputForListReplace);
		}
		opfl=null;
		sparql = null;
 	}
 	
 	/****
 	 * @Comments 根据条件查找生物过程
 	 *           将查找的生物过程写入数组
 	 * @Flow First:调用SPARQLParse类中的求生物过程的函数
 	 *       Second:将得到的生物过程ResultSet格式
 	 *       Thrid:将ResultSet格式写成XML格式
 	 *       Forth：从XML格式中读取写入数组，此时得到含有前缀的生物过程
 	 *       Fivth：去掉前缀，写入数组然后传回
 	 * @param strFactor
 	 * @param strFile
 	 * @return
 	 */
 	public ArrayList<String> getBioprocessFromFactor(String strFactor,String strFile){
 		SPARQLParse sparql = new SPARQLParse(strFilePath+strFileName);
 		String strFactorWithPrefix = "<"+strPrefix+strFactor+">";
 		String strPredicate = "<"+strPrefix+"条件>";
 		OperateFile opfl = new OperateFile();
 		
 		ArrayList<String> arrListBioprocessWithPrefix = new ArrayList<String>();    //含有前缀的生物过程的数组
 		ArrayList<String> arrListBioprocessWithoutPrefix = new ArrayList<String>(); //不含前缀的生物过程的数组
 		
 		ResultSet resultSet = sparql.findBioprocessFromFactor(strFactorWithPrefix, strPredicate);
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
 	 * @Comments 根据原料查找生物过程
 	 *           将查找的生物过程写入数组
 	 * @Flow First:调用SPARQLParse类中的求生物过程的函数
 	 *       Second:将得到的生物过程ResultSet格式
 	 *       Thrid:将ResultSet格式写成XML格式
 	 *       Forth：从XML格式中读取写入数组，此时得到含有前缀的生物过程
 	 *       Fivth：去掉前缀，写入数组然后传回
 	 * @param strRaw
 	 * @param strFile
 	 * @return
 	 */
 	public ArrayList<String> getBioprocessFromRaw(String strRaw,String strFile){
 		SPARQLParse sparql = new SPARQLParse(strFilePath+strFileName);
 		String strRawWithPrefix = "<"+strPrefix+strRaw+">";
 		String strPredicate = "<"+strPrefix+"原料>";
 		OperateFile opfl = new OperateFile();
 		
 		ArrayList<String> arrListBioprocessWithPrefix = new ArrayList<String>();    //含有前缀的生物过程的数组
 		ArrayList<String> arrListBioprocessWithoutPrefix = new ArrayList<String>(); //不含前缀的生物过程的数组
 		
 		ResultSet resultSet = sparql.findBioprocessFromRaw(strRawWithPrefix, strPredicate);
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
 	 * @param strFile
 	 * @return
 	 */
 	public ArrayList<String> getBioprocessFromOutput(String strOutput,String strFile){
 		SPARQLParse sparql = new SPARQLParse(strFilePath+strFileName);
 		String strOutputWithPrefix = "<"+strPrefix+strOutput+">";
 		String strPredicate = "<"+strPrefix+"产物>";
 		OperateFile opfl = new OperateFile();

 		ArrayList<String> arrListBioprocessWithPrefix = new ArrayList<String>();     //含有前缀的生物过程的数组
 		ArrayList<String> arrListBioprocessWithoutPrefix = new ArrayList<String>();  //不含前缀的生物过程的数组
 		
 		ResultSet resultSet = sparql.findBioprocessFromOutput(strOutputWithPrefix, strPredicate);
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
 	
 	/**
 	 * @Comments 查找生物过程的条件并进行打印(不打印也可)
 	 *           将条件存放在数组arrListFactor
 	 * @Flow First:创建一个处理文件的对象
 	 *       Second:查找生物过程的条件
 	 *       Third:得到的条件写入XML文件，对其进行解析
 	 *       Forth:将解析后的条件放入数组
 	 *       Final:打印条件并销毁对象
 	 * @param strBioprocess
 	 * @param arrListOutput
 	 * @param strFile
 	 */
 	public void printFactor(String strBioprocess,ArrayList<String>arrListFactor,String strFile){
 		SPARQLParse sparql = new SPARQLParse(strFilePath+strFileName);
 		String strBio="<"+strPrefix+strBioprocess+">";
 		String strPredicateForFactor="<"+strPrefix+"条件>";
 		
 		OperateFile opfl = new OperateFile();
 		ResultSet subprocess = sparql.findFactorFromBioprocess(strBio, strPredicateForFactor);
		String strFactorForFound= ResultSetFormatter.asXMLString(subprocess);
		opfl.writeToFile(strFile,strFactorForFound);
		 //读取XML文件
		ArrayList<String> arrlist=opfl.readXMLFileAboutSubprocess(strFile);
//		System.out.print("条件:");
		for(int i=0;i<arrlist.size();i++){
			String strFactorForList=arrlist.get(i);
			String strFactorForListReplace=strFactorForList.replaceAll(strPrefix, "");  //去掉前缀
			arrListFactor.add(strFactorForListReplace);
		}
		opfl=null;
		sparql = null;
 	}
 	
 	/***
 	 * @Comments 查找生物资料的子集
 	 *           将子集存放在动态数组返回
 	 * @example 光合色素包含叶绿素和类胡萝卜素
 	 * @param strMaterial
 	 * @param strFile
 	 * @return ArrayList<String>
 	 */
 	public ArrayList<String> getSubsetFromSet(String strMaterial,String strFile){
 		SPARQLParse sparql = new SPARQLParse(strFilePath+strFileName);
 		OperateFile opfl = new OperateFile();
 		
 		String strPredicate="<"+strPrefix+"包括>";
 		String strBio = "<"+strPrefix+strMaterial+">";
 		ArrayList<String> arrListSubsetWithPrefix = new ArrayList<String>();//含有前缀的子集数组
 		ArrayList<String> arrlistSubsetNoPrefix = new ArrayList<String>();  //不含前缀的子集数组
 		
		ResultSet result = sparql.getSubcontentFromContent(strBio, strPredicate);
		String result_sub = ResultSetFormatter.asXMLString(result);
		opfl.writeToFile(strFile,result_sub);
		arrListSubsetWithPrefix=opfl.readXMLFileAboutSubprocess(strFile);
		
		opfl=null;
		sparql = null;
		while(arrListSubsetWithPrefix.size()>0) {
			String strEachBioprocess = arrListSubsetWithPrefix.get(0).replaceAll(strPrefix, ""); //去掉子集的前缀
			arrListSubsetWithPrefix.remove(0);
			arrlistSubsetNoPrefix.add(strEachBioprocess);
		}
		arrListSubsetWithPrefix=null;
		return arrlistSubsetNoPrefix;
 	}
 	
 	/***
 	 * @comments 查找影响光合作用强度的因素
 	 * @param strOWLFile
 	 * @param strFile
 	 * @param allStage
 	 * @return
 	 */
 	public ArrayList<String> getFactorPSYIntensity(String strOWLFile,String strFile,int allStage){
 		SPARQLParse sparql = new SPARQLParse(strOWLFile);
 		OperateFile opfl = new OperateFile();
 		
 		String strPredicate = "<"+strPrefix+"影响>";
 		ArrayList<String> arrListFactorWithPrefix = new ArrayList<String>();//含有前缀的子集数组
 		ArrayList<String> arrlistFactorNoPrefix = new ArrayList<String>();  //不含前缀的子集数组
 		
 		for(int i=1;i<=allStage;i++)
		{
 			ResultSet result = sparql.getFactorFromPSYIntensity(strPredicate,i);
 			String result_sub = ResultSetFormatter.asXMLString(result);
 			opfl.writeToFile(strFile,result_sub);
 			arrListFactorWithPrefix=opfl.readXMLFileAboutSubprocess(strFile);
 			while(arrListFactorWithPrefix.size()>0) {
 				String strEachBioprocess = arrListFactorWithPrefix.get(0).replaceAll(strPrefix, ""); //去掉子集的前缀
 				arrListFactorWithPrefix.remove(0);
 				arrlistFactorNoPrefix.add(strEachBioprocess);
 			}
		}	
		opfl=null;
		sparql = null;
		arrListFactorWithPrefix=null;
		return arrlistFactorNoPrefix;
 	}
 	
 	/****
 	 * @comments 获得原因因素
 	 * @param strOWLFile
 	 * @param strFile
 	 * @param strFactor
 	 * @return
 	 */
 	public ArrayList<String> getOriginFactor(String strOWLFile,String strFile,String strFactor){
 		SPARQLParse sparql = new SPARQLParse(strOWLFile);
 		OperateFile opfl = new OperateFile();
 		
 		String strPredicate = "<"+strPrefix+"影响>";
 		String strResultFactor = "<" +strPrefix+strFactor+">";
 		ArrayList<String> arrListFactorWithPrefix = new ArrayList<String>();//含有前缀的子集数组
 		ArrayList<String> arrlistFactorNoPrefix = new ArrayList<String>();  //不含前缀的子集数组
 		
		ResultSet result = sparql.getInfluencingFactor(strResultFactor, strPredicate);
		String result_sub = ResultSetFormatter.asXMLString(result);
		opfl.writeToFile(strFile,result_sub);
		arrListFactorWithPrefix=opfl.readXMLFileAboutSubprocess(strFile);
		while(arrListFactorWithPrefix.size()>0) {
			String strEachBioprocess = arrListFactorWithPrefix.get(0).replaceAll(strPrefix, ""); //去掉子集的前缀
			arrListFactorWithPrefix.remove(0);
			arrlistFactorNoPrefix.add(strEachBioprocess);
		}
		opfl=null;
		sparql = null;
		arrListFactorWithPrefix=null;
		return arrlistFactorNoPrefix;
 	}
 	
 	/*****
 	 * @comments 获得影响因素的变化方向
 	 * @param strOWLFile
 	 * @param strFile
 	 * @param strFactor
 	 * @return
 	 */
 	public ArrayList<String> getChangeDirection(String strOWLFile,String strFile,String strFactor){
 		SPARQLParse sparql = new SPARQLParse(strOWLFile);
 		OperateFile opfl = new OperateFile();
 		
 		String strPredicate = "<"+strPrefix+"变化>";
 		String strResultFactor = "<" +strPrefix+strFactor+">";
 		ArrayList<String> arrListDirectionWithPrefix = new ArrayList<String>();//含有前缀的子集数组
 		ArrayList<String> arrlistDirectionNoPrefix = new ArrayList<String>();  //不含前缀的子集数组
 		
		ResultSet result = sparql.howToChange(strResultFactor, strPredicate);
		String result_sub = ResultSetFormatter.asXMLString(result);
		opfl.writeToFile(strFile,result_sub);
		arrListDirectionWithPrefix=opfl.readXMLFileAboutSubprocess(strFile);
		while(arrListDirectionWithPrefix.size()>0) {
			String strEachBioprocess = arrListDirectionWithPrefix.get(0).replaceAll(strPrefix, ""); //去掉子集的前缀
			arrListDirectionWithPrefix.remove(0);
			arrlistDirectionNoPrefix.add(strEachBioprocess);
		}
		opfl=null;
		sparql = null;
		arrListDirectionWithPrefix=null;
		return arrlistDirectionNoPrefix;
 	}
}
