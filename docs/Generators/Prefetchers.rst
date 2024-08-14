Prefetchers
====================================

BAR-fetchers 라이브러리는 Chipyard 및 Rocket-Chip SoC와의 호환성을 위해 설계된 Chisel로 구현된 프리페처 모음입니다.
이 패키지는 일반적인 프리페처 API와 NextLine, Strided, AMPM 프리페처의 예제 구현을 제공합니다.

프리페처는 L1D HellaCache 앞에 인스턴스화되거나, 일부 TileLink 버스 앞의 TileLink 노드로 인스턴스화될 수 있습니다.

프리페처를 사용하는 예제 구성은 ``PrefetchingRocketConfig`` 에서 찾을 수 있습니다.

