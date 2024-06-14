package co.ke.xently.features.stores.presentation.edit

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import kotlinx.datetime.DayOfWeek

internal sealed interface StoreEditDetailAction {
    data object ClickSave : StoreEditDetailAction
    data object ClickSaveAndAddAnother : StoreEditDetailAction
    data object ClickAddCategory : StoreEditDetailAction

    class ChangeCategoryName(val name: String) : StoreEditDetailAction
    class SelectCategory(val category: StoreCategory) : StoreEditDetailAction
    class RemoveCategory(val category: StoreCategory) : StoreEditDetailAction
    class ChangeName(val name: String) : StoreEditDetailAction
    class ChangeLocation(val location: String) : StoreEditDetailAction
    class ChangeEmailAddress(val email: String) : StoreEditDetailAction
    class AddService(val service: String) : StoreEditDetailAction
    class ChangePhoneNumber(val phone: String) : StoreEditDetailAction
    class ChangeOpeningHourOpenStatus(val dayOfWeekIsOpen: Pair<DayOfWeek, Boolean>) :
        StoreEditDetailAction

    class ChangeDescription(val description: String) : StoreEditDetailAction
    class ChangeOpeningHourTime(val dayOfWeekChangeOpeningHour: Pair<DayOfWeek, co.ke.xently.features.openinghours.domain.ChangeOpeningHourTime>) :
        StoreEditDetailAction
}