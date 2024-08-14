TileLink Edge Object Methods
============================

TileLink 노드와 관련된 엣지 객체는 TileLink 메시지를 구성하고 데이터에서 정보를 검색하는 데 유용한 여러 가지 메서드를 제공합니다.

Get
---

``Get`` 메시지를 인코딩하는 TLBundleA의 생성자입니다. 이 메시지는 메모리에서 데이터를 요청합니다. 이 메시지에 대한 D 채널 응답은 여러 비트(beats)를 가질 수 있는 ``AccessAckData`` 가 됩니다.

**인수:**

 - ``fromSource: UInt`` - 이 트랜잭션의 소스 ID
 - ``toAddress: UInt`` - 읽을 주소
 - ``lgSize: UInt`` - 읽을 바이트 수의 2의 로그 값

**반환값:**

``(Bool, TLBundleA)`` 튜플. 쌍의 첫 번째 항목은 이 엣지에서 작업이 합법적인지 여부를 나타내는 부울 값입니다. 두 번째 항목은 A 채널 번들입니다.

Put
---

``PutFull`` 또는 ``PutPartial`` 메시지를 인코딩하는 TLBundleA의 생성자입니다. 이는 메모리에 데이터를 쓰는 작업을 수행합니다. ``mask`` 가 지정되면 ``PutPartial`` 이 되고, 생략되면 ``PutFull`` 이 됩니다. 이 푸트(Put)는 여러 비트를 필요로 할 수 있습니다. 이 경우 각 비트에서 ``data`` 와 ``mask`` 만 변경되어야 합니다. 주소를 포함한 다른 모든 필드는 트랜잭션의 모든 비트에 대해 동일해야 합니다. 관리자는 이 메시지에 대해 단일 ``AccessAck`` 로 응답합니다.

**인수:**

 - ``fromSource: UInt`` - 이 트랜잭션의 소스 ID.
 - ``toAddress: UInt`` - 쓸 주소.
 - ``lgSize: UInt`` - 쓸 바이트 수의 2의 로그 값.
 - ``data: UInt`` - 이 비트에서 쓸 데이터.
 - ``mask: UInt`` - (선택 사항) 이 비트에서의 쓰기 마스크.

**반환값:**

``(Bool, TLBundleA)`` 튜플. 쌍의 첫 번째 항목은 이 엣지에서 작업이 합법적인지 여부를 나타내는 부울 값입니다. 두 번째 항목은 A 채널 번들입니다.

Arithmetic
----------

``Arithmetic`` 메시지를 인코딩하는 TLBundleA의 생성자입니다. 이는 원자적 연산(atomic operation)입니다. ``atomic`` 필드의 가능한 값은 ``TLAtomics`` 객체에 정의되어 있으며, ``MIN`` , ``MAX`` , ``MINU`` , ``MAXU`` , 또는 ``ADD`` 가 있습니다. 각각 원자적 최소값, 최대값, 부호 없는 최소값, 부호 없는 최대값 또는 덧셈 연산에 해당합니다. 응답으로는 메모리 위치의 이전 값이 반환되며, 이는 ``AccessAckData`` 형식으로 반환됩니다.

**인수:**

 - ``fromSource: UInt`` - 이 트랜잭션의 소스 ID.
 - ``toAddress: UInt`` - 산술 연산을 수행할 주소.
 - ``lgSize: UInt`` - 연산할 바이트 수의 2의 로그 값.
 - ``data: UInt`` - 산술 연산의 오른쪽 피연산자
 - ``atomic: UInt`` - 산술 연산 유형 (``TLAtomics`` 에서 가져옴)

**반환값:**

``(Bool, TLBundleA)`` 튜플. 쌍의 첫 번째 항목은 이 엣지에서 작업이 합법적인지 여부를 나타내는 부울 값입니다. 두 번째 항목은 A 채널 번들입니다.

Logical
-------

``Logical`` 메시지를 인코딩하는 TLBundleA의 생성자입니다. 이는 원자적 연산입니다. ``atomic`` 필드의 가능한 값은 ``XOR``, ``OR``, ``AND``, ``SWAP`` 이 있으며, 각각 원자적 배타적 논리합, 포함적 논리합, 논리곱 및 교환 연산에 해당합니다. 메모리 위치의 이전 값이 ``AccessAckData`` 응답으로 반환됩니다.

**인수:**

 - ``fromSource: UInt`` - 이 트랜잭션의 소스 ID.
 - ``toAddress: UInt`` - 논리 연산을 수행할 주소.
 - ``lgSize: UInt`` - 연산할 바이트 수의 2의 로그 값.
 - ``data: UInt`` - 논리 연산의 오른쪽 피연산자
 - ``atomic: UInt`` - 논리 연산 유형 (``TLAtomics`` 에서 가져옴)

**반환값:**

``(Bool, TLBundleA)`` 튜플. 쌍의 첫 번째 항목은 이 엣지에서 작업이 합법적인지 여부를 나타내는 부울 값입니다. 두 번째 항목은 A 채널 번들입니다.

Hint
----

