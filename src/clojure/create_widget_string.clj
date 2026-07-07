(ns clojure.create-widget-string
  (:require
            [clojure.add-listener]
            [clojure.ui-text-field :as text-field]
            [clojure.ui-text-tooltip :as text-tooltip]))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (clojure.add-listener/f (text-tooltip/create (str schema) skin))))
