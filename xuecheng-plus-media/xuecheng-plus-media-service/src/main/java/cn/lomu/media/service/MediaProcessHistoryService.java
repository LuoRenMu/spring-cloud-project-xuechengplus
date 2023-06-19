package cn.lomu.media.service;

/**
 * @author LoMu
 * Date  2023-05-29 20:46
 */
public interface MediaProcessHistoryService {
    void saveProcessFinishStatus(Long id, String status, String url, String errormsg);
}
