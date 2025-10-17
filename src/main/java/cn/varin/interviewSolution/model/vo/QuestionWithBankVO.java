package cn.varin.interviewSolution.model.vo;

import cn.hutool.json.JSONUtil;
import cn.varin.interviewSolution.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题库中的简要信息
 *

 */
@Data
public class QuestionWithBankVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;
    public QuestionWithBankVO() {}
    public QuestionWithBankVO(Long id ,String title) {
        this.id = id;
        this.title = title;
    }





}
