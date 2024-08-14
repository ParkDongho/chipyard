.. _sky130-openroad-tutorial:

Sky130 + OpenROAD Tutorial
==========================
이 리포지토리의 ``vlsi`` 폴더에는 Chipyard의 TinyRocketConfig를 사용한 Hammer 흐름의 예제가 포함되어 있습니다. 이 예제 튜토리얼은 내장된 Sky130 기술 플러그인과 OpenROAD 도구 플러그인을 사용합니다.

Project Structure
-----------------

이 예제에서는 권장하는 파일 구조와 빌드 시스템을 제공합니다. ``vlsi/`` 폴더에는 다음과 같은 파일과 폴더가 포함됩니다:

* ``Makefile``, ``sim.mk``, ``power.mk``

  * Hammer의 빌드 시스템을 Chipyard에 통합하고 일부 Hammer 명령을 추상화합니다.

* ``build``

  * Hammer 출력 디렉토리입니다. ``OBJ_DIR`` 변수로 변경할 수 있습니다.
  * ``syn-rundir`` 및 ``par-rundir`` 와 같은 하위 디렉토리와 상위 모듈 및 입력 Verilog 파일을 나타내는 ``inputs.yml`` 이 포함됩니다.

* ``env.yml``

  * 이 파일은 이 튜토리얼에서 사용되지 않지만, 상용 도구 흐름에는 필요합니다. 도구 환경 구성을 위한 템플릿 파일입니다. 설치 경로와 라이선스 서버 경로를 환경에 맞게 채우십시오. SLICE 및 BWRC 회원의 경우, 예제 환경 설정은 `여기 <https://github.com/ucb-bar/hammer/tree/master/e2e/env>`__ 에서 찾을 수 있습니다.

* ``example-vlsi-sky130``

  * Hammer의 진입점입니다. 훅을 위한 예제 플레이스홀더가 포함되어 있습니다.

* ``example-sky130.yml``, ``example-openroad.yml``, ``example-designs/sky130-openroad.yml``

  * 이 튜토리얼에 사용되는 Hammer IR입니다. SLICE 및 BWRC 회원의 경우, 예제 Sky130 설정은 `여기 <https://github.com/ucb-bar/hammer/tree/master/e2e/pdks>`__ 에서 찾을 수 있습니다.

* ``example-design.yml``, ``example-asap7.yml``, ``example-tech.yml``

  * 이 튜토리얼에서 사용되지 않지만 템플릿으로 제공되는 Hammer IR입니다.

* ``generated-src``

  * 모든 정교화된 Chisel 및 FIRRTL이 포함됩니다.

* ``hammer-<vendor>-plugins``

  * 이 튜토리얼에서는 사용되지 않는 도구 플러그인 리포지토리입니다(hammer-vlsi 패키지에 제공됨).

Prerequisites
-------------

* Python 3.9+
* OpenROAD 흐름 도구 (참고: 도구 버전에 따라 튜토리얼이 깨질 수 있음):

  * **Yosys 0.27+3** (합성), 설치 방법: `conda 사용 <https://anaconda.org/litex-hub/yosys>`__ 또는 `소스에서 빌드 <https://yosyshq.net/yosys/download.html>`__
  * **OpenROAD v2.0-7070-g0264023b6** (배치 및 라우팅), 설치 방법: `conda 사용 <https://anaconda.org/litex-hub/openroad>`__ (참고: conda 패키지에서는 GUI가 비활성화됨) 또는
    `소스에서 빌드 <https://github.com/The-OpenROAD-Project/OpenROAD/blob/master/docs/user/Build.md>`__ (git hash: 0264023b6c2a8ae803b8d440478d657387277d93)
  * **KLayout 0.28.5** (DEF에서 GDSII 변환, DRC), 설치 방법: `conda 사용 <https://anaconda.org/litex-hub/klayout>`__ 또는 `소스에서 빌드 <https://www.klayout.de/build.html>`__
  * **Magic 8.3.376** (DRC), 설치 방법: `conda 사용 <https://anaconda.org/litex-hub/magic>`__ 또는 `소스에서 빌드 <http://www.opencircuitdesign.com/magic/install.html>`__
  * **NetGen 1.5.250** (LVS), 설치 방법: `conda 사용 <https://anaconda.org/litex-hub/netgen>`__ 또는 `소스에서 빌드 <http://www.opencircuitdesign.com/netgen/install.html>`__

