package cn.lomu.media.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;


/**
 * @author LoMu
 * Date  2023-05-19 6:07
 */

@Data
@Accessors(chain = true)
public class UploadFileParamsDTO {
    private String fileName;
    private String fileType;
    private String tags;
    private Long fileSize;
    private String remark;
    private String username;
    private boolean isVideo;
    private String currentObjectName;
}
