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
        create = true
        val doctor = Doctor(
                doctor_key = "3Qbur18V2vQfJEcnkqM3H9ieNozVup4mG2v",
                information = "Петров Петр Петрович",
                license = true
        )
        doctors.put(doctor.doctor_key, doctor)

        val drugList=ArrayList<String>()
        val pharmacy = Pharmacy(
                pharmacy_key = "3QZdm3KxwX4GRdrdGLxeZ4xmEyrv5rEXCqF",
                drug_list = drugList,
                star_license = Timestamp(1522448019000),
                end_license = Timestamp(1932675219000)
        )
        pharmacies.put(pharmacy.pharmacy_key, pharmacy)

        drugs_forbidden.put("Прегабалин", false)
    }

    override fun addDrugs(drug_name: String, information: String) {
        require(pharmacies.has(call.sender)) {
            "ONLY_PHARMACIES_CAN_ADD_DRUGS"
        }
        require(!pharmacies[call.sender].drug_list.contains(drug_name)) {
            "DRUG_HAS_ALREADY_ADDED"
        }
        require(!drugs_forbidden.has(drug_name)) {
            "THIS_DRUG_IF_FORBIDDEN_BY_REGULATOR"
        }
        drugs.put(drug_name, Drug(name = drug_name, information = information, license = true))
        pharmacies[call.sender].drug_list.add(drug_name)
    }
    override fun invoke() {
        invoke = true
    }

    override fun addProtocol(diagnosis: String, complaints: String, anamnesis: String, treatment: String, recommendations: String, prognosis: String, patient_key: String) {
        require(doctors.has(call.sender)) {
            "ONLY_DOCTOR_CAN_ADD_INFORMATION"
        }
        val protocol = Protocol(
                diagnosis=diagnosis,
                complaints = complaints,
                anamnesis = anamnesis,
                treatment = treatment,
                recommendations = recommendations,
      