* Sky130A PDK, 설치 방법: `conda 사용 <https://anaconda.org/litex-hub/open_pdks.sky130a>`__ 또는 `이 지침 <https://github.com/ucb-bar/hammer/blob/master/hammer/technology/sky130>`__ 을 따르십시오.
* `Sram22 Sky130 SRAM 매크로 <https://github.com/rahulk29/sram22_sky130_macros>`__

  * 이 SRAM 매크로는 `Sram22 SRAM 생성기 <https://github.com/rahulk29/sram22>`__ (아직 개발 중)으로 생성되었습니다.

Quick Prerequisite Setup
^^^^^^^^^^^^^^^^^^^^^^^^
최근 대부분의 이 튜토리얼의 필수 구성 요소들은 conda 패키지로 설치할 수 있게 되었습니다.
이 튜토리얼의 필수 구성 요소 설정은 스크립트로 작성될 예정이지만, 현재는 설정 지침이 아래에 제공됩니다.
각 도구에 대해 서로 충돌하는 종속성이 있기 때문에 각 도구에 대해 새로운 conda 환경을 만드는 것이 좋습니다.

.. code-block:: shell

    # openroad/klayout이 제대로 설치되도록 채널 설정
    conda config --set channel_priority true
    conda config --add channels defaults

    # Sky130A PDK의 모든 파일 다운로드
    conda create -c litex-hub --prefix ~/.conda-sky130 open_pdks.sky130a=1.0.457_0_g32e8f23
    # SRAM22 Sky130 SRAM 매크로 클론
    git clone https://github.com/rahulk29/sram22_sky130_macros ~/sram22_sky130_macros

    # 모든 VLSI 도구 설치
    conda create -c litex-hub --prefix ~/.conda-yosys yosys=0.27_4_gb58664d44
    conda create -c litex-hub --prefix ~/.conda-openroad openroad=2.0_7070_g0264023b6
    conda create -c litex-hub --prefix ~/.conda-klayout klayout=0.28.5_98_g87e2def28
    conda create -c litex-hub --prefix ~/.conda-signoff magic=8.3.376_0_g5e5879c netgen=1.5.250_0_g178b172

    # conda 설정 되돌리기
    conda config --set channel_priority strict
    conda config --remove channels defaults

Initial Setup
-------------
Chipyard 루트에서, Chipyard conda 환경이 활성화되어 있는지 확인하십시오. 그런 다음:

.. code-block:: shell

    ./scripts/init-vlsi.sh sky130 openroad

를 실행하여 플러그인 하위 모듈을 가져오고 설치하십시오. ``sky130`` 또는 ``asap7`` 이외의 기술에 대해 설정할 때는 기술 하위 모듈이 ``vlsi`` 폴더에 복제되며, 상용 도구 흐름(즉, ``openroad`` 인수를 생략하여 설정)의 경우, 도구 플러그인 하위 모듈이 ``vlsi`` 폴더에 복제됩니다.

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

    # 모든

 ~는 이 디렉토리의 절대 경로로 대체되어야 합니다.
    # 기술 경로
    technology.sky130.sky130A: ~/.conda-sky130/share/pdk/sky130A
    technology.sky130.sram22_sky130_macros: ~/sram22_sky130_macros

example-openroad.yml
^^^^^^^^^^^^^^^^^^^^
이 파일은 OpenROAD 도구 흐름에 대한 Hammer 설정을 포함합니다.
합성(Yosys), 배치 및 라우팅(OpenROAD), DRC(KLayout 또는 Magic), 및 LVS(NetGen) 도구를 선택합니다.

도구 바이너리의 위치를 지정하려면 이 파일 상단에 다음 YAML 키를 추가하십시오.
도구가 이미 PATH에 있다면 이것은 필요하지 않습니다.

