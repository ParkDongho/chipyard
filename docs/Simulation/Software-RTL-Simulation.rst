.. _sw-rtl-sim-intro:

Software RTL Simulation
===================================

Verilator (Open-Source)
-----------------------

`Verilator <https://www.veripool.org/wiki/verilator>`__ 는 `Veripool <https://www.veripool.org/>`__ 에서 유지 관리하는 오픈 소스 LGPL 라이선스 시뮬레이터입니다.
Chipyard 프레임워크는 Verilator를 사용하여 시뮬레이션을 다운로드, 빌드 및 실행할 수 있습니다.


Synopsys VCS (License Required)
--------------------------------

`VCS <https://www.synopsys.com/verification/simulation/vcs.html>`__ 는 Synopsys에서 개발한 상용 RTL 시뮬레이터입니다.
상용 라이선스가 필요합니다.
Chipyard 프레임워크는 VCS를 사용하여 시뮬레이션을 컴파일하고 실행할 수 있습니다.
VCS 시뮬레이션은 일반적으로 Verilator 시뮬레이션보다 컴파일이 빠릅니다.

VCS 시뮬레이션을 실행하려면 VCS 시뮬레이터가 ``PATH`` 에 포함되어 있는지 확인하십시오.


Choice of Simulator
-------------------------------

먼저 Verilator 또는 VCS 디렉토리에 들어갑니다:

오픈 소스 Verilator 시뮬레이션을 위해 ``sims/verilator`` 디렉토리로 이동합니다.

.. code-block:: shell

    # Verilator 디렉토리로 이동
    cd sims/verilator

상용 VCS 시뮬레이션을 위해 ``sims/vcs`` 디렉토리로 이동합니다.

.. code-block:: shell

    # VCS 디렉토리로 이동
    cd sims/vcs

.. _sw-sim-help:

Simulating The Default Example
-------------------------------

예제 디자인을 컴파일하려면 선택한 Verilator 또는 VCS 디렉토리에서 ``make`` 를 실행합니다.
이 명령은 예제 프로젝트에서 ``RocketConfig`` 를 정교화(elaborate)합니다.

.. Note:: ``RocketConfig`` 를 정교화하려면 약 6.5GB의 메모리가 필요합니다. 그렇지 않으면 ``make: *** [firrtl_temp] Error 137`` 오류가 발생하여 프로세스가 실패할 수 있습니다. 다른 구성은 더 많은 메모리가 필요할 수 있습니다.

``simulator-chipyard.harness-RocketConfig`` 라는 실행 파일이 생성됩니다.
이 실행 파일은 빌드된 디자인을 기반으로 컴파일된 시뮬레이터입니다.
그런 다음 이 실행 파일을 사용하여 호환되는 RV64 코드를 실행할 수 있습니다.
예를 들어, riscv-tools 어셈블리 테스트 중 하나를 실행할 수 있습니다.

.. code-block:: shell

    ./simulator-chipyard.harness-RocketConfig $RISCV/riscv64-unknown-elf/share/riscv-tests/isa/rv64ui-p-simple

.. Note:: VCS 시뮬레이터에서는 시뮬레이터 이름이 ``simulator-chipyard.harness-RocketConfig`` 대신 ``simv-chipyard.harness-RocketConfig`` 가 됩니다.

Makefile에는 시뮬레이션 실행 파일을 실행하는 작업을 단순화하는 ``run-binary`` 규칙이 있습니다. 이 규칙은 많은 일반적인 명령줄 옵션을 추가하고 출력을 파일로 리디렉션합니다.

.. code-block:: shell

    make run-binary BINARY=$RISCV/riscv64-unknown-elf/share/riscv-tests/isa/rv64ui-p-simple

또는 ``run-asm-tests`` 또는 ``run-bmark-tests`` 라는 Makefile 타겟을 추가하여 사전 패키지된 RISC-V 어셈블리 또는 벤치마크 테스트를 실행할 수 있습니다.
예를 들어:

.. code-block:: shell

    make run-asm-tests
    make run-bmark-tests

.. Note:: 사전 패키지된 테스트를 실행하기 전에 단순 ``make`` 명령을 실행해야 합니다. 정교화 명령은 사전 패키지된 테스트 타겟을 포함하는 ``Makefile`` 조각을 생성하기 때문입니다. 그렇지 않으면 ``Makefile`` 타겟 오류가 발생할 수 있습니다.


.. _sw-sim-custom:

Custom Benchmarks/Tests
-------------------------------

Verilator/VCS 시뮬레이션에서 실행할 자체 베어메탈 코드를 컴파일하려면 Chipyard의 ``tests`` 디렉토리에 추가한 다음 이름을 ``Makefile`` 내의 ``PROGRAMS`` 목록에 추가하십시오. 이러한 바이너리는 베어메탈 바이너리를 위한 최소한의 유용한 시스템 호출을 구현하는 libgloss-htif 라이브러리로 컴파일됩니다. 그런 다음 ``make`` 를 실행하면 ``tests`` 디렉토리 내의 모든 프로그램이 ``.riscv`` ELF 바이너리로 컴파일되어 위에서 설명한 것처럼 시뮬레이터에서 사용할 수 있습니다.

