.. _hammer:

Core Hammer
================================

`Hammer <https://github.com/ucb-bar/hammer>`__ 는 물리적 설계 사양을 설계, CAD 도구, 공정 기술의 세 가지 구별된 영역으로 분할하여 재사용성을 장려하는 물리적 설계 흐름입니다. Hammer는 특정 공급업체의 기술과 도구를 감싸 단일 API를 제공하여 ASIC 설계 문제를 해결할 수 있도록 합니다.
Hammer는 ASIC 설계에서 재사용성을 허용하면서도 설계자가 자신의 수정 사항을 반영할 수 있는 유연성을 제공합니다.

자세한 내용은 `Hammer 논문 <https://dl.acm.org/doi/abs/10.1145/3489517.3530672>`__ 을 읽어보시고, `GitHub 리포지토리 <https://github.com/ucb-bar/hammer>`__ 및 관련 문서를 참조하십시오.

Hammer는 다음과 같은 고급 구조를 사용하여 VLSI 흐름을 구현합니다:

Actions
-------

Actions는 Hammer가 실행할 수 있는 최상위 작업들입니다 (예: 합성, 배치 및 라우팅 등).

Steps
-------

Steps는 Hammer 내에서 개별적으로 다룰 수 있는 Actions의 하위 구성 요소들입니다 (예: 배치 및 라우팅 작업 내의 배치 단계).

Hooks
-------

Hooks는 Hammer 설정에서 프로그래밍 방식으로 정의된 Steps 또는 Actions의 수정 사항입니다.

VLSI Flow Control
-----------------
때때로 우리는 Action 레벨보다 더 세밀하게 VLSI 흐름을 제어하고 싶을 때가 있습니다.
Hammer 흐름은 특정 Action 내에서 Steps 전후에 시작/중지를 할 수 있도록 지원합니다.
전체 옵션 목록과 설명은 `Hammer의 흐름 제어에 관한 문서 <https://hammer-vlsi.readthedocs.io/en/latest/Hammer-Use/Flow-Control.html>`__ 를 참조하십시오.
``vlsi`` 디렉토리의 ``Makefile``은 이 추가 정보를 ``HAMMER_EXTRA_ARGS`` 변수를 통해 전달합니다.
이 변수는 초기 빌드에서 변경되었거나 생략된 추가 YAML 설정을 지정하는 데도 사용할 수 있습니다.


Configuration (Hammer IR)
=========================

Hammer 흐름을 구성하려면 도구 및 기술 플러그인과 버전, 설계별 구성 옵션을 선택하는 ``yaml`` 또는 ``json`` 설정 파일 세트를 제공하십시오. 이러한 구성 API를 총칭하여 Hammer IR이라고 하며, 이는 상위 수준의 추상화에서 생성될 수 있습니다.

현재 사용 가능한 모든 Hammer API 세트는 `여기 <https://github.com/ucb-bar/hammer/blob/master/hammer/config/defaults.yml>`__ 에 명시되어 있습니다.

Tool Plugins
============

Hammer는 다양한 CAD 도구 공급업체에 대해 별도로 관리되는 플러그인을 지원합니다. 관련 CAD 도구 공급업체의 허가를 받아 포함된 Mentor 플러그인 하위 모듈에 접근할 수 있을 것입니다.
현재 지원되는 도구 유형(Hammer 명칭)은 다음과 같습니다:

* synthesis (합성)
* par (배치 및 라우팅)
* drc (디자인 규칙 검사)
* lvs (레이아웃 대 회로 비교)
* sram_generator (SRAM 생성기)
* sim (시뮬레이션)
* power (전력 분석)
* pcb (PCB 설계)

선택한 도구 플러그인을 구성하려면 몇 가지 설정 변수가 필요합니다.

먼저, 각 Action에 사용할 도구를 선택하여 ``vlsi.core.<tool_type>_tool`` 을 도구 패키지 이름으로 설정하십시오. 예: ``vlsi.core.par_tool: "hammer.par.innovus"``.

이 패키지 디렉토리에는 도구 이름을 가진 폴더가 포함되어 있어야 하며, 이 폴더에는 ``__init__.py`` 라는 Python 파일과 ``defaults.yml`` 이라는 YAML 파일이 포함되어 있어야 합니다. 도구 버전을 사용자 정의하려면 ``<tool_type>.<tool_name>.version`` 을 도구별 문자열로 설정하십시오.

``__init__.py`` 파일에는 이 도구를 구현하는 클래스를 가리키는 ``tool`` 변수가 포함되어야 합니다.
이 클래스는 ``Hammer<tool_type>Tool`` 의 하위 클래스여야 하며, ``HammerTool`` 의 하위 클래스가 됩니다. 이 클래스는 도구의 모든 단계를 구현하는 메서드를 포함해야 합니다.

``defaults.yml`` 파일에는 도구별 설정 변수가 포함됩니다. 필요에 따라 기본값을 재정의할 수 있습니다.

Technology Plugins
==================

Hammer는 NDA를 충족시키기 위해 별도로 관리되는 기술 플러그인을 지원합니다. 특정 기술 공급업체의 허가를 받아 사전에 구축된 기술 플러그인에 접근할 수 있을 것입니다. 또는, 자체 기술 플러그인을 구축하려면 최소한 ``<tech_name>.tech.json`` 및 ``defaults.yml`` 이 필요합니다. 기술별 메서드 또는 훅을 실행해야 할 경우 ``__init__.py`` 파일이 선택 사항입니다.

`ASAP7 플러그인 <https://github.com/ucb-bar/hammer/blob/master/hammer/technology/asap7>`__ 은 칩 테이프아웃에 적합하지 않은 오픈 소스 예제로서 기술 플러그인 설정의 출발점으로 좋습니다. Hammer 문서를 참조하여 스키마와 자세한 설정 방법을 확인하십시오.

선택한 기술을 구성하려면 몇 가지 설정 변수가 필요합니다.

먼저 기술 패키지를 선택하십시오. 예: ``vlsi.core.technology: hammer.technology.asap7``, 그런 다음 ``technology.<tech_name>.tarball_dir`` 또는 사전 설치된 디렉토리 ``technology.<tech_name>.install_dir`` 을 사용하여 PDK tarball의 위치를 지정하십시오.

공급 전압, MMMC 코너 등과 같은 기술별 옵션은 각각의 ``vlsi.inputs...`` 설정에서 정의됩니다. 가장 일반적인 사용 사례에 대한 옵션은 기술의 ``defaults.yml`` 에 이미 정의되어 있으며, 사용자가 재정의할 수 있습니다.
