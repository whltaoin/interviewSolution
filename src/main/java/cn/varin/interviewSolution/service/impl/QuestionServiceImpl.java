package cn.varin.interviewSolution.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.varin.interviewSolution.mapper.QuestionBankQuestionMapper;
import cn.varin.interviewSolution.model.entity.QuestionBankQuestion;
import cn.varin.interviewSolution.service.QuestionBankQuestionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.varin.interviewSolution.common.ErrorCode;
import cn.varin.interviewSolution.constant.CommonConstant;
import cn.varin.interviewSolution.exception.ThrowUtils;
import cn.varin.interviewSolution.mapper.QuestionMapper;
import cn.varin.interviewSolution.model.dto.question.QuestionQueryRequest;
import cn.varin.interviewSolution.model.entity.Question;
import cn.varin.interviewSolution.model.entity.User;
import cn.varin.interviewSolution.model.vo.QuestionVO;
import cn.varin.interviewSolution.model.vo.UserVO;
import cn.varin.interviewSolution.service.QuestionService;
import cn.varin.interviewSolution.service.UserService;
import cn.varin.interviewSolution.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题目服务实现
 *

 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param question
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = question.getTitle();
        ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);



        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }

    }

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        // todo 补充需要的查询条件

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        // 对象转封装类
        QuestionVO questionVO = QuestionVO.objToVo(question);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long questionId = question.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {

        }
        // endregion

        return questionVO;
    }

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            return QuestionVO.objToVo(question);
        }).collect(Collectors.toList());


        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    /**
     * 同时保存问题和题库和问题的关系
     * @param question
     * @param questionBankId
     * @return
     */
    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Override
    public Long saveQuestionAndQuestionBankQuestion(Question question, Long questionBankId) {
        boolean result = this.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        Long newQuestionId = question.getId();
        ThrowUtils.throwIf( newQuestionId==null || newQuestionId <= 0 , ErrorCode.OPERATION_ERROR);
        // 保存题和题库之间的关系
        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
        questionBankQuestion.setQuestionBankId(questionBankId);
        questionBankQuestion.setQuestionId(newQuestionId);
        questionBankQuestion.setUserId(question.getId());
        questionBankQuestionService.validQuestionBankQuestion(questionBankQuestion,true);
        boolean questionBankQuestionStatus = questionBankQuestionService.save(questionBankQuestion);
        ThrowUtils.throwIf(!questionBankQuestionStatus, ErrorCode.OPERATION_ERROR);
        return newQuestionId;
    }

}
