package com.example.java21_test.util;

import com.example.java21_test.entity.Point;
import com.example.java21_test.entity.PointLog;
import com.example.java21_test.respository.PointLogRepository;
import com.example.java21_test.respository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointUtil {

    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;

    @Transactional
    public void winPoint(PointLog pointLog, float odds) {
        Point point = pointLog.getPoint();
        int winPoint = (int) (pointLog.getAmount()*odds);
        point.update(winPoint);
        pointLog.update(winPoint, pointLog.getStatus() + " win");
    }

    @Transactional
    public void lossPoint(PointLog pointLog) {
        pointLog.update(pointLog.getAmount(), pointLog.getStatus() + " loss");

    }

    @Transactional
    public void getPoint(PointLog pointLog) {
        Point point = pointLog.getPoint();
        point.update(pointLog.getAmount());
    }


}