.. code-block:: shell

    # Tests 디렉토리로 이동
    cd tests
    make

    # Verilator 또는 VCS 디렉토리로 이동
    cd ../sims/verilator
    make run-binary BINARY=../../tests/hello.riscv

.. Note:: 멀티코어 구성에서는 하트(하드웨어 스레드) 0만이 ``main()`` 함수를 실행합니다. 나머지 하트는 기본적으로 무한 루프에 들어가는 ``__main()`` 함수를 실행합니다. Verilator/VCS 시뮬레이션에서 멀티스레드 워크로드를 실행하려면 ``__main()`` 을 사용자 정의 코드로 재정의하십시오. 자세한 내용은 `여기 <https://github.com/ucb-bar/libgloss-htif>`_  에서 확인할 수 있습니다.


Makefile Variables and Commands
-------------------------------
Verilator 또는 VCS 디렉토리에서 사용할 수 있는 유용한 Makefile 변수 및 명령 목록을 얻으려면 ``make help`` 를 실행하십시오:

.. code-block:: shell

    # Verilator 디렉토리로 이동
    cd sims/verilator
    make help

    # VCS 디렉토리로 이동
    cd sims/vcs
    make help

.. _sim-default:

Simulating A Custom Project
-------------------------------

나중에 자체 프로젝트를 생성하는 경우 환경 변수를 사용하여 다른 구성을 빌드할 수 있습니다.

맞춤형 디자인으로 시뮬레이터를 구성하려면 시뮬레이터 디렉토리 내에서 다음 명령을 실행합니다:

.. code-block:: shell

    make SBT_PROJECT=... MODEL=... VLOG_MODEL=... MODEL_PACKAGE=... CONFIG=... CONFIG_PACKAGE=... GENERATOR_PACKAGE=... TB=... TOP=...

이들 각각의 make 변수는 디자인/코드베이스의 특정 부분과 관련이 있으며, 올바르게 빌드하고 RTL 시뮬레이션을 만들기 위해 필요합니다.

``SBT_PROJECT`` 는 모든 소스 파일을 포함하고 RTL 빌드 중에 실행될 ``build.sbt`` 프로젝트입니다.

``MODEL`` 및 ``VLOG_MODEL`` 은 디자인의 최상위 클래스 이름입니다. 일반적으로 이들은 동일하지만, 어떤 경우에는(Chisel 클래스가 Verilog에서 내보내진 것과 다른 경우) 다를 수 있습니다.

``MODEL_PACKAGE`` 는 ``MODEL`` 클래스를 포함하는 Scala 패키지입니다(Scala 코드에서 ``package ...`` 라고 명시된 것).

``CONFIG`` 는 매개변수 구성을 위한 클래스의 이름이며, ``CONFIG_PACKAGE`` 는 이 클래스가 위치한 Scala 패키지입니다.

``GENERATOR_PACKAGE`` 는 디자인을 정교화하는 Generator 클래스를 포함하는 Scala 패키지입니다.

``TB`` 는 VCS/Verilator에서 시뮬레이션을 위해 ``TestHarness`` 에 연결하는 Verilog 래퍼의 이름입니다.

마지막으로 ``TOP`` 변수는 시스템에서 디자인의 최상위 레벨과 ``TestHarness`` 를 구분하는 데 사용됩니다.
예를 들어, 일반적인 경우 ``MODEL`` 변수는 디자인의 최상위 레벨로 ``TestHarness`` 를 지정합니다.
그러나 시뮬레이션되는 SoC인 실제 최상위 디자인은 ``TOP`` 변수로 지정됩니다.
이 구분을 통해 인프라가 하네스 또는 SoC 최상위 레벨을 기반으로 파일을 분리할 수 있습니다.

이 변수들의 모든 일반적인 구성은 ``SUB_PROJECT`` make 변수를 사용하여 패키지됩니다.
따라서 Rocket 기반의 간단한 예제 시스템을 시뮬레이션하려면 다음과 같이 사용할 수 있습니다:


.. code-block:: shell

    make SUB_PROJECT=yourproject
    ./simulator-<yourproject>-<yourconfig> ...


마지막으로, ``generated-src/<...>-<package>-<config>/`` 디렉토리에는 모든 자료가 있으며, 생성된 Verilog 소스 파일은 빌드/시뮬레이션을 위한 ``generated-src/<...>-<package>-<config>/gen-collateral`` 에 있습니다.
특히 ``CONFIG=RocketConfig`` 의 경우 SoC 최상위 레벨(TOP) Verilog 파일은 ``ChipTop.sv`` 이며, (Model) 파일은 ``TestHarness.sv`` 입니다.

Fast Memory Loading
-------------------

시뮬레이터는 프로그램 바이

