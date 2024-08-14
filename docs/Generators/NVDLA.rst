NVDLA
====================================

`NVDLA <http://nvdla.org/>`_ 는 NVIDIA에서 개발한 오픈 소스 딥 러닝 가속기입니다.
`NVDLA` 는 TileLink 주변 장치로 연결되어 `Rocket Chip SoC generator` 내에서 구성 요소로 사용할 수 있습니다.
이 가속기는 자체적으로 AXI 메모리 인터페이스(또는 "Large" 구성을 사용할 경우 두 개의 인터페이스), 제어 인터페이스 및 인터럽트 라인을 노출합니다.
Chipyard에서 이 가속기를 사용하는 주요 방법은 FireSim Linux에서 작동하도록 포팅된 `NVDLA SW 리포지토리 <https://github.com/ucb-bar/nvdla-sw>`_ 를 사용하는 것입니다.
그러나, 가속기를 베어메탈 시뮬레이션에서 사용할 수도 있습니다(예: ``tests/nvdla.c`` 참고).

하드웨어 아키텍처와 소프트웨어에 대한 자세한 내용은 `웹사이트 <http://nvdla.org/>`_ 를 방문하십시오.

NVDLA Software with FireMarshal
-------------------------------

``software/nvdla-workload`` 디렉토리에는 올바른 NVDLA 드라이버로 Linux를 부팅하기 위한 FireMarshal 기반 워크로드가 포함되어 있습니다.
시뮬레이션 실행 방법에 대한 자세한 내용은 해당 ``README.md`` 를 참조하십시오.

