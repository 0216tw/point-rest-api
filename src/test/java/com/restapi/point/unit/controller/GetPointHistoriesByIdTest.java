package com.restapi.point.unit.controller;

import com.restapi.point.application.enums.Messages;
import com.restapi.point.domain.model.PointHistory;
import com.restapi.point.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/* 포인트 내역 조회 단위 테스트 */
/* 성공 케이스
   [1] 사용자 포인트 내역 조회를 반환한다.
   [2] 내역이 없는 경우 빈 리스트를 반환한다.
---------------------
   실패 케이스
   [1] x 사용자 관련 검증은 인터셉터 통합테스트에서 진행
 */
public class GetPointHistoriesByIdTest extends PointRestControllerBase {

    @Test
    @DisplayName("성공-사용자 포인트 이용내역 조회하기")
    public void 성공_사용자_포인트_이용내역을_조회한다() throws Exception {

        //given
        long userId = 1L;
        List<PointHistory> pointHistories = List.of(
                new PointHistory(1L , userId , 10000L , "charge") ,  // return 10000
                new PointHistory(2L , userId , 5000L , "use") ,      // return 5000
                new PointHistory(3L , userId , 3000L , "use" ),      // return 2000
                new PointHistory(4L , userId , 5000L , "charge")     // return 7000
        );
        User mockUser = new User(userId , 7000L);

        //when
        when(pointHistoriesSearchUseCase.getPointHistoriesById(anyLong())).thenReturn(pointHistories);

        ResultActions perform = mockMvc.perform(get("/point/{id}/histories", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(Messages.POINT_HISTORY_SEARCH_SUCCESS.toString()))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].userId").value(1))
                .andExpect(jsonPath("$.data[0].point").value(10000))
                .andExpect(jsonPath("$.data[0].type").value("charge"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].userId").value(1))
                .andExpect(jsonPath("$.data[1].point").value(5000))
                .andExpect(jsonPath("$.data[1].type").value("use"))
                .andDo(print())
                .andDo(document("get-user-point-histories", // 문서화 작업
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        pathParameters(
                                parameterWithName("id").description("포인트를 조회할 사용자의 ID")
                        ),

                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("조회된 사용자의 포인트 사용 내역").type(JsonFieldType.ARRAY) ,
                                fieldWithPath("data[].id").description("포인트 기록의 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].userId").description("사용자의 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].point").description("포인트 값").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].type").description("포인트의 사용 유형 (충전 또는 사용)").optional().type(JsonFieldType.STRING)

                        )
                ));

    }

    @Test
    @DisplayName("성공-사용자 포인트 이용내역 조회하기 빈리스트인 경우")
    public void 성공_사용자_포인트_이용내역을_조회한다_빈리스트인_경우 () throws Exception {

        //given
        long userId = 1L;
        List<PointHistory> pointHistories = List.of();

        //when
        when(pointHistoriesSearchUseCase.getPointHistoriesById(anyLong())).thenReturn(pointHistories);

        ResultActions perform = mockMvc.perform(get("/point/{id}/histories", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(Messages.POINT_HISTORY_SEARCH_SUCCESS.toString()))
                .andDo(print())
                .andDo(document("get-user-point-histories-empty", // 문서화 작업
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("포인트를 조회할 사용자의 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("조회된 사용자의 포인트 사용 내역").type(JsonFieldType.ARRAY) ,
                                fieldWithPath("data[].id").description("포인트 기록의 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].userId").description("사용자의 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].point").description("포인트 값").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].type").description("포인트의 사용 유형 (충전 또는 사용)").optional().type(JsonFieldType.STRING)

                        )
                ));

    }
}
