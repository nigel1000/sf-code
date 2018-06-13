package cn.sf.car.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

//车辆
@Data
@NoArgsConstructor
public class BusDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;//
    private Long lineRouteId;//线路id，决定线路号和子线路
    private String busNo;//车号  车身右下角的编号
    private String busSystemNo;//公交系统号
    private String licenceNo;//车牌号
    private Date createdAt;//创建时间
    private Date updatedAt;//更新时间

}