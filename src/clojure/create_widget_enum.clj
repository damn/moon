(ns clojure.create-widget-enum
  (:require [clojure.edn-str :refer [->edn-str]]
            [clojure.ui-select-box :as select-box]))

(defn f [schema v {:keys [ctx/skin]}]
  (select-box/create
   {:skin skin
    :items (map ->edn-str (rest schema))
    :selected (->edn-str v)}))
