.. _sky130-commercial-tutorial:

Sky130 Commercial Tutorial
==========================
이 리포지토리의 ``vlsi`` 폴더에는 Chipyard의 TinyRocketConfig를 사용한 Hammer 흐름의 예제가 포함되어 있습니다. 이 예제 튜토리얼은 내장된 Sky130 기술 플러그인을 사용하며, DRC 및 LVS에 필요한 Mentor 도구 플러그인 하위 모듈에 대한 액세스가 필요합니다.

Project Structure
-----------------

이 예제에서는 권장되는 파일 구조와 빌드 시스템을 제공합니다. ``vlsi/`` 폴더에는 다음과 같은 파일과 폴더가 포함될 것입니다:

* ``Makefile``, ``sim.mk``, ``power.mk``

  * Hammer의 빌드 시스템을 Chipyard에 통합하고 일부 Hammer 명령을 추상화합니다.

* ``build``

  * Hammer 출력 디렉토리입니다. ``OBJ_DIR`` 변수로 변경할 수 있습니다.
  * ``syn-rundir`` 및 ``par-rundir`` 와 같은 하위 디렉토리와 상위 모듈 및 입력 Verilog 파일을 나타내는 ``inputs.yml`` 이 포함됩니다.

* ``env.yml``

  * 도구 환경 구성을 위한 템플릿 파일입니다. 설치 경로와 라이선스 서버 경로를 환경에 맞게 채우십시오. SLICE 및 BWRC 회원의 경우, 예제 환경 설정은 `여기 <https://github.com/ucb-bar/hammer/tree/master/e2e/env>`__ 에서 찾을 수 있습니다.

* ``example-vlsi-sky130``

  * Hammer의 진입점입니다. 훅을 위한 예제 플레이스홀더가 포함되어 있습니다.

* ``example-sky130.yml``, ``example-tools.yml``, ``example-designs/sky130-commercial.yml``

  * 이 튜토리얼에 사용되는 Hammer IR입니다. SLICE 및 BWRC 회원의 경우, 예제 ASAP7 설정은 `여기 <https://github.com/ucb-bar/hammer/tree/master/e2e/pdks>`__ 에서 찾을 수 있습니다.

* ``example-design.yml``, ``example-asap7.yml``, ``example-tech.yml``

  * 이 튜토리얼에서 사용되지 않지만 템플릿으로 제공되는 Hammer IR입니다.

* ``generated-src``

  * 모든 정교화된 Chisel 및 FIRRTL이 포함됩니다.

* ``hammer-<vendor>-plugins``

  * 도구 플러그인 리포지토리입니다.

Prerequisites
-------------

* Python 3.9+
* Genus, Innovus, Voltus, VCS, 및 Calibre 라이선스
* Sky130A PDK, 설치 방법: `conda 사용 <https://anaconda.org/litex-hub/open_pdks.sky130a>`__ 또는 `이 지침 <https://github.com/ucb-bar/hammer/blob/master/hammer/technology/sky130>`__ 을 따르십시오.
* `Sram22 Sky130 SRAM 매크로 <https://github.com/rahulk29/sram22_sky130_macros>`__

  * 이 SRAM 매크로는 `Sram22 SRAM 생성기 <https://github.com/rahulk29/sram22>`__ (아직 개발 중)으로 생성되었습니다.

Quick Prerequisite Setup
^^^^^^^^^^^^^^^^^^^^^^^^
최근 Sky130A PDK는 conda를 통해 설치할 수 있게 되었습니다.
이 튜토리얼의 필수 구성 요소 설정은 스크립트로 작성될 예정이지만, 현재는 설정 지침이 아래에 제공됩니다.

.. code-block:: shell

    # Sky130A PDK의 모든 파일 다운로드
    conda create -c litex-hub --prefix ~/.conda-sky130 open_pdks.sky130a=1.0.457_0_g32e8f23
    # SRAM22 Sky130 SRAM 매크로 클론
    git clone https://github.com/rahulk29/sram22_sky130_macros ~/sram22_sky130_macros


Initial Setup
-------------
Chipyard 루트에서, Chipyard conda 환경이 활성화되어 있는지 확인하십시오. 그런 다음:

.. code-block:: shell

    ./scripts/init-vlsi.sh sky130

를 실행하여 플러그인 하위 모듈을 가져오고 설치하십시오. ``sky130`` 또는 ``asap7`` 이외의 기술에 대해서는 먼저 ``vlsi`` 폴더에 기술 하위 모듈을 추가해야 합니다.

이제 ``vlsi`` 디렉토리로 이동하십시오. 튜토리얼의 나머지 부분은 이 디렉토리에 있다고 가정합니다.
튜토리얼의 나머지 부분에서 중요한 몇 가지 파일을 요약하겠습니다.

.. code-block:: shell

    cd ~chipyard/vlsi

