package com.experiment.sunnySummer;

import java.util.ArrayList;

import com.extend.GetMaterial;
import com.extend.OperateFile;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 用来打印条件和原料对应的生物过程
 * @JDKversion JDK1.8
 * @CreatorDate 2017-12-29
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-12-29
 * @Version 2.0.0
 * @author User
 */
public class PrintCircuit {
	
	/***
	 * @comments 递归查找所有生物过程，直到光合作用为止
	 * @param arrProcess
	 * @param strBProcess
	 */
	private void getCircuit(ArrayList<String>arrProcess,String strBProcess) {
		if(strBProcess.equals(null))
			return;
		arrProcess.add(strBProcess);
		GetMaterial gm = new GetMaterial();
		String strFilePath = "./src/data/experiment/";
		String strFileName = "Process.xml";
		String strFile = strFilePath+strFileName;
		ArrayList<String> arrBProcess = new ArrayList<String>();
		arrBProcess=gm.getParentproFromBioprocess(strBProcess, strFile);
		if(!arrBProcess.isEmpty()) {
			String strprocess = arrBProcess.get(0);
			getCircuit(arrProcess,strprocess);
		}
	}
	
	/****
	 * @comments 打印原料对应的生物过程
	 * @Example 二氧化碳如何影响光合作用
	 *          二氧化碳  二氧化碳的固定  暗反应  光合作用
	 * @param strRaw
	 * @param strResultFile
	 */
	protected void printCircuitOfRaw(String strRaw,String strResultFile) {
		GetMaterial gm = new  GetMaterial();
		OperateFile opfl = new OperateFile();
		String strFilePath = "./src/data/experiment/";
		String strFileName = "Process.xml";
		String strFile = strFilePath+strFileName;               //用来解析存放生物过程的XML文件
		
		ArrayList<String> arrRProcess = new ArrayList<String>();//用来存放原料在哪个生物过程中使用
		arrRProcess=gm.getBioprocessFromRaw(strRaw, strFile);
		for(int i=0;i<arrRProcess.size();i++)
		{
			ArrayList<String> arrRaw = new ArrayList<String>();     //用来存放原料及生物过程
			String strRProcess = arrRProcess.get(i);
			arrRaw.add(strRaw);
			getCircuit(arrRaw,strRProcess);
			if(arrRaw.size()>1){
				String strResult = arrRaw.get(0)+"是"+arrRaw.get(1)+"的原料，";
				String strSent0="";
				for(int t=1;t<arrRaw.size()-1;t++)
				{
					 strSent0 = strSent0+arrRaw.get(t)+"又是"+arrRaw.get(t+1)+"的子过程,";
				}
				String strSent1 = "从而"+arrRaw.get(0)+"影响"+"光合作用。";
				strResult = strResult+strSent0+strSent1;
				System.out.println(strResult);
				opfl.writeToTXT(strResult+"\n", strResultFile);
			}
			arrRaw=null;
		}
		opfl=null;
		gm=null;
		arrRProcess=null;
	}
	
	/***
	 * @comments 获得条件及其生物过程
	 * @example 光照  水的光解  光反应  光合作用
	 * @param strFactor
	 */
	protected void printCircuitOfFactor(String strFactor,String strResultFile) {
		GetMaterial gm = new  GetMaterial();
		OperateFile opfl = new OperateFile();
		String strFilePath = "./src/data/experiment/";
		String strFileName = "Process.xml";
		String strFile = strFilePath+strFileName;               //用来解析存放生物过程的XML文件
		ArrayList<String> arrFProcess = new ArrayList<String>();//用来存放条件在哪个生物过程中使用
		arrFProcess=gm.getBioprocessFromFactor(strFactor, strFile);
		for(int i=0;i<arrFProcess.size();i++)
		{
			ArrayList<String> arrFactor = new ArrayList<String>();  //用来存放条件及生物过程
			String strFProcess = arrFProcess.get(i);
			arrFactor.add(strFactor);
			getCircuit(arrFactor,strFProcess);
			if(arrFactor.size()>1){
				String strResult = arrFactor.get(0)+"是"+arrFactor.get(1)+"的条件，";
				String strSent0="";
				for(int t=1;t<arrFactor.size()-1;t++)
				{
					 strSent0 = strSent0+arrFactor.get(t)+"又是"+arrFactor.get(t+1)+"的子过程,";
				}
				String strSent1 = "从而"+arrFactor.get(0)+"影响"+"光合作用。";
				strResult = strResult+strSent0+strSent1;
				System.out.println(strResult);
				opfl.writeToTXT(strResult+"\n", strResultFile);
			}
			arrFactor=null;
		}
		gm=null;
		opfl=null;
		arrFProcess=null;
	}
	public static void main(String[] args) {
	}
}
