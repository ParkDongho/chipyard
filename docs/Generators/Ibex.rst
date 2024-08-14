Ibex Core
====================================

`Ibex <https://github.com/lowRISC/ibex>`__ 는 SystemVerilog로 작성된 파라미터화 가능한 RV32IMC 임베디드 코어이며, 현재 `lowRISC <https://lowrisc.org>`__ 에서 유지 관리하고 있습니다.
`Ibex core` 는 `Ibex tile` 로 래핑되어 `Rocket Chip SoC generator` 와 함께 사용할 수 있습니다.
이 코어는 커스텀 메모리 인터페이스, 인터럽트 포트, 기타 잡다한 포트를 노출하며, 이는 타일 내에서 TileLink 버스 및 기타 파라미터화 신호에 연결됩니다.

.. Warning:: Ibex mtvec 레지스터는 256바이트로 정렬되어 있습니다. 테스트를 작성하거나 실행할 때 트랩 벡터가 256바이트로 정렬되어 있는지 확인하십시오.

.. Warning:: Ibex 리셋 벡터는 BOOT_ADDR + 0x80에 위치해 있습니다.

코어 자체는 생성기가 아니지만, Ibex 코어가 제공하는 동일한 파라미터화를 노출하여 지원되는 모든 Ibex 구성을 사용할 수 있도록 합니다.

자세한 내용은 `Ibex의 GitHub 리포지토리 <https://github.com/lowRISC/ibex>`__ 를 참조하십시오.

