.. _hammer_basic_flow:

Using Hammer To Place and Route a Custom Block
=================================================

.. IMPORTANT:: Hammer VLSI 흐름을 사용하려면 Hammer 도구 및 기술 플러그인에 대한 액세스 권한이 필요합니다. 액세스 권한을 요청하려면 hammer-plugins-access@lists.berkeley.edu로 이메일을 보내어 액세스를 원하는 플러그인 목록과 함께 요청하십시오. 이메일에는 GitHub ID와 관련 도구에 대한 라이선스 액세스 권한이 있음을 증명할 수 있는 소속 정보 또는 기타 정보를 포함해야 합니다.

Initialize the Hammer Plug-ins
----------------------------------
Chipyard 루트에서 Chipyard conda 환경이 활성화되어 있는지 확인하십시오. 그런 다음, Hammer에 포함된 기술 플러그인(ASAP7, Sky130)을 사용하는 경우 또는 별도의 플러그인을 사용하는 경우 아래 명령 중 하나를 실행합니다.

Hammer에서 제공하는 플러그인의 경우(``<tech-plugin-name>`` 은 ``asap7`` 또는 ``sky130``):

.. code-block:: shell

    ./scripts/init-vlsi.sh <tech-plugin-name>

별도의 기술 플러그인의 경우(NDA 및 보안 서버가 필요한 독점 공정 기술의 일반적인 사용 사례), ``init-vlsi.sh`` 스크립트를 호출하기 전에 해당 플러그인을 VLSI 디렉토리에 ``hammer-<tech-plugin-name>-plugin`` 이라는 이름으로 직접 서브모듈로 추가하십시오.
예를 들어, tsmintel3이라는 가상의 공정 기술을 사용할 경우:

.. code-block:: shell

    cd vlsi
    git submodule add git@my-secure-server.berkeley.edu:tsmintel3/hammer-tsmintel3-plugin.git
    cd -
    ./scripts/init-vlsi.sh tsmintel3

서브모듈 플러그인을 업데이트해야 하는 경우, ``upgrade-vlsi.sh`` 스크립트를 호출하십시오. 이 스크립트는 최신 마스터 브랜치를 체크아웃하고 가져옵니다.

.. Note:: 일부 VLSI EDA 도구는 RHEL 기반 운영 체제에서만 지원됩니다. Chipyard를 RHEL7 이상에서 사용하는 것이 좋습니다. 그러나 많은 VLSI 서버는 RHEL6과 같은 오래된 운영 체제를 사용하며, 이러한 시스템은 Chipyard의 기본 요구 사항보다 오래된 소프트웨어 패키지를 포함하고 있습니다. RHEL6에서 Chipyard를 빌드하려면 devtoolset(예: devtoolset-8)과 같은 도구 패키지를 사용하거나 gcc, git, gmake, make, dtc, cc, bison, libexpat 및 liby를 소스에서 빌드해야 할 수 있습니다.

Setting up the Hammer Configuration Files
--------------------------------------------

설정해야 하는 첫 번째 구성 파일은 Hammer 환경 구성 파일 ``env.yml`` 입니다. 이 파일에서 사용할 EDA 도구 및 라이선스 서버 경로를 설정해야 합니다. 이 구성 파일의 모든 필드를 채울 필요는 없으며, 사용할 도구의 경로만 채우면 됩니다.
LSF 클러스터 설정이 있는 공유 서버 팜 환경(예: Berkeley Wireless Research Center)에서 작업하는 경우, 이 문서 페이지의 :ref:`VLSI/Basic-Flow:Advanced Environment Setup` 섹션에 나와 있는 추가 가능한 환경 구성을 참고하십시오.

Hammer는 YAML 기반 구성 파일에 의존합니다. 이러한 구성은 단일 파일 내에서 통합될 수 있지만(:ref:`tutorial` 및 :ref:`sky130-openroad-tutorial` 에서와 같이), 일반적으로 프로세스 기술이나 도구 플러그인을 사용할 때는 도구, 기술, 설계라는 세 가지 Hammer 항목에 맞는 세 가지 구성 파일을 사용하는 것이 좋습니다.
``vlsi`` 디렉토리에는 이러한 세 가지 항목에 맞는 세 가지 예제 구성 파일인 ``example-tools.yml``, ``example-tech.yml``, 및 ``example-design.yml`` 이 포함되어 있습니다.

