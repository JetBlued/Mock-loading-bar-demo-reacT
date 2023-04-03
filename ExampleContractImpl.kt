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
                prognosis = prognosis,
                timestamp = Timestamp(call.timestamp),
                doctor_key = call.sender
        )
        if (stories.has(patient_key)) {
            stories[patient_key].protocols.add(protocol)
        } else {
            val protocols = ArrayList<Protocol>()
            protocols.add(protocol)
            stories.put(patient_key, History(
                    patient_key=patient_key,
                    common_info = "",
                    protocols = protocols,
                    tests=ArrayList<Test>(),
                    prescriptions = ArrayList<Prescription>()
            ))
        }
    }

    override fun addCommonInfo(common_info: String, patient_key: String) {
        require(doctors.has(call.sender)) {
            "ONLY_DOCTOR_CAN_ADD_INFORMATION"
        }
        if (stories.has(patient_key)) {
            val patient = stories[patient_key]
            stories.put(patient_key, History(
                    patient_key=patient_key,
                    common_info = common_info,
                    protocols = patient.protocols,
                    tests=patient.tests,
                    prescriptions = patient.prescriptions
            ))
        } else {
            stories.put(patient_key, History(
                    patient_key=patient_key,
                    common_info = common_info,
                    protocols = ArrayList<Protocol>(),
                    tests=ArrayList<Test>(),
                    prescriptions = ArrayList<Prescription>()
            ))
        }
    }

    override fun addTest(name: String, indicator_names: String, results: String, comments: String, patient_key: String) {
        require(doctors.has(call.sender)) {
            "ONLY_DOCTOR_CAN_ADD_INFORMATION"
        }
        val indicator_names = indicator_names.split(", ").toTypedArray().toCollection(ArrayList())
        val results = results.split(", ").toTypedArray().toCollection(ArrayList())
        val comments = comments.split(", ").toTypedArray().toCollection(ArrayList())
        require(indicator_names.size == results.size) {
            "INVALID_RESULTS_SIZE"
        }
        require(indicator_names.size == comments.size) {
            "INVALID_RESULTS_SIZE"
        }
        val test = Test(
                name = name,
                indicator_names = indicator_names,
                results = results,
                comments = comments,
                timestamp = Timestamp(call.timestamp)
        )
        if (stories.has(patient_key)) {
            stories[patient_key].tests.add(test)
        } else {
            val tests = ArrayList<Test>()
            tests.add(test)
            stories.put(patient_key, History(
                    patient_key=patient_key,
                    common_info = "",
                    protocols = ArrayList<Protocol>(),
                    tests=tests,
                    prescriptions = ArrayList<Prescription>()
            ))
        }
    }

    override fun addPrescription(name: String, end_timestamp: Long, drug_name: String, patient_key: String) {
        require((doctors.has(call.sender)) and (doctors[call.sender].license==true)) {
            "ONLY_DOCTOR_CAN_ADD_INFORMATION"
        }
        require(!drugs_forbidden.has(drug_name)) {
            "THIS_DRUG_IS_FORBIDDEN_BY_REGULATOR"
        }
        val prescription = Prescription(
                doctor_key = call.sender,
                name = name,
                start_timestamp = Timestamp(call.timestamp),
                end_timestamp = Timestamp(end_timestamp),
                drug_name = drug_name
        )
        if (stories.has(patient_key)) {
            stories[patient_key].prescriptions.add(prescription)
        } else {
            val prescriptions = ArrayList<Prescription>()
            prescriptions.add(prescription)
            stories.put(patient_key, History(
                    patient_key=patient_key,
                    common_info = "",
                    protocols = ArrayList<Protocol>(),
                    tests=ArrayList<Test>(),
                    prescriptions = prescriptions
            ))
        }
    }

    override fun checkPrescription(patient_key: String, drug_name: String) {
        require(stories.has(patient_key)) {
            "NO_PATIENT_HISTORY"
        }
        require(!drugs_forbidden.has(drug_name)) {
            "THIS_DRUG_IF_FORBIDDEN_BY_REGULATOR"
        }
        val prescriptions = stories[patient_key].prescriptions
        var count = 0
        for (prescript in prescriptions) {
            if ((prescript.drug_name == drug_name) and
                    (Timestamp(call.timestamp) > prescript.start_timestamp) and
                    (Timestamp(call.timestamp) <prescript.end_timestamp) and
                    (doctors.has(prescript.doctor_key)) and
                    (doctors[prescript.doctor_key].license != false)) {
                count += 1
            }
        }
        require(count != 0) {
            "PATIENT_HAS_NO_SUITABLE_PRESCRIPTS"
        }
    }

    override fun disableLicense(type: String, value: Boolean, key: String) {
        require(call.caller == "3QVUnXdCq7vMJDjfcuM7G5qCqFKeBQuac8P") {
            "YOU_ARE_NOT_REGULATOR"
        }
        require(type in arrayListOf<String>("drug", "doctor", "pharmacy")) {
            "INVALID_TYPE"
        }
        if (type == "drug") {
            drugs_forbidden.put(key, false)
        }
        if (type == "doctor") {
            require(doctors.has(key)) {
                "DOCTOR_KEY_DOES_NOT_EXIST"
            }
            val doctor = doctors[key]
            doctors.put(key, Doctor(doctor_key = doctor.doctor_key, information = doctor.information, license = false))
        }
        if (type == "pharmacy") {
            require(pharmacies.has(key)) {
                "PHARMACY_KEY_DOES_NOT_EXIST"
            }
            val pharmacy =pharmacies[key]
            pharmacies.put(key, Pharmacy(pharmacy_key = pharmacy.pharmacy_key,drug_list = ArrayList(),star_license = pharmacy.star_license,end_license = Timestamp(call.timestamp)))
        }
    }
}
data class Protocol (
        val doctor_key: String,
        val diagnosis: String,
        val complaints: String,
        val anamnesis: String,
        val treatment: String,
        val recommendations: String,
        val prognosis: String,
        val timestamp: Timestamp
        )

data class Test (
        val name: String,
        val indicator_names: ArrayList<String>,
        val results: ArrayList<String>,
        val comments: ArrayList<String>,
        val timestamp: Timestamp
)

data class Drug (
        val name: String,
        val information: String,
        val license: Boolean
)

data class Prescription (
        val doctor_key: String,
        val name: String,
        val start_timestamp: Timestamp,
        val end_timestamp: Timestamp,
        val drug_name: String
)


data class History (
        val patient_key: String,
        val common_info: String,
        val protocols: ArrayList<Protocol>,
        val tests: ArrayList<Test>,
        val prescriptions: ArrayList<Prescription>
)

data class Doctor(
        val doctor_key: String,
        val information: String,
        val license: Boolean
)

data class Pharmacy (
        val pharmacy_key: String,
        val drug_list: ArrayList<String>,
        val star_license: Timestamp,
        val end_license: Timestamp?=null
        )
