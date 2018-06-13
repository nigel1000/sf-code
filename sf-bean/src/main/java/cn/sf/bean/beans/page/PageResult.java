package cn.sf.bean.beans.page;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by nijianfeng on 17/6/10.
 */
@Data
public class PageResult<T> implements Serializable {

    private Long total;
    private List<T> data;

    private PageResult(@NonNull Long total,@NonNull List<T> data) {
        this.data = data;
        this.total = total;
    }

    public Boolean isEmpty() {
        return Objects.equals(0L, this.total) || this.data == null || this.data.isEmpty();
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L, Collections.emptyList());
    }

    public static <T> PageResult<T> gen(Long total, List<T> data) {
        return new PageResult<>(total, data);
    }
}