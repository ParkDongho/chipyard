Running a Design on Arty
========================

Arty100T Instructions
---------------------

기본 Digilent Arty A7-100T 하네스는 FPGA를 부팅하기 위해 TSI-over-UART 어댑터를 사용합니다.
사용자는 ``uart_tsi`` 프로그램을 사용하여 UART TTY를 열어 Arty A7-100T 타겟에 연결할 수 있습니다.
``uart_tsi`` 프로그램의 인터페이스는 테스트 칩을 부팅하는 데 유용한 고유 기능을 제공합니다.

디자인을 빌드하려면 (Vivado가 ``PATH`` 에 추가되어 있어야 함), 다음을 실행하십시오:

.. code-block:: shell

		cd fpga/
		make SUB_PROJECT=arty100t bitstream

UART 기반 프론트엔드 서버를 빌드하려면 다음을 실행하십시오:

.. code-block:: shell

		cd generators/testchipip/uart_tsi
		make

비트스트림을 프로그래밍하고, Arty의 UART를 USB 케이블을 통해 호스트 PC에 연결한 후, ``uart_tsi`` 프로그램을 실행하여 타겟과 상호작용할 수 있습니다.

프로그램 실행:

.. code-block:: shell

		./uart_tsi +tty=/dev/ttyUSBX dhrystone.riscv

타겟 시스템의 주소 탐색:

.. code-block:: shell

		./uart_tsi +tty=/dev/ttyUSBX +init_read=0x10000 none

프로그램 실행 전에 일부 주소에 기록:

.. code-block:: shell

		./uart_tsi +tty=/dev/ttyUSBX +init_write=0x80000000:0xdeadbeef none

바이너리 로딩이 올바르게 진행되었는지 자체 점검:

.. code-block:: shell

		./uart_tsi +tty=/dev/ttyUSBX +selfcheck dhrystone.riscv

기본값보다 높은 전송 속도로 디자인 실행 (예: ``CONFIG=UART921600RocketArty100TConfig`` 가 빌드된 경우):

.. code-block:: shell

		./uart_tsi +tty=/dev/ttyUSBX +baudrate=921600 dhrystone.riscv


Arty35T Legacy Instructions
---------------------------

기본 Digilent Arty A7-35T 하네스는 보드의 PMOD 핀을 통해 JTAG를 사용할 수 있도록 설정되어 있으며, UART는 FTDI 시리얼 USB 어댑터를 통해 사용할 수 있습니다. JTAG 신호의 핀 매핑은 `SiFive Freedom E310 Arty 35T Getting Started Guide <https://static.dev.sifive.com/SiFive-E310-arty-gettingstarted-v1.0.6.pdf>`__ 에서 설명된 것과 동일합니다.
JTAG 인터페이스를 통해 사용자는 OpenOCD를 통해 코어에 연결하고 베어 메탈 애플리케이션을 실행하고 gdb를 사용하여 이 애플리케이션을 디버깅할 수 있습니다. UART는 USB 연결을 통해 코어와 통신하고 PC에서 실행되는 시리얼 콘솔을 사용할 수 있습니다.
이 디자인을 확장하려면 사용자는 자신만의 Chipyard 구성을 생성하고 ``fpga/src/main/scala/arty/Configs.scala`` 에 있는 ``WithArtyTweaks`` 를 추가할 수 있습니다.
이 구성 조각을 추가하면 JTAG 및 UART 인터페이스가 Chipyard 디자인에 연결되고 활성화됩니다.

.. literalinclude:: ../../fpga/src/main/scala/arty/Configs.scala
    :language: scala
    :start-after: DOC include start: AbstractArty and Rocket
    :end-before: DOC include end: AbstractArty and Rocket

향후 지원될 주변 장치에는 Arty A7-35T SPI Flash EEPROM 및 Arty A7-35T GPIO 핀을 통한 I2C/PWM/SPI가 포함됩니다. 이러한 주변 장치는 sifive-blocks의 일부로 제공됩니다.

Brief Implementation Description and Guidance for Adding/Changing Xilinx Collateral
-----------------------------------------------------------------------------------

VCU118와 마찬가지로, Arty A7-35T 디자인의 기본은 외부 IO(이는 Xilinx IP 블랙박스로 존재함)를 Chipyard 디자인에 연결하는 특수 테스트 하네스를 만드는 것입니다.
이는 기본 Arty A7-35T 타겟에서 ``ArtyTestHarness``를 사용하여 수행됩니다. 그러나 ``VCU118TestHarness`` 와 달리, ``ArtyTestHarness`` 는 ``Overlays`` 를 사용하지 않고, 대신 ``fpga-shells`` 에서 제공하는 ``IOBUF`` 와 같은 기능을 사용하여 칩 상단 IO를 외부 IO 블랙박스의 포트에 직접 연결합니다.
VCU118 및 다른 더 복잡한 테스트 하네스와 달리, Arty A7-35T Vivado 부수 자료는 ``Overlays`` 에 의해 생성되지 않으며, 대신 ``create_ip`` 및 ``set_properties`` 문장의 정적 모음으로 구성됩니다. 이 문장은 ``fpga/fpga-shells/xilinx/arty/tcl`` 및 ``fpga/fpga-shells/xilinx/arty/constraints`` 내의 파일에 위치합니다.
사용자가 FPGA 패키지 핀을 다른 하네스 레벨 IO로 다시 매핑하려는 경우, 이는 ``fpga/fpga-shells/xilinx/arty/constraints/arty-master.xdc`` 내에서 변경할 수 있습니다. 새로운 Xilinx IP 블록을 추가하려면 ``fpga-shells/xilinx/arty/tcl/ip.tcl`` 에서 수행할 수 있으며, 하네스 레벨 IO에 매핑되고, ``arty-master.xdc`` 에서 하네스에 연결되며, ``HarnessBinders`` 및 ``IOBinders`` 를 사용하여 칩 상단으로 연결됩니다.
신호를 라우팅하기 위한 간단한 ``IOBinder`` 및 ``HarnessBinder`` 의 예(이 경우 디버그 및 JTAG 리셋)는 ``WithResetPassthrough`` 및 ``WithArtyResetHarnessBinder`` 입니다.

