package co.ke.xently.features.reviews.data.domain.error

val x = """{
  "id": 9,
  "starRating": 5,
  "message": "This is an example but optional message.",
  "reviewerName": "Xently Technologies",
  "dateCreated": "2024-05-04T12:54:34.487479Z",
  "dateLastModified": "2024-05-04T12:55:51.864614Z",
  "store": {
    "name": "Mountain View",
    "slug": "mountain-view",
    "distance": null,
    "shop": {
      "name": "Xently",
      "slug": "xently",
      "onlineShopUrl": "https://xently.co.ke"
    }
  },
  "category": {
    "id": 1,
    "name": "Ambience",
    "slug": "ambience"
  },
  "maximumRating": 5,
  "question": null,
  "averageStarRating": 0.0,
  "_links": {
    "self": {
      "href": "http://7ac8-105-163-2-45.ngrok-free.app/api/v1/shops/xently_1/stores/mountain-view_1/review-categories/ambience_1/reviews/9"
    },
    "store-review-categories-with-my-ratings": {
      "href": "http://7ac8-105-163-2-45.ngrok-free.app/api/v1/shops/xently_1/stores/mountain-view_1/reviews/mine"
    }
  }
}
"""

data object ReviewCategoryNotFound : Error
class ReviewCategoryNotFoundException : RuntimeException()