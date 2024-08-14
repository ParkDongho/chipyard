The RISC-V ISA Simulator (Spike)
=================================

Spike는 RISC-V ISA의 참조 기능을 제공하는 C++ 소프트웨어 시뮬레이터입니다.
이 시뮬레이터는 `HTIF/FESVR <https://github.com/riscv/riscv-isa-sim/tree/master/fesvr>`__ 를 통해 전체 시스템 에뮬레이션 또는 프록시 에뮬레이션을 제공합니다.
Spike는 RISC-V 타겟에서 소프트웨어를 실행하는 출발점 역할을 합니다.
다음은 Spike의 주요 기능 중 일부입니다:

* 다중 ISA 지원: RV32IMAFDQCV 확장
* 다양한 메모리 모델 지원: 약한 메모리 순서(WMO) 및 전체 저장 순서(TSO)
* 특권 명세: 기계 모드, 감독자 모드, 사용자 모드 (v1.11)
* 디버그 명세 지원
* 단일 단계 디버깅과 메모리/레지스터 내용 보기 지원
* 다중 CPU 지원
* JTAG 지원
* 높은 확장성 (새로운 명령어 추가 및 테스트 가능)

대부분의 경우, Chipyard 타겟을 위한 소프트웨어 개발은 Spike를 사용한 기능적 시뮬레이션에서 시작되며
(일반적으로 맞춤형 가속기 기능을 위한 Spike 모델 추가와 함께), 이후에 소프트웨어 RTL 시뮬레이터나 FireSim을 사용하여
완전한 사이클 정확한 시뮬레이션으로 전환됩니다.

Spike는 RISC-V 툴체인에 사전 패키지로 포함되어 있으며 ``spike`` 라는 이름으로 경로에 사용할 수 있습니다.
자세한 내용은 `Spike repository <https://github.com/riscv/riscv-isa-sim>`__ 에서 확인할 수 있습니다.

Spike-as-a-Tile
-----------------

Chipyard는 uncore와 함께 Spike 프로세서 모델을 시뮬레이션할 수 있는 실험적 지원을 제공합니다. 
이 구성에서는 Spike가 캐시 일관성을 유지하며, C++ TileLink 프라이빗 캐시 모델을 통해 uncore와 통신합니다.

.. code-block:: shell

    make CONFIG=SpikeConfig run-binary BINARY=hello.riscv

Spike-as-a-Tile은 SpikeTile에서 시스템 메모리가 Spike 타일 내에서 완전히 모델링되는 Tightly-Coupled-Memory (TCM)를 지원하여 매우 빠른 시뮬레이션 성능을 제공합니다.

.. code-block:: shell

    make CONFIG=SpikeUltraFastConfig run-binary BINARY=hello.riscv

Spike-as-a-Tile은 사용자 정의 IPC, 커밋 로깅 및 기타 동작을 설정할 수 있습니다. Spike-specific 플래그는 ``EXTRA_SIM_FLAGS`` 에 플러스 아규먼트로 추가할 수 있습니다.

..  code-block:: shell

    make CONFIG=SpikeUltraFastConfig run-binary BINARY=hello.riscv EXTRA_SPIKE_FLAGS="+spike-ipc=10000 +spike-fast-clint +spike-debug" LOADMEM=1


* ``+spike-ipc=``: Spike가 uncore 시뮬레이션의 단일 "틱" 또는 사이클에서 완료할 수 있는 최대 명령어 수를 설정합니다.
* ``+spike-fast-clint``: WFI 대기 시간을 가짜 타이머 인터럽트를 생성하여 빠르게 진행하도록 합니다.
* ``+spike-debug``: Spike 디버그 로깅을 활성화합니다.
* ``+spike-verbose``: Spike 커밋 로그 생성을 활성화합니다.

Adding a new spike device model
-------------------------------

Spike에는 UART, CLINT, PLIC와 같은 몇 가지 기능적 장치 모델이 포함되어 있습니다.
그러나 예를 들어 블록 장치와 같은 사용자 정의 장치 모델을 Spike에 추가할 수 있습니다.
예제 장치는 ``toolchains/riscv-tools/riscv-spike-devices`` 디렉토리에 있습니다.
이 장치들은 공유 라이브러리로 컴파일되어 Spike에 동적으로 연결할 수 있습니다.

이 플러그인을 컴파일하려면 ``toolchains/riscv-tools/riscv-spike-devices`` 디렉토리에서 ``make`` 를 실행하십시오. 그러면 ``libspikedevices.so`` 가 생성됩니다.

Spike에 블록 장치를 연결하고 기본 이미지를 제공하여 블록 장치를 초기화하려면 다음 명령을 실행하십시오.

.. code-block:: shell

   spike --extlib=libspikedevices.so --device="iceblk,img=<path to Linux image>" <path to kernel binary>

.

``--device`` 옵션은 장치 이름과 인수로 구성됩니다.
위의 예에서는 ``iceblk`` 가 장치 이름이고 ``img=<path to Linux image>`` 가 플러그인 장치에 전달된 인수입니다.

