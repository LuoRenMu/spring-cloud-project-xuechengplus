package cn.lomu.base.model;

import lombok.*;

/**
 * @author LoMu
 * Date  2023-04-29 12:30
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {

    private long pageNo = 1L;
    private long pageSize = 10L;
}
