(ns moon.val-max.validate
  (:require [moon.val-max.schema :as schema]
            [malli.validate :refer [validate]]))

(defn f [val-max]
  (validate schema/v val-max))
