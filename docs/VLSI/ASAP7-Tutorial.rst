.. _tutorial:

ASAP7 Tutorial
==============
이 리포지토리의 ``vlsi`` 폴더에는 SHA-3 가속기와 더미 하드 매크로를 사용하는 예제 Hammer 흐름이 포함되어 있습니다. 이 튜토리얼은 내장된 ASAP7 기술 플러그인을 사용하며, DRC 및 LVS에 필요한 Mentor 도구 플러그인 서브모듈에 대한 액세스가 필요합니다.

Project Structure
-----------------

이 예제는 권장 파일 구조와 빌드 시스템을 제공합니다. ``vlsi/`` 폴더에는 다음 파일 및 폴더가 포함됩니다:

* ``Makefile``, ``sim.mk``, ``power.mk``

  * Hammer의 빌드 시스템을 Chipyard에 통합하고 일부 Hammer 명령을 추상화합니다.

* ``build``

  * Hammer 출력 디렉토리입니다. ``OBJ_DIR`` 변수로 변경할 수 있습니다.
  * ``syn-rundir`` 및 ``par-rundir`` 와 같은 하위 디렉토리와 입력 Verilog 파일 및 최상위 모듈을 나타내는 ``inputs.yml`` 파일이 포함됩니다.

* ``env.yml``

  * 도구 환경 구성을 위한 템플릿 파일입니다. 환경에 맞는 설치 경로와 라이선스 서버 경로를 채우십시오. SLICE 및 BWRC 회원의 경우, 예제 환경 구성은 `여기 <https://github.com/ucb-bar/hammer/tree/master/e2e/env>`__ 에서 찾을 수 있습니다.

* ``example-vlsi``

  * Hammer의 진입점입니다. 훅(hook)을 위한 예제 플레이스홀더가 포함되어 있습니다.

* ``example-asap7.yml``, ``example-tools.yml``

  * 이 튜토리얼에 대한 Hammer IR입니다. SLICE 및 BWRC 회원의 경우, 예제 ASAP7 구성은 `여기 <https://github.com/ucb-bar/hammer/tree/master/e2e/pdks>`__ 에서 찾을 수 있습니다.

* ``example-design.yml``, ``example-sky130.yml``, ``example-tech.yml``

  * 이 튜토리얼에서 사용되지 않지만 템플릿으로 제공된 Hammer IR입니다.

* ``generated-src``

  * 모든 정교화된 Chisel 및 FIRRTL 파일입니다.

* ``hammer-<vendor>-plugins``

  * 도구 플러그인 리포지토리입니다.

* ``view_gds.py``

  * gdstk 또는 gdspy를 사용하여 레이아웃을 보는 편리한 스크립트입니다. TinyRocketConfig 예제보다 작은 레이아웃에만 사용하십시오. gdstk가 생성한 SVG가 너무 크고 gdspy의 GUI가 큰 레이아웃에서는 매우 느릴 수 있기 때문입니다!

Prerequisites
-------------

* Python 3.9+
* Genus, Innovus, Voltus, VCS 및 Calibre 라이선스
* ASAP7에 대한 세부 정보는 (`README <https://github.com/ucb-bar/hammer/blob/master/hammer/technology/asap7>`__ 참고):

  * 먼저 `ASAP7 v1p7 PDK <https://github.com/The-OpenROAD-Project/asap7>`__ 를 다운로드합니다(리포지토리를 얕게 클론하거나 아카이브를 다운로드하는 것이 좋습니다). 그런 다음, 선택한 디렉토리에 `암호화된 Calibre 덱스 tarball <http://asap.asu.edu/asap/>`__ 을 다운로드하지만, 지침에 따라 압축을 풀지 마십시오. 기술 플러그인은 tarball을 캐시 디렉토리에 추출하도록 구성되어 있습니다.
  * 추가적인 ASAP7 하드 매크로가 있는 경우, 그들의 LEF & GDS는 4000 DBU 정밀도로 4배 확대되어야 합니다.

