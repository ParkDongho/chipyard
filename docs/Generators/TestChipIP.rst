Test Chip IP
============

Chipyard에는 SoC 설계 시 유용할 수 있는 다양한 하드웨어 위젯을 제공하는 Test Chip IP 라이브러리가 포함되어 있습니다. 이 라이브러리에는 :ref:`Generators/TestChipIP:SimTSI`, :ref:`Generators/TestChipIP:Block Device Controller`, :ref:`Generators/TestChipIP:TileLink SERDES`, :ref:`Generators/TestChipIP:TileLink Switcher`, :ref:`Generators/TestChipIP:TileLink Ring Network`, 및 :ref:`Generators/TestChipIP:UART Adapter` 가 포함되어 있습니다.

SimTSI
--------------

SimTSI와 TSIToTileLink는 테더드 테스트 칩이 호스트 프로세서와 통신할 수 있도록 사용됩니다. 호스트 CPU에서 실행되는 RISC-V 프론트엔드 서버 인스턴스는 TSIToTileLink에 명령을 보내 메모리 시스템에서 데이터를 읽고 쓸 수 있습니다. 프론트엔드 서버는 이 기능을 사용하여 테스트 프로그램을 메모리에 로드하고 프로그램 완료 여부를 폴링합니다. 이에 대한 자세한 내용은 :ref:`Customization/Boot-Process:Chipyard Boot Process` 에서 확인할 수 있습니다.

Block Device Controller
-----------------------

블록 장치 컨트롤러는 2차 저장소를 위한 일반적인 인터페이스를 제공합니다. 이 장치는 주로 FireSim에서 블록 장치 소프트웨어 시뮬레이션 모델과 인터페이스하는 데 사용됩니다. 기본 Linux 구성은 `firesim-software <https://github.com/firesim/firesim-software>`_ 에 있습니다.

디자인에 블록 장치를 추가하려면 ``WithBlockDevice`` 구성 조각을 구성에 추가하십시오.

TileLink SERDES
---------------

Test Chip IP 라이브러리의 TileLink SERDES는 TileLink 메모리 요청을 직렬화하여 직렬 링크를 통해 칩 밖으로 전달할 수 있게 합니다. 다섯 개의 TileLink 채널은 두 개의 SERDES 채널(각 방향으로 하나씩)로 다중화됩니다.

라이브러리는 세 가지 다른 변형을 제공합니다. ``TLSerdes`` 는 칩에 관리자 인터페이스를 노출하고, A, C, E 채널을 아웃바운드 링크에 터널링하며, B 및 D 채널을 인바운드 링크에 터널링합니다. ``TLDesser`` 는 칩에 클라이언트 인터페이스를 노출하고, A, C, E 채널을 인바운드 링크에 터널링하며, B 및 D 채널을 아웃바운드 링크에 터널링합니다. 마지막으로, ``TLSerdesser`` 는 칩에 클라이언트와 관리자 인터페이스를 모두 노출하며, 모든 채널을 양방향으로 터널링할 수 있습니다.

SERDES 클래스를 사용하는 예는 `the Test Chip IP unit test suite <https://github.com/ucb-bar/testchipip/blob/master/src/main/scala/Unittests.scala>`_에서 ``SerdesTest`` 단위 테스트를 참조하십시오.

TileLink Switcher
-----------------

칩에 여러 개의 메모리 인터페이스가 있고 부팅 시 메모리 요청을 매핑할 채널을 선택하려는 경우 TileLink 스위처를 사용할 수 있습니다. 이 스위처는 클라이언트 노드, 여러 관리자 노드 및 선택 신호를 노출합니다. 선택 신호 설정에 따라 클라이언트 노드의 요청은 관리자 노드 중 하나로 전달됩니다. 선택 신호는 TileLink 메시지가 전송되기 전에 설정되어야 하며, 운영 중에 안정적으로 유지되어야 합니다. TileLink 메시지가 전송되기 시작한 후에는 선택 신호를 변경하는 것이 안전하지 않습니다.

스위처를 사용하는 예는 `Test Chip IP 단위 테스트 <https://github.com/ucb-bar/testchipip/blob/master/src/main/scala/Unittests.scala>`_ 에서 ``SwitcherTest`` 단위 테스트를 참조하십시오.

TileLink Ring Network
---------------------

TestChipIP는 TLRingNetwork 생성기를 제공하며, 이는 RocketChip에서 제공하는 TLXbar와 유사한 인터페이스를 가지고 있지만 내부적으로는 크로스바 대신 링 네트워크를 사용합니다. 이는 매우 넓은 TileLink 네트워크(많은 코어와 L2 뱅크)를 가진 칩에서 크로스 섹션 대역폭을 희생하여 와이어 라우팅 혼잡을 완화하는 데 유용할 수 있습니다. 링 네트워크를 사용하는 방법에 대한 문서는 :ref:`Customization/Memory-Hierarchy:The System Bus` 에서 확인할 수 있습니다. 구현 자체는 `여기 <https://github.com/ucb-bar/testchipip/blob/master/src/main/scala/Ring.scala>`_ 에서 찾을 수 있으며, 다른 토폴로지로 TileLink 네트워크를 구현하는 예제로 사용할 수 있습니다.

UART Adapter
------------

UART 어댑터는 TestHarness에 있는 장치로, DUT의 UART 포트에 연결하여 UART 통신(예: Linux 부팅 중 UART에 출력)을 시뮬레이션합니다. 호스트의 ``stdin/stdout`` 과 함께 작동할 뿐만 아니라, 시뮬레이션 중에 ``+uartlog=<파일 이름>`` 을 사용하여 특정 파일에 UART 로그를 출력할 수 있습니다.

기본적으로 이 UART 어댑터는 ``WithUART`` 및 ``WithUARTAdapter`` 구성을 추가하여 Chipyard 내의 모든 시스템에 추가됩니다.

SPI Flash Model
---------------

SPI 플래시 모델은 간단한 SPI 플래시 장치를 모델링하는 장치입니다. 현재 단일 읽기, 쿼드 읽기, 단일 쓰기 및 쿼드 쓰기 명령만 지원합니다. 메모리는 ``+spiflash#=<파일 이름>`` 을 사용하여 제공된 파일로 지원되며, 여기서 ``#`` 은 SPI 플래시 ID(보통 ``0``)입니다.

Chip ID Pin
---------------

Chip ID 핀은 추가된 칩의 칩 ID를 설정합니다. 이는 다중 칩 구성에서 가장 유용합니다. 핀 값은 하니스 바인더에서 설정된 칩 ID 값에 의해 구동되며, 기본적으로 ``0x2000`` 의 주소에서 MMIO를 통해 칩 ID 값을 읽을 수 있습니다.

이 핀은 ``testchipip.soc.WithChipIdPin`` 구성을 사용하여 시스템에 추가할 수 있습니다. 핀 너비와 MMIO 주소는 매개변수화할 수 있으며, ``ChipIdPinParams`` 를 구성에 인수로 전달하여 설정할 수 있습니다. 너비는 ``testchipip.soc.WithChipIdPinWidth`` 구성을 사용하여 추가로 설정할 수 있습니다.