너리를 시뮬레이션된 시리얼 라인을 통해 로드합니다. 정적 데이터가 많은 경우 이 과정이 매우 느릴 수 있으므로, 시뮬레이터는 데이터를 파일에서 직접 DRAM 모델로 로드할 수도 있습니다.
Loadmem 파일은 ELF 파일이어야 합니다. 가장 일반적인 사용 사례에서는 바이너리가 될 수 있습니다.

.. code-block:: shell

    make run-binary BINARY=test.riscv LOADMEM=test.riscv

보통 ``LOADMEM`` ELF는 ``BINARY`` ELF와 동일하므로 ``LOADMEM=1`` 을 바로가기처럼 사용할 수 있습니다.

.. code-block:: shell

   make run-binary BINARY=test.riscv LOADMEM=1

Generating Waveforms
-----------------------

시뮬레이션에서 웨이브폼을 추출하려면 ``make`` 대신 ``make debug`` 명령을 실행하십시오.

특정 테스트에 대한 웨이브폼 파일을 자동으로 생성하는 특수 타겟도 사용할 수 있습니다.

.. code-block:: shell

    make run-binary-debug BINARY=test.riscv

Verilator 시뮬레이션의 경우, 이는 vcd 파일을 생성하며(vcd는 표준 웨이브폼 표현 파일 형식), 이를 모든 일반적인 웨이브폼 뷰어에 로드할 수 있습니다.
오픈 소스 vcd 지원 웨이브폼 뷰어는 `GTKWave <http://gtkwave.sourceforge.net/>`__ 입니다.

VCS 시뮬레이션의 경우, 이는 fsdb 파일을 생성하며, fsdb 지원 웨이브폼 뷰어에 로드할 수 있습니다.
Synopsys 라이선스가 있는 경우 Verdi 웨이브폼 뷰어를 사용하는 것이 좋습니다.

Visualizing Chipyard SoCs
--------------------------

Verilog 생성 중에 Diplomacy 그래프로 Chipyard SoC를 시각화할 수 있는 graphml 파일이 생성됩니다.

그래프를 보려면 먼저 `yEd <https://www.yworks.com/products/yed/>`__ 와 같은 뷰어를 다운로드하십시오.

``*.graphml`` 파일은 ``generated-src/<...>/`` 에 위치해 있습니다. 그래프 뷰어에서 파일을 엽니다.
SoC를 더 명확하게 보기 위해 "계층적" 보기로 전환하십시오. yEd의 경우, ``layout`` -> ``hierarchical`` 를 선택한 다음 설정을 변경하지 않고 "Ok"를 선택하면 됩니다.

.. _sw-sim-verilator-opts:

Additional Verilator Options
-------------------------------

Verilator 시뮬레이터를 빌드할 때 몇 가지 추가 옵션이 있습니다:

.. code-block:: shell

   make VERILATOR_THREADS=8 NUMACTL=1

``VERILATOR_THREADS=<num>`` 옵션은 컴파일된 Verilator 시뮬레이터가 ``<num>`` 개의 병렬 스레드를 사용하도록 합니다.
멀티 소켓 머신에서는 ``NUMACTL=1`` 을 사용하여 모든 스레드가 동일한 소켓에 있는지 확인하십시오.
이렇게 하면 Chipyard의 ``numa_prefix`` 래퍼가 사용되며, 이는 간단한 numactl 래퍼로, Verilator 시뮬레이터를 다음과 같이 실행합니다: ``$(numa_prefix) ./simulator-<name> <simulator-args>``.
이 두 플래그는 상호 배타적이며, 둘 중 하나만 독립적으로 사용할 수 있습니다(Verilator 시뮬레이션 중에는 ``VERILATOR_THREADS=8`` 과 함께 ``NUMACTL`` 를 사용하는 것이 합리적입니다).

Speeding up your RTL Simulation by 2x!
-----------------------------------------------

사용자 정의 모듈이 Tilelink와 인터페이스하는 경우(e.g., 사용자 정의 가속기를 작성할 때), 잘못된 Tilelink 인터페이스는 SoC가 멈추게 할 수 있으며 디버그하기 까다로울 수 있습니다.
이러한 상황을 처리하기 위해 잘못된 Tilelink 메시지가 전송될 때 어설션을 트리거하는 Tilelink 모니터라는 하드웨어 모듈을 SoC에 추가할 수 있습니다.
그러나 이러한 모듈은 RTL 시뮬레이션 속도를 크게 저하시킬 수 있습니다.

이 모듈들은 기본적으로 SoC에 추가되며, 사용자는 다음 줄을 구성에 추가하여 이 모듈들을 수동으로 제거해야 합니다.

.. code-block:: scala

  new freechips.rocketchip.subsystem.WithoutTLMonitors ++


예를 들어:

.. code-block:: scala

  class FastRTLSimRocketConfig extends Config(
    new freechips.rocketchip.subsystem.WithoutTLMonitors ++
    new chipyard.RocketConfig)
