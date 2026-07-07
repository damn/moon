(ns clojure.create-widget-default
  (:require [clojure.truncate :refer [truncate]]
            [clojure.edn-str :refer [->edn-str]]
            [clojure.ui-label :as label]))

(defn f
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))
