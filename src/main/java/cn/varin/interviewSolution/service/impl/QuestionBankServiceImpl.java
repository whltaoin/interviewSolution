package cn.varin.interviewSolution.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.varin.interviewSolution.mapper.QuestionBankQuestionMapper;
import cn.varin.interviewSolution.mapper.QuestionMapper;
import cn.varin.interviewSolution.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import cn.varin.interviewSolution.model.entity.Question;
import cn.varin.interviewSolution.model.entity.QuestionBankQuestion;
import cn.varin.interviewSolution.model.vo.QuestionWithBankVO;
import cn.varin.interviewSolution.service.QuestionBankQuestionService;
import cn.varin.interviewSolution.service.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.varin.interviewSolution.common.ErrorCode;
import cn.varin.interviewSolution.constant.CommonConstant;
import cn.varin.interviewSolution.exception.ThrowUtils;
import cn.varin.interviewSolution.mapper.QuestionBankMapper;
import cn.varin.interviewSolution.model.dto.questionBank.QuestionBankQueryRequest;
import cn.varin.interviewSolution.model.entity.QuestionBank;
import cn.varin.interviewSolution.model.entity.User;
import cn.varin.interviewSolution.model.vo.QuestionBankVO;
import cn.varin.interviewSolution.model.vo.UserVO;
import cn.varin.interviewSolution.service.QuestionBankService;
import cn.varin.interviewSolution.service.UserService;
import cn.varin.interviewSolution.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 题目服务实现
 *

 */
@Service
@Slf4j
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param questionBank
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestionBank(QuestionBank questionBank, boolean add) {
        ThrowUtils.throwIf(questionBank == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = questionBank.getTitle();
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
     * @param questionBankQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest) {
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
        if (questionBankQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQueryRequest.getId();
        String title = questionBankQueryRequest.getTitle();
        String sortField = questionBankQueryRequest.getSortField();
        String sortOrder = questionBankQueryRequest.getSortOrder();

        String description = questionBankQueryRequest.getDescription();
        String picture = questionBankQueryRequest.getPicture();
        // todo 补充需要的查询条件
        // 从多字段中搜索

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目封装
     *
     * @param questionBank
     * @param request
     * @return
     */
    @Override
    public QuestionBankVO getQuestionBankVO(QuestionBank questionBank, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankVO questionBankVO = QuestionBankVO.objToVo(questionBank);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBank.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态

        // endregion

        return questionBankVO;
    }

    /**
     * 分页获取题目封装
     *
     * @param questionBankPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionBankPage, HttpServletRequest request) {
        List<QuestionBank> questionBankList = questionBankPage.getRecords();
        Page<QuestionBankVO> questionBankVOPage = new Page<>(questionBankPage.getCurrent(), questionBankPage.getSize(), questionBankPage.getTotal());
        if (CollUtil.isEmpty(questionBankList)) {
            return questionBankVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankVO> questionBankVOList = questionBankList.stream().map(questionBank -> {
            return QuestionBankVO.objToVo(questionBank);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankList.stream().map(QuestionBank::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> questionBankIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> questionBankIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);

        // 填充信息
        questionBankVOList.forEach(questionBankVO -> {
            Long userId = questionBankVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionBankVOPage.setRecords(questionBankVOList);
        return questionBankVOPage;
    }

    @Resource
    private QuestionBankQuestionMapper questionBankQuestionMapper;

    @Resource
    private QuestionMapper questionMapper;

    @Override

    public List<QuestionWithBankVO> getQuestionList(QuestionBank questionBank){
        // 通过查询关系表，拿到所有类型
        List<QuestionWithBankVO>  questionWithBankVOList = new ArrayList<>();


        LambdaQueryWrapper<QuestionBankQuestion> eq = Wrappers.lambdaQuery(QuestionBankQuestion.class);
        eq.select(QuestionBankQuestion::getQuestionId)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBank.getId());

        List<QuestionBankQuestion> questionBankQuestions = questionBankQuestionMapper.selectList(eq);
        questionBankQuestions.forEach(e -> {
            Question question = questionMapper.selectById(e.getQuestionId());
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
            questionWithBankVOList.add( new QuestionWithBankVO(question.getId(),question.getTitle()));
        });



        return questionWithBankVOList;
    }

}
