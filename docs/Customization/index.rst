Customization
================================

이 가이드는 시스템 온 칩(SoC)을 사용자 정의하는 방법을 안내합니다:

- 기존 Chipyard 생성기와 구성 시스템을 사용하여 이기종 시스템 온 칩을 구성하는 방법

- Constellation을 사용하여 NoC(네트워크 온 칩) 기반의 인터커넥트를 갖춘 SoC를 구성하는 방법

- 사용자 정의 Chisel 소스를 Chipyard 빌드 시스템에 포함하는 방법

- 사용자 정의 코어 추가 방법

- 기존 Chipyard 코어(BOOM 또는 Rocket)에 사용자 정의 RoCC 가속기를 추가하는 방법

- 사용자 정의 상위 레벨 IO와 함께 TileLink 또는 AXI4를 통해 Chipyard 메모리 시스템에 사용자 정의 MMIO 위젯을 추가하는 방법

- Dsptools 기반 블록을 MMIO 위젯으로 추가하는 방법

- Keys, Traits, Configs를 사용하여 설계를 매개변수화하는 표준 관행

- 메모리 계층 구조를 사용자 정의하는 방법

- TileLink 마스터로 작동하는 위젯 연결 방법

- Chipyard 설계에 사용자 정의 블랙박스 Verilog를 추가하는 방법

또한 다음에 대한 정보도 제공합니다:

- Chipyard SoC의 부팅 프로세스

이 페이지를 순서대로 읽는 것을 권장합니다. 시작하려면 '다음'을 클릭하세요!

.. toctree::
   :maxdepth: 2
   :caption: Customization:

   Heterogeneous-SoCs
   NoC-SoCs
   Custom-Chisel
   Custom-Core
   RoCC-or-MMIO
   RoCC-Accelerators
   MMIO-Peripherals
   Dsptools-Blocks
   Keys-Traits-Configs
   DMA-Devices
   Incorporating-Verilog-Blocks
   Memory-Hierarchy
   Boot-Process
   IOBinders

