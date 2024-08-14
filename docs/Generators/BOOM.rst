Berkeley Out-of-Order Machine (BOOM)
==============================================

.. image:: ../_static/images/boom-pipeline-detailed.png

`Berkeley Out-of-Order Machine (BOOM) <https://boom-core.org/>`__ 은 Chisel 하드웨어 설계 언어로 작성된 합성 가능하고 파라미터화 가능한 오픈 소스 RV64GC RISC-V 코어입니다.
BOOM은 Rocket Chip에서 제공하는 Rocket 코어를 대체하여 RocketTile을 BoomTile로 대체할 수 있는 드롭인 교체 역할을 합니다.
BOOM은 MIPS R10k 및 Alpha 21264 아웃오브오더 프로세서에서 많은 영감을 받았습니다.
R10k와 21264처럼 BOOM은 통합 물리적 레지스터 파일 디자인(“명시적 레지스터 리네이밍”이라고도 함)을 사용합니다.
개념적으로, BOOM은 Fetch, Decode, Register Rename, Dispatch, Issue, Register Read, Execute, Memory, Writeback, Commit의 10단계로 나뉩니다.
그러나 현재 구현에서는 이러한 단계 중 많은 부분이 결합되어 Fetch, Decode/Rename, Rename/Dispatch, Issue/RegisterRead, Execute, Memory, Writeback의 7단계로 이루어져 있습니다 (Commit은 비동기적으로 발생하므로 "파이프라인"의 일부로 계산되지 않습니다).

BOOM 마이크로아키텍처에 대한 추가 정보는 `BOOM 문서 페이지 <https://docs.boom-core.org/>`__ 에서 확인할 수 있습니다.

