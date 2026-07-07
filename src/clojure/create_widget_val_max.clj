(ns clojure.create-widget-val-max
  (:require
            [clojure.add-listener]
            [clojure.edn-str :refer [->edn-str]]
            [clojure.ui-text-field :as text-field]
            [clojure.ui-text-tooltip :as text-tooltip]))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (clojure.add-listener/f (text-tooltip/create (str schema) skin))))
