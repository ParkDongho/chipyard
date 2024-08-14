Debugging RTL
======================

Chipyard에 기본으로 제공되는 구성(configs)과 RTL은 정상적으로 동작하도록 테스트되었습니다.
그러나 사용자는 종종 자신만의 IP를 추가하거나 기존의 Chisel 생성기를 수정하여 커스텀 칩을 만들고자 할 것입니다.
이러한 변경은 버그를 유발할 수 있습니다. 이 섹션은 Chipyard를 사용하여 일반적인 디버깅 절차를 설명하는 것을 목표로 합니다.
사용자가 커스텀 SoC 구성을 가지고 있으며, 이를 검증하기 위해 일부 소프트웨어 테스트를 실행하려고 한다고 가정합니다.
또한, 소프트웨어가 Spike나 QEMU와 같은 기능적 시뮬레이터에서 이미 검증되었다고 가정합니다.
이 섹션에서는 하드웨어 디버깅에 초점을 맞춥니다.

Waveforms
---------------------------

기본 소프트웨어 RTL 시뮬레이터는 실행 중에 웨이브폼을 덤프하지 않습니다.
웨이브 덤프 기능이 있는 시뮬레이터를 빌드하려면 ``debug`` 메이크 타겟을 사용해야 합니다. 예를 들어:

.. code-block:: shell

   make CONFIG=CustomConfig debug

``run-binary-debug`` 규칙은 시뮬레이터를 자동으로 빌드하고, 커스텀 바이너리를 실행하며, 웨이브폼을 생성합니다. 예를 들어,  ``helloworld.riscv`` 에서 테스트를 실행하려면 다음을 사용하십시오.

.. code-block:: shell

   make CONFIG=CustomConfig run-binary-debug BINARY=helloworld.riscv

VCS와 Verilator는 추가적인 여러 플래그를 지원합니다. 예를 들어, VCS에서 ``+vpdfilesize`` 플래그를 지정하면 출력 파일이 순환 버퍼로 처리되어 장기 실행 시뮬레이션의 디스크 공간을 절약할 수 있습니다.
추가 시뮬레이터 플래그를 설정하려면 ``SIM_FLAGS`` 메이크 변수를 사용할 수 있습니다:

.. code-block:: shell

   make CONFIG=CustomConfig run-binary-debug BINARY=linux.riscv SIM_FLAGS=+vpdfilesize=1024

.. note::
    여러 시뮬레이터 플래그가 있는 경우, ``SIM_FLAGS`` 를 다음과 같이 작성할 수 있습니다: ``SIM_FLAGS="+vpdfilesize=XYZ +some_other_flag=ABC"``.

Print Output
---------------------------

Rocket과 BOOM은 다양한 수준의 출력 프린트를 구성할 수 있습니다.
자세한 내용은 Rocket 코어 소스 코드 또는 BOOM `문서 <https://docs.boom-core.org/en/latest/>`__ 웹사이트를 참조하십시오.
또한, 개발자는 임의의 조건에서 Chisel 생성기 내에 임의의 printf를 삽입할 수 있습니다.
이와 관련된 자세한 내용은 Chisel 문서를 참조하십시오.

코어가 원하는 출력 문으로 구성된 후, ``+verbose`` 플래그를 사용하면 시뮬레이터가 해당 문을 출력합니다.
다음 명령어들은 모두 원하는 출력 문을 생성합니다:

.. code-block:: shell

   make CONFIG=CustomConfig run-binary-debug BINARY=helloworld.riscv

   # 아래 명령어는 동일한 작업을 수행합니다.
   ./simv-CustomConfig-debug +verbose helloworld.riscv

두 코어는 커밋 로그를 출력하도록 구성할 수 있으며, 이를 Spike 커밋 로그와 비교하여 정확성을 검증할 수 있습니다.

Basic tests
---------------------------

``riscv-tests`` 에는 기본 ISA 레벨 테스트와 기본 벤치마크가 포함되어 있습니다. 이 테스트들은 Chipyard CI에서 사용되며, 칩의 기능을 검증하는 첫 번째 단계로 사용해야 합니다. 메이크 규칙은 다음과 같습니다:

.. code-block:: shell

   make CONFIG=CustomConfig run-asm-tests run-bmark-tests


Torture tests
---------------------------
RISC-V torture 유틸리티는 무작위 RISC-V 어셈블리 스트림을 생성하고, 이를 컴파일한 후,
Spike 기능 모델과 소프트웨어 시뮬레이터에서 실행하여 동일한 프로그램 동작을 검증합니다.
torture 유틸리티는 스트레스 테스트를 위해 지속적으로 실행되도록 구성할 수도 있습니다. torture 유틸리티는 ``tools`` 디렉토리에 있습니다. torture 테스트를 실행하려면 시뮬레이션 디렉토리에서 ``make`` 를 실행하십시오:

.. code-block:: shell

  make CONFIG=CustomConfig torture

밤새 테스트(반복되는 무작위 테스트)를 실행하려면 다음을 실행하십시오:

.. code-block:: shell

  make CONFIG=CustomConfig TORTURE_ONIGHT_OPTIONS=<overnight options> torture-overnight

밤새 옵션은 torture 리포지토리의 `overnight/src/main/scala/main.scala` 에서 찾을 수 있습니다.

Firesim Debugging
---------------------------
Chisel printfs, assert, Cospike 공동 시뮬레이션, 파형 생성은 FireSim FPGA 가속 시뮬레이션에서도 사용할 수 있습니다.
자세한 내용은 FireSim `문서 <https://docs.fires.im/en/latest/>`__ 를 참조하십시오.
