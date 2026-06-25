(ns editor.widget.enum
  (:require [clojure.edn-str :refer [->edn-str]]
            [clojure.edn :as edn]
            [scene2d.ui.select-box :as select-box]
            [scene2d.ui.select-box.get-selected :as get-selected]))

(defn create [schema v {:keys [ctx/skin]}]
  (select-box/create
   {:skin skin
    :items (map ->edn-str (rest schema))
    :selected (->edn-str v)}))

(defn value [_  widget _schemas]
  (edn/read-string (get-selected/f widget)))