example-vlsi-sky130
^^^^^^^^^^^^^^^^^^^
이것은 훅에 대한 플레이스홀더가 포함된 진입 스크립트입니다. ``ExampleDriver`` 클래스에서, 훅의 목록이 ``get_extra_par_hooks`` 에 전달됩니다. 훅은 Hammer API를 확장하기 위한 추가적인 Python 및 TCL 코드 조각입니다(``x.append()`` 를 통해). 이 예제에서 보여지는 것처럼 ``make_pre/post/replacement_hook`` 메서드를 사용하여 훅을 삽입할 수 있습니다. 훅을 VLSI 흐름에 주입하는 방법에 대한 자세한 설명은 Hammer 문서를 참조하십시오.

example-sky130.yml
^^^^^^^^^^^^^^^^^^
이 파일은 이 예제 프로젝트를 위한 Hammer 설정을 포함합니다. 예제 클럭 제약, 전원 스트랩 정의, 배치 제약 및 핀 제약이 제공됩니다. 추가 라이브러리 및 도구에 대한 설정은 하단에 있습니다.

Sky130A PDK 및 SRAM 매크로의 위치를 지정하기 위해 이 파일 상단에 다음 YAML 키를 추가하십시오.

.. code-block:: yaml

    # 모든 ~는 이 디렉토리의 절대 경로로 대체되어야 합니다.
    # 기술 경로
    technology.sky130.sky130A: ~/.conda-sky130/share/pdk/sky130A
    technology.sky130.sram22_sky130_macros: ~/sram22_sky130_macros


example-tools.yml
^^^^^^^^^^^^^^^^^
이 파일은 상용 도구 흐름에 대한 Hammer 설정을 포함합니다.
합성(Cadence Genus), 배치 및 라우팅(Cadence Innovus), DRC 및 LVS(Mentor Calibre) 도구를 선택합니다.


Building the Design
--------------------
``TinyRocketConfig`` 을 정교화하고 설계 및 SRAM 매크로를 흐름을 통해 푸시하기 위한 모든 필수 조건을 설정하려면:

.. code-block:: shell

    make buildfile tutorial=sky130-commercial

``make buildfile`` 명령은 ``build/hammer.d`` 에 Make 타겟 집합을 생성합니다.
환경 변수가 변경된 경우 다시 실행해야 합니다.
이 변수들을 셸 환경으로 내보내는 대신 Makefile에서 직접 수정하는 것이 좋습니다.

``buildfile`` Make 타겟은 (1) 모든 Chisel 소스에서 정교화된 Verilog와 (2) 설계의 메모리 인스턴스를 SRAM 매크로로 매핑하는 것에 대한 종속성을 가집니다;
이 두 단계와 관련된 모든 파일은 ``generated-src/chipyard.harness.TestHarness.TinyRocketConfig-ChipTop`` 디렉토리에 위치합니다.
참고로, ``generated-src`` 의 파일은 각 도구/기술 흐름에 따라 다릅니다.
특히 Sky130 상용 버전과 OpenROAD 튜토리얼 흐름에서 다르므로, 이 흐름들은 별도의 Chipyard 설치에서 실행해야 합니다.
잘못된 소스가 생성된 경우, ``make buildfile -B`` 를 실행하여 모든 타겟을 올바르게 다시 빌드하십시오.

간결함을 위해, 이 튜토리얼에서는 ``tutorial=sky130-commercial`` Make 변수를 설정하여 ``tutorial.mk`` 에서 몇 가지 추가 변수를 설정하도록 하겠습니다. 그 중 일부는 다음과 같습니다:

* ``CONFIG=TinyRocketConfig`` 는 나머지 Chipyard 프레임워크와 동일한 방식으로 타겟 생성기 구성을 선택합니다. 도구 실행 시간을 최소화하기 위해 축소된 Rocket Chip을 정교화합니다.
* ``tech_name=sky130`` 는 ``Makefile`` 에서 적절한 Hammer 플러그인 등 몇 가지 필수 경로를 설정합니다.
* ``TOOLS_CONF`` 및 ``TECH_CONF`` 는 위에서 설명한 ``example-tools.yml`` 및 ``example-sky130.yml`` 설정 파일을 선택합니다.
* ``DESIGN_CONF`` 및 ``EXTRA_CONFS`` 는 ``example-sky130.yml`` 에서 Hammer IR의 추가적인 디자인별 오버라이드를 허용합니다.
* ``VLSI_OBJ_DIR=build-sky130-commercial``는 빌드 디렉토리에 고유한 이름을 부여하여 동일한 리포지토리에서 여러 흐름을 실행할 수 있도록 합니다. 나머지 튜토리얼에서는 간결함을 위해 이 디렉토리를 여전히 ``build`` 로 참조하겠습니다.
* ``VLSI_TOP`` 은 기본적으로 ``ChipTop`` 으로 설정되며, 이는 Chipyard SoC 구성에서 생성된 최상위 Verilog 모듈의 이름입니다. 대신 ``VLSI_TOP=Rocket`` 을 설정하면, 이 튜토리얼을 빠르게 실행할 수 있으며, SRAM에 의존하지 않는 단일 RISC-V 코어(캐시, 주변장치, 버스 등 없음)로 구성된 Rocket 코어를 최상위 모듈로 사용할 수 있습니다.

Running the VLSI Flow
---------------------

Synthesis
^^^^^^^^^
.. code-block:: shell

    make syn tutorial=sky130-commercial

