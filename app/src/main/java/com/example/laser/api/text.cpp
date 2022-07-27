//
// Created by 17600 on 2022/7/22.
//
//解析激光管数据
void Get_Bullet_Area(uint8_t data[GROUP][NUM])
{
	static uint8_t i,j,k;
	static uint16_t data_cnt = 0;
	uint16_t temp;
	static uint8_t zero_cnt,one_cnt;
	static uint16_t all_zero_cnt = 0;
	static uint8_t all_zero_flag = 0;
	data_cnt = 0;
	//初始化原始数据
	for(temp = 0;temp < (7*8*8);temp ++)
	{
		m_device.value[temp] = 0;
	}

	//解析所有激光管的数据，判断哪个管接收到数据
	for(i = 0;i < GROUP;i++)	//每一行，共8行，8个IO
	{
		for(j = 0;j < NUM;j ++)		//每个IO上的7个芯片，7个BYTE
		{
			for(k = 0;k < DATA_WIDTH;k ++)	//每个芯片8根引脚，接8个激光感应头
			{
				if(((data[i][j] >> k) & 0x01) == 0)
				{
					temp = (i*8*7 + j*8 + k + 1);
					if((temp < WRONG_DATA_VALUE)) //判定是否有效
					{
						m_device.value[data_cnt ++] = (i*8*7 + j*8 + k + 1);	//得到激光头编号
					}
				}
			}
		}
	}

	#if 1
	//将读取到激光管数据放到帧解析数组
	for(i= 0,j = 0;j < data_cnt&&i < 6;j ++)
	{
		{
			m_device.bullet_data[m_device.bullet_data_cnt][i] = m_device.value[j];
			i ++;
		}
	}
	//寻找停止位
	if((m_device.bullet_data_cnt == 0)&&(!all_zero_flag))
	{
		if(Is_ALL_Zero(m_device.value,6))
		{
			all_zero_cnt ++;
		}
		else
		{
			all_zero_cnt = 0;
		}
		if(all_zero_cnt >= (4*Q))///	找到4个停止位
		{
			all_zero_flag = 1;
		}
	}

	if(!all_zero_flag)
		return;
	m_device.bullet_data_cnt ++; //未找到停止位不自加
	if(m_device.bullet_data_cnt == 1)
	{
		if(Is_ALL_Zero(m_device.value,6))///
		{
			m_device.bullet_data_cnt = 0;
		}
	}
	//判断有效数据起始位
	else if(m_device.bullet_data_cnt == N)  //start
	{
		for(i = 0;i < 6;i ++)
		{
			if(m_device.bullet_data[0][i])
			{
				one_cnt = 1;
			}
			else
			{
				one_cnt = 0;
			}
			zero_cnt = 0;
			for(j = 1;j < N;j ++)
			{
				for(k = 0;k < 6;k ++)
				{
					temp_value[k] = m_device.bullet_data[j][k];
				}
				if(m_device.bullet_data[0][i])
				{
					Is_Data_In(m_device.bullet_data[0][i],temp_value,6)?one_cnt ++:zero_cnt ++;//判断当前数据列表是否包含第一组数据
				}
			}
			if(one_cnt >= Q) //找到数据i有效起始位
			{
				m_device.bullet_frame_start[i] = 1;
			}
		}
		if(Is_ALL_Zero((m_device.bullet_frame_start),6)) //未找到有效起始位
		{
			m_device.bullet_data_cnt = 0;
			all_zero_flag = 0;
		}
	}
	else if(m_device.bullet_data_cnt == (N*BIT_NUM)) //处理完整一帧数据
	{
		all_zero_flag = 0;
		m_device.bullet_data_cnt = 0;
		//处理一帧数据
		m_device.bullet_cnt = 0;
		for(i = 0;i < 6;i ++)
		{
			m_device.bullet_frame_data[i] = 0;
			if(m_device.bullet_frame_start[i])
			{
				for(j = 1;j < BIT_NUM;j ++)
				{
					zero_cnt = 0;
					one_cnt = 0;
					for(k = 0;k < N;k ++)
					{
						for(temp = 0;temp < 6;temp ++)
						{
							temp_value[temp] = m_device.bullet_data[j*N + k][temp];
						}
						Is_Data_In(m_device.bullet_data[0][i],temp_value,6)?one_cnt ++:zero_cnt ++; //判断当前数据列表是否包含第一组数据
					}
					if(one_cnt >= Q)//////////////////////////////////////////////////  当前bit为1
					{
						m_device.bullet_frame_data[i] |= (1 << (4-j));//// 存放解析到的bit位
					}
					else if(zero_cnt >= Q)
					{

					}
				}
			}
			{
//				if(((m_device.bullet_frame_data[i] >> 0) & 0xff) == m_device.target_id)//
				{
					m_device.clear_cnt = 0;
					m_device.no_data_cnt = 0;
					if(m_device.bullet_frame_start[i])
					{
						m_device.bullet_area[m_device.bullet_cnt] = m_device.bullet_data[0][i];
						m_device.bullet_cnt ++;
						if(m_device.bullet_cnt >=6)
						{
							m_device.bullet_cnt = 0;
						}
					}
				}
			}
		}
		//初始化数据
		for(i = 0;i < (N*BIT_NUM);i ++)
		{
			for(j = 0;j < 6;j++)
			{
				m_device.bullet_data[i][j] = 0;
			}
		}
		for(i = 0;i < 6;i ++)
		{
			m_device.bullet_frame_start[i] = 0;
			m_device.bullet_frame_data[i] = 0;
		}
	}

	#endif
}


