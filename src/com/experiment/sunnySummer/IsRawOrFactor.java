package com.experiment.sunnySummer;

import java.util.ArrayList;

import com.extend.GetMaterial;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 用来判断是否是原料还是条件
 * @JDKversion JDK1.8
 * @CreatorDate 2017-12-29
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-12-29
 * @Version 2.0.0
 * @author User
 */
public class IsRawOrFactor {
	
	public IsRawOrFactor() {}
	
	/***
	 * @comments 去除掉原料和产物中共有的部分
	 * @param arrListRaw
	 * @param arrListOutput
	 */
	private void removeCommon(ArrayList<String>arrListRaw,ArrayList<String>arrListOutput) {
		for(int i=0;i<arrListRaw.size();i++)
 		{
 			String str1 = arrListRaw.get(i);
 			for(int j=0;j<arrListOutput.size();j++){
 				String str2 = arrListOutput.get(j);
 				if(str1.equals(str2)){
 					arrListRaw.remove(str1);
 					arrListOutput.remove(str2);
 					i--;
 					j--;
 				}
 			}
 		}
	}
	/****
	 * @comments 获得影响光合作用的原料
	 * @param arrListRaw
	 * @param arrListOutput
	 * @param strPSY
	 */
	private void recursiveGetRaw(ArrayList<String>arrListRaw,ArrayList<String>arrListOutput,String strPSY){
		GetMaterial gm = new GetMaterial();
		String strSubFilePath = "./src/data/raw";
		String strRawFileName = "Raw.xml";
		String strSubFileName = "Sub.xml";
	
		String strSubFile = strSubFilePath+strSubFileName;
		String strRawFile = strSubFilePath+strRawFileName;
		ArrayList<String> arrRaw = new ArrayList<String>();
		ArrayList<String> arrOutput = new ArrayList<String>();
		if(strPSY.equals(null))
			return;
		ArrayList <String>arrListSub = gm.getSubprocessFromBioprocess(strPSY,strSubFile);
		for(int i=0;i<arrListSub.size();i++){
			String strBioprocess =arrListSub.get(i);
			recursiveGetRaw(arrListRaw,arrListOutput,strBioprocess);
		}
		gm.printRaw(strPSY, arrRaw, strRawFile);
		gm.printOutput(strPSY, arrOutput, strRawFile);
		
		for(int i=0;i<arrRaw.size();i++) {
			String strEachRaw = arrRaw.get(i);
			arrListRaw.add(strEachRaw);
		}
		for(int i=0;i<arrOutput.size();i++) {
			String strEachOutput = arrOutput.get(i);
			arrListOutput.add(strEachOutput);
		}
		removeCommon(arrListRaw,arrListOutput);
	}
	
	/***
	 * @comments 获得影响光合作用的条件
	 * @param arrListFactor
	 * @param strPSY
	 */
	private void recursiveGetFactor(ArrayList<String>arrListFactor,String strPSY) {
		GetMaterial gm = new GetMaterial();
		String strSubFilePath = "./src/data/raw";
		String strFactorFileName = "Factor.xml";
		String strSubFileName = "Sub.xml";
	
		String strSubFile = strSubFilePath+strSubFileName;
		String strFactorFile = strSubFilePath+strFactorFileName;
		ArrayList<String> arrFactor = new ArrayList<String>();
		if(strPSY.equals(null))
			return;
		ArrayList <String>arrListSub = gm.getSubprocessFromBioprocess(strPSY,strSubFile);
		for(int i=0;i<arrListSub.size();i++){
			String strBioprocess =arrListSub.get(i);
			recursiveGetFactor(arrListFactor,strBioprocess);
		}
		gm.printFactor(strPSY, arrFactor, strFactorFile);
		for(int i=0;i<arrFactor.size();i++) {
			String strEachFactor = arrFactor.get(i);
			arrListFactor.add(strEachFactor);
		}
	}
	
	/*****
	 * @comment 判断是否是原料
	 * @param strMaterial
	 * @return
	 */
	protected boolean isRaw(String strMaterial) {
		String strPSY = "光合作用";
		ArrayList<String> arrListRaw = new ArrayList<String>();
		ArrayList<String> arrListOutput = new ArrayList<String>();
		recursiveGetRaw(arrListRaw, arrListOutput, strPSY);
		for(int i=0;i<arrListRaw.size();i++) {
			String strEachRaw = arrListRaw.get(i);
			if(strMaterial.equals(strEachRaw))
				return true;
		}
		return false;
	}
	
	/***
	 * @comments 判断是否是条件
	 * @param strMaterial
	 * @return
	 */
	protected boolean isFactor(String strMaterial) {
		String strPSY = "光合作用";
		ArrayList<String> arrListFactor = new ArrayList<String>();
		recursiveGetFactor(arrListFactor, strPSY);
		for(int i=0;i<arrListFactor.size();i++) {
			String strEachFactor = arrListFactor.get(i);
			if(strMaterial.equals(strEachFactor))
				return true;
		}
		return false;
	}
	public static void main(String[] args) {
		IsRawOrFactor io = new IsRawOrFactor();
		System.out.println(io.isRaw("二氧化碳"));
		System.out.println(io.isFactor("光照"));
	}
}
