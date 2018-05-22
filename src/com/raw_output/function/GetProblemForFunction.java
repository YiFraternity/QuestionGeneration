package com.raw_output.function;

import java.util.ArrayList;

import com.extend.OperateFile;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 使用GetRawOutputFunctionForNeed.java得到的物质进行出题
 * @fromFile  Pi ATP的合成
 *          光合作用 光反应 ATP的合成 
 *			ADP ATP的合成
 *			光合作用 光反应 ATP的合成 
 *			水 水的光解
 * 			光合作用 光反应 水的光解 
 *          ...
 * @question Pi在ATP的合成的作用
 * @answer Pi在ATP中作为原料
 * @JDKversion JDK1.8
 * @CreatorDate 2017-10
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-30
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class GetProblemForFunction {
	
	private static final String strFilePath = "./src/data/raw_output/function/";
	private static final String strRawFileName= "rawKeyword.txt";
	private static final String strOutputFileName = "outputKeyword.txt";
	private static final String strFileName = "FunctionProblem.txt";
	private static final String strModelFileName="RawProcessModel.txt";
	public GetProblemForFunction(){};
	/****
	 * @comments 从文件中提取需要的关键词
	 * @format [[Pi,ATP的合成]
	 *          [ADP,ATP的合成]
	 *          ...]
	 * @param strFile
	 * @return
	 */
	private ArrayList<String[]> getKeywords(String strFile){
		ArrayList<String> arrContent=new ArrayList<String>();           //用于存放生物过程、原料和产物的动态数组
		ArrayList<String[]> arrListKeyword = new ArrayList<String[]>(); //用于存放结果，也即上面<format>格式
		if(strFile.equals(null))
			return null;
		OperateFile opfl=new OperateFile();
		arrContent=opfl.readToTXT(strFile);
		
		/***将得到的数组进行解析，将数组存放在动态数组里get上面<format>格式*/
		for(int i=0;i<arrContent.size();i=i+1) {
			String strEachArr=arrContent.get(i);  //从文件读取得到的值
			String[] arrStringEachArr=strEachArr.split(" ");
			arrListKeyword.add(arrStringEachArr);
		}
		arrContent =null;
		opfl=null;
		return arrListKeyword;
	}
	
	/***
	 * @Comments 解析模板文件，并将解析到的模板放入数组
	 *           解析模板包括：1.删掉空白行。2.去掉注释行
	 * @param strFile
	 * @return ArrayList<String>
	 */
	private ArrayList<String> parseModel(String strFile){
		if(strFile.equals(null))
			return null;
		OperateFile opfl=new OperateFile();
		ArrayList<String> getModelAfterParse = new ArrayList<String>();		//保存处理后的模板
		ArrayList<String> arrReadModelDirectly= opfl.readToTXT(strFile);    //得到读取的模板文件
		
		for(int i=0;i<arrReadModelDirectly.size();i++) {                 //对得到的模板文件进行处理，到的需要的模板
			String strModelSentence = arrReadModelDirectly.get(i);
			strModelSentence = strModelSentence.trim();                  //去掉空格
			if(0!=strModelSentence.length()) {                           //判断是否为空白行,过滤掉空行
				String charInitial=strModelSentence.substring(0, 1);     //读取首字母，若为"#"开头，为注释
				if(!("#".equals(charInitial))) {
					getModelAfterParse.add(strModelSentence);
				}
			}
		}
		opfl=null;
		arrReadModelDirectly=null;
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
	 * @Comments 从arrlistKeywords得到关键词，
	 *          从arrlistModel得到问题模板
	 *          提出问题和给出答案
	 *          将提出的问题和给出的答案写入到strFile文件里，
	 * @param arrlistKeywords
	 * @param arrlistModel
	 * @param strFile
	 */
	public void getProblemForReadom(String strKeywordFile,String strModelFile,String strFile,int flag) {
		OperateFile opfl = new OperateFile();
		
		ArrayList<String> arrlistModel=parseModel(strModelFile);   
		ArrayList<String[]> arrlistKeywords=getKeywords(strKeywordFile);
		/**对每一个关键词进行提问*/
		for(int i=0;i<arrlistKeywords.size();i=i+2) {
			String strGetProblem = getStringForReadom(arrlistModel);        //获得随机产生问题的模板
			/***对问题进行提问,替换x1和x2,得到问题**/
			String strMaterial=arrlistKeywords.get(i)[0];                   //生物物质
			String strBioprocess = arrlistKeywords.get(i)[1];               //生物过程
			strGetProblem = strGetProblem.replaceAll("x1", strMaterial);	//x1是生物物质（原料或者产物)
			strGetProblem = strGetProblem.replaceAll("x2", strBioprocess);  //x2是生物过程
			System.out.println(strGetProblem);
			
			/**给出答案的语句*/
			String[]strSubstance=arrlistKeywords.get(i+1);  //得到所有生物过程
			String strGetAnswer = strMaterial+"作为";

			int j=0;      //用于判断是哪个生物过程
			while(!strBioprocess.equals(strSubstance[j])) {
				j++;
			}
			while(j<strSubstance.length)
			{
				strGetAnswer = strGetAnswer+strSubstance[j]+"下的";
				j++;
			}
			strGetAnswer = strGetAnswer.substring(0, strGetAnswer.length()-2);
			if(1 == flag)
			{
				strGetAnswer = strGetAnswer+"的原料";
			}else if(2 == flag) {
				strGetAnswer = strGetAnswer +"的产物";
			}
			else {
				strGetAnswer = "Error,生成有关原料题目为1，生成有关产物题目为2";
			}
			opfl.writeToTXT(strGetProblem+"\n", strFile);
			opfl.writeToTXT(strGetAnswer+"\n", strFile);
		}
		opfl = null;
		arrlistModel=null;
		arrlistKeywords=null;
	}
	public static void main(String[] args) {
		GetProblemForFunction gf = new GetProblemForFunction();
		OperateFile opfl = new OperateFile();
		String strFile = strFilePath+strFileName;
		String strRawFile = strFilePath+strRawFileName;
		String strOuputFile = strFilePath +strOutputFileName;
		String strModelFile = strFilePath + strModelFileName;
		opfl.clearFile(strFile);
		gf.getProblemForReadom(strRawFile, strModelFile, strFile,1);
		gf.getProblemForReadom(strOuputFile, strModelFile, strFile, 2);
	}
}