.. code-block:: yaml

    # 모든 ~는 이 디렉토리의 절대 경로로 대체되어야 합니다.
    # 도구 바이너리 경로
    synthesis.yosys.yosys_bin: ~/.conda-yosys/bin/yosys
    par.openroad.openroad_bin: ~/.conda-openroad/bin/openroad
    par.openroad.klayout_bin: ~/.conda-klayout/bin/klayout  # OpenROAD에서 최종 GDS 쓰기에 호출되는 바이너리
    drc.klayout.klayout_bin: ~/.conda-klayout/bin/klayout   # DRC 단계에서 실행되는 바이너리
    drc.magic.magic_bin: ~/.conda-signoff/bin/magic
    lvs.netgen.netgen_bin: ~/.conda-signoff/bin/netgen


Building the Design
--------------------

``TinyRocketConfig`` 을 정교화하고 설계 및 SRAM 매크로를 흐름을 통해 푸시하기 위한 모든 필수 조건을 설정하려면:

.. code-block:: shell

    make buildfile tutorial=sky130-openroad

``make buildfile`` 명령은 ``build/hammer.d`` 에 Make 타겟 집합을 생성합니다.
환경 변수가 변경된 경우 다시 실행해야 합니다.
이 변수들을 셸 환경으로 내보내는 대신 Makefile에서 직접 수정하는 것이 좋습니다.

``buildfile`` Make 타겟은 (1) 모든 Chisel 소스에서 정교화된 Verilog와 (2) 설계의 메모리 인스턴스를 SRAM 매크로로 매핑하는 것에 대한 종속성을 가집니다;
이 두 단계와 관련된 모든 파일은 ``generated-src/chipyard.harness.TestHarness.TinyRocketConfig-ChipTop`` 디렉토리에 위치합니다.
참고로, ``generated-src``의 파일은 각 도구/기술 흐름에 따라 다릅니다.
특히 Sky130 상용 버전과 OpenROAD 튜토리얼 흐름에서 다르므로, 이 흐름들은 별도의 Chipyard 설치에서 실행해야 합니다.
잘못된 소스가 생성된 경우, ``make buildfile -B`` 를 실행하여 모든 타겟을 올바르게 다시 빌드하십시오.

간결함을 위해, 이 튜토리얼에서는 ``tutorial=sky130-openroad`` Make 변수를 설정하여 ``tutorial.mk`` 에서 몇 가지 추가 변수를 설정하도록 하겠습니다. 그 중 일부는 다음과 같습니다:

* ``CONFIG=TinyRocketConfig`` 는 나머지 Chipyard 프레임워크와 동일한 방식으로 타겟 생성기 구성을 선택합니다. 도구 실행 시간을 최소화하기 위해 축소된 Rocket Chip 을 정교화합니다.
* ``tech_name=sky130``는 ``Makefile`` 에서 적절한 Hammer 플러그인 등 몇 가지 필수 경로를 설정합니다.
* ``TOOLS_CONF`` 및 ``TECH_CONF`` 는 위에서 설명한 ``example-openroad.yml`` 및 ``example-sky130.yml`` 설정 파일을 선택합니다.
* ``DESIGN_CONF`` 및 ``EXTRA_CONFS``는 ``example-sky130.yml`` 에서 Hammer IR의 추가적인 디자인별 오버라이드를 허용합니다.
* ``VLSI_OBJ_DIR=build-sky130-openroad`` 는 빌드 디렉토리에 고유한 이름을 부여하여 동일한 리포지토리에서 여러 흐름을 실행할 수 있도록 합니다. 나머지 튜토리얼에서는 간결함을 위해 이 디렉토리를 여전히 ``build`` 로 참조하겠습니다.
* ``VLSI_TOP`` 은 기본적으로 ``ChipTop`` 으로 설정되며, 이는 Chipyard SoC 구성에서 생성된 최상위 Verilog 모듈의 이름입니다. 대신 ``VLSI_TOP=Rocket`` 을 설정하면, 이 튜토리얼을 빠르게 실행할 수 있으며, SRAM에 의존하지 않는 단일 RISC-V 코어(캐시, 주변장치, 버스 등 없음)로 구성된 Rocket 코어를 최상위 모듈로 사용할 수 있습니다.

Running the VLSI Flow
---------------------

