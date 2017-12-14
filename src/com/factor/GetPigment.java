package com.factor;

import java.util.ArrayList;

import com.extend.GetMaterial;
import com.extend.OperateFile;

public class GetPigment {

	private static final String strFilePath ="./src/data/factor/";
	private static final String strFileName ="Pigment.txt";
	private static final String strPigmentFileName = "Pigment.xml";
	private static final String strFactorFileName = "Factor.xml";
	private static final String strSubFileName = "Subprocess.xml";
	
	public GetPigment() {};
	
	private void recursiveGetSubContent(String strMaterial,ArrayList<String> arrListPigment,ArrayList<String>arrList) {
		GetMaterial gm = new GetMaterial();
		OperateFile opfl = new OperateFile();
		String strPigmentFile=strFilePath+strPigmentFileName;            //Pigment.xml文件
		String strFile = strFilePath+strFileName;                        //存放结果的文件
		ArrayList<String> arrListSubset = new ArrayList<String>();       //存放子集
		
		if(strMaterial.equals(null))
			return;
		
		arrListPigment.add(strMaterial);
		arrListSubset=gm.getSubsetFromSet(strMaterial, strPigmentFile); //得到子集
		for(int i=0;i<arrListSubset.size();i++) {
			String strEachSubmaterial = arrListSubset.get(i);
			recursiveGetSubContent(strEachSubmaterial,arrListPigment,arrList);
		}
		
		for(int i=0;i<arrListSubset.size();i++) {
			String strEachSubset=arrListSubset.get(i);
			arrListPigment.add(strEachSubset);           //添加最小成分，得到类似[光合色素 叶绿素 叶绿素a]
			for(int j=0;j<arrList.size();j++) {
				String strEach=arrList.get(j);
				System.out.print(strEach+" ");
				opfl.writeToTXT(strEach+" ", strFile);
			}
			System.out.println();
			for(int j=0;j<arrListPigment.size();j++)
			{
				String strEachPigment =arrListPigment.get(j);
				System.out.print(strEachPigment+" ");
				opfl.writeToTXT(strEachPigment+" ", strFile);
			}
			System.out.println();
			opfl.writeToTXT("\n",strFile);
			int intLength = arrListPigment.size();
			arrListPigment.remove(intLength-1); 
			
		}
		int intLength = arrListPigment.size();
		arrListPigment.remove(intLength-1);  
		opfl=null;
	}
	
	protected void recursive(String strBioprocess,ArrayList<String>arrList) {
		
		GetMaterial gm = new GetMaterial();
		String strFactorFile =strFilePath+strFactorFileName;             //Factor.xml文件
		String strSubFile=strFilePath+strSubFileName;                    //Subprocess.xml文件
		ArrayList<String> arrListFactor = new ArrayList<String>();       //存放条件的色素
		ArrayList<String> arrListSubprocess = new ArrayList<String>();   //存放从父过程得到子过程的数组
		ArrayList<String> arrListPigment = new ArrayList<String>();
		
		if(strBioprocess.equals(null))
			return;
		
		arrList.add(strBioprocess);   //添加生物过程，找到对应关系
		arrListSubprocess =	gm.getSubprocessFromBioprocess(strBioprocess,strSubFile);
		for(int i=0;i<arrListSubprocess.size();i++){
			String strSubprocess = arrListSubprocess.get(i);
			recursive(strSubprocess,arrList);
		}
		gm.printFactor(strBioprocess, arrListFactor, strFactorFile); //找到条件<光合色素>
		for(int i=0;i<arrListFactor.size(); i++) {    //得到影响因素，影响因素在arrListFactor中
			String strEachFactor = arrListFactor.get(i);
			arrList.add(strEachFactor);               //将条件添加到数组里，得到类似[光合作用  光反应  ATP的合成  光能]
			int intLength = arrList.size();
			arrList.remove(intLength-1);              //去除最后一一个成分
			recursiveGetSubContent(strEachFactor,arrListPigment,arrList);
		}
		int intLength = arrList.size();
		arrList.remove(intLength-1);                  //去除子过程
		arrListFactor=null;
		arrListSubprocess=null;
		gm=null;
	}
	
	public static void main(String[] args) {
		GetPigment gp = new GetPigment();
		OperateFile opfl = new OperateFile();
		String strFile = strFilePath+strFileName;
		opfl.clearFile(strFile);
		String strBioprocess = "光合作用";
		ArrayList<String> arrListFactor = new ArrayList<String>();
		gp.recursive(strBioprocess,arrListFactor);
		opfl=null;
	}
}
