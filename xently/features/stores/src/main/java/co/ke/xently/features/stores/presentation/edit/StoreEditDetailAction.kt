package co.ke.xently.features.stores.presentation.edit

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.storecategory.data.domain.StoreCategory

internal sealed interface StoreEditDetailAction {
    data object ClickSaveDetails : StoreEditDetailAction
    data object ClickAddCategory : StoreEditDetailAction

    class ChangeCategoryName(val name: String) : StoreEditDetailAction
    class SelectCategory(val category: StoreCategory) : StoreEditDetailAction
    class RemoveCategory(val category: StoreCategory) : StoreEditDetailAction
    class ChangeName(val name: String) : StoreEditDetailAction
    class ChangeEmailAddress(val email: String) : StoreEditDetailAction
    class AddService(val service: String) : StoreEditDetailAction
    class ChangePhoneNumber(val phone: String) : StoreEditDetailAction
    class ChangeOpeningHour(val openingHour: OpeningHour) : StoreEditDetailAction
    class ChangeOpeningHourOpenStatus(val isOpen: Boolean) : StoreEditDetailAction
    class ChangeDescription(val description: String) : StoreEditDetailAction
    class ChangeOpeningHourTime(val time: co.ke.xently.features.openinghours.domain.ChangeOpeningHourTime) :
        StoreEditDetailAction
}