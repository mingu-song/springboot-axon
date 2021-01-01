### 참고 사이트
* https://axoniq.io/download
    * Axon 서버 다운로드
    * AxonServer 폴더에서 직접 실행하거나 kubernetes 로 실행
        * 직접 : java -jar axonserver-4.4.6.jar
        * k8s : kubectl apply -f ./kubernetes/axonserver.yaml
* https://docs.axoniq.io/reference-guide/
    * 공식 사이트 
* https://cla9.tistory.com/2?category=814447
    * Axon 샘플

### 이벤트 흐름
* 기본 어노테이션 
    * @CommandHandler : Aggregate 에 대한 명령이 발생되었을 때 호출되는 메소드임을 알려주는 마커 역할
    * @EventSourcingHandler : CommandHandler 에서 발생한 이벤트를 적용하는 메소드임을 알려주는 마커 역할
    * @EventHandler : Query 모델 혹은 이벤트 발생시 해당 이벤트를 적용하는 메소드임을 알려주는 마커 역할
* common
    * 커맨드와 쿼리에서 사용하는 공통 이벤트 객체 등록
* command (Aggregate)
    * Command 객체 @TargetAggregateIdentifier 지정
    * 비지니스 서비스 -> CommandGateway 를 통해 send(Command 객체)
    * Axon 서버를 통해 @CommandHandler 로 등록된 것중 Command 객체와 동일한건 호출
    * Aggregate 생성 (@AggregateIdentifier)
        * CommandHandler 처리에서 비니지스 validation 후 apply(Event 객체)
        * apply 된 이벤트는 @EventSourcingHandler 최신화 + @EventHandler 호출
    * @EventSourcingHandler 에 의해 도메인 최신화
* query (Projection)
    * @EventHandler 를 등록하여 도메인 변화 감지하여 DB로 관리
    * DB의 정보가 삭제되더라도, EventProcessingConfiguration 설정을 통해 이벤트 정보를 복구 가능
    * ---- 위는 command event 에 대한 동기처리 ----
    * 비지니스 서비스 -> QueryGateway 를 통해 query 를 호출 -> @QueryHandler 을 찾아 실행
    
    
    
        