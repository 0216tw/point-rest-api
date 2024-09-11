package com.restapi.point.unit.controller;

/* 공통 로직 처리 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.point.application.usecase.PointChargeUseCase;
import com.restapi.point.application.usecase.PointHistoriesSearchUseCase;
import com.restapi.point.application.usecase.PointSearchUseCase;
import com.restapi.point.application.usecase.PointUseUseCase;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.presentation.controller.PointRestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;

@WebMvcTest(PointRestController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@ExtendWith(RestDocumentationExtension.class) //파라미터 주입 방식 사용을 위한 추가
public class PointRestControllerBase {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

//    @MockBean
//    protected PointService pointService;

    //유스케이스로 전환
    @MockBean
    PointChargeUseCase pointChargeUseCase;

    @MockBean
    PointUseUseCase pointUseUseCase;

    @MockBean
    PointSearchUseCase pointSearchUseCase;

    @MockBean
    PointHistoriesSearchUseCase pointHistoriesSearchUseCase;

    @Autowired
    protected WebApplicationContext context;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))

                .build();
    }

    //공통 오류 케이스 정리

}
