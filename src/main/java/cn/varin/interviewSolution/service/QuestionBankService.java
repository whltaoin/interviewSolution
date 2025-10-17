package cn.varin.interviewSolution.service;

import cn.varin.interviewSolution.model.entity.Question;
import cn.varin.interviewSolution.model.vo.QuestionWithBankVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.varin.interviewSolution.model.dto.questionBank.QuestionBankQueryRequest;
import cn.varin.interviewSolution.model.entity.QuestionBank;
import cn.varin.interviewSolution.model.vo.QuestionBankVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目服务
 *

 */
public interface QuestionBankService extends IService<QuestionBank> {

    /**
     * 校验数据
     *
     * @param questionBank
     * @param add 对创建的数据进行校验
     */
    void validQuestionBank(QuestionBank questionBank, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQueryRequest
     * @return
     */
    QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest);
    
    /**
     * 获取题目封装
     *
     * @param questionBank
     * @param request
     * @return
     */
    QuestionBankVO getQuestionBankVO(QuestionBank questionBank, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionBankPage
     * @param request
     * @return
     */
    Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionBankPage, HttpServletRequest request);

    List<QuestionWithBankVO> getQuestionList(QuestionBank questionBank);
}
