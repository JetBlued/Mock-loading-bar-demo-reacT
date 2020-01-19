
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