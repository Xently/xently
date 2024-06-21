package co.ke.xently.features.products.data.domain.error

typealias FieldName = String

data class RemoteFieldError(val errors: Map<FieldName, List<LocalFieldError>>) : FieldError