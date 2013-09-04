jasperserver
============

Jasper reports remote rendering servlet.

## Usage

Rendering is done via the following HTTP call

    GET /jasperserver/renderJasper?db_host=&db_user=&db_password=&source=
    
Where:
  * db_host: database connection host
  * db_user: database connection user
  * db_password: database connection password
  * source: compiled report url

Any additional query string parameters will be forwarded to the report.

The call should return the generated PDF file.

TODO:
  * Error handling
  * Compiled report caching
