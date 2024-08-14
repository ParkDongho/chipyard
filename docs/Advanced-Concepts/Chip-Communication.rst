Communicating with the DUT
===============================

두 가지 유형의 DUT(Digital Unit Testbench)를 만들 수 있습니다: `tethered` DUT와 `standalone` DUT입니다.
`tethered` DUT는 호스트 컴퓨터(또는 단순히 호스트)가 DUT에 트랜잭션을 전송하여 프로그램을 부팅해야 하는 DUT를 의미합니다.
이는 부팅 ROM을 가지고 자체적으로 프로그램을 로드하고 실행할 수 있는 `standalone` DUT와 다릅니다.
`tethered` DUT의 예로는 호스트가 테스트 프로그램을 DUT의 메모리에 로드하고 프로그램이 실행 준비가 되었음을 DUT에 신호를 보내는 Chipyard 시뮬레이션이 있습니다.
`standalone` DUT의 예로는 리셋 후 SD카드에서 프로그램을 로드할 수 있는 Chipyard 시뮬레이션이 있습니다.
이 섹션에서는 주로 tethered DUT와 통신하는 방법을 설명합니다.

호스트(또는 외부 세계)가 tethered Chipyard DUT와 통신하는 방법에는 두 가지가 있습니다:

* Tethered Serial Interface (TSI) 또는 Debug Module Interface (DMI)를 사용하여 Front-End Server (FESVR)로 DUT와 통신
* JTAG 인터페이스를 사용하여 OpenOCD 및 GDB로 DUT와 통신

다음 그림은 호스트와 시뮬레이션 사이에서 지원되는 통신 메커니즘을 블록 다이어그램으로 보여줍니다.

.. image:: ../_static/images/chip-communication.png

Using the Tethered Serial Interface (TSI) or the Debug Module Interface (DMI)
-----------------------------------------------------------------------------

TSI 또는 DMI를 사용하여 DUT와 통신하는 경우, Front-End Server (FESVR)를 사용하여 호스트와 DUT 간의 통신을 원활하게 합니다.

Primer on the Front-End Server (FESVR)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

FESVR은 호스트 컴퓨터와 RISC-V DUT 간의 통신을 관리하는 C++ 라이브러리입니다.
디버깅을 위해 DUT를 재설정하고, 메시지를 보내고, 프로그램을 로드/실행하는 간단한 API를 제공합니다.
또한 주변 장치 에뮬레이션을 제공합니다.
이 라이브러리는 시뮬레이터(VCS, Verilator, FireSim)와 통합할 수 있으며, 칩 테이프아웃 과정에서도 사용됩니다.

특히, FESVR은 호스트 타겟 인터페이스(HTIF)라는 통신 프로토콜을 사용하여 DUT와 통신합니다.
HTIF는 비차단 FIFO 인터페이스를 사용하여 DUT와 통신하는 비표준 Berkeley 프로토콜입니다.
HTIF는 메모리 읽기/쓰기, 프로그램 로드/시작/정지 등을 수행할 수 있는 프로토콜을 정의합니다.
TSI와 DMI는 DUT와 통신하기 위해 이 HTIF 프로토콜을 다르게 구현합니다.

Using the Tethered Serial Interface (TSI)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

기본적으로 Chipyard는 Tethered Serial Interface (TSI)를 사용하여 DUT와 통신합니다.
TSI 프로토콜은 RISC-V DUT에 명령을 보내는 HTIF의 구현입니다.
이 TSI 명령은 DUT의 메모리 공간에 접근할 수 있는 간단한 읽기/쓰기 명령입니다.
시뮬레이션 중에 호스트는 TSI 명령을 테스트 하니스 내에 있는 ``SimTSI`` 시뮬레이션 스텁(C++ 클래스)에 전송합니다.
이 ``SimTSI`` Verilog 모듈은 시뮬레이션 스텁에서 받은 TSI 명령을 TSI 명령을 TileLink 요청으로 변환하는 어댑터에 전달합니다.
이 변환은 ``TSIToTileLink`` 모듈에서 수행됩니다.
트랜잭션이 TileLink로 변환되면, ``TLSerdesser`` 가 트랜잭션을 직렬화하여 칩에 전송합니다. 
``TLSerdesser`` 는 때때로 디지털 직렬 링크 또는 SerDes라고도 합니다.
직렬화된 트랜잭션이 칩에 수신되면, 이를 역직렬화하여 칩의 TileLink 버스를 마스터링하여 요청을 처리합니다.
시뮬레이션에서 FESVR은 DUT를 재설정하고, 메모리에 테스트 프로그램을 쓰며, 인터럽트를 통해 프로그램 시작 신호를 보냅니다.
TSI를 사용하는 것이 현재 시뮬레이션에서 DUT와 통신하는 가장 빠른 방법이며, FireSim에서도 사용됩니다.

