package com.extend;

import java.util.ArrayList;

/***
 * @Copyright(c),2017-2018
 * @Project 光合作用自动出题
 * @Comments 作为一个结构体
 *           用于存放生物原料和产物，以便返回上一层生物过程
 *           将子过程使用的原料和产物作为上一层生物过程的原料和产物
 * @JDKversion JDK1.8
 * @CreatorDate 2017-9
 * @ModifiedBy 刘宇航
 * @ModifiedDate 2017-11-21
 * @ModifiedReason 完善代码，对代码进行优化
 * @Version 2.0.0
 * @author User
 */
public class Struct {

	 public ArrayList<String> struct_arr_raw = new ArrayList<String>();
	 public ArrayList<String> struct_arr_output =new ArrayList<String>();
	 
	 public ArrayList<String> struct_process_raw=new ArrayList<String>();//存放生物过程及原料
	 public ArrayList<String> struct_Process_output=new ArrayList<String>();//存放生物过程及产物
	
	/**
	* @Comments 存放子过程的生物原料
	*           子过程的生物原料存放在结构体里面
	*           目的是将子过程的生物原料作为父过程的原料
	* @param raw
	*/
	public void addRawToStruct(ArrayList<String> raw){
		for(int i=0;i<raw.size();i++)
		{
			this.struct_arr_raw.add(raw.get(i));
		}
	}
	
	/***
	* @Comments 存放子过程的产物
	*           子过程的产物存放在结构体里面
	*           目的是将子过程的产物作为父过程的原料
	* @param output
	*/
	public void addOutputToStruct(ArrayList<String> output){
		for (int i =0;i<output.size();i++){
			this.struct_arr_output.add(output.get(i));
		}
		
	}
	/**
	 * @Comments 清空结构体
	 */
	public void clearStruct(){
		this.struct_arr_raw.clear();
		this.struct_arr_output.clear();
	}
	
	/***
	 * @Comments 将子过程的原料返回父过程
	 * @param s
	 */
	public void add(Struct s){
		for(int i=0;i<s.struct_arr_raw.size();i++){
			this.struct_arr_raw.add(s.struct_arr_raw.get(i));
		}
		for(int i=0;i<s.struct_arr_output.size();i++){
			this.struct_arr_output.add(s.struct_arr_output.get(i));
		}
	}
	
	public Struct() {}
}