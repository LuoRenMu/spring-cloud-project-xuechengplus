package cn.lomu.media.model.dto;

import cn.lomu.media.model.po.MediaFiles;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author LoMu
 * Date  2023-05-19 2:30
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class UploadFileResultDTO extends MediaFiles {
}
