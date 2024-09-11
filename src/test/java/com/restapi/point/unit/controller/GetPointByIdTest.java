package com.restapi.point.unit.controller;

import com.restapi.point.application.enums.Messages;
import com.restapi.point.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* 포인트 조회 단위 테스트 */
/* 성공 케이스
   [1] 사용자 포인트 조회 성공 시 현재 잔액을 반환한다.
---------------------
   실패 케이스
   [1] x 사용자 관련 검증은 인터셉터 통합테스트에서 진행
 */
public class GetPointByIdTest extends PointRestControllerBase {

    @Test
    @DisplayName("성공-사용자 포인트 조회하기")
    public void 성공_사용자_포인트를_조회한다() throws Exception {

        //given
        long userId = 1L;
        User mockUser = new User(userId , 20000L);
        long expectedValue = 20000L;

        //when
        when(pointSearchUseCase.getPointById(anyLong())).thenReturn(expectedValue);

        ResultActions perform = mockMvc.perform(get("/point/{id}", userId) //RestDocs의 patch 사용
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(Messages.SEARCH_SUCCESS.toString()))
                .andExpect(jsonPath("$.data").value(mockUser.getPoint()))
                .andDo(print())
                .andDo(document("get-user-point", // 문서화 작업
                        pathParameters(
                                parameterWithName("id").description("포인트를 조회할 사용자의 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("조회된 사용자의 잔여 포인트").type(JsonFieldType.NUMBER)
                        )
                ));

    }

}
