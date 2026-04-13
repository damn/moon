(ns moon.schema.enum
  (:require [clojure.edn :as edn]
            [gdl.scene2d.ui.select-box :as select-box]
            [moon.edn]
            [moon.ui :as ui]))

(defn malli-form [[_ & params] _schemas]
  (apply vector :enum params))

(defn create [schema v {:keys [ctx/skin]}]
  (ui/create
   {:type :ui/select-box
    :skin skin
    :items (map moon.edn/->str (rest schema))
    :selected (moon.edn/->str v)}))

(defn value [_  widget _schemas]
  (edn/read-string (select-box/selected widget)))
