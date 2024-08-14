Simulation
=======================

Chipyard는 두 가지 종류의 시뮬레이션을 지원합니다:

1. 상용 또는 오픈 소스(Verilator) RTL 시뮬레이터를 사용하는 소프트웨어 RTL 시뮬레이션
2. FireSim을 사용하는 FPGA 가속 풀 시스템 시뮬레이션

Chipyard 디자인의 소프트웨어 RTL 시뮬레이터는 약 O(1 KHz)의 속도로 실행되지만, 컴파일이 빠르고 전체 웨이브폼을 제공합니다. 반면에 FPGA 가속 시뮬레이터는 약 O(100 MHz)의 속도로 실행되어 운영 체제를 부팅하고 전체 작업을 실행하는 데 적합하지만, 컴파일 시간이 여러 시간에 이르고 디버그 가시성이 낮습니다.

다음 항목을 클릭하여 시뮬레이션을 실행하는 방법을 확인하세요.

.. toctree::
   :maxdepth: 2
   :caption: Simulation:

   Software-RTL-Simulation
   FPGA-Accelerated-Simulation

