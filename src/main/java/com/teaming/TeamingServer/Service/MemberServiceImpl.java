package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // 밑에 MemberRepository 의 생성자를 쓰지 않기 위해
public class MemberServiceImpl implements MemberService {

    @Autowired
    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional
    @Override
    public Integer join(Member member) {

        //중복 회원 검증
        if(!validateDuplicateMember(member.getEmail())) {
            throw new IllegalArgumentException("이미 회원가입된 이메일입니다.");
        };

        // 비밀번호 일치 검증
        // checkPasswordMatch(member, checkPassword);

        // 이메일 인증

        // 회원 DB 에 저장
        memberRepository.save(member);

        return member.getMember_id();
    }

    @Override
    public boolean validateDuplicateMember(String email) {
        List<Member> findMembers = memberRepository.findByEmail(email);
        return findMembers.isEmpty();
    }

//    private void checkPasswordMatch(Member member, String checkPassword) {
//        if(member.getPassword() != checkPassword) {
//            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
//        }
//    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Integer id) {
        return memberRepository.findById(id).get();
    }

    /**
     * 회원 수정
     */
    @Transactional
    public void update(int id, String profile_image) {
        Member member = (memberRepository.findById(id)).get();
        member.update(profile_image);
    }
}
