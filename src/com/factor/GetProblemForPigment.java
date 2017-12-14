package com.factor;

import java.util.ArrayList;

import com.extend.OperateFile;

public class GetProblemForPigment {
	
	public GetProblemForPigment() {}
	/***
	 * @Comments:将读取到的关键词进行解析写入到动态数组里    
	 * @Format [生物过程  条件  ...]    
	 * @param strFile
	 * @return ArrayList<String>
	 */
	private ArrayList<String> getKeywords(String strFile){
		ArrayList<String> arrKeywords=new ArrayList<String>();//用于存放生物过程和产物的动态数组
		try {
			if(strFile.equals(null))
				return null;
			OperateFile opfl=new OperateFile();
			arrKeywords=opfl.readToTXT(strFile);
			opfl=null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return arrKeywords;
	}
	
	/***
	 * @Comments 解析模板文件，并将解析到的模板放入数组
	 *           解析模板包括：1.删掉空白行。2.去掉注释行
	 * @param strFile
	 * @return ArrayList<String>
	 */
	private ArrayList<String> parseModel(String strFile){
		//保存处理后的模板
		ArrayList<String> getModelAfterParse = new ArrayList<String>();
		try {
			if(strFile.equals(null))
			{
				System.out.println("请创建模板文件");
				return null;
			}
			OperateFile opfl=new OperateFile();
			ArrayList<String> arrReadModelDirectly= opfl.readToTXT(strFile); //得到读取的模板文件
			//对得到的模板文件进行处理，到的需要的模板. 1.去掉空白行2."#"开头代表注释，去掉注释行
			for(int i=0;i<arrReadModelDirectly.size();i++) {             
				String strModelSentence = arrReadModelDirectly.get(i);
				strModelSentence = strModelSentence.trim();              //去掉空格
				if(0!=strModelSentence.length()) {                       //判断是否为空白行
					String charInitial=strModelSentence.substring(0, 1); //读取首字母，若为"#"开头，为注释
					if(!("#".equals(charInitial))) {
						getModelAfterParse.add(strModelSentence);
					}
				}
			}
			opfl=null;
			arrReadModelDirectly=null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return getModelAfterParse;
	}
	
	/***
	 * @Comments 随机读取字符串，将读取到的字符串返回
	 * @param arrStr
	 * @return String
	 */
	private String getReadomString(ArrayList<String> arrStr) {
		int i=(int)Math.random()*arrStr.size();
		return arrStr.get(i);
	}
	
	/***
	 * @Comments 从arrlistKeywords得到关键词，
	 *           从arrlistModel得到问题模板
	 *           提出问题和给出答案
	 *           将提出的问题和给出的答案写入到strFile文件里，
	 * @function 替换模板中的x1和x2
	 *           x1代表条件
	 *           x2代表光合作用
	 * @get [条件]如何影响光合作用
	 * @param arrlistKeywords
	 * @param arrlistModel
	 * @param strFile
	 */
	public void getProblemForReadom(String strKeywordFile,String strModelFile,String strFile) {
		OperateFile opfl = new OperateFile();
		ArrayList<String> arrlistModel=parseModel(strModelFile);      //得到解析后的模板
		ArrayList<String> arrlistKeywords=getKeywords(strKeywordFile);//得到关键词
		
		for(int i=0;i<arrlistKeywords.size();i=i+1) {                 //对每一个关键词进行提问
			String strGetProblem = getReadomString(arrlistModel);     //获得随机产生问题的模板
			String[] strEachKeywords=arrlistKeywords.get(i).split(" "); 
			int intKeywordsSize = strEachKeywords.length;
			String strBioprocess= strEachKeywords[0];                 //得到生物过程
			String strFactor = strEachKeywords[intKeywordsSize-1];    //得到条件
			strGetProblem = strGetProblem.replaceAll("x1", strFactor);
			strGetProblem = strGetProblem.replaceAll("x2", strBioprocess);
			
			System.out.println(strGetProblem);
			/**给出答案的语句*/
			String strGetAnswer = "";
			String strGetFactor = "";
			String strAnswer = "";
			for(int j=intKeywordsSize-1;j>3;j--) {
				strGetFactor=strGetFactor+strEachKeywords[j]+"属于"+strEachKeywords[j-1]+"，";
			}
			String strIsCondition = strEachKeywords[3]+"是"+strEachKeywords[2]+"的条件,";
			for(int j=2;j>0;j--) {
				strGetAnswer=strGetAnswer+"而"+strEachKeywords[j]+"是"+strEachKeywords[j-1]+"的子过程，";
			}
			strAnswer=strGetFactor+strIsCondition+strGetAnswer+"\n从而"+strFactor+"影响"+strBioprocess+"。";
			opfl.writeToTXT(strGetProblem+"\n", strFile);
			opfl.writeToTXT(strAnswer+"\n"+"\n", strFile);
		}
	}
	public static void main(String[] args) {
		OperateFile opfl = new OperateFile();
		GetProblemForPigment gpfp = new GetProblemForPigment();
		String strFilePath = "./src/data/factor/";
		String strKeywordFileName="Pigment.txt";
		String strModelProblemFileName="PigmentModel.txt";
		String strResultFileName = "PigmentProblem.txt";
		
		String strKeywordFile=strFilePath+strKeywordFileName;
		String strModeProblemFile=strFilePath+strModelProblemFileName;
		String strResultFile=strFilePath+strResultFileName;
		
		opfl.clearFile(strResultFile);
		gpfp.getProblemForReadom(strKeywordFile, strModeProblemFile, strResultFile);
		
		opfl = null;
		gpfp = null;
	}
}
