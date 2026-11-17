# Interactive catalog

Copy `CatalogHandler.kt` into a Spring Boot application with the Telegram starter. Send `/catalog`. Browse three products with the arrow buttons, open a product with «Подробнее», then return with «К каталогу». A separate button opens the documentation. The bot edits the same message and keyboard after each callback and answers the callback query.

This is a self-contained UI example: products are hard-coded, no orders or payments are made, and the current page is encoded in callback data rather than stored in a session. Invalid or out-of-range callback data receives an alert. The example handles callbacks for ordinary chat messages, not inline-mode messages.
