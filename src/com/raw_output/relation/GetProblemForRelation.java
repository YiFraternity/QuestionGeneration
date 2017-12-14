package com.raw_output.relation;

import java.util.ArrayList;
import java.util.Stack;

import com.extend.OperateFile;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 使用GetRawOutputForRelationNeed.java获得生物过程对应的原料和产物
 *           <例>问：光合作用中【H】的用途
 *          答：光合作用中光反应的水的光解产生【H】作为碳反应碳的还原。
 * @JDKversion JDK1.8
 * @CreatorDate 2017-10
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-28
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class GetProblemForRelation {
	public GetProblemForRelation() {}
	
	/***
	 * @Comments 将读取到的关键词进行解析写入到动态数组里
	 * @format  {{C3},
     *          {光合作用,暗反应,三碳化合物的还原},
     *          {光合作用,暗反应,五碳化合物的固定},
     *          {...}}
     * @get {{C3,三碳化合物的还原，五碳化合物的固定},
     *      {光合作用,暗反应,三碳化合物的还原},
     *      {光合作用,暗反应,五碳化合物的固定},...,}          
	 * @param strFile
	 * @return ArrayList<ArrayList>
	 */
	private ArrayList<ArrayList<String>> getKeywords(String strFile){
		//用于存放生物过程、原料和产物的动态数组
		ArrayList<String> arrKeywords=new ArrayList<String>();
		
		//用于存放关键词的动态数组,存放结果为上面的<Format>
		ArrayList<ArrayList<String>> arrayListKeywordsForReturn=new ArrayList<ArrayList<String>>();
		
		try {
			if(strFile.equals(null))
				return null;
			OperateFile opfl=new OperateFile();
			arrKeywords=opfl.readToTXT(strFile);
			opfl=null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		//将得到的动态数组进行解析，每一行按照空格进行成数组，将数组存放在动态数组里
		//get上面<format>格式
		for(int i=0;i<arrKeywords.size();i++) {
			//从文件读取得到的值
			String strEachArr=arrKeywords.get(i);  
			String[] arrStringEachArr=strEachArr.split(" ");
			ArrayList<String> arrEachArr=new ArrayList<String>();
			for(int j=0;j<arrStringEachArr.length;j++) {
				arrEachArr.add(arrStringEachArr[j]);
			}
			arrayListKeywordsForReturn.add(arrEachArr);
		}
		
		/**从arrayListKeywordsForReturn数组中获取最终要的格式<Return>*/
		for(int i=0;i<arrayListKeywordsForReturn.size();i=i+3) {
			
			/**存放原料是arrayListKeywordsForReturn.get(i)的生物过程数组*/
			ArrayList<String> arrStrRawKey = arrayListKeywordsForReturn.get(i+1);
			/**存放产物是arrayListKeywordsForReturn.get(i)的生物过程数组*/
			ArrayList<String> arrStrOutputKey = arrayListKeywordsForReturn.get(i+2);
			int a=0,b=0;
			while(arrStrRawKey.get(a).equals(arrStrOutputKey.get(b))) {
				a++;
				b++;
			}
			arrayListKeywordsForReturn.get(i).add(arrStrRawKey.get(a));
			arrayListKeywordsForReturn.get(i).add(arrStrOutputKey.get(b));
		}
		
		return arrayListKeywordsForReturn;
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
			//对得到的模板文件进行处理，到的需要的模板
			//1.去掉空白行
			//2."#"开头代表注释，去掉注释行
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
	 * Comments:随机抽取字符串  
	 * @param arrlist
	 * @return String
	 */
	private String getReadomString(ArrayList<String> arrlist) {
		int i= (int)Math.random()*arrlist.size();
		return arrlist.get(i);
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
	public void getProblemForReadom(String strKeywordFile,String strModelFile,String strFile) {
		OperateFile opfl = new OperateFile();
		
		ArrayList<String> arrlistModel=parseModel(strModelFile);
		ArrayList<ArrayList<String>> arrlistKeywords=getKeywords(strKeywordFile);
		/**对每一个关键词进行提问*/
		for(int i=0;i<arrlistKeywords.size();i=i+3) {
			/**获得随机产生问题的模板*/
			String strGetProblem = getReadomString(arrlistModel);
			//对问题进行提问
			//替换x1和x2
			//其中x1用生物物质是生物过程的原料的生物过程
			//x2是生物物质是生物过程产物的生物过程
			//得到问题
			String strRawSubstance=arrlistKeywords.get(i).get(1);
			String strRawSub="";
			String strOutputSub="";
			String strOutputSubstance = arrlistKeywords.get(i).get(2);
			strGetProblem = strGetProblem.replaceAll("x1", strRawSubstance);
			strGetProblem = strGetProblem.replaceAll("x2", strOutputSubstance);
			
			System.out.println(strGetProblem);
			/**给出答案的语句*/
			String strSubstance=arrlistKeywords.get(i).get(0);
			String strGetAnswer = null;
			
			/**判断生物物质的生物过程是否为其直接过程*/
			int j=arrlistKeywords.get(i+1).size()-1;//用于判断是否是父过程
			Stack<String> sRaw = new Stack<String>();
			Stack<String> sOutput = new Stack<String>();
			/***将原料的父过程和子过程输出*/
			while(!strRawSubstance.equals(arrlistKeywords.get(i+1).get(j))) {
				String strItem=arrlistKeywords.get(i+1).get(j);
				sRaw.push(strItem);
				j--;
			}
			sRaw.push(strRawSubstance);
			while(!sRaw.isEmpty()) {
				String strItem=sRaw.pop();
				strRawSub=strRawSub+"里的"+strItem;
			}
			strRawSub=strRawSub.substring(2, strRawSub.length());
			j=arrlistKeywords.get(i+2).size()-1;
			/***将产物的父过程和子过程输出*/
			while(!strOutputSubstance.equals(arrlistKeywords.get(i+2).get(j))) {
				String strItem=arrlistKeywords.get(i+2).get(j);
				sOutput.push(strItem);
				j--;
			}
			sOutput.push(strOutputSubstance);
			while(!sOutput.isEmpty()) {
				String strItem=sOutput.pop();
				strOutputSub=strOutputSub+"里的"+strItem;
			}
			strOutputSub=strOutputSub.substring(2, strOutputSub.length());
			strGetAnswer = "从"+strOutputSub+"产生的"+strSubstance+"作为"+strRawSub+"的原料";
			
			opfl.writeToTXT(strGetProblem+"\n", strFile);
			opfl.writeToTXT(strGetAnswer+"\n", strFile);
		}
		
	}
	
	public static void main(String[] args) {
		
		OperateFile opfl = new OperateFile();
		GetProblemForRelation gpfr = new GetProblemForRelation();
		
		String strFilePath = "./src/data/raw_output/";
		String strKeywordFileName="raw_output.txt";
		String strModelProblemFileName="Raw_Relation_Output_Model.txt";
		String strResultFileName = "RawRelationOutput.txt";
		
		String strKeywordFile=strFilePath+strKeywordFileName;
		String strModeProblemFile=strFilePath+strModelProblemFileName;
		String strResultFile=strFilePath+strResultFileName;
		
		opfl.clearFile(strResultFile);
		gpfr.getProblemForReadom(strKeywordFile, strModeProblemFile, strResultFile);
		
		opfl = null;
		gpfr = null;
	}
}