Using the Debug Module Interface (DMI)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

DUT와 인터페이스하는 또 다른 방법은 Debug Module Interface (DMI)를 사용하는 것입니다. 
TSI와 유사하게, DMI 프로토콜은 HTIF의 구현입니다.
DUT와 DMI 프로토콜로 통신하려면, DUT에 Debug Transfer Module (DTM)이 포함되어 있어야 합니다.
DTM은 RISC-V Debug Specification에서 제공되며, DUT와 DMI의 반대편에 있는 장치 간의 통신을 관리합니다.
이는 Rocket Chip ``Subsystem`` 에서 ``HasPeripheryDebug`` 및 ``HasPeripheryDebugModuleImp`` 특성으로 구현됩니다.
시뮬레이션 중에 호스트는 ``SimDTM`` 이라는 시뮬레이션 스텁(C++ 클래스)에 DMI 명령을 보냅니다.
이 ``SimDTM`` Verilog 모듈은 시뮬레이션 스텁에서 받은 DMI 명령을 DUT로 전달하며, DUT는 이를 TileLink 요청으로 변환합니다.
이 변환은 ``DebugModule`` 이라는 DTM이 수행합니다.
DTM이 프로그램을 수신하면 메모리에 이진 데이터를 바이트 단위로 기록하기 시작합니다.
이는 프로그램 이진 파일을 직접 메모리에 쓰는 TSI 프로토콜 통신 파이프라인보다 상당히 느립니다.

Starting the TSI or DMI Simulation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

모든 기본 Chipyard 구성은 TSI를 사용하여 시뮬레이션과 시뮬레이션된 SoC/DUT 간의 통신을 처리합니다.
따라서 :ref:`simulation/Software-RTL-Simulation:Software RTL Simulation` 섹션에서 언급된 대로 소프트웨어 RTL 시뮬레이션을 실행할 때 TSI를 사용하여 DUT와 통신하고 있습니다.
다시 말해, 소프트웨어 RTL 시뮬레이션을 실행하려면 다음을 실행하십시오:

.. code-block:: bash

   cd sims/verilator
   # 또는
   cd sims/vcs

   make CONFIG=RocketConfig run-asm-tests

DMI 통신을 위해 DTM이 구성된 Chipyard 구성을 빌드하고 시뮬레이션하려는 경우, 직렬 링크 인터페이스를 끊고 `SimDTM` 을 인스턴스화해야 합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/PeripheralDeviceConfigs.scala
    :language: scala
    :start-after: DOC include start: DmiRocket
    :end-before: DOC include end: DmiRocket

그런 다음 DMI 지원 최상위 레벨 및 테스트 하니스로 시뮬레이션을 실행할 수 있습니다.

.. code-block:: bash

    cd sims/verilator
    # 또는
    cd sims/vcs

    make CONFIG=dmiRocketConfig run-asm-tests

Using the JTAG Interface
------------------------

DUT와 인터페이스하는 또 다른 방법은 JTAG을 사용하는 것입니다.
:ref:`Advanced-Concepts/Chip-Communication:Using the Debug Module interface (DMI)` 섹션과 유사하게, JTAG 프로토콜을 사용하려면
DUT에 JTAG 대신 DMI를 사용하도록 구성된 Debug Transfer Module (DTM)이 포함되어 있어야 합니다.
JTAG 포트가 노출되면 호스트는 시뮬레이션 스텁인 ``SimJTAG``(C++ 클래스)를 통해 JTAG을 통해 DUT와 통신할 수 있습니다.
이 시뮬레이션 스텁은 시뮬레이션이 실행되는 동안 OpenOCD와 GDB가 연결할 수 있는 소켓을 생성합니다.
기본 Chipyard 디자인은 JTAG을 사용하도록 구성된 DTM을 인스턴스화합니다(예: ``RocketConfig`` ).

