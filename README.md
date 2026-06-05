# 윷놀이 (Yut Nori)

Java Swing으로 구현한 한국 전통 보드게임 윷놀이입니다.  
2~4인 멀티플레이를 지원하며, 4각형·5각형·6각형 세 가지 보드를 선택할 수 있습니다.

---

## 목차

- [게임 소개](#게임-소개)
- [주요 기능](#주요-기능)
- [실행 방법](#실행-방법)
- [조작 방법](#조작-방법)
- [기술 스택](#기술-스택)
- [폴더 구조](#폴더-구조)
- [클래스 구조](#클래스-구조)
- [게임 흐름](#게임-흐름)
- [주요 구현 포인트](#주요-구현-포인트)
- [리팩토링 내용](#리팩토링-내용)
- [트러블슈팅](#트러블슈팅)
- [개선 예정 사항](#개선-예정-사항)

---

## 게임 소개

윷놀이는 한국의 전통 보드게임으로, 윷가락(막대)을 던져 나온 결과에 따라 말을 이동시킵니다.  
모든 말을 먼저 골인시키는 플레이어가 승리합니다.

| 윷 결과 | 이동 칸 수 | 특이 사항       |
| ------- | ---------- | --------------- |
| 빽도    | -1         | 뒤로 한 칸      |
| 도      | 1          |                 |
| 개      | 2          |                 |
| 걸      | 3          |                 |
| 윷      | 4          | 한 번 더 던지기 |
| 모      | 5          | 한 번 더 던지기 |

---

## 주요 기능

- **보드 선택** : 4각형 / 5각형 / 6각형 중 선택
- **플레이어 설정** : 2~4인, 말 2~5개
- **랜덤 / 수동 던지기** : 랜덤 결과 또는 직접 지정
- **말 업기** : 같은 팀 말이 같은 칸에 있으면 자동 합산
- **말 잡기** : 상대 말 위에 올라서면 상대 말이 대기 위치로 복귀
- **추가 턴** : 윷·모가 나오거나 상대 말을 잡으면 한 번 더 던지기
- **이동 미리보기** : 말 선택 시 목적지를 미리 표시
- **골인 버튼** : 목적지가 도착 칸일 때 골인 처리

---

## 실행 방법

### 요구 사항

- Java 17 이상
- Gradle 8.10 (Gradle Wrapper 포함)

### 실행

```bash
# Windows
./gradlew.bat run

# macOS / Linux
./gradlew run
```

### 빌드

```bash
./gradlew build
```

---

## 조작 방법

### 시작 화면

1. 사용할 보드 모양을 클릭하여 선택 (4각형 / 5각형 / 6각형)
2. 플레이어 수와 말 개수를 스피너로 설정
3. **게임 시작** 버튼 클릭

### 게임 화면

| 동작             | 방법                                           |
| ---------------- | ---------------------------------------------- |
| 윷 던지기 (랜덤) | `랜덤 던지기` 버튼 클릭                        |
| 윷 던지기 (수동) | `지정 던지기` 클릭 후 원하는 윷 결과 버튼 선택 |
| 보드 위 말 선택  | 말이 있는 노드 클릭 → 이동 미리보기 표시       |
| 이동 확정        | 미리보기가 표시된 노드 클릭                    |
| 새 말 투입       | `새로운 말 움직이기` 버튼 클릭                 |
| 골인 처리        | 목적지가 도착 칸일 때 `골인!` 버튼 클릭        |
| 시작 화면으로    | `← 시작 화면으로` 버튼 클릭                    |

---

## 기술 스택

| 항목     | 내용                     |
| -------- | ------------------------ |
| Language | Java 17                  |
| GUI      | Java Swing               |
| Build    | Gradle 8.10 (Kotlin DSL) |
| Test     | JUnit 5                  |

---

## 폴더 구조

```
src/main/java/com/yut/
├── Main.java                          # 진입점
│
├── model/                             # 게임 로직 (Model)
│   ├── Game.java                      # 게임 전체 상태 관리
│   ├── GameTurn.java                  # 단일 턴 상태 머신
│   ├── Board.java                     # 보드 추상 클래스
│   ├── SquareBoard.java               # 4각형 보드
│   ├── PentagonBoard.java             # 5각형 보드
│   ├── HexagonBoard.java              # 6각형 보드
│   ├── Node.java                      # 보드 칸
│   ├── Group.java                     # 말 그룹 (업기 포함)
│   ├── Player.java                    # 플레이어 상태
│   ├── Players.java                   # 플레이어 컨테이너
│   ├── Yut.java                       # 윷 던지기 (싱글톤)
│   └── YutResult.java                 # 윷 결과 enum
│
├── controller/                        # 이벤트 처리 (Controller)
│   ├── GameController.java            # 게임 화면 이벤트 처리
│   ├── TitleController.java           # 시작 화면 이벤트 처리
│   ├── model_interfaces/
│   │   ├── GameModelInterface.java
│   │   └── GameTurnModelInterface.java
│   └── view_interfaces/
│       └── GameScreenInterface.java
│
└── ui/swing/                          # 화면 구성 (View)
    ├── MainFrame.java                 # 메인 윈도우 (CardLayout)
    ├── GameScreen.java                # 게임 메인 화면
    ├── StartScreen.java               # 시작 / 설정 화면
    ├── BoardCanvas.java               # 보드 렌더링
    ├── ClickableNode.java             # 클릭 가능한 보드 칸
    ├── Piece.java                     # 말 컴포넌트
    ├── PlayerCanvas.java              # 플레이어 상태 표시
    ├── ControlPanel.java              # 버튼 패널
    └── EndingFrame.java               # 게임 종료 화면
```

---

## 클래스 구조

### Model

| 클래스         | 역할                                                                     |
| -------------- | ------------------------------------------------------------------------ |
| `Game`         | 플레이어·보드·현재 턴을 관리. `switchTurn()`으로 턴 교체                 |
| `GameTurn`     | 한 턴의 상태 머신. `THROWABLE` / `HASTOMOVE` 두 상태로 흐름 제어         |
| `Board` (추상) | 노드 맵을 관리. `SquareBoard` / `PentagonBoard` / `HexagonBoard`가 상속  |
| `Node`         | 보드의 단일 칸. `nextNodes[]`로 이동 경로 연결                           |
| `Group`        | 업힌 말 묶음. `move()`로 이동·잡기·업기 처리                             |
| `Player`       | 현재 남은 말 수, 그룹 목록, 이동 대상 관리                               |
| `Yut`          | 윷 던지기 싱글톤. 랜덤 / 지정 결과 지원                                  |
| `YutResult`    | 윷 결과 enum. `grantsExtraTurn()`, `getSteps()` 등 의미 있는 메서드 제공 |

### Controller

| 클래스            | 역할                                                        |
| ----------------- | ----------------------------------------------------------- |
| `GameController`  | 노드 클릭·버튼 이벤트를 받아 Model을 조작하고 View를 갱신   |
| `TitleController` | 시작 화면의 설정값을 받아 `Game` 인스턴스 생성 및 화면 전환 |

### View

| 클래스          | 역할                                                                |
| --------------- | ------------------------------------------------------------------- |
| `MainFrame`     | `CardLayout`으로 시작 화면 ↔ 게임 화면 전환                         |
| `GameScreen`    | `JLayeredPane`으로 보드·말·미리보기를 레이어 구조로 렌더링          |
| `BoardCanvas`   | 보드 형태 계산 및 노드 좌표 생성                                    |
| `ClickableNode` | 노드 클릭 감지. 상태(빈 칸 / 미리보기 / 말 있음)에 따라 시각 피드백 |

---

## 게임 흐름

```
앱 실행
  └─ MainFrame 생성 (CardLayout)
       └─ StartScreen 표시
            │
            │ (게임 시작 버튼)
            │
            └─ TitleController
                 ├─ Game(boardType, playerCount, pieceCount) 생성
                 └─ GameScreen + GameController 생성
                      │
                      └─ [게임 루프 - 이벤트 드리븐]
                           │
                           ├─ 윷 던지기
                           │    ├─ BACKDO + 모든 말 대기 → 다시 던지기
                           │    └─ 윷/모 → 추가 턴 부여
                           │
                           ├─ 말 선택 (노드 클릭 or 새 말 버튼)
                           │    └─ 이동 미리보기 표시
                           │
                           ├─ 이동 확정 (미리보기 노드 클릭 or 골인 버튼)
                           │    ├─ 업기  → 그룹 합산
                           │    ├─ 잡기  → 상대 말 대기 복귀 + 추가 턴
                           │    └─ 골인  → 말 수 감소
                           │
                           └─ 모든 말 골인 → EndingFrame 표시
```
