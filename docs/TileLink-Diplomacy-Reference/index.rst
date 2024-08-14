.. _tilelink_and_diplomacy:

TileLink and Diplomacy Reference
================================

TileLink은 RocketChip 및 기타 Chipyard 생성기에서 사용되는 캐시 일관성과 메모리 프로토콜입니다.
이는 캐시, 메모리, 주변 장치 및 DMA 장치와 같은 다양한 모듈이 서로 통신하는 방식입니다.

RocketChip의 TileLink 구현은 Diplomacy라는 프레임워크 위에 구축되어 있으며, 이는 Chisel 생성기 간에 구성 정보를 교환하기 위한 두 단계 정교화 방식의 프레임워크입니다. Diplomacy에 대한 자세한 설명은 `Cook, Terpstra, 그리고 Lee의 논문 <https://carrv.github.io/2017/papers/cook-diplomacy-carrv2017.pdf>`_ 을 참조하십시오.

간단한 TileLink 위젯을 연결하는 방법에 대한 간략한 개요는 :ref:`mmio-accelerators` 섹션에서 찾을 수 있습니다. 이 섹션에서는 RocketChip에서 제공하는 TileLink 및 Diplomacy 기능에 대한 자세한 참조를 제공합니다.

TileLink 1.7 프로토콜에 대한 자세한 사양은 `SiFive 웹사이트 <https://sifive.cdn.prismic.io/sifive%2F57f93ecf-2c42-46f7-9818-bcdd7d39400a_tilelink-spec-1.7.1.pdf>`_ 에서 확인할 수 있습니다.


.. toctree::
    :maxdepth: 2
    :caption: Reference

    NodeTypes
    Diplomacy-Connectors
    EdgeFunctions
    Register-Node
    Widgets

