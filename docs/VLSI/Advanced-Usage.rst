.. _advanced-usage:

Advanced Usage
==============

Hammer Development and Upgrades
-------------------------------
Chipyard 내에서 Hammer를 개발하거나 최신 PyPI 릴리스 이상의 Hammer 버전을 사용해야 하는 경우, `Hammer 리포지토리 <https://github.com/ucb-bar/hammer>`__ 를 디스크의 다른 위치에 클론하십시오. 그런 다음:

.. code-block:: shell

    pip install -e <path/to/hammer>

특정 플러그인을 최신 커밋으로 업그레이드하고 설치하려면, Chipyard 루트 디렉토리에서 플러그인 이름에 대한 매치 패턴을 인수로 제공하여 업그레이드 스크립트를 사용할 수 있습니다:

.. code-block:: shell

    ./scripts/upgrade-vlsi.sh <pattern(s)>

Hammer 설치를 최신 PyPI 릴리스로 업그레이드하고 모든 플러그인을 한 번에 업그레이드하려면 위의 스크립트를 인수 없이 실행하십시오. 주의: 이 작업은 최신 Hammer 릴리스보다 새로운 플러그인 변경 사항을 가져와서 호환성 문제를 일으킬 수 있습니다.

Alternative RTL Flows
---------------------
제공된 Make 기반 빌드 시스템은 Chipyard에서 생성된 RTL을 사용하지 않고도 Hammer를 사용할 수 있도록 지원합니다. 사용자 정의 Verilog 모듈을 처리하려면, ``make buildfile`` 명령에 다음 환경 변수를 추가하면 됩니다(또는 Makefile에서 직접 수정할 수 있습니다).

.. code-block:: shell

    CUSTOM_VLOG=<your Verilog files>
    VLSI_TOP=<your top module>

``CUSTOM_VLOG`` 는 Chipyard 인프라의 나머지 부분에 대한 종속성을 끊고 Chisel/FIRRTL 정교화를 시작하지 않습니다. ``VLSI_TOP`` 은 사용자 정의 Verilog 파일에서 최상위 모듈을 선택합니다.

Under the Hood
--------------
내부에서 무슨 일이 일어나는지 확인하려면, 실행되는 명령은 다음과 같습니다:

``make syn`` 의 경우:

.. code-block:: shell

    ./example-vlsi -e /path/to/env.yml -p /path/to/example.yml -p /path/to/inputs.yml --obj_dir /path/to/build syn

``example-vlsi`` 는 앞서 설명한 진입 스크립트이며, ``-e`` 는 환경 yml을 제공하고, ``-p`` 는 구성 yml/json을 가리키며, ``--obj_dir`` 은 대상 디렉토리를 지정하고, ``syn`` 은 수행할 작업입니다.

``make par`` 의 경우:

.. code-block:: shell

    ./example-vlsi -e /path/to/env.yml -p /path/to/syn-output-full.json -o /path/to/par-input.json --obj_dir /path/to/build syn-to-par
    ./example-vlsi -e /path/to/env.yml -p /path/to/par-input.json --obj_dir /path/to/build par

``syn-to-par`` 작업은 합성 출력 구성을 ``-o`` 로 제공된 입력 구성으로 변환합니다. 그런 다음, 이 구성은 ``par`` 작업으로 전달됩니다.

Hammer 명령줄 드라이버에 전달할 수 있는 모든 옵션에 대한 자세한 정보는 Hammer 문서를 참조하십시오.

Manual Step Execution & Dependency Tracking
-------------------------------------------
흐름의 특정 단계를 디버깅해야 하는 경우가 종종 있습니다. 예를 들어 전원 스트랩 설정을 업데이트해야 할 때가 있습니다. 기본 Hammer 명령은 ``--stop_after_step``, ``--start_before_step`` 및 ``--only_step`` 과 같은 옵션을 지원하여 특정 작업의 단계를 제어할 수 있습니다.

Make의 종속성 추적은 사용자가 특정 작업만 다시 실행하려는 경우에도 전체 흐름을 다시 시작하게 할 수 있습니다. Hammer의 빌드 시스템에는 전체 Hammer 명령을 입력하지 않고 특정 작업을 실행할 수 있는 "redo" 타겟(``redo-syn`` 및 ``redo-par`` 등)이 있습니다.

예를 들어 ``new_power_straps.yml`` 에서 전원 스트랩 설정을 업데이트하고 새로운 설정을 시도하려는 경우:

