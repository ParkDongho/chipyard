Target Software
==================================

Chipyard에는 타겟 소프트웨어 워크로드 개발을 위한 도구들이 포함되어 있습니다. 주요 도구는 FireMarshal로, 워크로드 설명을 관리하고 타겟 디자인에서 실행할 바이너리와 디스크 이미지를 생성합니다. 워크로드는 베어메탈일 수도 있고, 표준 Linux 배포판을 기반으로 할 수도 있습니다. 사용자는 빌드 과정의 모든 부분을 맞춤화할 수 있으며, 하드웨어가 필요로 하는 경우 사용자 지정 커널을 제공할 수 있습니다.

FireMarshal은 또한 Spike 및 Qemu와 같은 고성능 기능 시뮬레이터에서 워크로드를 실행할 수 있습니다. Spike는 쉽게 커스터마이징이 가능하며, RISC-V ISA의 공식 참조 구현으로 사용됩니다. Qemu는 네이티브 코드에 가까운 속도로 실행할 수 있는 고성능 기능 시뮬레이터이지만, 수정하기가 어려울 수 있습니다.

Coremark, SPEC2017 및 NVDLA를 위한 워크로드와 같은 추가 소프트웨어 저장소를 초기화하려면 다음 스크립트를 실행하십시오. 서브모듈은 ``software`` 디렉토리에 위치해 있습니다.

.. code-block:: shell

    ./scripts/init-software.sh


.. toctree::
   :maxdepth: 2
   :caption: Contents:

   FireMarshal
   Spike
   Baremetal

