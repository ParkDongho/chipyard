Constellation
========================

.. image:: ../_static/images/bigsoc.svg

`Constellation <https://github.com/ucb-bar/constellation>`__ 는 이종 SoC에 통합 및 매우 불규칙한 NoC 아키텍처의 평가를 지원하기 위해 처음부터 설계된 Chisel NoC RTL 생성기 프레임워크입니다.

 - Constellation은 **가상 네트워크 및 크레딧 기반 흐름 제어를 사용하는 패킷 스위칭 웜홀 라우팅 네트워크** 를 생성합니다.
 - Constellation은 **불규칙한** 및 **계층적** 네트워크 토폴로지를 포함하여 **임의의 방향성 그래프 네트워크 토폴로지** 를 지원합니다.
 - Constellation은 임의의 토폴로지에 대해 데드락이 없는 라우팅 테이블을 생성하고 이를 검증할 수 있는 **라우팅 알고리즘 검증기 및 라우팅 테이블 컴파일러** 를 포함합니다.
 - Constellation은 **프로토콜에 독립적인 전송 계층** 이지만 **AXI-4** 및 **TileLink** 와 같은 프로토콜의 데드락 없는 전송을 지원할 수 있습니다.
 - Constellation은 **Chipyard/Rocketchip SoC에 드롭인 방식으로 통합** 을 지원합니다.
 - Constellation은 거의 100가지의 네트워크 구성에 대한 테스트를 포함하여 **엄격하게 테스트** 됩니다.

Constellation은 Chipyard에 완전히 통합되어 있으며, Chipyard/Rocketchip 기반 SoC에서 거의 모든 인터커넥트를 생성하는 데 사용할 수 있습니다.

Constellation에 대한 문서는 `문서 페이지 <http://constellation.readthedocs.io>`__ 에서 확인하십시오.