Synthesis
^^^^^^^^^

.. code-block:: shell

    make syn tutorial=sky130-openroad

합성 후 로그와 관련 자료는 ``build/syn-rundir`` 에 있습니다.

.. 품질 결과 데이터는 ``build/syn-rundir/reports`` 에서 확인할 수 있으며, 설계 공간 탐색을 위한 정보 추출 방법이 진행 중입니다.

Place-and-Route
^^^^^^^^^^^^^^^
.. code-block:: shell

    make par tutorial=sky130-openroad

OpenROAD가 ``detailed_route`` 단계 이후의 명령에서 멈추는 경우가 있으므로, 배치 및 라우팅을 ``extraction`` 단계까지 실행한 후, 이 단계에서 흐름을 다시 시작하는 것이 좋습니다. 아래의 :ref:`VLSI/Sky130-OpenROAD-Tutorial:VLSI Flow Control` 문서를 참조하여 흐름을 이러한 단계로 나누는 방법을 확인하십시오.

완료 후, 최종 데이터베이스는 상호작용적인 OpenROAD 세션에서 열 수 있습니다.
Hammer는 이러한 세션을 시작하기 위한 편리한 스크립트를 생성합니다.

.. code-block:: shell

    cd ./build/chipyard.harness.TestHarness.TinyRocketConfig-ChipTop/par-rundir
    ./generated-scripts/open_chip

참고로, conda OpenROAD 패키지는 GUI가 비활성화된 상태로 컴파일되었으므로 레이아웃을 보려면 OpenROAD를 소스에서 설치해야 합니다.

아래는 OpenROAD로 생성된 Sky130에서 TinyRocketConfig의 배치 후 레이아웃입니다.

.. image:: ../_static/images/vlsi-openroad-par-tinyrocketconfig.png

중간 데이터베이스는 ``par`` 작업의 각 단계 사이에 ``build/par-rundir`` 에 작성되며,
이 데이터베이스는 디버깅 목적으로 동일한 ``open_chip`` 스크립트를 사용하여 복원할 수 있습니다.

.. code-block:: shell

    cd build/chipyard.harness.TestHarness.TinyRocketConfig-ChipTop/par-rundir
    ./generated_scripts/open_chip -h
    "
        Usage: ./generated-scripts/open_chip [-t] [openroad_db_name]

        Options
          openroad_db_name    : 로드할 데이터베이스 이름 (기본값=최신)
          -t, --timing        : 타이밍 정보를 로드 (로드 시간이 느려서 기본값=비활성화)
          -h, --help          : 이 메시지 표시
    "
    # 타이밍 정보 없이 사전 전역 라우팅 데이터베이스 로드
    ./generated_scripts/open_chip pre_global_route

    # 타이밍 정보와 함께 포스트 클럭 트리 데이터베이스 로드
    ./generated_scripts/open_chip -t post_clock_tree

타이밍 보고서를 포함한 다양한 보고서는 ``build/par-rundir/reports`` 에서 확인할 수 있습니다.

OpenROAD 도구의 전체 단계 목록과 구현 방법에 대한 자세한 내용은 `OpenROAD 도구 플러그인 <https://github.com/ucb-bar/hammer/blob/master/hammer/par/openroad>`__ 을 참조하십시오.

DRC & LVS
^^^^^^^^^

이 튜토리얼은 상용 사인오프 도구를 통해 광범위하게 실행되었으므로, 오픈 소스 사인오프 흐름이 안정적이지 않으며 유용한 결과를 보장하지는 않습니다.
`KLayout 도구 플러그인 <https://github.com/ucb-bar/hammer/blob/master/hammer/drc/klayout>`__,
`Magic 도구 플러그인 <https://github.com/ucb-bar/hammer/blob/master/hammer/drc/magic>`__,
및 `Netgen 도구 플러그인 <https://github.com/ucb-bar/hammer/blob/master/hammer/lvs/netgen>`__ 의 개선을 위한 기여를 환영합니다.

DRC를 위해 읽기 쉬운 결과를 생성하려면 KLayout을 권장하지만, ``example-openroad.yml`` 에서 줄을 주석 해제하여 Magic을 선택할 수도 있습니다. ``vlsi.core.drc_tool: "hammer.drc.magic"``.

