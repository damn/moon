(ns clojure.create-widget-string
  (:require [clojure.actor :as actor]
            [clojure.ui-text-field :as text-field]
            [clojure.ui-text-tooltip :as text-tooltip]))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (actor/add-listener! (text-tooltip/create (str schema) skin))))
