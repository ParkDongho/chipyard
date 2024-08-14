.. _fire-marshal:

FireMarshal
=================

FireMarshal은 RISC-V 기반 시스템을 위한 워크로드 생성 도구입니다. 현재 FireMarshal은 FireSim FPGA 가속 시뮬레이션 플랫폼만 지원합니다.

FireMarshal에서 **Workloads** 는 타겟 시스템의 논리적 노드에 할당된 일련의 **Jobs** 로 구성됩니다. 만약 작업이 지정되지 않은 경우, 워크로드는 ``uniform`` 으로 간주되며 시스템의 모든 노드에 대해 단일 이미지가 생성됩니다. 워크로드는 ``json`` 파일과 해당 워크로드 디렉토리로 설명되며, 기존 워크로드로부터 정의를 상속받을 수 있습니다. 일반적으로 워크로드 구성은 ``workloads/`` 에 보관되지만, 원하는 디렉토리를 사용할 수 있습니다. 시작하기 위한 몇 가지 기본 워크로드가 제공되며, 여기에는 buildroot 또는 Fedora 기반의 리눅스 배포판과 베어 메탈이 포함됩니다.

워크로드를 정의한 후, ``marshal`` 명령은 워크로드의 각 작업에 대한 부팅 바이너리와 rootfs를 생성합니다. 이 바이너리와 rootfs는 qemu 또는 spike(기능적 시뮬레이션)에서 실행되거나 실제 RTL에서 실행하기 위해 플랫폼에 설치될 수 있습니다(현재는 FireSim만 자동화 지원).

시작하려면, 전체 `FireMarshal 문서 <https://firemarshal.readthedocs.io/en/latest/index.html>`_ 를 참조하십시오.

