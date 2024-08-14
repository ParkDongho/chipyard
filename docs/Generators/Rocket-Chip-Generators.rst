Rocket-Chip Generators
======================

Chipyard에는 `SiFive <https://www.sifive.com/>`__ 에서 개발된 여러 오픈 소스 생성기가 포함되어 있으며, 현재는 Chips Alliance의 일부로 공개 유지되고 있습니다.
이들은 현재 ``rocket-chip-blocks`` 및 ``rocket-chip-inclusive-cache`` 라는 두 개의 서브모듈 내에 조직되어 있습니다.

Last-Level Cache Generator
-----------------------------

``rocket-chip-inclusive-cache`` 에는 마지막 레벨 캐시 생성기가 포함되어 있습니다. Chipyard 프레임워크는 이 마지막 레벨 캐시를 L2 캐시로 사용합니다. 이 L2 캐시를 사용하려면 SoC 구성에 ``freechips.rocketchip.subsystem.WithInclusiveCache`` 구성 조각을 추가해야 합니다.
이 L2 캐시를 구성하는 방법에 대한 자세한 내용은 :ref:`memory-hierarchy` 섹션을 참조하십시오.

Peripheral Devices Overview
----------------------------
``rocket-chip-blocks`` 에는 UART, SPI, PWM, JTAG, GPIO 등 여러 주변 장치 생성기가 포함되어 있습니다.

이 주변 장치들은 일반적으로 SoC의 메모리 맵과 최상위 IO에 영향을 미칩니다.
모든 주변 블록은 서로 충돌하지 않는 기본 메모리 주소를 가지고 있지만, SoC에서 여러 개의 중복된 블록을 통합해야 하는 경우 해당 장치에 대해 적절한 메모리 주소를 명시적으로 지정해야 합니다.

또한, 장치가 최상위 IO를 필요로 하는 경우 SoC의 최상위 구성을 변경하기 위한 구성 조각을 정의해야 합니다.
최상위 IO를 추가할 때는 테스트 하니스와의 상호작용 여부도 고려해야 합니다.

이 예제는 GPIO 포트를 포함하는 최상위 모듈을 인스턴스화한 다음, GPIO 포트 입력을 0(``false.B``)으로 타이오프(tie-off)합니다.

마지막으로, 관련 구성 조각을 SoC 구성에 추가하십시오. 예를 들어:

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/PeripheralDeviceConfigs.scala
    :language: scala
    :start-after: DOC include start: GPIORocketConfig
    :end-before: DOC include end: GPIORocketConfig


General Purpose I/Os (GPIO) Device
----------------------------------

GPIO 장치는 ``rocket-chip-blocks`` 에서 제공하는 주변 장치입니다. 각 범용 I/O 포트에는 다섯 개의 32비트 구성 레지스터, 두 개의 핀 입력 및 출력 값을 제어하는 32비트 데이터 레지스터, 그리고 신호 레벨 및 엣지 트리거링을 위한 여덟 개의 32비트 인터럽트 제어/상태 레지스터가 있습니다. 또한, 모든 GPIO는 두 개의 32비트 대체 기능 선택 레지스터를 가질 수 있습니다.

GPIO 주요 기능
~~~~~~~~~~~~~~~~~~~~~~~

* 출력 상태: 푸시-풀 또는 오픈 드레인(선택적 풀업/풀다운 저항 포함)

* 출력값 레지스터(GPIOx_OUTPUT_VAL) 또는 주변 장치(대체 기능 출력)에서 출력 데이터

* 각 I/O에 대해 3비트 드라이브 강도 선택

* 입력 상태: 부유, 풀업 또는 풀다운

* 입력값 레지스터(GPIOx_INPUT_VAL) 또는 주변 장치(대체 기능 입력)로의 입력 데이터

* 대체 기능 선택 레지스터

* 빠른 출력 반전을 위한 비트 인버트 레지스터(GPIOx_OUTPUT_XOR)


SoC에 GPIO 포함
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: scala

    class ExampleChipConfig extends Config(
      // ...

      // ==================================
      //   Set up Memory Devices
      // ==================================
      // ...

      // Peripheral section
      new chipyard.config.WithGPIO(address = 0x10010000, width = 32) ++

      // ...
    )


Universal Asynchronous Receiver/Transmitter (UART) Device
----------------------------------------------------------

