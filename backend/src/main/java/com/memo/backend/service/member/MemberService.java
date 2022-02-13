package com.memo.backend.service.member;

import com.memo.backend.domain.member.MemberRepository;
import com.memo.backend.dto.member.MemberRespDTO;
import com.memo.backend.exceptionhandler.BizException;
import com.memo.backend.exceptionhandler.MemberExceptionType;
import com.memo.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * MemberService 설명 : 멤버 등록 및 조회
 * @author jowonjun
 * @version 1.0.0
 * 작성일 : 2022/01/02
**/
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberRespDTO getMemberInfo(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberRespDTO::of)
                .orElseThrow(()-> new BizException(MemberExceptionType.NOT_FOUND_USER)); // 유저를 찾을 수 없습니다.
    }

    /**
     *
     * @return 현재 securityContext에 있는 유저 정보를 반환한다.
     */
    @Transactional(readOnly = true)
    public MemberRespDTO getMyInfo() {
        return memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .map(MemberRespDTO::of)
                .orElseThrow(()->new BizException(MemberExceptionType.NOT_FOUND_USER));
    }

}
