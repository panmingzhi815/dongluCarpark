package com.donglu.carpark;
import java.lang.reflect.Method; 
import java.lang.reflect.InvocationTargetException;

/** 
 * Created by IntelliJ IDEA. 
 * File: TestRef.java 
 * User: leizhimin 
 * Date: 2008-1-28 14:48:44 
 */ 
public class TestRef {

     public static void main(String args[]) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
         Foo foo = new Foo("这个一个Foo对象！");
         Class clazz = foo.getClass(); 
         Method m1 = clazz.getDeclaredMethod("outInfo");
         Method m2 = clazz.getDeclaredMethod("setMsg", String.class);
         Method m3 = clazz.getDeclaredMethod("getMsg");
         m1.invoke(foo); 
         m2.invoke(foo, "重新设置msg信息！"); 
         String msg = (String) m3.invoke(foo); 
         System.out.println(msg); 
     } 
 } 

class Foo { 
     private String msg; 

     public Foo(String msg) { 
         this.msg = msg; 
     } 

     public void setMsg(String msg) {
         this.msg = msg; 
     } 

     public String getMsg() { 
         return msg; 
     } 

     public void outInfo() {
         System.out.println("这是测试Java反射的测试类"); 
     } 
 }