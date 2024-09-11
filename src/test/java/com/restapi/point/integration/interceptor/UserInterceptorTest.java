package com.restapi.point.integration.interceptor;

import com.restapi.point.application.enums.Messages;
import com.restapi.point.domain.model.User;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.domain.service.UserService;
import com.restapi.point.presentation.dto.RequestDTO;
import com.restapi.point.unit.controller.PointRestControllerBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/*
* 사용자 인터셉터 통합테스트 검증
* 성공 케이스
* [1] 사용자ID 검증 완료 후 정상 controller 실행
* 실패 케이스
* [1] 사용자ID 가 URL경로에서 누락된 경우
* [2] 사용자ID 가 long 범위를 넘어서는 경우
* [3] 사용자ID 가 숫자가 아닌 값이 들어오는 경우
*
* */
@SpringBootTest
@AutoConfigureMockMvc //mockMvc 의존성 주입을 위해 설정
public class UserInterceptorTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected PointService pointService;

    @Test
    @DisplayName("성공-인터셉터에서 사용자ID 검증 성공")
    public void 성공_인터셉터가_사용자ID_잘검증_했습니다() throws Exception {

        //given
        long userId = 12345 ;
        User mockUser = new User(userId , 50000);

        //when

        when(pointService.getPointById(userId)).thenReturn(mockUser.getPoint());

        ResultActions perform = mockMvc.perform(get("/point/{id}", userId) //RestDocs의 patch 사용
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(Messages.SEARCH_SUCCESS.toString()))
                .andExpect(jsonPath("$.data").value(mockUser.getPoint()))
                .andDo(print());
    }

    @Test
    @DisplayName("실패-사용자ID가 누락된 경우")
    public void 실패_인터셉터에서_사용자ID_누락을_캐치함() throws Exception {

        //given
        long userId = 12345 ;
        User mockUser = new User(userId , 50000);

        //when

        when(pointService.getPointById(userId)).thenReturn(mockUser.getPoint());

        ResultActions perform = mockMvc.perform(get("/point", userId) //RestDocs의 patch 사용
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.BAD_REQUEST.toString()))
                .andDo(print());
    }


    @Test
    @DisplayName("실패-사용자ID가 숫자가 아닌 경우")
    public void 실패_인터셉터에서_사용자ID가_문자임을_캐치함() throws Exception {

        //given
        String userId = "가나다라마12345" ;

        //when

        when(pointService.getPointById(anyLong())).thenReturn(50000L);

        ResultActions perform = mockMvc.perform(get("/point/{id}", userId) //RestDocs의 patch 사용
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.BAD_REQUEST.toString()))
                .andDo(print());


    }

    @Test
    @DisplayName("실패-사용자ID가 long범위를 넘어선 경우")
    public void 실패_인터셉터에서_사용자ID가_long범위_넘는걸_캐치함() throws Exception {

        //given
        String userId ="1123123243621435352435454235";

        //when

        when(pointService.getPointById(anyLong())).thenReturn(50000L);

        ResultActions perform = mockMvc.perform(get("/point/{id}", userId) //RestDocs의 patch 사용
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.BAD_REQUEST.toString()))
                .andDo(print());


    }

}
