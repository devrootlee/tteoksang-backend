# 떡상🚀🚀(주식 예측 서비스) - 백엔드

<h3><b>📌 프로젝트 시작 배경</b></h3>

요즘 주식에 빠져있는 나
주식 관련해서 뭔가 재미있는 프로젝트를 해보고싶어서 고민하다가 어느정도 생각이 정리되서 진행하게 되었다.
우선은 해당프로젝트를 웹으로 만들어보고 반응이 좋으면 앱으로까지 만들어보려고 한다.

<h3>📌 화면 구성</h3>
- 메인 화면
  - [정중앙] 주식 예측 (떡상? 떡락?)
    - 종목코드 입력창(종목코드)
      - 사용자 입력 창(입력할 때 마다 선택 리스트 나옴)
  - [왼쪽] 주식 물타기 (물타보자)
    - 보유 단가 입력창
    - 보유 수량 입력창
    - 물타기 단가 입력창
    - 물타기 수량 입력창
  - [오른쪽] 가장 많이 예측한 주식 ()
    - 순위 정보(1 ~ 10)

<h3>📌 ETC</h3>
- 폰트
  - [무적해병체](https://www.rokmc.mil.kr:10005/contents/view.do?sMenuKey=304&contentKey=161)

<h3>📌 기능</h3>
- 주식 예측
- 주식 종목 정보 검색(한국주식은 한글 or 종목코드, 미국주식은 영문 or 종목코드)
- 예측 많이 한 주식 Top10 순위

<h3>📝 주식 가격 예측 공식</h3>
⚙️ 사용 알고리즘
- SMA (Simple Moving Average): 최근 20일간의 단순 평균.

- EMA (Exponential Moving Average): 최근 가격에 더 가중치를 둔 평균.

- Linear Regression: 가격의 시간 흐름에 따른 기울기를 기반으로 한 예측.

종합 예측: 예측가 = 0.3 * SMA + 0.4 * EMA + 0.3 * 선형회귀
예측가(predictedPrice)와 현재가를 비교해 트렌드(상승/하락/보합 예상)를 판단

<h3>💾 DB 설계 및 구축</h3>
데이터베이스는 MySQL이나 PostgreSQL 중 고민하였으나 아래와 같은 이유로 PostgreSQL을 사용하기로 결정하였다.
- JSON, 고급 SQL 기능, 트랜잭션 안정성 등에서 MySQL보다 강점이 많음

- 유저 정보 테이블(members)
```
# members
```

- 주식 정보 테이블(stocks)

주식 정보 테이블
```
# stock
CREATE TABLE stock (
    stock_id VARCHAR(255) PRIMARY KEY,
    nation_type VARCHAR(255) NOT NULL,
    market VARCHAR(255) NOT NULL,
    stock_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL
);

-- 인덱스
CREATE INDEX idx_market ON stock(market);
CREATE INDEX idx_stock_name ON stock(stock_name);
```

- 데이터 수집 소요 시간 테이블(data_ingest_log)

데이터가 수집될때 얼마나 걸리는지 확인 하기위한 테이블
```
CREATE TABLE data_ingest_log (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    run_date DATE NOT NULL,
    script_name VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    duration FLOAT NOT NULL,
    status VARCHAR(255) NOT NULL
);
```
- 예측한 주식 내역(predicted_stock_history) 테이블

예측 결과가 나온 주식의 내역을 저장하는 테이블
```
CREATE TABLE predicted_stock_history (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    date_key int NOT NULL,
    stock_id VARCHAR(255),
    ip_address VARCHAR(45), -- IPv6까지 대비하려면 45
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

- 한국투자 Open API 접근 토큰 정보 테이블

한투 API명세서에 아래와 같이 기재되어있다. 토큰과 유효기간을 DB에 저장하여 여러번 API 호출을 막기위한 용도로 사용하는 테이블 
1. 접근토큰(access_token)의 유효기간은 24시간 이며(1일 1회발급 원칙)
   갱신발급주기는 6시간 입니다.(6시간 이내는 기존 발급키로 응답)
2. 접근토큰발급(/oauth2/tokenP) 시 접근토큰값(access_token)과 함께 수신되는
   접근토큰 유효기간(acess_token_token_expired)을 이용해 접근토큰을 관리하실 수 있습니다.
```
CREATE TABLE kis_token (
    id INT PRIMARY KEY,
    access_token text NOT NULL,
    expired_date timestamp NOT NULL,
    created_at timestamp NOT NULL
);
```

<h3>💻 개발</h3>

<h3>📝 국내/해외 주식 전체 정보 수집하기</h3>

한국/미국 주식 전체 정보를 조회하는 방법을 찾아보고 있는데 제공해주는 곳이 국내는 공공데이터포털에서 찾을 수 있었는데 미국주식은 제공해주는곳이 없었다.<br>
프로젝트를 진행하려면 이게 제일 중요한데 어떡하지... 하면서 계속 검색을 해봤는데 파이썬에 FinanceDataReader 이라는 라이브러리가 있었다. <br>
해당 라이브러리를 통해 파이썬으로 데이터를 수집한다.<br>

https://github.com/devrootlee/tteoksang-crawler

<h3>📝 주식 가격 정보 API</h3>

주식 가격 정보를 얻어오기 위해 검색중 한국투자증권이 잘되있는 것 같아서 한국투자증권 Open API를 신청하였다.
- 한국투자증권 Open API 신청
    - [한국투자증권 Open API 홈페이지](https://apiportal.koreainvestment.com/intro)
- 사용할 API
  - [공용]
    - 접근 토큰 발급
      - API: [POST] https://openapi.koreainvestment.com:9443/oauth2/tokenP
      - CheckPoint: 하루에 여러번 조회시 API호출금지 될 수 있어서, 응답 데이터를 DB에 저장하여 유효기간 만료시에 재호출
  - [한국주식]
    - 주식현재가 기간별 시세
      - API: [GET] https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice
      - CheckPoint
        - 조회시작일과 조회종료일을 지정할 있는데 100일의 데이터를 가져오려고 하였는데 휴장일이 겹쳐있어서 그런지 데이터 조회가 잘되지않았다, 그래서 200일전을 조회시작일로 잡고 90일의 데이터만 가져온다.
  - [미국주식]
    - 해외주식 기간별 시세
      - API: [GET] https://openapi.koreainvestment.com:9443/uapi/overseas-price/v1/quotations/dailyprice
      - CheckPoint
        - 100일에서 90일의 데이터만 가져와 사용한다.
        - 주식 전체 종목을 가져오기 위해 사용한 파이썬(FinanceReader)의 종목코드 값과 KIS 에서 사용하는 해외주식 종목코드 값이 달랐다.(ex: FinanceReader value: BRK.B KIS API Value: BRK/B) -> . 이 붙어있는 종목들을 보정하려하였으나 나중에 이런 데이터가 더 생길수도 있어서 문제없게 그냥 삭제처리해준다.


<h3>📝 오류 모니터링</h3>
슬랙 웹훅을 사용하여 오류가 발생할 때 슬랙으로 메시지가 가도록 하였다.


<h3>📝 CI/CD</h3>
### 📌 인프라 구성

- **서버**: Microsoft Azure 가상 머신 (Ubuntu 24.04)
- **프론트엔드**: GitHub Pages (정적 배포)
- **백엔드/데이터 수집**: Docker Compose를 통해 Azure VM에서 통합 운영
- **데이터베이스**: Azure Database for PostgreSQL (유연한 서버)

### 📁 프로젝트 구성

이 프로젝트는 **3개의 독립된 레포지토리 또는 구성 요소**로 이루어져 있습니다:

- **[`tteoksang-front`](https://github.com/devrootlee/tteoksang-front)** (React)
  → GitHub Pages로 **정적 배포**  
  → 사용자 인터페이스를 담당

- **[`tteoksang-backend`](https://github.com/devrootlee/tteoksang-backend)** (Spring Boot)
  → Docker 이미지화 후 `docker-compose`로 통합 관리  
  → **API 서버**, 클라이언트 요청 처리 및 **주식 예측 기능** 담당

- **[`tteoksang-crawler`](https://github.com/devrootlee/tteoksang-crawler)** (Python)
  → Docker 이미지화 후 `docker-compose`로 통합 관리  
  → **데이터 수집 서버**, 주식 전체 정보를 수집

### 서버 패키지 구성
- app
  - tteoksang-backend.tar(springboot, docker image)
  - tteoksang-crawler.tar(python, docker image)
  - docker-compose.yml

### 서버 작업 스크립트
```
# db 설치
1. sudo apt update
2. sudo apt install postgresql postgresql
3. sudo systemctl status postgresql

# 1. 기존 패키지 업데이트
sudo apt update

# 2. 필수 패키지 설치
sudo apt install -y ca-certificates curl gnupg lsb-release

# 3. Docker 공식 GPG 키 추가
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# 4. Docker 저장소 추가
echo \
  "deb [arch=$(dpkg --print-architecture) \
  signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 5. 패키지 업데이트 & Docker 설치
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 6. Docker 실행 확인
sudo docker run hello-world

# 7. Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 8. Docker Compose 버전 확인
docker-compose --version

# 9 nginx 설치
sudo apt install nginx

# 10. certbot 설치 및 HTTPS 인증서 발급
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d [DOMAIN]

# 11. nginx 설정 변경
```