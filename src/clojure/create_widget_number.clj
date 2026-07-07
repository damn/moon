(ns clojure.create-widget-number
  (:require [clojure.actor :as actor]
            [clojure.edn-str :refer [->edn-str]]
            [clojure.ui-text-field :as text-field]
            [clojure.ui-text-tooltip :as text-tooltip]))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (actor/add-listener! (text-tooltip/create (str schema) skin))))