Initial Setup
-------------
Chipyard 루트에서 Chipyard conda 환경이 활성화되어 있는지 확인하십시오. 그런 다음:

.. code-block:: shell

    ./scripts/init-vlsi.sh asap7

명령을 실행하여 플러그인 서브모듈을 가져오고 설치하십시오. ``sky130`` 또는 ``asap7`` 외의 기술을 사용하는 경우, 먼저 ``vlsi`` 폴더에 기술 서브모듈을 추가해야 합니다.

이제 ``vlsi`` 디렉토리로 이동합니다. 튜토리얼의 나머지 부분은 이 디렉토리에 있다고 가정합니다.

.. code-block:: shell

    cd ~chipyard/vlsi

Building the Design
--------------------
``TinyRocketConfig`` 을 정교화하고 빌드 시스템이 설계 및 SRAM 매크로를 흐름을 통해 전달할 수 있도록 모든 필수 항목을 설정하려면:

.. code-block:: shell

    make buildfile CONFIG=TinyRocketConfig

``CONFIG=TinyRocketConfig`` 은 Chipyard 프레임워크의 나머지 부분과 동일한 방식으로 대상 생성기 구성을 선택합니다. 이는 도구 런타임을 최소화하기 위해 간소화된 Rocket Chip을 정교화합니다.

궁금한 분들을 위해, ``make buildfile`` 은 ``build/hammer.d`` 에 일련의 Make 타겟을 생성합니다. 환경 변수가 변경된 경우 다시 실행해야 합니다. 이러한 변수는 셸 환경에 내보내는 대신 Makefile에서 직접 수정하는 것이 좋습니다.

Running the VLSI Flow
---------------------

example-vlsi
^^^^^^^^^^^^
이것은 훅을 위한 플레이스홀더가 포함된 진입 스크립트입니다. ``ExampleDriver`` 클래스에서는 ``get_extra_par_hooks`` 에 훅 목록이 전달됩니다. 훅은 Hammer API를 확장하기 위한 추가적인 Python 및 TCL 코드 조각입니다(``x.append()`` 를 통해). 훅은 이 예제에서 보여지는 것처럼 ``make_pre/post/replacement_hook`` 메서드를 사용하여 삽입할 수 있습니다. 이러한 훅이 VLSI 흐름에 주입되는 방법에 대한 자세한 설명은 Hammer 문서를 참조하십시오.

example-asap7.yml
^^^^^^^^^^^^^^^^^
이 파일에는 이 예제 프로젝트에 대한 Hammer 구성이 포함되어 있습니다. 예제 클럭 제약, 전원 스트랩 정의, 배치 제약 및 핀 제약이 제공됩니다. 추가 라이브러리 및 도구에 대한 구성은 하단에 있습니다.

먼저, 다운로드한 ASAP7 Calibre 덱스 tarball이 있는 디렉토리의 절대 경로로 ``technology.asap7.tarball_dir`` 을 설정하십시오. PDK의 루트 디렉토리에 있지 않은 경우, ``technology.asap7.pdk_install_dir`` 및 ``technology.asap7.stdcell_install_dir`` 도 설정해야 합니다.

Synthesis
^^^^^^^^^
.. code-block:: shell

    make syn CONFIG=TinyRocketConfig

합성 후 로그와 관련 자료는 ``build/syn-rundir`` 에 저장됩니다. 원시 결과 데이터의 품질은 ``build/syn-rundir/reports`` 에서 확인할 수 있으며, 설계 공간 탐색을 위한 정보 추출 방법이 개발 중입니다.

Place-and-Route
^^^^^^^^^^^^^^^
.. code-block:: shell

    make par CONFIG=TinyRocketConfig

완료 후, 최종 데이터베이스는 ``./build/par-rundir/generated-scripts/open_chip`` 을 통해 인터랙티브 Innovus 세션에서 열 수 있습니다.

중간 데이터베이스는 ``par`` 작업의 각 단계 사이에 ``build/par-rundir`` 에 작성되며, 디버깅 목적으로 인터랙티브 Innovus 세션에서 복원할 수 있습니다.

