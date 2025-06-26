package com.zhy.yuban.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.yuban.model.domain.Tag;
import com.zhy.yuban.mapper.TagMapper;
import com.zhy.yuban.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author DELL
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2025-05-03 18:51:59
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




