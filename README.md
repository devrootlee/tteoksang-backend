# 떡상(주식 예측 서비스) 🚀🚀

<h3><b>📌 프로젝트 시작 배경</b></h3>

요즘 주식에 빠져있는 나
주식 관련해서 뭔가 재미있는 프로젝트를 해보고싶어서 고민하다가 어느정도 생각이 정리되서 진행하게 되었다.
우선은 해당프로젝트를 웹으로 만들어보고 반응이 좋으면 앱으로까지 만들어보려고 한다.

<h3>📌 화면 구성</h3>
- 메인 화면
  - [정중앙] 주식 예측 (떡상? 떡락?)
    - 한국/미국 선택
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
- 주식 종목 정보 업데이트
- 각 시장 Top10 순위
- 예측 많이 한 주식 Top10 순위
- 물타기 계산 기능
- 주식 예측

<h3>📝 주식 가격 예측 공식</h3>

주식가격을 예측할때 어떤 공식을 사용할 지 고민해봤다.
요청받은 날짜 기준으로 30거래일의 종가를 이용하려 했는데, 아래 3가지 방법을 찾아내었다.
아래 3가지중 가장 간단한 1번 SMA 방법을 사용하기로 결정했다.

1. 단순 이동평균 (Simple Moving Average, SMA)
   - 최근 30거래일 종가를 모두 더한 뒤 30으로 나누면 평균값(SMA)
   - 만약 SMA가 최근 종가보다 높다면 상승 추세, 낮다면 하락 추세로 해석

2. 지수 이동평균 (Exponential Moving Average, EMA)
   - EMA는 최근 데이터에 더 큰 가중치를 둠
   - 30일 EMA를 계산하려면 처음 SMA를 구한 뒤, 매일 이 공식을 업데이트

3. 간단한 선형 회귀 (Linear Regression)
   - 30일 데이터를 점으로 찍고 직선을 그려서 미래 값(31일째)을 예측, 기울기 b가 양수면 상승, 음수면 하락 추세

<h3>💾 DB 설계 및 구축</h3>
데이터베이스는 MySQL이나 PostgreSQL 중 고민하였으나 아래와 같은 이유로 PostgreSQL을 사용하기로 결정하였다.
- PostgreSQL은 주식 데이터처럼 정확성과 안정성이 중요한 프로젝트에서 더 적합
- JSON, 고급 SQL 기능, 트랜잭션 안정성 등에서 MySQL보다 강점이 많음
- 확장성과 분석 기능이 뛰어나 주식 데이터 저장 및 처리에 최적화

유저 정보 테이블(members)
```
# members
```

주식 정보 테이블(stocks)
```
# stocks
```



<h3>💻 개발</h3>

<h3>📝 주식 전체 정보 가져오기</h3>

한국/미국 주식 전체 정보를 조회하는 방법을 찾아보고 있는데 제공해주는 곳이 국내는 공공데이터포털에서 찾을 수 있었는데 미국주식은 제공해주는곳이 없었다.<br>
프로젝트를 진행하려면 이게 제일 중요한데 어떡하지... 하면서 계속 검색을 해봤는데 발견되지 않았다.<br>
고민하던 찰나에 네이버 증권에서 개발자도구로 찾던 도중 주식정보를 불러오는 것 같은 API를 찾아내었다. 막혀있지 않아서 이걸 사용해서 종목정보를 가져와야겠다, 감사합니다 네이버!<br>
데이터는 자바로 가져와도 되지만 파이썬을 별로 안써봐서 배울겸 파이썬을 이용해 가져오도록하자.<br>

미국 전체 주식 조회 API
- API URI
    - https://api.stock.naver.com/stock/exchange/[거래소]
- 거래소 정보
    - 미국
        - NASDAQ
        - NYSE
        - AMEX