타이밍 보고서는 ``build/par-rundir/timingReports`` 에서 찾을 수 있습니다. 이들은 gzip으로 압축된 텍스트 파일입니다.

`gdspy` 를 사용하여 `최종 레이아웃을 볼 수 있습니다 <https://gdspy.readthedocs.io/en/stable/reference.html?highlight=scale#layoutviewer>`__, 하지만 다소 조잡하고 느립니다(로드하는 데 몇 분이 걸릴 수 있음):

.. code-block:: shell

    ./view_gds.py build/chipyard.harness.TestHarness.TinyRocketConfig/par-rundir/ChipTop.gds

기본적으로 이 스크립트는 M2에서 M4까지의 라우팅만 표시합니다. 레이아웃 뷰어의 측면 창에서 레이어를 전환할 수 있으며 ``view_gds.py`` 에는 레이어 번호와 레이어 이름 간의 매핑이 있습니다.

DRC & LVS
^^^^^^^^^
DRC 및 LVS를 실행하고 Calibre에서 결과를 보려면:

.. code-block:: shell

    make drc CONFIG=TinyRocketConfig
    ./build/chipyard.harness.TestHarness.TinyRocketConfig-ChipTop/drc-rundir/generated-scripts/view-drc
    make lvs CONFIG=TinyRocketConfig
    ./build/chipyard.harness.TestHarness.TinyRocketConfig-ChipTop/lvs-rundir/generated-scripts/view-lvs

이 PDK에서 예상되는 일부 DRC 오류는 `ASAP7 플러그인 README <https://github.com/ucb-bar/hammer/blob/master/hammer/technology/asap7>`__ 에 설명되어 있습니다.
또한, 이 튜토리얼과 PDK에서 제공되는 더미 SRAM에는 내부에 지오메트리가 없으므로 DRC 오류가 발생할 수 있습니다.

Simulation
^^^^^^^^^^
VCS를 사용한 시뮬레이션이 지원되며, 합성 전 또는 배치 및 라우팅 후(post-syn 및 post-P&R) 게이트 수준에서 실행할 수 있습니다. 여기 포함된 시뮬레이션 인프라는 Chipyard 구성에서 RISC-V 바이너리를 실행하기 위한 것입니다. 예를 들어, RTL 수준의 시뮬레이션의 경우:

.. code-block:: shell

    make sim-rtl CONFIG=TinyRocketConfig BINARY=$RISCV/riscv64-unknown-elf/share/riscv-tests/isa/rv32ui-p-simple

합성 후 및 배치 및 라우팅 후 시뮬레이션은 각각 ``sim-syn`` 및 ``sim-par`` 메이크 타겟을 사용합니다.

이들 메이크 타겟에 ``-debug`` 및 ``-debug-timing`` 을 추가하면 VCS가 SAIF + FSDB(또는 ``USE_VPD`` 플래그가 설정된 경우 VPD)를 작성하고 타이밍이 주석으로 달린 시뮬레이션을 실행하도록 지시합니다. 사용 가능한 모든 타겟에 대한 정보는 ``sim.mk`` 파일을 참조하십시오.

Power/Rail Analysis
^^^^^^^^^^^^^^^^^^^
Voltus를 사용하여 배치 및 라우팅 후 전력 및 레일(IR 드롭) 분석이 지원됩니다:

.. code-block:: shell

    make power-par CONFIG=TinyRocketConfig

명령에 ``BINARY`` 변수를 추가하면 ``sim-<syn/par>-debug`` 실행에서 생성된 활동 파일을 사용하여 파형에 인코딩된 토글로부터 동적 전력 및 IR 드롭을 보고합니다.

게이트 수준 시뮬레이션을 건너뛰려면 전력 도구를 수동으로 실행해야 합니다(생성된 ``hammer.d`` 빌드 파일에 생성된 명령 참조). 정적 및 능동(벡터리스) 전력 및 IR 드롭이 보고됩니다.