UART 장치는 ``rocket-chip-blocks`` 에서 제공하는 주변 장치입니다. UART는 외부 장치와의 풀-듀플렉스 데이터 교환을 유연하게 수행할 수 있는 수단을 제공합니다. 분수식 보드 속도 생성기를 통해 매우 넓은 범위의 보드 속도를 달성할 수 있습니다. UART 주변 장치는 다른 모뎀 제어 신호나 동기식 직렬 데이터 전송을 지원하지 않습니다.

UART 주요 기능
~~~~~~~~~~~~~~~~~~~~~~~

* 풀-듀플렉스 비동기 통신

* 보드 속도 생성 시스템

* 비트당 2/3 다수결 투표를 통한 16배 Rx 오버샘플링

* 프로그래밍 가능한 워터마크 인터럽트를 가진 두 개의 내부 FIFO(송신 및 수신 데이터용)

* 공통 프로그래밍 가능한 송신 및 수신 보드 속도

* 구성 가능한 정지 비트(1 또는 2 정지 비트)

* 송신기 및 수신기에 대한 별도의 활성화 비트

* 인터럽트 소스 및 플래그

* 구성 가능한 하드웨어 플로우 제어 신호


SoC에 UART 포함
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: scala

    class ExampleChipConfig extends Config(
      // ...

      // ==================================
      //   Set up Memory Devices
      // ==================================
      // ...

      // Peripheral section
      new chipyard.config.WithUART(address = 0x10020000, baudrate = 115200) ++

      // ...
    )


Inter-Integrated Circuit (I2C) Interface Device
-------------------------------------------------

I2C 장치는 ``rocket-chip-blocks`` 에서 제공하는 주변 장치입니다. I2C(Inter-Integrated Circuit) 버스 인터페이스는 직렬 I2C 버스와의 통신을 처리합니다. 이 인터페이스는 멀티 마스터 기능을 제공하며, 모든 I2C 버스 전용 시퀀싱, 프로토콜, 중재 및 타이밍을 제어합니다. 표준 모드(Sm), 고속 모드(Fm), 고속 모드 플러스(Fm+)를 지원합니다.

I2C 주요 기능
~~~~~~~~~~~~~~~~~~~~~~~

* I2C 버스 사양 호환성:

  * 슬레이브 및 마스터 모드

  * 멀티 마스터 기능

  * 표준 모드(최대 100 kHz)

  * 고속 모드(최대 400 kHz)

  * 고속 모드 플러스(최대 1 MHz)

  * 7비트 주소 모드


SoC에 I2C 포함
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: scala

    class ExampleChipConfig extends Config(
      // ...

      // ==================================
      //   Set up Memory Devices
      // ==================================
      // ...

      // Peripheral section
      new chipyard.config.WithI2C(address = 0x10040000) ++

      // ...
    )


Serial Peripheral Interface (SPI) Device
-------------------------------------------------

SPI 장치는 ``rocket-chip-blocks`` 에서 제공하는 주변 장치입니다. SPI 인터페이스는 SPI 프로토콜을 사용하여 외부 장치와 통신하는 데 사용할 수 있습니다.

직렬 주변 장치 인터페이스(SPI) 프로토콜은 외부 장치와 반이중, 전이중 및 단순 동기식 직렬 통신을 지원합니다. 이 인터페이스는 마스터로 구성될 수 있으며, 이 경우 외부 슬레이브 장치에 통신 클록(SCLK)을 제공합니다.

SPI 주요 기능
~~~~~~~~~~~~~~~~~~~~~~~

* 마스터 작동

* 전이중 동기 전송

* 4비트에서 16비트까지 데이터 크기 선택

* 마스터 모드에서 최대 fPCLK/2까지 보드 속도 분주기

* 하드웨어 또는 소프트웨어에 의한 NSS 관리

* 프로그래밍 가능한 클록 극성과 위상

* MSB-우선 또는 LSB-우선으로 이동하는 프로그래밍 가능한 데이터 순서

* 인터럽트 기능이 있는 전용 전송 및 수신 플래그

* DMA 기능이 있는 두 개의 32비트 내장 Rx 및 Tx FIFO


SoC에 SPI 포함
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: scala

    class ExampleChipConfig extends Config(
      // ...

      // ==================================
      //   Set up Memory Devices
      // ==================================
      // ...

      // Peripheral section
      new chipyard.config.WithSPI(address = 0x10031000) ++

      // ...
    )

