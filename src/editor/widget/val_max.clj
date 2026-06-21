(ns editor.widget.val-max
  (:require [clojure.edn-str :refer [->edn-str]]
            [clojure.edn :as edn]
            [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.ui.text-field :as text-field]
            [clojure.ui.text-tooltip :as text-tooltip]))

(defn create
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (add-listener! (text-tooltip/create (str schema) skin))))

(defn value
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))
