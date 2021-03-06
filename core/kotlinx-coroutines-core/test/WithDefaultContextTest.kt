/*
 * Copyright 2016-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kotlinx.coroutines.experimental

import kotlin.test.*

class WithDefaultContextTest : TestBase() {
    @Test
    fun testNoSuspend() = runTest {
        expect(1)
        val result = withContext(DefaultDispatcher) {
            expect(2)
            "OK"
        }
        assertEquals("OK", result)
        finish(3)
    }

    @Test
    fun testWithSuspend() = runTest {
        expect(1)
        val result = withContext(DefaultDispatcher) {
            expect(2)
            delay(100)
            expect(3)
            "OK"
        }
        assertEquals("OK", result)
        finish(4)
    }
}