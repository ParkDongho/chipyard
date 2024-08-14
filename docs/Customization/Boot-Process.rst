Chipyard Boot Process
=======================

이 섹션에서는 Chipyard 기반 SoC가 Linux 커널을 부팅하는 과정과 이 과정을 사용자 정의하는 방법에 대해 자세히 설명합니다.

BootROM and RISC-V Frontend Server
----------------------------------

BootROM에는 SoC가 전원이 켜질 때 실행되는 첫 번째 명령어와 시스템 구성 요소를 자세히 설명하는 장치 트리 바이너리(dtb)가 포함되어 있습니다.
BootROM 코드의 어셈블리 코드는 
`generators/testchipip/src/main/resources/testchipip/bootrom/bootrom.S <https://github.com/ucb-bar/testchipip/blob/master/src/main/resources/testchipip/bootrom/bootrom.S>`_에 위치해 있습니다.
BootROM 주소 공간은 구성에서 ``BootROMParams`` 키에 의해 결정되는 ``0x10000`` 에서 시작하며, ``BootROMParams`` 의 링크 스크립트와 리셋 벡터에 의해 지정된 ``0x10000`` 주소에서 실행이 시작됩니다. 이 위치는 BootROM 어셈블리에서 ``_hang`` 레이블로 표시됩니다.

Chisel 생성기는 어셈블된 명령어를 하드웨어로 변환하여 BootROM에 삽입하므로, BootROM 코드를 변경하려면 bootrom 디렉터리에서 ``make`` 를 실행한 후 Verilog를 다시 생성해야 합니다. 기존 ``bootrom.S`` 를 덮어쓰지 않으려면 구성에서 ``BootROMParams`` 키를 재정의하여 다른 bootrom 이미지를 지정할 수도 있습니다.

.. code-block:: scala

    class WithMyBootROM extends Config((site, here, up) => {
      case BootROMParams =>
        BootROMParams(contentFileName = "/path/to/your/bootrom.img")
    })

기본 부트로더는 RISC-V 프런트엔드 서버(FESVR)가 실제 프로그램을 로드할 때까지 대기 명령어(WFI)에서 단순히 반복됩니다.
FESVR은 호스트 CPU에서 실행되며 Tethered Serial Interface(TSI)를 사용하여 대상 시스템 메모리의 임의 부분을 읽고 쓸 수 있는 프로그램입니다.

FESVR은 TSI를 사용하여 SoC 메모리에 베어메탈 실행 파일 또는 2차 부트로더를 로드합니다. :ref:`Simulation/Software-RTL-Simulation:Software RTL Simulation` 에서는 시뮬레이터에 전달할 바이너리 파일이 됩니다. 프로그램 로드가 완료되면 FESVR은 CPU 0의 소프트웨어 인터럽트 레지스터에 쓰기를 수행하여 CPU 0이 WFI 루프에서 벗어나게 됩니다. 인터럽트를 받으면 CPU 0은 시스템의 다른 CPU에 대한 소프트웨어 인터럽트 레지스터에 쓰기를 수행한 후, 로드된 실행 파일의 첫 번째 명령어를 실행하기 위해 DRAM의 시작 위치로 점프합니다. 다른 CPU는 첫 번째 CPU에 의해 깨어나며 또한 DRAM의 시작 위치로 점프하게 됩니다.

FESVR이 로드하는 실행 파일은 *tohost*와 *fromhost*로 지정된 메모리 위치를 가져야 합니다. FESVR은 이러한 메모리 위치를 사용하여 실행 파일과 통신합니다. 실행 파일은 *tohost*를 사용하여 FESVR에 명령을 보내 콘솔에 출력하거나, 시스템 호출을 프록시하거나, SoC를 종료할 수 있습니다. *fromhost* 레지스터는 *tohost* 명령에 대한 응답을 반환하고 콘솔 입력을 전송하는 데 사용됩니다.

The Berkeley Boot Loader and RISC-V Linux
-----------------------------------------

베어메탈 프로그램의 경우 이야기는 여기서 끝납니다. 로드된 실행 파일은 FESVR에 SoC의 전원을 끄라고 지시하는 명령을 *tohost* 레지스터를 통해 보낼 때까지 머신 모드에서 실행됩니다.

그러나 Linux 커널을 부팅하려면 Berkeley Boot Loader(BBL)라는 2차 부트로더를 사용해야 합니다. 이 프로그램은 부트 ROM에 인코딩된 장치 트리를 읽어 Linux 커널과 호환되는 형식으로 변환합니다. 그런 다음 가상 메모리와 인터럽트 컨트롤러를 설정하고, 부트로더 바이너리에 페이로드로 포함된 커널을 로드한 후, 커널을 슈퍼바이저 모드에서 실행하기 시작합니다. 부트로더는 또한 커널에서 발생한 머신 모드 트랩을 처리하고 이를 FESVR을 통해 프록시하는 역할을 합니다.

BBL이 슈퍼바이저 모드로 점프하면 Linux 커널이 제어를 인계받고 자신의 프로세스를 시작합니다. 결국 커널은 ``init`` 프로그램을 로드하고 이를 사용자 모드에서 실행하여 사용자 공간 실행을 시작합니다.

Linux를 부팅하는 BBL 이미지를 만드는 가장 쉬운 방법은 `firesim-software <https://github.com/firesim/firesim-software>`_ 리포지토리에 있는 FireMarshal 도구를 사용하는 것입니다. FireMarshal 사용 방법은 :fsim_doc:`FireSim documentation <Advanced-Usage/Workloads/FireMarshal.html>` 에 설명되어 있습니다. FireMarshal을 사용하여 커스텀 커널 구성 및 사용자 공간 소프트웨어를 워크로드에 추가할 수 있습니다.

