package com.wavesplatform.we.app.example.contract.impl

import com.wavesplatform.vst.contract.data.ContractCall
import com.wavesplatform.vst.contract.mapping.Mapping
import com.wavesplatform.vst.contract.spring.annotation.ContractHandlerBean
import com.wavesplatform.vst.contract.state.ContractState
import com.wavesplatform.vst.contract.state.setValue
import com.wavesplatform.vst.contract.state.getValue
import com.wavesplatform.we.app.example.contract.ExampleContract
import org.springframework.aop.aspectj.AspectJPrecedenceInformation
import java.sql.Time
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


@ContractHandlerBean
class ExampleContractImpl(
    state: ContractState,
    private val call: ContractCall
) : ExampleContract {

    private var create: Boolean? by state
    private var invoke: Boolean? by state

    private val doctors: Mapping<Doctor> by state
    private val pharmacies: Mapping<Pharmacy> by state
    private val stories: Mapping<History> by state
    private val drugs: Mapping<Drug> by state
    private val drugs_forbidden: Mapping<Boolean> by state

    override fun create() {
        cr