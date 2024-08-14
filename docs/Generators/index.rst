.. _generator-index:

Included RTL Generators
============================

Generator는 메타 프로그래밍과 표준 RTL을 혼합하여 작성된 일반화된 RTL 디자인으로 생각할 수 있습니다.
이러한 메타 프로그래밍은 Chisel 하드웨어 설명 언어에 의해 가능해집니다(참조: :ref:`Tools/Chisel:Chisel`).
표준 RTL 디자인은 기본적으로 생성기에서 파생된 단일 디자인 인스턴스에 불과합니다.
그러나 메타 프로그래밍과 매개변수 시스템을 사용하면, 생성기를 통해 복잡한 하드웨어 디자인을 자동화된 방식으로 통합할 수 있습니다.
다음 페이지에서는 Chipyard 프레임워크와 통합된 생성기를 소개합니다.

Chipyard는 생성기의 소스 코드를 ``generators/`` 디렉토리 아래에 번들로 제공합니다.
이를 소스에서 매번 빌드합니다(빌드 시스템은 변경되지 않은 경우 결과를 캐시합니다),
따라서 생성기 자체에 대한 변경 사항은 Chipyard로 빌드할 때 자동으로 사용되며 소프트웨어 시뮬레이션, FPGA 가속 시뮬레이션 및 VLSI 흐름에 전파됩니다.


.. toctree::
   :maxdepth: 2
   :caption: Generators:

   Rocket-Chip
   Rocket
   BOOM
   Constellation
   Gemmini
   Saturn
   IceNet
   TestChipIP
   Rocket-Chip-Generators
   CVA6
   Ibex
   fft
   NVDLA
   Sodor
   Shuttle
   Mempress
   CompressAcc
   Prefetchers
   Ara
