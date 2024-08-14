CVA6 Core
====================================

`CVA6 <https://github.com/openhwgroup/cva6>`__ (이전에는 Ariane으로 불림)는 ETH-Zurich의 F. Zaruba와 L. Benini에 의해 처음 개발된 6단계 인오더 스칼라 프로세서 코어입니다.
`CVA6 코어` 는 `CVA6 타일` 로 래핑되어 `Rocket Chip SoC 생성기` 내에서 구성 요소로 사용할 수 있습니다.
코어 자체는 AXI 인터페이스, 인터럽트 포트, 기타 잡다한 포트를 노출하며, 이는 타일 내에서 TileLink 버스 및 기타 파라미터화 신호에 연결됩니다.

.. Warning:: 이 코어는 메모리에 연결하기 위해 AXI 인터페이스를 사용하므로, 단일 코어 설정에서 사용하는 것이 매우 권장됩니다 (AXI는 비일관성 메모리 인터페이스이기 때문입니다).

코어 자체는 생성기가 아니지만, CVA6 코어가 제공하는 동일한 파라미터화를 노출합니다 (예: 브랜치 예측 파라미터 변경).

.. Warning:: 이 타겟은 현재 Verilator 시뮬레이션을 지원하지 않습니다. VCS를 사용하십시오.

자세한 내용은 `GitHub 리포지토리 <https://github.com/openhwgroup/cva6>`__ 를 참조하십시오.