``example-tools.yml`` 파일은 Hammer가 사용할 EDA 도구를 구성합니다. 이 예제 파일은 Cadence Innovus, Genus 및 Voltus, Synopsys VCS, 및 Mentor Calibre를 사용합니다(이 도구들은 Berkeley Wireless Research Center에서 작업할 때 사용하는 도구일 가능성이 큽니다). 도구 버전은 사용하는 공정 기술에 매우 민감하므로, 한 공정 기술에서 작동하는 도구 버전이 다른 공정 기술에서는 작동하지 않을 수 있습니다.

``example-design.yml`` 파일에는 기본 빌드 시스템 정보(사용할 코어/스레드 수 등)와 우리가 작업하고 있는 설계에 특정한 클럭 신호 이름 및 주파수, 전원 모드, 플로어플랜, 추가 제약 조건 등의 구성이 포함되어 있습니다.

마지막으로, ``example-tech.yml`` 파일은 공정 기술 플러그인 구성을 위한 템플릿 파일입니다. 이 파일을 복사하고, 액세스할 수 있는 공정 기술 플러그인의 적절한 세부 사항으로 필드를 대체합니다. 예를 들어, ``asap7`` 기술 플러그인의 경우, ``<tech_name>`` 필드를 "asap7"로 대체하고 공정 기술 파일 설치 디렉토리의 경로를 입력합니다. 기술 플러그인(ASAP7의 경우 Hammer 내에 있음)은 기술 노드 및 기타 매개변수를 정의합니다.

이러한 예제 구성 파일을 복사하고 다른 이름으로 사용자 정의하는 것이 좋습니다. 이렇게 하면 다른 프로세스 기술과 설계를 위한 별도의 구성 파일을 가질 수 있습니다(예: ``example-tech.yml`` 에서 ``tech-tsmintel3.yml`` 을 생성).

Building the Design
---------------------
구성 파일을 설정한 후, 이제 Chipyard Chisel 설계를 Verilog로 정교화하고 Verilog를 VLSI 친화적으로 만들기 위해 필요한 변환을 수행할 것입니다.
또한, 이 설계에 맞는 또 다른 Hammer 구성 파일 세트를 자동으로 생성하여 물리적 설계 도구를 구성하는 데 사용할 것입니다.
이를 위해 적절한 Chipyard 구성 변수와 Hammer 구성 파일을 사용하여 ``make buildfile`` 을 호출합니다.
다른 Chipyard 흐름과 마찬가지로 ``CONFIG`` make 변수를 사용하여 SoC 구성을 지정합니다.
그러나 물리적 설계의 경우 계층적 방식으로 작업할 수 있으며 단일 모듈에서 작업하는 것이 좋을 수 있습니다.
따라서 작업하려는 특정 Verilog 모듈의 이름을 ``VLSI_TOP`` make 변수로 지정할 수 있습니다(이 모듈 이름은 해당 Chisel 모듈 이름과 일치해야 함).
Makefile은 Tapeout-Tools 및 MacroCompiler(:ref:`Tools/Tapeout-Tools:Tapeout-Tools`)와 같은 도구를 자동으로 호출하여 생성된 Verilog를 더 VLSI 친화적으로 만듭니다.
기본적으로 MacroCompiler는 Hammer 기술 플러그인 내의 SRAM 옵션에 메모리를 매핑하려고 시도합니다. 그러나 새로운 공정 기술로 작업 중이며 플립플롭 배열을 선호하는 경우, ``TOP_MACROCOMPILER_MODE`` make 변수를 사용하여 MacroCompiler를 구성할 수 있습니다. 예를 들어, 기술 플러그인에 SRAM 컴파일러가 준비되지 않은 경우, ``TOP_MACROCOMPILER_MODE='--mode synflops'`` 옵션을 사용할 수 있습니다(플립플롭만으로 설계를 합성하는 것은 매우 느리며 종종 제약을 충족하지 못할 수 있습니다).

