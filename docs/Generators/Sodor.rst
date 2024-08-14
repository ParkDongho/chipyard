Sodor Core
====================================

`Sodor <https://github.com/ucb-bar/riscv-sodor>`__ 는 교육 목적으로 설계된 5개의 간단한 RV32MI 코어 모음입니다.
`Sodor 코어` 는 생성 시 타일로 래핑되어 `Rocket Chip SoC 생성기` 내에서 구성 요소로 사용할 수 있습니다.
이 코어들은 프로그램이 TileLink 슬레이브 포트를 통해 로드되는 작은 스크래치패드 메모리를 포함하며, 코어들은 **외부 메모리를 지원하지 않습니다**.

사용 가능한 다섯 가지 코어와 해당 생성기 구성은 다음과 같습니다:

* 1단계(본질적으로 ISA 시뮬레이터) - ``Sodor1StageConfig``
* 2단계(Chisel에서 파이프라이닝을 시연) - ``Sodor2StageConfig``
* 3단계(순차 메모리를 사용하며 하버드(``Sodor3StageConfig``)와 프린스턴(``Sodor3StageSinglePortConfig``) 버전을 지원)
* 5단계(완전한 바이패스 또는 완전한 인터락 간 전환 가능) - ``Sodor5StageConfig``
* "버스" 기반의 마이크로코드 구현 - ``SodorUCodeConfig``

자세한 내용은 `GitHub 리포지토리 <https://github.com/ucb-bar/riscv-sodor>`__ 를 참조하십시오.

