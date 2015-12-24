use carpark;
GO

alter table carpark_duration_standard alter column start_time datetime;
alter table carpark_duration_standard alter column end_time datetime;
GO
--当前时段收满的功能函数
IF  EXISTS(SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[getFullCurrentDurationPrice]') and type in(N'FN'))
DROP function [dbo].[getFullCurrentDurationPrice]
go

create function [dbo].[getFullCurrentDurationPrice](@intime datetime,@outtime datetime,@standardId int,@AcrossdayChargeStyle int,@IsAcrossDay int)
returns float 
begin
	declare @InTimeTmp datetime,@durationId int,@unitDuration int,@unitPrice float,@durationPriceSize int,@hourSize int,@minSize int,@resultMoney float,@durationEndTime datetime,@overrideSize int,@acrossTimeSize int,@acrossPrice int
	set @resultMoney = 0
	set @InTimeTmp = CONVERT(VARCHAR,@intime,108)
	SELECT top 1  @acrossTimeSize=[Cross_unit_duration],@acrossPrice=[Cross_unit_price],@durationId = id,@unitDuration = unit_duration,@unitPrice=unit_price,@durationEndTime=end_time FROM carpark..carpark_duration_standard WHERE standard_id = @standardId 
		AND ((start_time < end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND end_time)
		OR
		(start_time > end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN dateadd(day,-1,start_time) AND end_time)
		or
		(start_time >= end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND dateadd(day,1,end_time))
		)
		
	set @InTimeTmp = CONVERT(varchar(10), @intime, 120 ) + ' ' + CONVERT(varchar(8) , @durationEndTime, 108 )
	if @InTimeTmp < @intime
	begin
		set @InTimeTmp = DateAdd(DAY,1,@InTimeTmp);
	end
	if @outtime > @InTimeTmp
	begin
		set @outtime = @InTimeTmp
	end
	if @AcrossdayChargeStyle=0
	begin
		set @hourSize = DATEDIFF(SECOND,@intime,@outtime)/(60*60)
		select @resultMoney = duration_length_price from carpark_duration_price where duration_length = @hourSize and  duration_id = @durationId
		set @minSize = DATEDIFF(SECOND,@intime,@outtime)%(60*60)/60
		if @minSize <> 0
		begin
			set @overrideSize = @minSize / @unitDuration;
			SET @resultMoney = @resultMoney + (@overrideSize + 1)*@unitPrice
		end
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
			set @hourSize = DATEDIFF(SECOND,@intime,@outtime)/(60*60)
			select @resultMoney = duration_length_price from carpark_duration_price where duration_length = @hourSize and  duration_id = @durationId
			set @minSize = DATEDIFF(SECOND,@intime,@outtime)%(60*60)/60
			if @minSize <> 0
			begin
				set @overrideSize = @minSize / @unitDuration;
				SET @resultMoney = @resultMoney + (@overrideSize + 1)*@unitPrice
			end
		end		
	end
	return @resultMoney;
end
go
--当前时段收满后的重置起始时间
IF  EXISTS(SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[getFullCurrentDurationTime]') and type in(N'FN'))
DROP function [dbo].[getFullCurrentDurationTime]
go

create function [dbo].[getFullCurrentDurationTime](@intime datetime,@outtime datetime,@standardId int)
returns datetime
begin
	declare @InTimeTmp datetime,@resultTime datetime,@durationStartTime datetime,@durationEndTime datetime
	set @InTimeTmp = CONVERT(VARCHAR,@intime,108)
	SELECT top 1 @durationStartTime=start_time,@durationEndTime=end_time FROM carpark..carpark_duration_standard WHERE standard_id = @standardId 
		AND ((start_time < end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND end_time)
		OR
		(start_time > end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN dateadd(day,-1,start_time) AND end_time)
		or
		(start_time >= end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND dateadd(day,1,end_time))
		)
		
	set @resultTime = CONVERT(VARCHAR(10),@intime,120) + ' ' + convert(varchar(8),@durationEndTime,114)
	set @resultTime = DATEADD(SECOND,1,@resultTime)
	set @InTimeTmp = CONVERT(VARCHAR(10),@intime,120) + ' ' + convert(varchar(8),@durationStartTime,114)
	
	if @durationEndTime <= @durationStartTime
	begin
		if @intime > @InTimeTmp
		begin
			if DATEADD(DAY,1,@resultTime) > @outtime
			begin
				set @resultTime = @outtime
			end
			else
			begin
				set @resultTime = DATEADD(DAY,1,@resultTime);
			end
		end
	end
	return @resultTime
end
go

--当前时段收满后的重置截止时间，因为有可能不满整点，却收了整点的金额
IF  EXISTS(SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[getFullCurrentDurationExtendAppendSec]') and type in(N'FN'))
DROP function [dbo].[getFullCurrentDurationExtendAppendSec]
go

create function [dbo].[getFullCurrentDurationExtendAppendSec](@intime datetime,@outtime datetime,@standardId int)
returns int
begin
	declare @InTimeTmp datetime,@resultSec int,@durationStartTime datetime,@durationEndTime datetime
	set @InTimeTmp = CONVERT(VARCHAR,@intime,108)
	SELECT top 1 @durationStartTime=start_time,@durationEndTime=end_time FROM carpark..carpark_duration_standard WHERE standard_id = @standardId 
		AND ((start_time < end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND end_time)
		OR
		(start_time > end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN dateadd(day,-1,start_time) AND end_time)
		or
		(start_time >= end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND dateadd(day,1,end_time))
		)
		
	set @InTimeTmp = CONVERT(varchar(10), @intime, 120 ) + ' ' + CONVERT(varchar(8) , @durationEndTime, 108 )
	if @InTimeTmp < @intime
	begin
		set @InTimeTmp = DateAdd(DAY,1,@InTimeTmp);
	end
	if @outtime < @InTimeTmp
	begin
		set @InTimeTmp = @outtime
	end
	
	set @resultSec = Datediff(second,@intime,@InTimeTmp)
	if @resultSec % (60*60) <> 0
	begin
	 set @resultSec = 3600 - @resultSec % (60*60);
	end
	else begin
		set @resultSec = 0
	end
	--每次修正起始时间时，都加了1秒，在这里要在截止时间中减掉
	--set @resultSec = @resultSec + 1;
	return @resultSec;
end
go

--当前所在收费时段是否跨天
IF  EXISTS(SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[isCurrentDurationContain24]') and type in(N'FN'))
DROP function [dbo].[isCurrentDurationContain24]
go
create function [dbo].[isCurrentDurationContain24](@intime datetime,@standardId int)
returns bit
begin
	declare @InTimeTmp datetime,@resultBool bit,@durationStartTime datetime,@durationEndTime datetime,@arrive24HourSize int,@totalHour int,@durationId int
	set @InTimeTmp = CONVERT(VARCHAR,@intime,108)
	SELECT top 1 @durationStartTime=start_time,@durationEndTime=end_time,@durationId=id FROM carpark..carpark_duration_standard WHERE standard_id = @standardId 
		AND ((start_time < end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND end_time)
		OR
		(start_time > end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN dateadd(day,-1,start_time) AND end_time)
		or
		(start_time >= end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND dateadd(day,1,end_time))
		)
	
	if @durationEndTime < @durationStartTime
	begin
		select @totalHour = COUNT(1) from carpark..carpark_duration_price WHERE duration_id = @durationId
		set @arrive24HourSize = DATEDIFF(SECOND,@intime,DATEADD(day,1,CONVERT(VARCHAR(10),@intime,120)))/(60*60)
		if @arrive24HourSize > @totalHour
		begin
			set @resultBool = 0
		end
		else
		begin
			set @resultBool = 1
		end
		
	end
	else begin
		set @resultBool = 0
	end
	return @resultBool
end
go

--当前所在收费时段到24点的金额
IF  EXISTS(SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[arrive24DurationPrice]') and type in(N'FN'))
DROP function [dbo].[arrive24DurationPrice]
go
create function [dbo].arrive24DurationPrice(@intime datetime,@outtime datetime,@standardId int)
returns float
begin
	declare @InTimeTmp datetime,@resultMoney float,@durationId int,@unitDuration float,@unitPrice float,@hourSize int,@arrive24HourSize int ,@minSize int,@durationEndTime datetime,@overrideSize int
	set @InTimeTmp = CONVERT(VARCHAR,@intime,108)
	set @resultMoney = 0
	SELECT top 1  @durationId = id,@unitDuration = unit_duration,@unitPrice=unit_price,@durationEndTime=end_time FROM carpark..carpark_duration_standard WHERE standard_id = @standardId 
		AND ((start_time < end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND end_time)
		OR
		(start_time > end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN dateadd(day,-1,start_time) AND end_time)
		or
		(start_time >= end_time AND '1970-01-01 ' + @InTimeTmp BETWEEN start_time AND dateadd(day,1,end_time))
		)
		
	set @hourSize = DATEDIFF(SECOND,@intime,@outtime)/(60*60)
	set @arrive24HourSize = DATEDIFF(SECOND,@intime,DATEADD(day,1,CONVERT(VARCHAR(10),@intime,120)))/(60*60)
	if @hourSize > @arrive24HourSize
	begin
		select @resultMoney = @resultMoney + duration_length_price from carpark_duration_price where duration_length = @arrive24HourSize and  duration_id = @durationId
		set @minSize = DATEDIFF(SECOND,@intime,DATEADD(day,1,CONVERT(VARCHAR(10),@intime,120)))%(60*60)/60
		if @minSize <> 0
		begin
			set @overrideSize = @minSize / @unitDuration
			SET @resultMoney = @resultMoney + (@overrideSize + 1)*@unitPrice
		end
	end
	else begin
		select @resultMoney = @resultMoney + duration_length_price from carpark_duration_price where duration_length = @hourSize and  duration_id = @durationId
		set @minSize = DATEDIFF(SECOND,@intime,@outtime)%(60*60)/60
		if @minSize <> 0
		begin
			set @overrideSize = @minSize / @unitDuration
			SET @resultMoney = @resultMoney + (@overrideSize + 1)*@unitPrice
		end
	end
	return @resultMoney;
end
go

--当前所在收费时段到24点后的重置时间
IF  EXISTS(SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[arrive24DurationTime]') and type in(N'FN'))
DROP function [dbo].[arrive24DurationTime]
go
create function [dbo].[arrive24DurationTime](@intime datetime,@outtime datetime,@standardId int)
returns datetime
begin
	declare @resultTime datetime
	set @resultTime = DATEADD(day,1,CONVERT(VARCHAR(10),@intime,120))
	set @resultTime = DATEADD(SECOND,1,@resultTime)
	return @resultTime;
end
go

--当前所在收费时段到24点时，不足整点，却收了整点的金额，要在截止时间中把多收的时间去掉
IF  EXISTS(SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[arrive24DurationExtendApendSec]') and type in(N'FN'))
DROP function [dbo].[arrive24DurationExtendApendSec]
go
create function [dbo].[arrive24DurationExtendApendSec](@intime datetime,@outtime datetime,@standardId int)
returns int
begin
	declare @resultSec float,@arrive24HourSize int
	set @arrive24HourSize = DATEDIFF(SECOND,@intime,DATEADD(day,1,CONVERT(VARCHAR(10),@intime,120)))
	if @arrive24HourSize % (60*60) <> 0
	begin
		set @resultSec = @arrive24HourSize % (60*60);
	end
	else begin
		set @resultSec = 0;
	end
	--每次修正起始时间时，都加了1秒，在这里要在截止时间中减掉
	set @resultSec = @resultSec + 1;
	return @resultSec;
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
	@IsAcrossDay int
	
	

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
	select @ChargeTimeType=ccs.charge_time_type ,@AcrossdayChargeStyle=ccs.acrossday_charge_style,@OneDayMaxMoney=ccs.oneday_max_charge,@FreeTime = ccs.free_time,@StartStepTime=ccs.First_Time,@StartStepMoney=ccs.First_Time_Fee from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and carpark_id = @CarparkId and using = 'True'
end
--如果有工作日与非工作日之分，则取当前时间的收费标准
if @ChargeStandardSize = 2
begin
	--取非工作日的免费时长
	if @IsCurrentWorkDay <> 0
	begin
		select @ChargeTimeType=ccs.charge_time_type ,@AcrossdayChargeStyle=ccs.acrossday_charge_style,@OneDayMaxMoney=ccs.oneday_max_charge,@FreeTime = ccs.free_time,@StartStepTime=ccs.First_Time,@StartStepMoney=ccs.First_Time_Fee from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and ccs.workday_type = 0 and carpark_id = @CarparkId and using = 'True'
	end
	--取工作日的免费时长
	else begin
		select @ChargeTimeType=ccs.charge_time_type ,@AcrossdayChargeStyle=ccs.acrossday_charge_style,@OneDayMaxMoney=ccs.oneday_max_charge,@FreeTime = ccs.free_time,@StartStepTime=ccs.First_Time,@StartStepMoney=ccs.First_Time_Fee from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and ccs.workday_type = 1 and carpark_id = @CarparkId and using = 'True'
	end
end
--如果在免费时长之内，则直接返回0
IF DateDiff(Second, @InTime, @OutTime) <= (@FreeTime*60)
begin
	set @SumCharge = 0
	return
end

--如果在起步时长之内,则直接返回起步金额，如果不在，则累加起步金额，将时间向后推起步时间
IF DateDiff(Second, @InTime, @OutTime) <= (@StartStepTime*60)
begin
	set @SumCharge = @StartStepMoney
	return
end
else begin
	set @InTime = DATEADD(MINUTE,@StartStepTime,@InTime);
	set @SumCharge = @SumCharge + @StartStepMoney;
end
set @chargeSummay=0;
set @InitInTime=@InTime;
set @InitOutTime=@OutTime;
set @IsAcrossDay=0;
while @InitInTime<@InitOutTime
begin
	set @InTime=@InitInTime;
	if @ChargeTimeType=0
	begin
		if DATEADD(DAY,1,@InitInTime)>@InitOutTime
		begin
			set @InitInTime=@InitOutTime;
		end
		else
		begin
			set @InitInTime=DATEADD(DAY,1,@InitInTime);
		end
	end
	else
	begin
		if DATEDIFF(day,@InitInTime,@InitOutTime)>0
		begin
			set @InitInTime=CONVERT(VARCHAR(10),DATEADD(day,1,@InitInTime),120)+' 00:00:00';
		end
		else
		begin
			set @InitInTime=@InitOutTime;
		end
	end
	set @OutTime=@InitInTime;
	
	while @InTime < @OutTime
	begin
		set @IsCurrentWorkDay = 0;
		set @IsNextWorkDay = 0;
		
		SELECT @IsCurrentWorkDay = COUNT(1) FROM Holiday h WHERE @InTime >= h.start and @InTime < DATEADD(Day,h.length,h.start)
		--判断起始时间是否为节假日
		if @IsCurrentWorkDay > 0
		begin
			SELECT @IsNextWorkDay = COUNT(1) FROM Holiday h WHERE DATEADD(DAY,1,@InTime) >= h.start and DATEADD(DAY,1,@InTime) < DATEADD(Day,h.length,h.start)
			select @CurrentChargeStandardId = id from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and ccs.workday_type = 0 and carpark_id = @CarparkId and using = 'True'
			select @CurrentDurationChargeStandardSize = COUNT(1) from carpark_duration_standard cds where cds.standard_id = @CurrentChargeStandardId
			--如果第二天还是节假日
			if @IsNextWorkDay > 0
			begin
				set @SumCharge = @SumCharge + [dbo].[getFullCurrentDurationPrice](@InTime,@OutTime,@CurrentChargeStandardId,@AcrossdayChargeStyle,@IsAcrossDay);
				set @exchangeTime = [dbo].[getFullCurrentDurationTime](@InTime,@OutTime,@CurrentChargeStandardId);
				--set @OutTime = DATEADD(SECOND,[dbo].[getFullCurrentDurationExtendAppendSec](@InTime,@OutTime,@CurrentChargeStandardId)*-1,@OutTime);
				set @InTime = @exchangeTime;
			end
			--如果第二天不再是节假日
			else begin
				if @ChargeStandardSize = 1
				begin
					set @SumCharge = @SumCharge + [dbo].[getFullCurrentDurationPrice](@InTime,@OutTime,@CurrentChargeStandardId,@AcrossdayChargeStyle,@IsAcrossDay);
					set @exchangeTime = [dbo].[getFullCurrentDurationTime](@InTime,@OutTime,@CurrentChargeStandardId);
					--set @OutTime = DATEADD(SECOND,[dbo].[getFullCurrentDurationExtendAppendSec](@InTime,@OutTime,@CurrentChargeStandardId)*-1,@OutTime);
					set @InTime = @exchangeTime;
				end
				else begin
					--当前节假日有两个收费时段
					if @CurrentDurationChargeStandardSize > 1
					begin
						--如果当前所在的时段跨天，收到24点，重置起始时间
						if [dbo].[isCurrentDurationContain24](@InTime,@CurrentChargeStandardId) = 1
						begin
							set @SumCharge = @SumCharge + [dbo].[arrive24DurationPrice](@InTime,@OutTime,@CurrentChargeStandardId);
							set @exchangeTime = [dbo].[arrive24DurationTime](@InTime,@OutTime,@CurrentChargeStandardId);
							--set @OutTime = DATEADD(SECOND,[dbo].[arrive24DurationExtendApendSec](@InTime,@OutTime,@CurrentChargeStandardId)*-1,@OutTime);
							set @InTime = @exchangeTime;
						end
						--如果没有跨天，收满当前时段，重置起始时间
						else begin
							set @SumCharge = @SumCharge + [dbo].[getFullCurrentDurationPrice](@InTime,@OutTime,@CurrentChargeStandardId,@AcrossdayChargeStyle,@IsAcrossDay);
							set @exchangeTime = [dbo].[getFullCurrentDurationTime](@InTime,@OutTime,@CurrentChargeStandardId);
							--set @OutTime = DATEADD(SECOND,[dbo].[getFullCurrentDurationExtendAppendSec](@InTime,@OutTime,@CurrentChargeStandardId)*-1,@OutTime);
							set @InTime = @exchangeTime;
						end
					end
					--如果只有一个时段
					else begin
						set @SumCharge = @SumCharge + [dbo].[getFullCurrentDurationPrice](@InTime,@OutTime,@CurrentChargeStandardId,@AcrossdayChargeStyle,@IsAcrossDay);
						set @exchangeTime = [dbo].[getFullCurrentDurationTime](@InTime,@OutTime,@CurrentChargeStandardId);
						--set @OutTime = DATEADD(SECOND,[dbo].[getFullCurrentDurationExtendAppendSec](@InTime,@OutTime,@CurrentChargeStandardId)*-1,@OutTime);
						set @InTime = @exchangeTime;
					end
				end
			end
		end
		else begin
			SELECT @IsNextWorkDay = COUNT(1) FROM Holiday h WHERE DATEADD(DAY,1,@InTime) >= h.start and DATEADD(DAY,1,@InTime) < DATEADD(Day,h.length,h.start)
			select @CurrentChargeStandardId = id from carpark_charge_standard ccs where ccs.car_id = @PakCarTypeID and ccs.workday_type = 1 and carpark_id = @CarparkId and using = 'True'
			select @CurrentDurationChargeStandardSize = COUNT(1) from carpark_duration_standard cds where cds.standard_id = @CurrentChargeStandardId
			--如果第二天是工作日
			if @IsNextWorkDay < 1
			begin
				set @SumCharge = @SumCharge + [dbo].[getFullCurrentDurationPrice](@InTime,@OutTime,@CurrentChargeStandardId,@AcrossdayChargeStyle,@IsAcrossDay);
				set @exchangeTime = [dbo].[getFullCurrentDurationTime](@InTime,@OutTime,@CurrentChargeStandardId);
				--set @OutTime = DATEADD(SECOND,[dbo].[getFullCurrentDurationExtendAppendSec](@InTime,@OutTime,@CurrentChargeStandardId)*-1,@OutTime);
				set @InTime = @exchangeTime;
			end
			else begin
				if @ChargeStandardSize = 1
				begin 
					set @SumCharge = @SumCharge + [dbo].[getFullCurrentDurationPrice](@InTime,@OutTime,@CurrentChargeStandardId,@AcrossdayChargeStyle,@IsAcrossDay);
					set @exchangeTime = [dbo].[getFullCurrentDurationTime](@InTime,@OutTime,@CurrentChargeStandardId);
					--set @OutTime = DATEADD(SECOND,[dbo].[getFullCurrentDurationExtendAppendSec](@InTime,@OutTime,@CurrentChargeStandardId)*-1,@OutTime);
					set @InTime = @exchangeTime;
				end
				else begin
					if @CurrentDurationChargeStandardSize = 1
					begin
						set @SumCharge = @SumCharge + [dbo].[getFullCurrentDurationPrice](@InTime,@OutTime,@CurrentChargeStandardId,@AcrossdayChargeStyle,@IsAcrossDay);
						set @exchangeTime = [dbo].[getFullCurrentDurationTime](@InTime,@OutTime,@CurrentChargeStandardId);
						--set @OutTime = DATEADD(SECOND,[dbo].[getFullCurrentDurationExtendAppendSec](@InTime,@OutTime,@CurrentChargeStandardId)*-1,@OutTime);
						set @InTime = @exchangeTime;
					end
					else begin
						--如果当前所在的时段跨天，收到24点，重置起始时间
						if [dbo].[isCurrentDurationContain24](@InTime,@CurrentChargeStandardId) = 1
						begin
							set @SumCharge = @SumCharge + [dbo].[arrive24DurationPrice](@InTime,@OutTime,@CurrentChargeStandardId);
							set @exchangeTime = [dbo].[arrive24DurationTime](@InTime,@OutTime,@CurrentChargeStandardId);
							--set @OutTime = DATEADD(SECOND,[dbo].[arrive24DurationExtendApendSec](@InTime,@OutTime,@CurrentChargeStandardId)*-1,@OutTime);
							set @InTime = @exchangeTime;
						end
						--如果没有跨天，收满当前时段，重置起始时间
						else begin
							set @SumCharge = @SumCharge + [dbo].[getFullCurrentDurationPrice](@InTime,@OutTime,@CurrentChargeStandardId,@AcrossdayChargeStyle,@IsAcrossDay);
							set @exchangeTime = [dbo].[getFullCurrentDurationTime](@InTime,@OutTime,@CurrentChargeStandardId);
							--set @OutTime = DATEADD(SECOND,[dbo].[getFullCurrentDurationExtendAppendSec](@InTime,@OutTime,@CurrentChargeStandardId)*-1,@OutTime);
							set @InTime = @exchangeTime;
						end
					end
				end
			end 
		end
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