우리는 공정 기술의 이름(``tech_name``)과 생성한 구성 파일을 지정하여 ``make buildfile`` 명령을 호출합니다. ASAP7 튜토리얼에서는(:ref:`tutorial`) 이러한 구성 파일이 ``example-asap7.yml`` 이라는 단일 파일로 병합됩니다.

따라서 전체 SoC를 단일하게 배치하고 라우팅하려면 관련 명령은 다음과 같습니다.

.. code-block:: shell

    make buildfile CONFIG=<chipyard_config_name> tech_name=<tech_name> INPUT_CONFS="example-design.yml example-tools.yml example-tech.yml"

더 일반적인 시나리오로, 예를 들어 GemminiRocketConfig Chipyard SoC 구성 내의 Gemmini 가속기에서 작업하는 경우, 관련 명령은 다음과 같습니다:

.. code-block:: shell

    make buildfile CONFIG=GemminiRocketConfig VLSI_TOP=Gemmini tech_name=tsmintel3 INPUT_CONFS="example-design.yml example-tools.yml example-tech.yml"

Running the VLSI Flow
---------------------

Hammer 기본 구성을 사용하여 기본 VLSI 흐름을 실행하는 것은 매우 간단하며, 이전에 언급한 Make 변수를 사용하여 간단한 ``make`` 명령으로 구성됩니다.

Synthesis
^^^^^^^^^

합성을 실행하려면 해당 Make 변수를 사용하여 ``make syn`` 을 실행합니다.
합성 후 로그와 관련 자료는 ``build/<config-name>/syn-rundir`` 에 저장됩니다. 원시 QoR 데이터(면적, 타이밍, 게이트 수 등)는 ``build/<config-name>/syn-rundir/reports`` 에서 확인할 수 있습니다.

따라서 전체 SoC를 단일하게 합성하려면 관련 명령은 다음과 같습니다:

.. code-block:: shell

    make syn CONFIG=<chipyard_config_name> tech_name=<tech_name> INPUT_CONFS="example-design.yml example-tools.yml example-tech.yml"

더 일반적인 시나리오로, 예를 들어 GemminiRocketConfig Chipyard SoC 구성 내의 Gemmini 가속기에서 작업하는 경우, 관련 명령은 다음과 같습니다:

.. code-block:: shell

    make syn CONFIG=GemminiRocketConfig VLSI_TOP=Gemmini tech_name=tsmintel3 INPUT_CONFS="example-design.yml example-tools.yml example-tech.yml"

합성된 설계가 타이밍을 충족하는지 확인하기 위해 최종 QoR 보고서를 확인하는 것이 좋습니다.

Place-and-Route
^^^^^^^^^^^^^^^
배치 및 라우팅을 실행하려면 해당 Make 변수를 사용하여 ``make par`` 을 실행합니다.
PnR 후 로그와 관련 자료는 ``build/<config-name>/par-rundir`` 에 저장됩니다. 특히, 결과 GDSII 파일은 해당 디렉토리에 ``*.gds`` 확장자로 저장되며, 타이밍 보고서는 ``build/<config-name>/par-rundir/timingReports`` 에서 확인할 수 있습니다.
배치 및 라우팅은 합성보다 더 많은 설계 세부 정보를 필요로 합니다. 예를 들어, 배치 및 라우팅에는 기본 플로어플랜 제약이 필요합니다. 기본 ``example-design.yml`` 구성 파일 템플릿은 도구(Cadence Innovus 도구)가 설계의 최상위 수준(``ChipTop``) 내에서 자동 플로어플래닝 기능을 사용하도록 합니다. 그러나 SoC 최상위 수준이 아닌 특정 블록을 배치하고 라우팅하려는 경우, 사용 중인 ``VLSI_TOP`` make 매개변수에 맞게 최상위 경로 이름을 변경해야 합니다.

따라서 기본 기술 플러그인 파라미터를 사용하여 전체 SoC를 단일하게 배치하고 라우팅하려면 관련 명령은 다음과 같습니다:

