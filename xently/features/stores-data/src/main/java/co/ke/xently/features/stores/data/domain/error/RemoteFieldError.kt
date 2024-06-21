package co.ke.xently.features.stores.data.domain.error

typealias FieldName = String

data class RemoteFieldError(val errors: Map<FieldName, List<LocalFieldError>>) : FieldError