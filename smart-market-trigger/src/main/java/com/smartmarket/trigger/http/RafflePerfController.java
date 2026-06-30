package com.smartmarket.trigger.http;

import com.smartmarket.domain.strategy.model.entity.RaffleAwardEntity;
import com.smartmarket.domain.strategy.model.entity.RaffleFactorEntity;
import com.smartmarket.domain.strategy.service.IRaffleStrategy;
import com.smartmarket.domain.strategy.service.armory.IStrategyDispatch;
import com.smartmarket.domain.strategy.service.raffle.DefaultRaffleStrategy;
import com.smartmarket.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.smartmarket.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.smartmarket.trigger.api.dto.RaffleStrategyRequestDTO;
import com.smartmarket.trigger.api.dto.RaffleStrategyResponseDTO;
import com.smartmarket.trigger.api.response.Response;
import com.smartmarket.types.enums.ResponseCode;
import com.smartmarket.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 压测专用接口：只覆盖抽奖算法、规则树、Redis 库存扣减，不包含活动账户、订单、中奖记录落库和限流。
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/perf/")
public class RafflePerfController {

    @Resource
    private IRaffleStrategy raffleStrategy;
    @Resource
    private DefaultRaffleStrategy defaultRaffleStrategy;
    @Resource
    private IStrategyDispatch strategyDispatch;

    @RequestMapping(value = "core_draw", method = RequestMethod.POST)
    public Response<RaffleStrategyResponseDTO> coreDraw(@RequestBody RaffleStrategyRequestDTO requestDTO) {
        try {
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId("perf_user_" + ThreadLocalRandom.current().nextInt(1, 1000000))
                    .strategyId(requestDTO.getStrategyId())
                    .build());

            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(RaffleStrategyResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
        } catch (AppException e) {
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("核心抽奖压测接口失败 strategyId:{}", requestDTO.getStrategyId(), e);
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "core_fast_draw", method = RequestMethod.POST)
    public Response<RaffleStrategyResponseDTO> coreFastDraw(@RequestBody RaffleStrategyRequestDTO requestDTO) {
        try {
            String userId = "perf_user_" + ThreadLocalRandom.current().nextInt(1, 1000000);
            Long strategyId = requestDTO.getStrategyId();
            DefaultChainFactory.StrategyAwardVO chainStrategyAwardVO = defaultRaffleStrategy.raffleLogicChain(userId, strategyId);

            Integer awardId = chainStrategyAwardVO.getAwardId();
            String awardRuleValue = chainStrategyAwardVO.getAwardRuleValue();
            if (DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(chainStrategyAwardVO.getLogicModel())) {
                DefaultTreeFactory.StrategyAwardVO treeStrategyAwardVO = defaultRaffleStrategy.raffleLogicTree(userId, strategyId, awardId, null);
                awardId = treeStrategyAwardVO.getAwardId();
                awardRuleValue = treeStrategyAwardVO.getAwardRuleValue();
            }

            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(RaffleStrategyResponseDTO.builder()
                            .awardId(awardId)
                            .awardIndex(null == awardRuleValue ? 0 : awardRuleValue.hashCode())
                            .build())
                    .build();
        } catch (AppException e) {
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("核心快速抽奖压测接口失败 strategyId:{}", requestDTO.getStrategyId(), e);
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "stock_decr", method = RequestMethod.POST)
    public Response<Boolean> stockDecr(@RequestParam Long strategyId, @RequestParam Integer awardId) {
        try {
            Boolean status = strategyDispatch.subtractionAwardStock(strategyId, awardId, null);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(status)
                    .build();
        } catch (Exception e) {
            log.error("Redis 库存扣减压测接口失败 strategyId:{} awardId:{}", strategyId, awardId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

}


