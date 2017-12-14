package com.factor;

import java.util.ArrayList;

import com.extend.OperateFile;
import com.extend.GetMaterial;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 获取外界环境对光合作用的影响
 * @Example 问：光能如何影响光合作用
 *          答：光能是水的光解的条件，水的光解是光反应的子过程，光反应是光合作用的子过程
 *             光能通过影响水的光解，从而影响光反应，影响光合作用。
 * @JDKversion JDK1.8
 * @CreatorDate 2017-9
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-20
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class TheEffectsOfExternalEnvironment {
	
	private static final String strFilePath ="./src/data/factor/";
	private static final String strFileName ="Factors.txt";
	private static final String strFactorFileName = "Factor.xml";
	private static final String strSubFileName = "Subprocess.xml";
	
	public TheEffectsOfExternalEnvironment(){}

 	/****
 	 * @Comments 得到生物过程及条件
 	 * @Example [[光合作用  光反应  ATP的合成  光能]
 	 *           [光合作用  光反应  水的光解  光能]
 	 *           [...]]
 	 * @Flow First:从光合作用向下进行递归
 	 *       Second:判断其子过程是否为空
 	 *       Third:若不为空，对每一个子过程加入数组，进行递归
 	 *       Forth:若为空，查找其所需要的条件，将条件添加到数组
 	 * @param strBioprocess
 	 * @param arrListForNeed
 	 */
	protected void recursive(String strBioprocess,ArrayList<String>arrListForNeed)
	{
		OperateFile opfl = new OperateFile();
		GetMaterial gm = new GetMaterial();
		
		String strFactorFile=strFilePath+strFactorFileName;              //factor.xml文件
		String strSubFile=strFilePath+strSubFileName;                    //Subprocess.xml文件
		String strFile = strFilePath+strFileName;                        //存放结果的文件
		ArrayList<String> arrListFactor = new ArrayList<String>();       //存放影响因素的数组
		ArrayList<String> arrListSubprocess = new ArrayList<String>();   //存放从父过程得到子过程的数组
		if(strBioprocess==null)
			return;
		
		arrListForNeed.add(strBioprocess);           //将生物过程添加到数组
		arrListSubprocess =	gm.getSubprocessFromBioprocess(strBioprocess,strSubFile);
		for(int i=0;i<arrListSubprocess.size();i++){
			String strSubprocess = arrListSubprocess.get(i);
			recursive(strSubprocess,arrListForNeed);
		}
		
		gm.printFactor(strBioprocess, arrListFactor, strFactorFile);     
		for(int i=0;i<arrListFactor.size(); i++) {          //得到影响因素，影响因素在arrListFactor中
			String strEachFactor = arrListFactor.get(i);
			arrListForNeed.add(strEachFactor);              //将条件添加到数组里，得到类似[光合作用  光反应  ATP的合成  光能]
			for(int t=0;t<arrListForNeed.size();t++) {
				String strEach=arrListForNeed.get(t);
				System.out.println(strEach);
				opfl.writeToTXT(strEach+" ", strFile);      //写数组进入文件
			}
			opfl.writeToTXT("\n", strFile);
			int intLength = arrListForNeed.size();
			arrListForNeed.remove(intLength-1);             //去除最后一一个条件;
		}
		int intLength = arrListForNeed.size();
		arrListForNeed.remove(intLength-1);    //去除子过程
		arrListFactor=null;
		arrListSubprocess=null;
		gm=null;
		opfl=null;
	}
	
	public static void main(String[] args) {
		TheEffectsOfExternalEnvironment fa = new TheEffectsOfExternalEnvironment();
		OperateFile opfl = new OperateFile();
		String strFile = strFilePath+strFileName;
		opfl.clearFile(strFile);
		String strBioprocess = "光合作用";
		ArrayList<String> arrListFactor = new ArrayList<String>();
		fa.recursive(strBioprocess,arrListFactor);
		opfl=null;
	}
}