.. code-block:: shell

    make par CONFIG=<chipyard_config_name> tech_name=<tech_name> INPUT_CONFS="example-design.yml example-tools.yml example-tech.yml"

더 일반적인 시나리오로, 예를 들어 GemminiRocketConfig Chipyard SoC 구성 내의 Gemmini 가속기에서 작업하는 경우:

.. code-block:: shell

  vlsi.inputs.placement_constraints:
    - path: "Gemmini"
      type: toplevel
      x: 0
      y: 0
      width: 300
      height: 300
      margins:
        left: 0
        right: 0
        top: 0
        bottom: 0

관련 ``make`` 명령은 다음과 같습니다:

.. code-block:: shell

    make par CONFIG=GemminiRocketConfig VLSI_TOP=Gemmini tech_name=tsmintel3 INPUT_CONFS="example-design.yml example-tools.yml example-tech.yml"

너비와 높이 사양은 모듈에 따라 크게 다를 수 있으며 모듈 계층 수준도 다를 수 있습니다. 합리적인 너비와 높이 값을 설정해야 합니다.
배치 및 라우팅은 일반적으로 전원망, 클럭망, 핀 할당 및 플로어플랜에 대한 더 세밀한 입력 사양을 필요로 합니다. 템플릿 구성 파일은 기본적으로 도구의 자동 기본값을 제공합니다. 그러나 이는 일반적으로 매우 나쁜 QoR을 초래하므로, 더 잘 정보화된 플로어플랜, 핀 할당 및 전원망을 지정하는 것이 좋습니다. 이러한 매개변수를 사용자 정의하는 방법에 대한 자세한 내용은 :ref:`VLSI/Basic-Flow:Customizing Your VLSI Flow in Hammer` 섹션 또는 Hammer 문서를 참조하십시오.
또한, 일부 Hammer 공정 기술 플러그인은 도구 경로 및 핀 할당과 같은 필수 설정에 대한 기본값을 제공하지 않습니다(예: ASAP7). 이러한 경우, 이러한 제약 조건은 최상위 구성 yml 파일에서 수동으로 지정해야 하며, 이는 ``example-asap7.yml`` 구성 파일에서와 같이 지정됩니다.

배치 및 라우팅 도구는 공정 기술에 매우 민감하며(합성 도구보다 훨씬 민감함), 다른 공정 기술은 특정 도구 버전에서만 작동할 수 있습니다. 작업 중인 특정 공정 기술에 적합한 도구 버전을 확인하는 것이 좋습니다.


.. Note:: 합성 및 배치-라우팅 사이에 yml 구성 파일을 편집한 경우, ``make par`` 명령은 합성을 자동으로 다시 실행합니다. 이를 피하고 구성 파일 변경이 합성 결과에 영향을 미치지 않는다고 확신하는 경우, 대신 ``make redo-par`` 명령을 사용하고 ``HAMMER_EXTRA_ARGS='-p <your-changed.yml>'`` 변수를 사용할 수 있습니다.



Power Estimation
^^^^^^^^^^^^^^^^^^^^
Hammer에서 전력 추정은 두 가지 단계 중 하나에서 수행될 수 있습니다: 합성 후(post-syn) 또는 배치 및 라우팅 후(post-par). 가장 정확한 전력 추정은 post-par이며, 이는 배치된 인스턴스 및 배선 길이의 세부 사항을 포함합니다.
Post-par 전력 추정은 정적 평균 신호 토글 비율(일명 "정적 전력 추정") 또는 시뮬레이션에서 추출한 신호 토글 데이터를 기반으로 할 수 있습니다(일명 "동적 전력 추정").

.. Warning:: Post-par 전력 추정을 실행하려면 ``example-tools.yml`` 파일에 전력 추정 도구(예: Cadence Voltus)가 정의되어 있는지 확인하십시오. 전력 추정 도구(예: Cadence Voltus) 버전이 물리적 설계 도구(예: Cadence Innovus) 버전과 일치하는지 확인하지 않으면 데이터베이스 불일치 오류가 발생할 수 있습니다.

