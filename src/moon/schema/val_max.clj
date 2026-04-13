(ns moon.schema.val-max
  (:require [gdl.scene2d.ui.text-field :as text-field]
            [clojure.edn :as edn]
            [moon.edn]
            [moon.ui :as ui]
            [moon.val-max :as val-max]))

(defn malli-form [_ _schemas]
  val-max/schema)

(defn create
  [schema v {:keys [ctx/skin]}]
  (ui/create
   {:type :ui/text-field
    :text (moon.edn/->str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defn value
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))
