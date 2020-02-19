/*
 * Licensed under the Mailchimp Mobile SDK License Agreement (the "License");
 * you may not use this file except in compliance with the License. Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either or express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mailchimp.sdkdemo

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mailchimp.sdk.api.di.ApiImplementation
import com.mailchimp.sdk.audience.di.AudienceDependencies
import com.mailchimp.sdk.audience.di.AudienceImplementation
import com.mailchimp.sdk.core.MailchimpSdkConfiguration
import com.mailchimp.sdk.main.Mailchimp
import com.mailchimp.sdk.main.di.MailchimpInjector
import com.mailchimp.sdkdemo.mockapi.MockApiImplementation
import com.mailchimp.sdkdemo.mockapi.MockMailchimp
import kotlinx.android.synthetic.main.fragment_setup.*
import java.util.*

class SetupFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cbx_autotag.isChecked = true
        cbx_debug.isChecked = true
        et_sdk_key.text = Editable.Factory().newEditable(BuildConfig.MAILCHIMP_SDK_DEMO_KEY)
        btn_start.setOnClickListener { start() }
        btn_start_mock.setOnClickListener { startMock() }
    }

    private fun start() {
        val sdkKey = et_sdk_key.text.toString().toLowerCase(Locale.getDefault())
        if (sdkKey.isBlank()) {
            Toast.makeText(context!!, getString(R.string.invalid_sdk_key_msg), Toast.LENGTH_SHORT).show()
        } else {
            // Initialize SDK
            // Normally this would be done elsewhere. Typically App Start.
            val configuration =
                MailchimpSdkConfiguration.Builder(context!!, sdkKey)
                    .isDebugModeEnabled(cbx_debug.isChecked)
                    .isAutoTaggingEnabled(cbx_autotag.isChecked)
                    .build()
            Mailchimp.initialize(configuration)

            val configurationInfo = ConfigurationInfo("sdkkey-us1", cbx_debug.isChecked, cbx_autotag.isChecked)
            goToHomeFragment(configurationInfo)
        }
    }

    private fun startMock() {
        val configuration =
            MailchimpSdkConfiguration.Builder(context!!, "sdkkey-us1")
                .isDebugModeEnabled(cbx_debug.isChecked)
                .isAutoTaggingEnabled(cbx_autotag.isChecked)
                .build()
        val apiDependencies = MockApiImplementation()
        val mockInjector = object : MailchimpInjector(configuration) {
            override val audienceDependencies: AudienceDependencies by lazy {
                AudienceImplementation.initialize(
                    coreDependencies,
                    apiDependencies,
                    configuration,
                    override = true
                )
            }
            override val apiDependencies: ApiImplementation = apiDependencies
        }
        val mock = MockMailchimp(mockInjector)
        mock.initializeMock()
        MockMailchimp.setAudienceAsMock(mock)

        val configurationInfo = ConfigurationInfo("sdkkey-us1", cbx_debug.isChecked, cbx_autotag.isChecked)
        goToHomeFragment(configurationInfo)
    }

    private fun goToHomeFragment(configurationInfo: ConfigurationInfo) {
//        val action = SetupFragmentDirections.actionSetupFragmentToHomeFragment(configurationInfo)
//        findNavController().navigate(action)
    }
}