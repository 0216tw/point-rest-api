package com.restapi.point.unit.controller;

import com.restapi.point.application.enums.Messages;
import com.restapi.point.application.exception.BusinessException;
import com.restapi.point.domain.model.User;
import com.restapi.point.presentation.dto.RequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* 포인트 사용 단위 테스트 */
/* 성공 케이스
   [1] 사용자 포인트 사용 성공시 사용 후 잔액을 반환한다.
---------------------
   실패 케이스
   [1] 사용할 금액이 0원 이하인 경우
   [2] 사용 금액 필드가 누락된 경우
   [3] 보유 금액보다 사용 금액이 클 경우
 */
public class UseTest extends PointRestControllerBase {

    @Test
    @DisplayName("성공-사용자 포인트 사용하기")
    public void 성공_사용자_포인트를_사용한다() throws Exception {

        //given
        long userId = 1L;
        User mockUser = new User(userId , 20000);
        RequestDTO requestDTO = new RequestDTO(10000); //사용하려는 금액

        //when
        when(pointUseUseCase.use(anyLong() , any(RequestDTO.class))).thenAnswer(invocation -> {
            long beforePoint = mockUser.getPoint();
            mockUser.setPoint(beforePoint - requestDTO.getPoint());
            return mockUser;
        });

       String requestBody = "{ \"point\": 10000 }";  // JSON 형식의 요청 본문

        ResultActions perform = mockMvc.perform(patch("/point/{id}/use", userId) //RestDocs의 patch 사용
                .content(requestBody)  // RequestDto를 JSON으로 보냄
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(Messages.USE_SUCCESS.toString()))
                .andExpect(jsonPath("$.data").value(mockUser.getPoint()))
                .andDo(print())
                .andDo(document("use-user-point", // 문서화 작업
                        pathParameters(
                                parameterWithName("id").description("포인트를 사용할 사용자의 ID")
                        ),
                        requestFields(
                                fieldWithPath("point").description("사용할 포인트 양")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 메시지").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("사용 후 사용자의 잔여 포인트").type(JsonFieldType.NUMBER)
                        )
                ));

    }


    @Test
    @DisplayName("실패-사용할 금액이 0원 이하인 경우")
    public void 실패_사용할_금액이_0원_이하() throws Exception {

        //given
        long userId = 1L;
        String requestBody = "{ \"point\": 0 }";  // JSON 형식의 요청 본문

        //when
        when(pointUseUseCase.use(anyLong() , any(RequestDTO.class))).thenThrow(new BusinessException(400 , Messages.MUST_UPPER_ONE_POINT_USE));

        ResultActions perform = mockMvc.perform(patch("/point/{id}/use", userId)
                .content(requestBody)  // RequestDto를 JSON으로 보냄
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.MUST_UPPER_ONE_POINT_USE.toString()))
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print())
                .andDo(document("use-user-point-failure", // 문서화 작업

                        responseFields(
                                fieldWithPath("message").description("에러 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("code").description("에러 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("data").description("응답 데이터 (오류 시 null)").optional().type(JsonFieldType.OBJECT)  // optional로 설정
                        )
                ));

    }


    @Test
    @DisplayName("실패-사용 금액 필드가 누락된 경우")
    public void 실패_사용_금액_필드가_누락됨() throws Exception {

        //given
        long userId = 1L;
        String requestBody = "{}";  // JSON 형식의 요청 본문

        //when
        when(pointUseUseCase.use(anyLong() , any(RequestDTO.class))).thenThrow(new BusinessException(400, Messages.BAD_REQUEST));

        ResultActions perform = mockMvc.perform(patch("/point/{id}/use", userId)
                .content(requestBody)  // RequestDto를 JSON으로 보냄
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print())
                .andDo(document("use-user-point-failure-no-point", // 문서화 작업

                        responseFields(
                                fieldWithPath("message").description("에러 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("code").description("에러 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("data").description("응답 데이터 (오류 시 null)").optional().type(JsonFieldType.OBJECT)  // optional로 설정
                        )
                ));

    }

    @Test
    @DisplayName("실패-보유 금액보다 사용 금액이 클 경우")
    public void 실패_잔액이_부족함() throws Exception {

        //given
        long userId = 1L;
        User mockUser = new User(userId , 10000);
        RequestDTO requestDTO = new RequestDTO(20000); //사용하려는 금액

        //when
        when(pointUseUseCase.use(anyLong() , any(RequestDTO.class))).thenThrow(new BusinessException(402, Messages.LACK_POINT));

        String requestBody = "{ \"point\": 20000 }";  // JSON 형식의 요청 본문

        ResultActions perform = mockMvc.perform(patch("/point/{id}/use", userId)
                .content(requestBody)  // RequestDto를 JSON으로 보냄
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.message").value(Messages.LACK_POINT.toString()))
                .andExpect(jsonPath("$.code").value(402))
                .andDo(print())
                .andDo(document("use-user-point-failure-lack-point", // 문서화 작업

                        responseFields(
                                fieldWithPath("message").description("에러 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("code").description("에러 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("data").description("응답 데이터 (오류 시 null)").optional().type(JsonFieldType.OBJECT)  // optional로 설정
                        )
                ));

    }
}