합성 후 로그와 관련 자료는 ``build/syn-rundir`` 에 있습니다. 품질 결과 데이터는 ``build/syn-rundir/reports`` 에서 확인할 수 있으며, 설계 공간 탐색을 위한 정보 추출 방법이 진행 중입니다.

Place-and-Route
^^^^^^^^^^^^^^^
.. code-block:: shell

    make par tutorial=sky130-commercial

완료 후, 최종 데이터베이스는 ``./build/par-rundir/generated-scripts/open_chip`` 을 통해 상호작용하는 Innovus 세션에서 열 수 있습니다.

중간 데이터베이스는 ``par`` 작업의 각 단계 사이에 ``build/par-rundir`` 에 작성되며, 디버깅 목적으로 원하는 경우 상호작용하는 Innovus 세션에서 복원할 수 있습니다.

타이밍 보고서는 ``build/par-rundir/timingReports`` 에 있습니다. 이들은 gzip으로 압축된 텍스트 파일입니다.

DRC & LVS
^^^^^^^^^
DRC 및 LVS를 실행하고 Calibre에서 결과를 보려면:

.. code-block:: shell

    make drc tutorial=sky130-commercial
    ./build/chipyard.harness.TestHarness.TinyRocketConfig-ChipTop/drc-rundir/generated-scripts/view_drc
    make lvs tutorial=sky130-commercial
    ./build/chipyard.harness.TestHarness.TinyRocketConfig-ChipTop/lvs-rundir/generated-scripts/view_lvs

이 PDK에서 일부 DRC 오류는 예상됩니다, 특히 SRAM과 관련하여, 이는
`Sky130 Hammer 플러그인 README <https://github.com/ucb-bar/hammer/blob/master/hammer/technology/sky130>`__ 에서 설명된 바와 같이 입니다.
이러한 이유로, ``example-vlsi-sky130`` 스크립트는 DRC/LVS 분석을 위해 SRAM을 블랙박스로 처리합니다.

Simulation
^^^^^^^^^^
VCS를 사용한 시뮬레이션이 지원되며, RTL 또는 게이트 수준(합성 후 및 P&R 후)에서 실행할 수 있습니다. 여기 포함된 시뮬레이션 인프라는 Chipyard 설정에서 RISC-V 바이너리를 실행하는 데 사용됩니다. 예를 들어, RTL 수준 시뮬레이션의 경우:

.. code-block:: shell

    make sim-rtl tutorial=sky130-commercial BINARY=$RISCV/riscv64-unknown-elf/share/riscv-tests/isa/rv64ui-p-simple

합성 후 및 P&R 후 시뮬레이션은 각각 ``sim-syn`` 및 ``sim-par`` make 타겟을 사용합니다.

이러한 make 타겟에 ``-debug`` 및 ``-debug-timing``을 추가하면 VCS가 SAIF + FSDB(또는 ``USE_VPD`` 플래그가 설정된 경우 VPD)를 작성하고, 타이밍 주석이 있는 시뮬레이션을 실행합니다. 사용 가능한 모든 타겟은 ``sim.mk`` 파일에서 확인할 수 있습니다.

Power/Rail Analysis
^^^^^^^^^^^^^^^^^^^
Voltus를 사용한 P&R 후 전력 및 레일(IR 드롭) 분석이 지원됩니다:

.. code-block:: shell

    make power-par tutorial=sky130-commercial

명령에 ``BINARY`` 변수를 추가하면 ``sim-<syn/par>-debug`` 실행에서 생성된 활동 파일을 사용하여, 파형에 인코딩된 토글로부터 동적 전력 및 IR 드롭을 보고합니다.

게이트 수준 시뮬레이션을 우회하려면 전력 도구를 수동으로 실행해야 합니다(생성된 ``hammer.d`` 빌드 파일에 생성된 명령을 참조). 정적 및 활성(벡터리스) 전력 및 IR 드롭이 보고됩니다.


VLSI Flow Control
^^^^^^^^^^^^^^^^^
먼저, :ref:`VLSI/Hammer:VLSI Flow Control` 문서를 참조하십시오. 아래 예제는 ``redo-par`` Make 타겟을 사용하여 배치 및 라우팅만 다시 실행하는 방법을 보여줍니다. ``redo-`` 는 VLSI 흐름 작업 중 해당 작업만 다시 실행하도록 할 수 있습니다.

.. code-block:: shell

      # 다음 두 명령은 동일합니다. 왜냐하면
      #   추출 단계가 디자인 쓰기 단계 직전에 실행되기 때문입니다.
      make redo-par HAMMER_EXTRA_ARGS="--start_after_step extraction"
      make redo-par HAMMER_EXTRA_ARGS="--start_before_step write_design"

      # 새로운 플로어플랜 구성을 테스트하기 위해 플로어플래닝만 다시 실행하는 예입니다.
      #   "-p file.yml"은 file.yml이 이전의 모든 yaml/json 구성을 무시하도록 만듭니다.
      make redo-par \
        HAMMER_EXTRA_ARGS="--only_step floorplan_design -p example-designs/sky130-openroad.yml"

