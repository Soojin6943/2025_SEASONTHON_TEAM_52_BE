# 🏠 오손도손  
청년들의 주거비 부담 완화를 위한 주거 성향 기반 룸메이트 매칭 서비스


##  프로젝트 개요  
청년 1인 가구가 겪는 **높은 주거비 부담과 사회적 고립 문제**를 해결하기 위해 기획된 서비스입니다.  
<br>
대학생 및 사회초년생을 위한 룸메이트 매칭 및 공동 생활 관리 플랫폼입니다. 단순한 주거 공간 공유를 넘어, 생활 패턴과 가치관이 맞는 사람들을
  연결하여 긍정적인 주거 경험을 제공하는 것을 목표로 합니다.

  사용자는 자신의 프로필과 원하는 룸메이트의 조건을 설정하여 최적의 상대를 추천받을 수 있습니다. 
  
  
단순히 집을 찾는 것이 아니라, **나와 잘 맞는 ‘사람’을 찾는 것**에 집중하여 안전하고 지속 가능한 공동 주거 경험을 제공합니다.  

<br>

- **개발 기간**: 2025.09 (2주)  
- **팀 구성**: FE 2명, BE 3명, DE 1명

  <br>

##  기술 스택  
| 분야       | 기술 |
|------------|----------------------------------|
| Language   | Java |
| Framework  | Spring Boot |
| Database   | MySQL |
| Infra      | AWS EC2, AWS S3, Docker |
| DevOps     | GitHub Actions (CI/CD) |
| Tools      | Git, GitHub, Figma, Discord |

##  주요 기능  
 **희망 지역 기반 룸메이트 매칭**
  - 프로필 기반 추천: MBTI, 수면 시간, 청소 주기 등 다차원적인 데이터를 기반으로 한 정교한 매칭 알고리즘을 통해 최적의 룸메이트를 추천합니다.
  - 게시글 기반 탐색: 사용자가 직접 '룸메' 또는 '방'을 구하는 게시글을 작성하고 검색할 수 있습니다.

  **지역별 시세 정보 제공**  
  - 주택 유형·면적대별 평균 월세 및 추이 시각화  
  - 지역별 평균 월세를 한눈에 확인할 수 있는 대시보드 제공
  - 공공데이터포털 API와 연동하여 지역(구, 동)별, 주거 형태별 평균 월세 및 전세 통계 정보를 제공합니다.

  **안전 계약 가이드**  
  - 룸메이트 계약 전 반드시 확인해야 할 **안전 계약 체크리스트** 제공  
  - 합리적 계약 및 분쟁 방지를 위한 주의사항 안내
 
  **사용자 관리**:
  - JWT/세션 기반의 안전한 인증 및 인가 시스템을 제공합니다.
  - S3를 연동한 프로필 이미지 업로드 및 관리가 가능합니다.

   



## 아키텍처
<img width="623" height="547" alt="Image" src="https://github.com/user-attachments/assets/83ca22dd-5125-489d-b21c-e191a46ef8e6" />
  


 ## 주요 설계 결정

  🔹 CI/CD 파이프라인 구축
   - 목적: 코드 변경 사항을 자동으로 빌드, 테스트하고 배포하여 개발 생산성을 높이고 안정적인 서비스를 제공하기 위해 GitHub Actions를 사용했습니다.
   - 구성:
       1. push 또는 pull_request가 main 브랜치로 발생 시 워크플로우 실행
       2. Gradle을 이용한 자동 빌드 및 단위 테스트 수행
       3. 테스트 통과 시 Docker 이미지를 빌드하여 Docker Hub 푸시
       4. SSH를 통해 서버에 접속하여 최신 Docker 이미지를 pull 받아 애플리케이션 실행

  🔹 S3를 이용한 이미지 파일 관리
   - 목적: 서버의 부담을 줄이고, 이미지 파일의 확장성과 관리 효율성을 높이기 위해 AWS S3를 파일 스토리지로 사용했습니다.
   - 프로세스: 클라이언트가 이미지를 요청하면, 서버는 S3에 이미지를 업로드하고 해당 파일에 접근할 수 있는 URL을 데이터베이스에 저장합니다. 이를 통해 서버는
     이미지 데이터 자체를 들고 있지 않아도 되므로 트래픽 부담이 감소합니다.


## 배포 및 운영
  - GitHub Actions + Docker + AWS EC2 기반 자동 배포 파이프라인 구축  
  - `main` 브랜치 push 시 자동 빌드 & 배포  

<img width="1041" height="586" alt="Image" src="https://github.com/user-attachments/assets/dab2e933-b883-4b14-a4ee-e9929643f38c" />
<img width="1042" height="583" alt="Image" src="https://github.com/user-attachments/assets/949a171c-a6f4-41d6-9d5e-9725ec4a5a18" />
<img width="1043" height="583" alt="Image" src="https://github.com/user-attachments/assets/6128fc1d-207b-4806-94b0-946b7df3a329" />
<img width="1047" height="585" alt="Image" src="https://github.com/user-attachments/assets/40b22bf1-9aef-47ef-82f7-6b62c49f4094" />
<img width="1044" height="588" alt="Image" src="https://github.com/user-attachments/assets/5a91a764-17bd-47a0-8c19-7db68992c0e5" />
<img width="1046" height="586" alt="Image" src="https://github.com/user-attachments/assets/68bd83b4-9e6c-4d96-91d1-62cb9999ed77" />
<img width="1044" height="586" alt="Image" src="https://github.com/user-attachments/assets/34b45154-1788-4299-b6b5-bab513ff3136" />
<img width="1050" height="589" alt="Image" src="https://github.com/user-attachments/assets/fab8583f-7998-4260-9a66-ccacd4527428" />
<img width="1046" height="586" alt="Image" src="https://github.com/user-attachments/assets/23d61a16-663b-48dc-a66c-72ca0e5f7ff0" />
<img width="1048" height="589" alt="Image" src="https://github.com/user-attachments/assets/e20aeaad-cf60-4c98-b62a-d9dd46efaacc" />

