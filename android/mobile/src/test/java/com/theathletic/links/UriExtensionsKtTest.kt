package com.theathletic.links

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UriExtensionsKtTest {

    @Test
    fun `extracts the query parameters from Uri to params map`() {
        val uri = Uri.parse("http://theathletic.com/?first-param=first&second-param=second")

        val params = uri.paramsMap()

        assertThat(params).containsExactly(
            "first-param", "first",
            "second-param", "second"
        )
    }

    @Test
    fun `extracts the fragment parameters with dash separator from Uri to params map`() {
        val uri = Uri.parse("http://theathletic.com/#fragment-value")

        val params = uri.paramsMap()

        assertThat(params).containsEntry("fragment", "value")
    }

    @Test
    fun `extracts the query and fragment parameters with dash separator from Uri to params map`() {
        // Everything after # is on client side, so path params and query goes before the fragment
        val uri = Uri.parse("http://theathletic.com?query_param=q_value#fragment-f_value")

        val params = uri.paramsMap()

        assertThat(params).containsExactly(
            "query_param", "q_value",
            "fragment", "f_value"
        )
    }

    @Test
    fun `extracts the fragment parameters with equals separator from Uri to params map`() {
        val uri = Uri.parse("http://theathletic.com/#fragment=value")

        val params = uri.paramsMap()

        assertThat(params).containsEntry("fragment", "value")
    }

    @Test
    fun `extracts the query and fragment parameters with equals separator from Uri to params map`() {
        // Everything after # is on client side, so path params and query goes before the fragment
        val uri = Uri.parse("http://theathletic.com?query_param=q_value#fragment=f_value")

        val params = uri.paramsMap()

        assertThat(params).containsExactly(
            "query_param", "q_value",
            "fragment", "f_value"
        )
    }
}