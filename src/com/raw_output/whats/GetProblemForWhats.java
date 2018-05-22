package com.raw_output.whats;

import java.util.ArrayList;

import com.extend.*;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 使用GetRawAndOutput.java获得生物过程对应的原料和产物
 *           此Java文件对得到的原料和产物进行提问
 * @JDKversion JDK1.8
 * @CreatorDate 2017-9
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-20
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class GetProblemForWhats {

	protected GetProblemForWhats() {}
	
	/***
	 * @Comments:将读取到的关键词进行解析写入到动态数组里    
	 * @Format [生物过程  原料  产物  ...]    
	 * @param strFile
	 * @return ArrayList<String>
	 */
	private ArrayList<String> getKeywords(String strFile){
		//用于存放生物过程、原料和产物的动态数组
		ArrayList<String> arrKeywords=new ArrayList<String>();
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
				return null;
			OperateFile opfl=new OperateFile();
			//得到读取的模板文件
			ArrayList<String> arrReadModelDirectly= opfl.readToTXT(strFile);
			/***
			 * 对得到的模板文件进行处理，到的需要的模板
			 * 1.去掉空白行
			 * 2."#"开头代表注释，去掉注释行
			 */
			for(int i=0;i<arrReadModelDirectly.size();i++) {
				String strModelSentence = arrReadModelDirectly.get(i);
				//去掉空格
				strModelSentence = strModelSentence.trim();
				//判断是否为空白行
				if(0!=strModelSentence.length()) {
					//读取首字母，若为"#"开头，为注释
					String charInitial=strModelSentence.substring(0, 1);
					if(!("#".equals(charInitial))) {
						getModelAfterParse.add(strModelSentence);
					}
				}
			}
			//释放内存
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
	private String getStringForReadom(ArrayList<String> arrStr) {
		int i=(int)Math.random()*arrStr.size();
		return arrStr.get(i);
	}
	
	/***
	 * @Comments 随机生成题目
	 *           将读取到的文件进行出题，将得到的题目写入到文件strFile里面
	 * @param strFile
	 * @param strKeywordFile
	 * @param strModelFile
	 */
	protected void getPrblemForRandom(String strFile,String strKeywordFile,String strModelFile) {
		OperateFile opfl = new OperateFile();
		ArrayList<String> arrGetKeywords= getKeywords(strKeywordFile);
		for(int i=0;i<arrGetKeywords.size();i=i+3) {
			ArrayList<String> arrGetModel = parseModel(strModelFile);
			String strGetModel = getStringForReadom(arrGetModel);
			String strGetProblem = strGetModel.replaceAll("x1", arrGetKeywords.get(i));
			String strGetAnswer = strGetProblem.replaceAll("问题","答案");
			strGetAnswer = strGetAnswer.replaceAll("什么", arrGetKeywords.get(i+1));
			strGetAnswer=strGetAnswer.replaceAll("哪些", arrGetKeywords.get(i+2));
			opfl.writeToTXT(strGetProblem+"\n", strFile);
			opfl.writeToTXT(strGetAnswer+"\n", strFile);
			arrGetModel=null;
		}
		arrGetKeywords = null;
		opfl=null;
	}
	
	/***
	 * @Comments 按照模板顺序依次提出问题
	 * @param strFile
	 * @param strKeywordFile
	 * @param strModelFile
	 */
	protected void GetProblemForOrder(String strFile,String strKeywordFile,String strModelFile) {
		OperateFile opfl = new OperateFile();
		ArrayList<String> arrGetKeywords= getKeywords(strKeywordFile);
		//选择第几条语句
		int j=1;
		for(int i=0;i<arrGetKeywords.size();i=i+3) {
			ArrayList<String> arrGetModel = parseModel(strModelFile);
			int t=j%(arrGetModel.size());;
			String strGetModel = arrGetModel.get(t);
			String strGetProblem = strGetModel.replaceAll("x1", arrGetKeywords.get(i));
			String strGetAnswer = strGetProblem.replaceAll("什么", arrGetKeywords.get(i+1));
			strGetAnswer=strGetAnswer.replaceAll("哪些", arrGetKeywords.get(i+2));
			opfl.writeToTXT(strGetProblem+"\n", strFile);
			opfl.writeToTXT(strGetAnswer+"\n", strFile);
			arrGetModel=null;
		}
		arrGetKeywords = null;
		opfl=null;
	}
	public static void main(String[] args) {
		String strFilePath = "./src/data/raw_output/whats/";
		String strModelFileName = "raw_output_model.txt";
		String strKeywordsFileName = "raw_output.txt";
		String strProblemFileName ="problem.txt";
		String strModelFile = strFilePath + strModelFileName;
		String strKeywordFile = strFilePath + strKeywordsFileName;
		String strProblemFile = strFilePath + strProblemFileName;
		GetProblemForWhats gp = new GetProblemForWhats();
		OperateFile opfl = new OperateFile();
		opfl.clearFile(strProblemFile);
		opfl=null;
		gp.getPrblemForRandom(strProblemFile,strKeywordFile, strModelFile);
		gp.GetProblemForOrder(strProblemFile, strKeywordFile, strModelFile);
		gp=null;
	}
}
