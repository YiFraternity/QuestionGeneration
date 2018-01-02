package com.extend;

import org.apache.jena.util.FileManager;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.query.*;

import java.io.*;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 用SPARQL语句解析OWL文件，
 *          进行查询各类生物物质及生物过程
 * @JDKversion JDK1.8
 * @CreatorDate 2017-9
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-20
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class SPARQLParse {
	private OntModel model;
	private QueryExecution qe;
	
	static final String strFilePath = "./src/data/";  //文件根目录
	static final String strFileName = "光合作用.owl";  //owl文件
	
	public SPARQLParse(){}
	
	/***
	 * Comments:含参构造函数
	 * @param strFile
	 */
	public SPARQLParse(String strFile){
		InputStream in = FileManager.get().open(strFile);
		if(in == null)
		{
			throw new IllegalArgumentException("file not found");
		}
		model=ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.read(in,"");        //read the RDF/XML File
//		model.write(System.out);  //Write it to standard out
	}
	
	/**
	 * @Comments 查找原料和产物
	 *           已知生物过程，查找此生物过程的原料和产物
	 * @param strBioprocess
	 * @param raw
	 * @param output
	 * @return ResultSet
	 */
	public ResultSet findRawOutput(String strBioprocess,String raw, String output){
		if(model == null)
			return null;
		String strquery=
				"SELECT (?SPARQLParse As ?光合作用过程) (group_concat(distinct ?raw;separator = ',') As ?原料) (group_concat(distinct ?output;separator = ',') As ?生成物)"
				+"WHERE{"
				+ "OPTIONAL{"+strBioprocess+raw+"?raw }."
				+ "OPTIONAL{"+strBioprocess+output+"?output}"
				+ "}"
				+ "GROUP BY ?SPARQLParse";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;	
	}
	
	/***
	 * @Comments 根据原料查找生物过程
	 *           已知原料,查询使用此原料的生物过程
	 * @param raw
	 * @param strPredicate
	 */
	public ResultSet findBioprocessFromRaw(String raw,String strPredicate) {
		if(model == null)
			return null;
		String strquery=
				"SELECT  (?subject As ?生物过程 ) (?raw As ?原料) "
				+"WHERE{"
				+ "OPTIONAL{"+"?subject" + strPredicate + raw +"}."
				+ "}";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;	
	}

	/***
	 * @Comments 根据条件来查找生物过程
	 * @param strFactor
	 * @param strPredicate
	 * @return
	 */
	public ResultSet findBioprocessFromFactor(String strFactor,String strPredicate) {
		if(model == null)
			return null;
		String strquery=
				"SELECT  (?subject As ?生物过程 ) (?Factor As ?条件) "
				+"WHERE{"
				+ "OPTIONAL{"+strFactor + strPredicate + "?subject" +"}."
				+ "}";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;	
	}
	
	/***
	 * @Comments 根据产物来查找生物过程
	 * @param output
	 * @param strPredicate
	 * @return
	 */
	public ResultSet findBioprocessFromOutput(String output,String strPredicate) {
		if(model == null)
			return null;
		String strquery=
				"SELECT  (?subject As ?生物过程 ) (?output As ?产物) "
				+"WHERE{"
				+ "OPTIONAL{"+"?subject" + strPredicate + output +"}."
				+ "}";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;	
	}
	
	/***
	 * @Comment:查找父过程 
	 *          已知一生物过程，查找该生物过程对应的父过程
	 *          若存在，返回ResultSet类型，若无返回null
	 * @param BiologyProcess
	 * @param strPredicate
	 */
	public ResultSet findParentbioFromBioprocess(String BiologyProcess,String strPredicate) {
		if(model == null)
			return null;
		String strquery=
				"SELECT (?subProcess As ?生物过程) (?parentProcess As ?父过程) "
				+"WHERE{"
				+ "OPTIONAL{"+"?subProcess"+ strPredicate + BiologyProcess +"}."
				+ "}";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;
	}
	
	/***
	 * @Comments 查找原料
	 *           已知生物过程，查找该生物过程所使用的原料
	 *           若存在，返回ResultSet类型；若不存在，返回null
	 * @param strBioprocess
	 * @param raw
	 * @return
	 */
	public ResultSet findRawFromBioprocess(String strBioprocess,String strPredicate){
		if(model == null)
			return null;
		String strquery=
				"SELECT (?SPARQLParse As ?光合作用过程) (?raw As ?原料) "
				+"WHERE{"
				+ "OPTIONAL{"+strBioprocess+strPredicate+"?raw }."
				+ "}";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;	
	}
	
	/***
	 * @Comments 查找产物
	 *           已知生物过程，查找该生物过程生成的产物
	 *           若存在，返回ResultSet类型，若不存在，返回null
	 * @param strBioprocess
	 * @param output
	 * @return
	 */
	public ResultSet findOutputFromBioprocess(String strBioprocess,String output){
		if(model == null)
			return null;
		String strquery=
				"SELECT (?SPARQLParse As ?光合作用过程) (?output As ?生成物)"
				+"WHERE{"
				+ "OPTIONAL{"+strBioprocess+output+"?output}"
				+ "}";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;	
	}
	
	/****
	 * @Comments 根据生物过程查找所需要的条件
	 * @param strBioprocess
	 * @param strPredicate
	 * @return
	 */
	public ResultSet findFactorFromBioprocess(String strBioprocess,String strPredicate) {
		if(model == null)
			return null;
		String strquery=
				"SELECT (?factor As ?条件) (?subject As ?生物过程 ) "
				+"WHERE{"
				+ "OPTIONAL{"+ "?factor" + strPredicate + strBioprocess +"}."
				+ "}";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;	
	}
	
	/***
	 * @Comments 查找子过程
	 *           已知生物过程，查找该生物过程的子过程
	 * @param strBioprocess
	 * @param sub
	 * @return
	 */
	public ResultSet findBioprocessFromParentbio(String strBioprocess,String strPredicate){
		if(model == null)
			return null;
		String strquery=
				"SELECT (?subprocess As ?子过程)  "
				+ "WHERE{"
				+ "OPTIONAL{"+strBioprocess+strPredicate+"?subprocess }"
				+ "}"
				+ "GROUP BY ?subprocess";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;	
	}
	
	/*****
	 * @Contents 根据生物资料查找其包含的东西
	 * @Example 光合色素包括叶绿素和类胡萝卜素
	 * @param strMaterial
	 * @param strPredicate
	 * @return
	 */
	public ResultSet getSubcontentFromContent(String strMaterial,String strPredicate) {
		if(model == null)
			return null;
		String strquery=
				"SELECT (?Subcontent As ?成分)  "
				+ "WHERE{"
				+ "OPTIONAL{"+ strMaterial + strPredicate + "?Subcontent }"
				+ "}"
				+ "GROUP BY ?Subcontent";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;	
	}
	
	/***
	 * @comments 查找影响光合作用强度的条件因素
	 * @param strPredicate
	 * @param iStage
	 * @return
	 */
	public ResultSet getFactorFromPSYIntensity(String strPredicate,int iStage) {
		String strMaterial = "<http://www.co-ode.org/ontologies/ont.owl#光合作用强度"+iStage+">";
		if(model == null)
			return null;
		String strquery=
				"SELECT (?factor As ?影响因素)  "
				+ "WHERE{"
				+ "OPTIONAL{"+ "?factor " + strPredicate + strMaterial +" }"
				+ "}";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;
	}
	
	/****
	 * @comments 查找引起结果变化的原因
	 * @param strResultFactor
	 * @param strPredicate
	 * @return
	 */
	public ResultSet getInfluencingFactor(String strResultFactor,String strPredicate) {
		if(model == null)
			return null;
		String strquery=
				"SELECT (?OriginFactor As ?原因因素)  "
				+ "WHERE{"
				+ "OPTIONAL{"+ "?OriginFactor " + strPredicate + strResultFactor +" }"
				+ "}";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;
	}
	
	/***
	 * @comments 查找影响因素的变化方向
	 * @param strFactor
	 * @param strPredicate
	 * @return
	 */
	public ResultSet howToChange(String strFactor,String strPredicate) {
		if(model == null)
			return null;
		String strquery=
				"SELECT (?Direction As ?变化方向)  "
				+ "WHERE{"
				+ "OPTIONAL{"+ strFactor + strPredicate + "?Direction " +" }"
				+ "}";
		Query query=QueryFactory.create(strquery);
        qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();//select 类型
		return results;
	}
	/***
	 * @Comments 关闭SPARQL查询
	 * @param 
	 */
	private void closeQe() {
		if(!qe.isClosed()){
			qe.close();
		}
	}
	/***
	 * @Comment 将字符串写入文件
	 *          不保留原料文件内容
	 * @param a
	 * @param fileName
	 */
	private void writeToFile(String a,String fileName){
		try{
			File file = new File(strFilePath+fileName);
			if(!file.exists()){
				file.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(a);
			out.flush();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		SPARQLParse sp = new SPARQLParse(strFilePath+strFileName);
		String sub1 = "<http://www.co-ode.org/ontologies/ont.owl#光合作用>";
		String sub2 = "<http://www.co-ode.org/ontologies/ont.owl#子过程>";
		String sub3 = "<http://www.co-ode.org/ontologies/ont.owl#ATP的合成>";
		String raw = "<http://www.co-ode.org/ontologies/ont.owl#原料>";
		String raw_H="<http://www.co-ode.org/ontologies/ont.owl#H>";
		String output = "<http://www.co-ode.org/ontologies/ont.owl#产物>";
		ResultSet result_sub;
		result_sub = sp.findBioprocessFromParentbio(sub1, sub2);
		String a;
		a=ResultSetFormatter.asXMLString(result_sub);
		sp.writeToFile(a,"sub.xml");
		sp.closeQe();
		ResultSet result_raw_out;
		result_raw_out = sp.findRawOutput(sub3, raw, output);
		a=ResultSetFormatter.asXMLString(result_raw_out);
		sp.writeToFile(a,"atp_raw_output.xml");
		sp.closeQe();
		ResultSet result_raw_BiologyProcess;
		result_raw_BiologyProcess=sp.findBioprocessFromRaw(raw_H, raw);
		a=ResultSetFormatter.asXMLString(result_raw_BiologyProcess);
		sp.writeToFile(a, "H_raw_process.xml");
		sp.closeQe();
	}
}