시뮬레이션에서 추출한 전력 추정은 평가 중인 블록(DUT)을 위한 전용 테스트 하니스가 필요한 경우가 많습니다. Hammer 흐름은 이러한 구성을 지원하며(자세한 내용은 Hammer 문서에서 확인 가능), Chipyard의 통합 흐름은 Hammer VLSI 흐름과의 통합을 통해 전체 디지털 SoC 시뮬레이션에서 추출된 post-par 전력 추정을 자동으로 지원합니다. 따라서, 전체 디지털 SoC 시뮬레이션에서 추출한 전력 추정은 관련된 ``make`` 명령과 함께 간단한 바이너리 실행 파일을 지정하여 수행할 수 있습니다.

.. code-block:: shell

    make power-par BINARY=/path/to/baremetal/binary/rv64ui-p-addi.riscv CONFIG=<chipyard_config_name> tech_name=tsmintel3 INPUT_CONFS="example-design.yml example-tools.yml example-tech.yml"

시뮬레이션에서 추출한 전력 추정 흐름은 암묵적으로 Hammer의 게이트 수준 시뮬레이션 흐름을 사용합니다(``saif`` 활동 데이터 파일을 생성하기 위해). 이 게이트 수준 시뮬레이션 흐름은 ``make sim-par`` 명령을 사용하여 전력 추정 흐름과 독립적으로 실행될 수도 있습니다.


.. Note:: 게이트 수준 시뮬레이션 흐름(및 시뮬레이션에서 추출한 전력 추정)은 현재 Synopsys VCS 시뮬레이션과만 통합되어 있습니다(Verilator는 게이트 수준 시뮬레이션을 지원하지 않음. Cadence Xcelium에 대한 지원은 진행 중임).


Signoff
^^^^^^^^^

칩 테이프아웃(tapeout) 동안, 생성된 GDSII가 의도한 대로 제조될 수 있는지 확인하기 위해 사인오프 검사를 수행해야 합니다. 이는 디자인 규칙 검사(DRC) 및 레이아웃 대 회로 비교(LVS) 검증을 수행하는 전용 사인오프 도구를 사용하여 수행됩니다.
대부분의 경우, 배치 및 라우팅된 설계는 미세한 설계 규칙과 배치 및 라우팅 도구의 미묘한/침묵적인 실패로 인해 첫 시도에서 DRC 및 LVS를 통과하지 못할 수 있습니다. DRC 및 LVS를 통과하려면 종종 EDA 도구를 특정 패턴으로 "강제"하기 위해 수동으로 배치 제약을 추가해야 합니다.
면적 및 전력 추정을 목표로 설계를 배치 및 라우팅한 경우, DRC 및 LVS는 엄격히 필요하지 않으며 결과는 매우 유사할 가능성이 큽니다. 칩을 테이프아웃하고 제조할 계획이라면, DRC 및 LVS는 필수적이며 여러 번의 수동 배치 제약 수정이 필요할 수 있습니다.
DRC/LVS 위반이 많으면 배치 및 라우팅 절차의 런타임에 큰 영향을 미칠 수 있습니다(도구가 각 위반을 여러 번 수정하려고 시도하기 때문에). DRC/LVS 위반이 많다는 것은 이 특정 공정 기술에 대해 설계가 반드시 현실적이지 않다는 것을 나타낼 수 있으며, 이는 전력/면적에 영향을 미칠 수 있습니다.

사인오프 검사는 전체 칩 테이프아웃에만 필요하므로 현재 Hammer에서 완전히 자동화되지 않았으며, 종종 특정 공정 기술과 관련된 사용자 정의 Makefile의 추가 수동 포함이 필요합니다. 그러나 Hammer 내에서 사인오프를 실행하는 일반적인 단계는 이전 단계와 유사한 Make 명령으로 이루어집니다.

DRC를 실행하려면 관련 ``make`` 명령은 ``make drc`` 입니다. 이전 단계와 마찬가지로 make 명령은 관련된 구성 Make 변수를 동반해야 합니다:

.. code-block:: shell

    make drc CONFIG=GemminiRocketConfig VLSI_TOP=Gemmini tech_name=tsmintel3 INPUT_CONFS="example-design.yml example-tools.yml example-tech.yml"

