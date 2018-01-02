package com.experiment.sunnySummer;

import java.util.ArrayList;

import com.extend.GetMaterial;
import com.extend.OperateFile;

public class GetAnswer {

	private static final String strFilePath = "./src/data/experiment/";
	private static final String strOWLFilePath = "./src/data/";
	private static final String strExperimentFileName = "exp2.owl";
	
	private static final int iStage=3;
	
	public GetAnswer() {}
	
	/****
	 * @comments 获得实验过程中每个阶段，如何影响光合作用的线路
	 * @example 实验阶段二中
	 *          光照增强->温度升高->气孔关闭->二氧化碳减少->光合作用强度减弱
	 * @flow First,查找影响光合作用强度的关键因素
	 *       Second,查找外界环境是如何影响关键因素
	 *       Third,关键因素是是原料还是条件
	 *       Fourth,查找关键因素是如何影响光合作用
	 */
	protected void getfactor() {
		GetMaterial gm = new GetMaterial();
		OperateFile opfl = new OperateFile();
		String strFactorName = "Factor.xml";
		String strDirectFileName = "Dircetion.xml";
		String strResultFileName = "Problem.txt";
		String strResultFile = strFilePath+strResultFileName;
		String strDirectFile = strFilePath+strDirectFileName;                  //用来存放SPARQL解析得到的
		String strExperFile = strOWLFilePath+strExperimentFileName;            //使用的哪个OWL文件
		String strFile = strFilePath+strFactorName;
		ArrayList<String> arrListFactor = new ArrayList<String>();             //存放影响光合作用的关键因素
		arrListFactor = gm.getFactorPSYIntensity(strExperFile, strFile,iStage);//三个实验阶段，三个关键因素。
		for(int i=0;i<arrListFactor.size();i++) {
			String strPSYFactor = arrListFactor.get(i);
			
			int factorLength = strPSYFactor.length();
			String strKeyFactor = strPSYFactor.substring(0, factorLength-1);   //去掉阶段的过程(光照1->光照)
			String strStage = strPSYFactor.substring(factorLength-1);          //获得阶段
			
			//获得此阶段光合作用强度的变化
			ArrayList<String> arrVarietyOfPSYI = new ArrayList<String>();      //光合作用强度的变化
			arrVarietyOfPSYI = gm.getChangeDirection(strExperFile, strDirectFile, "光合作用强度"+strStage);
			String strVarietyOfPSYI = arrVarietyOfPSYI.get(0);
			
			//得到问题
			String strProblem="请简述阶段"+strStage+"光合作用强度"+strVarietyOfPSYI+"的原因";
			System.out.println(strProblem);
			opfl.writeToTXT(strProblem+"\n", strResultFile);
			
			//获得影响光合作用的线路
			ArrayList<String> arrThisFactor = new ArrayList<String>();         //获得外界环境影响光合作用的线路
			recursiveGetOriginFactor(arrThisFactor,strPSYFactor);
			String strCircuit="";
			for(int t=0;t<arrThisFactor.size();t++)
			{
				String strEachFactor = arrThisFactor.get(t);                   //外界环境影响线路中过程
				
				int eachFactorLength = strEachFactor.length();
				String strFacRmStage = strEachFactor.substring(0, eachFactorLength-1);   //去掉阶段的过程(光照1->光照)
				
				ArrayList<String> arrListDirect = new ArrayList<String>();     //外界环境中变化阶段
				arrListDirect=gm.getChangeDirection(strExperFile, strDirectFile, strEachFactor);
				String strDirect = arrListDirect.get(0);
				strCircuit=strCircuit+strFacRmStage+strDirect+"，使得";
			}
			strCircuit=strCircuit.substring(0, strCircuit.length()-3);
			System.out.println(strCircuit);
			opfl.writeToTXT(strCircuit+"\n", strResultFile);
			arrThisFactor=null;
			
			//查看关键因素如何影响光合作用
			IsRawOrFactor isrf = new IsRawOrFactor();
			PrintCircuit pc = new PrintCircuit();
			if(isrf.isFactor(strKeyFactor))
				pc.printCircuitOfFactor(strKeyFactor,strResultFile);
			else if(isrf.isRaw(strKeyFactor))
				pc.printCircuitOfRaw(strKeyFactor,strResultFile);
			else
				System.out.println("error");
			
			String strResult = "第"+strStage+"阶段，影响光合作用强度的主要因素是"+strKeyFactor;
			strResult =strResult+"，所以，光合作用强度"+strVarietyOfPSYI;
			System.out.println(strResult);
			opfl.writeToTXT(strResult+"\n\n",strResultFile);
			isrf = null;
			pc=null;
		}
		gm=null;
		opfl=null;
	}
	
	/****
	 * @comments 获得如何影响光合作用的决定因素
	 * @example 实验过程阶段二如何二氧化碳
	 *          光照->温度->气孔->二氧化碳
	 * @param arrListFactor
	 * @param strPSYFactor
	 */
	private void recursiveGetOriginFactor(ArrayList<String>arrListFactor,String strPSYFactor) {
		String strFactorName = "Factor.xml";
		String strExperFile = strOWLFilePath+strExperimentFileName;
		String strFile = strFilePath+strFactorName;
		if(strPSYFactor.equals(null))
			return;
		GetMaterial gm = new GetMaterial();
		ArrayList<String> arrListThisFactor = new ArrayList<String>();
		arrListThisFactor = gm.getOriginFactor(strExperFile, strFile, strPSYFactor);
		if(!arrListThisFactor.isEmpty())
		{
			String strThisFactor = arrListThisFactor.get(0);
			recursiveGetOriginFactor(arrListFactor,strThisFactor);
		}
		arrListFactor.add(strPSYFactor);
	}
	
	public static void main(String[] args) {
		GetAnswer gk = new GetAnswer();
		OperateFile opfl = new OperateFile();
		String strResultFileName = "Problem.txt";
		String strResultFile = strFilePath+strResultFileName;
		opfl.clearFile(strResultFile);
		gk.getfactor();
	}
}
