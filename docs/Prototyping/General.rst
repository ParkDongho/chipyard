General Setup and Usage
==============================

Sources
-------

모든 FPGA 프로토타이핑 관련 자료와 소스는 Chipyard의 최상위 디렉토리인 ``fpga`` 에 위치해 있습니다.
여기에는 ``fpga-shells`` 서브모듈과 Scala, TCL 및 기타 자료를 포함한 ``src`` 디렉토리가 포함됩니다.

Generating a Bitstream
----------------------

Vivado를 사용하여 FPGA 타겟을 위한 비트스트림을 생성하는 것은 소프트웨어 RTL 시뮬레이션을 위한 RTL을 빌드하는 것과 유사합니다.
소프트웨어 RTL 시뮬레이션과 유사하게 (:ref:`Simulation/Software-RTL-Simulation:Simulating A Custom Project` ), Vivado를 사용하여 비트스트림을 빌드하기 위해 ``fpga`` 디렉토리에서 다음 명령어를 실행할 수 있습니다:

.. code-block:: shell

    make SBT_PROJECT=... MODEL=... VLOG_MODEL=... MODEL_PACKAGE=... CONFIG=... CONFIG_PACKAGE=... GENERATOR_PACKAGE=... TB=... TOP=... BOARD=... FPGA_BRAND=... bitstream

    # 또는

    make SUB_PROJECT=<sub_project> bitstream

``SUB_PROJECT`` make 변수는 다른 모든 make 변수들을 특정 기본값으로 설정하는 메타 make 변수입니다.
예를 들어:

.. code-block:: shell

    make SUB_PROJECT=vcu118 bitstream

    # 다음으로 변환됩니다

    make SBT_PROJECT=chipyard_fpga MODEL=VCU118FPGATestHarness VLOG_MODEL=VCU118FPGATestHarness MODEL_PACKAGE=chipyard.fpga.vcu118 CONFIG=RocketVCU118Config CONFIG_PACKAGE=chipyard.fpga.vcu118 GENERATOR_PACKAGE=chipyard TB=none TOP=ChipTop BOARD=vcu118 FPGA_BRAND=... bitstream

몇 가지 ``SUB_PROJECT`` 기본값이 이미 정의되어 있으며, ``vcu118`` 및 ``arty`` 를 포함합니다.
이 기본 ``SUB_PROJECT`` 는 Chipyard make 시스템에 필요한 테스트 하네스, 패키지 등을 설정합니다.
소프트웨어 RTL 시뮬레이션의 make 호출과 마찬가지로, 모든 make 변수는 사용자 정의 값으로 재정의할 수 있습니다 (예: ``SUB_PROJECT`` 를 포함하여 ``CONFIG`` 및 ``CONFIG_PACKAGE`` 를 재정의).
대부분의 경우, ``SUB_PROJECT`` 와 재정의된 ``CONFIG`` 를 가리키는 명령을 실행하기만 하면 됩니다.
예를 들어, VCU118에서 BOOM 구성을 빌드하려면:

.. code-block:: shell

    make SUB_PROJECT=vcu118 CONFIG=BoomVCU118Config bitstream

이 명령은 RTL을 빌드하고 Vivado를 사용하여 비트스트림을 생성합니다.
생성된 비트스트림은 디자인의 특정 빌드 폴더( ``generated-src/<LONG_NAME>/obj`` )에 위치하게 됩니다.
그러나 소프트웨어 RTL 시뮬레이션과 마찬가지로, 중간 make 단계를 실행하여 Verilog 또는 FIRRTL만 생성할 수도 있습니다.

Debugging with ILAs on Supported FPGAs
--------------------------------------

ILA (Integrated Logic Analyzers)를 특정 설계에 추가하여 관련 신호를 디버깅할 수 있습니다.
먼저, Vivado에서 설계를 위한 빌드 디렉토리에 위치한 포스트 합성 체크포인트를 열어야 합니다 ( ``post_synth.dcp`` 로 표시되어야 함).
그런 다음 Vivado를 사용하여 설계에 ILA(및 기타 디버깅 도구)를 추가하십시오 (ILA를 추가하는 방법에 대한 자세한 내용은 온라인에서 검색하십시오).
이는 포스트 합성 체크포인트를 수정하고 저장한 후, ``make ... debug-bitstream`` 을 실행하여 수행할 수 있습니다.
그러면 ``generated-src/<LONG_NAME>/debug_obj/`` 라는 폴더에 ``top.bit`` 이라는 새 비트스트림이 생성됩니다.
예를 들어, BOOM 구성에 대한 ILA를 추가하여 비트스트림을 빌드하는 경우:

.. code-block:: shell

    make SUB_PROJECT=vcu118 CONFIG=BoomVCU118Config debug-bitstream

.. IMPORTANT:: FPGA 시뮬레이션을 위한 더 광범위한 디버깅 도구(printf 합성, assert 합성, 명령어 추적, ILA, 외부 프로파일링, 공동 시뮬레이션 등)에 대해서는 :ref:`Simulation/FPGA-Accelerated-Simulation:FireSim` 플랫폼을 참조하십시오.