DRC는 쉽게 감사할 수 있는 보고서를 생성하지 않습니다. 위반된 규칙 이름이 매우 난해할 수 있기 때문입니다. 적절한 도구 내에서 DRC 오류 데이터베이스를 여는 스크립트를 사용하는 것이 더 생산적일 수 있습니다. 이러한 생성된 스크립트는 ``./build/<config-name>/drc-rundir/generated-scripts/view_drc`` 에서 호출할 수 있습니다.

LVS를 실행하려면 관련 ``make`` 명령은 ``make lvs`` 입니다. 이전 단계와 마찬가지로 make 명령은 관련된 구성 Make 변수를 동반해야 합니다:

.. code-block:: shell

    make lvs CONFIG=GemminiRocketConfig VLSI_TOP=Gemmini tech_name=tsmintel3 INPUT_CONFS="example-design.yml example-tools.yml example-tech.yml"

LVS는 쉽게 감사할 수 있는 보고서를 생성하지 않으며, 위반 사항은 텍스트로 보면 종종 난해합니다. 따라서 관련 도구 내에서 LVS 오류 데이터베이스를 열 수 있는 생성된 스크립트를 사용하여 LVS 문제를 시각적으로 확인하는 것이 더 생산적일 수 있습니다. 이러한 생성된 스크립트는 ``./build/<config-name>/lvs-rundir/generated-scripts/view_lvs`` 에서 호출할 수 있습니다.

Customizing Your VLSI Flow in Hammer
----------------------------------------

Advanced Environment Setup
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

공유 LSF 클러스터에 액세스할 수 있고 Hammer가 계산 집약적인 작업을 로그인 머신이 아닌 LSF 클러스터에 제출하도록 하려는 경우, ``env.yml`` 파일에 다음 코드 세그먼트를 추가할 수 있습니다(관련 값으로 bsub 바이너리 경로, 요청된 CPU 수 및 요청된 LSF 큐를 완성):

.. code-block:: shell

    #submit command (use LSF)
    vlsi.submit:
        command: "lsf"
        settings: [{"lsf": {
            "bsub_binary": "</path/to/bsub/binary/bsub>",
            "num_cpus": <N>,
            "queue": "<lsf_queu>",
            "extra_args": ["-R", "span[hosts=1]"]
            }
        }]
        settings_meta: "append"



Composing a Hierarchical Design
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

대형 설계의 경우, 단일 VLSI 흐름을 처리하고 최적화하는 데 EDA 도구가 매우 오랜 시간이 걸릴 수 있으며, 때로는 실현 가능하지 않을 수도 있습니다.
Hammer는 계층적 물리적 설계 흐름을 지원하며, 설계를 여러 지정된 하위 구성 요소로 분해하고 각 하위 구성 요소에서 흐름을 개별적으로 실행합니다. 그런 다음 Hammer는 이러한 블록을 상위 설계로 조립할 수 있습니다. 이 계층적 접근 방식은 동일한 하위 구성 요소가 여러 인스턴스화될 수 있는 대형 설계에서 VLSI 흐름을 가속화합니다(하위 구성 요소는 단순히 레이아웃에서 복제될 수 있음).
계층적 물리적 설계는 여러 가지 방식으로 수행될 수 있지만(상위에서 하위로, 하위에서 상위로, 인접 등), 현재 Hammer는 하위에서 상위로 접근하는 방식을 지원합니다.
하위에서 상위로 접근하는 방식은 계층 구조를 나타내는 트리를 리프에서 시작하여 루트 방향(즉, "최상위")으로 탐색하며, 계층 트리의 각 노드에서 물리적 설계 흐름을 이전에 레이아웃된 자식 노드를 사용하여 실행합니다.
노드가 계층 구조의 루트(또는 "최상위")에 가까워질수록 설계의 더 큰 섹션이 레이아웃됩니다.

Hammer 계층적 흐름은 원하는 계층 트리의 수동으로 지정된 설명에 의존합니다. 계층 트리의 사양은 생성된 Verilog의 인스턴스 이름을 기반으로 정의되므로 불일치한 인스턴스 이름으로 인해 이 사양이 어려울 수 있습니다. 또한, 계층 트리의 사양은 설계에 대한 수동 플로어플랜 사양과 얽혀 있습니다.

