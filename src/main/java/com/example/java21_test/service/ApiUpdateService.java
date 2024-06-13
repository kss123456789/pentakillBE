package com.example.java21_test.service;

import com.example.java21_test.entity.Schedule;
import com.example.java21_test.respository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j(topic = "api update service")
@Service
@RequiredArgsConstructor
public class ApiUpdateService {
    private final ScheduleService scheduleService;
    private final ScheduleRepository scheduleRepository;
    /*
    1. 최근토너먼트 업데이트에서 새로운 토너먼트일정이 업데이트 된걸 확인
    (토너먼트기준 가장 가까운 날짜의 경기일정을 확)
    2. 토너먼트 일정이 업데이트 된걸 eventListner로 감지하여 새로운 schedule(경기 시작시 지속적으로 경기상황을 업데이트 하는기능) 추가
    (경기의 가장 가까운 날짜에 실행되도록하는 schedule)
    2-1. event listener... 추가해야 할게 많아보인다 -> 대신 업데이트 된걸 if문으로 확인해서 schedule을 등록하도록 하자
    3. 새로 추가된 schedule 실행
    (시작 시간에 시작되어 지속적으로 경기상황 업데이트하다가 경기 종료를 확인하고 종료(10분 간격을 두고 무한루프하다가 조건이 되면 break 시키기))
    4. 조건이 맞아서 break 시키기 전에 다음 경기 시작시간에 맞게 새로운 schedule을 등록 (2.의 schedule과 동일)
    4-1. 새로운 schedule 등록전에 현재 끝난 경기에 대하여 배당금 분배작업을 수행하도록함
    5. 다음 경기가 없을경우 아무것도 하지않고 다시 새로운 토너먼트 일정이 추가되기를 기다림
    **예상되는 변수 1) 새로운 토너먼트가 시작되고 경기 일정이 올라왔지만 아직 전체 일정이 나오질 않아서 이후의 경기 일정이 없는 경우... 어라 생각해보니
    설마 경기가 진행되는 중에 업데이트 되겠지...긴한데 그러고 보니 다음경기 시작시간은 어떻게 가져온담... 음... 같은 경
    */
    // startTime에 맞춰 실행될 특정 작업 구현
    // (시작 시간에 시작되어 지속적으로 경기상황 업데이트하다가 경기 종료를 확인하고 종료(10분 간격을 두고 무한루프하다가 조건이 되면 break 시키기))
    // 가져와야 된다 api한테서 가져와야된다. 어떻게? getlive로 가져와서 startTime이 같은 값들로 업데이트
    // -> 문제점 경기 종료시 completed 된걸 받아 오지 못한다
    // getSchedule 하는데 리그 아이디라도 있으면 조회수가 확 줄겠는데...음 설마 live 경기는 getSchedule에 안들어오는건 아니겠지...?
    //조회, 업데이트 로직

    // 경기 일정
    public void createScheduleTaskAt(Instant startTime, Schedule schedule, String leagueId) {
        TaskScheduler scheduler = new SimpleAsyncTaskScheduler();
        scheduler.schedule(() -> roofScheduleTaskAt(schedule, leagueId), startTime);
        //임시로 현재 시간 기준 30초뒤로 지정 원래는 startTime
    }

     @Transactional
     public void roofScheduleTaskAt(Schedule schedule, String leagueId) {

         //이 사이에 조회 업데이트로직이 들어감 //단순하게 그냥 그 토너먼트를 전체 업데이트 시도하도록 수정...
         scheduleService.getLeagueSchedulesFromApi(leagueId, null);

         boolean isCompleted = schedule.getState().equals("completed");

         if (!isCompleted) {
             // 아직 경기가 끝나지 않아서 10분뒤 다시 자신이 작동하도록 호출함
             TaskScheduler scheduler = new SimpleAsyncTaskScheduler();

             Instant checkTime = Instant.now().plusSeconds(600); // 10분 뒤에 다시 실행
//             Instant testTime = Instant.now().plusSeconds(60);
             scheduler.schedule(() -> roofScheduleTaskAt(schedule, leagueId), checkTime);
         } else {
             // 다음경기의 시작시간에 맞게 createScheduleTaskAt 실행
             Instant instantNow = Instant.now();
             Schedule nextSchedule = scheduleRepository.findTop1ByLeagueSlugAndStartTimeAfterOrderByStartTimeAsc(schedule.getLeagueSlug(), instantNow.toString())
                     .orElse(null);
             if (nextSchedule != null) {
                 Instant nextStartTime = Instant.parse(nextSchedule.getStartTime());
                 createScheduleTaskAt(nextStartTime, nextSchedule, leagueId);
             }
         }
     }
}
