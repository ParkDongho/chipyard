.. _chipyard-components:

Chipyard Components
===============================

Generators
-------------------------------------------

Chipyard 프레임워크는 현재 다음과 같은 RTL 생성기로 구성되어 있습니다:


Processor Cores
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

**Rocket Core**
  순차 실행 방식의 RISC-V 코어입니다.
  자세한 내용은 :ref:`Generators/Rocket:Rocket Core` 를 참조하세요.

**BOOM (Berkeley Out-of-Order Machine)**
  비순차 실행 방식의 RISC-V 코어입니다.
  자세한 내용은 :ref:`Generators/BOOM:Berkeley Out-of-Order Machine (BOOM)` 을 참조하세요.

**CVA6 Core**
  System Verilog으로 작성된 순차 실행 방식의 RISC-V 코어입니다. 이전에는 Ariane이라고 불렸습니다.
  자세한 내용은 :ref:`Generators/CVA6:CVA6 Core` 를 참조하세요.

**Ibex Core**
  System Verilog으로 작성된 순차 실행 방식의 32비트 RISC-V 코어입니다.
  자세한 내용은 :ref:`Generators/Ibex:Ibex Core` 를 참조하세요.

Accelerators
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

**Gemmini**
  신경망을 대상으로 하는 행렬 곱셈 가속기입니다.

System Components:
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
**constellation**
  칩 내 네트워크(NoC) 인터커넥트를 위한 생성기입니다.

**icenet**
  최대 200 Gbps까지 지원하는 네트워크 인터페이스 컨트롤러(NIC)입니다.

**rocket-chip-blocks**
  SiFive 프로젝트에서 사용된 시스템 구성 요소로, Rocket Chip 생성기와 통합되도록 설계되었습니다. 현재는 Chips Alliance에서 유지 관리합니다. 이 시스템 및 주변 장치 구성 요소에는 UART, SPI, JTAG, I2C, PWM 및 기타 주변 장치와 인터페이스 장치가 포함됩니다.

**AWL (Analog Widget Library)**
  고속 직렬 링크와의 통합을 위해 필요한 디지털 구성 요소입니다.

**testchipip**
  칩 테스트와 대규모 테스트 환경과의 인터페이스를 위한 유틸리티 모음입니다.


Tools
-------------------------------------------

**Chisel**
  스칼라에 포함된 하드웨어 설명 라이브러리입니다.
  Chisel은 스칼라 프로그래밍 언어에 하드웨어 생성 원시 기능을 포함시켜 메타 프로그래밍을 통해 RTL 생성기를 작성하는 데 사용됩니다.
  Chisel 컴파일러는 생성기를 FIRRTL 출력으로 변환합니다.
  자세한 내용은 :ref:`Tools/Chisel:Chisel` 를 참조하세요.

**FIRRTL**
  디지털 설계의 RTL 설명을 위한 중간 표현 라이브러리입니다.
  FIRRTL은 Chisel과 Verilog 사이의 형식화된 디지털 회로 표현으로 사용됩니다.
  FIRRTL은 Chisel 변환과 Verilog 생성 사이에서 디지털 회로를 조작할 수 있게 합니다.
  자세한 내용은 :ref:`Tools/FIRRTL:FIRRTL` 를 참조하세요.

**Tapeout-Tools (구 Barstools)**
  생성기 소스 RTL을 변경하지 않고 디지털 회로를 조작하는 데 사용되는 일반적인 FIRRTL 변환 모음입니다.
  자세한 내용은 :ref:`Tools/Tapeout-Tools:Tapeout-Tools` 를 참조하세요.

**Dsptools**
  맞춤형 신호 처리 하드웨어를 작성하고 이를 SoC(특히 Rocket 기반 SoC)에 통합하기 위한 Chisel 라이브러리입니다.

Toolchains
-------------------------------------------

**riscv-tools**
  RISC-V ISA에서 소프트웨어를 개발하고 실행하는 데 사용되는 소프트웨어 툴체인 모음입니다.
  여기에는 컴파일러 및 어셈블러 툴체인, 기능적 ISA 시뮬레이터(spike), Berkeley Boot Loader(BBL) 및 프록시 커널이 포함됩니다.
  riscv-tools 저장소는 이전에는 모든 RISC-V 소프트웨어를 실행하는 데 필수적이었지만, 이후 여러 구성 요소가 각자의 오픈 소스 프로젝트(Linux, GNU 등)로 통합되었습니다.
  그럼에도 불구하고 버전 관리의 일관성과 맞춤형 하드웨어에 대한 소프트웨어 설계 유연성을 위해 Chipyard 프레임워크에 riscv-tools 저장소와 설치가 포함되어 있습니다.

