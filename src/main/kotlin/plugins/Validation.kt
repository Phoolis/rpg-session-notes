package fi.paulcarlson.plugins

import fi.paulcarlson.domain.campaign.Campaign
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*


// Does not catch missing non-nullable fields, since those cause errors in Serialization and do not get converted into a class.
// Conversion failures lead to less descriptive error messages: ie. no "name cannot be null" messages.
// TODO: Figure out a way to add validation messages for missing fields.
fun Application.configureValidation() {
    install(RequestValidation) {
        validate<Campaign> { campaign ->
            if (campaign.name.length !in 2..50)
                ValidationResult.Invalid("Campaign name should be between 2 and 50 characters.")
            if (campaign.description.isBlank())
                ValidationResult.Invalid("Campaign description should not be blank.")
            else ValidationResult.Valid
        }
    }
}