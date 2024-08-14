Running a Design on Nexys Video
===============================

Nexys Video Instructions
------------------------

기본 Digilent Nexys Video 하네스는 FPGA를 부팅하기 위해 TSI-over-UART 어댑터를 사용합니다.
사용자는 ``uart_tsi`` 프로그램을 사용하여 UART TTY를 열어 Nexys Video 타겟에 연결할 수 있습니다.
``uart_tsi`` 프로그램의 인터페이스는 테스트 칩을 부팅하는 데 유용한 고유 기능을 제공합니다.

디자인을 빌드하려면 (Vivado가 ``PATH`` 에 추가되어 있어야 함), 다음을 실행하십시오:

.. code-block:: shell

		cd fpga/
		make SUB_PROJECT=nexysvideo bitstream

UART 기반 프론트엔드 서버를 빌드하려면 다음을 실행하십시오:

.. code-block:: shell

		cd generators/testchipip/uart_tsi
		make

비트스트림을 프로그래밍하고, Nexys Video의 UART를 USB 케이블을 통해 호스트 PC에 연결한 후, ``uart_tsi`` 프로그램을 실행하여 타겟과 상호작용할 수 있습니다.

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
