(ns moon.schema.number
  (:require [clojure.scene2d.ui.text-field :as text-field]
            [clojure.edn :as edn]
            [clojure.scene2d.actor :as actor]
            [moon.edn]
            [moon.ui :as ui]))

(defn malli-form [[_ predicate] _schemas]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))

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