예를 들어, 이전에 언급한 ``GemminiRocketConfig`` 구성을 Gemmini 가속기와 최상위 SoC에서 마지막 수준 캐시를 분리하여 계층적으로 지정하려는 경우, ``example-design.yml`` 의 플로어플랜 예제를 :ref:`VLSI/Basic-Flow:Place-and-Route` 섹션에서 다음 사양으로 대체합니다:

.. code-block:: shell

    vlsi.inputs.hiearchical.top_module: "ChipTop"
    vlsi.inputs.hierarchical.mode: manual"
    vlsi.inputs.manual_modules:
      - ChipTop:
        - RocketTile
        - InclusiveCache
      - RocketTile:
        - Gemmini
    vlsi.manual_placement_constraints:
      - ChipTop
        - path: "ChipTop"
          type: toplevel
          x: 0
          y: 0
          width: 500
          height: 500
          margins:
            left: 0
            right: 0
            top: 0
            bottom: 0
      - RocketTile
        - path: "chiptop.system.tile_prci_domain.tile"
          type: hierarchical
          master: ChipTop
          x: 0
          y: 0
          width: 250
          height: 250
          margins:
            left: 0
            right: 0
            top: 0
            bottom: 0
      - Gemmini
        - path: "chiptop.system.tile_prci_domain.tile.gemmini"
          type: hierarchical
          master: RocketTile
          x: 0
          y: 0
          width: 200
          height: 200
          margins:
            left: 0
            right: 0
            top: 0
            bottom: 0
      - InclusiveCache
        - path: "chiptop.system.subsystem_l2_wrapper.l2"
          type: hierarchical
          master: ChipTop
          x: 0
          y: 0
          width: 100
          height: 100
          margins:
            left: 0
            right: 0
            top: 0
            bottom: 0

이 사양에서 ``vlsi.inputs.hierarchical.mode`` 는 계층 트리의 수동 지정(``manual``)을 나타내며, ``vlsi.inputs.hierarchical.top_module`` 은 계층 트리의 루트를 설정하고, ``vlsi.inputs.hierarchical.manual_modules`` 는 계층 모듈 트리를 열거하며, ``vlsi.inputs.hierarchical.manual_placement_constraints`` 는 각 모듈의 플로어플랜을 열거합니다.

Hammer 계층적 흐름 및 계층 구조 및 제약 조건을 지정하는 방법에 대한 자세한 내용은 `Hammer documentation <https://hammer-vlsi.readthedocs.io/en/stable/Hammer-Use/Hierarchical.html>`__ 을 참조하십시오.

.. Note:: ``make buildfile`` 타겟을 실행하기 전에 계층적 계층 구조를 생성해야 합니다. 이는 Hammer가 ``$(OBJ_DIR)/hammer.d`` 에 생성된 Makefile에 계층적 흐름 그래프를 인코딩하기 때문입니다. 물리적 계층 구조를 수정한 경우, 이 Makefile을 삭제하고 다시 생성해야 합니다. 마지막으로, 작업 중인 계층 블록이 ``VLSI_TOP`` 변수를 항상 재정의해야 합니다. 이는 계층적 시뮬레이션 및 전력 흐름에 필요합니다.


Customizing Generated Tcl Scripts
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``example-vlsi`` Python 스크립트는 Hammer의 진입 스크립트로, 훅을 위한 플레이스홀더가 포함되어 있습니다. 훅은 Hammer API를 확장하기 위한 추가적인 Python 및 TCL 코드 조각입니다(``x.append()`` 를 통해). 훅은 ``example-vlsi`` 진입 스크립트 예제에서 보여지는 것처럼 ``make_pre/post/replacement_hook`` 메서드를 사용하여 삽입할 수 있습니다. 이러한 훅이 VLSI 흐름에 주입되는 방법에 대한 자세한 설명은 `Hammer documentation on hooks <https://hammer-vlsi.readthedocs.io/en/latest/Hammer-Use/Hooks.html>`__ 을 참조하십시오.

