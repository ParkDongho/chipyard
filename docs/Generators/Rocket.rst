Rocket Core
====================================

`Rocket <https://github.com/freechipsproject/rocket-chip>`__ 는 UC Berkeley와 `SiFive <https://www.sifive.com/>`__ 에서 처음 개발된 5단계 인오더 스칼라 프로세서 코어 생성기이며, 현재는 Chips Alliance에서 유지 관리되고 있습니다. `Rocket core`는 `Rocket Chip SoC generator` 내에서 구성 요소로 사용됩니다. Rocket 코어는 L1 캐시(데이터 및 명령어 캐시)와 결합하여 `Rocket 타일` 을 형성합니다. `Rocket 타일` 은 `Rocket Chip SoC generator` 의 복제 가능한 구성 요소입니다.

Rocket 코어는 오픈 소스 RV64GC RISC-V 명령어 세트를 지원하며, Chisel 하드웨어 생성 언어로 작성되었습니다.
이 코어는 페이지 기반 가상 메모리를 지원하는 MMU, 논블로킹 데이터 캐시, 그리고 분기 예측 기능을 갖춘 프론트엔드를 가지고 있습니다.
분기 예측은 브랜치 타겟 버퍼(BTB), 브랜치 히스토리 테이블(BHT), 리턴 주소 스택(RAS)에 의해 제공되며, 구성 가능합니다.
부동 소수점 연산을 위해 Rocket은 Berkeley의 Chisel로 구현된 부동 소수점 유닛을 사용합니다.
또한 Rocket은 RISC-V의 머신, 슈퍼바이저, 사용자 권한 레벨을 지원합니다.
여러 매개변수가 노출되어 있으며, 일부 ISA 확장(M, A, F, D)의 선택적 지원, 부동 소수점 파이프라인 단계 수, 캐시 및 TLB 크기 등이 포함됩니다.

자세한 내용은 `GitHub 리포지토리 <https://github.com/freechipsproject/rocket-chip>`__, `기술 보고서 <https://www2.eecs.berkeley.edu/Pubs/TechRpts/2016/EECS-2016-17.html>`__  또는 `이 Chisel 커뮤니티 컨퍼런스 비디오 <https://youtu.be/Eko86PGEoDY>`__ 를 참조하십시오.

