package com.memo.backend.service.member;

import com.memo.backend.domain.member.Member;
import com.memo.backend.domain.member.MemberRepository;
import com.memo.backend.dto.member.MemberRespDTO;
import com.memo.backend.dto.member.MemberSaveDTO;
import com.memo.backend.exceptionhandler.BizException;
import com.memo.backend.exceptionhandler.MemberExceptionType;
import com.memo.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final ModelMapper modelMapper;

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

    @Transactional
    public Long saveMember(MemberSaveDTO saveDTO){
        Optional<Member> find = memberRepository.findByEmail(saveDTO.getEmail());
        if(find.isPresent()) throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        Member member = modelMapper.map(saveDTO, Member.class);
        memberRepository.save(member);
        log.debug("### MemberService -> saveMember : " + member);
        return member.getId();
    }

    @Transactional(readOnly = true)
    public MemberRespDTO findById(Long id) throws IllegalArgumentException{
        Optional<Member> finds = memberRepository.findById(id);

        if(finds.isEmpty()) throw new IllegalArgumentException("아이디 [ " + id+" ] 에 해당하는 멤버가 없습니다.");

        return modelMapper.map(
                finds.get(),
                MemberRespDTO.class
        );
    }

    @Transactional(readOnly = true)
    public List<MemberRespDTO> findAll(){
        return memberRepository.findAll().stream().map(o->modelMapper.map(o,MemberRespDTO.class)).collect(Collectors.toList());
    }

}
