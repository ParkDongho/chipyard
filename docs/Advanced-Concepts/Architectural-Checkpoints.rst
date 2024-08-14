.. _checkpointing:

Architectural Checkpoints
=========================

Chipyard는 Spike를 사용하여 아키텍처 체크포인트를 생성하는 기능을 지원합니다.
이 체크포인트는 프로그램 실행의 특정 시점에서 RISC-V SoC의 아키텍처 상태를 스냅샷으로 저장합니다.
체크포인트에는 캐시 가능한 메모리, 코어 아키텍처 레지스터, 코어 CSR의 내용이 포함됩니다.
SoC의 RTL 시뮬레이션은 아키텍처 상태를 복원한 후 체크포인트에서 실행을 재개할 수 있습니다.

.. note::
   현재는 단일 코어 시스템의 체크포인트만 지원됩니다.

Generating Checkpoints
------------------------

``scripts/generate-ckpt.sh`` 스크립트는 Spike를 사용하여 아키텍처 체크포인트를 생성하는 데 필요한 명령을 실행합니다.
``scripts/generate-ckpt.sh -h`` 명령을 통해 체크포인트 생성 옵션을 확인할 수 있습니다.

예: ``hello.riscv`` 바이너리를 1000개 명령어 실행 후 체크포인트를 생성합니다.
이 작업은 ``hello.riscv.*.loadarch`` 라는 디렉터리를 생성해야 합니다.

.. code::

   scripts/generate-ckpt.sh -b tests/hello.riscv -i 1000


Loading Checkpoints in RTL Simulation
--------------------------------------

체크포인트는 ``LOADARCH`` 플래그를 사용하여 RTL 시뮬레이션에서 로드할 수 있습니다.
대상 구성에는 다음과 같은 속성이 필요합니다:

- **반드시** DMI 기반 부팅을 사용해야 합니다 (기본 TSI 기반 부팅과 대조됨).
- **반드시** 빠른 ``LOADMEM`` 을 지원해야 합니다.
- Spike로 체크포인트를 생성할 때 사용된 아키텍처 구성과 일치해야 합니다 (즉, 동일한 ISA, PMP 없음 등).

.. code::

   cd sims/vcs
   make CONFIG=dmiRocketConfig run-binary LOADARCH=../../hello.riscv.*.loadarch

Checkpointing Linux Binaries
----------------------------

체크포인트는 다음과 같은 제한 사항을 가진 Linux 바이너리에서 사용할 수 있습니다:

- 바이너리는 HTIF 콘솔만 사용해야 하며, 비상호작용형이어야 합니다 (즉, stdin 사용 불가).
- 대상 구성은 시리얼 장치 없이 빌드되어야 합니다 (즉, Rocket Chip Blocks UART는 사용할 수 없음).
- 바이너리는 initramfs만 사용해야 합니다 (즉, 블록 장치 사용 불가).
- 대상 구성은 블록 장치 없이 빌드되어야 합니다 (즉, IceBlk 블록 장치를 사용할 수 없음).
- 바이너리 크기는 대상 구성의 메모리 영역 크기보다 작아야 합니다 (예: FireMarshal의 ``rootfs-size`` 가 1GB이고, OpenSBI가 350KB인 경우 최소 1GB + 350KB 이상의 공간이 필요함).

이 의미는 다음과 같은 작업이 필요함을 의미합니다:

- 기본적으로 Spike는 대부분의 Linux 부팅 시 기본 UART 장치를 사용합니다.
  이를 우회하려면 시리얼 장치가 없는 DTS를 생성하고 이를 ``generate-ckpt.sh`` 스크립트에 전달해야 합니다.
  체크포인트를 생성하려는 디자인의 DTS를 Chipyard의 ``sims/<simulator>/generated-src/`` 에서 복사한 후, 스크립트에 전달하기 위해 수정할 수 있습니다 (추가 장치 및 노드 제거 필요).
  체크포인트 생성을 위해 만들어진 구성의 예는 ``dmiCospikeCheckpointingRocketConfig`` 또는 ``dmiCheckpointingSpikeUltraFastConfig`` 이니다.
- 또한, FireMarshal에서 OpenSBI 동안 기본적으로 HTIF만 사용하도록 Linux 구성을 변경하고, Linux가 OpenSBI HTIF 콘솔을 사용하도록 강제해야 합니다.
  이는 ``linux-config`` 에서 ``CONFIG_CMDLINE="console=hvc0 earlycon=sbi"`` 로 변경하고, ``CONFIG_RISCV_SBI_V01=y``, ``CONFIG_HVC_RISCV_SBI=y``, ``CONFIG_SERIAL_EARLYCON_RISCV_SBI=y`` 를 추가하여 수행할 수 있습니다.
  이러한 변경이 포함된 예제 작업은 ``<firemarshal>/example-workloads/br-base-htif-only-serial.yaml`` 에서 찾을 수 있습니다.

