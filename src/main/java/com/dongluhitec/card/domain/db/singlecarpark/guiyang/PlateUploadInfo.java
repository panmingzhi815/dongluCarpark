package com.dongluhitec.card.domain.db.singlecarpark.guiyang;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PlateUploadInfo {
	private String cs_id; // 服务商在平台注册的编号 Y [string] 10000
	private String msg_ver="V1.1"; // 版本号 Y [string] V1.1
	private String cs_rec_no; // 服务商系统记录流水号 Y [string] 单服务商停车场管理系统内唯一
	private long timestamp=System.currentTimeMillis()/1000; // 请求时间 Y [number] 时间戳(单位秒) 1537513015
	private String park_app_id; // 停车场appId Y [string] 接入时由平台统一分配 2014072300007148
	private String park_name; // 停车场名称 Y [string]
	private String channel_id; // 停车场进口id Y [string] 接入时由平台统一分配
	private String channel_in_id; // 进口编号 Y [string] 编号从 1 开始顺延
	private String channel_in_name; // 进口名称 Y [string]
	private String plate; // 车牌号 Y [string]
	private String plate_type; // 车牌类型 Y [string] 详见附录
	private long pass_time; // 通行时间 Y [number] 时间戳(单位秒)
	private String rep_up_tag; // 补传标志 Y [string] 0 补传数据，1 实时数据
	private int total_num_reg; // 停车场注册总车位数 Y [string] 此处的剩余车位数使用可容纳总车位数-在场车辆数
	private int total_num_in; // 在场车辆数 Y [string]
	private int blank_num; // 剩余车位数 Y [string]
	private int rent_tag; // 月卡标志 Y [string] 0 为非月卡，1 为月卡
	private String name; // 姓名 N [string] 月卡车辆必传
	private String comtact; // 联系号码 N [string] 月卡车辆必传
	private String places; // 详情地址 N [string] 包括楼栋号，单元号，房号 月卡车辆必传
	private String beginDate; // 有效期开始时间 N [string] 时间戳 月卡车辆必传
	private String endDate; // 有效期结束时间 N [string] 时间戳 月卡车辆必传
	private String sign_type="RSA"; // 加密类型 Y [string] RSA

	private String channel_out_id;// 出口编号 Y [string] 编号从 1 开始顺延
	private String channel_out_name;// 出口名称 Y [string]
	private String out_time;// 出场时间 Y [number] 时间戳(单位秒)
	private int amount;// 停车费用 Y [double] 单位分 3000
	private String gateway="offline";// 支付类型 Y [string] platform-平台支付，offline-线下支付 platform
	private long in_time;// 入场时间 Y [string] 时间戳(单位秒)
	private long keep_time;// 停车时长 Y [string] 以秒为单位
	private String sign;// 加密数据 Y [string] z2HfGE7SagPh03aW

}
