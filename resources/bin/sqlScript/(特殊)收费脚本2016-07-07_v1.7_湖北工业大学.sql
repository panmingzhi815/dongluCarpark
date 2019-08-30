use carpark;
GO

alter table carpark_duration_standard alter column start_time datetime;
alter table carpark_duration_standard alter column end_time datetime;
GO
--当前时段收满的功能函数
IF  EXISTS(SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[getFullCurrentDurationPrice]') and type in(N'FN',N'TF'))
DROP function [dbo].[getFullCurrentDurationPrice]
go

create function [dbo].[getFullCurrentDurationPrice](@intime datetime,@outtime datetime,@standardId int,@AcrossdayChargeStyle int,@IsAcrossDay int,@IsAcrossDuration int)
returns @t table(resultMoney float,resultTime dateTime)
begin
	declare @InTimeTmp datetime,@durationId int,@unitDuration int,@unitPrice float,@durationPriceSize int,@hourSize int,@minSize int,@resultMoney float,
		@durationEndTime datetime,@overrideSize int,@acrossTimeSize int,@acrossPrice int,@startStepPrice float,@startStepTime int,@durationSize int
	set @resultMoney = 0
	set @InTimeTmp = CONVERT(VARCHAR,@intime,108)
	select @durationSize=count(1) from carpark..carpark_duration_standard cds WHERE standard_id = @standardId
	SELECT top 1 @startStepTime=startStepTime,@startStepPrice=startStepPrice,@acrossTimeSize=[Cross_unit_duration],@acrossPrice=[Cross_unit_price],@durationId = id,@unitDuration = unit_duration,@unitPrice=unit_price,@durationEndTime=end_time FROM carpark..carpark_duration_standard cds WHERE standard_id = @standardId 
		AND ((start_time < end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND end_time)
		OR
		(start_time > end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN dateadd(day,-1,start_time) AND end_time)
		or
		(start_time >= end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND dateadd(day,1,end_time))
		)
	if 	@durationSize=1
	begin
		set @InTimeTmp=@outtime;
	end
	else
	begin
		set @InTimeTmp = CONVERT(varchar(10), @intime, 120 ) + ' ' + CONVERT(varchar(8) , @durationEndTime, 108 )
		if @InTimeTmp <= @intime
		begin
			set @InTimeTmp = DateAdd(DAY,1,@InTimeTmp);
		end
	end
	
	if datediff(minute,@intime,@outtime)<=60 and datediff(minute,@InTimeTmp,@outtime)>=1 and @outtime>@InTimeTmp and @intime>CONVERT(datetime,CONVERT(varchar,@intime,23)+' 18:01:00',120)
	begin
		SELECT top 1 @startStepTime=startStepTime,@startStepPrice=startStepPrice,@acrossTimeSize=[Cross_unit_duration],@acrossPrice=[Cross_unit_price],@durationId = id,@unitDuration = unit_duration,@unitPrice=unit_price,@durationEndTime=end_time FROM carpark..carpark_duration_standard cds WHERE standard_id = @standardId 
		AND ((start_time < end_time AND '1970-01-01 ' + CONVERT(VARCHAR,@outtime,108) BETWEEN start_time AND end_time)
		OR
		(start_time > end_time AND '1970-01-01 ' + CONVERT(VARCHAR,@outtime,108) BETWEEN dateadd(day,-1,start_time) AND end_time)
		or
		(start_time >= end_time AND '1970-01-01 ' + CONVERT(VARCHAR,@outtime,108) BETWEEN start_time AND dateadd(day,1,end_time))
		)
	end
	
	
	if @outtime > @InTimeTmp
	begin
		set @hourSize=60-datediff(minute,@intime,@InTimeTmp)%60
		set @outtime = dateadd(minute,@hourSize,@InTimeTmp)
	end
	if @IsAcrossDuration=0 and @startStepTime>0
	begin
		set @intime=dateadd(minute,@startStepTime,@intime);
		set @resultMoney=@resultMoney+@startStepPrice;
	end
	if @outtime>@intime
	begin
		if @AcrossdayChargeStyle=0
		begin
			set @hourSize = DATEDIFF(SECOND,@intime,@outtime)/(@unitDuration*60)
			set @minSize = DATEDIFF(SECOND,@intime,@outtime)%(@unitDuration*60)/60
			if @minSize <> 0
			begin
				set @hourSize = @hourSize+1;
			end
			select @resultMoney =@resultMoney+duration_length_price from carpark_duration_price where duration_length = @hourSize and  duration_id = @durationId
		end
		else
		begin
			if @IsAcrossDay>0
			begin
				set @minSize = DATEDIFF(SECOND,@intime,@outtime)/60
				if @acrossTimeSize<>0
				begin
					set @resultMoney=@resultMoney+(@minSize/@acrossTimeSize)*@acrossPrice;
					if @minSize%@acrossTimeSize>0
					begin
						set @resultMoney=@resultMoney+@acrossPrice;
					end
				end
			end
			else
			begin
				set @hourSize = DATEDIFF(SECOND,@intime,@outtime)/(@unitDuration*60)
				set @minSize = DATEDIFF(SECOND,@intime,@outtime)%(@unitDuration*60)/60
				if @minSize <> 0
				begin
					set @hourSize = @hourSize+1;
				end
				select @resultMoney =@resultMoney+duration_length_price from carpark_duration_price where duration_length = @hourSize and  duration_id = @durationId
			end		
		end
	end
	insert into @t values(@resultMoney,DateAdd(second,1,@outtime));
	return;
end
go


--创建计费存储过程
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[upGetNewPakCarCharge]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[upGetNewPakCarCharge]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

create PROCEDURE [dbo].[upGetNewPakCarCharge]
@CarparkId	bigint,
@PakCarTypeID	char(2),
@InTime	    DateTime,
@OutTime	DATETIME,
@SumCharge	numeric(8,2)	output
AS
SET NOCOUNT ON

set @SumCharge = 0

-- 定义一些常用变量
DECLARE         
	@FreeTime int,
	@StartStepTime int,
	@OneDayMaxMoney numeric(8,2),
	@StartStepMoney numeric(8,2),
	--当天是否为工作日
	@IsCurrentWorkDay int,
	--第二天是否为工作日
	@IsNextWorkDay int,
	--当前车类型收费标准数量
	@ChargeStandardSize int,
	--当前标准有多少个收费时段
	@CurrentDurationChargeStandardSize int,
	--当前收费标准id
	@CurrentChargeStandardId int,
	@exchangeTime datetime,
	@InitInTime datetime,
	@InitOutTime datetime,
	@ChargeTimeType int,
	@AcrossdayChargeStyle int,
	@chargeSummay numeric(8,2),
	--是否跨了天
	@IsAcrossDay int,
	@AcrossdayChargeEnable int,
	--跨天是否免费
	@acrossDayIsFree int,
	--是否跨了时段
	@IsAcrossDuration int,
	@FinalInitInTime datetime,
	@AcrossDayMorePrice int,
	@NowIsAcrossDay int
	
	

--------判断是否超过免费的时间，如果没有，直接结束，如果是，继续计算----------------------------
--判断起始时间是否在节假日内
SELECT @IsCurrentWorkDay = COUNT(1) FROM Holiday h WHERE @InTime >= h.start and @InTime < DATEADD(Day,h.length,h.start)
--判断当前车牌有几个收费标准
select @ChargeStandardSize = COUNT(1) from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and carpark_id = @CarparkId and using = 'True'; 

--如果没有设置收费标准，则直接返回0
if @ChargeStandardSize = 0
begin
	set @SumCharge = 0
	return
end
--如果只有一种收费标准，则直接获取停车的免费时长
if @ChargeStandardSize = 1
begin
	select @AcrossDayMorePrice=ccs.acrossDayPrice,@acrossDayIsFree=ccs.acrossDayIsFree,@AcrossdayChargeEnable=ccs.acrossday_charge_enable,@ChargeTimeType=ccs.charge_time_type ,@AcrossdayChargeStyle=ccs.acrossday_charge_style,@OneDayMaxMoney=ccs.oneday_max_charge,@FreeTime = ccs.free_time,@StartStepTime=ccs.First_Time,@StartStepMoney=ccs.First_Time_Fee from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and carpark_id = @CarparkId and using = 'True'
end
--如果有工作日与非工作日之分，则取当前时间的收费标准
if @ChargeStandardSize = 2
begin
	--取非工作日的免费时长
	if @IsCurrentWorkDay <> 0
	begin
		select  @AcrossDayMorePrice=ccs.acrossDayPrice,@AcrossdayChargeEnable=ccs.acrossday_charge_enable,@ChargeTimeType=ccs.charge_time_type ,@AcrossdayChargeStyle=ccs.acrossday_charge_style,@OneDayMaxMoney=ccs.oneday_max_charge,@FreeTime = ccs.free_time,@StartStepTime=ccs.First_Time,@StartStepMoney=ccs.First_Time_Fee from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and ccs.workday_type = 0 and carpark_id = @CarparkId and using = 'True'
	end
	--取工作日的免费时长
	else begin
		select @AcrossDayMorePrice=ccs.acrossDayPrice,@AcrossdayChargeEnable=ccs.acrossday_charge_enable,@ChargeTimeType=ccs.charge_time_type ,@AcrossdayChargeStyle=ccs.acrossday_charge_style,@OneDayMaxMoney=ccs.oneday_max_charge,@FreeTime = ccs.free_time,@StartStepTime=ccs.First_Time,@StartStepMoney=ccs.First_Time_Fee from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and ccs.workday_type = 1 and carpark_id = @CarparkId and using = 'True'
	end
end
--如果在免费时长之内，则直接返回0
IF DateDiff(Second, @InTime, @OutTime) <= (@FreeTime*60)
begin
	set @SumCharge = 0
	return
end
if @AcrossdayChargeEnable=0
begin
	set @InTime=DATEADD(MINUTE,@FreeTime,@InTime);
end



set @chargeSummay=0;
set @InitInTime=@InTime;
set @FinalInitInTime=@InTime;
set @InitOutTime=@OutTime;
set @IsAcrossDay=0;
set @IsAcrossDuration=0;
set @NowIsAcrossDay=0;
while @InitInTime<@InitOutTime
begin
	--分天计算
	if @AcrossdayChargeEnable=0 and @IsAcrossDay>0 and @acrossDayIsFree>0
	begin
		set @InitInTime=DATEADD(MINUTE,@FreeTime,@InitInTime);
	end
	set @InTime=@InitInTime;
	if @ChargeTimeType=0
	begin
		if @ChargeStandardSize>1
		begin
			SELECT @IsCurrentWorkDay = COUNT(1) FROM Holiday h WHERE @InitInTime >= h.start and @InitInTime < DATEADD(Day,h.length,h.start);
			SELECT @IsNextWorkDay = COUNT(1) FROM Holiday h WHERE DATEADD(DAY,1,@InitInTime) >= h.start and DATEADD(DAY,1,@InitInTime) < DATEADD(Day,h.length,h.start)
			if @IsCurrentWorkDay <> @IsNextWorkDay
			begin
				if DATEDIFF(day,@InitInTime,@InitOutTime)>0
				begin
					set @InitInTime=CONVERT(VARCHAR(10),DATEADD(day,1,@InitInTime),120)+' 00:00:00';
					set @NowIsAcrossDay=1;
				end
				else
				begin
					set @InitInTime=@InitOutTime;
				end
			end
			else
			begin
				if DATEADD(DAY,1,@InitInTime)>@InitOutTime
				begin
					set @InitInTime=@InitOutTime;
				end
				else
				begin
					set @InitInTime=DATEADD(DAY,1,@InitInTime);
					set @NowIsAcrossDay=1;
				end	
			end
		end
		else
		begin
			if DATEADD(DAY,1,@InitInTime)>@InitOutTime
			begin
				set @InitInTime=@InitOutTime;
			end
			else
			begin
				set @InitInTime=DATEADD(DAY,1,@InitInTime);
				set @NowIsAcrossDay=1;
			end	
		end		
			
	end
	else
	begin
		if DATEDIFF(day,@InitInTime,@InitOutTime)>0
		begin
			set @InitInTime=CONVERT(VARCHAR(10),DATEADD(day,1,@InitInTime),120)+' 00:00:00';
			set @NowIsAcrossDay=1;
		end
		else
		begin
			set @InitInTime=@InitOutTime;
		end
	end
	set @OutTime=@InitInTime;
	--
	
	--获取一天最大收费
	if @ChargeStandardSize>1
	begin
		SELECT @IsCurrentWorkDay = COUNT(1) FROM Holiday h WHERE @InTime >= h.start and @InTime < DATEADD(Day,h.length,h.start)
		--判断起始时间是否为节假日
		if @IsCurrentWorkDay > 0
		begin
			select @AcrossDayMorePrice=ccs.acrossDayPrice,@OneDayMaxMoney=ccs.oneday_max_charge from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and ccs.workday_type = 0 and carpark_id = @CarparkId and using = 'True'
		end
		else
		begin
			select @AcrossDayMorePrice=ccs.acrossDayPrice,@OneDayMaxMoney=ccs.oneday_max_charge from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and ccs.workday_type = 1 and carpark_id = @CarparkId and using = 'True'
		end
	end
	--跨天是否免费
	if @IsAcrossDay>0 and @acrossDayIsFree>0 and @AcrossdayChargeEnable=1
	begin		
		set @InTime=DATEADD(MINUTE,@FreeTime,@InTime);
	end
	if @IsCurrentWorkDay=0
	begin
		set @IsCurrentWorkDay=1
	end
	else if @IsCurrentWorkDay>0
	begin
		set @IsCurrentWorkDay=0
	end
	while @InTime < @OutTime
	begin	
		
		if @ChargeStandardSize>1
		begin
			select @CurrentChargeStandardId = id from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and ccs.workday_type = @IsCurrentWorkDay and carpark_id = @CarparkId and using = 'True'
		end
		else
		begin
			select @CurrentChargeStandardId = id from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and carpark_id = @CarparkId and using = 'True'
		end
		select @SumCharge = @SumCharge +resultMoney,@InTime=resultTime from [dbo].[getFullCurrentDurationPrice](@InTime,@OutTime,@CurrentChargeStandardId,@AcrossdayChargeStyle,@IsAcrossDay,@IsAcrossDuration);

		set @IsAcrossDuration=1
	end
	if @NowIsAcrossDay>0
	begin
		set @SumCharge=@SumCharge+@AcrossDayMorePrice;
		set @NowIsAcrossDay=0;
	end
	--一天最大收费
	if @OneDayMaxMoney>0
	begin
		if @SumCharge>@OneDayMaxMoney
		begin
		set @SumCharge=@OneDayMaxMoney;
		end
	end
	set @chargeSummay=@chargeSummay+@SumCharge;
	set @SumCharge=0;
	set @IsAcrossDay=1;
end
set @SumCharge=@chargeSummay;