DRC & LVS를 실행하고 결과를 보려면:

.. code-block:: shell

    make drc tutorial=sky130-openroad
    ./build/chipyard.harness.TestHarness.TinyRocketConfig-ChipTop/drc-rundir/generated-scripts/view_drc
    make lvs tutorial=sky130-openroad
    ./build/chipyard.harness.TestHarness.TinyRocketConfig-ChipTop/lvs-rundir/generated-scripts/view_lvs

참고

로, ``sky130-openroad.yml`` 에서 다음 YAML 키를 설정했습니다:

.. code-block:: yaml

    drc.magic.generate_only: true
    lvs.netgen.generate_only: true

이 키들은 Hammer 플러그인이 필요한 모든 스크립트를 생성하기만 하고 해당 도구로 실행하지 않도록 합니다.
Magic과 Netgen은 이 튜토리얼을 작성할 당시에는 상호작용적으로 로드할 수 있는 데이터베이스 형식을 가지고 있지 않기 때문에,
DRC/LVS 결과를 디버깅하기 위해서는 도구를 상호작용적으로 실행한 후 DRC/LVS 검사를 실행해야 하며, 이는 ``generated-scripts/view_[drc|lvs]`` 스크립트로 수행됩니다. KLayout은 로드 가능한 데이터베이스 형식을 가지고 있어 이와 같은 문제가 없습니다.

아래는 KLayout DRC 결과를 상호작용적으로 로드할 때 표시되는 창입니다. 대부분의 DRC 오류는 별도로 검증된 Sky130 SRAM과 관련된 특별 규칙에서 발생한 것입니다. 향후 KLayout 도구 플러그인은 기본적으로 이러한 SRAM 매크로를 블랙박스 처리해야 하지만, 이 기능은 아직 구현되지 않았습니다.

.. image:: ../_static/images/vlsi-openroad-klayout-drc.png


VLSI Flow Control
^^^^^^^^^^^^^^^^^
먼저, :ref:`VLSI/Hammer:VLSI Flow Control` 문서를 참조하십시오. 아래 예제는 ``redo-par`` Make 타겟을 사용하여 배치 및 라우팅만 다시 실행하는 방법을 보여줍니다. ``redo-`` 는 VLSI 흐름 작업 중 해당 작업만 다시 실행하도록 할 수 있습니다.

.. code-block:: shell

      # 다음 두 명령은 전체 흐름을 실행하며, 설계의 체크포인트를 저장하고 다시 로드하기 위해 pre_extraction
      #   데이터베이스를 사용합니다.
      make par HAMMER_EXTRA_ARGS="--stop_after_step extraction"
      make redo-par HAMMER_EXTRA_ARGS="--start_before_step extraction"

      # 다음 두 명령은 동일합니다. 왜냐하면 추출 단계가 디자인 쓰기 단계 직전에 실행되기 때문입니다.
      make redo-par HAMMER_EXTRA_ARGS="--start_after_step extraction"
      make redo-par HAMMER_EXTRA_ARGS="--start_before_step write_design"

      # 새로운 플로어플랜 구성을 테스트하기 위해 플로어플래닝만 다시 실행하는 예입니다.
      #   "-p file.yml"은 file.yml이 이전의 모든 yaml/json 구성을 무시하도록 만듭니다.
      make redo-par \
        HAMMER_EXTRA_ARGS="--only_step floorplan_design -p example-designs/sky130-openroad.yml"

Documentation
-------------
Hammer의 기본 구현에 대한 자세한 내용은 `Hammer 문서 웹사이트 <https://hammer-vlsi.readthedocs.io/en/latest/index.html>`__ 를 참조하십시오.

이 튜토리얼에서 사용된 플러그인에 대한 자세한 내용은 `OpenROAD 도구 플러그인 저장소 + README <https://github.com/ucb-bar/hammer/blob/master/hammer/par/openroad>`__
 및 `Sky130 기술 플러그인 저장소 + README <https://github.com/ucb-bar/hammer/blob/master/hammer/technology/sky130>`__ 를 확인하십시오.
