.. _cdes:

Context-Dependent-Environments
========================================

이 섹션에서는 Chipyard와 Rocket Chip에서 강력한 조합 가능한 하드웨어 구성 기능을 제공하기 위해 사용하는 "Context-Dependent-Environment" 시스템에 대해 설명합니다. 독자는 파라미터 시스템에서 ``(site, here, up)`` 를 자주 사용하는 것을 볼 수 있는데, 이것은 바로 이 CDE 시스템의 산물입니다.

CDE 파라미터화 시스템은 단일 전역 파라미터화의 여러 "View"를 제공합니다. ``Field`` 내에서 ``View`` 에 접근하는 구문은 ``my_view(MyKey, site_view)`` 이며, 여기서 ``site_view`` 는 "글로벌" View로, ``my_view(MyKey, site_view)`` 의 호출 스택에서 다양한 함수 및 키 조회에 재귀적으로 전달됩니다.

.. note::
   Rocket Chip 기반 설계는 자주 ``val p: Parameters`` 및 ``p(SomeKey)`` 를 사용하여 키의 값을 조회합니다. ``Parameters`` 는 단순히 ``View`` 추상 클래스의 하위 클래스일 뿐이며, ``p(SomeKey)`` 는 실제로 ``p(SomeKey, p)`` 로 확장됩니다. 이는 ``p(SomeKey)`` 호출을 키 쿼리의 "site" 또는 "source"로 간주하기 때문에, ``site`` 인수를 통해 제공된 ``p`` 의 구성을 재귀적으로 후속 호출에 전달해야 하기 때문입니다.

CDE를 사용하는 다음 예제를 고려해 보십시오.

.. code:: scala

    case object SomeKeyX extends Field[Boolean](false) // 기본값은 false입니다.
    case object SomeKeyY extends Field[Boolean](false) // 기본값은 false입니다.
    case object SomeKeyZ extends Field[Boolean](false) // 기본값은 false입니다.

    class WithX(b: Boolean) extends Config((site, here, up) => {
      case SomeKeyX => b
    })

    class WithY(b: Boolean) extends Config((site, here, up) => {
      case SomeKeyY => b
    })


``Parameters`` 객체를 기반으로 쿼리를 생성할 때, 예를 들어 ``p(SomeKeyX)`` 같은 경우, 구성 시스템은 해당 키에 정의된 부분 함수를 찾을 때까지 설정 조각의 "체인"을 탐색한 후 해당 값을 반환합니다.

.. code:: scala

    val params = new Config(new WithX(true) ++ new WithY(true)) // 설정 조각을 "체인"으로 연결합니다.
    params(SomeKeyX) // true로 평가됩니다.
    params(SomeKeyY) // true로 평가됩니다.
    params(SomeKeyZ) // false로 평가됩니다.

이 예에서 ``params(SomeKeyX)`` 의 평가는 ``WithX(true)`` 에 정의된 부분 함수에서 종료되며, ``params(SomeKeyY)`` 의 평가는 ``WithY(true)`` 에 정의된 부분 함수에서 종료됩니다. 어떤 부분 함수도 일치하지 않으면 평가가 해당 파라미터의 기본값을 반환합니다.

설정 조각은 왼쪽에서 오른쪽으로 우선순위를 가지므로, 체인의 시작 부분에 있는 조각이 오른쪽의 조각의 값을 재정의할 수 있습니다. 체인 조각을 오른쪽에서 왼쪽으로 읽으면 도움이 됩니다.

.. code:: scala

    case object SomeKeyX extends Field 

    class WithX(n: Int) extends Config((site, here, up) => {
      case SomeKeyX => n
    })

    val params = new Config(new WithX(10) ++ new WithX(5))
    println(params(SomeKeyX)) // 10으로 평가됩니다.

CDE의 진정한 힘은 ``(site, here, up)`` 파라미터에서 나옵니다. 이 파라미터들은 부분 함수가 접근할 수 있는 전역 파라미터화에 대한 유용한 "View"를 제공합니다.

.. note::
   CDE에 대한 추가 정보는 `Henry Cook's Thesis <https://www2.eecs.berkeley.edu/Pubs/TechRpts/2016/EECS-2016-89.pdf>`_ 의 2장에 있습니다.

Site
~~~~

``site`` 는 원본 파라미터 쿼리의 "source"에 대한 ``View`` 를 제공합니다.

.. code:: scala

    class WithXEqualsYSite extends Config((site, here, up) => {
      case SomeKeyX => site(SomeKeyY) // site(SomeKeyY, site)로 확장됩니다.
    })

    val params_1 = new Config(new WithXEqualsYSite ++ new WithY(true))
    val params_2 = new Config(new WithY(true) ++ new WithXEqualsYSite)
    params_1(SomeKeyX) // true로 평가됩니다.
    params_2(SomeKeyX) // true로 평가됩니다.

이 예에서, ``WithXEqualsYSite`` 에 있는 부분 함수는 원본 ``params_N`` 객체에서 ``SomeKeyY`` 의 값을 조회하며, 이것은 각 호출의 재귀적 탐색에서 ``site`` 가 됩니다.

Here
~~~~

``here`` 는 로컬에 정의된 구성의 ``View`` 를 제공합니다. 일반적으로 일부 부분 함수만 포함합니다.

.. code:: scala

    class WithXEqualsYHere extends Config((site, here, up) => {
      case SomeKeyY => false
      case SomeKeyX => here(SomeKeyY, site)
    })

    val params_1 = new Config(new WithXEqualsYHere ++ new WithY(true))
    val params_2 = new Config(new WithY(true) ++ new WithXEqualsYHere)

    params_1(SomeKeyX) // false로 평가됩니다.
    params_2(SomeKeyX) // false로 평가됩니다.

이 예에서, 최종 파라미터화된 ``params_2`` 에는 ``SomeKeyY`` 가 ``true``로 설정되어 있음에도 불구하고, ``here(SomeKeyY, site)`` 호출은 ``WithXEqualsYHere`` 에 정의된 로컬 부분 함수만 조회합니다. ``site`` 가 재귀적 호출에서 사용될 수 있기 때문에 ``site`` 를 ``here`` 에 전달해야 한다는 점에 주의하십시오.

Up
~~~~

``up`` 은 부분 함수의 "체인"에서 이전에 정의된 부분 함수 세트에 대한 ``View`` 를 제공합니다. 이는 특정 키의 최종 값이 아닌 이전에 설정된 값을 조회하고자 할 때 유용합니다.

.. code:: scala

    class WithXEqualsYUp extends Config((site, here, up) => {
      case SomeKeyX => up(SomeKeyY, site)
    })

    val params_1 = new Config(new WithXEqualsYUp ++ new WithY(true))
    val params_2 = new Config(new WithY(true) ++ new WithXEqualsYUp)

    params_1(SomeKeyX) // true로 평가됩니다.
    params_2(SomeKeyX) // false로 평가됩니다.

이 예에서 ``WithXEqualsYUp`` 의 ``up(SomeKeyY, site)`` 는 *어떤 경우에는* ``WithY(true)`` 에 있는 ``SomeKeyY`` 를 정의한 부분 함수나 원래 ``case object SomeKeyY`` 정의에서 제공된 기본값을 참조합니다. 설정 조각의 순서가 ``View`` 탐색 순서에 영향을 미치기 때문에, ``up`` 은 ``params_1``과 ``params_2`` 에서 파라미터화에 대해 다른 ``View`` 를 제공합니다.

다시 한번 ``site`` 를 ``up`` 호출을 통해 재귀적으로 전달해야 한다는 점에 유의하십시오.