.. code-block:: shell

   make redo-par HAMMER_REDO_ARGS='-p new_power_straps.yml --only_step power_straps'

실행될 명령은 다음과 같습니다:

.. code-block:: shell

    ./example-vlsi -e /path/to/env.yml -p /path/to/par-input.json -p new_power_straps.yml --only_step power_straps --obj_dir /path/to/build par

Hierarchical RTL/Gate-level Simulation, Power Estimation
--------------------------------------------------------
Synopsys 플러그인을 사용하면 칩 수준에서 VCS를 사용한 계층적 RTL 및 게이트 수준 시뮬레이션이 지원됩니다. 또한, Cadence 플러그인에서는 Joules를 사용한 RTL 수준/합성 후 전력 추정과 Voltus를 사용한 배치 및 라우팅 후 전력 추정도 지원됩니다. ``vlsi/`` 디렉토리의 ``sims.mk`` 및 ``power.mk`` 에 특별한 Make 타겟이 제공됩니다. 간단히 설명하면 다음과 같습니다:

* ``sim-rtl``: RTL 수준 시뮬레이션

  * ``sim-rtl-debug``: FSDB 파형도 기록

* ``sim-syn``: 합성 후 게이트 수준 시뮬레이션

  * ``sim-syn-debug``: FSDB 파형도 기록
  * ``sim-syn-timing-debug``: 타이밍 주석이 포함된 FSDB 파형

* ``sim-par``: 배치 및 라우팅 후 게이트 수준 시뮬레이션

  * ``sim-par-debug``: FSDB 파형도 기록
  * ``sim-par-timing-debug``: 타이밍 주석이 포함된 FSDB 파형

* ``power-rtl``: RTL 수준 전력 추정

  * 참고: 먼저 ``sim-rtl-debug`` 를 실행합니다

* ``power-syn``: 합성 후 전력 추정

  * 참고: 먼저 ``sim-syn-debug`` 를 실행합니다

* ``power-par``: 배치 및 라우팅 후 전력 추정

  * 참고: 먼저 ``sim-par-debug`` 를 실행합니다

* ``redo-`` 는 위의 모든 타겟에 추가하여 종속성 추적을 무시할 수 있습니다.

* ``-$(VLSI_TOP)`` 접미사는 계층적 흐름에서 하위 모듈에 대한 시뮬레이션/전력 분석을 나타냅니다(이 변수를 재정의해야 합니다). 이러한 모듈에 대한 테스트벤치를 제공해야 합니다. 기본 테스트벤치는 Chipyard 기반 ``ChipTop`` DUT 인스턴스만 시뮬레이트합니다.

시뮬레이션 구성(예: 바이너리)은 설계에 맞게 편집할 수 있습니다. ``Makefile`` 을 참조하고 설계를 위한 시뮬레이션 매개변수를 설정하는 방법에 대한 자세한 내용은 Hammer 문서를 참조하십시오.

UPF Generation Flow
-------------------------------
이 VLSI 흐름은 실험적으로 `Chisel Aspects <https://javadoc.io/doc/edu.berkeley.cs/chisel3_2.13/latest/chisel3/aop/Aspect.html>`__ 을 사용하여 Chisel 기반 `UPF <https://vlsitutorials.com/upf-low-power-vlsi/>`__ 파일 생성을 지원합니다.

어떤 설계든 UPF를 생성하려면, ``generators/chipyard/src/main/scala/upf/UPFInputs.scala`` 에 있는 ``UPFInputs`` 객체를 수정하여 설계 전력 사양에 맞게 조정하십시오.

이 작업은 설계에서 원하는 모든 전력 도메인을 나타내는 ``PowerDomainInput`` 객체와 계층 구조 및 도메인 속성을 지정하는 ``upfInfo`` 목록을 채우는 것을 포함합니다.

``UPFInputs`` 에 제공된 예는 3개의 전력 도메인(모든 비코어 모듈이 포함된 1개의 부모 도메인과 Rocket 타일에 해당하는 2개의 자식 도메인)이 있는 듀얼 코어 Rocket 구성을 나타냅니다.

흐름을 실행하려면:

.. code-block:: shell

    cd chipyard/vlsi
    make verilog ASPECTS=chipyard.upf.ChipTopUPFAspect

출력된 UPF 파일은 ``vlsi/generated-src/upf`` 에 덤프됩니다.
