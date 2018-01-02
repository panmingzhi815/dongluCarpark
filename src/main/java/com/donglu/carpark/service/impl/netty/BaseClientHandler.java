//package com.donglu.carpark.service.impl.netty;  
//  
//import java.io.UnsupportedEncodingException;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelHandlerContext;  
//import io.netty.channel.SimpleChannelInboundHandler;  
//  
//public class BaseClientHandler extends SimpleChannelInboundHandler<ByteBuf>{  
//	
//	private ChannelHandlerContext channelHandlerContext;
//      
////    @Override  
////    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {  
////        System.out.println("Client channelRead0 received:" + msg);  
////    }  
//      
////    @Override  
////    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
////        System.out.println("Client channelRead received:" + msg);  
////        ByteBuf buf = (ByteBuf) msg;// 获取服务端传来的Msg
////        String recieved = getMessage(buf);
////        System.out.println("------收到信息------"+recieved );
////        String type = JSON.parseObject(recieved).getString("type");
////        if("connectSuccess".equals(type)){
////        	channelHandlerContext = ctx;
////        	JSONObject jo=new JSONObject();
////    		jo.put("buildingId", "7e257819d2764bb6aa5c1fd43baf2f71");
////    		jo.put("type", "PING_MSG");
////        	//ChannelFuture future = ctx.writeAndFlush(getSendByteBuf(jo.toJSONString()));
////        	ChannelFuture future = ctx.write(getSendByteBuf(jo.toJSONString()));
////        	ctx.flush();
////        	if(future.isSuccess()){
////        		System.out.println("发送建筑id成功");
////        	}else{
////        		System.out.println("发送失败");
////        	}
////        }else if("PONG_MSG".equals(type)){
////        	
////        }else{
////        	
////        }
////        
////    } 
//    
//    /*
//     * 将字节UTF-8编码返回字符串
//     */
//    private String getMessage(ByteBuf buf) {
//        byte[] con = new byte[buf.readableBytes()];
//        buf.readBytes(con);
//        try {
//            return new String(con, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    
//    /*
//     * 将Sting转化为UTF-8编码的字节
//     */
//    private ByteBuf getSendByteBuf(String message) throws UnsupportedEncodingException {
//        byte[] req = message.getBytes("UTF-8");
//        ByteBuf pingMessage = Unpooled.buffer();
//        pingMessage.writeBytes(req);
//        return pingMessage;
//    }
//    
//     @Override  
//     public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {  
//         cause.printStackTrace();  
//         ctx.close();  
//     }
//
//	@Override
//	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
//		System.out.println("Client channelRead received:" + msg);  
//          ByteBuf buf = (ByteBuf) msg;// 获取服务端传来的Msg
//          String recieved = getMessage(buf);
//          System.out.println("------收到信息------"+recieved );
//          String type = JSON.parseObject(recieved).getString("type");
//          ctx.writeAndFlush(getSendByteBuf("123www"));
//          if("connectSuccess".equals(type)){
//          	channelHandlerContext = ctx;
//          	JSONObject jo=new JSONObject();
//      		jo.put("buildingId", "7e257819d2764bb6aa5c1fd43baf2f71");
//      		jo.put("type", "PING_MSG");
//          	ChannelFuture future = ctx.writeAndFlush(getSendByteBuf(jo.toJSONString()));
////          ChannelFuture future = ctx.write(getSendByteBuf(jo.toJSONString()));
////          ctx.flush();
//          	if(future.isSuccess()){
//          		System.out.println("发送建筑id成功");
//          	}else{
//          		System.out.println("发送失败");
//          	}
//          }else if("PONG_MSG".equals(type)){
//          	
//          }else{
//          	
//          }
//	}  
//  
//     
//  
//}