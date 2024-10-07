package co.ke.xently.features.stores.presentation.edit

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.libraries.location.tracker.domain.Location
import com.dokar.chiptextfield.Chip
import kotlinx.datetime.DayOfWeek

internal sealed interface StoreEditDetailAction {
    data object ClickSave : StoreEditDetailAction
    data object ClickSaveAndAddAnother : StoreEditDetailAction
    data object ClearFieldsForNewStore : StoreEditDetailAction

    class SelectCategory(val category: StoreCategory) : StoreEditDetailAction
    class RemoveCategory(val category: StoreCategory) : StoreEditDetailAction
    class ChangeName(val name: String) : StoreEditDetailAction
    class ChangeLocationString(val location: String) : StoreEditDetailAction
    class ChangeLocation(val location: Location) : StoreEditDetailAction
    class ChangeEmailAddress(val email: String) : StoreEditDetailAction
    class AddService(val service: String) : StoreEditDetailAction
    class RemoveService(val service: Chip) : StoreEditDetailAction
    class OnServiceQueryChange(val query: String) : StoreEditDetailAction
    class AddPaymentMethod(val paymentMethod: String) : StoreEditDetailAction
    class RemovePaymentMethod(val paymentMethod: Chip) : StoreEditDetailAction
    class OnPaymentMethodQueryChange(val query: String) : StoreEditDetailAction
    class AddAdditionalCategory(val category: String) : StoreEditDetailAction
    class RemoveAdditionalCategory(val category: Chip) : StoreEditDetailAction
    class OnCategoryQueryChange(val query: String) : StoreEditDetailAction
    class ChangePhoneNumber(val phone: String) : StoreEditDetailAction

    class ChangeOpeningHourOpenStatus(val dayOfWeekIsOpen: Pair<DayOfWeek, Boolean>) :
        StoreEditDetailAction

    class ChangeDescription(val description: String) : StoreEditDetailAction

    class ChangeOpeningHourTime(val dayOfWeekChangeOpeningHour: Pair<DayOfWeek, co.ke.xently.features.openinghours.domain.ChangeOpeningHourTime>) :
        StoreEditDetailAction
}