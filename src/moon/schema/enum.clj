(ns moon.schema.enum
  (:require [clojure.edn :as edn]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.ui.select-box :as select-box]
            [moon.edn]))

(defn malli-form [[_ & params] _schemas]
  (apply vector :enum params))

(defn create [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/select-box
    :skin skin
    :items (map moon.edn/->str (rest schema))
    :selected (moon.edn/->str v)}))

(defn value [_  widget _schemas]
  (edn/read-string (select-box/selected widget)))