.. note::
    기본 Chipyard 디자인은 JTAG을 사용하도록 활성화되어 있습니다.
    그러나 JTAG 인터페이스를 사용하지 않는 경우 FESVR과 함께 TSI/Serialized-TL을 사용합니다.
    이를 통해 DUT와의 통신 방법을 선택할 수 있습니다(TSI 또는 JTAG 사용).

Debugging with JTAG
~~~~~~~~~~~~~~~~~~~

JTAG을 사용하여 디버그하는 대략적인 단계는 다음과 같습니다:

1. Chipyard JTAG 지원 RTL 디자인을 빌드합니다. 기본 Chipyard 디자인은 JTAG 준비가 되어 있음을 기억하십시오.

.. code-block:: bash

    cd sims/verilator
    # 또는
    cd sims/vcs

    make CONFIG=RocketConfig

2. 원격 비트-뱅 기능이 활성화된 시뮬레이션을 실행합니다. JTAG을 사용하여 바이너리를 로드/실행하려고 하므로,
   ``none`` 을 바이너리로 전달할 수 있습니다(FESVR이 프로그램을 로드하지 못하도록 방지). (출처: https://github.com/chipsalliance/rocket-chip#3-launch-the-emulator)

.. code-block:: bash

    # 참고: 이것은 시뮬레이션 인수를 올바르게 래핑하기 위해 Chipyard 메이크 호출을 사용하여 시뮬레이션을 실행합니다.
    make CONFIG=RocketConfig BINARY=none SIM_FLAGS="+jtag_rbb_enable=1 --rbb-port=9823" run-binary

3. OpenOCD + GDB를 사용하여 시뮬레이션에 연결하려면 `여기 <https://github.com/chipsalliance/rocket-chip#4-launch-openocd>`__ 의 지침을 따르십시오.

.. note::
    이 섹션은 Rocket Chip 및 riscv-isa-sim의 지침을 기반으로 작성되었습니다. 자세한 내용은 해당 문서를 참조하십시오: `Rocket Chip GDB Docs <https://github.com/chipsalliance/rocket-chip#-debugging-with-gdb>`__,
    `riscv-isa-sim GDB Docs <https://github.com/riscv/riscv-isa-sim#debugging-with-gdb>`__

Example Test Chip Bringup Communication
---------------------------------------

Intro to Typical Chipyard Test Chip
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

대부분의 Chipyard 구성은 TSI(직렬 링크를 통해)를 사용하여 테더링되며,
AXI 포트(백업 AXI 메모리)를 통해 외부 메모리에 접근할 수 있습니다.
다음 이미지는 이러한 기본 신호 세트를 가진 DUT를 보여줍니다:

.. image:: ../_static/images/default-chipyard-config-communication.png

이 설정에서 직렬 링크는 TSI/FESVR 주변 장치에 연결되며, AXI 포트는 시뮬레이션된 AXI 메모리에 연결됩니다.
그러나 AXI 포트는 많은 신호와 관련된 와이어가 있기 때문에 AXI 포트를 DUT에서 직접 생성하는 대신,
메모리 트랜잭션을 양방향 직렬 링크(``TLSerdesser``)를 통해 전송할 수 있습니다.
이를 통해 DUT와의 주요 인터페이스는 AXI 포트에 비해 상대적으로 신호가 적은 직렬 링크가 됩니다.
이 새로운 설정(아래에 표시됨)은 일반적인 Chipyard 테스트 칩 설정입니다:

.. image:: ../_static/images/bringup-chipyard-config-communication.png

Simulation Setup of the Example Test Chip
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

표준 테스트 칩 부팅 절차는 직렬화된 TileLink로 FPGA 구성에 칩을 테더링합니다.

.. image:: ../_static/images/chip-bringup-simulation.png

전체 부팅 절차는 Multi-ChipTop 시뮬레이션 기능을 사용하여 시뮬레이션할 수 있습니다.
여기에서 하나의 ``ChipTop`` 은 테이프아웃할 디자인이고, 다른 하나는 FPGA 부팅 디자인입니다.

이 시스템은 ``ChipLikeRocketConfig`` (테이프아웃할 디자인)과 ``ChipBringupHostConfig`` (FPGA 부팅 디자인)를 결합한 다음 예제 구성으로 생성하고 시뮬레이션할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/ChipConfigs.scala
    :language: scala
    :start-after: DOC include start: TetheredChipLikeRocketConfig
    :end-before: DOC include end: TetheredChipLikeRocketConfig

