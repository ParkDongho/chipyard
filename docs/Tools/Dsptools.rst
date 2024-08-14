Dsptools
===============================

`Dsptools <https://github.com/ucb-bar/dsptools/>`__ 는 사용자 정의 신호 처리 하드웨어를 작성하기 위한 Chisel 라이브러리입니다.
또한, dsptools는 사용자 정의 신호 처리 하드웨어를 SoC(특히 Rocket 기반 SoC)에 통합하는 데 유용합니다.

주요 기능:

* 복소수 타입
* 다형성 하드웨어 생성기를 작성하기 위한 타입클래스
  * 예를 들어, 실수 또는 복소수 입력에 대해 작동하는 FIR 필터 생성기를 하나 작성할 수 있습니다.
* 고정 소수점 및 부동 소수점 타입에 대한 Chisel 테스터 확장
* AXI4-Stream의 외교적 구현
* chisel-testers를 사용한 APB, AXI-4, 및 TileLink 인터페이스 검증을 위한 모델
* DSP 빌딩 블록

