package org.test.kotlin_base.common.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import reactor.util.function.Tuples

suspend fun <T1, T2> asyncAndAwait(
    block1: suspend CoroutineScope.() -> T1  & Any,
    block2: suspend CoroutineScope.() -> T2  & Any,
) = coroutineScope {
    Tuples.of(
        async { block1() },
        async { block2() },
    ).run {
        Tuples.of(t1.await(), t2.await())
    }
}

suspend fun <T1 , T2 , T3 > asyncAndAwait(
    block1: suspend CoroutineScope.() -> T1 & Any,
    block2: suspend CoroutineScope.() -> T2 & Any,
    block3: suspend CoroutineScope.() -> T3 & Any,
) = coroutineScope {
    Tuples.of(
        async { block1() },
        async { block2() },
        async { block3() },
    ).run {
        Tuples.of(t1.await(), t2.await(), t3.await())
    }
}

suspend fun <T1 , T2 , T3 , T4 > asyncAndAwait(
    block1: suspend CoroutineScope.() -> T1 & Any,
    block2: suspend CoroutineScope.() -> T2 & Any,
    block3: suspend CoroutineScope.() -> T3 & Any,
    block4: suspend CoroutineScope.() -> T4 & Any,
) = coroutineScope {
    Tuples.of(
        async { block1() },
        async { block2() },
        async { block3() },
        async { block4() },
    ).run {
        Tuples.of(t1.await(), t2.await(), t3.await(), t4.await())
    }
}

suspend fun <T1 , T2 , T3 , T4 , T5 > asyncAndAwait(
    block1: suspend CoroutineScope.() -> T1 & Any,
    block2: suspend CoroutineScope.() -> T2 & Any,
    block3: suspend CoroutineScope.() -> T3 & Any,
    block4: suspend CoroutineScope.() -> T4 & Any,
    block5: suspend CoroutineScope.() -> T5 & Any,
) = coroutineScope {
    Tuples.of(
        async { block1() },
        async { block2() },
        async { block3() },
        async { block4() },
        async { block5() },
    ).run {
        Tuples.of(t1.await(), t2.await(), t3.await(), t4.await(), t5.await())
    }
}

suspend fun <T1 , T2 , T3 , T4 , T5 , T6 > asyncAndAwait(
    block1: suspend CoroutineScope.() -> T1 & Any,
    block2: suspend CoroutineScope.() -> T2 & Any,
    block3: suspend CoroutineScope.() -> T3 & Any,
    block4: suspend CoroutineScope.() -> T4 & Any,
    block5: suspend CoroutineScope.() -> T5 & Any,
    block6: suspend CoroutineScope.() -> T6 & Any,
) = coroutineScope {
    Tuples.of(
        async { block1() },
        async { block2() },
        async { block3() },
        async { block4() },
        async { block5() },
        async { block6() },
    ).run {
        Tuples.of(t1.await(), t2.await(), t3.await(), t4.await(), t5.await(), t6.await())
    }
}

suspend fun <T1 , T2 , T3 , T4 , T5 , T6 , T7 > asyncAndAwait(
    block1: suspend CoroutineScope.() -> T1 & Any,
    block2: suspend CoroutineScope.() -> T2 & Any,
    block3: suspend CoroutineScope.() -> T3 & Any,
    block4: suspend CoroutineScope.() -> T4 & Any,
    block5: suspend CoroutineScope.() -> T5 & Any,
    block6: suspend CoroutineScope.() -> T6 & Any,
    block7: suspend CoroutineScope.() -> T7 & Any,
) = coroutineScope {
    Tuples.of(
        async { block1() },
        async { block2() },
        async { block3() },
        async { block4() },
        async { block5() },
        async { block6() },
        async { block7() },
    ).run {
        Tuples.of(t1.await(), t2.await(), t3.await(), t4.await(), t5.await(), t6.await(), t7.await())
    }
}

suspend fun <T1 , T2 , T3 , T4 , T5 , T6 , T7 , T8 > asyncAndAwait(
    block1: suspend CoroutineScope.() -> T1 & Any,
    block2: suspend CoroutineScope.() -> T2 & Any,
    block3: suspend CoroutineScope.() -> T3 & Any,
    block4: suspend CoroutineScope.() -> T4 & Any,
    block5: suspend CoroutineScope.() -> T5 & Any,
    block6: suspend CoroutineScope.() -> T6 & Any,
    block7: suspend CoroutineScope.() -> T7 & Any,
    block8: suspend CoroutineScope.() -> T8 & Any,
) = coroutineScope {
    Tuples.of(
        async { block1() },
        async { block2() },
        async { block3() },
        async { block4() },
        async { block5() },
        async { block6() },
        async { block7() },
        async { block8() },
    ).run {
        Tuples.of(t1.await(), t2.await(), t3.await(), t4.await(), t5.await(), t6.await(), t7.await(), t8.await())
    }
}
