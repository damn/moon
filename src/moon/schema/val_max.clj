(ns moon.schema.val-max
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.ui.text-field :as text-field]
            [clojure.edn :as edn]
            [moon.edn]
            [moon.val-max :as val-max]))

(defn malli-form [_ _schemas]
  val-max/schema)

(defn create
  [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/text-field
    :text (moon.edn/->str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defn value
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))
