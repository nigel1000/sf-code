package cn.sf.netty.demo.serialize.javaser;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubscribeResp implements Serializable {

   /**
   * 默认序列ID
   */
 private static final long serialVersionUID = 1L;

   private int subReqID;

   private int respCode;

   private String desc;

   @Override
   public String toString() {
   return "SubscribeResp [subReqID=" + subReqID + ", respCode=" + respCode
     + ", desc=" + desc + "]";
   }
 }
