
package com.wavesplatform.we.app.example.contract

import com.wavesplatform.vst.contract.ContractAction
import com.wavesplatform.vst.contract.ContractInit
import com.wavesplatform.vst.contract.InvokeParam

interface ExampleContract {

    @ContractInit
    fun create()

    @ContractAction
    fun invoke()

    @ContractAction
    fun addDrugs(
            @InvokeParam(name="drug_name") drug_name: String,
            @InvokeParam(name="information") information: String
    )

    @ContractAction
    fun addProtocol(
            @InvokeParam(name="diagnosis") diagnosis: String,
            @InvokeParam(name="complaints") complaints: String,
            @InvokeParam(name="anamnesis") anamnesis: String,
            @InvokeParam(name="treatment") treatment: String,
            @InvokeParam(name="recommendations") recommendations: String,
            @InvokeParam(name="prognosis") prognosis: String,
            @InvokeParam(name="patient_key") patient_key: String
    )

    @ContractAction
    fun addCommonInfo(
            @InvokeParam(name="common_info") common_info: String,
            @InvokeParam(name="patient_key") patient_key: String
    )

    @ContractAction
    fun addTest(
            @InvokeParam(name="name") name: String,
            @InvokeParam(name="indicator_names") indicator_names: String,
            @InvokeParam(name="results") results: String,
            @InvokeParam(name="comments") comments: String,
            @InvokeParam(name="patient_key") patient_key: String
    )

    @ContractAction