- Response(stocks):
```
        {
            "stockType": "worldstock",
            "stockEndType": "stock",
            "compareToPreviousPrice": {
                "code": "2",
                "text": "상승",
                "name": "RISING"
            },
            "nationType": "USA",
            "stockExchangeType": {
                "code": "NSQ",
                "zoneId": "EST5EDT",
                "nationType": "USA",
                "delayTime": 0,
                "startTime": "0930",
                "endTime": "1600",
                "closePriceSendTime": "2031",
                "nameKor": "나스닥 증권거래소",
                "nameEng": "NASDAQ Stock Exchange",
                "name": "NASDAQ",
                "nationName": "미국",
                "stockType": "worldstock",
                "nationCode": "USA"
            },
            "reutersCode": "LYT.O",
            "symbolCode": "LYT",
            "stockName": "리투스 테크놀로지스 홀딩스",
            "stockNameEng": "Lytus Technologies Holdings Ptv Ltd",
            "reutersIndustryCode": "5720103010",
            "industryCodeType": {
                "code": "57201030",
                "industryGroupKor": "온라인 서비스",
                "name": "INDUSTRY57201030"
            },
            "openPrice": "0.14",
            "closePrice": "0.12",
            "compareToPreviousClosePrice": "0.06",
            "fluctuationsRatio": "89.41",
            "executedVolume": null,
            "accumulatedTradingVolume": "1,009,479,875",
            "accumulatedTradingValue": "126,410",
            "accumulatedTradingValueKrwHangeul": "1,855억원",
            "localTradedAt": "2025-03-27T16:00:00-04:00",
            "marketStatus": "CLOSE",
            "overMarketPriceInfo": null,
            "marketValue": "3,671",
            "marketValueHangeul": "0.04억 USD",
            "marketValueKrwHangeul": "53.9억원",
            "currencyType": {
                "code": "USD",
                "text": "US dollar",
                "name": "USD"
            },
            "dividend": "0.00",
            "dividendPayAt": "2024-02-23T21:00:00Z",
            "tradeStopType": {
                "code": "1",
                "text": "운영.Trading",
                "name": "TRADING"
            },
            "endUrl": "https://m.stock.naver.com/worldstock/stock/LYT.O",
            "delayTime": 0,
            "delayTimeName": "실시간",
            "stockEndUrl": "https://m.stock.naver.com/worldstock/stock/LYT.O",
            "exchangeOperatingTime": false
        }
```

한국 전체 주식 조회 API
- API URI
    - https://m.stock.naver.com/api/stocks/marketValue/[거래소]
- 거래소 정보
    - 한국
        - KOSPI
        - KOSDAQ
    - Response(stocks):
```
        {
            "stockType": "domestic",
            "stockEndType": "stock",
            "itemCode": "005930",
            "reutersCode": "005930",
            "stockName": "삼성전자",
            "sosok": "0",
            "closePrice": "60,200",
            "compareToPreviousClosePrice": "-1,600",
            "compareToPreviousPrice": {
                "code": "5",
                "text": "하락",
                "name": "FALLING"
            },
            "fluctuationsRatio": "-2.59",
            "accumulatedTradingVolume": "16,222,219",
            "accumulatedTradingValue": "980,043",
            "accumulatedTradingValueKrwHangeul": "9,800억원",
            "localTradedAt": "2025-03-28T16:11:53+09:00",
            "marketValue": "3,563,622",
            "marketValueHangeul": "356조 3,622억원",
            "nav": "N/A",
            "threeMonthEarningRate": "N/A",
            "marketStatus": "CLOSE",
            "tradeStopType": {
                "code": "1",
                "text": "운영.Trading",
                "name": "TRADING"
            },
            "stockExchangeType": {
                "code": "KS",
                "zoneId": "Asia/Seoul",
                "nationType": "KOR",
                "delayTime": 0,
                "startTime": "0900",
                "endTime": "1530",
                "closePriceSendTime": "1630",
                "nameKor": "코스피",
                "nameEng": "KOSPI",
                "stockType": "domestic",
                "nationCode": "KOR",
                "nationName": "대한민국",
                "name": "KOSPI"
            },
            "endUrl": "https://m.stock.naver.com/domestic/stock/005930",
            "overMarketPriceInfo": {
                "tradingSessionType": "AFTER_MARKET",
                "overMarketStatus": "CLOSE",
                "overPrice": "60,200",
                "compareToPreviousPrice": {
                    "code": "5",
                    "text": "하락",
                    "name": "FALLING"
                },
                "compareToPreviousClosePrice": "-1,600",
                "fluctuationsRatio": "-2.59",
                "localTradedAt": "2025-03-28T20:00:00+09:00",
                "tradeStopType": {
                    "code": "1",
                    "text": "운영.Trading",
                    "name": "TRADING"
                }
            }
        }
```

<h3>📝 주식 가격 정보 API</h3>

주식 가격 정보를 얻어오기 위해 검색중 한국투자증권이 잘되있는 것 같아서 한국투자증권 Open API를 신청하였다.
- 한국투자증권 Open API 신청
    - [한국투자증권 Open API 홈페이지](https://apiportal.koreainvestment.com/intro)
- 사용할 API 선택 - 용도
    - [한국주식]
        - 일별 종목 종가 조회
    - [미국주식]
        - 일별 종목 종가 조회


<h3>📝 오류 모니터링</h3>
슬랙 웹훅을 사용하여 오류가 발생할 때 슬랙으로 메시지가 가도록 하였다.