Software
-------------------------------------------

**FireMarshal**
  FireMarshal은 Chipyard에서 플랫폼에서 실행할 소프트웨어를 생성하는 기본 워크로드 생성 도구입니다.
  자세한 내용은 :ref:`fire-marshal` 을 참조하세요.

**Baremetal-IDE**
  베어메탈 수준의 C/C++ 프로그램 개발을 위한 올인원 도구입니다. 자세한 내용은 `Tutorial <https://ucb-bar.gitbook.io/chipyard/baremetal-ide/getting-started-with-baremetal-ide/>`_ 을 참조하세요.

Sims
-------------------------------------------

**Verilator**
  Verilator는 오픈 소스 Verilog 시뮬레이터입니다.
  ``verilator`` 디렉토리는 생성된 RTL에서 Verilator 기반 시뮬레이터를 구성하는 래퍼를 제공하여 시뮬레이터에서 RISC-V 프로그램을 테스트할 수 있도록 합니다(vcd 파형 파일 포함).
  자세한 내용은 :ref:`Simulation/Software-RTL-Simulation:Verilator (Open-Source)` 를 참조하세요.

**VCS**
  VCS는 독점 Verilog 시뮬레이터입니다.
  사용자가 유효한 VCS 라이선스와 설치를 가지고 있다고 가정하면, ``vcs`` 디렉토리는 생성된 RTL에서 VCS 기반 시뮬레이터를 구성하는 래퍼를 제공하여 시뮬레이터에서 RISC-V 프로그램을 테스트할 수 있도록 합니다(vcd/vpd 파형 파일 포함).
  자세한 내용은 :ref:`Simulation/Software-RTL-Simulation:Synopsys VCS (License Required)` 를 참조하세요.

**FireSim**
  FireSim은 오픈 소스 FPGA 가속 시뮬레이션 플랫폼으로, Amazon Web Services(AWS) EC2 F1 인스턴스를 사용합니다.
  FireSim은 오픈 하드웨어 설계를 고속(10s-100s MHz), 결정론적, FPGA 기반 시뮬레이터로 자동 변환 및 계측하여 생산적인 사전 실리콘 검증 및 성능 검증을 가능하게 합니다.
  FireSim은 DRAM, Ethernet, UART 등 표준 인터페이스에 대한 합성 가능하고 시간적으로 정확한 모델을 포함하여 I/O를 모델링합니다.
  Elastic 퍼블릭 클라우드를 사용하여 FireSim은 수천 개의 노드로 시뮬레이션을 확장할 수 있습니다.
  FireSim을 사용하려면 AWS 인스턴스에서 저장소를 클론하고 실행해야 합니다.
  자세한 내용은 :ref:`Simulation/FPGA-Accelerated-Simulation:FireSim` 을 참조하세요.

Prototyping
-------------------------------------------

**FPGA 프로토타이핑**
  FPGA 프로토타이핑은 SiFive의 ``fpga-shells`` 을 사용하여 Chipyard에서 지원됩니다.
  지원되는 FPGA의 예로는 Xilinx Arty 35T 및 VCU118 보드가 있습니다.
  충분한 디버깅 도구와 함께 빠르고 결정론적인 시뮬레이션을 위해 :ref:`Simulation/FPGA-Accelerated-Simulation:FireSim` 플랫폼을 사용하는 것을 고려하세요.
  FPGA 프로토타입에 대한 자세한 내용은 :ref:`Prototyping/index:Prototyping Flow` 를 참조하세요.

VLSI
-------------------------------------------

**Hammer**
  Hammer는 일반적인 물리적 설계 개념과 벤더별 EDA 도구 명령 사이에 추상화 계층을 제공하도록 설계된 VLSI 흐름입니다.
  HAMMER 흐름은 물리적 설계 제약 조건에 대한 상위 수준 설명을 기반으로 관련 도구 명령을 생성하는 자동화된 스크립트를 제공합니다.
  Hammer 흐름은 또한 프로세스 기술과 관련된 특정 제약 조건(구식 표준 셀, 금속층 라우팅 제약 등)을 설명하는 프로세스 기술별 플러그인을 구축할 수 있게 하여 프로세스 기술 지식의 재사용을 가능하게 합니다.
  Hammer 흐름에는 독점 EDA 도구와 프로세스 기술 라이브러리에 대한 접근이 필요합니다.
  자세한 내용은 :ref:`VLSI/Hammer:Core HAMMER` 를 참조하세요.
