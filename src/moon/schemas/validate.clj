(ns moon.schemas.validate
  (:require [clojure.malli-form :refer [malli-form]]
            [malli.core :as m]
            [malli.utils.validate-humanize :refer [validate-humanize]]))

(defn validate [schemas k value]
  (-> (get schemas k)
      (malli-form schemas)
      m/schema
      (validate-humanize value)))
