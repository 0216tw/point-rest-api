package com.restapi.point.presentation.controller;

import com.restapi.point.application.enums.Messages;
import com.restapi.point.application.usecase.PointChargeUseCase;
import com.restapi.point.application.usecase.PointHistoriesSearchUseCase;
import com.restapi.point.application.usecase.PointSearchUseCase;
import com.restapi.point.application.usecase.PointUseUseCase;
import com.restapi.point.domain.model.PointHistory;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.presentation.dto.RequestDTO;
import com.restapi.point.presentation.dto.ResponseDTO;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointRestController {

    @Autowired
    PointChargeUseCase pointChargeUseCase;
    @Autowired
    PointHistoriesSearchUseCase pointHistoriesSearchUseCase;
    @Autowired
    PointSearchUseCase pointSearchUseCase;
    @Autowired
    PointUseUseCase pointUseUseCase;

    //포인트 충전
    @PatchMapping("/{id}/charge") // -> 특정 id에 대한 포인트 충전 (멱등을 보장하지 않음)
    public ResponseEntity<ResponseDTO<Long>> charge(@PathVariable("id") long id ,
                                                      @RequestBody RequestDTO requestDTO) {
        long afterPoint = pointChargeUseCase.charge(id, requestDTO).getPoint();
        return ResponseEntity.ok(new ResponseDTO<>(200 , Messages.CHARGE_SUCCESS.toString() , afterPoint));
    }

    //포인트 사용
    @PatchMapping("/{id}/use")
    public ResponseEntity<ResponseDTO<Long>> use(@PathVariable("id") long id ,
                                                 @RequestBody RequestDTO requestDTO) {
        long afterPoint = pointUseUseCase.use(id , requestDTO).getPoint();
        return ResponseEntity.ok(new ResponseDTO<>(200 , Messages.USE_SUCCESS.toString() , afterPoint));
    }

    //포인트 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Long>> getPointById(@PathVariable("id") long id) {
        long userPoint = pointSearchUseCase.getPointById(id);
        return ResponseEntity.ok(new ResponseDTO<>(200 , Messages.SEARCH_SUCCESS.toString() , userPoint));
    }

    //포인트 내역 조회
    @GetMapping("/{id}/histories")
    public ResponseEntity<ResponseDTO<List<PointHistory>>> getPointHistoriesById(@PathVariable("id") long id) {
        List<PointHistory> pointHistories = pointHistoriesSearchUseCase.getPointHistoriesById(id);
        return ResponseEntity.ok(new ResponseDTO<>(200 , Messages.POINT_HISTORY_SEARCH_SUCCESS.toString() , pointHistories));
    }
}
