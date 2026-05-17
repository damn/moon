(ns moon.schema-widget.number
  (:require [clojure.edn]
            [moon.edn :as edn]
            [moon.schema :as schema]
            [moon.ui.actor :as actor]
            [moon.ui.text-field :as text-field]))

(defmethod schema/create :s/number
  [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/text-field
    :text (edn/->str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod schema/value :s/number
  [_  widget _schemas]
  (clojure.edn/read-string (text-field/text widget)))
