package com.microcosm.homer.service;

import com.microcosm.homer.model.Result;
import com.microcosm.homer.model.VideoBO;

import java.util.List;

/**
 * @author caojiancheng
 * @date 2022-05-02 20:02
 */
public abstract class VideoAbstract {
    /**
     * 搜索视频
     *
     * @param keyword 关键字
     * @return Result
     */
    public abstract Result<List<VideoBO>> search(String keyword);

    /**
     * 视频详情
     *
     * @param url 地址
     * @return Result
     */
    public abstract Result<List<VideoBO>> detail(String url);

    /**
     * 下载视频
     *
     * @param videoBO 视频参数
     * @return Result
     */
    public abstract Result<Void> down(VideoBO videoBO);
}
