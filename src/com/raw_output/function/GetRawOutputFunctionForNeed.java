package com.raw_output.function;

import java.util.ArrayList;

import com.extend.GetMaterial;
import com.extend.OperateFile;
import com.extend.Struct;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 查找原料和产物的生物过程
 * @format 水的光解 还原氢
 *         光合作用 水的光解 还原氢
 *         ...
 * @JDKversion JDK1.8
 * @CreatorDate 2017-10
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-29
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class GetRawOutputFunctionForNeed {

	private static final String strFilePath ="./src/data/raw_output/function/";
	private static final String strFileForResultRawName = "rawKeyword.txt";
	private static final String strFileForResultOutputName = "outputKeyword.txt";
	private static final String strFileForRawXMLName= "raw.xml";
	private static final String strFileForOutputXMLName= "output.xml";
	private static final String strFileForSubXMLName= "sub.xml";
	private static final String strFileForParentXMLName= "parent.xml";
	
	public GetRawOutputFunctionForNeed() {}
	
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
 			for(int j=0;j<arr_str2.size();j++)
 			{
 				String str2 = arr_str2.get(j);
 				if(str1.equals(str2))
 				{
 					arr_str1.remove(str1);
 					arr_str2.remove(str2);
 					i--;
 					j--;
 				}
 			}
 		}
	}
 	
 	/***
 	 * @Comments 递归进行查找生物过程,将其进行打印
 	 *           将找到的生物过程写入到数组
 	 * @format [光合作用  ... ...]
 	 * @param arrayListBioprocess
 	 * @param strMaterial
 	 */
 	private String recursiveFindParprocess(ArrayList<String> arrayListBioprocess,String strBioprocess) {
 		GetMaterial gm = new GetMaterial();
 		String strFileForParentprocess = strFilePath + strFileForParentXMLName;   //存放父过程的XML文件
 		ArrayList<String> arrayListParentpro = new ArrayList<String>();           //存放父过程的数组
 		
 		if(strBioprocess == null) {
 			return null;
 		}
 		arrayListParentpro=gm.getParentproFromBioprocess(strBioprocess,strFileForParentprocess);
 		String strParentpro = null;
 		if(arrayListParentpro.size()>0) {
 			strParentpro=arrayListParentpro.get(0);
 		}
 		recursiveFindParprocess(arrayListBioprocess,strParentpro);
 		arrayListBioprocess.add(strBioprocess);
 		return strBioprocess;
 	}
 	
 	/*****
 	 * @comments 递归查找生物过程的所有原料和产物的生物过程
 	 * @flow  First:查找所有原料和产物
 	 *        Second:去除掉相同的原料和产物
 	 *        Third:对剩余的原料和产物查找其所有生物过程
 	 *        Forth:将得到的写入到文件
 	 * @format 水的光解 还原氢
 	 *         光合作用 水的光解 还原氢
 	 *         ...
 	 * @param strBioprocess
 	 * @return
 	 */
 	protected Struct recursiveFindMaterial(String strBioprocess) {
		Struct s = new Struct();
		OperateFile opfl = new OperateFile();
		GetMaterial gm = new GetMaterial();
		
		String strFileResultRaw = strFilePath+strFileForResultRawName;      //存放结果原料的文件
		String strFileResultOutput = strFilePath+strFileForResultOutputName;//存放结果产物的文件
		String strFileForRaw = strFilePath +strFileForRawXMLName;           //存放原料的XML文件
		String strFileForOutput = strFilePath +strFileForOutputXMLName;     //存放产物的XML文件
		String strFileForSubprocess = strFilePath + strFileForSubXMLName;   //存放子过程的XML文件
		ArrayList <String>arrListRaw = new ArrayList<String>();             //存放原料的数组
		ArrayList <String>arrListOutput = new ArrayList<String>();          //存放产物的数组
		
		if(strBioprocess==null)
			return null;
		ArrayList <String>arr_sub = gm.getSubprocessFromBioprocess(strBioprocess,strFileForSubprocess);  //获得生物过程的子过程
		for(int i=0;i<arr_sub.size();i++){
			String strSubprocess=arr_sub.get(i);
			s.add(recursiveFindMaterial(strSubprocess));
		}
		/****查找原料和产物并打印，不打印也可以*/
		gm.printRaw(strBioprocess,arrListRaw,strFileForRaw);               //查找生物过程的原料,将原料存放至数组
		gm.printOutput(strBioprocess,arrListOutput,strFileForOutput);      //查找生物过程的产物,将产物存放至数组
		s.addRawToStruct(arrListRaw);           //将子过程使用的原料保存至父过程
		s.addOutputToStruct(arrListOutput);     //将子过程产生的产物保存至父过程
		
		checkCommon(s.struct_arr_raw,s.struct_arr_output);
		/****对于剩下的每一个生物物质<原料>，查找其所有父过程*/
		for(int m=0;m<s.struct_arr_raw.size();m++){
			String strEachStructArrayListRaw=s.struct_arr_raw.get(m);
			
			opfl.writeToTXT(strEachStructArrayListRaw+" ", strFileResultRaw);   //写原料进入文件
			opfl.writeToTXT(strBioprocess+"\n",strFileResultRaw);               //写此时的生物过程进入文件
		
 	 		ArrayList<String> arrListBioprocessForRaw = new ArrayList<String>();    //存放有关使用原料的生物过程
 			ArrayList<String> arrListGetBioprocessForRaw = new ArrayList<String>(); //得到直接使用原料的生物过程
 			arrListGetBioprocessForRaw =gm.getBioprocessFromRaw(strEachStructArrayListRaw,strFileForSubprocess);	
 			String strBioproForRaw = arrListGetBioprocessForRaw.get(0);             //得到直接使用原料的生物过程
 			recursiveFindParprocess(arrListBioprocessForRaw,strBioproForRaw);
	 			
 			for(int j=0;j<arrListBioprocessForRaw.size();j++) {
 	 			String strEachBioprocessForRaw = arrListBioprocessForRaw.get(j);
 				System.out.print(strEachBioprocessForRaw+" ");
 				opfl.writeToTXT(strEachBioprocessForRaw+" ", strFileResultRaw); //写生物过程进入文件
 	 		}
 	 		System.out.println();
 	 		opfl.writeToTXT("\n", strFileResultRaw);
 	 		
 	 		arrListBioprocessForRaw = null;
 			arrListGetBioprocessForRaw=null;
		}
		/******对于剩下的每一个生物物质<产物>，查找其所有父过程*/
		for(int m=0;m<s.struct_arr_output.size();m++){
			String strEachStructArrayListOutput=s.struct_arr_output.get(m);
			
			opfl.writeToTXT(strEachStructArrayListOutput+" ", strFileResultOutput);   //写产物进入文件
			opfl.writeToTXT(strBioprocess+"\n",strFileResultOutput);                  //写此时的生物过程进入文件
		
 	 		ArrayList<String> arrListBioprocessForOutput = new ArrayList<String>();    //存放有关使用产物的生物过程
 			ArrayList<String> arrListGetBioprocessForOutput = new ArrayList<String>(); //得到直接使用产物的生物过程
 			arrListGetBioprocessForOutput =gm.getBioprocessFromOutput(strEachStructArrayListOutput,strFileForSubprocess);	
 			String strBioproForOutput = arrListGetBioprocessForOutput.get(0);          //得到直接使用产物的生物过程
 			recursiveFindParprocess(arrListBioprocessForOutput,strBioproForOutput);
	 			
 			for(int j=0;j<arrListBioprocessForOutput.size();j++) {
 	 			String strEachBioprocessForOutput = arrListBioprocessForOutput.get(j);
 				System.out.print(strEachBioprocessForOutput+" ");
 				opfl.writeToTXT(strEachBioprocessForOutput+" ", strFileResultOutput); //写生物过程进入文件
 	 		}
 	 		System.out.println();
 	 		opfl.writeToTXT("\n", strFileResultOutput);
 	 		
 	 		arrListBioprocessForOutput = null;
 			arrListGetBioprocessForOutput=null;
		}
		arrListRaw.clear();
		arrListOutput.clear();
		return s;
	}
 	
 	public static void main(String[] args) {
 		GetRawOutputFunctionForNeed gn = new GetRawOutputFunctionForNeed();
 		OperateFile opfl = new OperateFile();
 		String strBioprocess = "光合作用";
 		String strFileResultRaw = strFilePath+strFileForResultRawName;      //存放结果原料的文件
		String strFileResultOutput = strFilePath+strFileForResultOutputName;//存放结果产物的文件
 		opfl.clearFile(strFileResultRaw);
 		opfl.clearFile(strFileResultOutput);
 		gn.recursiveFindMaterial(strBioprocess);
 		opfl =null;
 		gn=null;
 	}
}
