/**
 * Created by marcus on 12/12/16.
 */
import javax.inject.Inject

import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter

class Filters @Inject() (corsFilter: CORSFilter)
  extends DefaultHttpFilters(corsFilter)