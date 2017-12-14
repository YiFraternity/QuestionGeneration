package com.raw_output.whats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/****
 * @comment 暂且无用
 */
public class WritePhotosynthsisToXML {

	/****
	 * @Comments 将数组里面的内容写入CSV文件
	 * @param strFile
	 * @param arrlist
	 */
	public void writeToCSV(String strFile,ArrayList<String> arrlist){
		//表头
		Object[] head={"生物过程","原料","产物"};
		List<Object> headList = Arrays.asList(head);
		
		//数据
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		List<Object> rowlist =null;
		for(int i =0;i<arrlist.size();i=i+2){
			String pro = (String)arrlist.get(i);
			String pro_str = (String)arrlist.get(i+1);
			pro_str = pro_str.replace("原料:", "");
			pro_str = pro_str.replace("产物:", "");
			String pro_raw = pro_str.substring(0,pro_str.indexOf("&"));
			String pro_output = pro_str.substring(pro_str.indexOf("&")+1);
//			System.out.println(pro+","+pro_raw+","+pro_output);
			rowlist = new ArrayList<Object>();
			rowlist.add(pro);
			rowlist.add(pro_raw);
			rowlist.add(pro_output);
			dataList.add(rowlist);
		}
		File csvfile= new File(strFile);
		BufferedWriter writer;
		try{
			if(!csvfile.exists())
			{
				csvfile.createNewFile();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvfile),"UTF-8"),1024);
			
			int num=headList.size()/2;
			StringBuffer buffer = new StringBuffer();
			for(int i=0;i<num;i++){
				buffer.append(",");
			}
			//writer.write(buffer.toString()+strFile+buffer.toString());
			//写入文件头部
			writeRow(headList,writer);
			
			//写入文件内容
			for(List<Object>row:dataList){
				writeRow(row,writer);
			}
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void writeRow(List<Object>row,BufferedWriter csvWriter) throws IOException{
		for(Object data:row){
			StringBuffer sbuffer = new StringBuffer();
			String rowstr = sbuffer.append("\"").append(data).append("\",").toString();
			csvWriter.write(rowstr);
		}
		csvWriter.newLine();
	}
	
	/******************将光合作用写入XML文件*****************/
	public void WriteToXML(String strFile,String a){
		try{
			DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docfactory.newDocumentBuilder();
			
			//root elements
			Document doc = docBuilder.newDocument();
			Element rootelement = doc.createElement("sparql");
			doc.appendChild(rootelement);
			//set attribute to root element
			Attr attr = doc.createAttribute("xmlns");
			attr.setValue("http://www.w3.org/2005/sparql-results#");
			rootelement.setAttributeNode(attr);
			
			//head element
			Element head = doc.createElement("head");
			rootelement.appendChild(head);
			
			//variable element
			Element variable =doc.createElement("variable");
			head.appendChild(variable);
			//set attribute to variable element
			Attr variable_attr = doc.createAttribute("name");
			variable_attr.setValue("子过程");
			variable.setAttributeNode(variable_attr);
			
			//results element
			Element results = doc.createElement("results");
			rootelement.appendChild(results);
			
			//result element
			Element result = doc.createElement("result");
			results.appendChild(result);
			
			//binding element
			Element binding =doc.createElement("binding");
			result.appendChild(binding);
			//set attribute to binding element
			Attr binding_attr = doc.createAttribute("name");
			binding_attr.setValue("子过程");
			binding.setAttributeNode(binding_attr);
			
			// uri elements
			Element uri = doc.createElement("uri");
			uri.appendChild(doc.createTextNode(a));
			binding.appendChild(uri);
			
			//write the content into xml file
			File file;
			try{
				file = new File(strFile);
				if(!file.exists())
				{
					file.createNewFile();
				}
				TransformerFactory transformerfactory = TransformerFactory.newInstance();
				Transformer transformer = transformerfactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult sResult = new StreamResult(file);
				transformer.transform(source, sResult);
			}catch(IOException e){
				e.printStackTrace();
			}
			
		}catch(ParserConfigurationException pe){
			pe.printStackTrace();
		}catch(TransformerException tfe){
			tfe.printStackTrace();
		}
	}
}