``Hint`` 메시지를 인코딩하는 TLBundleA의 생성자입니다. 이는 캐시로 프리페치 힌트를 보내는 데 사용됩니다. ``param`` 인수는 힌트의 종류를 결정합니다. 가능한 값은 ``TLHints`` 객체에서 가져온 ``PREFETCH_READ`` 및 ``PREFETCH_WRITE`` 입니다. 첫 번째는 캐시가 데이터를 공유 상태로 획득하도록 지시하고, 두 번째는 캐시가 데이터를 독점 상태로 획득하도록 지시합니다. 이 메시지가 마지막 레벨 캐시에 도달하면 차이가 없을 것입니다. 이 메시지가 캐시가 아닌 관리자에 도달하면 단순히 무시됩니다. 어떤 경우에도 응답으로 ``HintAck`` 메시지가 전송됩니다.

**인수:**

 - ``fromSource: UInt`` - 이 트랜잭션의 소스 ID.
 - ``toAddress: UInt`` - 프리페치할 주소
 - ``lgSize: UInt`` - 프리페치할 바이트 수의 2의 로그 값
 - ``param: UInt`` - 힌트 유형 (TLHints에서 가져옴)

**반환값:**

``(Bool, TLBundleA)`` 튜플. 쌍의 첫 번째 항목은 이 엣지에서 작업이 합법적인지 여부를 나타내는 부울 값입니다. 두 번째 항목은 A 채널 번들입니다.

AccessAck
---------

``AccessAck`` 또는 ``AccessAckData`` 메시지를 인코딩하는 TLBundleD의 생성자입니다. 선택적 ``data`` 필드가 제공되면 ``AccessAckData`` 가 됩니다. 그렇지 않으면 ``AccessAck`` 이 됩니다.

**인수**

 - ``a: TLBundleA`` - 확인할 A 채널 메시지
 - ``data: UInt`` - (선택 사항) 다시 보낼 데이터

**반환값:**

D 채널 메시지에 대한 ``TLBundleD``.

HintAck
-------

``HintAck`` 메시지를 인코딩하는 TLBundleD의 생성자입니다.

**인수**

 - ``a: TLBundleA`` - 확인할 A 채널 메시지

**반환값:**

D 채널 메시지에 대한 ``TLBundleD``.

first
-----

이 메서드는 디커플드 채널(A 채널 또는 D 채널)을 받아 현재 비트가 트랜잭션의 첫 번째 비트인지 여부를 결정합니다.

**인수:**

 - ``x: DecoupledIO[TLChannel]`` - 모니터링할 디커플드 채널.

**반환값:**

현재 비트가 첫 번째 비트이면 true를 반환하는 ``Boolean`` 값입니다. 그렇지 않으면 false입니다.

last
----

이 메서드는 디커플드 채널(A 채널 또는 D 채널)을 받아 현재 비트가 트랜잭션의 마지막 비트인지 여부를 결정합니다.

**인수:**

 - ``x: DecoupledIO[TLChannel]`` - 모니터링할 디커플드 채널.

**반환값:**

현재 비트가 마지막 비트이면 true를 반환하는 ``Boolean`` 값입니다. 그렇지 않으면 false입니다.

done
----

``x.fire() && last(x)`` 와 동등한 연산입니다.

**인수:**

 - ``x: DecoupledIO[TLChannel]`` - 모니터링할 디커플드 채널.

**반환값:**

현재 비트가 마지막 비트이고 이 사이클에 비트가 전송되면 true를 반환하는 ``Boolean`` 값입니다. 그렇지 않으면 false입니다.

count
-----

이 메서드는 디커플드 채널(A 채널 또는 D 채널)을 받아 트랜잭션에서 현재 비트의 수(0부터 시작)를 결정합니다.

**인수:**

 - ``x: DecoupledIO[TLChannel]`` - 모니터링할 디커플드 채널.

**반환값:**

현재 비트의 수를 나타내는 ``UInt``.

numBeats
---------

이 메서드는 TileLink 번들을 받아 트랜잭션에 필요한 비트 수를 제공합니다.

**인수:**

 - ``x: TLChannel`` - 비트 수를 얻기 위한 TileLink 번들

**반환값:**

현재 트랜잭션의 비트 수인 ``UInt``.

numBeats1


---------

``numBeats``와 유사하지만 비트 수에서 1을 뺀 값을 제공합니다. 이것이 필요한 경우 ``numBeats - 1.U`` 대신 이 메서드를 사용하는 것이 더 효율적입니다.

**인수:**

 - ``x: TLChannel`` - 비트 수를 얻기 위한 TileLink 번들

**반환값:**

현재 트랜잭션의 비트 수에서 1을 뺀 값인 ``UInt``.

hasData
--------

TileLink 메시지에 데이터가 포함되어 있는지 여부를 결정합니다. 이 값이 true이면 메시지는 PutFull, PutPartial, Arithmetic, Logical 또는 AccessAckData입니다.

**인수:**

 - ``x: TLChannel`` - 확인할 TileLink 번들

**반환값:**

현재 메시지에 데이터가 있으면 true, 그렇지 않으면 false인 ``Boolean`` 값.

